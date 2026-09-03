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

package com.google.android.horologist.remotecompose.lottie.format.graphicelement

import com.google.android.horologist.remotecompose.lottie.format.graphicelement.geometry.Ellipse
import com.google.android.horologist.remotecompose.lottie.format.graphicelement.geometry.Path
import com.google.android.horologist.remotecompose.lottie.format.graphicelement.geometry.PolyStar
import com.google.android.horologist.remotecompose.lottie.format.graphicelement.geometry.Rectangle
import com.google.android.horologist.remotecompose.lottie.format.graphicelement.grouping.Group
import com.google.android.horologist.remotecompose.lottie.format.graphicelement.grouping.Transform
import com.google.android.horologist.remotecompose.lottie.format.graphicelement.styles.Fill
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
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

/**
 * A graphic element in a Lottie animation.
 *
 * Graphic elements are the building blocks of a Lottie animation. They can be shapes (which get
 * rendered to screen), styles (which control the look of shapes - e.g. the fill color), or grouping
 * mechanisms (including transforms).
 */
@Serializable(with = GraphicElementSerializer::class)
internal interface GraphicElement {
  val name: String?
  val hidden: Boolean?
  val type: ShapeType
}

@Serializable(with = ShapeTypeSerializer::class)
internal enum class ShapeType(val value: String) {
  Ellipse("el"),
  Fill("fl"),
  Group("gr"),
  Path("sh"),
  PolyStar("sr"),
  Rectangle("rc"),
  Transform("tr");

  companion object {
    fun fromValueOrNull(value: String): ShapeType? {
      return values().firstOrNull { it.value == value }
    }
  }
}

internal object GraphicElementSerializer :
  JsonContentPolymorphicSerializer<GraphicElement>(GraphicElement::class) {
  override fun selectDeserializer(element: JsonElement): DeserializationStrategy<GraphicElement> {
    val ty = element.jsonObject["ty"]?.jsonPrimitive?.contentOrNull
    return when (ty) {
      ShapeType.Path.value -> Path.serializer()
      ShapeType.Group.value -> Group.serializer()
      ShapeType.Transform.value -> Transform.serializer()
      ShapeType.Fill.value -> Fill.serializer()
      ShapeType.Rectangle.value -> Rectangle.serializer()
      ShapeType.Ellipse.value -> Ellipse.serializer()
      ShapeType.PolyStar.value -> PolyStar.serializer()
      else -> Group.serializer()
    }
  }
}

internal object ShapeTypeSerializer : KSerializer<ShapeType> {
  override val descriptor: SerialDescriptor =
    PrimitiveSerialDescriptor("ShapeType", PrimitiveKind.STRING)

  override fun deserialize(decoder: Decoder): ShapeType {
    val value = decoder.decodeString()
    return ShapeType.fromValueOrNull(value) ?: ShapeType.Group
  }

  override fun serialize(encoder: Encoder, value: ShapeType) {
    encoder.encodeString(value.value)
  }
}
