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
import com.google.android.horologist.remotecompose.lottie.LottieSettings
import com.google.android.horologist.remotecompose.lottie.format.graphicelement.modifiers.RoundedCorners
import com.google.android.horologist.remotecompose.lottie.format.graphicelement.modifiers.TrimPath
import com.google.android.horologist.remotecompose.lottie.format.properties.BaseBezierProperty
import com.google.android.horologist.remotecompose.lottie.format.values.BezierValue
import com.google.android.horologist.remotecompose.lottie.renderer.RemoteBooleanPath
import com.google.android.horologist.remotecompose.lottie.renderer.RemoteGroup
import com.google.android.horologist.remotecompose.lottie.renderer.RemoteLottiePath
import com.google.android.horologist.remotecompose.lottie.renderer.RemoteShape
import com.google.android.horologist.remotecompose.lottie.renderer.properties.animateBezier
import com.google.android.horologist.remotecompose.lottie.renderer.properties.animateScalar
import kotlin.math.hypot

private const val ROUNDED_CORNER_CONTROL_POINT_CONSTANT = 0.5519f

/** Applies rounding at its authored position, including earlier paints and styled groups. */
@SuppressLint("RestrictedApi")
internal fun evaluateRoundedCorners(
  shapes: List<RemoteShape>,
  rounding: RoundedCorners,
  settings: LottieSettings,
): List<RemoteShape> {
  if (rounding.hidden?.constantValue == true || shapes.isEmpty()) return shapes
  val radius = animateScalar(rounding.radius, settings)
  if (radius.constantValueOrNull == 0f) return shapes
  return shapes.map { shape ->
    when (shape) {
      is RemoteBooleanPath ->
        error("RoundedCorners after a live boolean merge is not yet supported")
      is RemoteLottiePath -> {
        val source = shape.materializeTrim()
        source.withPath(source.path.map { roundRemoteBezier(it, radius) })
      }
      is RemoteGroup ->
        RemoteGroup(
          shape.childShapes.map {
            it.copy(shapes = evaluateRoundedCorners(it.shapes, rounding, settings))
          },
          shape.animationSettings,
          shape.transform,
          shape.opacityMultiplier,
        )
      else -> shape
    }
  }
}

/**
 * Rounds sharp corners of a [BezierValue] subpath with the given [radius].
 *
 * For each sharp vertex (where in and out tangents are zero), the corner is replaced by two
 * vertices and cubic Bézier control points approximating a circular arc of radius `r` (clamped to
 * at most half the length of adjacent edges).
 */
