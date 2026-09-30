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

package com.google.android.horologist.remotecompose.lottie.format.graphicelement.modifiers

import androidx.compose.remote.creation.compose.state.rb
import androidx.compose.remote.creation.compose.state.rf
import com.google.android.horologist.remotecompose.lottie.format.graphicelement.ShapeType
import com.google.android.horologist.remotecompose.lottie.format.properties.BaseScalarProperty
import com.google.android.horologist.remotecompose.lottie.format.properties.StaticScalarProperty
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
import kotlinx.serialization.json.floatOrNull
import kotlinx.serialization.json.intOrNull
import kotlinx.serialization.json.jsonPrimitive

/** Repeater modifier */
@Serializable
internal data class Repeater(
  @SerialName("nm") override val name: String? = "",
  @SerialName("hd") override val hidden: SerializableBoolean? = false.rb,
  @SerialName("ty") override val type: ShapeType = ShapeType.Repeater,
  @SerialName("ix") override val index: Int? = null,
  @SerialName("mn") override val matchName: String? = null,
  @SerialName("cix") override val propertyIndex: Int? = null,
  @SerialName("c") val copies: BaseScalarProperty = StaticScalarProperty(value = 1f.rf),
  @SerialName("o") val offset: BaseScalarProperty = StaticScalarProperty(value = 0f.rf),
  @SerialName("m") val composite: CompositeMode = CompositeMode.Above,
  @SerialName("tr") val transform: RepeaterTransform? = null,
) : ShapeModifier

@Serializable(with = CompositeModeSerializer::class)
internal enum class CompositeMode(val value: Int) {
  Above(1),
  Below(2);

  companion object {
    fun fromValueOrNull(value: Int): CompositeMode? = values().firstOrNull { it.value == value }
  }
}

internal object CompositeModeSerializer : KSerializer<CompositeMode> {
  override val descriptor: SerialDescriptor =
    PrimitiveSerialDescriptor("CompositeMode", PrimitiveKind.INT)

  override fun deserialize(decoder: Decoder): CompositeMode {
    return try {
      val jsonDecoder = decoder as? JsonDecoder
      if (jsonDecoder != null) {
        val element = jsonDecoder.decodeJsonElement()
        val intVal =
          element.jsonPrimitive.intOrNull ?: element.jsonPrimitive.floatOrNull?.toInt() ?: 1
        CompositeMode.fromValueOrNull(intVal) ?: CompositeMode.Above
      } else {
        val value = decoder.decodeInt()
        CompositeMode.fromValueOrNull(value) ?: CompositeMode.Above
      }
    } catch (e: Exception) {
      CompositeMode.Above
    }
  }

  override fun serialize(encoder: Encoder, value: CompositeMode) {
    encoder.encodeInt(value.value)
  }
}
