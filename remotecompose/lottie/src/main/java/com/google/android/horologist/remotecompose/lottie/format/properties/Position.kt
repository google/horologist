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

package com.google.android.horologist.remotecompose.lottie.format.properties

import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonContentPolymorphicSerializer
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.intOrNull
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

/** A position property is an array of floats (either 2D or 3D). */
@Serializable(with = BasePositionPropertySerializer::class)
internal sealed class BasePositionProperty {
  abstract val animated: Boolean
  abstract val slotId: String?
}

/** A static position property is an array of floats with 2 or 3 values. */
@Serializable
internal data class StaticPositionProperty(
  @SerialName("sid") override val slotId: String? = null,
  @SerialName("a") val animatedInt: Int = 0,
  @SerialName("k") val value: FloatArray,
) : BasePositionProperty() {
  override val animated: Boolean
    get() = animatedInt == 1

  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (javaClass != other?.javaClass) return false
    other as StaticPositionProperty
    if (slotId != other.slotId) return false
    if (!value.contentEquals(other.value)) return false
    return true
  }

  override fun hashCode(): Int {
    var result = slotId?.hashCode() ?: 0
    result = 31 * result + value.contentHashCode()
    return result
  }
}

/** An animated position property with keyframes. */
@Serializable
internal data class AnimatedPositionProperty(
  @SerialName("sid") override val slotId: String? = null,
  @SerialName("a") val animatedInt: Int = 1,
  @SerialName("k") val keyframes: List<VectorPropertyKeyframe>,
) : BasePositionProperty() {
  override val animated: Boolean
    get() = animatedInt == 1
}

/** Polymorphic serializer for [BasePositionProperty] based on "a" field. */
internal object BasePositionPropertySerializer :
  JsonContentPolymorphicSerializer<BasePositionProperty>(BasePositionProperty::class) {
  override fun selectDeserializer(
    element: JsonElement
  ): DeserializationStrategy<BasePositionProperty> {
    val animated = element.jsonObject["a"]?.jsonPrimitive?.intOrNull == 1
    return if (animated) {
      AnimatedPositionProperty.serializer()
    } else {
      StaticPositionProperty.serializer()
    }
  }
}
