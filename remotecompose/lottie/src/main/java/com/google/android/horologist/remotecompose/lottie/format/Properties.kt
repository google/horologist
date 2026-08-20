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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Base class for all animatable properties.
 *
 * This class is used to detect whether a property is animated or not for deserialization.
 */
@Serializable
internal sealed class AnimatableProperty {
  abstract val animated: Boolean
}

/** Base class for scalar (single Float) properties. */
@Serializable(with = BaseScalarPropertySerializer::class)
internal sealed class BaseScalarProperty : AnimatableProperty() {
  abstract override val animated: Boolean
}

/** A single float value that is not animated */
@Serializable(with = StaticScalarPropertySerializer::class)
internal data class StaticScalarProperty(
  @SerialName("sid") val slotId: String? = null,
  override val animated: Boolean = false,
  @SerialName("k") val value: Float = 0f,
) : BaseScalarProperty()

/** An animated scalar property with keyframes. */
@Serializable
internal data class AnimatedScalarProperty(
  @SerialName("sid") val slotId: String? = null,
  @SerialName("a") val animatedInt: Int = 1,
  @SerialName("k") val keyframes: List<ScalarPropertyKeyframe>,
) : BaseScalarProperty() {
  override val animated: Boolean
    get() = animatedInt == 1
}

/** A single keyframe for an animated scalar property. */
@Serializable(with = ScalarPropertyKeyframeSerializer::class)
internal data class ScalarPropertyKeyframe(
  @SerialName("t") val frame: Float = 0f,
  @SerialName("h") val hold: Boolean = false,
  @SerialName("i") val inTangent: ScalarKeyframeEasing? = null,
  @SerialName("o") val outTangent: ScalarKeyframeEasing? = null,
  @SerialName("s") val value: Float = 0f,
)

/** A vector property is an array of floats. */
@Serializable(with = BaseVectorPropertySerializer::class)
internal sealed class BaseVectorProperty : AnimatableProperty() {
  abstract override val animated: Boolean
}

/** A static array of floats. */
@Serializable
internal data class StaticVectorProperty(
  @SerialName("sid") val slotId: String? = null,
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
  @SerialName("sid") val slotId: String? = null,
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
  @SerialName("ti") val inSpatialTangent: FloatArray? = null,
  @SerialName("to") val outSpatialTangent: FloatArray? = null,
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
    if (inSpatialTangent != null) {
      if (other.inSpatialTangent == null || !inSpatialTangent.contentEquals(other.inSpatialTangent))
        return false
    } else if (other.inSpatialTangent != null) return false
    if (outSpatialTangent != null) {
      if (
        other.outSpatialTangent == null || !outSpatialTangent.contentEquals(other.outSpatialTangent)
      )
        return false
    } else if (other.outSpatialTangent != null) return false
    return true
  }

  override fun hashCode(): Int {
    var result = frame.hashCode()
    result = 31 * result + hold.hashCode()
    result = 31 * result + (inTangent?.hashCode() ?: 0)
    result = 31 * result + (outTangent?.hashCode() ?: 0)
    result = 31 * result + value.contentHashCode()
    result = 31 * result + (inSpatialTangent?.contentHashCode() ?: 0)
    result = 31 * result + (outSpatialTangent?.contentHashCode() ?: 0)
    return result
  }
}

/** A position property is an array of floats (either 2D or 3D). */
@Serializable(with = BasePositionPropertySerializer::class)
internal sealed class BasePositionProperty : AnimatableProperty() {
  abstract override val animated: Boolean
}

