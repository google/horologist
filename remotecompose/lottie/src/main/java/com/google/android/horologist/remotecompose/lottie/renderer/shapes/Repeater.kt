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
import androidx.compose.remote.creation.compose.state.floor
import androidx.compose.remote.creation.compose.state.max
import androidx.compose.remote.creation.compose.state.pow
import androidx.compose.remote.creation.compose.state.rb
import androidx.compose.remote.creation.compose.state.rf
import androidx.compose.remote.creation.compose.state.selectIfLt
import androidx.compose.remote.creation.compose.state.sin
import androidx.compose.remote.creation.compose.state.tan
import androidx.compose.remote.creation.compose.state.toRad
import com.google.android.horologist.remotecompose.lottie.LottieSettings
import com.google.android.horologist.remotecompose.lottie.format.graphicelement.grouping.Transform
import com.google.android.horologist.remotecompose.lottie.format.graphicelement.modifiers.CompositeMode
import com.google.android.horologist.remotecompose.lottie.format.graphicelement.modifiers.Repeater
import com.google.android.horologist.remotecompose.lottie.format.properties.AnimatedScalarProperty
import com.google.android.horologist.remotecompose.lottie.format.properties.StaticPositionProperty
import com.google.android.horologist.remotecompose.lottie.format.properties.StaticScalarProperty
import com.google.android.horologist.remotecompose.lottie.format.properties.StaticVectorProperty
import com.google.android.horologist.remotecompose.lottie.format.values.Point
import com.google.android.horologist.remotecompose.lottie.renderer.GeometryIdentity
import com.google.android.horologist.remotecompose.lottie.renderer.NoopStyle
import com.google.android.horologist.remotecompose.lottie.renderer.RemoteBooleanPath
import com.google.android.horologist.remotecompose.lottie.renderer.RemoteGroup
import com.google.android.horologist.remotecompose.lottie.renderer.RemoteLottiePath
import com.google.android.horologist.remotecompose.lottie.renderer.RemoteShape
import com.google.android.horologist.remotecompose.lottie.renderer.StyledShapes
import com.google.android.horologist.remotecompose.lottie.renderer.properties.RemoteBezierValue
import com.google.android.horologist.remotecompose.lottie.renderer.properties.animatePosition
import com.google.android.horologist.remotecompose.lottie.renderer.properties.animateScalar
import com.google.android.horologist.remotecompose.lottie.renderer.properties.animateVector

/** Represents an evaluated instance of a repeated shape with an associated opacity multiplier. */
@SuppressLint("RestrictedApi")
internal data class RepeatedShapeInstance(
  val shape: RemoteShape,
  val opacityMultiplier: RemoteFloat = 1f.rf,
)

/**
 * Evaluates a [Repeater] modifier on a collection of [RemoteShape] instances, producing duplicate
 * geometry copies with incremental affine transforms and start/end opacity compounding.
 */
@SuppressLint("RestrictedApi")
internal fun evaluateRepeater(
  shapes: List<RemoteShape>,
  repeater: Repeater,
  animationSettings: LottieSettings,
  pathOnly: Boolean = false,
): List<RepeatedShapeInstance> {
  if (repeater.hidden?.constantValue == true || shapes.isEmpty()) {
    return shapes.map { RepeatedShapeInstance(it) }
  }

  val copies = animateScalar(repeater.copies, animationSettings)
  require(copies.constantValueOrNull?.let { it.isFinite() && it <= 1024f } != false) {
    "Repeater copies must be finite and at most 1024"
  }
  val count = copies.constantValueOrNull?.toInt() ?: repeaterCapacity(repeater)
  if (count <= 0) return emptyList()

  val offset = animateScalar(repeater.offset, animationSettings)

  val repeaterTransform = repeater.transform?.toTransform()
  val startOpacity =
    repeater.transform
      ?.let { it.startOpacity ?: it.legacyOpacity }
      ?.let { animateScalar(it, animationSettings) } ?: 100f.rf
  val endOpacity =
    repeater.transform
      ?.let { it.endOpacity ?: it.legacyOpacity }
      ?.let { animateScalar(it, animationSettings) } ?: 100f.rf

  val copyIndices =
    if (repeater.composite == CompositeMode.Below) {
      count - 1 downTo 0
    } else {
      0 until count
    }

  val instances = mutableListOf<RepeatedShapeInstance>()
  for (i in copyIndices) {
    val k = i.toFloat().rf + offset
    // The reference renderer uses index/copies, not index/(copies-1). Preserve fractional input
    // in the ramp denominator while retaining the existing integer-copy topology policy.
    val fraction = i.toFloat().rf / max(copies, 1f.rf)
    val visible = selectIfLt(i.toFloat().rf, floor(copies), 1f.rf, 0f.rf)
    val alpha = visible * (startOpacity + (endOpacity - startOpacity) * fraction) / 100f

    for (shape in shapes) {
      val transformedShape =
        if (repeaterTransform != null) {
            transformRepeaterShape(shape, repeaterTransform, k, animationSettings)
          } else {
            shape
          }
          .let { identifyCopy(it, repeater, i) }
      instances.add(
        if (pathOnly) {
          RepeatedShapeInstance(withPathVisibility(transformedShape, visible))
        } else RepeatedShapeInstance(shape = transformedShape, opacityMultiplier = alpha)
      )
    }
  }

  return instances
}

