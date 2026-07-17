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

@file:Suppress("TopLevelName")

package com.google.android.horologist.remotecompose.lottie.format

import android.content.Context
import androidx.annotation.RawRes
import androidx.compose.remote.creation.compose.state.RemoteColor
import androidx.compose.remote.creation.compose.state.RemoteFloat
import androidx.compose.remote.creation.compose.state.rc
import androidx.compose.remote.creation.compose.state.rf
import androidx.compose.ui.graphics.Color
import com.squareup.moshi.FromJson
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.JsonReader
import com.squareup.moshi.JsonWriter
import com.squareup.moshi.Moshi
import com.squareup.moshi.ToJson
import com.squareup.moshi.adapters.PolymorphicJsonAdapterFactory

/** Moshi deserializer for Lottie animations. */
object LottieDeserializer {
  private val shapeTypeJsonAdapter =
    PolymorphicJsonAdapterFactory.of(GraphicElement::class.java, "ty")
      .withSubtype(GraphicElement.Path::class.java, "sh")
      .withSubtype(GraphicElement.Group::class.java, "gr")
      .withSubtype(GraphicElement.Transform::class.java, "tr")
      .withSubtype(GraphicElement.Fill::class.java, "fl")

  private val moshi =
    Moshi.Builder()
      .add(LayerTypeEnumJsonAdapter())
      .add(LayerJsonAdapter())
      .add(ShapeTypeEnumJsonAdapter())
      .add(shapeTypeJsonAdapter)
      .add(BaseBezierProperty::class.java, BaseBezierPropertyJsonAdapter)
      .add(BaseVectorProperty::class.java, BaseVectorPropertyJsonAdapter)
      .add(ScalarKeyframeEasingJsonAdapter)
      .add(RemoteFloat::class.java, RemoteFloatJsonAdapter)
      .add(RemoteColor::class.java, RemoteColorJsonAdapter())
      .build()

  val jsonAdapter: JsonAdapter<Animation> = moshi.adapter(Animation::class.java)

  fun load(@RawRes rawRes: Int, context: Context): Animation {
    val asset = context.resources.openRawResource(rawRes).readBytes()
    val deserializer = LottieDeserializer.jsonAdapter
    return deserializer.fromJson(asset.decodeToString())!!
  }

  class LayerTypeEnumJsonAdapter : JsonAdapter<LayerType>() {
    @FromJson
    override fun fromJson(reader: JsonReader): LayerType? {
      return if (reader.peek() == JsonReader.Token.NULL) {
        reader.nextNull()
      } else {
        val value = reader.nextInt()
        return LayerType.fromValueOrNull(value)
      }
    }

    @ToJson
    override fun toJson(writer: JsonWriter, value: LayerType?) {
      writer.value(value?.value)
    }
  }

  class ShapeTypeEnumJsonAdapter : JsonAdapter<ShapeType>() {
    @FromJson
    override fun fromJson(reader: JsonReader): ShapeType? {
      return if (reader.peek() == JsonReader.Token.NULL) {
        reader.nextNull()
      } else {
        val value = reader.nextString()
        return ShapeType.fromValueOrNull(value)
      }
    }

    @ToJson
    override fun toJson(writer: JsonWriter, value: ShapeType?) {
      writer.value(value?.value)
    }
  }

  /**
   * Json adapter for [Layer].
   *
   * This is similar to a PolymorphicJsonAdapterFactory, but it discriminates the layer based on an
   * integer value (the type property), rather than a string.
   */
  class LayerJsonAdapter : JsonAdapter<Layer>() {
    private val jsonAdapters: Map<Int, JsonAdapter<out Layer>> by lazy {
      mapOf(
        3 to moshi.adapter(Layer.NullLayer::class.java),
        4 to moshi.adapter(Layer.ShapeLayer::class.java),
      )
    }

    @FromJson
    override fun fromJson(reader: JsonReader): Layer? {
      val peeked = reader.peekJson()
      val adapter = jsonAdapters[getType(peeked)]
      return adapter?.fromJson(reader)
    }

    @ToJson
    override fun toJson(writer: JsonWriter, value: Layer?) {
      if (value == null) {
        writer.nullValue()
      } else {
        val flattenToken = writer.beginFlatten()
        @Suppress("UNCHECKED_CAST")
        val adapter = jsonAdapters[value.type.value] as JsonAdapter<Any>?
        adapter?.toJson(writer, value)
        writer.endFlatten(flattenToken)
        writer.endObject()
      }
    }

    private fun getType(reader: JsonReader): Int? {
      reader.beginObject()

      while (reader.hasNext()) {
        if (reader.nextName() == "ty") {
          return reader.nextInt()
        }

        reader.skipValue()
      }

      reader.endObject()
      return null
    }
  }

  object BaseBezierPropertyJsonAdapter :
    AnimatedPropertyJsonAdapter<BaseBezierProperty>(
      StaticBezierProperty::class.java,
      AnimatedBezierProperty::class.java,
    )

