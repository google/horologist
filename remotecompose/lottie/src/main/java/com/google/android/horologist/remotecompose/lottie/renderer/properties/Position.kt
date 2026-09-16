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
import com.google.android.horologist.remotecompose.lottie.format.properties.AnimatedPositionProperty
import com.google.android.horologist.remotecompose.lottie.format.properties.BasePositionProperty
import com.google.android.horologist.remotecompose.lottie.format.properties.StaticPositionProperty
import com.google.android.horologist.remotecompose.lottie.format.values.Point
import com.google.android.horologist.remotecompose.lottie.renderer.lookupValueInBezier
import com.google.android.horologist.remotecompose.lottie.renderer.scalarLinearEasingIn
import com.google.android.horologist.remotecompose.lottie.renderer.scalarLinearEasingOut

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
    // Static constant position: directly return the Point.
    is StaticPositionProperty -> position.value
    // Keyframed animated position: interpolate [x, y] across keyframes using Bézier easing curves.
    is AnimatedPositionProperty -> {
      if (position.keyframes.isEmpty()) {
        return Point(0f.rf, 0f.rf)
      }
      // Single keyframe: hold static position at that single value.
      if (position.keyframes.size == 1) {
        return position.keyframes[0].value
      }

      val animationSegments = mutableListOf<List<AnimationSegment>>()

      // If the first keyframe starts after frame 0, prepend an initial static segment
      // holding the first keyframe's value from frame 0 until the first keyframe.
      val firstKeyframe = position.keyframes[0]
      if (firstKeyframe.frame.constantValue != 0f) {
        animationSegments.add(
          listOf(
            AnimationSegment(0f, firstKeyframe.value.x),
            AnimationSegment(0f, firstKeyframe.value.y),
          )
        )
      }

      // Build interpolation segments between adjacent keyframe pairs.
      for (i in 0 until position.keyframes.size - 1) {
        val startKeyframe = position.keyframes[i]
        val endKeyframe = position.keyframes[i + 1]
        val duration = endKeyframe.frame.constantValue - startKeyframe.frame.constantValue
        val frameInAnimation = animationSettings.currentFrame - startKeyframe.frame

        // Control point tangents for the cubic Bézier curve, defaulting to linear easing if
        // omitted.
        fun progressForDimension(index: Int): RemoteFloat {
          val outTangent = (startKeyframe.outTangent ?: scalarLinearEasingOut).forDimension(index)
          val inTangent = (startKeyframe.inTangent ?: scalarLinearEasingIn).forDimension(index)

          // Temporal easing may overshoot [0, 1]; spatial playback preserves that overshoot.
          return if (startKeyframe.hold.constantValue) {
            selectIfLt(frameInAnimation, duration.rf, 0f.rf, 1f.rf)
          } else
            lookupValueInBezier(
              outTangent.x,
              outTangent.y,
              inTangent.x,
              inTangent.y,
              duration,
              frameInAnimation,
            )
        }

        val progressX = progressForDimension(0)
        val spatial =
          if (
            !startKeyframe.hold.constantValue &&
              (startKeyframe.outSpatialTangent != null || startKeyframe.inSpatialTangent != null)
          ) {
            sampleSpatialPosition(
              startKeyframe.value,
              endKeyframe.value,
              startKeyframe.outSpatialTangent,
              startKeyframe.inSpatialTangent,
              progressX,
            )
          } else null

        // Without a spatial path, each coordinate uses its own temporal easing.
        val segment =
          listOf(
            AnimationSegment(
              startKeyframe.frame.constantValue,
              spatial?.x ?: lerp(startKeyframe.value.x, endKeyframe.value.x, progressX),
            ),
            AnimationSegment(
              startKeyframe.frame.constantValue,
              spatial?.y
                ?: lerp(startKeyframe.value.y, endKeyframe.value.y, progressForDimension(1)),
            ),
          )

        animationSegments.add(segment)
      }

      // Chain individual segments together into conditional expressions that resolve
      // the appropriate interpolated value for X and Y based on currentFrame.
      val chainedX = chainAnimation(animationSegments.map { it[0] }, animationSettings.currentFrame)
      val chainedY = chainAnimation(animationSegments.map { it[1] }, animationSettings.currentFrame)

      Point(x = chainedX, y = chainedY)
    }
  }
}
