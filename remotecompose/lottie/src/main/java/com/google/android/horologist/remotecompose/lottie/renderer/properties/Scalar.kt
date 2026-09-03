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
import androidx.compose.remote.creation.compose.state.selectIfLt
import com.google.android.horologist.remotecompose.lottie.LottieSettings
import com.google.android.horologist.remotecompose.lottie.format.properties.AnimatedScalarProperty
import com.google.android.horologist.remotecompose.lottie.format.properties.BaseScalarProperty
import com.google.android.horologist.remotecompose.lottie.format.properties.StaticScalarProperty
import com.google.android.horologist.remotecompose.lottie.renderer.lookupValueInBezier
import com.google.android.horologist.remotecompose.lottie.renderer.scalarLinearEasingIn
import com.google.android.horologist.remotecompose.lottie.renderer.scalarLinearEasingOut

internal data class AnimationSegment(val startFrame: Float, val value: RemoteFloat)

/**
 * Support keyframed animations (and delayed start animations) by chaining multiple animations
 * together.
 *
 * This recursively builds up a chain of IFELSE operations to select the correct RemoteFloat
 * representing the current segment of the animation.
 */
@SuppressLint("RestrictedApi")
internal fun chainAnimation(segments: List<AnimationSegment>, frame: RemoteFloat): RemoteFloat {
  if (segments.size == 1) {
    return segments[0].value
  }

  return selectIfLt(
    frame,
    segments[1].startFrame.rf,
    segments[0].value,
    chainAnimation(segments.subList(1, segments.size), frame),
  )
}

/**
 * Animates a scalar property.
 *
 * Take a BaseScalarProperty (either animated or static) and convert it to a RemoteFloat. If the
 * scalar is animated, the RemoteFloat will change based on the animation specified in the Lottie
 * Scalar Property.
 */
@SuppressLint("RestrictedApi")
internal fun animateScalar(
  scalar: BaseScalarProperty,
  animationSettings: LottieSettings,
): RemoteFloat {
  return when (scalar) {
    is StaticScalarProperty -> scalar.value.rf
    is AnimatedScalarProperty -> {
      if (scalar.keyframes.isEmpty()) {
        return 0f.rf
      }
      if (scalar.keyframes.size == 1) {
        return scalar.keyframes[0].value.rf
      }

      val animationSegments = mutableListOf<AnimationSegment>()

      val firstKeyframe = scalar.keyframes[0]
      if (firstKeyframe.frame != 0f) {
        animationSegments.add(AnimationSegment(0f, firstKeyframe.value.rf))
      }

      for (i in 0 until scalar.keyframes.size - 1) {
        val startKeyframe = scalar.keyframes[i]
        val endKeyframe = scalar.keyframes[i + 1]
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

        val segmentValue = lerp(startKeyframe.value.rf, endKeyframe.value.rf, currentBezierValue)
        animationSegments.add(AnimationSegment(startKeyframe.frame, segmentValue))
      }

      chainAnimation(animationSegments, animationSettings.currentFrame)
    }
  }
}
