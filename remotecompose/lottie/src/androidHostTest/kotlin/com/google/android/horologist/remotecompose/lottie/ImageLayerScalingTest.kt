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

package com.google.android.horologist.remotecompose.lottie

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.util.Base64
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.android.horologist.remotecompose.lottie.format.asset.ImageAsset
import com.google.android.horologist.remotecompose.lottie.renderer.layers.decodeImageAsset
import com.google.common.truth.Truth.assertThat
import java.io.ByteArrayOutputStream
import org.junit.Test
import org.junit.runner.RunWith

@SuppressLint("RestrictedApi")
@RunWith(AndroidJUnit4::class)
class ImageLayerScalingTest {

  @Test
  fun decodeImageAsset_withBase64DataUrl_extractsNativeDimensions() {
    // Create a 4x8 bitmap and encode to PNG Base64
    val bitmap = Bitmap.createBitmap(4, 8, Bitmap.Config.ARGB_8888)
    val outputStream = ByteArrayOutputStream()
    bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
    val base64Data = Base64.encodeToString(outputStream.toByteArray(), Base64.NO_WRAP)
    val dataUrl = "data:image/png;base64,$base64Data"

    // Asset specifies composition dimensions 100x200 (different from native 4x8)
    val asset = ImageAsset(id = "image_0", width = 100f, height = 200f, path = dataUrl)

    val decoded = decodeImageAsset(asset)
    assertThat(decoded).isNotNull()
    assertThat(decoded!!.nativeWidth).isEqualTo(4f)
    assertThat(decoded.nativeHeight).isEqualTo(8f)
    assertThat(decoded.bitmap).isNotNull()
  }

  @Test
  fun decodeImageAsset_withHttpUrl_fallsBackToAssetDimensions() {
    val asset =
      ImageAsset(
        id = "image_remote",
        width = 300f,
        height = 150f,
        path = "https://example.com/assets/logo.png",
      )

    val decoded = decodeImageAsset(asset)
    assertThat(decoded).isNotNull()
    assertThat(decoded!!.nativeWidth).isEqualTo(300f)
    assertThat(decoded.nativeHeight).isEqualTo(150f)
    assertThat(decoded.bitmap).isNotNull()
  }

  @Test
  fun decodeImageAsset_withEmptyPath_returnsNull() {
    val asset = ImageAsset(id = "empty_asset", path = null, directory = null)
    val decoded = decodeImageAsset(asset)
    assertThat(decoded).isNull()
  }
}