/** Path consumers ignore the paint ramp, but must still hide unused live copy slots. */
@SuppressLint("RestrictedApi")
private fun withPathVisibility(shape: RemoteShape, visible: RemoteFloat): RemoteShape {
  if (visible.constantValueOrNull == 1f) return shape
  return when (shape) {
    is RemoteLottiePath ->
      RemoteLottiePath(
        shape.path,
        shape.fillRule,
        shape.trim,
        shape.geometryTransforms,
        shape.geometryVisibility * visible,
        shape.identity,
      )
    is RemoteBooleanPath ->
      RemoteBooleanPath(
        withPathVisibility(shape.remainder, visible),
        withPathVisibility(shape.last, visible),
        shape.operation,
      )
    else -> error("Repeater path visibility requires path geometry")
  }
}

/** Bounds recorded topology, including temporal easing overshoot; visibility stays live. */
private fun repeaterCapacity(repeater: Repeater): Int {
  val property =
    repeater.copies as? AnimatedScalarProperty
      ?: error("Repeater copy expressions require finite keyframe bounds")
  val values = property.keyframes.map { it.value.constantValue }
  require(values.isNotEmpty() && values.all { it.isFinite() }) { "Invalid repeater copy count" }
  val minimum = values.min()
  val maximum = values.max()
  val easingExtent =
    property.keyframes
      .flatMap { listOfNotNull(it.inTangent, it.outTangent).flatMap { easing -> easing.yValues } }
      .maxOfOrNull { kotlin.math.abs(it.constantValue) }
      ?.coerceAtLeast(1f) ?: 1f
  val capacity =
    kotlin.math.ceil(maximum + (maximum - minimum) * easingExtent).toInt().coerceAtLeast(0)
  require(capacity <= 1024) { "Repeater requires more than 1024 recorded copies" }
  return capacity
}

/** Transforms a [RemoteShape] by a Lottie [Transform] at step [k]. */
@SuppressLint("RestrictedApi")
internal fun transformRepeaterShape(
  shape: RemoteShape,
  transform: Transform,
  k: RemoteFloat,
  animationSettings: LottieSettings,
): RemoteShape {
  return when (shape) {
    is RemoteLottiePath -> transformRepeaterLottiePath(shape, transform, k, animationSettings)
    is RemoteBooleanPath ->
      RemoteBooleanPath(
        transformRepeaterShape(shape.remainder, transform, k, animationSettings),
        transformRepeaterShape(shape.last, transform, k, animationSettings),
        shape.operation,
      )
    is RemoteGroup -> {
      // A repeater transforms the whole painted group in its parent's coordinate space.
      // Transforming child vertices instead reverses nested transforms and leaves paint
      // coordinates (stroke widths, gradients and dashes) unscaled.
      val anchor = animatePosition(transform.anchorPoint, animationSettings)
      val translation = animatePosition(transform.positionTranslation, animationSettings)
      val scale = animateVector(transform.scale, animationSettings)
      val instanceTransform =
        transform.copy(
          anchorPoint = StaticPositionProperty(value = anchor),
          positionTranslation =
            StaticPositionProperty(
              value = Point(anchor.x + translation.x * k, anchor.y + translation.y * k)
            ),
          scale =
            StaticVectorProperty(
              animated = false.rb,
              value = scale.map { pow(it / 100f, k) * 100f },
            ),
          rotation =
            StaticScalarProperty(value = animateScalar(transform.rotation, animationSettings) * k),
          opacity = StaticScalarProperty(value = 100f.rf),
          skew =
            transform.skew?.let {
              StaticScalarProperty(value = animateScalar(it, animationSettings) * k)
            },
          skewAxis =
            transform.skewAxis?.let {
              StaticScalarProperty(value = animateScalar(it, animationSettings))
            },
        )
      RemoteGroup(
        childShapes = listOf(StyledShapes(listOf(shape), NoopStyle())),
        animationSettings = animationSettings,
        transform = instanceTransform,
      )
    }
    else -> shape
  }
}

/** Transforms a [RemoteLottiePath] by a repeater [Transform] at step [k]. */
@SuppressLint("RestrictedApi")
internal fun transformRepeaterLottiePath(
  lottiePath: RemoteLottiePath,
  transform: Transform,
  k: RemoteFloat,
  animationSettings: LottieSettings,
): RemoteLottiePath {
  return RemoteLottiePath(
    lottiePath.path,
    lottiePath.fillRule,
    lottiePath.trim,
    lottiePath.geometryTransforms + DeferredPathTransform(transform, animationSettings, k),
    lottiePath.geometryVisibility,
    lottiePath.identity,
  )
}

