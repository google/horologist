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
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.booleanOrNull
import kotlinx.serialization.json.buildJsonArray
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.floatOrNull
import kotlinx.serialization.json.intOrNull
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.put

/** A path defined by a set of bezier curves. */
@Serializable(with = BezierValueSerializer::class)
internal data class BezierValue(
  @SerialName("c") val closed: Boolean = false,
  @SerialName("i") val inTangents: List<List<Float>> = emptyList(),
  @SerialName("o") val outTangents: List<List<Float>> = emptyList(),
  @SerialName("v") val vertices: List<List<Float>> = emptyList(),
)

/** Serializer for [BezierValue] supporting flexible closed flag (int 0/1 or boolean true/false). */
internal object BezierValueSerializer : KSerializer<BezierValue> {
  override val descriptor: SerialDescriptor =
    buildClassSerialDescriptor("BezierValue") {
      element<Boolean>("c", isOptional = true)
      element<List<List<Float>>>("i", isOptional = true)
      element<List<List<Float>>>("o", isOptional = true)
      element<List<List<Float>>>("v", isOptional = true)
    }

  override fun deserialize(decoder: Decoder): BezierValue {
    val jsonDecoder = decoder as JsonDecoder
    val element = jsonDecoder.decodeJsonElement()
    val obj = element.jsonObject

    val closed =
      when (val cElem = obj["c"]) {
        is JsonPrimitive -> cElem.booleanOrNull ?: ((cElem.intOrNull ?: 0) != 0)
        else -> false
      }

    val inTangents = parsePointList(obj["i"])
    val outTangents = parsePointList(obj["o"])
    val vertices = parsePointList(obj["v"])

    return BezierValue(
      closed = closed,
      inTangents = inTangents,
      outTangents = outTangents,
      vertices = vertices,
    )
  }

  override fun serialize(encoder: Encoder, value: BezierValue) {
    val jsonEncoder = encoder as JsonEncoder
    jsonEncoder.encodeJsonElement(
      buildJsonObject {
        put("c", value.closed)
        put("i", buildPointArray(value.inTangents))
        put("o", buildPointArray(value.outTangents))
        put("v", buildPointArray(value.vertices))
      }
    )
  }

  private fun parsePointList(element: JsonElement?): List<List<Float>> {
    if (element !is JsonArray) return emptyList()
    return element.mapNotNull { item ->
      when (item) {
        is JsonArray -> item.mapNotNull { it.jsonPrimitive.floatOrNull }
        else -> null
      }
    }
  }

  private fun buildPointArray(points: List<List<Float>>): JsonArray = buildJsonArray {
    for (point in points) {
      add(
        buildJsonArray {
          for (coord in point) {
            add(JsonPrimitive(coord))
          }
        }
      )
    }
  }
}
