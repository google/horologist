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
import androidx.compose.remote.creation.compose.state.lerp
import androidx.compose.remote.creation.compose.state.rf
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
    // Static constant position: directly wrap the [x, y] coordinates into RemoteFloats.
    is StaticPositionProperty -> {
      Point(x = position.value.getOrElse(0) { 0f }.rf, y = position.value.getOrElse(1) { 0f }.rf)
    }
    // Keyframed animated position: interpolate [x, y] across keyframes using Bézier easing curves.
    is AnimatedPositionProperty -> {
      if (position.keyframes.isEmpty()) {
        return Point(0f.rf, 0f.rf)
      }
      // Single keyframe: hold static position at that single value.
      if (position.keyframes.size == 1) {
        return Point(
          x = position.keyframes[0].value.getOrElse(0) { 0f }.rf,
          y = position.keyframes[0].value.getOrElse(1) { 0f }.rf,
        )
      }

      val animationSegments = mutableListOf<List<AnimationSegment>>()

      // If the first keyframe starts after frame 0, prepend an initial static segment
      // holding the first keyframe's value from frame 0 until the first keyframe.
      val firstKeyframe = position.keyframes[0]
      if (firstKeyframe.frame != 0f) {
        animationSegments.add(firstKeyframe.value.map { AnimationSegment(0f, it.rf) })
      }

      // Build interpolation segments between adjacent keyframe pairs.
      for (i in 0 until position.keyframes.size - 1) {
        val startKeyframe = position.keyframes[i]
        val endKeyframe = position.keyframes[i + 1]
        val duration = endKeyframe.frame - startKeyframe.frame
        val frameInAnimation = animationSettings.currentFrame - startKeyframe.frame

        // Control point tangents for the cubic Bézier curve, defaulting to linear easing if
        // omitted.
        val outTangent = startKeyframe.outTangent ?: scalarLinearEasingOut
        val inTangent = startKeyframe.inTangent ?: scalarLinearEasingIn

        // Evaluate the cubic Bézier curve to obtain the normalized interpolation factor [0.0, 1.0].
        val currentBezierValue =
          lookupValueInBezier(
            outTangent.x,
            outTangent.y,
            inTangent.x,
            inTangent.y,
            duration,
            frameInAnimation,
          )

        // Linearly interpolate each coordinate (x, y) between the start and end keyframe values.
        val segment =
          startKeyframe.value.mapIndexed { index, value ->
            AnimationSegment(
              startKeyframe.frame,
              lerp(value.rf, endKeyframe.value[index].rf, currentBezierValue),
            )
          }

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
