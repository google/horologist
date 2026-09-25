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

import com.google.android.horologist.remotecompose.lottie.format.graphicelement.grouping.Transform
import com.google.android.horologist.remotecompose.lottie.format.mask.Mask
import com.google.android.horologist.remotecompose.lottie.format.values.SerializableBoolean
import com.google.android.horologist.remotecompose.lottie.format.values.SerializableRemoteBoolean
import com.google.android.horologist.remotecompose.lottie.format.values.SerializableRemoteFloat
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonContentPolymorphicSerializer
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.intOrNull
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

/**
 * Base class for all Lottie animation layers conforming to
 * [Visual Layer](https://lottie.github.io/lottie-spec/1.0.1/specs/layers/#visual-layer).
 *
 * Layers are independent visual, temporal, and spatial nodes arranged in a compositing tree.
 *
 * Essential Invariants:
 * - Discriminator: Partitioned by integer [type] ([Layer
 *   Type](https://lottie.github.io/lottie-spec/1.0.1/specs/layers/#layer-types)).
 * - Parenting Hierarchy: Child layer transforms are concatenated with their parent's current
 *   transformation matrix: CTM(child) = CTM(parent) * Transform(child).
 * - Timeline Visibility Window: A layer is active on frame t when ip <= t < op.
 * - Hidden Layers: [hidden] (hd) suppresses direct rendering while retaining participation in
 *   parenting and track matte hierarchies.
 */
@Serializable(with = LayerSerializer::class)
internal sealed class Layer {
  abstract val name: String?
  abstract val hidden: SerializableBoolean
  abstract val type: LayerType
  abstract val index: Int?
  abstract val parent: Int?
  abstract val startFrame: SerializableRemoteFloat
  abstract val endFrame: SerializableRemoteFloat
  abstract val transform: Transform?
  abstract val autoOrient: SerializableRemoteBoolean
  abstract val matteMode: MatteMode
  abstract val matteParent: Int?
  abstract val masks: List<Mask>?
  open val startTime: Float? = 0f
  open val timeStretch: Float? = 1f
  open val blendMode: BlendMode? = BlendMode.Normal
  open val matteTarget: Int? = 0
  open val is3d: Int? = 0
  open val masksProperties: List<Mask>
    get() = masks.orEmpty()
}

/**
 * Canonical layer types defined in the
 * [Lottie Specification](https://lottie.github.io/lottie-spec/1.0.1/specs/layers/#layer-types).
 */
@Serializable(with = LayerTypeSerializer::class)
internal enum class LayerType(val value: Int) {
  Precomposition(0),
  Solid(1),
  Image(2),
  Null(3),
  Shape(4),
  Text(5),
  Audio(6),
  Unknown(-1);

  companion object {
    fun fromValueOrNull(value: Int): LayerType? {
      return entries.firstOrNull { it.value == value }
    }
  }
}

/**
 * Polymorphic serializer for [Layer] discriminating on the integer "ty" field per
 * [Layer Type](https://lottie.github.io/lottie-spec/1.0.1/specs/layers/#layer-types).
 *
 * Contract:
 * - Deserialization Preconditions: [element] must be a [JsonObject].
 * - Deserialization Postconditions:
 *     - Selects [SolidColorLayer.serializer] when "ty" is 1.
 *     - Selects [NullLayer.serializer] when "ty" is 3.
 *     - Selects [ShapeLayer.serializer] when "ty" is 4.
 *     - Falls back to [NullLayer.serializer] for unrecognized, missing, or unsupported layer types,
 *       preserving transform parenting chains without crashing animation decoding.
 */
internal object LayerSerializer : JsonContentPolymorphicSerializer<Layer>(Layer::class) {
  override fun selectDeserializer(element: JsonElement): DeserializationStrategy<Layer> {
    val ty = element.jsonObject["ty"]?.jsonPrimitive?.intOrNull
    return when (ty) {
      LayerType.Precomposition.value -> PrecompLayer.serializer()
      LayerType.Solid.value -> SolidColorLayer.serializer()
      LayerType.Image.value -> ImageLayer.serializer()
      LayerType.Null.value -> NullLayer.serializer()
      LayerType.Shape.value -> ShapeLayer.serializer()
      LayerType.Text.value -> TextLayer.serializer()
      else -> UnknownLayer.serializer()
    }
  }
}

/** Serializer for [LayerType] enum. */
internal object LayerTypeSerializer : KSerializer<LayerType> {
  override val descriptor: SerialDescriptor =
    PrimitiveSerialDescriptor("LayerType", PrimitiveKind.INT)

  override fun deserialize(decoder: Decoder): LayerType {
    val value = decoder.decodeInt()
    return LayerType.fromValueOrNull(value) ?: LayerType.Unknown
  }

  override fun serialize(encoder: Encoder, value: LayerType) {
    encoder.encodeInt(value.value)
  }
}
