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
import kotlinx.serialization.json.jsonPrimitive

/**
 * A 2D point [x, y] in the Lottie AST.
 *
 * See [Lottie Vector Value](https://lottie.github.io/lottie-spec/1.0.1/specs/values/#vector).
 *
 * In the Lottie specification, a Vector representing a 2D position or point consists of an array of
 * numbers:
 * - [x]: The horizontal coordinate (first array component).
 * - [y]: The vertical coordinate (second array component).
 *
 * In Lottie JSON, points and position coordinates are serialized as vector arrays (e.g. `[x, y]` or
 * `[x, y, z]`). Deserialization requires at least 2 coordinates and retains strictly `[x, y]`,
 * discarding any extra dimensions (e.g. z-axis) per Lottie's 2D canvas model.
 */
@Serializable(with = PointSerializer::class) internal data class Point(val x: Float, val y: Float)

internal object PointSerializer : KSerializer<Point> {
  override val descriptor: SerialDescriptor =
    buildClassSerialDescriptor("Point") {
      element<Float>("x")
      element<Float>("y")
    }

  override fun deserialize(decoder: Decoder): Point {
    val jsonDecoder = decoder as JsonDecoder
    val element = jsonDecoder.decodeJsonElement()
    val array =
      element as? JsonArray
        ?: throw SerializationException("Point must be a JSON array of coordinates")
    if (array.size < 2) {
      throw SerializationException(
        "Point must have at least 2 coordinates [x, y], but had ${array.size}"
      )
    }
    val x =
      array[0].jsonPrimitive.floatOrNull
        ?: throw SerializationException("X coordinate is not a valid float: ${array[0]}")
    val y =
      array[1].jsonPrimitive.floatOrNull
        ?: throw SerializationException("Y coordinate is not a valid float: ${array[1]}")
    return Point(x, y)
  }

  override fun serialize(encoder: Encoder, value: Point) {
    val jsonEncoder = encoder as JsonEncoder
    jsonEncoder.encodeJsonElement(
      buildJsonArray {
        add(JsonPrimitive(value.x))
        add(JsonPrimitive(value.y))
      }
    )
  }
}
