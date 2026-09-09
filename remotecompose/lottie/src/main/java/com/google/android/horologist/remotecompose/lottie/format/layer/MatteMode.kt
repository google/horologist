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

/**
 * Track matte mode for layer compositing conforming to
 * [Matte Mode](https://lottie.github.io/lottie-spec/1.0.1/specs/constants/#matte-mode).
 *
 * Defines how a layer is masked or composited against the layer immediately above it in the stack:
 * - `0` ([Normal]): No matte clipping applied.
 * - `1` ([Alpha]): Uses the alpha channel of the matte layer.
 * - `2` ([InvertedAlpha]): Uses the inverse of the alpha channel.
 * - `3` ([Luma]): Uses the luminance/brightness of the matte layer.
 * - `4` ([InvertedLuma]): Uses the inverse of the luminance.
 */
@Serializable(with = MatteModeSerializer::class)
internal enum class MatteMode(val value: Int) {
  Normal(0),
  Alpha(1),
  InvertedAlpha(2),
  Luma(3),
  InvertedLuma(4);

  companion object {
    fun fromValueOrNull(value: Int): MatteMode? = entries.firstOrNull { it.value == value }
  }
}

/** Serializer for [MatteMode] discriminating on the integer matte mode value. */
internal object MatteModeSerializer : KSerializer<MatteMode> {
  override val descriptor: SerialDescriptor =
    PrimitiveSerialDescriptor("MatteMode", PrimitiveKind.INT)

  override fun deserialize(decoder: Decoder): MatteMode {
    return try {
      val jsonDecoder = decoder as? JsonDecoder
      if (jsonDecoder != null) {
        val element = jsonDecoder.decodeJsonElement()
        val intVal =
          element.jsonPrimitive.intOrNull ?: element.jsonPrimitive.floatOrNull?.toInt() ?: 0
        MatteMode.fromValueOrNull(intVal) ?: MatteMode.Normal
      } else {
        val value = decoder.decodeInt()
        MatteMode.fromValueOrNull(value) ?: MatteMode.Normal
      }
    } catch (_: Exception) {
      MatteMode.Normal
    }
  }

  override fun serialize(encoder: Encoder, value: MatteMode) {
    encoder.encodeInt(value.value)
  }
}