internal fun roundBezierValue(subpath: BezierValue, radius: Float): BezierValue {
  if (subpath.vertices.size < 2) {
    return subpath
  }

  val count = subpath.vertices.size
  val newVertices = mutableListOf<List<Float>>()
  val newInTangents = mutableListOf<List<Float>>()
  val newOutTangents = mutableListOf<List<Float>>()

  for (i in 0 until count) {
    val currX = subpath.vertices[i].getOrElse(0) { 0f }
    val currY = subpath.vertices[i].getOrElse(1) { 0f }
    val inTan = subpath.inTangents.getOrNull(i)
    val inX = inTan?.getOrElse(0) { 0f } ?: 0f
    val inY = inTan?.getOrElse(1) { 0f } ?: 0f
    val outTan = subpath.outTangents.getOrNull(i)
    val outX = outTan?.getOrElse(0) { 0f } ?: 0f
    val outY = outTan?.getOrElse(1) { 0f } ?: 0f

    val isSharp = inX == 0f && inY == 0f && outX == 0f && outY == 0f

    val prevPoint: Point? =
      when {
        i > 0 ->
          Point(
            subpath.vertices[i - 1].getOrElse(0) { 0f },
            subpath.vertices[i - 1].getOrElse(1) { 0f },
          )
        subpath.closed.constantValue ->
          Point(
            subpath.vertices[count - 1].getOrElse(0) { 0f },
            subpath.vertices[count - 1].getOrElse(1) { 0f },
          )
        else -> null
      }

    val nextPoint: Point? =
      when {
        i < count - 1 ->
          Point(
            subpath.vertices[i + 1].getOrElse(0) { 0f },
            subpath.vertices[i + 1].getOrElse(1) { 0f },
          )
        subpath.closed.constantValue ->
          Point(subpath.vertices[0].getOrElse(0) { 0f }, subpath.vertices[0].getOrElse(1) { 0f })
        else -> null
      }

    if (prevPoint == null || nextPoint == null || !isSharp) {
      newVertices.add(listOf(currX, currY))
      newInTangents.add(listOf(inX, inY))
      newOutTangents.add(listOf(outX, outY))
    } else {
      val dxPrev = prevPoint.x - currX
      val dyPrev = prevPoint.y - currY
      val lenPrev = hypot(dxPrev, dyPrev)

      val dxNext = nextPoint.x - currX
      val dyNext = nextPoint.y - currY
      val lenNext = hypot(dxNext, dyNext)

      if (lenPrev == 0f && lenNext == 0f) {
        newVertices.add(listOf(currX, currY))
        newInTangents.add(listOf(inX, inY))
        newOutTangents.add(listOf(outX, outY))
      } else {
        val r = maxOf(0f, radius)
        val tPrev = if (lenPrev > 0f) minOf(r / lenPrev, 0.5f) else 0f
        val tNext = if (lenNext > 0f) minOf(r / lenNext, 0.5f) else 0f

        val pStartX = currX + dxPrev * tPrev
        val pStartY = currY + dyPrev * tPrev
        val pEndX = currX + dxNext * tNext
        val pEndY = currY + dyNext * tNext

        val outTanStartX = (currX - pStartX) * ROUNDED_CORNER_CONTROL_POINT_CONSTANT
        val outTanStartY = (currY - pStartY) * ROUNDED_CORNER_CONTROL_POINT_CONSTANT
        val inTanEndX = (currX - pEndX) * ROUNDED_CORNER_CONTROL_POINT_CONSTANT
        val inTanEndY = (currY - pEndY) * ROUNDED_CORNER_CONTROL_POINT_CONSTANT

        // Add start vertex of rounded corner
        newVertices.add(listOf(pStartX, pStartY))
        newInTangents.add(listOf(0f, 0f))
        newOutTangents.add(listOf(outTanStartX, outTanStartY))

        // Add end vertex of rounded corner
        newVertices.add(listOf(pEndX, pEndY))
        newInTangents.add(listOf(inTanEndX, inTanEndY))
        newOutTangents.add(listOf(0f, 0f))
      }
    }
  }

  return BezierValue(
    closed = subpath.closed.constantValue,
    inTangents = newInTangents,
    outTangents = newOutTangents,
    vertices = newVertices,
  )
}

/** Evaluates rounding at playback time instead of baking modifier results into keyframes. */
@SuppressLint("RestrictedApi")
internal fun evaluatePathGeometry(
  bezierProperty: BaseBezierProperty,
  trimPath: TrimPath?,
  roundedCorners: RoundedCorners?,
  animationSettings: LottieSettings,
): RemoteLottiePath {
  val hasRounding = roundedCorners != null && roundedCorners.hidden?.constantValue != true
  if (!hasRounding) {
    return trimEvaluatedPaths(
      animateBezier(bezierProperty, animationSettings),
      trimPath,
      animationSettings,
    )
  }
  val radius = animateScalar(roundedCorners!!.radius, animationSettings)
  val paths = animateBezier(bezierProperty, animationSettings).map { roundRemoteBezier(it, radius) }
  // Rounded contours share the same arc-length domain and clamping policy as other paths.
  // Keep a single contour on native playback trimming; materialize compound cuts together.
  return trimEvaluatedPaths(paths, trimPath, animationSettings, playback = true)
}
