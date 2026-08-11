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
import androidx.compose.remote.creation.compose.state.lerp
import androidx.compose.remote.creation.compose.state.rf
import com.google.android.horologist.remotecompose.lottie.LottieSettings
import com.google.android.horologist.remotecompose.lottie.format.AnimatedVectorProperty
import com.google.android.horologist.remotecompose.lottie.format.BaseVectorProperty
import com.google.android.horologist.remotecompose.lottie.format.StaticVectorProperty
import com.google.android.horologist.remotecompose.lottie.format.VectorPropertyKeyframe

/**
 * Animates a vector property.
 *
 * Take a BaseVectorProperty (either animated or static) and convert it to a List of RemoteFloats.
 * If the vector is animated, the RemoteFloat will change based on the animation specified in the
 * Lottie Vector Property.
 */
@SuppressLint("RestrictedApi")
internal fun animateVector(
  vector: BaseVectorProperty,
  animationSettings: LottieSettings,
): List<RemoteFloat> {
  return when (vector) {
    is StaticVectorProperty -> vector.value.map { it.rf }
    is AnimatedVectorProperty -> evaluateAnimatedVector(vector, animationSettings)
  }
}

@SuppressLint("RestrictedApi")
private fun evaluateAnimatedVector(
  vector: AnimatedVectorProperty,
  animationSettings: LottieSettings,
): List<RemoteFloat> {
  if (vector.keyframes.size == 1) {
    return vector.keyframes[0].value.map { it.rf }
  }

  val animationSegments = buildVectorSegments(vector.keyframes, animationSettings)

  val vectorSize = animationSegments[0].size
  return (0..<vectorSize).map { index ->
    chainAnimation(animationSegments.map { it[index] }, animationSettings.currentFrame)
  }
}

@SuppressLint("RestrictedApi")
private fun buildVectorSegments(
  keyframes: List<VectorPropertyKeyframe>,
  animationSettings: LottieSettings,
): List<List<AnimationSegment>> {
  val segments = mutableListOf<List<AnimationSegment>>()
  addInitialVectorSegment(keyframes[0], segments)

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

    val segment =
      start.value.mapIndexed { index, v ->
        AnimationSegment(start.frame, lerp(v.rf, end.value[index].rf, progress))
      }
    segments.add(segment)
  }
  return segments
}

private fun addInitialVectorSegment(
  firstKeyframe: VectorPropertyKeyframe,
  segments: MutableList<List<AnimationSegment>>,
) {
  if (firstKeyframe.frame != 0f) {
    segments.add(firstKeyframe.value.map { AnimationSegment(0f, it.rf) })
  }
}
