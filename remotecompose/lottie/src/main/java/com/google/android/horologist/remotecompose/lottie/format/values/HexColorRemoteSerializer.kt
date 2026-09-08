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

import androidx.compose.remote.creation.compose.state.RemoteColor
import androidx.compose.remote.creation.compose.state.rc
import androidx.compose.ui.graphics.Color
import kotlin.math.roundToInt
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

/**
 * Typealias for Lottie's
 * [Hex Color](https://lottie.github.io/lottie-spec/1.0.1/specs/values/#hex-color), binding
 * [RemoteColor] to [HexColorRemoteSerializer] for concise kotlinx.serialization in Lottie AST
 * models.
 *
 * In the Lottie specification, hex colors are encoded as JSON string primitives with a leading `#`
 * prefix (such as `#ffffff`), used in solid layers (`sc`).
 */
internal typealias SerializableHexColor =
  @Serializable(with = HexColorRemoteSerializer::class) RemoteColor

/**
 * Streaming serializer for Lottie's
 * [Hex Color](https://lottie.github.io/lottie-spec/1.0.1/specs/values/#hex-color) values.
 *
 * Contract:
 * - Deserialization Preconditions: Incoming token must decode to a string matching 6-digit
 *   (`#RRGGBB`), 8-digit (`#AARRGGBB`), or 3-digit (`#RGB`) hexadecimal format.
 * - Deserialization Postconditions: Returns a [RemoteColor] wrapping an immutable [Color] with
 *   channels normalized to [0.0, 1.0].
 * - Exceptions: Throws [SerializationException] if the token is not a string, is of invalid length,
 *   or contains non-hexadecimal characters.
 * - Serialization: Encodes [RemoteColor.constantValue] as an opaque `#rrggbb` 6-digit hex string if
 *   alpha is 1.0, or an `#aarrggbb` 8-digit hex string if alpha is fractional.
 */
internal object HexColorRemoteSerializer : KSerializer<RemoteColor> {
  override val descriptor: SerialDescriptor =
    PrimitiveSerialDescriptor("HexColorRemote", PrimitiveKind.STRING)

  override fun deserialize(decoder: Decoder): RemoteColor {
    val hexString = decoder.decodeString()
    return parseHexColor(hexString).rc
  }

  override fun serialize(encoder: Encoder, value: RemoteColor) {
    val color = value.constantValue
    val r = (color.red * 255f).roundToInt().coerceIn(0, 255)
    val g = (color.green * 255f).roundToInt().coerceIn(0, 255)
    val b = (color.blue * 255f).roundToInt().coerceIn(0, 255)
    val a = (color.alpha * 255f).roundToInt().coerceIn(0, 255)
    val hex =
      if (a == 255) {
        String.format("#%02x%02x%02x", r, g, b)
      } else {
        String.format("#%02x%02x%02x%02x", a, r, g, b)
      }
    encoder.encodeString(hex)
  }

  private fun parseHexColor(hexString: String): Color {
    val cleanHex = hexString.removePrefix("#").trim()
    return try {
      when (cleanHex.length) {
        6 -> {
          val r = cleanHex.substring(0, 2).toInt(16) / 255f
          val g = cleanHex.substring(2, 4).toInt(16) / 255f
          val b = cleanHex.substring(4, 6).toInt(16) / 255f
          Color(r, g, b, 1f)
        }
        8 -> {
          val a = cleanHex.substring(0, 2).toInt(16) / 255f
          val r = cleanHex.substring(2, 4).toInt(16) / 255f
          val g = cleanHex.substring(4, 6).toInt(16) / 255f
          val b = cleanHex.substring(6, 8).toInt(16) / 255f
          Color(r, g, b, a)
        }
        3 -> {
          val r = cleanHex.substring(0, 1).repeat(2).toInt(16) / 255f
          val g = cleanHex.substring(1, 2).repeat(2).toInt(16) / 255f
          val b = cleanHex.substring(2, 3).repeat(2).toInt(16) / 255f
          Color(r, g, b, 1f)
        }
        else -> throw SerializationException("Invalid hex color format: '$hexString'")
      }
    } catch (e: NumberFormatException) {
      throw SerializationException("Invalid hex color format: '$hexString'", e)
    }
  }
}
