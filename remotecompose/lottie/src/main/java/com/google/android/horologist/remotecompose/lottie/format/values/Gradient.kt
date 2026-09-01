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

import androidx.compose.ui.graphics.Color
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.descriptors.element
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonDecoder
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonEncoder
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonArray
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.floatOrNull
import kotlinx.serialization.json.intOrNull
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.put

@Serializable
internal data class ColorStop(val offset: Float, val red: Float, val green: Float, val blue: Float)

@Serializable internal data class OpacityStop(val offset: Float, val alpha: Float)

internal data class ResolvedColorStop(val offset: Float, val color: Color)

@Serializable(with = GradientValueSerializer::class)
internal data class GradientValue(
  @SerialName("p") val numberOfColors: Int = 0,
  @SerialName("k") val values: List<Float> = emptyList(),
) {
  val hasTransparency: Boolean
    get() =
      if (numberOfColors > 0) {
        values.size > numberOfColors * 4
      } else {
        values.size > 0 && values.size % 4 != 0
      }

  val colorStops: List<ColorStop>
    get() {
      val stops = mutableListOf<ColorStop>()
      val colorCount = if (numberOfColors > 0) numberOfColors else values.size / 4
      for (i in 0 until colorCount) {
        val base = i * 4
        if (base + 3 < values.size) {
          stops.add(
            ColorStop(
              offset = values[base],
              red = normalizeColorComponent(values[base + 1]),
              green = normalizeColorComponent(values[base + 2]),
              blue = normalizeColorComponent(values[base + 3]),
            )
          )
        }
      }
      return stops
    }

  val opacityStops: List<OpacityStop>
    get() {
      val stops = mutableListOf<OpacityStop>()
      val colorCount = if (numberOfColors > 0) numberOfColors else values.size / 4
      val opacityOffset = colorCount * 4
      var i = opacityOffset
      while (i + 1 < values.size) {
        stops.add(OpacityStop(offset = values[i], alpha = normalizeColorComponent(values[i + 1])))
        i += 2
      }
      return stops
    }

  fun resolveStops(): List<ResolvedColorStop> {
    val cStops = colorStops
    val oStops = opacityStops
    if (cStops.isEmpty()) return emptyList()
    if (oStops.isEmpty()) {
      return cStops.map {
        ResolvedColorStop(
          offset = it.offset,
          color = Color(red = it.red, green = it.green, blue = it.blue, alpha = 1f),
        )
      }
    }

    val allOffsets = (cStops.map { it.offset } + oStops.map { it.offset }).distinct().sorted()
    return allOffsets.map { offset ->
      val (r, g, b) = interpolateRgbAt(cStops, offset)
      val alpha = interpolateAlphaAt(oStops, offset)
      ResolvedColorStop(offset = offset, color = Color(red = r, green = g, blue = b, alpha = alpha))
    }
  }

  private fun normalizeColorComponent(v: Float): Float =
    if (v > 1f) (v / 255f).coerceIn(0f, 1f) else v.coerceIn(0f, 1f)

  private fun interpolateRgbAt(stops: List<ColorStop>, offset: Float): Triple<Float, Float, Float> {
    if (stops.size == 1 || offset <= stops.first().offset) {
      val first = stops.first()
      return Triple(first.red, first.green, first.blue)
    }
    if (offset >= stops.last().offset) {
      val last = stops.last()
      return Triple(last.red, last.green, last.blue)
    }
    for (i in 0 until stops.size - 1) {
      val s1 = stops[i]
      val s2 = stops[i + 1]
      if (offset >= s1.offset && offset <= s2.offset) {
        val range = s2.offset - s1.offset
        val factor = if (range == 0f) 0f else (offset - s1.offset) / range
        val r = s1.red + (s2.red - s1.red) * factor
        val g = s1.green + (s2.green - s1.green) * factor
        val b = s1.blue + (s2.blue - s1.blue) * factor
        return Triple(r, g, b)
      }
    }
    val last = stops.last()
    return Triple(last.red, last.green, last.blue)
  }

  private fun interpolateAlphaAt(stops: List<OpacityStop>, offset: Float): Float {
    if (stops.isEmpty()) return 1f
    if (stops.size == 1 || offset <= stops.first().offset) return stops.first().alpha
    if (offset >= stops.last().offset) return stops.last().alpha
    for (i in 0 until stops.size - 1) {
      val s1 = stops[i]
      val s2 = stops[i + 1]
      if (offset >= s1.offset && offset <= s2.offset) {
        val range = s2.offset - s1.offset
        val factor = if (range == 0f) 0f else (offset - s1.offset) / range
        return s1.alpha + (s2.alpha - s1.alpha) * factor
      }
    }
    return stops.last().alpha
  }
}

internal fun parseGradientValueElement(element: JsonElement?): GradientValue {
  return when (element) {
    null -> GradientValue()
    is JsonArray -> {
      val floatList = element.mapNotNull { it.jsonPrimitive.floatOrNull }
      val count = if (floatList.size >= 4 && floatList.size % 4 == 0) floatList.size / 4 else 0
      GradientValue(numberOfColors = count, values = floatList)
    }
    is JsonObject -> {
      val p = element["p"]?.jsonPrimitive?.intOrNull ?: 0
      val kElem = element["k"]
      val values =
        when (kElem) {
          is JsonArray -> kElem.mapNotNull { it.jsonPrimitive.floatOrNull }
          is JsonObject -> parseGradientValueElement(kElem).values
          else -> emptyList()
        }
      val count =
        if (p > 0) p else if (values.size >= 4 && values.size % 4 == 0) values.size / 4 else 0
      GradientValue(numberOfColors = count, values = values)
    }
    else -> GradientValue()
  }
}

internal object GradientValueSerializer : KSerializer<GradientValue> {
  override val descriptor: SerialDescriptor =
    buildClassSerialDescriptor("GradientValue") {
      element<Int>("p", isOptional = true)
      element<List<Float>>("k", isOptional = true)
    }

  override fun deserialize(decoder: Decoder): GradientValue {
    val jsonDecoder = decoder as JsonDecoder
    val element = jsonDecoder.decodeJsonElement()
    return parseGradientValueElement(element)
  }

  override fun serialize(encoder: Encoder, value: GradientValue) {
    val jsonEncoder = encoder as JsonEncoder
    if (value.numberOfColors > 0) {
      jsonEncoder.encodeJsonElement(
        buildJsonObject {
          put("p", value.numberOfColors)
          put("k", buildJsonArray { value.values.forEach { add(JsonPrimitive(it)) } })
        }
      )
    } else {
      jsonEncoder.encodeJsonElement(
        buildJsonArray { value.values.forEach { add(JsonPrimitive(it)) } }
      )
    }
  }
}
