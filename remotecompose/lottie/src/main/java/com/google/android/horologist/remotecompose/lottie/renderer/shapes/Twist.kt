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
import androidx.compose.remote.creation.compose.state.cos
import androidx.compose.remote.creation.compose.state.rf
import androidx.compose.remote.creation.compose.state.sin
import androidx.compose.remote.creation.compose.state.sqrt
import com.google.android.horologist.remotecompose.lottie.LottieSettings
import com.google.android.horologist.remotecompose.lottie.format.graphicelement.modifiers.Twist
import com.google.android.horologist.remotecompose.lottie.format.values.Point
import com.google.android.horologist.remotecompose.lottie.renderer.RemoteBooleanPath
import com.google.android.horologist.remotecompose.lottie.renderer.RemoteGroup
import com.google.android.horologist.remotecompose.lottie.renderer.RemoteLottiePath
import com.google.android.horologist.remotecompose.lottie.renderer.RemoteShape
import com.google.android.horologist.remotecompose.lottie.renderer.properties.RemoteBezierValue
import com.google.android.horologist.remotecompose.lottie.renderer.properties.animatePosition
import com.google.android.horologist.remotecompose.lottie.renderer.properties.animateScalar

/**
 * Evaluates a [Twist] modifier across [shapes], rotating vertices around a center proportional to
 * distance.
 */
@SuppressLint("RestrictedApi")
internal fun evaluateTwist(
  shapes: List<RemoteShape>,
  twist: Twist,
  animationSettings: LottieSettings,
): List<RemoteShape> {
  if (twist.hidden?.constantValue == true || shapes.isEmpty()) return shapes

  val angle = animateScalar(twist.angle, animationSettings)
  val center = animatePosition(twist.center, animationSettings)
  val cx = center.x
  val cy = center.y

  if (angle.constantValueOrNull == 0f) return shapes

  return shapes.map { shape ->
    when (shape) {
      is RemoteBooleanPath -> error("Twist after a live boolean merge is not yet supported")
      is RemoteLottiePath -> {
        val source = shape.materializeTrim()
        val newSubpaths = source.path.map { subpath -> applyTwistToSubpath(subpath, angle, cx, cy) }
        source.withPath(newSubpaths)
      }
      is RemoteGroup -> {
        val newChildShapes =
          shape.childShapes.map { styledShapes ->
            com.google.android.horologist.remotecompose.lottie.renderer.StyledShapes(
              shapes = evaluateTwist(styledShapes.shapes, twist, animationSettings),
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
private fun applyTwistToSubpath(
  subpath: RemoteBezierValue,
  angleDeg: RemoteFloat,
  cx: RemoteFloat,
  cy: RemoteFloat,
): RemoteBezierValue {
  val count = subpath.vertices.size
  if (count == 0) return subpath

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

    val vTwisted = twistPoint(vx, vy, cx, cy, angleDeg)
    val inPointTwisted = twistPoint(vx + inX, vy + inY, cx, cy, angleDeg)
    val outPointTwisted = twistPoint(vx + outX, vy + outY, cx, cy, angleDeg)

    newVertices.add(listOf(vTwisted.x, vTwisted.y))
    newInTangents.add(listOf(inPointTwisted.x - vTwisted.x, inPointTwisted.y - vTwisted.y))
    newOutTangents.add(listOf(outPointTwisted.x - vTwisted.x, outPointTwisted.y - vTwisted.y))
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

@SuppressLint("RestrictedApi")
private fun twistPoint(
  px: RemoteFloat,
  py: RemoteFloat,
  cx: RemoteFloat,
  cy: RemoteFloat,
  angleDeg: RemoteFloat,
): Point {
  val dx = px - cx
  val dy = py - cy
  val dist = sqrt(dx * dx + dy * dy)
  val theta = angleDeg * dist * (Math.PI.toFloat() / 18000f)
  val cosT = cos(theta)
  val sinT = sin(theta)
  val newX = cx + dx * cosT - dy * sinT
  val newY = cy + dx * sinT + dy * cosT
  return Point(newX, newY)
}
