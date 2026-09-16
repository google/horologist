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

package com.google.android.horologist.remotecompose.lottie.format.graphicelement.modifiers

import androidx.compose.remote.creation.compose.state.rb
import com.google.android.horologist.remotecompose.lottie.format.graphicelement.ShapeType
import com.google.android.horologist.remotecompose.lottie.format.values.SerializableBoolean
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonDecoder
import kotlinx.serialization.json.floatOrNull
import kotlinx.serialization.json.intOrNull
import kotlinx.serialization.json.jsonPrimitive

/** Merge paths modifier */
@Serializable
internal data class MergePaths(
  @SerialName("nm") override val name: String? = "",
  @SerialName("hd") override val hidden: SerializableBoolean? = false.rb,
  @SerialName("ty") override val type: ShapeType = ShapeType.MergePaths,
  @SerialName("ix") override val index: Int? = null,
  @SerialName("mn") override val matchName: String? = null,
  @SerialName("cix") override val propertyIndex: Int? = null,
  @SerialName("mm") val mode: MergeMode = MergeMode.Merge,
) : ShapeModifier

@Serializable(with = MergeModeSerializer::class)
internal enum class MergeMode(val value: Int) {
  Merge(1),
  Add(2),
  Subtract(3),
  Intersect(4),
  ExcludeIntersections(5);

  companion object {
    fun fromValueOrNull(value: Int): MergeMode? = values().firstOrNull { it.value == value }
  }
}

internal object MergeModeSerializer : KSerializer<MergeMode> {
  override val descriptor: SerialDescriptor =
    PrimitiveSerialDescriptor("MergeMode", PrimitiveKind.INT)

  override fun deserialize(decoder: Decoder): MergeMode {
    return try {
      val jsonDecoder = decoder as? JsonDecoder
      if (jsonDecoder != null) {
        val element = jsonDecoder.decodeJsonElement()
        val intVal =
          element.jsonPrimitive.intOrNull ?: element.jsonPrimitive.floatOrNull?.toInt() ?: 1
        MergeMode.fromValueOrNull(intVal) ?: MergeMode.Merge
      } else {
        val value = decoder.decodeInt()
        MergeMode.fromValueOrNull(value) ?: MergeMode.Merge
      }
    } catch (e: Exception) {
      MergeMode.Merge
    }
  }

  override fun serialize(encoder: Encoder, value: MergeMode) {
    encoder.encodeInt(value.value)
  }
}
