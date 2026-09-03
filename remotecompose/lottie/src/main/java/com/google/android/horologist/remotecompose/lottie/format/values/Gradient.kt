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

package com.google.android.horologist.remotecompose.lottie.format.values

import android.annotation.SuppressLint
import androidx.compose.remote.creation.compose.state.RemoteColor
import androidx.compose.remote.creation.compose.state.RemoteFloat
import androidx.compose.remote.creation.compose.state.lerp
import androidx.compose.remote.creation.compose.state.rc
import androidx.compose.remote.creation.compose.state.rf
import androidx.compose.ui.graphics.Color
import androidx.core.math.MathUtils.clamp
import kotlinx.serialization.SerializationException

internal data class ColorStop(val offset: Float, val color: RemoteColor)

internal data class TransparencyStop(val offset: Float, val alpha: RemoteFloat)

/**
 * A gradient color value defining color stops and optional opacity stops.
 *
 * While the [RawGradientValue] class focuses on the serialization, this class is used for *logical*
 * representation of the gradient color value.
 *
 * This class is intended to be constructed from the [RawGradientValue] class using the
 * [RawGradientValue.toGradient] method. The direct deserialization from JSON is not possible
 * because the parameter `"p"` is contained higher in the JSON and is not accessible from the
 * current one.
 *
 * The main value of this class is the [getColorForPosition] method that gives a [RemoteColor] based
 * on the `position`.
 *
 * See [Lottie Gradient Value](https://lottie.github.io/lottie-spec/1.0.1/specs/values/#gradient)
 * (`#/$defs/values/gradient`) and
 * [Lottie Gradient Property](https://lottie.github.io/lottie-spec/1.0.1/specs/properties/#gradient-property)
 * (`#/$defs/properties/gradient-property`).
 */
@SuppressLint("RestrictedApi")
internal data class GradientValue(
  val colorStops: List<ColorStop>,
  val transparencyStops: List<TransparencyStop>,
) {

  /**
   * Samples the combined RGBA [RemoteColor] at the given normalized [position] in `[0.0, 1.0]`.
   *
   * If the [position] is more than 1.0 or less than 0.0, the resulted value would be returned for
   * 1.0 and 0.0 respectively.
   */
  fun getColorForPosition(position: Float): RemoteColor {
    if (colorStops.isEmpty()) return Color.Transparent.rc

    val clampedPos = clamp(position, 0f, 1f)
    val rgbColor = getRgbForPosition(clampedPos)
    val opacity = getOpacityForPosition(clampedPos)
    return rgbColor.copy(alpha = opacity)
  }

  /**
   * Linearly interpolates the RGB color from the color stops.
   *
   * Adds dummy boundary points at 0.0 and 1.0 (if not already present) to guarantee full coverage,
   * then finds the containing interval and interpolates using [lerp].
   */
  private fun getRgbForPosition(pos: Float): RemoteColor {
    if (colorStops.size == 1) return colorStops.first().color

    val paddedStops = buildList {
      if (colorStops.first().offset > 0f) {
        add(ColorStop(0f, colorStops.first().color))
      }
      addAll(colorStops)
      if (colorStops.last().offset < 1f) {
        add(ColorStop(1f, colorStops.last().color))
      }
    }

    val startIndex = paddedStops.indexOfLast { it.offset <= pos }
    val start = paddedStops.getOrElse(startIndex) { paddedStops.first() }
    val end = paddedStops.getOrElse(startIndex + 1) { paddedStops.last() }
    return interpolateColorSegment(pos, start, end)
  }

  /**
   * Linearly interpolates the alpha opacity from the opacity stops.
   *
   * Adds dummy boundary points at 0.0 and 1.0 (if not already present) to guarantee full coverage,
   * then finds the containing interval and interpolates using [lerp].
   */
  private fun getOpacityForPosition(pos: Float): RemoteFloat {
    if (transparencyStops.isEmpty()) return 1f.rf
    if (transparencyStops.size == 1) return transparencyStops.first().alpha

    val paddedStops = buildList {
      if (transparencyStops.first().offset > 0f) {
        add(TransparencyStop(0f, transparencyStops.first().alpha))
      }
      addAll(transparencyStops)
      if (transparencyStops.last().offset < 1f) {
        add(TransparencyStop(1f, transparencyStops.last().alpha))
      }
    }

    val startIndex = paddedStops.indexOfLast { it.offset <= pos }
    val start = paddedStops.getOrElse(startIndex) { paddedStops.first() }
    val end = paddedStops.getOrElse(startIndex + 1) { paddedStops.last() }
    return interpolateOpacitySegment(pos, start, end)
  }

  private fun interpolateColorSegment(pos: Float, start: ColorStop, end: ColorStop): RemoteColor {
    val factor = getInterpolationFactor(pos, start.offset, end.offset)
    return RemoteColor.rgb(
      red = lerp(start.color.red, end.color.red, factor),
      green = lerp(start.color.green, end.color.green, factor),
      blue = lerp(start.color.blue, end.color.blue, factor),
    )
  }

  private fun interpolateOpacitySegment(
    pos: Float,
    start: TransparencyStop,
    end: TransparencyStop,
  ): RemoteFloat {
    val factor = getInterpolationFactor(pos, start.offset, end.offset)
    return lerp(start.alpha, end.alpha, factor)
  }

  private fun getInterpolationFactor(
    pos: Float,
    startOffset: Float,
    endOffset: Float,
  ): RemoteFloat {
    val range = endOffset - startOffset
    return if (range == 0f) {
      0f.rf
    } else {
      clamp((pos - startOffset) / range, 0f, 1f).rf
    }
  }
}