  object BaseVectorPropertyJsonAdapter :
    AnimatedPropertyJsonAdapter<BaseVectorProperty>(
      StaticVectorProperty::class.java,
      AnimatedVectorProperty::class.java,
    )

  /**
   * Json adapter for [AnimatableProperty].
   *
   * This adapter detects whether the property is animated or not, and delegates to the appropriate
   * adapter.
   */
  abstract class AnimatedPropertyJsonAdapter<T : AnimatableProperty>(
    private val static: Class<out T>,
    private val animated: Class<out T>,
  ) : JsonAdapter<T>() {
    private val staticAdapter by lazy { moshi.adapter(static) }
    private val animatedAdapter by lazy { moshi.adapter(animated) }

    @FromJson
    override fun fromJson(reader: JsonReader): T? {
      val peeked = reader.peekJson()
      val adapter = if (isAnimated(peeked)) animatedAdapter else staticAdapter
      return adapter.fromJson(reader)
    }

    @ToJson
    override fun toJson(writer: JsonWriter, value: T?) {
      if (value == null) {
        writer.nullValue()
      } else {
        val flattenToken = writer.beginFlatten()
        @Suppress("UNCHECKED_CAST")
        val adapter = (if (value.animated) animatedAdapter else staticAdapter) as JsonAdapter<Any>
        adapter.toJson(writer, value)
        writer.endFlatten(flattenToken)
        writer.endObject()
      }
    }

    private fun isAnimated(reader: JsonReader): Boolean {
      reader.beginObject()

      while (reader.hasNext()) {
        if (reader.nextName() == "a") {
          return reader.nextInt() == 1
        }

        reader.skipValue()
      }

      reader.endObject()
      return false
    }
  }

  /**
   * Json adapter for [ScalarKeyframeEasing].
   *
   * The x and y values of ScalarKeyframeEaasings are sometimes encoded as numbers, and sometimes as
   * arrays. This adapter detects which format is used and decodes accordingly (taking the first
   * element if it's an array)
   */
  object ScalarKeyframeEasingJsonAdapter : JsonAdapter<ScalarKeyframeEasing>() {
    private val arrayAdapter by lazy { moshi.adapter(FloatArray::class.java) }

    @FromJson
    override fun fromJson(reader: JsonReader): ScalarKeyframeEasing? {
      var x: Float? = null
      var y: Float? = null
      var currentProp = ""

      while (reader.hasNext()) {
        val nextToken = reader.peek()
        if (nextToken == JsonReader.Token.BEGIN_OBJECT) {
          reader.beginObject()
        } else if (nextToken == JsonReader.Token.NAME) {
          currentProp = reader.nextName()
        } else if (nextToken == JsonReader.Token.BEGIN_ARRAY) {
          val value = arrayAdapter.fromJson(reader)?.first()
          if (currentProp == "x") {
            x = value
          } else if (currentProp == "y") {
            y = value
          }
        } else if (nextToken == JsonReader.Token.NUMBER) {
          if (currentProp == "x") {
            x = reader.nextDouble().toFloat()
          } else if (currentProp == "y") {
            y = reader.nextDouble().toFloat()
          }
        } else {
          reader.skipValue()
        }
      }

      reader.endObject()

      return if (x == null || y == null) {
        null
      } else {
        ScalarKeyframeEasing(x = x, y = y)
      }
    }

    @ToJson
    override fun toJson(writer: JsonWriter, value: ScalarKeyframeEasing?) {
      writer.beginObject()
      writer.name("x")
      writer.value(value?.x)
      writer.name("y")
      writer.value(value?.y)
      writer.endObject()
    }
  }

  object RemoteFloatJsonAdapter : JsonAdapter<RemoteFloat>() {
    @FromJson
    override fun fromJson(reader: JsonReader): RemoteFloat? {
      return if (reader.peek() == JsonReader.Token.NULL) {
        reader.nextNull()
      } else {
        reader.nextDouble().toFloat().rf
      }
    }

    @ToJson
    override fun toJson(writer: JsonWriter, value: RemoteFloat?) {
      writer.value(0.0)
    }
  }

  class RemoteColorJsonAdapter : JsonAdapter<RemoteColor>() {
    @FromJson
    override fun fromJson(reader: JsonReader): RemoteColor? {
      if (reader.peek() == JsonReader.Token.NULL) {
        return reader.nextNull()
      }
      reader.beginArray()
      val r = reader.nextDouble().toFloat()
      val g = reader.nextDouble().toFloat()
      val b = reader.nextDouble().toFloat()
      val a = if (reader.hasNext()) reader.nextDouble().toFloat() else 1f
      reader.endArray()
      return Color(r, g, b, a).rc
    }

    @ToJson
    override fun toJson(writer: JsonWriter, value: RemoteColor?) {
      writer.nullValue()
    }
  }
}
