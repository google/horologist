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

package com.google.android.horologist.remotecompose.lottie.format.mask

import androidx.compose.remote.creation.compose.state.rb
import com.google.android.horologist.remotecompose.lottie.format.properties.BaseBezierProperty
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
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonPrimitive

/**
 * A layer mask in a Lottie composition conforming to
 * [Mask](https://lottie.github.io/lottie-spec/1.0.1/specs/layers/#visual-layer).
 *
 * Masks define clipping paths and boolean compositing visibility operations applied to a layer.
 *
 * Essential Invariants:
 * - [mode]: Determines the boolean operation combining this mask with others ([MaskMode]).
 * - [path]: Animatable Bézier curve shape of the mask outline (`pt`).
 * - [opacity]: Animatable scalar transparency factor (0-100%) (`o`).
 * - [inverted]: Inverts the mask coverage region (`inv`).
 * - [expand]: Animatable scalar expansion or contraction of the mask boundary (`x`).
 */
@Serializable
internal data class Mask(
  @SerialName("nm") val name: String? = null,
  @SerialName("mode") val mode: MaskMode = MaskMode.Add,
  @SerialName("pt") val path: BaseBezierProperty? = null,
  @SerialName("o") val opacity: BaseScalarProperty? = null,
  @SerialName("inv") val inverted: SerializableBoolean = false.rb,
  @SerialName("x") val expand: BaseScalarProperty? = null,
)

/**
 * Mask mode indicating how the mask path combines with other masks conforming to
 * [Mask Mode](https://lottie.github.io/lottie-spec/1.0.1/specs/constants/#mask-mode).
 */
@Serializable(with = MaskModeSerializer::class)
internal enum class MaskMode(val value: String) {
  Add("a"),
  Subtract("s"),
  Intersect("i"),
  Lighten("l"),
  Darken("d"),
  Difference("f"),
  None("n");

  companion object {
    fun fromValueOrNull(value: String): MaskMode? = entries.firstOrNull {
      it.value.equals(value, ignoreCase = true) || it.name.equals(value, ignoreCase = true)
    }
  }
}

/** Serializer for [MaskMode] mapping string codes to [MaskMode] instances. */
internal object MaskModeSerializer : KSerializer<MaskMode> {
  override val descriptor: SerialDescriptor =
    PrimitiveSerialDescriptor("MaskMode", PrimitiveKind.STRING)

  override fun deserialize(decoder: Decoder): MaskMode {
    return try {
      val jsonDecoder = decoder as? JsonDecoder
      val value =
        if (jsonDecoder != null) {
          jsonDecoder.decodeJsonElement().jsonPrimitive.contentOrNull ?: "a"
        } else {
          decoder.decodeString()
        }
      MaskMode.fromValueOrNull(value) ?: MaskMode.Add
    } catch (_: Exception) {
      MaskMode.Add
    }
  }

  override fun serialize(encoder: Encoder, value: MaskMode) {
    encoder.encodeString(value.value)
  }
}
