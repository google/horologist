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
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.remote.creation.compose.state.RemoteColor
import androidx.compose.remote.creation.compose.state.RemoteFloat
import androidx.compose.remote.creation.compose.state.clamp
import androidx.compose.remote.creation.compose.state.lerp
import androidx.compose.remote.creation.compose.state.max
import androidx.compose.remote.creation.compose.state.pow
import androidx.compose.remote.creation.compose.state.rc
import androidx.compose.remote.creation.compose.state.rf
import androidx.compose.remote.creation.compose.state.selectIfLt
import androidx.compose.ui.graphics.Color
import com.google.android.horologist.remotecompose.lottie.LottieSettings
import com.google.android.horologist.remotecompose.lottie.format.properties.AnimatedColorProperty
import com.google.android.horologist.remotecompose.lottie.format.properties.BaseColorProperty
import com.google.android.horologist.remotecompose.lottie.format.properties.ColorPropertyKeyframe
import com.google.android.horologist.remotecompose.lottie.format.properties.StaticColorProperty
import com.google.android.horologist.remotecompose.lottie.renderer.lookupValueInBezier
import com.google.android.horologist.remotecompose.lottie.renderer.scalarLinearEasingIn
import com.google.android.horologist.remotecompose.lottie.renderer.scalarLinearEasingOut
import kotlin.math.pow

/**
 * Resolves or animates a color property at the current timeline frame.
 *
 * Follows the timeline evaluation contract specified in
 * [Color Property](https://lottie.github.io/lottie-spec/1.0.1/specs/properties/#color-property) and
 * [Color Keyframe](https://lottie.github.io/lottie-spec/1.0.1/specs/properties/#color-keyframe):
 * - Slot overrides configured in [LottieSettings.slotMap] take precedence over intrinsic property
 *   values.
 * - Constant [StaticColorProperty] evaluates directly to its underlying [RemoteColor].
 * - [AnimatedColorProperty] evaluates keyframed transitions:
 *     - Empty keyframe sequences fallback to transparent color.
 *     - Single keyframes evaluate as constant across the entire timeline.
 *     - Clamps to the first keyframe before animation starts, and holds the last keyframe after
 *       animation ends.
 *     - Respects the hold interpolation flag (`h`), holding start keyframe values until the next
 *       keyframe.
 *     - Interpolates RGBA channels using cubic Bézier easing curves between consecutive keyframes.
 *
 * @param property The static or animated color property to evaluate.
 * @param animationSettings The animation settings containing current frame and slot mappings.
 * @return Evaluated [RemoteColor] for the active frame.
 */
