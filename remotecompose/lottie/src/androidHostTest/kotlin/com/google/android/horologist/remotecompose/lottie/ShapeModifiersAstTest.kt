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
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.android.horologist.remotecompose.lottie.format.Animation
import com.google.android.horologist.remotecompose.lottie.format.graphicelement.ShapeType
import com.google.android.horologist.remotecompose.lottie.format.graphicelement.modifiers.CompositeMode
import com.google.android.horologist.remotecompose.lottie.format.graphicelement.modifiers.MergeMode
import com.google.android.horologist.remotecompose.lottie.format.graphicelement.modifiers.MergePaths
import com.google.android.horologist.remotecompose.lottie.format.graphicelement.modifiers.OffsetPath
import com.google.android.horologist.remotecompose.lottie.format.graphicelement.modifiers.PuckerBloat
import com.google.android.horologist.remotecompose.lottie.format.graphicelement.modifiers.Repeater
import com.google.android.horologist.remotecompose.lottie.format.graphicelement.modifiers.RoundedCorners
import com.google.android.horologist.remotecompose.lottie.format.graphicelement.modifiers.TrimMode
import com.google.android.horologist.remotecompose.lottie.format.graphicelement.modifiers.TrimPath
import com.google.android.horologist.remotecompose.lottie.format.graphicelement.modifiers.Twist
import com.google.android.horologist.remotecompose.lottie.format.graphicelement.modifiers.UnknownElement
import com.google.android.horologist.remotecompose.lottie.format.graphicelement.modifiers.ZigZag
import com.google.android.horologist.remotecompose.lottie.format.graphicelement.styles.LineJoin
import com.google.android.horologist.remotecompose.lottie.format.graphicelement.styles.NoStyle
import com.google.android.horologist.remotecompose.lottie.format.layer.ShapeLayer
import com.google.android.horologist.remotecompose.lottie.format.properties.StaticPositionProperty
import com.google.android.horologist.remotecompose.lottie.format.properties.StaticScalarProperty
import com.google.common.truth.Truth.assertThat
import org.junit.Test
import org.junit.runner.RunWith

@SuppressLint("RestrictedApi")
@RunWith(AndroidJUnit4::class)
class ShapeModifiersAstTest {

  @Test
  fun trimPath_decodesStaticAndAnimatedProperties() {
    val json =
      """
      {
        "v": "5.9.6",
        "fr": 30.0,
        "ip": 0.0,
        "op": 60.0,
        "w": 100,
        "h": 100,
        "layers": [
          {
            "ty": 4,
            "ind": 1,
            "ip": 0.0,
            "op": 60.0,
            "shapes": [
              {
                "ty": "tm",
                "nm": "Trim Path 1",
                "s": { "a": 0, "k": 10.0 },
                "e": { "a": 0, "k": 85.0 },
                "o": { "a": 0, "k": 45.0 },
                "m": 2
              }
            ]
          }
        ]
      }
      """
        .trimIndent()

    val animation = Animation.decodeFromString(json)
    val shapeLayer = animation.layers[0] as ShapeLayer
    val trimPath = shapeLayer.shapes[0] as TrimPath

    assertThat(trimPath.name).isEqualTo("Trim Path 1")
    assertThat(trimPath.type).isEqualTo(ShapeType.TrimPath)
    assertThat((trimPath.start as StaticScalarProperty).value.constantValue).isEqualTo(10f)
    assertThat((trimPath.end as StaticScalarProperty).value.constantValue).isEqualTo(85f)
    assertThat((trimPath.offset as StaticScalarProperty).value.constantValue).isEqualTo(45f)
    assertThat(trimPath.mode).isEqualTo(TrimMode.Individually)
  }

  @Test
  fun roundedCorners_decodesRadiusProperty() {
    val json =
      """
      {
        "v": "5.9.6",
        "fr": 30.0,
        "ip": 0.0,
        "op": 60.0,
        "w": 100,
        "h": 100,
        "layers": [
          {
            "ty": 4,
            "ind": 1,
            "ip": 0.0,
            "op": 60.0,
            "shapes": [
              {
                "ty": "rd",
                "nm": "Rounded Corners 1",
                "r": { "a": 0, "k": 16.5 }
              }
            ]
          }
        ]
      }
      """
        .trimIndent()

    val animation = Animation.decodeFromString(json)
    val shapeLayer = animation.layers[0] as ShapeLayer
    val roundedCorners = shapeLayer.shapes[0] as RoundedCorners

    assertThat(roundedCorners.name).isEqualTo("Rounded Corners 1")
    assertThat(roundedCorners.type).isEqualTo(ShapeType.RoundedCorners)
    assertThat((roundedCorners.radius as StaticScalarProperty).value.constantValue).isEqualTo(16.5f)
  }

  @Test
  fun mergePaths_decodesAllMergeModes() {
    val modes =
      listOf(
        1 to MergeMode.Merge,
        2 to MergeMode.Add,
        3 to MergeMode.Subtract,
        4 to MergeMode.Intersect,
        5 to MergeMode.ExcludeIntersections,
      )

    for ((modeInt, expectedMode) in modes) {
      val json =
        """
        {
          "v": "5.9.6",
          "fr": 30.0,
          "ip": 0.0,
          "op": 60.0,
          "w": 100,
          "h": 100,
          "layers": [
            {
              "ty": 4,
              "ind": 1,
              "ip": 0.0,
              "op": 60.0,
              "shapes": [
                {
                  "ty": "mm",
                  "nm": "Merge Paths",
                  "mm": $modeInt
                }
              ]
            }
          ]
        }
        """
          .trimIndent()

      val animation = Animation.decodeFromString(json)
      val shapeLayer = animation.layers[0] as ShapeLayer
      val mergePaths = shapeLayer.shapes[0] as MergePaths
      assertThat(mergePaths.mode).isEqualTo(expectedMode)
    }
  }

