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
import com.google.android.horologist.remotecompose.lottie.format.properties.AnimatedGradientProperty
import com.google.android.horologist.remotecompose.lottie.format.properties.BaseGradientProperty
import com.google.android.horologist.remotecompose.lottie.format.properties.StaticGradientProperty
import com.google.android.horologist.remotecompose.lottie.format.values.GradientValue
import com.google.android.horologist.remotecompose.lottie.renderer.lookupValueInBezier
import com.google.android.horologist.remotecompose.lottie.renderer.scalarLinearEasingIn
import com.google.android.horologist.remotecompose.lottie.renderer.scalarLinearEasingOut

internal data class RemoteGradientValue(val numberOfColors: Int, val values: List<RemoteFloat>) {
  val hasTransparency: Boolean
    get() =
      if (numberOfColors > 0) {
        values.size > numberOfColors * 4
      } else {
        values.size > 0 && values.size % 4 != 0
      }
}

internal data class GradientAnimationSegment(val startFrame: Float, val value: List<RemoteFloat>)

internal fun GradientValue.toRemote(): RemoteGradientValue {
  return RemoteGradientValue(numberOfColors = numberOfColors, values = values.map { it.rf })
}

@SuppressLint("RestrictedApi")
internal fun animateGradient(
  gradient: BaseGradientProperty,
  animationSettings: LottieSettings,
): RemoteGradientValue {
  return when (gradient) {
    is StaticGradientProperty -> gradient.value.toRemote()
    is AnimatedGradientProperty -> {
      if (gradient.keyframes.isEmpty()) {
        return RemoteGradientValue(
          numberOfColors = gradient.numberOfColors ?: 0,
          values = emptyList(),
        )
      }
      if (gradient.keyframes.size == 1) {
        val single =
          gradient.keyframes[0].value.firstOrNull()
            ?: GradientValue(numberOfColors = gradient.numberOfColors ?: 0)
        return single.toRemote()
      }

      val firstKeyframe = gradient.keyframes[0]
      val firstVal =
        firstKeyframe.value.firstOrNull()
          ?: GradientValue(numberOfColors = gradient.numberOfColors ?: 0)
      val numberOfColors = gradient.numberOfColors ?: firstVal.numberOfColors

      val animationSegments = mutableListOf<GradientAnimationSegment>()
      if (firstKeyframe.frame != 0f) {
        animationSegments.add(
          GradientAnimationSegment(startFrame = 0f, value = firstVal.values.map { it.rf })
        )
      }

      for (i in 0 until gradient.keyframes.size - 1) {
        val startKeyframe = gradient.keyframes[i]
        val endKeyframe = gradient.keyframes[i + 1]
        val duration = endKeyframe.frame - startKeyframe.frame
        val frameInAnimation = animationSettings.currentFrame - startKeyframe.frame

        val startGradient = startKeyframe.value.firstOrNull() ?: firstVal
        val endGradient = endKeyframe.value.firstOrNull() ?: startGradient

        val segmentValues =
          if (startKeyframe.hold?.constantValue == true) {
            startGradient.values.mapIndexed { index, startCoord ->
              val endCoord = endGradient.values.getOrElse(index) { startCoord }
              selectIfLt(frameInAnimation, duration.rf, startCoord.rf, endCoord.rf)
            }
          } else {
            val outTangent = startKeyframe.outTangent ?: scalarLinearEasingOut
            val inTangent = startKeyframe.inTangent ?: scalarLinearEasingIn

            val progress =
              lookupValueInBezier(
                outTangent.x,
                outTangent.y,
                inTangent.x,
                inTangent.y,
                duration,
                frameInAnimation,
              )

            startGradient.values.mapIndexed { index, startCoord ->
              val endCoord = endGradient.values.getOrElse(index) { startCoord }
              lerp(startCoord.rf, endCoord.rf, progress)
            }
          }

        animationSegments.add(GradientAnimationSegment(startKeyframe.frame, segmentValues))
      }

      val chainedValues = chainGradientAnimation(animationSegments, animationSettings.currentFrame)
      RemoteGradientValue(numberOfColors = numberOfColors, values = chainedValues)
    }
  }
}

@SuppressLint("RestrictedApi")
private fun chainGradientAnimation(
  segments: List<GradientAnimationSegment>,
  frame: RemoteFloat,
): List<RemoteFloat> {
  if (segments.size == 1) {
    return segments[0].value
  }

  val firstSegment = segments[0]
  val remainingChained = chainGradientAnimation(segments.subList(1, segments.size), frame)
  val nextStartFrame = segments[1].startFrame.rf

  return firstSegment.value.mapIndexed { index, coordVal ->
    val remainingVal = remainingChained.getOrElse(index) { coordVal }
    selectIfLt(frame, nextStartFrame, coordVal, remainingVal)
  }
}
