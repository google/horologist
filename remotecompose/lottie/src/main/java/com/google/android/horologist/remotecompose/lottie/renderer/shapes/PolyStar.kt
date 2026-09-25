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
import androidx.compose.remote.creation.compose.state.atan2
import androidx.compose.remote.creation.compose.state.cos
import androidx.compose.remote.creation.compose.state.rf
import androidx.compose.remote.creation.compose.state.sin
import com.google.android.horologist.remotecompose.lottie.LottieSettings
import com.google.android.horologist.remotecompose.lottie.format.graphicelement.geometry.PolyStar
import com.google.android.horologist.remotecompose.lottie.format.graphicelement.geometry.PolyStarType
import com.google.android.horologist.remotecompose.lottie.format.graphicelement.modifiers.RoundedCorners
import com.google.android.horologist.remotecompose.lottie.format.graphicelement.modifiers.TrimPath
import com.google.android.horologist.remotecompose.lottie.renderer.RemoteLottiePath
import com.google.android.horologist.remotecompose.lottie.renderer.properties.RemoteBezierValue
import com.google.android.horologist.remotecompose.lottie.renderer.properties.animatePosition
import com.google.android.horologist.remotecompose.lottie.renderer.properties.animateScalar
import kotlin.math.PI
import kotlin.math.ceil
import kotlin.math.floor

// Note: We deliberately do not use `androidx.graphics.shapes.RoundedPolygon` here because:
// 1. Lottie defines its own exact Bézier tangent calculation and rounding constants (0.47829 for
//    stars, 0.25 for polygons) matching Bodymovin/After Effects and lottie-android, whereas
//    RoundedPolygon cuts corner arcs with a different circular/smoothed curvature profile.
// 2. Lottie polystars support fractional points (e.g. 5.5 points) smoothly morphing the last
//    vertex, while RoundedPolygon requires an integer vertex count.
// 3. Lottie shapes support explicit path direction (e.g. counter-clockwise d=3) affecting fill
//    winding rules.
// 4. Constructing RemoteBezierValue enables affine transform baking in GeometryTransform.kt and
//    preserves 1:1 visual parity across group transformations.

/** Evaluates a Lottie [PolyStar] parametric shape into a [RemoteLottiePath]. */
@SuppressLint("RestrictedApi")
internal fun evaluatePolyStar(
  star: PolyStar,
  animationSettings: LottieSettings,
  trimPath: TrimPath? = null,
  roundedCorners: RoundedCorners? = null,
): RemoteLottiePath? {
  if (star.hidden?.constantValue == true) return null

  val pos = animatePosition(star.position, animationSettings)
  val posX = pos.x
  val posY = pos.y

  val livePoints = animateScalar(star.points, animationSettings)
  val points = livePoints.constantValueOrNull
  require(points == null || (points.isFinite() && points in 0f..1024f)) {
    "Polystar point count must be finite and at most 1024"
  }
  val rotation = animateScalar(star.rotation, animationSettings)
  val outerRadius = animateScalar(star.outerRadius, animationSettings)
  val outerRoundedness = animateScalar(star.outerRoundness, animationSettings) / 100f

  val subpath =
    if (points == null) createLivePolystar(star, animationSettings, livePoints, posX, posY)
    else
      when (star.starType) {
        PolyStarType.Star -> {
          val innerRadius = star.innerRadius?.let { animateScalar(it, animationSettings) } ?: 0f.rf
          val innerRoundedness =
            (star.innerRoundness?.let { animateScalar(it, animationSettings) } ?: 0f.rf) / 100f
          createStarBezier(
            points = points,
            positionX = posX,
            positionY = posY,
            rotation = rotation,
            innerRadius = innerRadius,
            outerRadius = outerRadius,
            innerRoundedness = innerRoundedness,
            outerRoundedness = outerRoundedness,
            reversed = star.direction == 3,
          )
        }
        PolyStarType.Polygon -> {
          createPolygonBezier(
            points = points,
            positionX = posX,
            positionY = posY,
            rotation = rotation,
            radius = outerRadius,
            roundedness = outerRoundedness,
          )
        }
      }

  val radius =
    roundedCorners
      ?.takeIf { it.hidden?.constantValue != true }
      ?.let { animateScalar(it.radius, animationSettings) }
  val rounded = if (radius == null) subpath else roundRemoteBezier(subpath, radius)
  return trimParametricPath(rounded, trimPath, animationSettings)
}

