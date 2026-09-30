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
import androidx.compose.remote.creation.compose.state.clamp
import androidx.compose.remote.creation.compose.state.rf
import com.google.android.horologist.remotecompose.lottie.LottieSettings
import com.google.android.horologist.remotecompose.lottie.format.graphicelement.modifiers.TrimPath
import com.google.android.horologist.remotecompose.lottie.format.values.BezierValue
import com.google.android.horologist.remotecompose.lottie.renderer.GeometryIdentity
import com.google.android.horologist.remotecompose.lottie.renderer.RemoteBooleanPath
import com.google.android.horologist.remotecompose.lottie.renderer.RemoteGroup
import com.google.android.horologist.remotecompose.lottie.renderer.RemoteLottiePath
import com.google.android.horologist.remotecompose.lottie.renderer.RemotePathTrim
import com.google.android.horologist.remotecompose.lottie.renderer.RemoteShape
import com.google.android.horologist.remotecompose.lottie.renderer.properties.RemoteBezierValue
import com.google.android.horologist.remotecompose.lottie.renderer.properties.animateScalar
import com.google.android.horologist.remotecompose.lottie.renderer.properties.toRemote
import kotlin.math.abs

/** Build the shared cut once from authored operands, then apply it to every paint view. */
@SuppressLint("RestrictedApi")
internal fun individualTrimModifier(
  operands: List<RemoteShape>,
  trim: TrimPath,
  settings: LottieSettings,
): (List<RemoteShape>) -> List<RemoteShape> {
  if (trim.hidden?.constantValue == true) return { it }
  val sources = linkedMapOf<GeometryIdentity, RemoteLottiePath>()
  fun collect(shape: RemoteShape) {
    when (shape) {
      is RemoteLottiePath -> {
        if (shape.identity !in sources) {
          val path = shape.materializeTrim()
          sources[shape.identity] =
            path.withPath(
              path.path.map { it.copy(visibility = it.visibility * path.geometryVisibility) }
            )
        }
      }
      is RemoteGroup -> shape.childShapes.forEach { it.shapes.forEach(::collect) }
      is RemoteBooleanPath -> error("Trim after a live boolean merge is not yet supported")
      else -> error("Individual trim requires path geometry")
    }
  }
  operands.forEach(::collect)
  val cut =
    RemotePathTrim(
      animateScalar(trim.start, settings) / 100f,
      animateScalar(trim.end, settings) / 100f,
      animateScalar(trim.offset, settings) / 360f,
    )
  val results = materializeSharedTrim(sources.values.toList(), cut).associateBy { it.identity }
  fun apply(shape: RemoteShape): RemoteShape =
    when (shape) {
      is RemoteLottiePath -> {
        val result =
          checkNotNull(results[shape.identity]) { "Trim paint view has no authored operand" }
        RemoteLottiePath(
          result.path,
          shape.fillRule,
          geometryTransforms = shape.geometryTransforms,
          geometryVisibility = shape.geometryVisibility,
          identity = shape.identity,
        )
      }
      is RemoteGroup ->
        RemoteGroup(
          shape.childShapes.map { it.copy(shapes = it.shapes.map(::apply)) },
          shape.animationSettings,
          shape.transform,
          shape.opacityMultiplier,
        )
      else -> error("Individual trim requires path geometry")
    }
  return { shapes -> shapes.map(::apply) }
}

/** Apply trims to the geometry at their authored position, including existing paint views. */
@SuppressLint("RestrictedApi")
internal fun evaluateTrimPaths(
  shapes: List<RemoteShape>,
  trim: TrimPath,
  settings: LottieSettings,
): List<RemoteShape> {
  if (trim.hidden?.constantValue == true || shapes.isEmpty()) return shapes
  return shapes.map { shape ->
    when (shape) {
      is RemoteLottiePath -> {
        val result =
          if (shape.trim == null) trimEvaluatedPaths(shape.path, trim, settings, playback = true)
          else
            RemoteLottiePath(
              shape.path,
              trim =
                RemotePathTrim(
                  animateScalar(trim.start, settings) / 100f,
                  animateScalar(trim.end, settings) / 100f,
                  animateScalar(trim.offset, settings) / 360f,
                  shape.trim,
                ),
            )
        RemoteLottiePath(
          result.path,
          shape.fillRule,
          result.trim,
          shape.geometryTransforms,
          shape.geometryVisibility,
          shape.identity,
        )
      }
      is RemoteGroup ->
        RemoteGroup(
          shape.childShapes.map { it.copy(shapes = evaluateTrimPaths(it.shapes, trim, settings)) },
          shape.animationSettings,
          shape.transform,
          shape.opacityMultiplier,
        )
      is RemoteBooleanPath -> error("Trim after a live boolean merge is not yet supported")
      else -> shape
    }
  }
}

