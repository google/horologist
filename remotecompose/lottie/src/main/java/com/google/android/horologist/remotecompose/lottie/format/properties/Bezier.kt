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

import com.google.android.horologist.remotecompose.lottie.format.values.BezierValue
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonContentPolymorphicSerializer
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.intOrNull
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

/** A base class for bezier properties. */
@Serializable(with = BaseBezierPropertySerializer::class)
internal sealed class BaseBezierProperty {
  abstract val animated: Boolean
}

/**
 * A static bezier. The value is an array of floats with 4 values, describing the 2 control points
 * of the curve.
 */
@Serializable
internal data class StaticBezierProperty(
  @SerialName("a") val animatedInt: Int = 0,
  @SerialName("k") val value: BezierValue,
) : BaseBezierProperty() {
  override val animated: Boolean
    get() = animatedInt == 1
}

/** An animated bezier. */
@Serializable
internal data class AnimatedBezierProperty(
  @SerialName("a") val animatedInt: Int = 1,
  @SerialName("k") val keyframes: List<BezierKeyframe>,
) : BaseBezierProperty() {
  override val animated: Boolean
    get() = animatedInt == 1
}

/** A single keyframe for an animated bezier property. */
@Serializable
internal data class BezierKeyframe(
  @SerialName("t") val frame: Float = 0f,
  @SerialName("h") val hold: Boolean = false,
  @SerialName("i") val inTangent: ScalarKeyframeEasing? = null,
  @SerialName("o") val outTangent: ScalarKeyframeEasing? = null,
  @SerialName("s") val value: List<BezierValue>,
)

/** Polymorphic serializer for [BaseBezierProperty] based on "a" field. */
internal object BaseBezierPropertySerializer :
  JsonContentPolymorphicSerializer<BaseBezierProperty>(BaseBezierProperty::class) {
  override fun selectDeserializer(
    element: JsonElement
  ): DeserializationStrategy<BaseBezierProperty> {
    val animated = element.jsonObject["a"]?.jsonPrimitive?.intOrNull == 1
    return if (animated) {
      AnimatedBezierProperty.serializer()
    } else {
      StaticBezierProperty.serializer()
    }
  }
}