/**
 * Converts a list of [RawGradientValue] into a list of [GradientValue].
 *
 * @param colorStopCount stop count from JSON `"p"`.
 */
internal fun List<RawGradientValue>.toGradientList(colorStopCount: Int): List<GradientValue> =
  this.map { it.toGradient(colorStopCount) }

/**
 * Converts a [RawGradientValue] into a [GradientValue].
 *
 * @param colorStopCount stop count from JSON `"p"`.
 */
internal fun RawGradientValue.toGradient(colorStopCount: Int): GradientValue {
  validateGradient(colorStopCount = colorStopCount, values = values)

  val colorStops =
    List(colorStopCount) { i ->
      val base = i * 4
      val offset = values[base]
      val r = normalizeColorComponent(values[base + 1])
      val g = normalizeColorComponent(values[base + 2])
      val b = normalizeColorComponent(values[base + 3])
      ColorStop(offset, Color(red = r, green = g, blue = b).rc)
    }

  val opacityBase = colorStopCount * 4
  val opacityCount = (values.size - opacityBase) / 2

  val transparencyStops =
    List(opacityCount) { j ->
      val base = opacityBase + j * 2
      val offset = values[base]
      val alpha = normalizeColorComponent(values[base + 1]).rf
      TransparencyStop(offset, alpha)
    }

  return GradientValue(colorStops, transparencyStops)
}

/**
 * Validates the structural invariants of a Lottie gradient.
 *
 * @param colorStopCount Number of color stops ($N_c$).
 * @param values The flat list of [Float] values containing color stops followed by opacity stops.
 */
private fun validateGradient(colorStopCount: Int, values: List<Float>) {
  if (colorStopCount < 0) {
    throw SerializationException("colorStopCount ('p') cannot be negative: $colorStopCount")
  }
  if (values.size < colorStopCount * 4) {
    throw SerializationException(
      "Gradient values array length (${values.size}) must be at least 4 * colorStopCount (${colorStopCount * 4})"
    )
  }
  if ((values.size - colorStopCount * 4) % 2 != 0) {
    throw SerializationException(
      "Gradient opacity stops length (${values.size - colorStopCount * 4}) must be a multiple of 2"
    )
  }
}

private fun normalizeColorComponent(v: Float): Float =
  if (v > 1f) (v / 255f).coerceIn(0f, 1f) else v.coerceIn(0f, 1f)
