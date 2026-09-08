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
import com.google.android.horologist.remotecompose.lottie.format.values.GradientValue
import com.google.android.horologist.remotecompose.lottie.format.values.GradientValueSerializer
import com.google.android.horologist.remotecompose.lottie.format.values.KeyframeEasing
import com.google.android.horologist.remotecompose.lottie.format.values.ScalarKeyframeEasingSerializer
import com.google.android.horologist.remotecompose.lottie.format.values.SerializableBoolean
import com.google.android.horologist.remotecompose.lottie.format.values.SerializableRemoteBoolean
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
 * Base class for all Lottie animatable gradient properties conforming to
 * [Gradient Property](https://lottie.github.io/lottie-spec/1.0.1/specs/properties/#gradient-property).
 *
 * Unifies static constant gradients ([StaticGradientProperty]) and keyframed animations
 * ([AnimatedGradientProperty]) under a shared contract.
 */
@Serializable(with = BaseGradientPropertySerializer::class)
internal sealed class BaseGradientProperty {
  abstract val animated: SerializableRemoteBoolean
  abstract val slotId: String?
}

/** A static gradient property holding a [GradientValue]. */
@Serializable(with = StaticGradientPropertySerializer::class)
internal data class StaticGradientProperty(
  @SerialName("sid") override val slotId: String? = null,
  @SerialName("p") val numberOfColors: Int? = null,
  @SerialName("a") override val animated: SerializableRemoteBoolean = false.rb,
  @SerialName("k") val value: GradientValue,
) : BaseGradientProperty()

/** An animated gradient property with keyframes. */
@Serializable(with = AnimatedGradientPropertySerializer::class)
internal data class AnimatedGradientProperty(
  @SerialName("sid") override val slotId: String? = null,
  @SerialName("p") val numberOfColors: Int? = null,
  @SerialName("a") override val animated: SerializableRemoteBoolean = true.rb,
  @SerialName("k") val keyframes: List<GradientKeyframe> = emptyList(),
) : BaseGradientProperty()

/** A single keyframe for an animated gradient property. */
@Serializable(with = GradientKeyframeSerializer::class)
internal data class GradientKeyframe(
  @SerialName("t") val time: Float = 0f,
  @SerialName("h") val hold: SerializableBoolean? = null,
  @SerialName("i") val inTangent: KeyframeEasing? = null,
  @SerialName("o") val outTangent: KeyframeEasing? = null,
  @SerialName("s") val startValue: List<GradientValue> = emptyList(),
)

/** Polymorphic serializer for [BaseGradientProperty] based on "a" field. */
internal object BaseGradientPropertySerializer :
  JsonContentPolymorphicSerializer<BaseGradientProperty>(BaseGradientProperty::class) {
  override fun selectDeserializer(
    element: JsonElement
  ): DeserializationStrategy<BaseGradientProperty> {
    val animated = element is JsonObject && element["a"]?.jsonPrimitive?.intOrNull == 1
    return if (animated) {
      AnimatedGradientPropertySerializer
    } else {
      StaticGradientPropertySerializer
    }
  }
}

/** Serializer for [StaticGradientProperty] supporting slot IDs and raw gradient values. */
internal object StaticGradientPropertySerializer : KSerializer<StaticGradientProperty> {
  override val descriptor: SerialDescriptor =
    buildClassSerialDescriptor("StaticGradientProperty") {
      element<String?>("sid", isOptional = true)
      element<Int?>("p", isOptional = true)
      element<SerializableRemoteBoolean>("animated", isOptional = true)
      element<GradientValue>("k")
    }

  override fun deserialize(decoder: Decoder): StaticGradientProperty {
    val jsonDecoder = decoder as JsonDecoder
    val element = jsonDecoder.decodeJsonElement()
    return when (element) {
      is JsonObject -> {
        val slotId = element["sid"]?.jsonPrimitive?.contentOrNull
        val p = element["p"]?.jsonPrimitive?.intOrNull
        val kElem = element["k"]
        val count = p ?: 0
        val gradientValue =
          when (kElem) {
            is JsonArray -> {
              val stopCount =
                if (count > 0) count
                else if (kElem.size >= 4 && kElem.size % 4 == 0) kElem.size / 4 else 0
              jsonDecoder.json.decodeFromJsonElement(GradientValueSerializer(stopCount), kElem)
            }
            is JsonObject -> {
              val innerP = kElem["p"]?.jsonPrimitive?.intOrNull ?: count
              val innerK = kElem["k"]?.jsonArray ?: JsonArray(emptyList())
              val stopCount =
                if (innerP > 0) innerP
                else if (innerK.size >= 4 && innerK.size % 4 == 0) innerK.size / 4 else 0
              jsonDecoder.json.decodeFromJsonElement(GradientValueSerializer(stopCount), innerK)
            }
            else -> GradientValue(emptyList(), emptyList())
          }
        StaticGradientProperty(
          slotId = slotId,
          numberOfColors = p,
          animated = false.rb,
          value = gradientValue,
        )
      }
      is JsonArray -> {
        val stopCount = if (element.size >= 4 && element.size % 4 == 0) element.size / 4 else 0
        val gradientValue =
          jsonDecoder.json.decodeFromJsonElement(GradientValueSerializer(stopCount), element)
        StaticGradientProperty(
          slotId = null,
          numberOfColors = stopCount,
          animated = false.rb,
          value = gradientValue,
        )
      }
      else -> {
        StaticGradientProperty(
          slotId = null,
          numberOfColors = null,
          animated = false.rb,
          value = GradientValue(emptyList(), emptyList()),
        )
      }
    }
  }

  override fun serialize(encoder: Encoder, value: StaticGradientProperty) {
    val jsonEncoder = encoder as JsonEncoder
    jsonEncoder.encodeJsonElement(
      buildJsonObject {
        value.slotId?.let { put("sid", it) }
        value.numberOfColors?.let { put("p", it) }
        put("a", 0)
        put(
          "k",
          jsonEncoder.json.encodeToJsonElement(
            GradientValueSerializer(value.value.colorStops.size),
            value.value,
          ),
        )
      }
    )
  }
}

/** Serializer for [AnimatedGradientProperty] supporting slot IDs and keyframes. */
internal object AnimatedGradientPropertySerializer : KSerializer<AnimatedGradientProperty> {
  override val descriptor: SerialDescriptor =
    buildClassSerialDescriptor("AnimatedGradientProperty") {
      element<String?>("sid", isOptional = true)
      element<Int?>("p", isOptional = true)
      element<Int>("a")
      element<List<GradientKeyframe>>("k")
    }

  override fun deserialize(decoder: Decoder): AnimatedGradientProperty {
    val jsonDecoder = decoder as JsonDecoder
    val obj = jsonDecoder.decodeJsonElement().jsonObject
    val slotId = obj["sid"]?.jsonPrimitive?.contentOrNull
    val numberOfColors = obj["p"]?.jsonPrimitive?.intOrNull
    val p = numberOfColors ?: 0
    val keyframesArray = obj["k"]?.jsonArray
    val keyframes =
      keyframesArray?.map { element -> deserializeGradientKeyframe(jsonDecoder, element, p) }
        ?: emptyList()
    return AnimatedGradientProperty(
      slotId = slotId,
      numberOfColors = numberOfColors,
      animated = true.rb,
      keyframes = keyframes,
    )
  }

  override fun serialize(encoder: Encoder, value: AnimatedGradientProperty) {
    val jsonEncoder = encoder as JsonEncoder
    jsonEncoder.encodeJsonElement(
      buildJsonObject {
        value.slotId?.let { put("sid", it) }
        value.numberOfColors?.let { put("p", it) }
        put("a", 1)
        put(
          "k",
          jsonEncoder.json.encodeToJsonElement(
            ListSerializer(GradientKeyframeSerializer),
            value.keyframes,
          ),
        )
      }
    )
  }
}

private fun deserializeGradientKeyframe(
  jsonDecoder: JsonDecoder,
  element: JsonElement,
  colorStopCount: Int,
): GradientKeyframe {
  val obj = element.jsonObject
  val frame = obj["t"]?.jsonPrimitive?.floatOrNull ?: 0f
  val hold =
    when (val h = obj["h"]) {
      is JsonPrimitive -> (h.booleanOrNull ?: ((h.intOrNull ?: 0) == 1)).rb
      else -> null
    }
  val inTangent =
    obj["i"]?.let { jsonDecoder.json.decodeFromJsonElement(ScalarKeyframeEasingSerializer, it) }
  val outTangent =
    obj["o"]?.let { jsonDecoder.json.decodeFromJsonElement(ScalarKeyframeEasingSerializer, it) }
  val sElem = obj["s"]
  val startValue =
    when (sElem) {
      is JsonArray -> {
        if (sElem.isNotEmpty() && sElem.first() is JsonPrimitive) {
          val count =
            if (colorStopCount > 0) colorStopCount
            else if (sElem.size >= 4 && sElem.size % 4 == 0) sElem.size / 4 else 0
          listOf(jsonDecoder.json.decodeFromJsonElement(GradientValueSerializer(count), sElem))
        } else {
          sElem.map { inner ->
            val innerArr = inner.jsonArray
            val count =
              if (colorStopCount > 0) colorStopCount
              else if (innerArr.size >= 4 && innerArr.size % 4 == 0) innerArr.size / 4 else 0
            jsonDecoder.json.decodeFromJsonElement(GradientValueSerializer(count), inner)
          }
        }
      }
      is JsonObject -> {
        val innerArr = sElem["k"]?.jsonArray ?: JsonArray(emptyList())
        val count =
          if (colorStopCount > 0) colorStopCount
          else if (innerArr.size >= 4 && innerArr.size % 4 == 0) innerArr.size / 4 else 0
        listOf(jsonDecoder.json.decodeFromJsonElement(GradientValueSerializer(count), innerArr))
      }
      else -> emptyList()
    }

  return GradientKeyframe(
    time = frame,
    hold = hold,
    inTangent = inTangent,
    outTangent = outTangent,
    startValue = startValue,
  )
}

/** Serializer for [GradientKeyframe] handling timing, easing, and gradient values. */
internal object GradientKeyframeSerializer : KSerializer<GradientKeyframe> {
  override val descriptor: SerialDescriptor =
    buildClassSerialDescriptor("GradientKeyframe") {
      element<Float>("t", isOptional = true)
      element<SerializableBoolean?>("h", isOptional = true)
      element<KeyframeEasing?>("i", isOptional = true)
      element<KeyframeEasing?>("o", isOptional = true)
      element<List<GradientValue>>("s", isOptional = true)
    }

  override fun deserialize(decoder: Decoder): GradientKeyframe {
    val jsonDecoder = decoder as JsonDecoder
    return deserializeGradientKeyframe(jsonDecoder, jsonDecoder.decodeJsonElement(), 0)
  }

  override fun serialize(encoder: Encoder, value: GradientKeyframe) {
    val jsonEncoder = encoder as JsonEncoder
    jsonEncoder.encodeJsonElement(
      buildJsonObject {
        put("t", value.time)
        value.hold?.let { put("h", if (it.constantValue) 1 else 0) }
        value.inTangent?.let {
          put("i", jsonEncoder.json.encodeToJsonElement(ScalarKeyframeEasingSerializer, it))
        }
        value.outTangent?.let {
          put("o", jsonEncoder.json.encodeToJsonElement(ScalarKeyframeEasingSerializer, it))
        }
        if (value.startValue.isNotEmpty()) {
          val first = value.startValue.first()
          put(
            "s",
            jsonEncoder.json.encodeToJsonElement(
              GradientValueSerializer(first.colorStops.size),
              first,
            ),
          )
        }
      }
    )
  }
}
