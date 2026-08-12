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
import androidx.compose.remote.creation.compose.state.lerp
import androidx.compose.remote.creation.compose.state.rf
import com.google.android.horologist.remotecompose.lottie.LottieSettings
import com.google.android.horologist.remotecompose.lottie.format.AnimatedPositionProperty
import com.google.android.horologist.remotecompose.lottie.format.BasePositionProperty
import com.google.android.horologist.remotecompose.lottie.format.StaticPositionProperty
import com.google.android.horologist.remotecompose.lottie.format.VectorPropertyKeyframe

/**
 * Animates a position property.
 *
 * Take a BasePositionProperty (either animated or static) and convert it to a [Point] of
 * RemoteFloats (x, y). If the position is animated, the RemoteFloats will change based on the
 * animation specified in the Lottie Position Property.
 */
@SuppressLint("RestrictedApi")
internal fun animatePosition(
  position: BasePositionProperty,
  animationSettings: LottieSettings,
): Point {
  return when (position) {
    is StaticPositionProperty -> Point(position.value[0].rf, position.value[1].rf)
    is AnimatedPositionProperty -> evaluateAnimatedPosition(position, animationSettings)
  }
}

@SuppressLint("RestrictedApi")
private fun evaluateAnimatedPosition(
  position: AnimatedPositionProperty,
  animationSettings: LottieSettings,
): Point {
  if (position.keyframes.size == 1) {
    return Point(position.keyframes[0].value[0].rf, position.keyframes[0].value[1].rf)
  }

  val xSegments = mutableListOf<AnimationSegment>()
  val ySegments = mutableListOf<AnimationSegment>()

  buildPositionSegments(position.keyframes, animationSettings, xSegments, ySegments)

  return Point(
    x = chainAnimation(xSegments, animationSettings.currentFrame),
    y = chainAnimation(ySegments, animationSettings.currentFrame),
  )
}

@SuppressLint("RestrictedApi")
private fun buildPositionSegments(
  keyframes: List<VectorPropertyKeyframe>,
  animationSettings: LottieSettings,
  xSegments: MutableList<AnimationSegment>,
  ySegments: MutableList<AnimationSegment>,
) {
  addInitialPositionSegment(keyframes[0], xSegments, ySegments)

  for (i in 0 until keyframes.size - 1) {
    val start = keyframes[i]
    val end = keyframes[i + 1]

    val progress =
      evaluateKeyframeProgress(
        start.frame,
        end.frame,
        start.outTangent,
        start.inTangent,
        animationSettings.currentFrame,
      )

    xSegments.add(AnimationSegment(start.frame, lerp(start.value[0].rf, end.value[0].rf, progress)))
    ySegments.add(AnimationSegment(start.frame, lerp(start.value[1].rf, end.value[1].rf, progress)))
  }
}

private fun addInitialPositionSegment(
  firstKeyframe: VectorPropertyKeyframe,
  xSegments: MutableList<AnimationSegment>,
  ySegments: MutableList<AnimationSegment>,
) {
  if (firstKeyframe.frame != 0f) {
    xSegments.add(AnimationSegment(0f, firstKeyframe.value[0].rf))
    ySegments.add(AnimationSegment(0f, firstKeyframe.value[1].rf))
  }
}
