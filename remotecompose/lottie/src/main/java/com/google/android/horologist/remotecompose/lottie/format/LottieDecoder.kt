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

import android.content.Context
import androidx.annotation.RawRes
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import java.io.InputStream
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.descriptors.element
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonContentPolymorphicSerializer
import kotlinx.serialization.json.JsonDecoder
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonEncoder
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.decodeFromStream
import kotlinx.serialization.json.float
import kotlinx.serialization.json.floatOrNull
import kotlinx.serialization.json.intOrNull
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.put

/** `kotlinx.serialization` JSON decoder for Lottie animations. */
internal object LottieDecoder {

  val json = Json {
    ignoreUnknownKeys = true
    isLenient = true
    coerceInputValues = true
  }

  fun decodeFromString(jsonString: String): Animation {
    return json.decodeFromString(Animation.serializer(), jsonString)
  }

  @OptIn(ExperimentalSerializationApi::class)
  fun decodeFromStream(stream: InputStream): Animation {
    return json.decodeFromStream(Animation.serializer(), stream)
  }

  fun load(@RawRes rawRes: Int, context: Context): Animation {
    return context.resources.openRawResource(rawRes).use { stream -> decodeFromStream(stream) }
  }
}

/** Polymorphic serializer for [Layer] based on integer "ty" field. */
internal object LayerSerializer : JsonContentPolymorphicSerializer<Layer>(Layer::class) {
  override fun selectDeserializer(element: JsonElement): DeserializationStrategy<Layer> {
    val ty = element.jsonObject["ty"]?.jsonPrimitive?.intOrNull
    return when (ty) {
      LayerType.Null.value -> Layer.NullLayer.serializer()
      LayerType.Shape.value -> Layer.ShapeLayer.serializer()
      else -> Layer.NullLayer.serializer()
    }
  }
}

/** Serializer for [LayerType] enum. */
internal object LayerTypeSerializer : KSerializer<LayerType> {
  override val descriptor: SerialDescriptor =
    PrimitiveSerialDescriptor("LayerType", PrimitiveKind.INT)

  override fun deserialize(decoder: Decoder): LayerType {
    val value = decoder.decodeInt()
    return LayerType.fromValueOrNull(value) ?: LayerType.Null
  }

  override fun serialize(encoder: Encoder, value: LayerType) {
    encoder.encodeInt(value.value)
  }
}

/** Polymorphic serializer for [GraphicElement] based on string "ty" field. */
internal object GraphicElementSerializer :
  JsonContentPolymorphicSerializer<GraphicElement>(GraphicElement::class) {
  override fun selectDeserializer(element: JsonElement): DeserializationStrategy<GraphicElement> {
    val ty = element.jsonObject["ty"]?.jsonPrimitive?.contentOrNull
    return when (ty) {
      ShapeType.Path.value -> GraphicElement.Path.serializer()
      ShapeType.Group.value -> GraphicElement.Group.serializer()
      ShapeType.Transform.value -> GraphicElement.Transform.serializer()
      ShapeType.Fill.value -> GraphicElement.Fill.serializer()
      ShapeType.Rectangle.value -> GraphicElement.Rectangle.serializer()
      ShapeType.Ellipse.value -> GraphicElement.Ellipse.serializer()
      ShapeType.PolyStar.value -> GraphicElement.PolyStar.serializer()
      else -> GraphicElement.Group.serializer()
    }
  }
}

/** Serializer for [ShapeType] enum. */
internal object ShapeTypeSerializer : KSerializer<ShapeType> {
  override val descriptor: SerialDescriptor =
    PrimitiveSerialDescriptor("ShapeType", PrimitiveKind.STRING)

  override fun deserialize(decoder: Decoder): ShapeType {
    val value = decoder.decodeString()
    return ShapeType.fromValueOrNull(value) ?: ShapeType.Group
  }

  override fun serialize(encoder: Encoder, value: ShapeType) {
    encoder.encodeString(value.value)
  }
}

/** Serializer for [PolyStarType] enum. */
internal object PolyStarTypeSerializer : KSerializer<PolyStarType> {
  override val descriptor: SerialDescriptor =
    PrimitiveSerialDescriptor("PolyStarType", PrimitiveKind.INT)

  override fun deserialize(decoder: Decoder): PolyStarType {
    val value = decoder.decodeInt()
    return PolyStarType.fromValueOrNull(value) ?: PolyStarType.Star
  }

  override fun serialize(encoder: Encoder, value: PolyStarType) {
    encoder.encodeInt(value.value)
  }
}

