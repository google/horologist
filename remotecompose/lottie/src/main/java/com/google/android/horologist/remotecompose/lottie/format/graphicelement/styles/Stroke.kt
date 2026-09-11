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
 * Shape Element representing a solid stroke outline, conforming to
 * [Lottie Stroke](https://lottie.github.io/lottie-spec/latest/specs/shapes/#stroke) and
 * [Base Stroke](https://lottie.github.io/lottie-spec/latest/specs/shapes/#base-stroke).
 *
 * Essential Invariants:
 * - Scoping: Outlines all preceding shape curves within the current group scope.
 * - Stacking Order: Evaluated in bottom-to-top order when combined with sibling fills or strokes.
 * - Discriminator: type is strictly [ShapeType.Stroke] ("st").
 *
 * Schema Specification:
 * - Required Fields: "ty" (const "st"), "c" (Color), "w" (Stroke width), "o" (Opacity).
 * - Optional Fields with Schema Defaults:
 *     - "lc": Line cap (schema default: 2 -> [LineCap.Round]).
 *     - "lj": Line join (schema default: 2 -> [LineJoin.Round]).
 *     - "ml": Numeric miter limit (schema default: 0 -> 0f).
 * - Optional Fields without Schema Defaults:
 *     - "nm": Human-readable name (default: null).
 *     - "hd": Hidden boolean flag (default: null).
 *     - "ml2": Animatable miter limit (default: null).
 *     - "d": Dash pattern array (default: null).
 *
 * @property name Human-readable element name.
 * @property hidden When true, suppresses rendering of this stroke.
 * @property type Shape type discriminator, strictly [ShapeType.Stroke].
 * @property opacity Animatable stroke opacity on [0.0, 100.0]. Required in schema.
 * @property color Animatable solid RGBA stroke color. Required in schema.
 * @property strokeWidth Animatable stroke width. Required in schema.
 * @property lineCap Style at the end of stroked lines. Defaults to [LineCap.Round] per schema.
 * @property lineJoin Style at sharp corners of stroked lines. Defaults to [LineJoin.Round] per
 *   schema.
 * @property miterLimit Maximum miter limit before beveling. Defaults to 0f per schema.
 * @property miterLimitAnimatable Animatable scalar alternative to miterLimit.
 * @property dashes Optional list of dash segments, gaps, and offsets.
 */
@Serializable
internal data class Stroke(
  @SerialName("nm") override val name: String? = null,
  @SerialName("hd") override val hidden: SerializableBoolean? = null,
  @SerialName("ty") override val type: ShapeType = ShapeType.Stroke,
  @SerialName("o") override val opacity: BaseScalarProperty,
  @SerialName("c") val color: BaseColorProperty,
  @SerialName("w") val strokeWidth: BaseScalarProperty,
  @SerialName("lc") val lineCap: LineCap = LineCap.Round,
  @SerialName("lj") val lineJoin: LineJoin = LineJoin.Round,
  @SerialName("ml") val miterLimit: Float = 0f,
  @SerialName("ml2") val miterLimitAnimatable: BaseScalarProperty? = null,
  @SerialName("d") val dashes: List<StrokeDash>? = null,
) : ShapeStyle

/**
 * Style at the end of a stroked line, conforming to
 * [Lottie Line Cap](https://lottie.github.io/lottie-spec/latest/specs/constants/#line-cap).
 */
@Serializable(with = LineCapSerializer::class)
internal enum class LineCap(val value: Int) {
  Butt(1),
  Round(2),
  Square(3);

  companion object {
    fun fromValueOrNull(value: Int): LineCap? = entries.firstOrNull { it.value == value }
  }
}

/**
 * Serializer for [LineCap] supporting integer and float primitives with fallback to
 * [LineCap.Round].
 */
internal object LineCapSerializer : KSerializer<LineCap> {
  override val descriptor: SerialDescriptor =
    PrimitiveSerialDescriptor("LineCap", PrimitiveKind.INT)

  override fun deserialize(decoder: Decoder): LineCap {
    return try {
      val jsonDecoder = decoder as? JsonDecoder
      if (jsonDecoder != null) {
        val element = jsonDecoder.decodeJsonElement()
        val intVal =
          element.jsonPrimitive.intOrNull ?: element.jsonPrimitive.floatOrNull?.toInt() ?: 2
        LineCap.fromValueOrNull(intVal) ?: LineCap.Round
      } else {
        val value = decoder.decodeInt()
        LineCap.fromValueOrNull(value) ?: LineCap.Round
      }
    } catch (e: Exception) {
      LineCap.Round
    }
  }

  override fun serialize(encoder: Encoder, value: LineCap) {
    encoder.encodeInt(value.value)
  }
}

/**
 * Style at a sharp corner of a stroked line, conforming to
 * [Lottie Line Join](https://lottie.github.io/lottie-spec/latest/specs/constants/#line-join).
 */
@Serializable(with = LineJoinSerializer::class)
internal enum class LineJoin(val value: Int) {
  Miter(1),
  Round(2),
  Bevel(3);

  companion object {
    fun fromValueOrNull(value: Int): LineJoin? = entries.firstOrNull { it.value == value }
  }
}

/**
 * Serializer for [LineJoin] supporting integer and float primitives with fallback to
 * [LineJoin.Round].
 */
internal object LineJoinSerializer : KSerializer<LineJoin> {
  override val descriptor: SerialDescriptor =
    PrimitiveSerialDescriptor("LineJoin", PrimitiveKind.INT)

  override fun deserialize(decoder: Decoder): LineJoin {
    return try {
      val jsonDecoder = decoder as? JsonDecoder
      if (jsonDecoder != null) {
        val element = jsonDecoder.decodeJsonElement()
        val intVal =
          element.jsonPrimitive.intOrNull ?: element.jsonPrimitive.floatOrNull?.toInt() ?: 2
        LineJoin.fromValueOrNull(intVal) ?: LineJoin.Round
      } else {
        val value = decoder.decodeInt()
        LineJoin.fromValueOrNull(value) ?: LineJoin.Round
      }
    } catch (e: Exception) {
      LineJoin.Round
    }
  }

  override fun serialize(encoder: Encoder, value: LineJoin) {
    encoder.encodeInt(value.value)
  }
}

/**
 * An item describing the dash pattern in a stroked path, conforming to
 * [Lottie Stroke Dash](https://lottie.github.io/lottie-spec/latest/specs/shapes/#stroke-dash).
 *
 * @property name Human-readable name inherited from Visual Object.
 * @property type Type of dash item. Defaults to [StrokeDashType.Dash] per schema.
 * @property length Length of the dash or gap segment.
 */
@Serializable
internal data class StrokeDash(
  @SerialName("nm") val name: String? = null,
  @SerialName("n") val type: StrokeDashType = StrokeDashType.Dash,
  @SerialName("v") val length: BaseScalarProperty? = null,
)

/**
 * Type of a dash item in a stroked line, conforming to
 * [Lottie Stroke Dash Type](https://lottie.github.io/lottie-spec/latest/specs/constants/#stroke-dash-type).
 */
@Serializable
internal enum class StrokeDashType(val value: String) {
  @SerialName("d") Dash("d"),
  @SerialName("g") Gap("g"),
  @SerialName("o") Offset("o");

  companion object {
    fun fromValueOrNull(value: String): StrokeDashType? = entries.firstOrNull { it.value == value }
  }
}
