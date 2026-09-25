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

package com.google.android.horologist.remotecompose.lottie.renderer

import android.annotation.SuppressLint
import androidx.compose.remote.creation.compose.state.RemoteFloat
import androidx.compose.remote.creation.compose.state.clamp
import androidx.compose.remote.creation.compose.state.cubicEasing
import androidx.compose.remote.creation.compose.state.rf
import com.google.android.horologist.remotecompose.lottie.format.values.KeyframeEasing

@SuppressLint("RestrictedApi")
internal fun lookupValueInBezier(
  a: Float,
  b: Float,
  c: Float,
  d: Float,
  duration: Float,
  frame: RemoteFloat,
): RemoteFloat = lookupValueInBezier(a.rf, b.rf, c.rf, d.rf, duration, frame)

@SuppressLint("RestrictedApi")
internal fun lookupValueInBezier(
  a: RemoteFloat,
  b: RemoteFloat,
  c: RemoteFloat,
  d: RemoteFloat,
  duration: Float,
  frame: RemoteFloat,
): RemoteFloat {
  // Coincident keyframes select the later value without dividing by a zero duration.
  if (duration <= 0f) return 1f.rf

  val progress = clamp(frame / duration, 0f.rf, 1f.rf)

  // A diagonal timing curve is exactly linear. Evaluating cubic easing by bisection introduces
  // enough rounding error to miss exact zero crossings (which can enable/disable a modifier).
  val aConst = a.constantValueOrNull
  val bConst = b.constantValueOrNull
  val cConst = c.constantValueOrNull
  val dConst = d.constantValueOrNull
  if (aConst != null && aConst == bConst && cConst != null && cConst == dConst) {
    return progress
  }

  return cubicEasing(a, b, c, d, progress)
}

internal val scalarLinearEasingOut = KeyframeEasing(x = 0f.rf, y = 0f.rf)
internal val scalarLinearEasingIn = KeyframeEasing(x = 1f.rf, y = 1f.rf)
