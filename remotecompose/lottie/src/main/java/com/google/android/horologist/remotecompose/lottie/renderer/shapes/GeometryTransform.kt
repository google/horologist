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
import androidx.compose.remote.creation.compose.state.tan
import androidx.compose.remote.creation.compose.state.toRad
import com.google.android.horologist.remotecompose.lottie.LottieSettings
import com.google.android.horologist.remotecompose.lottie.format.graphicelement.grouping.Transform
import com.google.android.horologist.remotecompose.lottie.renderer.RemoteBooleanPath
import com.google.android.horologist.remotecompose.lottie.renderer.RemoteLottiePath
import com.google.android.horologist.remotecompose.lottie.renderer.RemoteShape
import com.google.android.horologist.remotecompose.lottie.renderer.properties.RemoteBezierValue
import com.google.android.horologist.remotecompose.lottie.renderer.properties.animatePosition
import com.google.android.horologist.remotecompose.lottie.renderer.properties.animateScalar
import com.google.android.horologist.remotecompose.lottie.renderer.properties.animateVector

/** A path-space boundary retained until modifiers have finished evaluating its source geometry. */
@SuppressLint("RestrictedApi")
internal data class DeferredPathTransform(
  val transform: Transform,
  val settings: LottieSettings,
  val repeaterStep: RemoteFloat? = null,
) {
  fun apply(path: RemoteBezierValue): RemoteBezierValue =
    if (repeaterStep == null) transformBezierValue(path, transform, settings)
    else transformRepeaterBezierValue(path, transform, repeaterStep, settings)
}

/**
 * Transforms a [RemoteShape] geometry by a Lottie [Transform] definition into the parent coordinate
 * space.
 */
@SuppressLint("RestrictedApi")
internal fun transformRemoteShape(
  shape: RemoteShape,
  transform: Transform,
  animationSettings: LottieSettings,
): RemoteShape {
  return when (shape) {
    is RemoteLottiePath -> transformLottiePath(shape, transform, animationSettings)
    is RemoteBooleanPath ->
      RemoteBooleanPath(
        transformRemoteShape(shape.remainder, transform, animationSettings),
        transformRemoteShape(shape.last, transform, animationSettings),
        shape.operation,
      )
    else -> shape
  }
}

/**
 * Retains an affine path-space boundary. Resolving it after modifiers preserves their local radius,
 * center and amplitude while still letting the final paint combine transformed contours.
 */
@SuppressLint("RestrictedApi")
internal fun transformLottiePath(
  lottiePath: RemoteLottiePath,
  transform: Transform,
  animationSettings: LottieSettings,
): RemoteLottiePath {
  return RemoteLottiePath(
    lottiePath.path,
    lottiePath.fillRule,
    lottiePath.trim,
    lottiePath.geometryTransforms + DeferredPathTransform(transform, animationSettings),
    lottiePath.geometryVisibility,
    lottiePath.identity,
  )
}

/** Transforms a single [RemoteBezierValue] by a Lottie [Transform]. */
@SuppressLint("RestrictedApi")
internal fun transformBezierValue(
  subpath: RemoteBezierValue,
  transform: Transform,
  animationSettings: LottieSettings,
): RemoteBezierValue {
  val anchorPoint = animatePosition(transform.anchorPoint, animationSettings)
  val translation = animatePosition(transform.positionTranslation, animationSettings)
  val scale = animateVector(transform.scale, animationSettings)
  val scaleX = (scale.getOrNull(0) ?: 100f.rf) / 100f
  val scaleY = (scale.getOrNull(1) ?: 100f.rf) / 100f
  val rotation = animateScalar(transform.rotation, animationSettings)
  val skew = transform.skew?.let { animateScalar(it, animationSettings) }
  val skewAxis = transform.skewAxis?.let { animateScalar(it, animationSettings) }

  val newVertices =
    subpath.vertices.map { point ->
      transformPoint(
        x = point.getOrElse(0) { 0f.rf },
        y = point.getOrElse(1) { 0f.rf },
        anchorX = anchorPoint.x,
        anchorY = anchorPoint.y,
        scaleX = scaleX,
        scaleY = scaleY,
        rotation = rotation,
        skew = skew,
        skewAxis = skewAxis,
        transX = translation.x,
        transY = translation.y,
      )
    }

  val newInTangents =
    subpath.inTangents.map { tangent ->
      transformTangent(
        dx = tangent.getOrElse(0) { 0f.rf },
        dy = tangent.getOrElse(1) { 0f.rf },
        scaleX = scaleX,
        scaleY = scaleY,
        rotation = rotation,
        skew = skew,
        skewAxis = skewAxis,
      )
    }

  val newOutTangents =
    subpath.outTangents.map { tangent ->
      transformTangent(
        dx = tangent.getOrElse(0) { 0f.rf },
        dy = tangent.getOrElse(1) { 0f.rf },
        scaleX = scaleX,
        scaleY = scaleY,
        rotation = rotation,
        skew = skew,
        skewAxis = skewAxis,
      )
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
private fun transformPoint(
  x: RemoteFloat,
  y: RemoteFloat,
  anchorX: RemoteFloat,
  anchorY: RemoteFloat,
  scaleX: RemoteFloat,
  scaleY: RemoteFloat,
  rotation: RemoteFloat,
  skew: RemoteFloat?,
  skewAxis: RemoteFloat?,
  transX: RemoteFloat,
  transY: RemoteFloat,
): List<RemoteFloat> {
  var px = x - anchorX
  var py = y - anchorY

  px = px * scaleX
  py = py * scaleY

  if (skew != null) {
    val axis = skewAxis ?: 0f.rf
    val radAxis = toRad(90f.rf - axis)
    val cosA = cos(radAxis)
    val sinA = sin(radAxis)
    val rx = px * cosA + py * sinA
    val ry = -px * sinA + py * cosA
    val skX = rx
    val skY = rx * tan(toRad(skew)) + ry
    px = skX * cosA - skY * sinA
    py = skX * sinA + skY * cosA
  }

  val rad = toRad(rotation)
  val cosR = cos(rad)
  val sinR = sin(rad)
  val rx = px * cosR - py * sinR
  val ry = px * sinR + py * cosR

  val finalX = rx + transX
  val finalY = ry + transY

  return listOf(finalX, finalY)
}

@SuppressLint("RestrictedApi")
private fun transformTangent(
  dx: RemoteFloat,
  dy: RemoteFloat,
  scaleX: RemoteFloat,
  scaleY: RemoteFloat,
  rotation: RemoteFloat,
  skew: RemoteFloat?,
  skewAxis: RemoteFloat?,
): List<RemoteFloat> {
  var px = dx * scaleX
  var py = dy * scaleY

  if (skew != null) {
    val axis = skewAxis ?: 0f.rf
    val radAxis = toRad(90f.rf - axis)
    val cosA = cos(radAxis)
    val sinA = sin(radAxis)
    val rx = px * cosA + py * sinA
    val ry = -px * sinA + py * cosA
    val skX = rx
    val skY = rx * tan(toRad(skew)) + ry
    px = skX * cosA - skY * sinA
    py = skX * sinA + skY * cosA
  }

  val rad = toRad(rotation)
  val cosR = cos(rad)
  val sinR = sin(rad)
  val rx = px * cosR - py * sinR
  val ry = px * sinR + py * cosR

  return listOf(rx, ry)
}