/** Painted and path-only evaluations must address the same authored copy, even if coincident. */
private fun identifyCopy(shape: RemoteShape, repeater: Repeater, index: Int): RemoteShape =
  when (shape) {
    is RemoteLottiePath -> shape.withIdentity(GeometryIdentity(repeater, shape.identity, index))
    is RemoteGroup ->
      RemoteGroup(
        shape.childShapes.map {
          it.copy(shapes = it.shapes.map { child -> identifyCopy(child, repeater, index) })
        },
        shape.animationSettings,
        shape.transform,
        shape.opacityMultiplier,
      )
    is RemoteBooleanPath ->
      RemoteBooleanPath(
        identifyCopy(shape.remainder, repeater, index),
        identifyCopy(shape.last, repeater, index),
        shape.operation,
      )
    else -> shape
  }

/** Transforms a single [RemoteBezierValue] by a repeater [Transform] at step [k]. */
@SuppressLint("RestrictedApi")
internal fun transformRepeaterBezierValue(
  subpath: RemoteBezierValue,
  transform: Transform,
  k: RemoteFloat,
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

  val scaleXK = pow(scaleX, k)
  val scaleYK = pow(scaleY, k)

  val rotK = rotation * k
  val skewK = skew?.let { it * k }
  val transXK = anchorPoint.x + translation.x * k
  val transYK = anchorPoint.y + translation.y * k

  val newVertices =
    subpath.vertices.map { point ->
      transformRepeaterPoint(
        x = point.getOrElse(0) { 0f.rf },
        y = point.getOrElse(1) { 0f.rf },
        anchorX = anchorPoint.x,
        anchorY = anchorPoint.y,
        scaleXK = scaleXK,
        scaleYK = scaleYK,
        rotK = rotK,
        skewK = skewK,
        skewAxis = skewAxis,
        transXK = transXK,
        transYK = transYK,
      )
    }

  val newInTangents =
    subpath.inTangents.map { tangent ->
      transformRepeaterTangent(
        dx = tangent.getOrElse(0) { 0f.rf },
        dy = tangent.getOrElse(1) { 0f.rf },
        scaleXK = scaleXK,
        scaleYK = scaleYK,
        rotK = rotK,
        skewK = skewK,
        skewAxis = skewAxis,
      )
    }

  val newOutTangents =
    subpath.outTangents.map { tangent ->
      transformRepeaterTangent(
        dx = tangent.getOrElse(0) { 0f.rf },
        dy = tangent.getOrElse(1) { 0f.rf },
        scaleXK = scaleXK,
        scaleYK = scaleYK,
        rotK = rotK,
        skewK = skewK,
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
private fun transformRepeaterPoint(
  x: RemoteFloat,
  y: RemoteFloat,
  anchorX: RemoteFloat,
  anchorY: RemoteFloat,
  scaleXK: RemoteFloat,
  scaleYK: RemoteFloat,
  rotK: RemoteFloat,
  skewK: RemoteFloat?,
  skewAxis: RemoteFloat?,
  transXK: RemoteFloat,
  transYK: RemoteFloat,
): List<RemoteFloat> {
  var px = (x - anchorX) * scaleXK
  var py = (y - anchorY) * scaleYK

  if (skewK != null) {
    val axis = skewAxis ?: 0f.rf
    val radAxis = toRad(90f.rf - axis)
    val cosA = cos(radAxis)
    val sinA = sin(radAxis)
    val rx = px * cosA + py * sinA
    val ry = -px * sinA + py * cosA
    val skX = rx
    val skY = rx * tan(toRad(skewK)) + ry
    px = skX * cosA - skY * sinA
    py = skX * sinA + skY * cosA
  }

  val rad = toRad(rotK)
  val cosR = cos(rad)
  val sinR = sin(rad)
  val rx = px * cosR - py * sinR
  val ry = px * sinR + py * cosR

  val finalX = rx + transXK
  val finalY = ry + transYK

  return listOf(finalX, finalY)
}

@SuppressLint("RestrictedApi")
private fun transformRepeaterTangent(
  dx: RemoteFloat,
  dy: RemoteFloat,
  scaleXK: RemoteFloat,
  scaleYK: RemoteFloat,
  rotK: RemoteFloat,
  skewK: RemoteFloat?,
  skewAxis: RemoteFloat?,
): List<RemoteFloat> {
  var px = dx * scaleXK
  var py = dy * scaleYK

  if (skewK != null) {
    val axis = skewAxis ?: 0f.rf
    val radAxis = toRad(90f.rf - axis)
    val cosA = cos(radAxis)
    val sinA = sin(radAxis)
    val rx = px * cosA + py * sinA
    val ry = -px * sinA + py * cosA
    val skX = rx
    val skY = rx * tan(toRad(skewK)) + ry
    px = skX * cosA - skY * sinA
    py = skX * sinA + skY * cosA
  }

  val rad = toRad(rotK)
  val cosR = cos(rad)
  val sinR = sin(rad)
  val rx = px * cosR - py * sinR
  val ry = px * sinR + py * cosR

  return listOf(rx, ry)
}
