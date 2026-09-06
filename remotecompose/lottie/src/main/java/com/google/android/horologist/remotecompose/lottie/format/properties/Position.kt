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

import androidx.compose.remote.creation.compose.state.rb
import com.google.android.horologist.remotecompose.lottie.format.values.Point
import com.google.android.horologist.remotecompose.lottie.format.values.SerializableRemoteBoolean
import com.google.android.horologist.remotecompose.lottie.format.values.SerializableRemoteFloat
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.JsonContentPolymorphicSerializer
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.intOrNull
import kotlinx.serialization.json.jsonPrimitive

/**
 * Base class for all Lottie animatable position properties conforming to
 * [Position Property](https://lottie.github.io/lottie-spec/1.0.1/specs/properties/#position-property).
 *
 * Position properties represent multidimensional spatial coordinates (such as layer translation,
 * anchor point, or parametric shape positions).
 *
 * Essential Invariants:
 * - The property is partitioned into two mutually exclusive branches identified by the
 *   integer-boolean discriminator [animated]:
 *     - `0` (`false.rb`): [StaticPositionProperty], holding constant coordinate vector components.
 *     - `1` (`true.rb`): [AnimatedPositionProperty], holding a chronological sequence of spatial
 *       keyframes.
 * - [slotId]: Optional slot identifier (`sid`) enabling runtime value replacement via Lottie slots.
 */
@Serializable(with = BasePositionPropertySerializer::class)
internal sealed class BasePositionProperty {
  abstract val animated: SerializableRemoteBoolean
  abstract val slotId: String?
}

/**
 * Conforms to
 * [Position Property](https://lottie.github.io/lottie-spec/1.0.1/specs/properties/#position-property)
 * (Not animated branch):
 * - Required Fields: `"a"` (const 0), `"k"` (array of numbers with at least 2 coordinates).
 * - Optional Fields: `"sid"` (slot identifier, default null).
 *
 * Invariants:
 * - [animated] is guaranteed to represent integer `0` (`false.rb`).
 * - [value] contains 2D coordinates [Point]. Coordinate parsing requires at least two numerical
 *   components ([Point.x] and [Point.y]), discarding any additional dimensions per Lottie's 2D
 *   canvas model.
 */
@Serializable
internal data class StaticPositionProperty(
  @SerialName("sid") override val slotId: String? = null,
  @SerialName("a") override val animated: SerializableRemoteBoolean = false.rb,
  @SerialName("k") val value: Point,
) : BasePositionProperty()

/**
 * Conforms to
 * [Position Property](https://lottie.github.io/lottie-spec/1.0.1/specs/properties/#position-property)
 * (Animated branch):
 * - Required Fields: `"a"` (const 1), `"k"` (array of position keyframes).
 * - Optional Fields: `"sid"` (slot identifier, default null).
 *
 * Invariants:
 * - [animated] is guaranteed to represent integer `1` (`true.rb`).
 * - [keyframes] defines the spatial and temporal evolution of position coordinates over animation
 *   frames.
 */
@Serializable
internal data class AnimatedPositionProperty(
  @SerialName("sid") override val slotId: String? = null,
  @SerialName("a") override val animated: SerializableRemoteBoolean = true.rb,
  @SerialName("k") val keyframes: List<PositionPropertyKeyframe>,
) : BasePositionProperty()

/**
 * A single position keyframe conforming to
 * [Position Keyframe](https://lottie.github.io/lottie-spec/1.0.1/specs/properties/#position-keyframe).
 *
 * Defines the 2D coordinate value and optional easing interpolation parameters at a specific
 * timeline frame.
 *
 * Schema Specification:
 * - Required Fields: `"t"` (start frame), `"s"` (value array of coordinates).
 * - Optional Fields with Schema Default: `"h"` (hold interpolation flag, default: 0 -> `false.rb`).
 * - Optional Fields without Schema Default:
 *     - `"i"` (incoming temporal tangent handle)
 *     - `"o"` (outgoing temporal tangent handle)
 *     - `"ti"` (incoming spatial tangent)
 *     - `"to"` (outgoing spatial tangent)
 *
 * Invariants:
 * - [frame]: Timeline time in frames at which this keyframe takes effect.
 * - [value]: 2D coordinate position [Point] active at [frame]. Coordinate parsing requires at least
 *   two numerical components ([Point.x] and [Point.y]), discarding any additional dimensions per
 *   Lottie's 2D canvas model.
 * - [hold]: When `1` (`true.rb`), the position is held constant until the next keyframe without
 *   interpolation.
 * - [inTangent], [outTangent]: Optional cubic Bézier temporal easing handles conforming to
 *   [Easing Handle](https://lottie.github.io/lottie-spec/1.0.1/specs/properties/#easing-handle).
 *   These are null under any of the following canonical Lottie conditions:
 *     1. Easing handles are omitted from the JSON payload, in which case default linear
 *        interpolation applies.
 *     2. Hold interpolation is active ([hold] is `true.rb`), making easing curves inapplicable.
 *     3. The keyframe is the final (terminal) keyframe in an animation sequence, having no
 *        subsequent interval to interpolate towards.
 * - [inSpatialTangent], [outSpatialTangent]: Optional spatial Bézier control point coordinates for
 *   curved motion paths conforming to
 *   [Position Keyframe](https://lottie.github.io/lottie-spec/1.0.1/specs/properties/#position-keyframe).
 */
@Serializable
internal data class PositionPropertyKeyframe(
  @SerialName("t") val frame: SerializableRemoteFloat,
  @SerialName("s") val value: Point,
  @SerialName("h") val hold: SerializableRemoteBoolean = false.rb,
  @SerialName("i") val inTangent: ScalarKeyframeEasing? = null,
  @SerialName("o") val outTangent: ScalarKeyframeEasing? = null,
  @SerialName("ti") val inSpatialTangent: Point? = null,
  @SerialName("to") val outSpatialTangent: Point? = null,
)

/**
 * Polymorphic serializer for [BasePositionProperty] discriminating between static and animated
 * variants based on the Lottie schema `"a"` field ([Integer
 * Boolean](https://lottie.github.io/lottie-spec/1.0.1/specs/values/#int-boolean)).
 *
 * Contract:
 * - Preconditions: [element] must be a [JsonObject].
 * - Postconditions:
 *     - Selects [AnimatedPositionProperty.serializer] when `"a"` is integer `1`.
 *     - Selects [StaticPositionProperty.serializer] when `"a"` is integer `0`.
 * - Exceptions:
 *     - Throws [SerializationException] if [element] is not a [JsonObject].
 *     - Throws [SerializationException] if `"a"` is missing.
 *     - Throws [SerializationException] if `"a"` is neither `0` nor `1`.
 */
internal object BasePositionPropertySerializer :
  JsonContentPolymorphicSerializer<BasePositionProperty>(BasePositionProperty::class) {
  override fun selectDeserializer(
    element: JsonElement
  ): DeserializationStrategy<BasePositionProperty> {
    val obj = element as? JsonObject ?: throw SerializationException("Expected JSON object")
    val animated = obj["a"]?.jsonPrimitive?.intOrNull
    return when (animated) {
      1 -> AnimatedPositionProperty.serializer()
      0 -> StaticPositionProperty.serializer()
      null ->
        throw SerializationException(
          "Position property missing required 'a' field per Lottie schema"
        )
      else -> throw SerializationException("Field 'a' must be 0 or 1, but was $animated")
    }
  }
}
