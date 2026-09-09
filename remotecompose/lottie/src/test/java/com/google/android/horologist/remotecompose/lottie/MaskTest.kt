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
import com.google.android.horologist.remotecompose.lottie.format.LottieDecoder
import com.google.android.horologist.remotecompose.lottie.format.layer.ShapeLayer
import com.google.android.horologist.remotecompose.lottie.format.mask.Mask
import com.google.android.horologist.remotecompose.lottie.format.mask.MaskMode
import com.google.android.horologist.remotecompose.lottie.format.properties.StaticScalarProperty
import com.google.common.truth.Truth.assertThat
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MaskTest {

  @Test
  fun maskMode_deserializesValidCodes() {
    assertThat(LottieDecoder.json.decodeFromString(MaskMode.serializer(), "\"n\""))
      .isEqualTo(MaskMode.None)
    assertThat(LottieDecoder.json.decodeFromString(MaskMode.serializer(), "\"a\""))
      .isEqualTo(MaskMode.Add)
    assertThat(LottieDecoder.json.decodeFromString(MaskMode.serializer(), "\"s\""))
      .isEqualTo(MaskMode.Subtract)
    assertThat(LottieDecoder.json.decodeFromString(MaskMode.serializer(), "\"i\""))
      .isEqualTo(MaskMode.Intersect)
  }

  @Test
  fun maskMode_serializesCanonicalCodes() {
    assertThat(LottieDecoder.json.encodeToString(MaskMode.serializer(), MaskMode.None))
      .isEqualTo("\"n\"")
    assertThat(LottieDecoder.json.encodeToString(MaskMode.serializer(), MaskMode.Add))
      .isEqualTo("\"a\"")
    assertThat(LottieDecoder.json.encodeToString(MaskMode.serializer(), MaskMode.Subtract))
      .isEqualTo("\"s\"")
    assertThat(LottieDecoder.json.encodeToString(MaskMode.serializer(), MaskMode.Intersect))
      .isEqualTo("\"i\"")
  }

  @Test
  fun maskMode_fallsBackToIntersectForUnknown() {
    assertThat(LottieDecoder.json.decodeFromString(MaskMode.serializer(), "\"unknown\""))
      .isEqualTo(MaskMode.Intersect)
  }

  @Test
  fun mask_deserializesCanonicalAttributes() {
    val json =
      """
      {
        "mode": "s",
        "o": {"a": 0, "k": 75.0},
        "pt": {
          "a": 0,
          "k": {
            "c": true,
            "i": [[0.0, 0.0]],
            "o": [[0.0, 0.0]],
            "v": [[10.0, 20.0]]
          }
        }
      }
      """
        .trimIndent()

    val mask = LottieDecoder.json.decodeFromString(Mask.serializer(), json)
    assertThat(mask.mode).isEqualTo(MaskMode.Subtract)
    assertThat(mask.opacity).isNotNull()
    assertThat(mask.path).isNotNull()
  }

  @Test
  fun mask_defaultsToIntersectAndFullOpacityWhenModeAndOpacityOmitted() {
    val json =
      """
      {
        "pt": {
          "a": 0,
          "k": {
            "c": true,
            "i": [[0.0, 0.0]],
            "o": [[0.0, 0.0]],
            "v": [[10.0, 20.0]]
          }
        }
      }
      """
        .trimIndent()

    val mask = LottieDecoder.json.decodeFromString(Mask.serializer(), json)
    assertThat(mask.mode).isEqualTo(MaskMode.Intersect)
    assertThat(mask.path).isNotNull()
    assertThat((mask.opacity as StaticScalarProperty).value.constantValue).isEqualTo(100f)
  }

  @Test
  fun mask_ignoresNonSpecFieldsGracefully() {
    val json =
      """
      {
        "nm": "Mask 1",
        "inv": false,
        "x": {"a": 0, "k": 0.0},
        "mode": "a",
        "o": {"a": 0, "k": 100.0}
      }
      """
        .trimIndent()

    val mask = LottieDecoder.json.decodeFromString(Mask.serializer(), json)
    assertThat(mask.mode).isEqualTo(MaskMode.Add)
    assertThat(mask.opacity).isNotNull()
    assertThat(mask.path).isNull()
  }

  @Test
  fun layer_deserializesMasksProperties() {
    val json =
      """
      {
        "ty": 4,
        "ip": 0,
        "op": 60,
        "masksProperties": [
          {
            "mode": "a",
            "o": {"a": 0, "k": 100.0}
          }
        ],
        "shapes": []
      }
      """
        .trimIndent()

    val layer = LottieDecoder.json.decodeFromString(ShapeLayer.serializer(), json)
    assertThat(layer.masks).isNotNull()
    assertThat(layer.masks).hasSize(1)
    assertThat(layer.masks!![0].mode).isEqualTo(MaskMode.Add)
  }
}
