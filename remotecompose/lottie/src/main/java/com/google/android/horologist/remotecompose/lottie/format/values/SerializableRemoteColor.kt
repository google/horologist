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

import androidx.compose.remote.creation.compose.state.RemoteColor
import androidx.compose.remote.creation.compose.state.rc
import androidx.compose.ui.graphics.Color
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonDecoder
import kotlinx.serialization.json.JsonEncoder
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonArray
import kotlinx.serialization.json.floatOrNull

/**
 * Typealias for [RemoteColor] bound to [RemoteColorSerializer] for concise kotlinx.serialization
 * usage across Lottie AST property models.
 *
 * Conforms to [Color Value](https://lottie.github.io/lottie-spec/1.0.1/specs/values/#color).
 *
 * In the Lottie specification, colors are encoded as numerical arrays containing 3 (RGB) or 4
 * (RGBA) components in range [0.0, 1.0].
 */
internal typealias SerializableRemoteColor =
  @Serializable(with = RemoteColorSerializer::class) RemoteColor

/**
 * Serializer for [RemoteColor] conforming to
 * [Color Value](https://lottie.github.io/lottie-spec/1.0.1/specs/values/#color).
 *
 * Decodes 3- or 4-element JSON arrays into [RemoteColor].
 *
 * Contract:
 * - Preconditions: Incoming JSON element must be a [JsonArray] with length in 3..4.
 * - Component Invariants: Each element must be a non-string numeric [JsonPrimitive].
 * - Normalization: Legacy values exceeding 1.0 are scaled by 1/255. All components are clamped to
 *   [0.0, 1.0].
 * - Default Alpha: When 3 components [r, g, b] are provided, alpha defaults to 1.0.
 * - Postconditions: Emits a [RemoteColor] backed by [Color].
 * - Exceptions: Throws [SerializationException] if element is not a JSON array, length is invalid,
 *   or components are non-numeric.
 * - Serialization: Encodes RGBA components as a 4-element JSON array [red, green, blue, alpha].
 */
internal object RemoteColorSerializer : KSerializer<RemoteColor> {
  override val descriptor: SerialDescriptor = buildClassSerialDescriptor("RemoteColor")

  override fun deserialize(decoder: Decoder): RemoteColor {
    val jsonDecoder =
      decoder as? JsonDecoder ?: throw SerializationException("Decoder must be JsonDecoder")
    val element = jsonDecoder.decodeJsonElement()
    val array =
      element as? JsonArray
        ?: throw SerializationException("Color components must be a JSON array, but was $element")
    if (array.size !in 3..4) {
      throw SerializationException(
        "Color components array must have 3 or 4 elements, but had ${array.size}"
      )
    }
    val components = array.map { elem ->
      val prim =
        elem as? JsonPrimitive
          ?: throw SerializationException("Color component must be a number, but was $elem")
      if (prim.isString) {
        throw SerializationException(
          "Color component must be a number, but was string '${prim.content}'"
        )
      }
      val f =
        prim.floatOrNull
          ?: throw SerializationException("Color component must be a valid float, but was '$prim'")
      if (f > 1f) (f / 255f).coerceIn(0f, 1f) else f.coerceIn(0f, 1f)
    }

    val r = components[0]
    val g = components[1]
    val b = components[2]
    val a = if (components.size == 4) components[3] else 1f

    return Color(r, g, b, a).rc
  }

  override fun serialize(encoder: Encoder, value: RemoteColor) {
    val jsonEncoder =
      encoder as? JsonEncoder ?: throw SerializationException("Encoder must be JsonEncoder")
    val color = value.constantValue
    val jsonArray = buildJsonArray {
      add(JsonPrimitive(color.red))
      add(JsonPrimitive(color.green))
      add(JsonPrimitive(color.blue))
      add(JsonPrimitive(color.alpha))
    }
    jsonEncoder.encodeJsonElement(jsonArray)
  }
}
