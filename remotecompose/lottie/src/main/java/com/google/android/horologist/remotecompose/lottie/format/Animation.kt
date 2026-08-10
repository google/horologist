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
import java.io.InputStream
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/** Top level object in a Lottie file, describing the animation. */
@Serializable
internal data class Animation(
  @SerialName("nm") val name: String? = null,
  @SerialName("ver") val version: String? = "0.0.0",
  @SerialName("fr") val frameRate: Int,
  @SerialName("ip") val startFrame: Int,
  @SerialName("op") val endFrame: Int,
  @SerialName("w") val width: Int,
  @SerialName("h") val height: Int,
  @SerialName("layers") val layers: List<Layer>,
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
