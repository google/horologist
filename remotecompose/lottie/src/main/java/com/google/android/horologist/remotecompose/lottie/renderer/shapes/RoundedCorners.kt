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
import com.google.android.horologist.remotecompose.lottie.renderer.RemoteBooleanPath
import com.google.android.horologist.remotecompose.lottie.renderer.RemoteGroup
import com.google.android.horologist.remotecompose.lottie.renderer.RemoteLottiePath
import com.google.android.horologist.remotecompose.lottie.renderer.RemoteShape
import com.google.android.horologist.remotecompose.lottie.renderer.properties.animateBezier
import com.google.android.horologist.remotecompose.lottie.renderer.properties.animateScalar

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