/** A static position property is an array of floats with 2 or 3 values. */
@Serializable
internal data class StaticPositionProperty(
  @SerialName("sid") val slotId: String? = null,
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

/** An animatable position where position values may be defined and animated separately. */
@Serializable
internal data class SplitPositionProperty(
  @SerialName("sid") val slotId: String? = null,
  @SerialName("s") val split: Boolean = true,
  @SerialName("x") val x: BaseScalarProperty = StaticScalarProperty(value = 0f),
  @SerialName("y") val y: BaseScalarProperty = StaticScalarProperty(value = 0f),
) : BasePositionProperty() {
  override val animated: Boolean
    get() = x.animated || y.animated
}

/** An animated position property with keyframes. */
@Serializable
internal data class AnimatedPositionProperty(
  @SerialName("sid") val slotId: String? = null,
  @SerialName("a") val animatedInt: Int = 1,
  @SerialName("k") val keyframes: List<VectorPropertyKeyframe>,
) : BasePositionProperty() {
  override val animated: Boolean
    get() = animatedInt == 1
}

/** Base class for color properties. */
@Serializable(with = BaseColorPropertySerializer::class)
internal sealed class BaseColorProperty : AnimatableProperty() {
  abstract val slotId: String?
  abstract override val animated: Boolean
}

/** A static color property is an array of floats with 3 or 4 values - r, g, b, a */
@Serializable(with = StaticColorPropertySerializer::class)
internal data class StaticColorProperty(
  @SerialName("sid") override val slotId: String? = null,
  override val animated: Boolean = false,
  @SerialName("k") @ColorInt val value: Int = 0,
) : BaseColorProperty() {
  companion object {
    fun fromColor(color: Color): StaticColorProperty {
      return StaticColorProperty(value = color.toArgb())
    }

    fun fromColor(@ColorInt color: Int): StaticColorProperty {
      return StaticColorProperty(value = color)
    }
  }
}

/** An animated color property with keyframes. */
@Serializable
internal data class AnimatedColorProperty(
  @SerialName("sid") override val slotId: String? = null,
  @SerialName("a") val animatedInt: Int = 1,
  @SerialName("k") val keyframes: List<ColorPropertyKeyframe>,
) : BaseColorProperty() {
  override val animated: Boolean
    get() = animatedInt == 1
}

/** A single keyframe for an animated color property. */
@Serializable(with = ColorPropertyKeyframeSerializer::class)
internal data class ColorPropertyKeyframe(
  @SerialName("t") val frame: Float = 0f,
  @SerialName("h") val hold: Boolean = false,
  @SerialName("i") val inTangent: ScalarKeyframeEasing? = null,
  @SerialName("o") val outTangent: ScalarKeyframeEasing? = null,
  @SerialName("s") val value: Int = 0,
)

/** A base class for bezier properties. */
@Serializable(with = BaseBezierPropertySerializer::class)
internal sealed class BaseBezierProperty : AnimatableProperty() {
  abstract override val animated: Boolean
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

@Serializable(with = ScalarKeyframeEasingSerializer::class)
internal data class ScalarKeyframeEasing(val x: Float, val y: Float)

/** Base class for gradient properties. */
@Serializable(with = BaseGradientPropertySerializer::class)
internal sealed class BaseGradientProperty : AnimatableProperty() {
  abstract override val animated: Boolean
}

/** A static gradient property. */
@Serializable
internal data class StaticGradientProperty(
  @SerialName("sid") val slotId: String? = null,
  @SerialName("p") val numberOfColors: Int = 0,
  @SerialName("k") val value: GradientValue,
) : BaseGradientProperty() {
  override val animated: Boolean = false
}

/** An animated gradient property. */
@Serializable
internal data class AnimatedGradientProperty(
  @SerialName("sid") val slotId: String? = null,
  @SerialName("p") val numberOfColors: Int = 0,
  @SerialName("k") val keyframes: List<GradientPropertyKeyframe>,
) : BaseGradientProperty() {
  override val animated: Boolean = true
}

/** A single keyframe for an animated gradient property. */
@Serializable
internal data class GradientPropertyKeyframe(
  @SerialName("t") val frame: Float = 0f,
  @SerialName("h") val hold: Boolean = false,
  @SerialName("i") val inTangent: ScalarKeyframeEasing? = null,
  @SerialName("o") val outTangent: ScalarKeyframeEasing? = null,
  @SerialName("s") val value: List<GradientValue>,
)
