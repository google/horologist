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
import kotlinx.serialization.json.intOrNull
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

/**
 * A layer in a Lottie animation.
 *
 * Layer parenting provides a way for layer transforms to be applied to child layers. This allows
 * for a single set of transforms to be applied to multiple layers.
 */
@Serializable(with = LayerSerializer::class)
internal sealed class Layer {
  abstract val name: String?
  abstract val hidden: Boolean?
  abstract val type: LayerType
  abstract val index: Int?
  abstract val parent: Int?
  abstract val startFrame: Int?
  abstract val endFrame: Int?
  abstract val transform: Transform?
}

@Serializable(with = LayerTypeSerializer::class)
internal enum class LayerType(val value: Int) {
  Solid(1),
  Null(3),
  Shape(4);

  companion object {
    fun fromValueOrNull(value: Int): LayerType? {
      return values().firstOrNull { it.value == value }
    }
  }
}

/** Polymorphic serializer for [Layer] based on integer "ty" field. */
internal object LayerSerializer : JsonContentPolymorphicSerializer<Layer>(Layer::class) {
  override fun selectDeserializer(element: JsonElement): DeserializationStrategy<Layer> {
    val ty = element.jsonObject["ty"]?.jsonPrimitive?.intOrNull
    return when (ty) {
      LayerType.Solid.value -> SolidColorLayer.serializer()
      LayerType.Null.value -> NullLayer.serializer()
      LayerType.Shape.value -> ShapeLayer.serializer()
      else -> NullLayer.serializer()
    }
  }
}

/** Serializer for [LayerType] enum. */
internal object LayerTypeSerializer : KSerializer<LayerType> {
  override val descriptor: SerialDescriptor =
    PrimitiveSerialDescriptor("LayerType", PrimitiveKind.INT)

  override fun deserialize(decoder: Decoder): LayerType {
    val value = decoder.decodeInt()
    return LayerType.fromValueOrNull(value) ?: LayerType.Null
  }

  override fun serialize(encoder: Encoder, value: LayerType) {
    encoder.encodeInt(value.value)
  }
}
