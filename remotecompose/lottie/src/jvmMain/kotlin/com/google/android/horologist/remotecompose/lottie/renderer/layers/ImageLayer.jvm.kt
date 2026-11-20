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

import androidx.compose.remote.creation.compose.state.RemoteImageBitmap
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.google.android.horologist.remotecompose.lottie.format.asset.ImageAsset

@Composable
internal actual fun rememberDecodedImage(asset: ImageAsset): DecodedImage? =
  remember(asset) {
    val path = asset.path.orEmpty()
    val dir = asset.directory.orEmpty()
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
      fullPath.startsWith("http://", ignoreCase = true) ||
        fullPath.startsWith("https://", ignoreCase = true)
    ) {
      DecodedImage(
        bitmap = RemoteImageBitmap(fullPath),
        nativeWidth = asset.width ?: 0f,
        nativeHeight = asset.height ?: 0f,
      )
    } else {
      null
    }
  }
