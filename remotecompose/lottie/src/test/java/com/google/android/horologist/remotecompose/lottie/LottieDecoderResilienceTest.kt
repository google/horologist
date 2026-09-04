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
import com.google.android.horologist.remotecompose.lottie.format.LottieDecoder
import com.google.android.horologist.remotecompose.lottie.format.graphicelement.grouping.Group
import com.google.android.horologist.remotecompose.lottie.format.graphicelement.styles.Fill
import com.google.android.horologist.remotecompose.lottie.format.layer.NullLayer
import com.google.android.horologist.remotecompose.lottie.format.layer.ShapeLayer
import com.google.android.horologist.remotecompose.lottie.format.values.GradientValueSerializer
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
    assertThat(animation.layers[0]).isInstanceOf(NullLayer::class.java)
    assertThat(animation.layers[0].name).isEqualTo("UnsupportedAudioLayer")
    assertThat(animation.layers[1]).isInstanceOf(ShapeLayer::class.java)
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

    val shapeLayer = animation.layers[0] as ShapeLayer
    assertThat(shapeLayer.shapes).hasSize(2)
    assertThat(shapeLayer.shapes[0]).isInstanceOf(Group::class.java)
    assertThat(shapeLayer.shapes[1]).isInstanceOf(Fill::class.java)
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

    val shapeLayer = animation.layers[0] as ShapeLayer
    val fill1 = shapeLayer.shapes[0] as Fill
    val fill2 = shapeLayer.shapes[1] as Fill

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

    val shapeLayer = animation.layers[0] as ShapeLayer
    val fill1 = shapeLayer.shapes[0] as Fill
    val fill2 = shapeLayer.shapes[1] as Fill

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

  @Test
  fun gradientProperty_opaqueArray_samplesColorAtPositions() {
    val json = "[0.0, 1.0, 0.0, 0.0, 1.0, 0.0, 1.0, 0.0]"
    val gradient =
      LottieDecoder.json.decodeFromString(GradientValueSerializer(colorStopCount = 2), json)

    val colorStart = gradient.getColorForPosition(0.0f).constantValueOrNull
    assertThat(colorStart?.red).isEqualTo(1f)
    assertThat(colorStart?.green).isEqualTo(0f)
    assertThat(colorStart?.blue).isEqualTo(0f)
    assertThat(colorStart?.alpha).isEqualTo(1f)

    val colorMid = gradient.getColorForPosition(0.5f).constantValueOrNull
    assertThat(colorMid?.red).isWithin(0.01f).of(0.5f)
    assertThat(colorMid?.green).isWithin(0.01f).of(0.5f)
    assertThat(colorMid?.alpha).isEqualTo(1f)

    val colorEnd = gradient.getColorForPosition(1.0f).constantValueOrNull
    assertThat(colorEnd?.green).isEqualTo(1f)
    assertThat(colorEnd?.alpha).isEqualTo(1f)
  }

  @Test
  fun gradientProperty_transparentObject_samplesColorWithAlpha() {
    val json = "[0.0, 1.0, 0.0, 0.0, 1.0, 0.0, 0.0, 1.0, 0.0, 1.0, 1.0, 0.5]"
    val gradient =
      LottieDecoder.json.decodeFromString(GradientValueSerializer(colorStopCount = 2), json)

    val colorStart = gradient.getColorForPosition(0.0f).constantValueOrNull
    assertThat(colorStart?.red).isEqualTo(1f)
    assertThat(colorStart?.alpha).isEqualTo(1f)

    val colorEnd = gradient.getColorForPosition(1.0f).constantValueOrNull
    assertThat(colorEnd?.blue).isEqualTo(1f)
    assertThat(colorEnd?.alpha).isWithin(0.01f).of(0.5f)
  }

  @Test
  fun gradientProperty_scaled255Integers_samplesNormalizedColor() {
    val json = "[0.0, 255.0, 0.0, 0.0, 1.0, 0.0, 255.0, 0.0, 0.0, 255.0, 1.0, 128.0]"
    val gradient =
      LottieDecoder.json.decodeFromString(GradientValueSerializer(colorStopCount = 2), json)

    val colorStart = gradient.getColorForPosition(0.0f).constantValueOrNull
    assertThat(colorStart?.red).isEqualTo(1f)
    assertThat(colorStart?.alpha).isEqualTo(1f)

    val colorEnd = gradient.getColorForPosition(1.0f).constantValueOrNull
    assertThat(colorEnd?.green).isEqualTo(1f)
    assertThat(colorEnd?.alpha).isWithin(0.01f).of(128f / 255f)
  }

  @Test
  fun gradientProperty_independentStops_interpolatesAlongTimeline() {
    val json =
      """
      [
        0.0, 1.0, 0.0, 0.0,
        0.5, 0.0, 1.0, 0.0,
        1.0, 0.0, 0.0, 1.0,
        0.25, 0.8,
        0.75, 0.4
      ]
      """
        .trimIndent()
    val gradient =
      LottieDecoder.json.decodeFromString(GradientValueSerializer(colorStopCount = 3), json)

    val c0 = gradient.getColorForPosition(0.0f).constantValueOrNull
    assertThat(c0?.red).isWithin(0.01f).of(1.0f)
    assertThat(c0?.green).isWithin(0.01f).of(0.0f)
    assertThat(c0?.alpha).isWithin(0.01f).of(0.8f)

    val c025 = gradient.getColorForPosition(0.25f).constantValueOrNull
    assertThat(c025?.red).isWithin(0.01f).of(0.5f)
    assertThat(c025?.green).isWithin(0.01f).of(0.5f)
    assertThat(c025?.alpha).isWithin(0.01f).of(0.8f)

    val c05 = gradient.getColorForPosition(0.5f).constantValueOrNull
    assertThat(c05?.green).isWithin(0.01f).of(1.0f)
    assertThat(c05?.alpha).isWithin(0.01f).of(0.6f)

    val c075 = gradient.getColorForPosition(0.75f).constantValueOrNull
    assertThat(c075?.green).isWithin(0.01f).of(0.5f)
    assertThat(c075?.blue).isWithin(0.01f).of(0.5f)
    assertThat(c075?.alpha).isWithin(0.01f).of(0.4f)

    val c1 = gradient.getColorForPosition(1.0f).constantValueOrNull
    assertThat(c1?.blue).isWithin(0.01f).of(1.0f)
    assertThat(c1?.alpha).isWithin(0.01f).of(0.4f)
  }
}
