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
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerializationException
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.descriptors.element
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonDecoder
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonEncoder
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonArray
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.floatOrNull
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.put

internal data class ColorStop(val offset: Float, val color: RemoteColor)

internal data class TransparencyStop(val offset: Float, val alpha: RemoteFloat)

/**
 * A gradient color value defining color stops and optional opacity stops.
 *
 * In Lottie, gradient values are represented as a flat array of numbers:
 * - Color stops ($N_c$): the first $4 \times N_c$ elements, where each stop consists of `[offset,
 *   red, green, blue]`.
 * - Opacity stops ($N_o$): the remaining elements, where $N_o = (\text{length} - 4 \times N_c) / 2$
 *   and each stop consists of `[offset, alpha]`.
 *
 * All offsets and color/opacity components are normalized floats in the range `[0.0, 1.0]`.
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

internal class GradientValueSerializer(val colorStopCount: Int) : KSerializer<GradientValue> {
  override val descriptor: SerialDescriptor =
    buildClassSerialDescriptor("GradientValue") { element<List<Float>>("k") }

  override fun deserialize(decoder: Decoder): GradientValue {
    val jsonDecoder = decoder as JsonDecoder
    val element = jsonDecoder.decodeJsonElement()
    return parseGradientValueElement(element)
  }

  /**
   * Parses a [JsonElement] into a [GradientValue], expecting a direct flat float array (`[offset,
   * r, g, b, ...]`) per Lottie
   * [specifications](https://lottie.github.io/lottie-spec/1.0.1/specs/values/#gradient).
   */
  private fun parseGradientValueElement(element: JsonElement?): GradientValue {
    val values = element?.jsonArray?.toFloatList() ?: emptyList()
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

  private fun JsonArray.toFloatList(): List<Float> = map { elem ->
    elem.jsonPrimitive.floatOrNull
      ?: throw SerializationException("Gradient coordinate value must be a number: $elem")
  }

  override fun serialize(encoder: Encoder, value: GradientValue) {
    val jsonEncoder = encoder as JsonEncoder
    val count = value.colorStops.size
    jsonEncoder.encodeJsonElement(
      buildJsonObject {
        put("p", count)
        put(
          "k",
          buildJsonArray {
            value.colorStops.forEach { stop ->
              add(JsonPrimitive(stop.offset))
              add(JsonPrimitive(stop.color.red.constantValue))
              add(JsonPrimitive(stop.color.green.constantValue))
              add(JsonPrimitive(stop.color.blue.constantValue))
            }
            value.transparencyStops.forEach { stop ->
              add(JsonPrimitive(stop.offset))
              add(JsonPrimitive(stop.alpha.constantValue))
            }
          },
        )
      }
    )
  }
}
