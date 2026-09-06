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

import androidx.compose.remote.creation.compose.state.RemoteFloat
import androidx.compose.remote.creation.compose.state.rb
import androidx.compose.remote.creation.compose.state.rf
import com.google.android.horologist.remotecompose.lottie.format.values.SerializableRemoteBoolean
import com.google.android.horologist.remotecompose.lottie.format.values.SerializableRemoteFloat
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.descriptors.element
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonContentPolymorphicSerializer
import kotlinx.serialization.json.JsonDecoder
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonEncoder
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.add
import kotlinx.serialization.json.buildJsonArray
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.floatOrNull
import kotlinx.serialization.json.intOrNull
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.put

/**
 * Base class for all Lottie animatable scalar properties conforming to
 * [Scalar Property](https://lottie.github.io/lottie-spec/1.0.1/specs/properties/#scalar-property).
 *
 * Scalar properties represent single floating-point numbers (such as opacity, rotation, corner
 * radius, or star point counts).
 *
 * Essential Invariants:
 * - The property is partitioned into two mutually exclusive branches identified by the
 *   integer-boolean discriminator [animated]:
 *     - `0` (`false.rb`): [StaticScalarProperty], holding a constant [SerializableRemoteFloat].
 *     - `1` (`true.rb`): [AnimatedScalarProperty], holding a sequence of keyframes over time.
 * - [slotId]: Optional slot identifier (`sid`) enabling runtime value replacement via Lottie slots.
 */
@Serializable(with = BaseScalarPropertySerializer::class)
internal sealed class BaseScalarProperty {
  abstract val animated: SerializableRemoteBoolean
  abstract val slotId: String?
}

/**
 * Conforms to
 * [Scalar Property](https://lottie.github.io/lottie-spec/1.0.1/specs/properties/#scalar-property)
 * (Not animated branch):
 * - Required Fields: `"a"` (const 0), `"k"` (single number).
 * - Optional Fields: `"sid"` (slot identifier, default null).
 *
 * Invariants:
 * - [animated] is guaranteed to represent integer `0` (`false.rb`).
 * - [value] contains the constant scalar value as [SerializableRemoteFloat].
 */
@Serializable
internal data class StaticScalarProperty(
  @SerialName("sid") override val slotId: String? = null,
  @SerialName("a") override val animated: SerializableRemoteBoolean = false.rb,
  @SerialName("k") val value: SerializableRemoteFloat,
) : BaseScalarProperty()

/**
 * Conforms to
 * [Scalar Property](https://lottie.github.io/lottie-spec/1.0.1/specs/properties/#scalar-property)
 * (Animated branch):
 * - Required Fields: `"a"` (const 1), `"k"` (array of keyframes).
 * - Optional Fields: `"sid"` (slot identifier, default null).
 *
 * Invariants:
 * - [animated] is guaranteed to represent integer `1` (`true.rb`).
 * - [keyframes] defines the temporal evolution of the scalar across animation frames.
 */
@Serializable
internal data class AnimatedScalarProperty(
  @SerialName("sid") override val slotId: String? = null,
  @SerialName("a") override val animated: SerializableRemoteBoolean = true.rb,
  @SerialName("k") val keyframes: List<ScalarPropertyKeyframe>,
) : BaseScalarProperty()

/**
 * A single scalar keyframe conforming to
 * [Vector Keyframe](https://lottie.github.io/lottie-spec/1.0.1/specs/properties/#vector-keyframe)
 * as specified in
 * [Scalar Property](https://lottie.github.io/lottie-spec/1.0.1/specs/properties/#scalar-property).
 *
 * Defines the scalar value and optional easing interpolation parameters at a specific timeline
 * frame.
 *
 * Schema Specification:
 * - Required Fields: `"t"` (start frame), `"s"` (scalar value; serialized as a single-element
 *   array).
 * - Optional Fields with Schema Default: `"h"` (hold interpolation flag, default: 0 -> `false.rb`).
 * - Optional Fields without Schema Default: `"i"` (incoming tangent, default null), `"o"` (outgoing
 *   tangent, default null).
 *
 * Invariants:
 * - [frame]: Timeline time in frames at which this keyframe takes effect.
 * - [value]: Scalar component active at [frame]. We have to use a custom serializer
 *   [ScalarKeyframeValueSerializer] instead of relying on [SerializableRemoteFloat] because this
 *   data is encoded in json as an array with one element, not as a float.
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
internal data class ScalarPropertyKeyframe(
  @SerialName("t") val frame: SerializableRemoteFloat,
  @SerialName("s")
  @Serializable(with = ScalarKeyframeValueSerializer::class)
  val value: RemoteFloat,
  @SerialName("h") val hold: SerializableRemoteBoolean = false.rb,
  @SerialName("i") val inTangent: ScalarKeyframeEasing? = null,
  @SerialName("o") val outTangent: ScalarKeyframeEasing? = null,
)

/**
 * Easing handle coordinates [x, y] conforming to
 * [Keyframe Easing](https://lottie.github.io/lottie-spec/1.0.1/specs/properties/#easing-handle).
 *
 * Defaults:
 * - [x] defaults to `0f.rf`
 * - [y] defaults to `0f.rf`
 */
