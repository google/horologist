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

package com.google.android.horologist.remotecompose.lottie.renderer.properties

import android.annotation.SuppressLint
import androidx.compose.remote.creation.compose.state.RemoteFloat
import androidx.compose.remote.creation.compose.state.lerp
import androidx.compose.remote.creation.compose.state.rf
import com.google.android.horologist.remotecompose.lottie.LottieSettings
import com.google.android.horologist.remotecompose.lottie.format.properties.AnimatedVectorProperty
import com.google.android.horologist.remotecompose.lottie.format.properties.BaseVectorProperty
import com.google.android.horologist.remotecompose.lottie.format.properties.StaticVectorProperty
import com.google.android.horologist.remotecompose.lottie.renderer.lookupValueInBezier
import com.google.android.horologist.remotecompose.lottie.renderer.scalarLinearEasingIn
import com.google.android.horologist.remotecompose.lottie.renderer.scalarLinearEasingOut

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
    is AnimatedVectorProperty -> {
      if (vector.keyframes.size == 1) {
        return vector.keyframes[0].value.map { it.rf }
      }

      val animationSegments = mutableListOf<List<AnimationSegment>>()

      val firstKeyframe = vector.keyframes[0]
      if (firstKeyframe.frame != 0f) {
        animationSegments.add(firstKeyframe.value.map { AnimationSegment(0f, it.rf) })
      }

      for (i in 0 until vector.keyframes.size - 1) {
        val startKeyframe = vector.keyframes[i]
        val endKeyframe = vector.keyframes[i + 1]
        val duration = endKeyframe.frame - startKeyframe.frame
        val frameInAnimation = animationSettings.currentFrame - startKeyframe.frame
        val outTangent = startKeyframe.outTangent ?: scalarLinearEasingOut
        val inTangent = startKeyframe.inTangent ?: scalarLinearEasingIn
        val currentBezierValue =
          lookupValueInBezier(
            outTangent.x,
            outTangent.y,
            inTangent.x,
            inTangent.y,
            duration,
            frameInAnimation,
          )

        val segment =
          startKeyframe.value.mapIndexed { index, value ->
            AnimationSegment(
              startKeyframe.frame,
              lerp(value.rf, endKeyframe.value[index].rf, currentBezierValue),
            )
          }

        animationSegments.add(segment)
      }

      val vectorSize = animationSegments[0].size
      return (0..<vectorSize).map { index ->
        chainAnimation(animationSegments.map { it[index] }, animationSettings.currentFrame)
      }
    }
  }
}
