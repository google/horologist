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
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.JsonTransformingSerializer
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.floatOrNull
import kotlinx.serialization.json.put

/** ZigZag modifier */
@Serializable
internal data class ZigZag(
  @SerialName("nm") override val name: String? = "",
  @SerialName("hd") override val hidden: SerializableBoolean? = false.rb,
  @SerialName("ty") override val type: ShapeType = ShapeType.ZigZag,
  @SerialName("ix") override val index: Int? = null,
  @SerialName("mn") override val matchName: String? = null,
  @SerialName("cix") override val propertyIndex: Int? = null,
  @SerialName("s") val size: BaseScalarProperty = StaticScalarProperty(value = 0f.rf),
  @SerialName("r") val ridgesPerSegment: BaseScalarProperty = StaticScalarProperty(value = 0f.rf),
  @SerialName("pt")
  @Serializable(with = ZigZagPointTypeSerializer::class)
  val pointType: BaseScalarProperty = StaticScalarProperty(value = 1f.rf),
) : ShapeModifier

/** Accept legacy numeric point types as well as the reference player's scalar property form. */
internal object ZigZagPointTypeSerializer :
  JsonTransformingSerializer<BaseScalarProperty>(BaseScalarProperty.serializer()) {
  override fun transformDeserialize(element: JsonElement): JsonElement {
    if (element !is JsonPrimitive) return element
    val value =
      element.floatOrNull
        ?: throw SerializationException("ZigZag point type must be a number or scalar property")
    if (element.isString || !value.isFinite()) {
      throw SerializationException("ZigZag point type must be a finite number")
    }
    return buildJsonObject {
      put("a", 0)
      put("k", value)
    }
  }
}
