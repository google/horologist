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

package com.google.android.horologist.remotecompose.lottie.format.graphicelement.styles

import com.google.android.horologist.remotecompose.lottie.format.graphicelement.ShapeType
import com.google.android.horologist.remotecompose.lottie.format.properties.BaseColorProperty
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
import kotlinx.serialization.json.JsonDecoder
import kotlinx.serialization.json.floatOrNull
import kotlinx.serialization.json.intOrNull
import kotlinx.serialization.json.jsonPrimitive

/**
 * Shape Element representing a solid color fill, conforming to
 * [Lottie Fill](https://lottie.github.io/lottie-spec/latest/specs/shapes/#fill).
 *
 * Essential Invariants:
 * - Scoping: Colors the interior of all preceding shape curves within the current group scope.
 * - Stacking Order: Evaluated in bottom-to-top order when combined with sibling fills or strokes.
 * - Discriminator: type is strictly [ShapeType.Fill] ("fl").
 *
 * Schema Specification:
 * - Required Fields: "ty" (const "fl"), "c" (Color), "o" (Opacity).
 * - Optional Fields without Defaults: "nm" (String), "hd" (Boolean), "r" (FillRule).
 *
 * @property name Human-readable element name.
 * @property hidden When true, suppresses rendering of this fill.
 * @property type Shape type discriminator, strictly [ShapeType.Fill].
 * @property opacity Animatable fill opacity on [0.0, 100.0]. Required in schema.
 * @property color Animatable solid RGBA color property. Required in schema.
 * @property fillRule Path winding rule used to resolve multi-path interiors and self-intersections.
 */
@Serializable
internal data class Fill(
  @SerialName("nm") override val name: String? = null,
  @SerialName("hd") override val hidden: SerializableBoolean? = null,
  @SerialName("ty") override val type: ShapeType = ShapeType.Fill,
  @SerialName("o") override val opacity: BaseScalarProperty,
  @SerialName("c") val color: BaseColorProperty,
  @SerialName("r") val fillRule: FillRule? = null,
) : ShapeStyle

/**
 * Rule used to handle multiple shapes or intersecting paths rendered with the same fill object,
 * conforming to
 * [Lottie Fill Rule](https://lottie.github.io/lottie-spec/latest/specs/constants/#fill-rule).
 */
@Serializable(with = FillRuleSerializer::class)
internal enum class FillRule(val value: Int) {
  NonZero(1),
  EvenOdd(2);

  companion object {
    fun fromValueOrNull(value: Int): FillRule? = entries.firstOrNull { it.value == value }
  }
}

/**
 * Serializer for [FillRule] supporting integer and float representations with fallback to
 * [FillRule.NonZero].
 *
 * Contract:
 * - Deserialization Preconditions: Incoming token is a numerical primitive or JSON element.
 * - Deserialization Postconditions: Returns matching [FillRule], defaulting to [FillRule.NonZero]
 *   (1) on unknown tokens.
 * - Exceptions: Does not throw; malformed tokens fall back to [FillRule.NonZero].
 * - Serialization: Encodes the primitive integer value via [Encoder.encodeInt].
 */
internal object FillRuleSerializer : KSerializer<FillRule> {
  override val descriptor: SerialDescriptor =
    PrimitiveSerialDescriptor("FillRule", PrimitiveKind.INT)

  override fun deserialize(decoder: Decoder): FillRule {
    return try {
      val jsonDecoder = decoder as? JsonDecoder
      if (jsonDecoder != null) {
        val element = jsonDecoder.decodeJsonElement()
        val intVal =
          element.jsonPrimitive.intOrNull ?: element.jsonPrimitive.floatOrNull?.toInt() ?: 1
        FillRule.fromValueOrNull(intVal) ?: FillRule.NonZero
      } else {
        val value = decoder.decodeInt()
        FillRule.fromValueOrNull(value) ?: FillRule.NonZero
      }
    } catch (e: Exception) {
      FillRule.NonZero
    }
  }

  override fun serialize(encoder: Encoder, value: FillRule) {
    encoder.encodeInt(value.value)
  }
}
