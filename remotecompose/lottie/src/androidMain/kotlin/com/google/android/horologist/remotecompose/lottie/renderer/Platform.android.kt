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

import android.graphics.Paint
import android.graphics.Typeface
import androidx.compose.remote.creation.profile.Profile
import androidx.compose.remote.creation.profile.RcPlatformProfiles

internal actual fun defaultAndroidXProfile(): Profile = RcPlatformProfiles.ANDROIDX

internal actual fun createTextMeasurer(
  fontFamily: String,
  weight: Int,
  isItalic: Boolean,
  fontSize: Float,
): (String) -> Float {
  val style =
    when {
      weight >= 600 && isItalic -> Typeface.BOLD_ITALIC
      weight >= 600 -> Typeface.BOLD
      isItalic -> Typeface.ITALIC
      else -> Typeface.NORMAL
    }
  val resolvedTypeface =
    if (fontFamily.isNotEmpty()) Typeface.create(fontFamily, style)
    else Typeface.defaultFromStyle(style)
  val paint =
    Paint().apply {
      textSize = fontSize
      typeface = resolvedTypeface
    }
  return { ch -> paint.measureText(ch) }
}