@SuppressLint("RestrictedApi")
private fun createStarBezier(
  points: Float,
  positionX: RemoteFloat,
  positionY: RemoteFloat,
  rotation: RemoteFloat,
  innerRadius: RemoteFloat,
  outerRadius: RemoteFloat,
  innerRoundedness: RemoteFloat,
  outerRoundedness: RemoteFloat,
  reversed: Boolean,
): RemoteBezierValue {
  if (points <= 0f) {
    return RemoteBezierValue(closed = true, emptyList(), emptyList(), emptyList())
  }

  var currentAngle = (rotation - 90f) * (PI.toFloat() / 180f)
  val anglePerPoint = (2.0 * PI / points).toFloat() * if (reversed) -1f else 1f
  val halfAnglePerPoint = anglePerPoint / 2.0f
  val partialPointAmount = points - points.toInt()
  if (partialPointAmount != 0f) currentAngle += halfAnglePerPoint * (1f - partialPointAmount)

  var x: RemoteFloat
  var y: RemoteFloat
  var previousX: RemoteFloat
  var previousY: RemoteFloat
  var partialPointRadius = 0f.rf

  if (partialPointAmount != 0f) {
    partialPointRadius = innerRadius + (outerRadius - innerRadius) * partialPointAmount
    x = partialPointRadius * cos(currentAngle)
    y = partialPointRadius * sin(currentAngle)
    currentAngle += anglePerPoint * partialPointAmount / 2f
  } else {
    x = outerRadius * cos(currentAngle)
    y = outerRadius * sin(currentAngle)
    currentAngle += halfAnglePerPoint
  }

  val numPoints = ceil(points.toDouble()).toInt() * 2
  val vertices = ArrayList<List<RemoteFloat>>(numPoints)
  val inTangents = ArrayList<List<RemoteFloat>>(numPoints)
  val outTangents = ArrayList<List<RemoteFloat>>(numPoints)

  for (k in 0 until numPoints) {
    inTangents.add(listOf(0f.rf, 0f.rf))
    outTangents.add(listOf(0f.rf, 0f.rf))
  }

  vertices.add(listOf(x + positionX, y + positionY))

  var longSegment = false
  for (i in 0 until numPoints) {
    var radius = if (longSegment) outerRadius else innerRadius
    var dTheta = halfAnglePerPoint
    if (partialPointAmount != 0f && i == numPoints - 2) {
      dTheta = anglePerPoint * partialPointAmount / 2f
    }
    if (partialPointAmount != 0f && i == numPoints - 1) {
      radius = partialPointRadius
    }
    previousX = x
    previousY = y
    x = radius * cos(currentAngle)
    y = radius * sin(currentAngle)

    val targetIndex = (i + 1) % numPoints
    if (i < numPoints - 1) {
      vertices.add(listOf(x + positionX, y + positionY))
    }

    if (innerRoundedness.constantValueOrNull != 0f || outerRoundedness.constantValueOrNull != 0f) {
      val cp1Theta = atan2(previousY, previousX) - (PI.toFloat() / 2f)
      val cp1Dx = cos(cp1Theta)
      val cp1Dy = sin(cp1Theta)

      val cp2Theta = atan2(y, x) - (PI.toFloat() / 2f)
      val cp2Dx = cos(cp2Theta)
      val cp2Dy = sin(cp2Theta)

      val cp1Roundedness = if (longSegment) innerRoundedness else outerRoundedness
      val cp2Roundedness = if (longSegment) outerRoundedness else innerRoundedness
      val cp1Radius = if (longSegment) innerRadius else outerRadius
      val cp2Radius = if (longSegment) outerRadius else innerRadius

      var cp1x = cp1Radius * cp1Roundedness * 0.47829f * cp1Dx
      var cp1y = cp1Radius * cp1Roundedness * 0.47829f * cp1Dy
      var cp2x = cp2Radius * cp2Roundedness * 0.47829f * cp2Dx
      var cp2y = cp2Radius * cp2Roundedness * 0.47829f * cp2Dy
      if (partialPointAmount != 0f) {
        if (i == 0) {
          cp1x *= partialPointAmount
          cp1y *= partialPointAmount
        } else if (i == numPoints - 1) {
          cp2x *= partialPointAmount
          cp2y *= partialPointAmount
        }
      }

      outTangents[i] = listOf(-cp1x, -cp1y)
      inTangents[targetIndex] = listOf(cp2x, cp2y)
    }

    currentAngle += dTheta
    longSegment = !longSegment
  }

  return RemoteBezierValue(
    closed = true,
    inTangents = inTangents,
    outTangents = outTangents,
    vertices = vertices,
  )
}