@Serializable(with = ScalarKeyframeEasingSerializer::class)
internal data class ScalarKeyframeEasing(val x: RemoteFloat = 0f.rf, val y: RemoteFloat = 0f.rf) {
  constructor(x: Float, y: Float) : this(x.rf, y.rf)
}

/**
 * Polymorphic serializer for [BaseScalarProperty] discriminating between static and animated
 * variants based on the Lottie schema `"a"` field ([Integer
 * Boolean](https://lottie.github.io/lottie-spec/1.0.1/specs/values/#int-boolean)).
 *
 * Contract:
 * - Preconditions: [element] must be a [JsonObject].
 * - Postconditions:
 *     - Selects [AnimatedScalarProperty.serializer] when `"a"` is integer `1`.
 *     - Selects [StaticScalarProperty.serializer] when `"a"` is integer `0`.
 * - Exceptions:
 *     - Throws [SerializationException] if [element] is not a [JsonObject].
 *     - Throws [SerializationException] if `"a"` is missing.
 *     - Throws [SerializationException] if `"a"` is neither `0` nor `1`.
 */
internal object BaseScalarPropertySerializer :
  JsonContentPolymorphicSerializer<BaseScalarProperty>(BaseScalarProperty::class) {
  override fun selectDeserializer(
    element: JsonElement
  ): DeserializationStrategy<BaseScalarProperty> {
    val obj = element as? JsonObject ?: throw SerializationException("Expected JSON object")
    val animated = obj["a"]?.jsonPrimitive?.intOrNull
    return when (animated) {
      1 -> AnimatedScalarProperty.serializer()
      0 -> StaticScalarProperty.serializer()
      null ->
        throw SerializationException("Scalar property missing required 'a' field per Lottie schema")
      else -> throw SerializationException("Field 'a' must be 0 or 1, but was $animated")
    }
  }
}

/** Serializer for [ScalarKeyframeEasing] handling numbers or 1-element arrays. */
internal object ScalarKeyframeEasingSerializer : KSerializer<ScalarKeyframeEasing> {
  override val descriptor: SerialDescriptor =
    buildClassSerialDescriptor("ScalarKeyframeEasing") {
      element<Float>("x", isOptional = true)
      element<Float>("y", isOptional = true)
    }

  override fun deserialize(decoder: Decoder): ScalarKeyframeEasing {
    val jsonDecoder = decoder as? JsonDecoder ?: return ScalarKeyframeEasing()
    val element = jsonDecoder.decodeJsonElement()
    val obj = element as? JsonObject ?: return ScalarKeyframeEasing()
    val x = parseTangentValue(obj["x"])
    val y = parseTangentValue(obj["y"])
    return ScalarKeyframeEasing(x, y)
  }

  private fun parseTangentValue(element: JsonElement?): RemoteFloat {
    val value =
      when (element) {
        is JsonPrimitive -> element.floatOrNull ?: 0f
        is JsonArray -> element.firstOrNull()?.jsonPrimitive?.floatOrNull ?: 0f
        else -> 0f
      }
    return value.rf
  }

  override fun serialize(encoder: Encoder, value: ScalarKeyframeEasing) {
    val jsonEncoder = encoder as JsonEncoder
    jsonEncoder.encodeJsonElement(
      buildJsonObject {
        put("x", value.x.constantValue)
        put("y", value.y.constantValue)
      }
    )
  }
}

/**
 * Custom serializer for the scalar keyframe value `"s"`.
 *
 * Conforms to
 * [Scalar Property](https://lottie.github.io/lottie-spec/1.0.1/specs/properties/#scalar-property),
 * which specifies that animated scalar properties use vector keyframes where values are encoded as
 * arrays with a single component (e.g. `[100.0]`).
 *
 * Deserialization:
 * - Unpacks the primary float value from a JSON array.
 * - Tolerates arrays with additional dimensions, extracting the primary scalar component.
 * - Throws [SerializationException] if the token is not an array, is empty, or contains non-numeric
 *   data.
 *
 * Serialization:
 * - Encodes the [RemoteFloat] scalar value as a single-element JSON array `[value.constantValue]`.
 */
internal object ScalarKeyframeValueSerializer : KSerializer<RemoteFloat> {
  override val descriptor: SerialDescriptor = ListSerializer(Float.serializer()).descriptor

  override fun deserialize(decoder: Decoder): RemoteFloat {
    val jsonDecoder =
      decoder as? JsonDecoder
        ?: throw SerializationException("ScalarKeyframeValueSerializer only supports JSON decoding")
    val element = jsonDecoder.decodeJsonElement()
    val array =
      element as? JsonArray
        ?: throw SerializationException("Keyframe value 's' must be an array per Lottie schema")
    if (array.isEmpty()) {
      throw SerializationException("Keyframe value 's' must contain at least one element")
    }
    val primitive =
      array.first() as? JsonPrimitive
        ?: throw SerializationException("Keyframe value 's' element must be a primitive")
    val floatVal =
      primitive.floatOrNull
        ?: throw SerializationException("Keyframe value 's' element must be a valid float")
    return floatVal.rf
  }

  override fun serialize(encoder: Encoder, value: RemoteFloat) {
    val jsonEncoder =
      encoder as? JsonEncoder
        ?: throw SerializationException("ScalarKeyframeValueSerializer only supports JSON encoding")
    jsonEncoder.encodeJsonElement(buildJsonArray { add(value.constantValue) })
  }
}
