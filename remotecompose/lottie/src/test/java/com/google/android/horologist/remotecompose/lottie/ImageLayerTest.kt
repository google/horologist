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
import androidx.compose.remote.creation.compose.state.rf
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.android.horologist.remotecompose.lottie.format.Animation
import com.google.android.horologist.remotecompose.lottie.format.asset.ImageAsset
import com.google.android.horologist.remotecompose.lottie.format.asset.PrecompAsset
import com.google.android.horologist.remotecompose.lottie.format.layer.ImageLayer
import com.google.android.horologist.remotecompose.lottie.renderer.layers.decodeImageAsset
import com.google.common.truth.Truth.assertThat
import org.junit.Test
import org.junit.runner.RunWith

@SuppressLint("RestrictedApi")
@RunWith(AndroidJUnit4::class)
class ImageLayerTest {

  private val sample1x1PngDataUrl =
    "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAQAAAC1HAwCAAAAC0lEQVR42mNk+A8AAQUBAScY42YAAAAASUVORK5CYII="

  private val sampleRawBase64Png =
    "iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAQAAAC1HAwCAAAAC0lEQVR42mNk+A8AAQUBAScY42YAAAAASUVORK5CYII="

  @Test
  fun decodeImageAsset_dataUrlPng_returnsRemoteBitmap() {
    val asset = ImageAsset(id = "image_0", width = 100f, height = 100f, path = sample1x1PngDataUrl)
    val decoded = decodeImageAsset(asset, ApplicationProvider.getApplicationContext())
    assertThat(decoded).isNotNull()
    assertThat(decoded?.bitmap).isNotNull()
    assertThat(decoded?.nativeWidth).isEqualTo(1f)
    assertThat(decoded?.nativeHeight).isEqualTo(1f)
  }

  @Test
  fun decodeImageAsset_embeddedFlagWithRawBase64_returnsRemoteBitmap() {
    val asset =
      ImageAsset(
        id = "image_1",
        width = 100f,
        height = 100f,
        path = sampleRawBase64Png,
        embedded = 1,
      )
    val decoded = decodeImageAsset(asset, ApplicationProvider.getApplicationContext())
    assertThat(decoded).isNotNull()
    assertThat(decoded?.bitmap).isNotNull()
    assertThat(decoded?.nativeWidth).isEqualTo(1f)
    assertThat(decoded?.nativeHeight).isEqualTo(1f)
  }

  @Test
  fun decodeImageAsset_httpUrl_returnsRemoteBitmap() {
    val asset =
      ImageAsset(
        id = "image_remote",
        width = 200f,
        height = 200f,
        path = "https://example.com/image.png",
      )
    val bitmap = decodeImageAsset(asset, ApplicationProvider.getApplicationContext())
    assertThat(bitmap).isNotNull()
  }

  @Test
  fun decodeImageAsset_withDirectoryAndUrl_returnsRemoteBitmap() {
    val asset =
      ImageAsset(
        id = "image_dir_url",
        width = 200f,
        height = 200f,
        directory = "https://example.com/assets/",
        path = "hero.png",
      )
    val bitmap = decodeImageAsset(asset, ApplicationProvider.getApplicationContext())
    assertThat(bitmap).isNotNull()
  }

  @Test
  fun decodeImageAsset_invalidBase64_returnsNull() {
    val asset =
      ImageAsset(id = "invalid_image", width = 50f, height = 50f, path = "data:image/png;base64,")
    val bitmap = decodeImageAsset(asset, ApplicationProvider.getApplicationContext())
    assertThat(bitmap).isNull()
  }

  @Test
  fun decodeImageAsset_emptyPath_returnsNull() {
    val asset = ImageAsset(id = "empty_image", width = 50f, height = 50f, path = "")
    val bitmap = decodeImageAsset(asset, ApplicationProvider.getApplicationContext())
    assertThat(bitmap).isNull()
  }

  @Test
  fun animation_withImageLayer_decodesCorrectly() {
    val json =
      """
      {
        "v": "5.9.6",
        "fr": 30.0,
        "ip": 0.0,
        "op": 60.0,
        "w": 500,
        "h": 500,
        "assets": [
          {
            "id": "img_logo",
            "nm": "CompanyLogo",
            "w": 128,
            "h": 128,
            "p": "$sample1x1PngDataUrl",
            "e": 1
          }
        ],
        "layers": [
          {
            "ty": 2,
            "nm": "LogoLayer",
            "refId": "img_logo",
            "ind": 1,
            "ip": 0.0,
            "op": 60.0,
            "st": 0.0,
            "sr": 1.0,
            "ks": {
              "o": { "a": 0, "k": 80.0 },
              "p": { "a": 0, "k": [100.0, 150.0, 0.0] },
              "a": { "a": 0, "k": [64.0, 64.0, 0.0] },
              "s": { "a": 0, "k": [100.0, 100.0, 100.0] },
              "r": { "a": 0, "k": 0.0 }
            }
          }
        ]
      }
      """
        .trimIndent()

    val animation = Animation.decodeFromString(json)
    assertThat(animation.assets).hasSize(1)
    assertThat(animation.layers).hasSize(1)

    val imageAsset = animation.assets[0] as ImageAsset
    assertThat(imageAsset.id).isEqualTo("img_logo")
    assertThat(imageAsset.name).isEqualTo("CompanyLogo")
    assertThat(imageAsset.width).isEqualTo(128f)
    assertThat(imageAsset.height).isEqualTo(128f)

    val imageLayer = animation.layers[0] as ImageLayer
    assertThat(imageLayer.refId).isEqualTo("img_logo")
    assertThat(imageLayer.name).isEqualTo("LogoLayer")
    assertThat(imageLayer.index).isEqualTo(1)
    assertThat(imageLayer.startFrame.constantValue).isEqualTo(0f)
    assertThat(imageLayer.endFrame.constantValue).isEqualTo(60f)
  }

  @Test
  fun imageLayer_withMissingAsset_safelyHandledInSettings() {
    val settings = LottieSettings(currentFrame = 0f.rf, assets = emptyMap())
    val layer = ImageLayer(refId = "non_existent")
    val asset = settings.assets[layer.refId] as? ImageAsset
    assertThat(asset).isNull()
  }

  @Test
  fun imageLayer_withNonImageAsset_safelyHandledInSettings() {
    val precomp = PrecompAsset(id = "precomp_asset", layers = emptyList())
    val settings = LottieSettings(currentFrame = 0f.rf, assets = mapOf("precomp_asset" to precomp))
    val layer = ImageLayer(refId = "precomp_asset")
    val asset = settings.assets[layer.refId] as? ImageAsset
    assertThat(asset).isNull()
  }
}
