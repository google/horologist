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
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.remote.creation.compose.state.RemoteFloat
import androidx.compose.remote.creation.compose.state.RemoteFloatArray
import androidx.compose.remote.creation.compose.state.clamp
import androidx.compose.remote.creation.compose.state.floor
import androidx.compose.remote.creation.compose.state.lerp
import androidx.compose.remote.creation.compose.state.min
import androidx.compose.remote.creation.compose.state.rf
import com.google.android.horologist.remotecompose.lottie.format.values.KeyframeEasing
import kotlin.math.ceil

@SuppressLint("RestrictedApi")
internal fun lookupValueInBezier(
  a: Float,
  b: Float,
  c: Float,
  d: Float,
  duration: Float,
  frame: RemoteFloat,
): RemoteFloat {
  // Coincident keyframes select the later value without dividing by a zero duration.
  if (duration <= 0f) return 1f.rf

  // A diagonal timing curve is exactly linear. Sampling it introduces enough rounding
  // error to miss exact zero crossings (which can enable/disable a geometry modifier).
  if (a == b && c == d) return clamp(frame / duration, 0f.rf, 1f.rf)

  // TODO implement using Remote Compose expressions to avoid a Compose UI impl
  val easing = CubicBezierEasing(a, b, c, d)
  // Include both endpoints even for fractional durations. Bound the table size and
  // interpolate neighbouring samples: RemoteFloatArray indexing truncates to an integer.
  val sampleCount = ceil(duration).toInt().coerceIn(32, 4096)
  val samples =
    RemoteFloatArray((0..sampleCount).map { easing.transform(it.toFloat() / sampleCount).rf })
  val sampleIndex = clamp(frame / duration, 0f.rf, 1f.rf) * sampleCount.rf
  val lowerIndex = floor(sampleIndex)
  val upperIndex = min(lowerIndex + 1f, sampleCount.rf)
  return lerp(samples[lowerIndex], samples[upperIndex], sampleIndex - lowerIndex)
}

internal fun lookupValueInBezier(
  a: RemoteFloat,
  b: RemoteFloat,
  c: RemoteFloat,
  d: RemoteFloat,
  duration: Float,
  frame: RemoteFloat,
): RemoteFloat =
  lookupValueInBezier(
    a.constantValue,
    b.constantValue,
    c.constantValue,
    d.constantValue,
    duration,
    frame,
  )

internal val scalarLinearEasingOut = KeyframeEasing(x = 0f.rf, y = 0f.rf)
internal val scalarLinearEasingIn = KeyframeEasing(x = 1f.rf, y = 1f.rf)
