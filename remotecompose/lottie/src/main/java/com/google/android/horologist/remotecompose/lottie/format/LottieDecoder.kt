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
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream

/** `kotlinx.serialization` JSON decoder for Lottie animations. */
internal object LottieDecoder {

  val json = Json {
    ignoreUnknownKeys = true
    isLenient = true
    coerceInputValues = true
    encodeDefaults = true
    explicitNulls = false
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
