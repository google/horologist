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
import com.google.android.horologist.remotecompose.lottie.format.properties.BaseGradientProperty
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
import kotlinx.serialization.json.JsonDecoder
import kotlinx.serialization.json.floatOrNull
import kotlinx.serialization.json.intOrNull
import kotlinx.serialization.json.jsonPrimitive

/**
 * Type of a gradient, conforming to
 * [Lottie Gradient Type](https://lottie.github.io/lottie-spec/latest/specs/constants/#gradient-type).
 */
@Serializable(with = GradientTypeSerializer::class)
internal enum class GradientType(val value: Int) {
  Linear(1),
  Radial(2);

  companion object {
    fun fromValueOrNull(value: Int): GradientType? = entries.firstOrNull { it.value == value }
  }
}

/**
 * Serializer for [GradientType] supporting integer and float primitives with fallback to
 * [GradientType.Linear].
 */
internal object GradientTypeSerializer : KSerializer<GradientType> {
  override val descriptor: SerialDescriptor =
    PrimitiveSerialDescriptor("GradientType", PrimitiveKind.INT)

  override fun deserialize(decoder: Decoder): GradientType {
    return try {
      val jsonDecoder = decoder as? JsonDecoder
      if (jsonDecoder != null) {
        val element = jsonDecoder.decodeJsonElement()
        val intVal =
          element.jsonPrimitive.intOrNull ?: element.jsonPrimitive.floatOrNull?.toInt() ?: 1
        GradientType.fromValueOrNull(intVal) ?: GradientType.Linear
      } else {
        val value = decoder.decodeInt()
        GradientType.fromValueOrNull(value) ?: GradientType.Linear
      }
    } catch (e: Exception) {
      GradientType.Linear
    }
  }

  override fun serialize(encoder: Encoder, value: GradientType) {
    encoder.encodeInt(value.value)
  }
}

/**
 * Shape Element representing a gradient fill color, conforming to
 * [Lottie Gradient Fill](https://lottie.github.io/lottie-spec/latest/specs/shapes/#gradient-fill)
 * and [Base Gradient](https://lottie.github.io/lottie-spec/latest/specs/shapes/#base-gradient).
 *
 * Schema Specification:
 * - Required Fields: "ty" (const "gf"), "o" (Opacity), "g" (Colors), "s" (Start point), "e" (End
 *   point), "t" (Gradient type).
 * - Optional Fields without Schema Defaults: "nm" (String), "hd" (Boolean), "r" (FillRule), "h"
 *   (Highlight length), "a" (Highlight angle).
 *
 * @property name Human-readable element name.
 * @property hidden When true, suppresses rendering of this fill.
 * @property type Shape type discriminator, strictly [ShapeType.GradientFill].
 * @property opacity Animatable fill opacity on [0.0, 100.0]. Required in schema.
 * @property colors Gradient stops and color definitions. Required in schema.
 * @property startPoint Starting point coordinate for the gradient. Required in schema.
 * @property endPoint Ending point coordinate for the gradient. Required in schema.
 * @property gradientType Type of gradient (linear or radial). Required in schema.
 * @property fillRule Path winding rule for multi-path intersections.
 * @property highlightLength Radial highlight length as a percentage between start and end points.
 * @property highlightAngle Radial highlight angle in clockwise degrees.
 */
@Serializable
internal data class GradientFill(
  @SerialName("nm") override val name: String? = null,
  @SerialName("hd") override val hidden: SerializableBoolean? = null,
  @SerialName("ty") override val type: ShapeType = ShapeType.GradientFill,
  @SerialName("o") override val opacity: BaseScalarProperty,
  @SerialName("g") val colors: BaseGradientProperty,
  @SerialName("s") val startPoint: BasePositionProperty,
  @SerialName("e") val endPoint: BasePositionProperty,
  @SerialName("t") val gradientType: GradientType,
  @SerialName("r") val fillRule: FillRule? = null,
  @SerialName("h") val highlightLength: BaseScalarProperty? = null,
  @SerialName("a") val highlightAngle: BaseScalarProperty? = null,
) : ShapeStyle
