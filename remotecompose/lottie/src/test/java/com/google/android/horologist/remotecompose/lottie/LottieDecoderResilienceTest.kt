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
import com.google.android.horologist.remotecompose.lottie.format.GraphicElement
import com.google.android.horologist.remotecompose.lottie.format.Layer
import com.google.common.truth.Truth.assertThat
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LottieDecoderResilienceTest {

  @Test
  fun unknownLayerType_deserializesAsNullLayerFallback() {
    val json =
      """
      {
        "v": "5.7.0",
        "fr": 60,
        "ip": 0,
        "op": 60,
        "w": 100,
        "h": 100,
        "layers": [
          { "ty": 999, "nm": "UnsupportedAudioLayer", "ind": 1 },
          { "ty": 4, "nm": "ValidShapeLayer", "ind": 2, "shapes": [] }
        ]
      }
      """
        .trimIndent()

    val animation = Animation.decodeFromString(json)

    assertThat(animation.layers).hasSize(2)
    assertThat(animation.layers[0]).isInstanceOf(Layer.NullLayer::class.java)
    assertThat(animation.layers[0].name).isEqualTo("UnsupportedAudioLayer")
    assertThat(animation.layers[1]).isInstanceOf(Layer.ShapeLayer::class.java)
  }

  @Test
  fun unknownShapeType_deserializesAsGroupFallback() {
    val json =
      """
      {
        "v": "5.7.0",
        "fr": 30,
        "ip": 0,
        "op": 30,
        "w": 50,
        "h": 50,
        "layers": [
          {
            "ty": 4,
            "nm": "ShapeLayer",
            "shapes": [
              { "ty": "unknown_shape_type", "nm": "CustomShape", "it": [] },
              {
                "ty": "fl",
                "nm": "RedFill",
                "c": { "k": [1.0, 0.0, 0.0, 1.0] }
              }
            ]
          }
        ]
      }
      """
        .trimIndent()

    val animation = Animation.decodeFromString(json)

    val shapeLayer = animation.layers[0] as Layer.ShapeLayer
    assertThat(shapeLayer.shapes).hasSize(2)
    assertThat(shapeLayer.shapes[0]).isInstanceOf(GraphicElement.Group::class.java)
    assertThat(shapeLayer.shapes[1]).isInstanceOf(GraphicElement.Fill::class.java)
  }

  @Test
  fun colorProperty_handlesRgbRgbaAndScaledIntegers() {
    val json =
      """
      {
        "v": "5.7.0",
        "fr": 30,
        "ip": 0,
        "op": 30,
        "w": 50,
        "h": 50,
        "layers": [
          {
            "ty": 4,
            "nm": "ShapeLayer",
            "shapes": [
              {
                "ty": "fl",
                "nm": "RgbFill",
                "c": { "k": [1.0, 0.5, 0.0] }
              },
              {
                "ty": "fl",
                "nm": "ScaledIntFill",
                "c": { "k": [255, 128, 0, 255] }
              }
            ]
          }
        ]
      }
      """
        .trimIndent()

    val animation = Animation.decodeFromString(json)

    val shapeLayer = animation.layers[0] as Layer.ShapeLayer
    val fill1 = shapeLayer.shapes[0] as GraphicElement.Fill
    val fill2 = shapeLayer.shapes[1] as GraphicElement.Fill

    assertThat(fill1.color.value).isNotNull()
    assertThat(fill2.color.value).isNotNull()
  }

  @Test
  fun colorProperty_parsesSlotId() {
    val json =
      """
      {
        "v": "5.7.0",
        "fr": 30,
        "ip": 0,
        "op": 30,
        "w": 50,
        "h": 50,
        "layers": [
          {
            "ty": 4,
            "nm": "ShapeLayer",
            "shapes": [
              {
                "ty": "fl",
                "nm": "SidFill",
                "c": { "sid": "color.primary", "k": [1.0, 0.0, 0.0, 1.0] }
              },
              {
                "ty": "fl",
                "nm": "DefaultFill",
                "c": { "k": [0.0, 1.0, 0.0, 1.0] }
              }
            ]
          }
        ]
      }
      """
        .trimIndent()

    val animation = Animation.decodeFromString(json)

    val shapeLayer = animation.layers[0] as Layer.ShapeLayer
    val fill1 = shapeLayer.shapes[0] as GraphicElement.Fill
    val fill2 = shapeLayer.shapes[1] as GraphicElement.Fill

    assertThat(fill1.color.slotId).isEqualTo("color.primary")
    assertThat(fill2.color.slotId).isNull()

    val slotMap = SlotMap(mapOf("color.primary" to 0xFF00FF00.toInt()))
    assertThat(slotMap.getColor("color.primary")).isNotNull()
    assertThat(slotMap.getColor("unknown")).isNull()
  }

  @Test
  fun extraPluginMetadata_ignoredCleanly() {
    val json =
      """
      {
        "v": "5.7.0",
        "fr": 30,
        "ip": 0,
        "op": 30,
        "w": 50,
        "h": 50,
        "meta": { "g": "LottieFiles 2.0" },
        "_ae_version": "17.5.0",
        "layers": []
      }
      """
        .trimIndent()

    val animation = Animation.decodeFromString(json)

    assertThat(animation.frameRate).isEqualTo(30)
    assertThat(animation.layers).isEmpty()
  }
}
