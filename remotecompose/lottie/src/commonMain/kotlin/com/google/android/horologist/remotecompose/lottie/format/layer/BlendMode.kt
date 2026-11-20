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

package com.google.android.horologist.remotecompose.lottie.format.layer

import kotlinx.serialization.KSerializer
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

/** Blend mode for layer compositing. */
@Serializable(with = BlendModeSerializer::class)
internal enum class BlendMode(val value: Int) {
  Normal(0),
  Multiply(1),
  Screen(2),
  Overlay(3),
  Darken(4),
  Lighten(5),
  ColorDodge(6),
  ColorBurn(7),
  HardLight(8),
  SoftLight(9),
  Difference(10),
  Exclusion(11),
  Hue(12),
  Saturation(13),
  Color(14),
  Luminosity(15),
  Add(16),
  HardMix(17);

  companion object {
    fun fromValueOrNull(value: Int): BlendMode? = entries.firstOrNull { it.value == value }
  }
}

internal object BlendModeSerializer : KSerializer<BlendMode> {
  override val descriptor: SerialDescriptor =
    PrimitiveSerialDescriptor("BlendMode", PrimitiveKind.INT)

  override fun deserialize(decoder: Decoder): BlendMode {
    return try {
      val jsonDecoder = decoder as? JsonDecoder
      if (jsonDecoder != null) {
        val element = jsonDecoder.decodeJsonElement()
        val intVal =
          element.jsonPrimitive.intOrNull ?: element.jsonPrimitive.floatOrNull?.toInt() ?: 0
        BlendMode.fromValueOrNull(intVal) ?: BlendMode.Normal
      } else {
        val value = decoder.decodeInt()
        BlendMode.fromValueOrNull(value) ?: BlendMode.Normal
      }
    } catch (e: Exception) {
      BlendMode.Normal
    }
  }

  override fun serialize(encoder: Encoder, value: BlendMode) {
    encoder.encodeInt(value.value)
  }
}
