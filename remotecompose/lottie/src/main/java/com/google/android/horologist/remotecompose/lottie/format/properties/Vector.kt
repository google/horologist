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

/** Base class for vector (array of floats) properties. */
@Serializable(with = BaseVectorPropertySerializer::class)
internal sealed class BaseVectorProperty {
  abstract val animated: Boolean
  abstract val slotId: String?
}

/** A static array of floats. */
@Serializable
internal data class StaticVectorProperty(
  @SerialName("sid") override val slotId: String? = null,
  @SerialName("a") val animatedInt: Int = 0,
  @SerialName("k") val value: FloatArray,
) : BaseVectorProperty() {
  override val animated: Boolean
    get() = animatedInt == 1

  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (javaClass != other?.javaClass) return false
    other as StaticVectorProperty
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

/** An animated array of floats. */
@Serializable
internal data class AnimatedVectorProperty(
  @SerialName("sid") override val slotId: String? = null,
  @SerialName("a") val animatedInt: Int = 1,
  @SerialName("k") val keyframes: List<VectorPropertyKeyframe>,
) : BaseVectorProperty() {
  override val animated: Boolean
    get() = animatedInt == 1
}

/** A single keyframe for an animated vector property. */
@Serializable
internal data class VectorPropertyKeyframe(
  @SerialName("t") val frame: Float = 0f,
  @SerialName("h") val hold: Boolean = false,
  @SerialName("i") val inTangent: ScalarKeyframeEasing? = null,
  @SerialName("o") val outTangent: ScalarKeyframeEasing? = null,
  @SerialName("s") val value: FloatArray,
) {
  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (javaClass != other?.javaClass) return false
    other as VectorPropertyKeyframe
    if (frame != other.frame) return false
    if (hold != other.hold) return false
    if (inTangent != other.inTangent) return false
    if (outTangent != other.outTangent) return false
    if (!value.contentEquals(other.value)) return false
    return true
  }

  override fun hashCode(): Int {
    var result = frame.hashCode()
    result = 31 * result + hold.hashCode()
    result = 31 * result + (inTangent?.hashCode() ?: 0)
    result = 31 * result + (outTangent?.hashCode() ?: 0)
    result = 31 * result + value.contentHashCode()
    return result
  }
}

/** Polymorphic serializer for [BaseVectorProperty] based on "a" field. */
internal object BaseVectorPropertySerializer :
  JsonContentPolymorphicSerializer<BaseVectorProperty>(BaseVectorProperty::class) {
  override fun selectDeserializer(
    element: JsonElement
  ): DeserializationStrategy<BaseVectorProperty> {
    val animated = element.jsonObject["a"]?.jsonPrimitive?.intOrNull == 1
    return if (animated) {
      AnimatedVectorProperty.serializer()
    } else {
      StaticVectorProperty.serializer()
    }
  }
}
