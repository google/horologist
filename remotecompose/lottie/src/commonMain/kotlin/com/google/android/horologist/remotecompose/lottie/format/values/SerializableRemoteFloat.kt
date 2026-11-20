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

import androidx.compose.remote.creation.compose.state.RemoteFloat
import androidx.compose.remote.creation.compose.state.rf
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

/**
 * Typealias for [RemoteFloat] bound to [RemoteFloatSerializer] for concise kotlinx.serialization
 * usage across Lottie AST property models.
 *
 * In the Lottie specification, floating point values are encoded as standard JSON numbers used for
 * frame indices, scale ratios, sizes, opacities, and Bézier tangents.
 */
internal typealias SerializableRemoteFloat =
  @Serializable(with = RemoteFloatSerializer::class) RemoteFloat

/**
 * Streaming serializer for [RemoteFloat] providing zero-AST allocation overhead when decoding
 * numbers from JSON token streams.
 *
 * Contract:
 * - Deserialization Preconditions: The incoming JSON token must decode to a valid 32-bit float
 *   primitive via [Decoder.decodeFloat].
 * - Deserialization Postconditions: Returns a [RemoteFloat] instance wrapping the decoded float
 *   value.
 * - Exceptions: Throws [SerializationException] if the token is not a valid numeric primitive.
 * - Serialization: Encodes [RemoteFloat.constantValue] as a primitive float token via
 *   [Encoder.encodeFloat].
 */
internal object RemoteFloatSerializer : KSerializer<RemoteFloat> {
  override val descriptor: SerialDescriptor =
    PrimitiveSerialDescriptor("RemoteFloat", PrimitiveKind.FLOAT)

  override fun deserialize(decoder: Decoder): RemoteFloat = decoder.decodeFloat().rf

  override fun serialize(encoder: Encoder, value: RemoteFloat) =
    encoder.encodeFloat(value.constantValue)
}
