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
import com.google.android.horologist.remotecompose.lottie.format.asset.Asset
import com.google.android.horologist.remotecompose.lottie.format.layer.Layer
import java.io.InputStream
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/** Top level object in a Lottie file, describing the animation. */
@Serializable
internal data class Animation(
  @SerialName("nm") val name: String? = null,
  @SerialName("v") val version: String? = "5.9.6",
  @SerialName("fr") val frameRate: Float = 30f,
  @SerialName("ip") val startFrame: Float = 0f,
  @SerialName("op") val endFrame: Float = 0f,
  @SerialName("w") val width: Int = 0,
  @SerialName("h") val height: Int = 0,
  @SerialName("assets") val assets: List<Asset> = emptyList(),
  @SerialName("layers") val layers: List<Layer> = emptyList(),
  @SerialName("markers") val markers: List<Marker> = emptyList(),
  @SerialName("fonts") val fonts: FontList? = null,
  @SerialName("chars") val chars: List<FontChar> = emptyList(),
) {
  companion object {
    /** Decodes an [Animation] from a JSON string using [LottieDecoder]. */
    fun decodeFromString(json: String): Animation = LottieDecoder.decodeFromString(json)

    /** Decodes an [Animation] from an [InputStream] using [LottieDecoder]. */
    fun decodeFromStream(stream: InputStream): Animation = LottieDecoder.decodeFromStream(stream)

    /** Decodes an [Animation] from a raw resource ID using [LottieDecoder]. */
    fun load(@RawRes rawRes: Int, context: Context): Animation = LottieDecoder.load(rawRes, context)
  }
}

/** Container for the fonts list in a Lottie composition. */
@Serializable internal data class FontList(@SerialName("list") val list: List<Font> = emptyList())

/** A font definition in a Lottie composition. */
@Serializable
internal data class Font(
  @SerialName("fName") val name: String = "",
  @SerialName("fFamily") val family: String = "",
  @SerialName("fStyle") val style: String = "",
  @SerialName("ascent") val ascent: Float? = null,
)

/** A vector character glyph definition in a Lottie composition. */
@Serializable
internal data class FontChar(
  @SerialName("ch") val character: String = "",
  @SerialName("fFamily") val family: String = "",
  @SerialName("style") val style: String = "",
  @SerialName("size") val size: Float = 0f,
  @SerialName("w") val width: Float = 0f,
  @SerialName("data") val shapeData: FontShapeData? = null,
)

/** Shape data container for a character glyph. */
@Serializable
internal data class FontShapeData(
  @SerialName("shapes")
  val shapes:
    List<com.google.android.horologist.remotecompose.lottie.format.graphicelement.GraphicElement> =
    emptyList()
)
