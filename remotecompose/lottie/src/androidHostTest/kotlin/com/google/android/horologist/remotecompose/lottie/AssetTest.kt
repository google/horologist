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

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.android.horologist.remotecompose.lottie.format.Animation
import com.google.android.horologist.remotecompose.lottie.format.asset.AudioAsset
import com.google.android.horologist.remotecompose.lottie.format.asset.ImageAsset
import com.google.android.horologist.remotecompose.lottie.format.asset.PrecompAsset
import com.google.android.horologist.remotecompose.lottie.format.asset.UnknownAsset
import com.google.android.horologist.remotecompose.lottie.format.layer.NullLayer
import com.google.common.truth.Truth.assertThat
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AssetTest {

  @Test
  fun decodeAnimation_withoutAssets_defaultsToEmptyList() {
    val json =
      """
      {
        "v": "5.9.6",
        "fr": 30.0,
        "ip": 0.0,
        "op": 60.0,
        "w": 100,
        "h": 100,
        "layers": []
      }
      """
        .trimIndent()

    val animation = Animation.decodeFromString(json)
    assertThat(animation.assets).isEmpty()
  }

  @Test
  fun decodeAnimation_withPrecompAsset() {
    val json =
      """
      {
        "v": "5.9.6",
        "fr": 30.0,
        "ip": 0.0,
        "op": 60.0,
        "w": 200,
        "h": 200,
        "assets": [
          {
            "id": "comp_0",
            "nm": "NestedSubcomp",
            "fr": 60.0,
            "xt": 1,
            "layers": [
              {
                "ty": 3,
                "nm": "NullLayerInsideComp",
                "ind": 1,
                "ip": 0.0,
                "op": 60.0
              }
            ]
          }
        ],
        "layers": []
      }
      """
        .trimIndent()

    val animation = Animation.decodeFromString(json)
    assertThat(animation.assets).hasSize(1)

    val asset = animation.assets[0]
    assertThat(asset).isInstanceOf(PrecompAsset::class.java)
    val precomp = asset as PrecompAsset
    assertThat(precomp.id).isEqualTo("comp_0")
    assertThat(precomp.name).isEqualTo("NestedSubcomp")
    assertThat(precomp.frameRate).isEqualTo(60.0f)
    assertThat(precomp.extra).isEqualTo(1)
    assertThat(precomp.layers).hasSize(1)
    assertThat(precomp.layers[0]).isInstanceOf(NullLayer::class.java)
    assertThat(precomp.layers[0].name).isEqualTo("NullLayerInsideComp")
  }

  @Test
  fun decodeAnimation_withImageAsset() {
    val json =
      """
      {
        "v": "5.9.6",
        "fr": 30.0,
        "ip": 0.0,
        "op": 60.0,
        "w": 200,
        "h": 200,
        "assets": [
          {
            "id": "image_0",
            "nm": "sample_image",
            "w": 320,
            "h": 240,
            "p": "sample.png",
            "u": "images/",
            "e": 0
          }
        ],
        "layers": []
      }
      """
        .trimIndent()

    val animation = Animation.decodeFromString(json)
    assertThat(animation.assets).hasSize(1)

    val asset = animation.assets[0]
    assertThat(asset).isInstanceOf(ImageAsset::class.java)
    val image = asset as ImageAsset
    assertThat(image.id).isEqualTo("image_0")
    assertThat(image.name).isEqualTo("sample_image")
    assertThat(image.width).isEqualTo(320f)
    assertThat(image.height).isEqualTo(240f)
    assertThat(image.path).isEqualTo("sample.png")
    assertThat(image.directory).isEqualTo("images/")
    assertThat(image.embedded).isEqualTo(0)
  }

  @Test
  fun decodeAnimation_withEmbeddedBase64ImageAsset() {
    val json =
      """
      {
        "v": "5.9.6",
        "fr": 30.0,
        "ip": 0.0,
        "op": 60.0,
        "w": 200,
        "h": 200,
        "assets": [
          {
            "id": "image_data",
            "w": 64,
            "h": 64,
            "p": "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAQAAAC1HAwCAAAAC0lEQVR42mNk+A8AAQUBAScY42YAAAAASUVORK5CYII=",
            "u": "",
            "e": 1
          }
        ],
        "layers": []
      }
      """
        .trimIndent()

    val animation = Animation.decodeFromString(json)
    assertThat(animation.assets).hasSize(1)

    val asset = animation.assets[0]
    assertThat(asset).isInstanceOf(ImageAsset::class.java)
    val image = asset as ImageAsset
    assertThat(image.id).isEqualTo("image_data")
    assertThat(image.width).isEqualTo(64f)
    assertThat(image.height).isEqualTo(64f)
    assertThat(image.path).startsWith("data:image/png;base64,")
    assertThat(image.embedded).isEqualTo(1)
  }

  @Test
  fun decodeAnimation_withAudioAsset() {
    val json =
      """
      {
        "v": "5.9.6",
        "fr": 30.0,
        "ip": 0.0,
        "op": 60.0,
        "w": 200,
        "h": 200,
        "assets": [
          {
            "id": "audio_0",
            "nm": "click_sound",
            "p": "click.mp3",
            "u": "audio/",
            "e": 0
          }
        ],
        "layers": []
      }
      """
        .trimIndent()

    val animation = Animation.decodeFromString(json)
    assertThat(animation.assets).hasSize(1)

    val asset = animation.assets[0]
    assertThat(asset).isInstanceOf(AudioAsset::class.java)
    val audio = asset as AudioAsset
    assertThat(audio.id).isEqualTo("audio_0")
    assertThat(audio.name).isEqualTo("click_sound")
    assertThat(audio.path).isEqualTo("click.mp3")
    assertThat(audio.directory).isEqualTo("audio/")
    assertThat(audio.embedded).isEqualTo(0)
  }

  @Test
  fun decodeAnimation_withHeterogeneousAssets() {
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
            "id": "comp_1",
            "layers": []
          },
          {
            "id": "image_1",
            "w": 100,
            "h": 100,
            "p": "icon.png"
          },
          {
            "id": "audio_1",
            "p": "music.mp3"
          },
          {
            "id": "unknown_1",
            "custom_property": 42
          }
        ],
        "layers": []
      }
      """
        .trimIndent()

    val animation = Animation.decodeFromString(json)
    assertThat(animation.assets).hasSize(4)
    assertThat(animation.assets[0]).isInstanceOf(PrecompAsset::class.java)
    assertThat(animation.assets[1]).isInstanceOf(ImageAsset::class.java)
    assertThat(animation.assets[2]).isInstanceOf(AudioAsset::class.java)
    assertThat(animation.assets[3]).isInstanceOf(UnknownAsset::class.java)
  }
}
