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
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.ListSerializer
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
import kotlinx.serialization.json.booleanOrNull
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.floatOrNull
import kotlinx.serialization.json.intOrNull
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.put

/**
 * Base class for all Lottie scalar (single float) properties.
 *
 * Unifies static constant scalars ([StaticScalarProperty]) and keyframed dynamic animations
 * ([AnimatedScalarProperty]) under a shared contract for the AST and renderer pipeline.
 */
@Serializable(with = BaseScalarPropertySerializer::class)
internal sealed class BaseScalarProperty {
  abstract val animated: Boolean
  abstract val slotId: String?
}

/** A single float value that is not animated. */
@Serializable(with = StaticScalarPropertySerializer::class)
internal data class StaticScalarProperty(
  @SerialName("sid") override val slotId: String? = null,
  override val animated: Boolean = false,
  @SerialName("k") val value: Float = 0f,
) : BaseScalarProperty()

/** An animated scalar property with keyframes. */
@Serializable(with = AnimatedScalarPropertySerializer::class)
internal data class AnimatedScalarProperty(
  @SerialName("sid") override val slotId: String? = null,
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

@Serializable(with = ScalarKeyframeEasingSerializer::class)
internal data class ScalarKeyframeEasing(val x: Float, val y: Float)

/** Polymorphic serializer for [BaseScalarProperty] based on "a" field. */
internal object BaseScalarPropertySerializer :
  JsonContentPolymorphicSerializer<BaseScalarProperty>(BaseScalarProperty::class) {
  override fun selectDeserializer(
    element: JsonElement
  ): DeserializationStrategy<BaseScalarProperty> {
    val animated = element is JsonObject && element["a"]?.jsonPrimitive?.intOrNull == 1
    return if (animated) {
      AnimatedScalarPropertySerializer
    } else {
      StaticScalarPropertySerializer
    }
  }
}

/**
 * Helper to parse a scalar float value from a [JsonElement], supporting primitive numbers,
 * 1-element arrays, nested float arrays, and nested objects.
 */
internal fun parseScalarElement(element: JsonElement?): Float {
  return when (element) {
    null -> 0f
    is JsonPrimitive -> element.floatOrNull ?: 0f
    is JsonArray -> {
      if (element.isEmpty()) {
        0f
      } else {
        parseScalarElement(element.first())
      }
    }
    is JsonObject -> {
      element["k"]?.let { parseScalarElement(it) }
        ?: element["s"]?.let { parseScalarElement(it) }
        ?: 0f
    }
  }
}

/** Serializer for [StaticScalarProperty] handling primitive numbers, arrays, or objects. */
internal object StaticScalarPropertySerializer : KSerializer<StaticScalarProperty> {
  override val descriptor: SerialDescriptor =
    buildClassSerialDescriptor("StaticScalarProperty") {
      element<String?>("sid", isOptional = true)
      element<Boolean>("animated", isOptional = true)
      element<Float>("k")
    }

  override fun deserialize(decoder: Decoder): StaticScalarProperty {
    val jsonDecoder = decoder as JsonDecoder
    val element = jsonDecoder.decodeJsonElement()
    return when (element) {
      is JsonObject -> {
        val slotId = element["sid"]?.jsonPrimitive?.contentOrNull
        val kElem = element["k"]
        val v = if (kElem != null) parseScalarElement(kElem) else parseScalarElement(element)
        StaticScalarProperty(slotId = slotId, animated = false, value = v)
      }
      is JsonArray -> {
        val v = parseScalarElement(element)
        StaticScalarProperty(slotId = null, animated = false, value = v)
      }
      is JsonPrimitive -> {
        val v = parseScalarElement(element)
        StaticScalarProperty(slotId = null, animated = false, value = v)
      }
    }
  }

  override fun serialize(encoder: Encoder, value: StaticScalarProperty) {
    val jsonEncoder = encoder as JsonEncoder
    jsonEncoder.encodeJsonElement(
      buildJsonObject {
        value.slotId?.let { put("sid", it) }
        put("a", 0)
        put("k", value.value)
      }
    )
  }
}

/** Serializer for [AnimatedScalarProperty] supporting keyframed scalar animations and slot IDs. */
internal object AnimatedScalarPropertySerializer : KSerializer<AnimatedScalarProperty> {
  override val descriptor: SerialDescriptor =
    buildClassSerialDescriptor("AnimatedScalarProperty") {
      element<String?>("sid", isOptional = true)
      element<Int>("a")
      element<List<ScalarPropertyKeyframe>>("k")
    }

  override fun deserialize(decoder: Decoder): AnimatedScalarProperty {
    val jsonDecoder = decoder as JsonDecoder
    val obj = jsonDecoder.decodeJsonElement().jsonObject
    val slotId = obj["sid"]?.jsonPrimitive?.contentOrNull
    val animatedInt = obj["a"]?.jsonPrimitive?.intOrNull ?: 1
    val keyframesArray = obj["k"]?.jsonArray
    val keyframes =
      keyframesArray?.map { element ->
        jsonDecoder.json.decodeFromJsonElement(ScalarPropertyKeyframeSerializer, element)
      } ?: emptyList()
    return AnimatedScalarProperty(slotId = slotId, animatedInt = animatedInt, keyframes = keyframes)
  }

  override fun serialize(encoder: Encoder, value: AnimatedScalarProperty) {
    val jsonEncoder = encoder as JsonEncoder
    jsonEncoder.encodeJsonElement(
      buildJsonObject {
        value.slotId?.let { put("sid", it) }
        put("a", value.animatedInt)
        put(
          "k",
          jsonEncoder.json.encodeToJsonElement(
            ListSerializer(ScalarPropertyKeyframeSerializer),
            value.keyframes,
          ),
        )
      }
    )
  }
}

/** Serializer for [ScalarPropertyKeyframe] handling scalar keyframes. */
internal object ScalarPropertyKeyframeSerializer : KSerializer<ScalarPropertyKeyframe> {
  override val descriptor: SerialDescriptor =
    buildClassSerialDescriptor("ScalarPropertyKeyframe") {
      element<Float>("t", isOptional = true)
      element<Boolean>("h", isOptional = true)
      element<ScalarKeyframeEasing?>("i", isOptional = true)
      element<ScalarKeyframeEasing?>("o", isOptional = true)
      element<Float>("s", isOptional = true)
    }

  override fun deserialize(decoder: Decoder): ScalarPropertyKeyframe {
    val jsonDecoder = decoder as JsonDecoder
    val obj = jsonDecoder.decodeJsonElement().jsonObject

    val frame = obj["t"]?.jsonPrimitive?.floatOrNull ?: 0f
    val hold =
      when (val hElem = obj["h"]) {
        is JsonPrimitive -> hElem.booleanOrNull ?: ((hElem.intOrNull ?: 0) == 1)
        else -> false
      }
    val inTangent =
      obj["i"]?.let { jsonDecoder.json.decodeFromJsonElement(ScalarKeyframeEasingSerializer, it) }
    val outTangent =
      obj["o"]?.let { jsonDecoder.json.decodeFromJsonElement(ScalarKeyframeEasingSerializer, it) }
    val sElem = obj["s"]
    val value = parseScalarElement(sElem)

    return ScalarPropertyKeyframe(
      frame = frame,
      hold = hold,
      inTangent = inTangent,
      outTangent = outTangent,
      value = value,
    )
  }

  override fun serialize(encoder: Encoder, value: ScalarPropertyKeyframe) {
    val jsonEncoder = encoder as JsonEncoder
    jsonEncoder.encodeJsonElement(
      buildJsonObject {
        put("t", value.frame)
        if (value.hold) put("h", 1)
        value.inTangent?.let {
          put("i", jsonEncoder.json.encodeToJsonElement(ScalarKeyframeEasingSerializer, it))
        }
        value.outTangent?.let {
          put("o", jsonEncoder.json.encodeToJsonElement(ScalarKeyframeEasingSerializer, it))
        }
        put("s", value.value)
      }
    )
  }
}

/** Serializer for [ScalarKeyframeEasing] handling numbers or 1-element arrays. */
internal object ScalarKeyframeEasingSerializer : KSerializer<ScalarKeyframeEasing> {
  override val descriptor: SerialDescriptor =
    buildClassSerialDescriptor("ScalarKeyframeEasing") {
      element<Float>("x")
      element<Float>("y")
    }

  override fun deserialize(decoder: Decoder): ScalarKeyframeEasing {
    val jsonDecoder = decoder as JsonDecoder
    val element = jsonDecoder.decodeJsonElement().jsonObject
    val x = parseTangentValue(element["x"])
    val y = parseTangentValue(element["y"])
    return ScalarKeyframeEasing(x, y)
  }

  private fun parseTangentValue(element: JsonElement?): Float {
    return when (element) {
      is JsonPrimitive -> element.floatOrNull ?: 0f
      is JsonArray -> element.firstOrNull()?.jsonPrimitive?.floatOrNull ?: 0f
      else -> 0f
    }
  }

  override fun serialize(encoder: Encoder, value: ScalarKeyframeEasing) {
    val jsonEncoder = encoder as JsonEncoder
    jsonEncoder.encodeJsonElement(
      buildJsonObject {
        put("x", value.x)
        put("y", value.y)
      }
    )
  }
}