  @Test
  fun repeater_decodesCopiesOffsetCompositeAndTransform() {
    val json =
      """
      {
        "v": "5.9.6",
        "fr": 30.0,
        "ip": 0.0,
        "op": 60.0,
        "w": 100,
        "h": 100,
        "layers": [
          {
            "ty": 4,
            "ind": 1,
            "ip": 0.0,
            "op": 60.0,
            "shapes": [
              {
                "ty": "rp",
                "nm": "Repeater 1",
                "c": { "a": 0, "k": 5.0 },
                "o": { "a": 0, "k": 1.0 },
                "m": 2,
                "tr": {
                  "ty": "tr",
                  "a": { "a": 0, "k": [0, 0] },
                  "p": { "a": 0, "k": [10, 20] },
                  "s": { "a": 0, "k": [100, 100] },
                  "r": { "a": 0, "k": 30 },
                  "o": { "a": 0, "k": 100 }
                }
              }
            ]
          }
        ]
      }
      """
        .trimIndent()

    val animation = Animation.decodeFromString(json)
    val shapeLayer = animation.layers[0] as ShapeLayer
    val repeater = shapeLayer.shapes[0] as Repeater

    assertThat(repeater.name).isEqualTo("Repeater 1")
    assertThat((repeater.copies as StaticScalarProperty).value.constantValue).isEqualTo(5f)
    assertThat((repeater.offset as StaticScalarProperty).value.constantValue).isEqualTo(1f)
    assertThat(repeater.composite).isEqualTo(CompositeMode.Below)
    assertThat(repeater.transform).isNotNull()
  }

  @Test
  fun extendedModifiers_decodeOffsetPathPuckerBloatTwistZigZag() {
    val json =
      """
      {
        "v": "5.9.6",
        "fr": 30.0,
        "ip": 0.0,
        "op": 60.0,
        "w": 100,
        "h": 100,
        "layers": [
          {
            "ty": 4,
            "ind": 1,
            "ip": 0.0,
            "op": 60.0,
            "shapes": [
              {
                "ty": "op",
                "nm": "Offset 1",
                "a": { "a": 0, "k": 8.0 },
                "lj": 1
              },
              {
                "ty": "pb",
                "nm": "Pucker 1",
                "a": { "a": 0, "k": 25.0 }
              },
              {
                "ty": "tw",
                "nm": "Twist 1",
                "a": { "a": 0, "k": 90.0 },
                "c": { "a": 0, "k": [50, 50] }
              },
              {
                "ty": "zz",
                "nm": "ZigZag 1",
                "s": { "a": 0, "k": 10.0 },
                "r": { "a": 0, "k": 4.0 },
                "pt": 2
              }
            ]
          }
        ]
      }
      """
        .trimIndent()

    val animation = Animation.decodeFromString(json)
    val shapeLayer = animation.layers[0] as ShapeLayer

    val offset = shapeLayer.shapes[0] as OffsetPath
    assertThat((offset.amount as StaticScalarProperty).value.constantValue).isEqualTo(8f)
    assertThat(offset.lineJoin).isEqualTo(LineJoin.Miter)

    val pucker = shapeLayer.shapes[1] as PuckerBloat
    assertThat((pucker.amount as StaticScalarProperty).value.constantValue).isEqualTo(25f)

    val twist = shapeLayer.shapes[2] as Twist
    assertThat((twist.angle as StaticScalarProperty).value.constantValue).isEqualTo(90f)
    val center = (twist.center as StaticPositionProperty).value
    assertThat(center.x.constantValue).isEqualTo(50f)
    assertThat(center.y.constantValue).isEqualTo(50f)

    val zigzag = shapeLayer.shapes[3] as ZigZag
    assertThat((zigzag.size as StaticScalarProperty).value.constantValue).isEqualTo(10f)
    assertThat((zigzag.ridgesPerSegment as StaticScalarProperty).value.constantValue).isEqualTo(4f)
    assertThat((zigzag.pointType as StaticScalarProperty).value.constantValue).isEqualTo(2f)
  }

  @Test
  fun noStyleAndUnknownElement_decodeGracefully() {
    val json =
      """
      {
        "v": "5.9.6",
        "fr": 30.0,
        "ip": 0.0,
        "op": 60.0,
        "w": 100,
        "h": 100,
        "layers": [
          {
            "ty": 4,
            "ind": 1,
            "ip": 0.0,
            "op": 60.0,
            "shapes": [
              {
                "ty": "no",
                "nm": "Empty Style"
              },
              {
                "ty": "custom_unrecognized_shape",
                "nm": "Custom Shape"
              }
            ]
          }
        ]
      }
      """
        .trimIndent()

    val animation = Animation.decodeFromString(json)
    val shapeLayer = animation.layers[0] as ShapeLayer

    assertThat(shapeLayer.shapes[0]).isInstanceOf(NoStyle::class.java)
    assertThat(shapeLayer.shapes[1]).isInstanceOf(UnknownElement::class.java)
  }
}
