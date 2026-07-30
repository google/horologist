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

package com.google.android.horologist.remotecompose.lottie.format

import androidx.annotation.ColorInt
import androidx.compose.remote.creation.compose.state.RemoteColor
import androidx.compose.remote.creation.compose.state.rc
import androidx.compose.ui.graphics.Color
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Base class for all animatable properties.
 *
 * This class is used to detect whether a property is animated or not for deserialization.
 */
@Serializable
sealed class AnimatableProperty {
  abstract val animated: Boolean
}

/** A single float value that is not animated */
@Serializable
data class StaticScalarProperty(
  @SerialName("s") val slotId: String? = null,
  val animated: Boolean = false,
  @SerialName("k") val value: Float,
)

/** A vector property is an array of floats. */
@Serializable(with = BaseVectorPropertySerializer::class)
sealed class BaseVectorProperty : AnimatableProperty() {
  abstract override val animated: Boolean
}

/** A static array of floats. */
@Serializable
data class StaticVectorProperty(
  @SerialName("s") val slotId: String? = null,
  override val animated: Boolean = false,
  @SerialName("k") val value: FloatArray,
) : BaseVectorProperty()

/** An animated array of floats. */
@Serializable
data class AnimatedVectorProperty(
  @SerialName("s") val slotId: String? = null,
  override val animated: Boolean = true,
  @SerialName("k") val keyframes: List<VectorPropertyKeyframe>,
) : BaseVectorProperty()

/** A single keyframe for an animated vector property. */
@Serializable
data class VectorPropertyKeyframe(
  @SerialName("t") val frame: Float = 0f,
  @SerialName("h") val hold: Boolean = false,
  @SerialName("i") val inTangent: ScalarKeyframeEasing? = null,
  @SerialName("o") val outTangent: ScalarKeyframeEasing? = null,
  @SerialName("s") val value: FloatArray,
)

/** A static position property is an array of floats with 2 values - x and y */
@Serializable
data class StaticPositionProperty(
  @SerialName("s") val slotId: String? = null,
  val animated: Boolean = false,
  @SerialName("k") val value: FloatArray,
)

/** A static color property is an array of floats with 3 or 4 values - r, g, b, a */
@Serializable(with = StaticColorPropertySerializer::class)
data class StaticColorProperty(
  @SerialName("sid") val slotId: String? = null,
  val animated: Boolean = false,
  @SerialName("k") val colorInt: Int = 0,
) {
  val value: RemoteColor
    get() = Color(colorInt).rc

  companion object {
    fun fromColor(color: Color): StaticColorProperty {
      return StaticColorProperty(colorInt = color.hashCode())
    }

    fun fromColor(@ColorInt color: Int): StaticColorProperty {
      return StaticColorProperty(colorInt = color)
    }
  }
}

/** A base class for bezier properties. */
@Serializable(with = BaseBezierPropertySerializer::class)
sealed class BaseBezierProperty : AnimatableProperty() {
  abstract override val animated: Boolean
}

/**
 * A static bezier. The value is an array of floats with 4 values, describing the 2 control points
 * of the curve.
 */
@Serializable
data class StaticBezierProperty(
  override val animated: Boolean = false,
  @SerialName("k") val value: BezierValue,
) : BaseBezierProperty()

/** An animated bezier. */
@Serializable
data class AnimatedBezierProperty(
  override val animated: Boolean = true,
  @SerialName("k") val keyframes: List<BezierKeyframe>,
) : BaseBezierProperty()

/** A single keyframe for an animated bezier property. */
@Serializable
data class BezierKeyframe(
  @SerialName("t") val frame: Float = 0f,
  @SerialName("h") val hold: Boolean = false,
  @SerialName("i") val inTangent: ScalarKeyframeEasing? = null,
  @SerialName("o") val outTangent: ScalarKeyframeEasing? = null,
  @SerialName("s") val value: List<BezierValue>,
)

@Serializable(with = ScalarKeyframeEasingSerializer::class)
data class ScalarKeyframeEasing(val x: Float, val y: Float)
