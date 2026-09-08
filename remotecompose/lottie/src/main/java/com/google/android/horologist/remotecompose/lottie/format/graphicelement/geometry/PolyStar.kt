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

package com.google.android.horologist.remotecompose.lottie.format.graphicelement.geometry

import com.google.android.horologist.remotecompose.lottie.format.graphicelement.ShapeType
import com.google.android.horologist.remotecompose.lottie.format.properties.BasePositionProperty
import com.google.android.horologist.remotecompose.lottie.format.properties.BaseScalarProperty
import com.google.android.horologist.remotecompose.lottie.format.values.SerializableBoolean
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

/**
 * Parametric star or regular polygon shape conforming to
 * [PolyStar Shape](https://lottie.github.io/lottie-spec/latest/specs/shapes/#polystar).
 *
 * Schema Specification:
 * - Required Fields: `"ty"` (`"sr"`), `"p"` (position), `"or"` (outer radius), `"os"` (outer
 *   roundness), `"r"` (rotation), `"pt"` (points).
 * - Optional Fields with Schema Default:
 *     - `"sy"` (star type, default: `1` -> [PolyStarType.Star])
 * - Optional Fields without Schema Default:
 *     - `"ir"` (inner radius, default: `null`, conditionally required when [starType] is
 *       [PolyStarType.Star])
 *     - `"is"` (inner roundness, default: `null`, conditionally required when [starType] is
 *       [PolyStarType.Star])
 *     - `"nm"` (name, default: `null`)
 *     - `"hd"` (hidden flag, default: `null`)
 *     - `"d"` (shape direction, default: `null`)
 *
 * Invariants:
 * - [starType]: Selects star vs polygon topology (`"sy"`). Defaults to [PolyStarType.Star] per
 *   schema default `1`.
 * - [points], [position], [rotation], [outerRadius], [outerRoundness]: Required; no schema default.
 * - [innerRadius], [innerRoundness]: Optional in schema; evaluated when [starType] is
 *   [PolyStarType.Star].
 */
@Serializable
internal data class PolyStar(
  @SerialName("nm") override val name: String? = null,
  @SerialName("hd") override val hidden: SerializableBoolean? = null,
  @SerialName("ty") override val type: ShapeType = ShapeType.PolyStar,
  @SerialName("d") override val direction: Int? = null,
  @SerialName("sy") val starType: PolyStarType = PolyStarType.Star,
  @SerialName("pt") val points: BaseScalarProperty,
  @SerialName("p") val position: BasePositionProperty,
  @SerialName("r") val rotation: BaseScalarProperty,
  @SerialName("or") val outerRadius: BaseScalarProperty,
  @SerialName("os") val outerRoundness: BaseScalarProperty,
  @SerialName("ir") val innerRadius: BaseScalarProperty? = null,
  @SerialName("is") val innerRoundness: BaseScalarProperty? = null,
) : GeometryShape

/**
 * Geometric topology for [PolyStar] conforming to
 * [Star Type](https://lottie.github.io/lottie-spec/1.0.1/specs/constants/#star-type).
 *
 * Values:
 * - [Star] (`1`): Multi-pointed star topology.
 * - [Polygon] (`2`): Regular convex polygon topology.
 */
@Serializable(with = PolyStarTypeSerializer::class)
internal enum class PolyStarType(val value: Int) {
  Star(1),
  Polygon(2);

  companion object {
    fun fromValueOrNull(value: Int): PolyStarType? {
      return entries.firstOrNull { it.value == value }
    }
  }
}

/**
 * Serializer for [PolyStarType] decoding integer enum tokens.
 *
 * Contract:
 * - Deserialization: Decodes integer or numeric string; returns [PolyStarType.Star] for `1`,
 *   [PolyStarType.Polygon] for `2`. Defaults to [PolyStarType.Star] for unrecognized tokens.
 * - Serialization: Encodes the integer primitive [PolyStarType.value].
 */
internal object PolyStarTypeSerializer : KSerializer<PolyStarType> {
  override val descriptor: SerialDescriptor =
    PrimitiveSerialDescriptor("PolyStarType", PrimitiveKind.INT)

  override fun deserialize(decoder: Decoder): PolyStarType {
    val value = decoder.decodeInt()
    return PolyStarType.fromValueOrNull(value) ?: PolyStarType.Star
  }

  override fun serialize(encoder: Encoder, value: PolyStarType) {
    encoder.encodeInt(value.value)
  }
}
