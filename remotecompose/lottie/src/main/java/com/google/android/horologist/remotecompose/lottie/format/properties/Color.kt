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
import com.google.android.horologist.remotecompose.lottie.format.values.KeyframeEasing
import com.google.android.horologist.remotecompose.lottie.format.values.SerializableRemoteBoolean
import com.google.android.horologist.remotecompose.lottie.format.values.SerializableRemoteColor
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
 * Base class for all Lottie animatable color properties conforming to
 * [Color Property](https://lottie.github.io/lottie-spec/1.0.1/specs/properties/#color-property).
 *
 * Color properties represent RGBA colors as 3- or 4-element numerical arrays with components in
 * range [0.0, 1.0].
 *
 * Essential Invariants:
 * - The property is partitioned into two mutually exclusive branches identified by the
 *   integer-boolean discriminator [animated]:
 *     - `0` (`false.rb`): [StaticColorProperty], holding a constant color value.
 *     - `1` (`true.rb`): [AnimatedColorProperty], holding a sequence of keyframes over time.
 * - [slotId]: Optional slot identifier (`sid`) enabling runtime value replacement via Lottie slots.
 */
@Serializable(with = BaseColorPropertySerializer::class)
internal sealed class BaseColorProperty {
  abstract val animated: SerializableRemoteBoolean
  abstract val slotId: String?
}

/**
 * Conforms to
 * [Color Property](https://lottie.github.io/lottie-spec/1.0.1/specs/properties/#color-property)
 * (Not animated branch):
 * - Required Fields: `"a"` (const 0), `"k"` (array of numbers, minItems: 3, maxItems: 4).
 * - Optional Fields: `"sid"` (slot identifier, default null).
 *
 * Invariants:
 * - [animated] is guaranteed to represent integer `0` (`false.rb`).
 * - [value] contains the resolved color as [SerializableRemoteColor] with components in [0.0, 1.0].
 */
@Serializable
internal data class StaticColorProperty(
  @SerialName("sid") override val slotId: String? = null,
  @SerialName("a") override val animated: SerializableRemoteBoolean = false.rb,
  @SerialName("k") val value: SerializableRemoteColor,
) : BaseColorProperty()

/**
 * Conforms to
 * [Color Property](https://lottie.github.io/lottie-spec/1.0.1/specs/properties/#color-property)
 * (Animated branch):
 * - Required Fields: `"a"` (const 1), `"k"` (array of color keyframes).
 * - Optional Fields: `"sid"` (slot identifier, default null).
 *
 * Invariants:
 * - [animated] is guaranteed to represent integer `1` (`true.rb`).
 * - [keyframes] defines the temporal evolution of the color across animation frames.
 */
@Serializable
internal data class AnimatedColorProperty(
  @SerialName("sid") override val slotId: String? = null,
  @SerialName("a") override val animated: SerializableRemoteBoolean = true.rb,
  @SerialName("k") val keyframes: List<ColorPropertyKeyframe>,
) : BaseColorProperty()

/**
 * A single color keyframe conforming to
 * [Color Keyframe](https://lottie.github.io/lottie-spec/1.0.1/specs/properties/#color-keyframe).
 *
 * Defines the color value and optional easing interpolation parameters at a specific timeline
 * frame.
 *
 * Schema Specification:
 * - Required Fields: `"t"` (start frame), `"s"` (value array of 3 or 4 numbers).
 * - Optional Fields with Schema Default: `"h"` (hold interpolation flag, default: 0 -> `false.rb`).
 * - Optional Fields without Schema Default: `"i"` (incoming tangent), `"o"` (outgoing tangent).
 *
 * Invariants:
 * - [frame]: Timeline time in frames at which this keyframe takes effect.
 * - [value]: Color active at [frame] as [SerializableRemoteColor].
 * - [hold]: When `1` (`true.rb`), the value is held constant until the next keyframe without
 *   interpolation.
 * - [inTangent], [outTangent]: Optional cubic Bézier easing curve handles conforming to
 *   [Easing Handle](https://lottie.github.io/lottie-spec/dev/specs/properties/#easing-handle).
 *   These are null under any of the following canonical Lottie conditions:
 *     1. Easing handles are omitted from the JSON payload, in which case default linear
 *        interpolation applies.
 *     2. Hold interpolation is active ([hold] is `true.rb`), making easing curves inapplicable.
 *     3. The keyframe is the final (terminal) keyframe in an animation sequence, having no
 *        subsequent interval to interpolate towards.
 */
@Serializable
internal data class ColorPropertyKeyframe(
  @SerialName("t") val frame: SerializableRemoteFloat,
  @SerialName("s") val value: SerializableRemoteColor,
  @SerialName("h") val hold: SerializableRemoteBoolean = false.rb,
  @SerialName("i") val inTangent: KeyframeEasing? = null,
  @SerialName("o") val outTangent: KeyframeEasing? = null,
)

/**
 * Polymorphic serializer for [BaseColorProperty] discriminating between static and animated
 * variants based on the Lottie schema `"a"` field ([Integer
 * Boolean](https://lottie.github.io/lottie-spec/1.0.1/specs/values/#int-boolean)).
 *
 * Contract:
 * - Preconditions: [element] must be a [JsonObject].
 * - Postconditions:
 *     - Selects [AnimatedColorProperty.serializer] when `"a"` is integer `1`.
 *     - Selects [StaticColorProperty.serializer] when `"a"` is integer `0`.
 * - Exceptions:
 *     - Throws [SerializationException] if [element] is not a [JsonObject].
 *     - Throws [SerializationException] if `"a"` is missing.
 *     - Throws [SerializationException] if `"a"` is neither `0` nor `1`.
 */
internal object BaseColorPropertySerializer :
  JsonContentPolymorphicSerializer<BaseColorProperty>(BaseColorProperty::class) {
  override fun selectDeserializer(
    element: JsonElement
  ): DeserializationStrategy<BaseColorProperty> {
    val obj = element as? JsonObject ?: throw SerializationException("Expected JSON object")
    val animated = obj["a"]?.jsonPrimitive?.intOrNull
    return when (animated) {
      1 -> AnimatedColorProperty.serializer()
      0 -> StaticColorProperty.serializer()
      null ->
        throw SerializationException("Color property missing required 'a' field per Lottie schema")
      else -> throw SerializationException("Field 'a' must be 0 or 1, but was $animated")
    }
  }
}
