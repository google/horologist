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
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

/**
 * Typealias for Lottie's
 * [Integer Boolean](https://lottie.github.io/lottie-spec/dev/specs/values/#int-boolean), binding
 * [RemoteBoolean] to [IntBooleanRemoteSerializer] for concise kotlinx.serialization in Lottie AST
 * models.
 *
 * In the Lottie specification, boolean values are represented as integer primitives where:
 * - `0` denotes `false`
 * - `1` denotes `true`
 *
 * Strict validation is enforced during deserialization: true booleans (`true`/`false`), floating
 * point numbers, or integers other than `0` and `1` are rejected as schema violations.
 */
internal typealias SerializableRemoteBoolean =
  @Serializable(with = IntBooleanRemoteSerializer::class) RemoteBoolean

/**
 * Streaming serializer for Lottie's
 * [Integer Boolean](https://lottie.github.io/lottie-spec/dev/specs/values/#int-boolean) primitive.
 *
 * Contract:
 * - Deserialization Preconditions: The incoming token must be an integer primitive equal to either
 *   `0` or `1`.
 * - Deserialization Postconditions: Returns `false.rb` for `0` and `true.rb` for `1`.
 * - Exceptions: Throws [SerializationException] if the token is not an integer or is outside the
 *   valid set `{0, 1}`.
 * - Serialization: Encodes `1` if [RemoteBoolean.constantValue] is `true`, otherwise `0`.
 */
internal object IntBooleanRemoteSerializer : KSerializer<RemoteBoolean> {
  override val descriptor: SerialDescriptor =
    PrimitiveSerialDescriptor("IntBooleanRemote", PrimitiveKind.INT)

  override fun deserialize(decoder: Decoder): RemoteBoolean {
    return when (val v = decoder.decodeInt()) {
      0 -> false.rb
      1 -> true.rb
      else -> throw SerializationException("Expected 0 or 1 for int-boolean, but got $v")
    }
  }

  override fun serialize(encoder: Encoder, value: RemoteBoolean) {
    encoder.encodeInt(if (value.constantValue) 1 else 0)
  }
}