@SuppressLint("RestrictedApi")
internal fun animateColor(
  property: BaseColorProperty,
  animationSettings: LottieSettings,
): RemoteColor {
  val slotColor = property.slotId?.let { animationSettings.slotMap.colorSlots[it] }
  if (slotColor != null) {
    return slotColor
  }

  return when (property) {
    is StaticColorProperty -> property.value
    is AnimatedColorProperty -> {
      val keyframes = property.keyframes
      if (keyframes.isEmpty()) {
        return Color.Transparent.rc
      }
      if (keyframes.size == 1) {
        return keyframes[0].value
      }

      val constantFrame = animationSettings.currentFrame.constantValueOrNull
      if (constantFrame != null) {
        val firstKf = keyframes.first()
        val lastKf = keyframes.last()
        if (constantFrame <= firstKf.frame.constantValue) {
          return firstKf.value
        }
        if (constantFrame >= lastKf.frame.constantValue) {
          return lastKf.value
        }

        for (i in 0 until keyframes.size - 1) {
          val startKf = keyframes[i]
          val endKf = keyframes[i + 1]
          val startT = startKf.frame.constantValue
          val endT = endKf.frame.constantValue

          if (constantFrame >= startT && (constantFrame < endT || i == keyframes.size - 2)) {
            if (startKf.hold.constantValue) {
              return if (constantFrame < endT) startKf.value else endKf.value
            }
            val duration = endT - startT
            val t =
              if (duration == 0f) 0f else ((constantFrame - startT) / duration).coerceIn(0f, 1f)
            val outTangent = startKf.outTangent ?: scalarLinearEasingOut
            val inTangent = startKf.inTangent ?: scalarLinearEasingIn
            val easing =
              CubicBezierEasing(
                outTangent.x.constantValue,
                outTangent.y.constantValue,
                inTangent.x.constantValue,
                inTangent.y.constantValue,
              )
            val easedT = easing.transform(t)

            val startColor = startKf.value.constantValue
            val startR = startColor.red
            val startG = startColor.green
            val startB = startColor.blue
            val startA = startColor.alpha

            val endColor = endKf.value.constantValue
            val endR = endColor.red
            val endG = endColor.green
            val endB = endColor.blue
            val endA = endColor.alpha

            val r = gammaLerp(startR.rf, endR.rf, easedT.rf).constantValue
            val g = gammaLerp(startG.rf, endG.rf, easedT.rf).constantValue
            val b = gammaLerp(startB.rf, endB.rf, easedT.rf).constantValue
            val a = lerpFloat(startA, endA, easedT)
            return Color(
                r,
                g,
                b,
                a,
                androidx.compose.ui.graphics.colorspace.ColorSpaces.ExtendedSrgb,
              )
              .rc
          }
        }
        return lastKf.value
      }

      val animationSegments = mutableListOf<List<AnimationSegment>>()

      val firstKeyframe = keyframes[0]
      if (firstKeyframe.frame.constantValue > 0f) {
        animationSegments.add(toRgbaFloats(firstKeyframe).map { AnimationSegment(0f, it) })
      }

      for (i in 0 until keyframes.size - 1) {
        val startKeyframe = keyframes[i]
        val endKeyframe = keyframes[i + 1]
        val startT = startKeyframe.frame.constantValue
        val endT = endKeyframe.frame.constantValue
        val duration = endT - startT

        val startValues = toRgbaFloats(startKeyframe)
        val endValues = toRgbaFloats(endKeyframe)

        val segment: List<AnimationSegment> =
          if (startKeyframe.hold.constantValue) {
            startValues.map { AnimationSegment(startT, it) }
          } else {
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
            startValues.mapIndexed { index, value ->
              AnimationSegment(
                startT,
                if (index == 3) lerp(value, endValues[index], currentBezierValue)
                else gammaLerp(value, endValues[index], currentBezierValue),
              )
            }
          }

        animationSegments.add(segment)
      }

      // A final hold segment still has to switch to the last keyframe's value.
      val lastKeyframe = keyframes.last()
      animationSegments.add(
        toRgbaFloats(lastKeyframe).map { AnimationSegment(lastKeyframe.frame.constantValue, it) }
      )

      val channels =
        (0 until 4).map { index ->
          chainAnimation(animationSegments.map { it[index] }, animationSettings.currentFrame)
        }

      RemoteColor.rgb(
        red = channels[0],
        green = channels[1],
        blue = channels[2],
        alpha = channels[3],
      )
    }
  }
}

private fun toRgbaFloats(keyframe: ColorPropertyKeyframe): List<RemoteFloat> {
  val color = keyframe.value.constantValue
  return listOf(color.red.rf, color.green.rf, color.blue.rf, color.alpha.rf)
}

private fun lerpFloat(start: Float, stop: Float, fraction: Float): Float =
  start + (stop - start) * fraction

/** Interpolate RGB in linear light, matching lottie-android's sRGB transfer function. */
@SuppressLint("RestrictedApi")
private fun gammaLerp(start: RemoteFloat, end: RemoteFloat, progress: RemoteFloat): RemoteFloat {
  fun linear(value: Float): Float =
    if (value <= 0.04045f) value / 12.92f else ((value + 0.055f) / 1.055f).pow(2.4f)
  val fraction = clamp(progress, 0f.rf, 1f.rf)
  val value = lerp(linear(start.constantValue).rf, linear(end.constantValue).rf, fraction)
  return selectIfLt(
    value,
    0.0031308f.rf,
    value * 12.92f,
    pow(max(value, 0f.rf), (1f / 2.4f).rf) * 1.055f - 0.055f,
  )
}
