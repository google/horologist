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

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.descriptors.element
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonDecoder
import kotlinx.serialization.json.JsonEncoder
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonArray
import kotlinx.serialization.json.floatOrNull
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonPrimitive

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
@Serializable(with = RawGradientValueSerializer::class)
internal data class RawGradientValue(val values: List<Float>)

internal object RawGradientValueSerializer : KSerializer<RawGradientValue> {
  override val descriptor: SerialDescriptor =
    buildClassSerialDescriptor("RawGradientValue") { element<List<Float>>("k") }

  override fun deserialize(decoder: Decoder): RawGradientValue {
    val jsonDecoder = decoder as JsonDecoder
    val element = jsonDecoder.decodeJsonElement()
    val values = element.jsonArray.toFloatList()
    return RawGradientValue(values)
  }

  override fun serialize(encoder: Encoder, value: RawGradientValue) {
    val jsonEncoder = encoder as JsonEncoder
    jsonEncoder.encodeJsonElement(
      buildJsonArray { value.values.forEach { add(JsonPrimitive(it)) } }
    )
  }

  private fun JsonArray.toFloatList(): List<Float> = map { elem ->
    elem.jsonPrimitive.floatOrNull
      ?: throw SerializationException("Gradient coordinate value must be a number: $elem")
  }
}
