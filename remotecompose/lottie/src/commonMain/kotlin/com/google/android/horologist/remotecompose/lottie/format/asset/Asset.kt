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

package com.google.android.horologist.remotecompose.lottie.format.asset

import com.google.android.horologist.remotecompose.lottie.format.layer.Layer
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonContentPolymorphicSerializer
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

/**
 * An asset defined in the root `assets` array of a Lottie animation composition.
 *
 * Assets can be precompositions (nested layer hierarchies), bitmap images, or audio clips.
 */
@Serializable(with = AssetSerializer::class)
internal sealed class Asset {
  abstract val id: String
  abstract val name: String?
}

/** An asset representing a nested precomposition. */
@Serializable
internal data class PrecompAsset(
  @SerialName("id") override val id: String = "",
  @SerialName("nm") override val name: String? = null,
  @SerialName("layers") val layers: List<Layer> = emptyList(),
  @SerialName("fr") val frameRate: Float? = null,
  @SerialName("xt") val extra: Int? = null,
) : Asset()

/** An asset representing an external or embedded image. */
@Serializable
internal data class ImageAsset(
  @SerialName("id") override val id: String = "",
  @SerialName("nm") override val name: String? = null,
  @SerialName("w") val width: Float? = null,
  @SerialName("h") val height: Float? = null,
  @SerialName("p") val path: String? = null,
  @SerialName("u") val directory: String? = null,
  @SerialName("e") val embedded: Int? = null,
  @SerialName("t") val assetType: String? = null,
) : Asset()

/** An asset representing an external or embedded audio file. */
@Serializable
internal data class AudioAsset(
  @SerialName("id") override val id: String = "",
  @SerialName("nm") override val name: String? = null,
  @SerialName("p") val path: String? = null,
  @SerialName("u") val directory: String? = null,
  @SerialName("e") val embedded: Int? = null,
) : Asset()

/** Fallback for unrecognized or custom assets. */
@Serializable
internal data class UnknownAsset(
  @SerialName("id") override val id: String = "",
  @SerialName("nm") override val name: String? = null,
) : Asset()

internal object AssetSerializer : JsonContentPolymorphicSerializer<Asset>(Asset::class) {
  override fun selectDeserializer(element: JsonElement): DeserializationStrategy<Asset> {
    val jsonObject = element.jsonObject
    return when {
      jsonObject.containsKey("layers") -> PrecompAsset.serializer()
      jsonObject.containsKey("w") || jsonObject.containsKey("h") -> ImageAsset.serializer()
      jsonObject.containsKey("p") || jsonObject.containsKey("u") -> {
        val path = jsonObject["p"]?.jsonPrimitive?.contentOrNull.orEmpty()
        val type = jsonObject["t"]?.jsonPrimitive?.contentOrNull.orEmpty()
        if (
          type == "seq" ||
            type == "img" ||
            path.startsWith("data:image", ignoreCase = true) ||
            path.endsWith(".png", ignoreCase = true) ||
            path.endsWith(".jpg", ignoreCase = true) ||
            path.endsWith(".jpeg", ignoreCase = true) ||
            path.endsWith(".webp", ignoreCase = true) ||
            path.endsWith(".gif", ignoreCase = true)
        ) {
          ImageAsset.serializer()
        } else {
          AudioAsset.serializer()
        }
      }
      else -> UnknownAsset.serializer()
    }
  }
}
