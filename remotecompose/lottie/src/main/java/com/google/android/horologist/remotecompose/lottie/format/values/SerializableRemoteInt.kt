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

import androidx.compose.remote.creation.compose.state.RemoteInt
import androidx.compose.remote.creation.compose.state.ri
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

/**
 * Typealias for [RemoteInt] bound to [RemoteIntSerializer] for concise kotlinx.serialization usage
 * across Lottie AST property models.
 *
 * In the Lottie specification, integer values are encoded as standard JSON numbers used for layer
 * dimensions (e.g. solid layer width and height) and indices.
 */
internal typealias SerializableRemoteInt =
  @Serializable(with = RemoteIntSerializer::class) RemoteInt

/**
 * Streaming serializer for [RemoteInt] providing zero-AST allocation overhead when decoding numbers
 * from JSON token streams.
 *
 * Contract:
 * - Deserialization Preconditions: The incoming JSON token must decode to a valid 32-bit int
 *   primitive via [Decoder.decodeInt].
 * - Deserialization Postconditions: Returns a [RemoteInt] instance wrapping the decoded int value.
 * - Exceptions: Throws [SerializationException] if the token is not a valid integer numeric
 *   primitive.
 * - Serialization: Encodes [RemoteInt.constantValue] as a primitive int token via
 *   [Encoder.encodeInt].
 */
internal object RemoteIntSerializer : KSerializer<RemoteInt> {
  override val descriptor: SerialDescriptor =
    PrimitiveSerialDescriptor("RemoteInt", PrimitiveKind.INT)

  override fun deserialize(decoder: Decoder): RemoteInt = decoder.decodeInt().ri

  override fun serialize(encoder: Encoder, value: RemoteInt) =
    encoder.encodeInt(value.constantValue)
}
