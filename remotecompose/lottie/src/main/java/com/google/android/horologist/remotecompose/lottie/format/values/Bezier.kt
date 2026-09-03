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

import androidx.compose.remote.creation.compose.state.RemoteBoolean
import androidx.compose.remote.creation.compose.state.rb
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.descriptors.element
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonDecoder
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonEncoder
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.booleanOrNull
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.encodeToJsonElement
import kotlinx.serialization.json.intOrNull
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.put

/**
 * A cubic polybezier path value.
 *
 * See [Lottie Bezier Shape](https://lottie.github.io/lottie-spec/1.0.1/specs/values/#bezier).
 *
 * In the Lottie specification, a Bezier value represents a cubic polybezier consisting of:
 * - [closed]: Whether the bezier path is closed (schema `c`, default `false`).
 * - [inTangents]: Control points along the in tangents relative to the corresponding vertices
 *   (schema `i`).
 * - [outTangents]: Control points along the out tangents relative to the corresponding vertices
 *   (schema `o`).
 * - [vertices]: Vertex points on the path (schema `v`).
 */
@Serializable(with = BezierValueSerializer::class)
internal data class BezierValue(
  @SerialName("c") val closed: RemoteBoolean = false.rb,
  @SerialName("i") val inTangents: List<Point>,
  @SerialName("o") val outTangents: List<Point>,
  @SerialName("v") val vertices: List<Point>,
)

internal object BezierValueSerializer : KSerializer<BezierValue> {
  override val descriptor: SerialDescriptor =
    buildClassSerialDescriptor("BezierValue") {
      element<Boolean>("c", isOptional = true)
      element<List<Point>>("i")
      element<List<Point>>("o")
      element<List<Point>>("v")
    }

  override fun deserialize(decoder: Decoder): BezierValue {
    val jsonDecoder = decoder as JsonDecoder
    val json = jsonDecoder.json
    val obj = jsonDecoder.decodeJsonElement().jsonObject

    return BezierValue(
      closed = parseClosed(obj["c"]).rb,
      inTangents = getPoints(obj, "i", json),
      outTangents = getPoints(obj, "o", json),
      vertices = getPoints(obj, "v", json),
    )
  }

  /**
   * Parses the closed flag (`c`) from a [JsonElement].
   *
   * Official Lottie specification represents booleans as integers (0/1). But
   * [Lottie 1.0.1 JSON Schema](https://lottie.github.io/lottie-spec/1.0.1/lottie.schema.json)
   * defines "c" as boolean default: false), and JSON schemas only accept true/false. We accept both
   * representations.
   *
   * Throws [SerializationException] if the element is neither a boolean nor an integer.
   */
  private fun parseClosed(element: JsonElement?): Boolean {
    if (element == null || element is JsonNull) {
      return false
    }
    val primitive =
      element as? JsonPrimitive
        ?: throw SerializationException(
          "Property 'c' must be a boolean or an integer, but was: $element"
        )
    if (!primitive.isString) {
      primitive.booleanOrNull?.let {
        return it
      }
      primitive.intOrNull?.let {
        return it != 0
      }
    }
    throw SerializationException("Property 'c' must be a boolean or an integer, but was: $element")
  }

  private fun getPoints(obj: JsonObject, propertyName: String, json: Json): List<Point> {
    val element =
      obj[propertyName] ?: throw SerializationException("Missing required property '$propertyName'")
    return json.decodeFromJsonElement(element)
  }

  override fun serialize(encoder: Encoder, value: BezierValue) {
    val jsonEncoder = encoder as JsonEncoder
    val json = jsonEncoder.json
    jsonEncoder.encodeJsonElement(
      buildJsonObject {
        put("c", value.closed.constantValue)
        put("i", json.encodeToJsonElement(value.inTangents))
        put("o", json.encodeToJsonElement(value.outTangents))
        put("v", json.encodeToJsonElement(value.vertices))
      }
    )
  }
}
