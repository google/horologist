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
 * Typealias for standard JSON boolean values, binding [RemoteBoolean] to [BooleanRemoteSerializer]
 * for concise kotlinx.serialization in Lottie AST models.
 *
 * In the Lottie specification, certain boolean properties (such as layer `hd` / hidden) are
 * represented as standard JSON boolean primitives (`true`/`false`).
 */
internal typealias SerializableBoolean =
  @Serializable(with = BooleanRemoteSerializer::class) RemoteBoolean

/**
 * Streaming serializer for standard JSON boolean primitives (`true`/`false`), binding
 * [RemoteBoolean] for concise kotlinx.serialization in Lottie AST models.
 *
 * Contract:
 * - Deserialization Preconditions: The incoming JSON token must decode to a valid boolean primitive
 *   via [Decoder.decodeBoolean].
 * - Deserialization Postconditions: Returns a [RemoteBoolean] instance wrapping the decoded boolean
 *   value (`false.rb` or `true.rb`).
 * - Exceptions: Throws [SerializationException] if the token is not a valid boolean primitive.
 * - Serialization: Encodes [RemoteBoolean.constantValue] as a primitive boolean token via
 *   [Encoder.encodeBoolean].
 */
internal object BooleanRemoteSerializer : KSerializer<RemoteBoolean> {
  override val descriptor: SerialDescriptor =
    PrimitiveSerialDescriptor("BooleanRemote", PrimitiveKind.BOOLEAN)

  override fun deserialize(decoder: Decoder): RemoteBoolean = decoder.decodeBoolean().rb

  override fun serialize(encoder: Encoder, value: RemoteBoolean) {
    encoder.encodeBoolean(value.constantValue)
  }
}
