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

import androidx.annotation.ColorInt
import androidx.compose.remote.creation.compose.state.RemoteColor
import androidx.compose.remote.creation.compose.state.rc
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.descriptors.element
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonDecoder
import kotlinx.serialization.json.JsonEncoder
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonArray
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.floatOrNull
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.put

/** A static color property is an array of floats with 3 or 4 values - r, g, b, a */
@Serializable(with = StaticColorPropertySerializer::class)
internal data class StaticColorProperty(
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
    val color = Color(value.colorInt)
    jsonEncoder.encodeJsonElement(
      buildJsonObject {
        value.slotId?.let { put("sid", it) }
        put("a", 0)
        put(
          "k",
          buildJsonArray {
            add(JsonPrimitive(color.red))
            add(JsonPrimitive(color.green))
            add(JsonPrimitive(color.blue))
            add(JsonPrimitive(color.alpha))
          },
        )
      }
    )
  }
}
