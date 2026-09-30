/*
 * Copyright 2026 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.android.horologist.remotecompose.lottie.renderer.shapes

import android.annotation.SuppressLint
import androidx.compose.remote.creation.compose.state.RemoteFloat
import androidx.compose.remote.creation.compose.state.max
import androidx.compose.remote.creation.compose.state.rf
import com.google.android.horologist.remotecompose.lottie.LottieSettings
import com.google.android.horologist.remotecompose.lottie.format.graphicelement.modifiers.PuckerBloat
import com.google.android.horologist.remotecompose.lottie.renderer.RemoteBooleanPath
import com.google.android.horologist.remotecompose.lottie.renderer.RemoteGroup
import com.google.android.horologist.remotecompose.lottie.renderer.RemoteLottiePath
import com.google.android.horologist.remotecompose.lottie.renderer.RemoteShape
import com.google.android.horologist.remotecompose.lottie.renderer.properties.RemoteBezierValue
import com.google.android.horologist.remotecompose.lottie.renderer.properties.animateScalar

/**
 * Evaluates a [PuckerBloat] modifier across [shapes], bowing vertices and tangents inward/outward.
 */
@SuppressLint("RestrictedApi")
internal fun evaluatePuckerBloat(
  shapes: List<RemoteShape>,
  puckerBloat: PuckerBloat,
  animationSettings: LottieSettings,
): List<RemoteShape> {
  if (puckerBloat.hidden?.constantValue == true || shapes.isEmpty()) return shapes

  val amount = animateScalar(puckerBloat.amount, animationSettings)
  if (amount.constantValueOrNull == 0f) return shapes

  return shapes.map { shape ->
    when (shape) {
      is RemoteBooleanPath -> error("PuckerBloat after a live boolean merge is not yet supported")
      is RemoteLottiePath -> {
        val source = shape.materializeTrim()
        val newSubpaths = source.path.map { subpath -> applyPuckerBloatToSubpath(subpath, amount) }
        source.withPath(newSubpaths)
      }
      is RemoteGroup -> {
        val newChildShapes =
          shape.childShapes.map { styledShapes ->
            com.google.android.horologist.remotecompose.lottie.renderer.StyledShapes(
              shapes = evaluatePuckerBloat(styledShapes.shapes, puckerBloat, animationSettings),
              style = styledShapes.style,
            )
          }
        RemoteGroup(
          newChildShapes,
          shape.animationSettings,
          shape.transform,
          shape.opacityMultiplier,
        )
      }
      else -> shape
    }
  }
}

@SuppressLint("RestrictedApi")
private fun applyPuckerBloatToSubpath(
  subpath: RemoteBezierValue,
  amount: RemoteFloat,
): RemoteBezierValue {
  val count = subpath.vertices.size
  if (count == 0 || (count == 1 && !subpath.closed)) return subpath

  var sumX = 0f.rf
  var sumY = 0f.rf
  var activeCount = 0f.rf
  for ((index, v) in subpath.vertices.withIndex()) {
    val active = subpath.topology?.vertices?.get(index) ?: 1f.rf
    sumX += v.getOrElse(0) { 0f.rf } * active
    sumY += v.getOrElse(1) { 0f.rf } * active
    activeCount += active
  }
  val cx = sumX / max(activeCount, 1f.rf)
  val cy = sumY / max(activeCount, 1f.rf)

  val f = amount / 100f

  val newVertices = mutableListOf<List<RemoteFloat>>()
  val newInTangents = mutableListOf<List<RemoteFloat>>()
  val newOutTangents = mutableListOf<List<RemoteFloat>>()

  for (i in 0 until count) {
    val vx = subpath.vertices[i].getOrElse(0) { 0f.rf }
    val vy = subpath.vertices[i].getOrElse(1) { 0f.rf }

    val inTan = subpath.inTangents.getOrNull(i)
    val inX = inTan?.getOrElse(0) { 0f.rf } ?: 0f.rf
    val inY = inTan?.getOrElse(1) { 0f.rf } ?: 0f.rf

    val outTan = subpath.outTangents.getOrNull(i)
    val outX = outTan?.getOrElse(0) { 0f.rf } ?: 0f.rf
    val outY = outTan?.getOrElse(1) { 0f.rf } ?: 0f.rf

    val dx = vx - cx
    val dy = vy - cy

    // Vertices move toward the centroid; absolute control points move away from it.
    val newVx = vx - dx * f
    val newVy = vy - dy * f

    // Convert the displaced absolute controls back to offsets from the displaced vertex.
    val newOutX = outX * (1f.rf + f) + dx * f * 2f
    val newOutY = outY * (1f.rf + f) + dy * f * 2f
    val newInX = inX * (1f.rf + f) + dx * f * 2f
    val newInY = inY * (1f.rf + f) + dy * f * 2f

    newVertices.add(listOf(newVx, newVy))
    // A padding slot may own the incoming control of the real closing edge, but its outgoing
    // edge is empty. Gate edges independently so pucker does not inflate collapsed slots.
    val incoming = subpath.topology?.segments?.get((i + count - 1) % count) ?: 1f.rf
    val outgoing = subpath.topology?.segments?.get(i) ?: 1f.rf
    newInTangents.add(listOf(newInX * incoming, newInY * incoming))
    newOutTangents.add(listOf(newOutX * outgoing, newOutY * outgoing))
  }

  return RemoteBezierValue(
    closed = subpath.closed,
    inTangents = newInTangents,
    outTangents = newOutTangents,
    vertices = newVertices,
    topology = subpath.topology,
    visibility = subpath.visibility,
  )
}