/** Polymorphic serializer for [BaseScalarProperty] based on "a" field. */
internal object BaseScalarPropertySerializer :
  JsonContentPolymorphicSerializer<BaseScalarProperty>(BaseScalarProperty::class) {
  override fun selectDeserializer(
    element: JsonElement
  ): DeserializationStrategy<BaseScalarProperty> {
    return when (element) {
      is JsonPrimitive -> StaticScalarProperty.serializer()
      is JsonArray -> StaticScalarProperty.serializer()
      is JsonObject -> {
        val animated = element["a"]?.jsonPrimitive?.intOrNull == 1
        if (animated) {
          AnimatedScalarProperty.serializer()
        } else {
          StaticScalarProperty.serializer()
        }
      }
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
      is JsonPrimitive -> {
        StaticScalarProperty(value = element.floatOrNull ?: 0f)
      }
      is JsonArray -> {
        val v = element.firstOrNull()?.jsonPrimitive?.floatOrNull ?: 0f
        StaticScalarProperty(value = v)
      }
      is JsonObject -> {
        val slotId = element["sid"]?.jsonPrimitive?.contentOrNull
        val kElem = element["k"]
        val v =
          when (kElem) {
            is JsonPrimitive -> kElem.floatOrNull ?: 0f
            is JsonArray -> kElem.firstOrNull()?.jsonPrimitive?.floatOrNull ?: 0f
            else -> 0f
          }
        StaticScalarProperty(slotId = slotId, animated = false, value = v)
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
    val hold = (obj["h"]?.jsonPrimitive?.intOrNull ?: 0) == 1
    val inTangent =
      obj["i"]?.let { jsonDecoder.json.decodeFromJsonElement(ScalarKeyframeEasingSerializer, it) }
    val outTangent =
      obj["o"]?.let { jsonDecoder.json.decodeFromJsonElement(ScalarKeyframeEasingSerializer, it) }
    val sElem = obj["s"]
    val value =
      when (sElem) {
        is JsonPrimitive -> sElem.floatOrNull ?: 0f
        is JsonArray -> sElem.firstOrNull()?.jsonPrimitive?.floatOrNull ?: 0f
        else -> 0f
      }

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

/** Polymorphic serializer for [BasePositionProperty] based on "a" field. */
internal object BasePositionPropertySerializer :
  JsonContentPolymorphicSerializer<BasePositionProperty>(BasePositionProperty::class) {
  override fun selectDeserializer(
    element: JsonElement
  ): DeserializationStrategy<BasePositionProperty> {
    val animated = element.jsonObject["a"]?.jsonPrimitive?.intOrNull == 1
    return if (animated) {
      AnimatedPositionProperty.serializer()
    } else {
      StaticPositionProperty.serializer()
    }
  }
}

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
      is JsonPrimitive -> element.float
      is JsonArray -> element.firstOrNull()?.jsonPrimitive?.float ?: 0f
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

/** Custom serializer for [StaticColorProperty] parsing color array [r, g, b, a]. */
internal object StaticColorPropertySerializer : KSerializer<StaticColorProperty> {
  override val descriptor: SerialDescriptor =
    buildClassSerialDescriptor("StaticColorProperty") {
      element<String?>("sid", isOptional = true)
      element<Boolean>("animated", isOptional = true)
      element<List<Float>>("k")
    }

  override fun deserialize(decoder: Decoder): StaticColorProperty {
    val jsonDecoder = decoder as JsonDecoder
    val obj = jsonDecoder.decodeJsonElement().jsonObject
    val slotId = obj["sid"]?.jsonPrimitive?.contentOrNull
    val kArray = obj["k"]?.jsonArray
    val r = kArray?.getOrNull(0)?.jsonPrimitive?.floatOrNull ?: 0f
    val g = kArray?.getOrNull(1)?.jsonPrimitive?.floatOrNull ?: 0f
    val b = kArray?.getOrNull(2)?.jsonPrimitive?.floatOrNull ?: 0f
    val a = if ((kArray?.size ?: 0) > 3) kArray!![3].jsonPrimitive.floatOrNull ?: 1f else 1f

    val red = if (r > 1f) (r / 255f).coerceIn(0f, 1f) else r.coerceIn(0f, 1f)
    val green = if (g > 1f) (g / 255f).coerceIn(0f, 1f) else g.coerceIn(0f, 1f)
    val blue = if (b > 1f) (b / 255f).coerceIn(0f, 1f) else b.coerceIn(0f, 1f)
    val alpha = if (a > 1f) (a / 255f).coerceIn(0f, 1f) else a.coerceIn(0f, 1f)

    val color = Color(red, green, blue, alpha)
    return StaticColorProperty(slotId = slotId, colorInt = color.toArgb())
  }

  override fun serialize(encoder: Encoder, value: StaticColorProperty) {
    val jsonEncoder = encoder as JsonEncoder
    jsonEncoder.encodeJsonElement(
      buildJsonObject {
        value.slotId?.let { put("sid", it) }
        put("animated", false)
      }
    )
  }
}