/** Parametric and authored paths share the same live trim policy. */
@SuppressLint("RestrictedApi")
internal fun trimParametricPath(
  path: RemoteBezierValue,
  trim: TrimPath?,
  settings: LottieSettings,
): RemoteLottiePath = trimEvaluatedPaths(listOf(path), trim, settings)

/** Never synthesize trimmed keyframes: path length and trim fractions change independently. */
@SuppressLint("RestrictedApi")
internal fun trimEvaluatedPaths(
  paths: List<RemoteBezierValue>,
  trim: TrimPath?,
  settings: LottieSettings,
  playback: Boolean = false,
): RemoteLottiePath {
  if (trim == null || trim.hidden?.constantValue == true) return RemoteLottiePath(paths)
  val start = clamp(animateScalar(trim.start, settings) / 100f, 0f.rf, 1f.rf)
  val end = clamp(animateScalar(trim.end, settings) / 100f, 0f.rf, 1f.rf)
  val offset = animateScalar(trim.offset, settings) / 360f
  val constantStart = start.constantValueOrNull
  val constantEnd = end.constantValueOrNull
  val constantOffset = offset.constantValueOrNull
  if (constantStart != null && constantEnd != null && abs(constantEnd - constantStart) >= 1f) {
    return RemoteLottiePath(paths)
  }
  if (paths.size > 1) {
    // One shape's contours share its arc-length domain, including live/conditional contours.
    return RemoteLottiePath(paths, trim = RemotePathTrim(start, end, offset)).materializeTrim()
  }
  val constantGeometry = paths.all { path ->
    (path.vertices + path.inTangents + path.outTangents).flatten().all {
      it.constantValueOrNull != null
    }
  }
  if (
    !playback &&
      constantGeometry &&
      constantStart != null &&
      constantEnd != null &&
      constantOffset != null
  ) {
    // A genuine constant evaluation can still expose cut vertices to subsequent modifiers.
    return RemoteLottiePath(
      paths.flatMap { path ->
        val constant =
          BezierValue(
            closed = path.closed,
            vertices = path.vertices.map { point -> point.map { it.constantValue } },
            inTangents = path.inTangents.map { point -> point.map { it.constantValue } },
            outTangents = path.outTangents.map { point -> point.map { it.constantValue } },
          )
        trimBezierValue(
            constant,
            constantStart,
            constantEnd,
            constantOffset,
            keepStructureIfDegenerate = false,
          )
          .map { it.toRemote().copy(visibility = path.visibility) }
      }
    )
  }
  return RemoteLottiePath(paths, trim = RemotePathTrim(start, end, offset))
}

/** Constant cuts can expose real vertices to a subsequent modifier without sampling time. */
@SuppressLint("RestrictedApi")
internal fun RemoteLottiePath.materializeConstantTrim(): RemoteLottiePath {
  val cut = trim ?: return this
  // The scalar evaluator trims one contour. Compound paths need the shared length domain.
  if (path.size > 1) return this
  if (cut.ranges.any { (a, b) -> a.constantValueOrNull == null || b.constantValueOrNull == null })
    return this
  if (
    path.any { value ->
      (value.vertices + value.inTangents + value.outTangents).flatten().any {
        it.constantValueOrNull == null
      }
    }
  )
    return this
  val cutPaths = path.flatMap { value ->
    val constant =
      BezierValue(
        closed = value.closed,
        vertices = value.vertices.map { p -> p.map { it.constantValue } },
        inTangents = value.inTangents.map { p -> p.map { it.constantValue } },
        outTangents = value.outTangents.map { p -> p.map { it.constantValue } },
      )
    cut.ranges.flatMap { (a, b) ->
      trimBezierValue(constant, a.constantValue, b.constantValue, keepStructureIfDegenerate = false)
        .map { it.toRemote().copy(visibility = value.visibility) }
    }
  }
  return RemoteLottiePath(
    cutPaths,
    fillRule,
    geometryTransforms = geometryTransforms,
    geometryVisibility = geometryVisibility,
    identity = identity,
  )
}
