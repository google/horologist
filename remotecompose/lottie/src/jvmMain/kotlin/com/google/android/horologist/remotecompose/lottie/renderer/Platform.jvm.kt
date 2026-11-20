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

package com.google.android.horologist.remotecompose.lottie.renderer

import androidx.compose.remote.core.CoreDocument
import androidx.compose.remote.creation.JvmRcPlatformServices
import androidx.compose.remote.creation.RemoteComposeWriter
import androidx.compose.remote.creation.profile.Profile
import java.awt.Canvas
import java.awt.Font
import kotlin.math.roundToInt

@Suppress("RestrictedApi")
internal actual fun defaultAndroidXProfile(): Profile =
  Profile(CoreDocument.DOCUMENT_API_LEVEL, CoreDocument.PROFILE, JvmRcPlatformServices()) {
    creationDisplayInfo,
    profile,
    callback ->
    RemoteComposeWriter(creationDisplayInfo, null, profile, callback)
  }

internal actual fun createTextMeasurer(
  fontFamily: String,
  weight: Int,
  isItalic: Boolean,
  fontSize: Float,
): (String) -> Float {
  val style =
    (if (weight >= 600) Font.BOLD else Font.PLAIN) or (if (isItalic) Font.ITALIC else Font.PLAIN)
  val name = fontFamily.ifEmpty { Font.SANS_SERIF }
  val metrics = Canvas().getFontMetrics(Font(name, style, fontSize.roundToInt().coerceAtLeast(1)))
  return { ch -> metrics.stringWidth(ch).toFloat() }
}
