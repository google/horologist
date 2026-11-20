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

package com.google.android.horologist.remotecompose.lottie.renderer.layers

import android.content.Context
import android.graphics.BitmapFactory
import android.util.Base64
import androidx.compose.remote.creation.compose.state.RemoteImageBitmap
import androidx.compose.remote.creation.compose.state.rb
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import com.google.android.horologist.remotecompose.lottie.format.asset.ImageAsset

@Composable
internal actual fun rememberDecodedImage(asset: ImageAsset): DecodedImage? {
  val context = LocalContext.current
  return remember(asset, context) { decodeImageAsset(asset, context) }
}

/**
 * Decodes an [ImageAsset] into a [DecodedImage] supporting Base64 data URLs, HTTP/HTTPS URLs, and
 * local assets with native bitmap dimensions.
 */
internal fun decodeImageAsset(asset: ImageAsset, context: Context? = null): DecodedImage? {
  val path = asset.path.orEmpty()
  val dir = asset.directory.orEmpty()
  if (path.isEmpty() && dir.isEmpty()) {
    return null
  }

  val fullPath =
    if (
      path.startsWith("/") ||
        path.startsWith("http://", ignoreCase = true) ||
        path.startsWith("https://", ignoreCase = true) ||
        path.startsWith("data:", ignoreCase = true)
    ) {
      path
    } else {
      dir + path
    }

  if (
    fullPath.startsWith("data:image", ignoreCase = true) ||
      fullPath.startsWith("data:", ignoreCase = true) ||
      asset.embedded == 1
  ) {
    try {
      val base64Data = if (fullPath.contains(",")) fullPath.substringAfter(",") else fullPath
      if (base64Data.isBlank()) return null
      val bytes = Base64.decode(base64Data, Base64.DEFAULT)
      if (bytes == null || bytes.isEmpty()) return null
      val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
      if (bitmap != null) {
        return DecodedImage(
          bitmap = bitmap.asImageBitmap().rb,
          nativeWidth = bitmap.width.toFloat(),
          nativeHeight = bitmap.height.toFloat(),
        )
      }
    } catch (_: Exception) {
      return null
    }
    return null
  }

  if (
    fullPath.startsWith("http://", ignoreCase = true) ||
      fullPath.startsWith("https://", ignoreCase = true)
  ) {
    val declaredWidth = asset.width ?: 0f
    val declaredHeight = asset.height ?: 0f
    return DecodedImage(
      bitmap = RemoteImageBitmap(fullPath),
      nativeWidth = declaredWidth,
      nativeHeight = declaredHeight,
    )
  }

  if (context != null && fullPath.isNotEmpty()) {
    try {
      context.assets.open(fullPath).use { stream ->
        val bitmap = BitmapFactory.decodeStream(stream)
        if (bitmap != null) {
          return DecodedImage(
            bitmap = bitmap.asImageBitmap().rb,
            nativeWidth = bitmap.width.toFloat(),
            nativeHeight = bitmap.height.toFloat(),
          )
        }
      }
    } catch (_: Exception) {}

    try {
      val bitmap = BitmapFactory.decodeFile(fullPath)
      if (bitmap != null) {
        return DecodedImage(
          bitmap = bitmap.asImageBitmap().rb,
          nativeWidth = bitmap.width.toFloat(),
          nativeHeight = bitmap.height.toFloat(),
        )
      }
    } catch (_: Exception) {}
  }

  return null
}