@SuppressLint("RestrictedApi")
private fun createPolygonBezier(
  points: Float,
  positionX: RemoteFloat,
  positionY: RemoteFloat,
  rotation: RemoteFloat,
  radius: RemoteFloat,
  roundedness: RemoteFloat,
): RemoteBezierValue {
  if (points < 3f) {
    return RemoteBezierValue(closed = true, emptyList(), emptyList(), emptyList())
  }

  val pts = floor(points.toDouble()).toInt()
  var currentAngle = (rotation - 90f) * (PI.toFloat() / 180f)
  val anglePerPoint = (2.0 * PI / pts).toFloat()

  var x = radius * cos(currentAngle)
  var y = radius * sin(currentAngle)
  currentAngle += anglePerPoint

  var previousX: RemoteFloat
  var previousY: RemoteFloat
  val numPoints = pts

  val vertices = ArrayList<List<RemoteFloat>>(numPoints)
  val inTangents = ArrayList<List<RemoteFloat>>(numPoints)
  val outTangents = ArrayList<List<RemoteFloat>>(numPoints)

  for (k in 0 until numPoints) {
    inTangents.add(listOf(0f.rf, 0f.rf))
    outTangents.add(listOf(0f.rf, 0f.rf))
  }

  vertices.add(listOf(x + positionX, y + positionY))

  for (i in 0 until numPoints) {
    previousX = x
    previousY = y
    x = radius * cos(currentAngle)
    y = radius * sin(currentAngle)

    val targetIndex = (i + 1) % numPoints
    if (i < numPoints - 1) {
      vertices.add(listOf(x + positionX, y + positionY))
    }

    if (roundedness.constantValueOrNull != 0f) {
      val cp1Theta = atan2(previousY, previousX) - (PI.toFloat() / 2f)
      val cp1Dx = cos(cp1Theta)
      val cp1Dy = sin(cp1Theta)

      val cp2Theta = atan2(y, x) - (PI.toFloat() / 2f)
      val cp2Dx = cos(cp2Theta)
      val cp2Dy = sin(cp2Theta)

      val cp1x = radius * roundedness * 0.25f * cp1Dx
      val cp1y = radius * roundedness * 0.25f * cp1Dy
      val cp2x = radius * roundedness * 0.25f * cp2Dx
      val cp2y = radius * roundedness * 0.25f * cp2Dy

      outTangents[i] = listOf(-cp1x, -cp1y)
      inTangents[targetIndex] = listOf(cp2x, cp2y)
    }

    currentAngle += anglePerPoint
  }

  return RemoteBezierValue(
    closed = true,
    inTangents = inTangents,
    outTangents = outTangents,
    vertices = vertices,
  )
}
