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
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * Base class for all animatable properties.
 *
 * This class is used to detect whether a property is animated or not for deserialization.
 */
@JsonClass(generateAdapter = true, generator = "sealed:type")
sealed class AnimatableProperty {
  abstract val animated: Boolean
}

/** A single float value that is not animated */
@JsonClass(generateAdapter = true)
data class StaticScalarProperty(
  @param:Json(name = "s") val slotId: String? = null,
  val animated: Boolean = false,
  @param:Json(name = "k") val value: Float,
)

/** A vector property is an array of floats. */
@JsonClass(generateAdapter = true, generator = "sealed:type")
sealed class BaseVectorProperty : AnimatableProperty() {
  abstract override val animated: Boolean
}

/** A static array of floats. */
@JsonClass(generateAdapter = true)
data class StaticVectorProperty(
  @param:Json(name = "s") val slotId: String? = null,
  override val animated: Boolean = false,
  @param:Json(name = "k") val value: FloatArray,
) : BaseVectorProperty()

/** An animated array of floats. */
@JsonClass(generateAdapter = true)
data class AnimatedVectorProperty(
  @param:Json(name = "s") val slotId: String? = null,
  override val animated: Boolean = true,
  @param:Json(name = "k") val keyframes: List<VectorPropertyKeyframe>,
) : BaseVectorProperty()

/** A single keyframe for an animated vector property. */
@JsonClass(generateAdapter = true)
data class VectorPropertyKeyframe(
  @param:Json(name = "t") val frame: Float = 0f,
  @param:Json(name = "h") val hold: Boolean = false,
  @param:Json(name = "i") val inTangent: ScalarKeyframeEasing? = null,
  @param:Json(name = "o") val outTangent: ScalarKeyframeEasing? = null,
  @param:Json(name = "s") val value: FloatArray,
)

/** A static position property is an array of floats with 2 values - x and y */
@JsonClass(generateAdapter = true)
data class StaticPositionProperty(
  @param:Json(name = "s") val slotId: String? = null,
  val animated: Boolean = false,
  @param:Json(name = "k") val value: FloatArray,
)

/** A static color property is an array of floats with 3 or 4 values - r, g, b, a */
@JsonClass(generateAdapter = true)
data class StaticColorProperty(
  @param:Json(name = "sid") val slotId: String? = null,
  val animated: Boolean = false,
  @param:Json(name = "k") val value: RemoteColor,
) {
  companion object {
    fun fromColor(color: Color): StaticColorProperty {
      return StaticColorProperty(value = color.rc)
    }

    fun fromColor(@ColorInt color: Int): StaticColorProperty {
      return StaticColorProperty(value = Color(color).rc)
    }
  }
}

/** A base class for bezier properties. */
@JsonClass(generateAdapter = true, generator = "sealed:type")
sealed class BaseBezierProperty : AnimatableProperty() {
  abstract override val animated: Boolean
}

/**
 * A static bezier. The value is an array of floats with 4 values, describing the 2 control points
 * of the curve.
 */
@JsonClass(generateAdapter = true)
data class StaticBezierProperty(
  override val animated: Boolean = false,
  @param:Json(name = "k") val value: BezierValue,
) : BaseBezierProperty()

/** An animated bezier. */
@JsonClass(generateAdapter = true)
data class AnimatedBezierProperty(
  override val animated: Boolean = true,
  @param:Json(name = "k") val keyframes: List<BezierKeyframe>,
) : BaseBezierProperty()

/** A single keyframe for an animated bezier property. */
@JsonClass(generateAdapter = true)
data class BezierKeyframe(
  @param:Json(name = "t") val frame: Float = 0f,
  @param:Json(name = "h") val hold: Boolean = false,
  @param:Json(name = "i") val inTangent: ScalarKeyframeEasing? = null,
  @param:Json(name = "o") val outTangent: ScalarKeyframeEasing? = null,
  @param:Json(name = "s") val value: List<BezierValue>,
)

@JsonClass(generateAdapter = true) data class ScalarKeyframeEasing(val x: Float, val y: Float)
