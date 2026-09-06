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

import androidx.compose.remote.creation.compose.state.RemoteFloat
import androidx.compose.remote.creation.compose.state.rf
import kotlinx.serialization.KSerializer
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
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.floatOrNull
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.put

/**
 * Easing handle coordinates [x, y] conforming to
 * [Keyframe Easing](https://lottie.github.io/lottie-spec/1.0.1/specs/properties/#easing-handle).
 *
 * Represents cubic Bézier temporal easing handles (`i` and `o`) applied to keyframe intervals.
 *
 * [Point] cannot be used instead because:
 * - Serialization wire format: Serialized as a JSON object (`{"x": ..., "y": ...}`) with optional
 *   array-wrapped components, whereas [Point] serializes as a JSON array (`[x, y]`).
 * - Domain and bounds semantics: [x] represents normalized time progress constrained to `[0.0 ..
 *   1.0]`. Unlike x values, y values are not clamped to `[0 .. 1]`. Supernormal y values allow the
 *   interpolated value to overshoot (extrapolate) beyond the specified keyframe values range (for
 *   example, for spring, bounce, or anticipation effects). In contrast, [Point] represents spatial
 *   Cartesian coordinates on a 2D canvas without temporal progress constraints.
 *
 * Defaults:
 * - [x] defaults to `0f.rf`
 * - [y] defaults to `0f.rf`
 */
@Serializable(with = ScalarKeyframeEasingSerializer::class)
internal data class KeyframeEasing(val x: RemoteFloat = 0f.rf, val y: RemoteFloat = 0f.rf)

/**
 * Serializer for [KeyframeEasing] handling numbers or single-element arrays.
 *
 * In Lottie JSON schemas, easing coordinates in `i` and `o` objects may be formatted either as
 * primitive numbers (e.g. `{"x": 0.33, "y": 1.0}`) or as arrays (e.g. `{"x": [0.33], "y": [1.0]}`).
 */
internal object ScalarKeyframeEasingSerializer : KSerializer<KeyframeEasing> {
  override val descriptor: SerialDescriptor =
    buildClassSerialDescriptor("ScalarKeyframeEasing") {
      element<Float>("x", isOptional = true)
      element<Float>("y", isOptional = true)
    }

  override fun deserialize(decoder: Decoder): KeyframeEasing {
    val jsonDecoder = decoder as? JsonDecoder ?: return KeyframeEasing()
    val element = jsonDecoder.decodeJsonElement()
    val obj = element as? JsonObject ?: return KeyframeEasing()
    val x = parseTangentValue(obj["x"])
    val y = parseTangentValue(obj["y"])
    return KeyframeEasing(x, y)
  }

  private fun parseTangentValue(element: JsonElement?): RemoteFloat {
    val value =
      when (element) {
        is JsonPrimitive -> element.floatOrNull ?: 0f
        is JsonArray -> element.firstOrNull()?.jsonPrimitive?.floatOrNull ?: 0f
        else -> 0f
      }
    return value.rf
  }

  override fun serialize(encoder: Encoder, value: KeyframeEasing) {
    val jsonEncoder = encoder as JsonEncoder
    jsonEncoder.encodeJsonElement(
      buildJsonObject {
        put("x", value.x.constantValue)
        put("y", value.y.constantValue)
      }
    )
  }
}
