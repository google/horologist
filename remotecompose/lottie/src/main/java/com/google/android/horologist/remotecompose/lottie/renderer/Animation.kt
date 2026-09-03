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
import androidx.compose.remote.creation.compose.state.rf
import com.google.android.horologist.remotecompose.lottie.format.properties.ScalarKeyframeEasing

@SuppressLint("RestrictedApi")
internal fun lookupValueInBezier(
  a: Float,
  b: Float,
  c: Float,
  d: Float,
  duration: Float,
  frame: RemoteFloat,
): RemoteFloat {
  // TODO implement using Remote Compose expressions to avoid a Compose UI impl
  val easing = CubicBezierEasing(a, b, c, d)
  val frameAnimationValues = mutableListOf<Float>()

  for (i in 0..duration.toInt()) {
    frameAnimationValues.add(easing.transform(i / duration))
  }

  val remoteFrameAnimationValues = RemoteFloatArray(frameAnimationValues.map { it.rf })
  val clampedFrame = clamp(value = frame, min = 0.rf, max = (frameAnimationValues.size - 1).rf)

  return remoteFrameAnimationValues[clampedFrame]
}

internal val scalarLinearEasingOut = ScalarKeyframeEasing(x = 0f, 0f)
internal val scalarLinearEasingIn = ScalarKeyframeEasing(1f, 1f)
