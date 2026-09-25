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
import androidx.compose.remote.creation.compose.state.rb
import androidx.compose.remote.creation.compose.state.rf
import androidx.compose.ui.graphics.Color
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.android.horologist.remotecompose.lottie.format.Animation
import com.google.android.horologist.remotecompose.lottie.format.graphicelement.geometry.Rectangle
import com.google.android.horologist.remotecompose.lottie.format.graphicelement.grouping.Group
import com.google.android.horologist.remotecompose.lottie.format.layer.LayerType
import com.google.android.horologist.remotecompose.lottie.format.layer.TextJustify
import com.google.android.horologist.remotecompose.lottie.format.layer.TextLayer
import com.google.android.horologist.remotecompose.lottie.format.properties.StaticPositionProperty
import com.google.android.horologist.remotecompose.lottie.format.properties.StaticVectorProperty
import com.google.android.horologist.remotecompose.lottie.format.values.Point
import com.google.android.horologist.remotecompose.lottie.renderer.NoopStyle
import com.google.android.horologist.remotecompose.lottie.renderer.RemoteGroup
import com.google.android.horologist.remotecompose.lottie.renderer.gatherShapes
import com.google.android.horologist.remotecompose.lottie.renderer.layers.parseColorFromList
import com.google.common.truth.Truth.assertThat
import org.junit.Test
import org.junit.runner.RunWith

@SuppressLint("RestrictedApi")
@RunWith(AndroidJUnit4::class)
class TextLayerTest {

  @Test
  fun decodeAnimation_withTextLayer_parsesTextDocumentAndProperties() {
    val json =
      """
      {
        "v": "5.9.6",
        "fr": 30.0,
        "ip": 0.0,
        "op": 60.0,
        "w": 400,
        "h": 400,
        "layers": [
          {
            "ty": 5,
            "nm": "TitleText",
            "ind": 1,
            "ip": 0.0,
            "op": 60.0,
            "t": {
              "d": {
                "k": [
                  {
                    "s": {
                      "t": "Hello Horologist",
                      "s": 32.0,
                      "f": "Roboto-Bold",
                      "j": 2,
                      "tr": 10.0,
                      "lh": 40.0,
                      "ls": 0.0,
                      "fc": [1.0, 0.5, 0.0, 1.0],
                      "sc": [0.0, 0.0, 0.0, 1.0],
                      "sw": 2.0,
                      "of": true
                    },
                    "t": 0.0
                  }
                ]
              },
              "m": {
                "g": 1
              }
            }
          }
        ]
      }
      """
        .trimIndent()

    val animation = Animation.decodeFromString(json)
    assertThat(animation.layers).hasSize(1)

    val layer = animation.layers[0]
    assertThat(layer).isInstanceOf(TextLayer::class.java)
    val textLayer = layer as TextLayer
    assertThat(textLayer.name).isEqualTo("TitleText")
    assertThat(textLayer.type).isEqualTo(LayerType.Text)

    val textData = textLayer.text
    assertThat(textData).isNotNull()
    val docProp = textData?.document
    assertThat(docProp).isNotNull()
    assertThat(docProp?.keyframes).hasSize(1)

    val kf = docProp?.keyframes?.get(0)
    assertThat(kf?.time).isEqualTo(0f)
    val doc = kf?.start
    assertThat(doc).isNotNull()
    assertThat(doc?.text).isEqualTo("Hello Horologist")
    assertThat(doc?.fontSize).isEqualTo(32f)
    assertThat(doc?.fontName).isEqualTo("Roboto-Bold")
    assertThat(doc?.justification).isEqualTo(TextJustify.Center)
    assertThat(doc?.tracking).isEqualTo(10f)
    assertThat(doc?.lineHeight).isEqualTo(40f)
    assertThat(doc?.fillColor).containsExactly(1.0f, 0.5f, 0.0f, 1.0f).inOrder()
    assertThat(doc?.strokeColor).containsExactly(0.0f, 0.0f, 0.0f, 1.0f).inOrder()
    assertThat(doc?.strokeWidth).isEqualTo(2f)
    assertThat(doc?.strokeOverFill).isTrue()
  }

  @Test
  fun decodeAnimation_withFontsAndChars_parsesGlyphs() {
    val json =
      """
      {
        "v": "5.9.6",
        "fr": 30.0,
        "ip": 0.0,
        "op": 60.0,
        "w": 300,
        "h": 300,
        "fonts": {
          "list": [
            {
              "fName": "Roboto-Regular",
              "fFamily": "Roboto",
              "fStyle": "Regular",
              "ascent": 75.0
            }
          ]
        },
        "chars": [
          {
            "ch": "A",
            "fFamily": "Roboto",
            "style": "Regular",
            "size": 100.0,
            "w": 65.0,
            "data": {
              "shapes": []
            }
          }
        ],
        "layers": []
      }
      """
        .trimIndent()

    val animation = Animation.decodeFromString(json)
    assertThat(animation.fonts).isNotNull()
    assertThat(animation.fonts?.list).hasSize(1)
    assertThat(animation.fonts?.list?.get(0)?.name).isEqualTo("Roboto-Regular")
    assertThat(animation.fonts?.list?.get(0)?.family).isEqualTo("Roboto")

    assertThat(animation.chars).hasSize(1)
    val charA = animation.chars[0]
    assertThat(charA.character).isEqualTo("A")
    assertThat(charA.family).isEqualTo("Roboto")
    assertThat(charA.width).isEqualTo(65f)
    assertThat(charA.size).isEqualTo(100f)
  }

  @Test
  fun textJustify_fromValue_mapsAllEnumVariants() {
    assertThat(TextJustify.fromValueOrNull(0)).isEqualTo(TextJustify.Left)
    assertThat(TextJustify.fromValueOrNull(1)).isEqualTo(TextJustify.Right)
    assertThat(TextJustify.fromValueOrNull(2)).isEqualTo(TextJustify.Center)
    assertThat(TextJustify.fromValueOrNull(3)).isEqualTo(TextJustify.JustifyWithLastLineLeft)
    assertThat(TextJustify.fromValueOrNull(4)).isEqualTo(TextJustify.JustifyWithLastLineRight)
    assertThat(TextJustify.fromValueOrNull(5)).isEqualTo(TextJustify.JustifyWithLastLineCenter)
    assertThat(TextJustify.fromValueOrNull(6)).isEqualTo(TextJustify.JustifyWithLastLineFull)
    assertThat(TextJustify.fromValueOrNull(99)).isNull()
  }

  @Test
  fun parseColorFromList_handlesRgbAndRgba() {
    val rgb = parseColorFromList(listOf(1.0f, 0.0f, 0.0f))
    assertThat(rgb).isEqualTo(Color(1f, 0f, 0f, 1f))

    val rgba = parseColorFromList(listOf(0.0f, 1.0f, 0.0f, 0.5f))
    assertThat(rgba).isEqualTo(Color(0f, 1f, 0f, 0.5f))

    val empty = parseColorFromList(emptyList())
    assertThat(empty).isEqualTo(Color.Black)
  }

  @Test
  fun gatherShapes_withInheritedStyle_harvestsUnstyledGeometries() {
    val rect =
      Rectangle(
        position = StaticPositionProperty(value = Point(0f.rf, 0f.rf)),
        size = StaticVectorProperty(animated = false.rb, value = listOf(50f.rf, 50f.rf)),
      )
    val settings = LottieSettings(currentFrame = 0f.rf)
    val styledShapes = gatherShapes(listOf(rect), settings, inheritedStyle = NoopStyle())

    assertThat(styledShapes).hasSize(1)
    assertThat(styledShapes[0].style).isInstanceOf(NoopStyle::class.java)
    assertThat(styledShapes[0].shapes).hasSize(1)
  }

  @Test
  fun gatherShapes_withInheritedStyle_harvestsUnstyledGroupGeometries() {
    val rect1 =
      Rectangle(
        position = StaticPositionProperty(value = Point(0f.rf, 0f.rf)),
        size = StaticVectorProperty(animated = false.rb, value = listOf(10f.rf, 10f.rf)),
      )
    val rect2 =
      Rectangle(
        position = StaticPositionProperty(value = Point(20f.rf, 20f.rf)),
        size = StaticVectorProperty(animated = false.rb, value = listOf(10f.rf, 10f.rf)),
      )
    val group = Group(shapes = listOf(rect1, rect2), name = "GlyphGroup")
    val settings = LottieSettings(currentFrame = 0f.rf)
    val styledShapes = gatherShapes(listOf(group), settings, inheritedStyle = NoopStyle())

    assertThat(styledShapes).hasSize(1)
    assertThat(styledShapes[0].style).isInstanceOf(NoopStyle::class.java)
    val remoteGroup = styledShapes[0].shapes[0] as? RemoteGroup
    assertThat(remoteGroup).isNotNull()
    assertThat(remoteGroup!!.childShapes).hasSize(1)
    assertThat(remoteGroup.childShapes[0].shapes).hasSize(2)
  }

  @Test
  fun gatherShapes_withoutInheritedStyle_dropsUnstyledGeometries() {
    val rect =
      Rectangle(
        position = StaticPositionProperty(value = Point(0f.rf, 0f.rf)),
        size = StaticVectorProperty(animated = false.rb, value = listOf(50f.rf, 50f.rf)),
      )
    val settings = LottieSettings(currentFrame = 0f.rf)
    val styledShapes = gatherShapes(listOf(rect), settings, inheritedStyle = null)

    assertThat(styledShapes).isEmpty()
  }

  @Test
  fun gatherShapes_withMixedGroupAndGeometryAndInheritedStyle_emitsBothWithoutDuplicates() {
    val groupRect =
      Rectangle(
        position = StaticPositionProperty(value = Point(0f.rf, 0f.rf)),
        size = StaticVectorProperty(animated = false.rb, value = listOf(10f.rf, 10f.rf)),
      )
    val group = Group(shapes = listOf(groupRect), name = "SubGroup")
    val trailingRect =
      Rectangle(
        position = StaticPositionProperty(value = Point(20f.rf, 20f.rf)),
        size = StaticVectorProperty(animated = false.rb, value = listOf(15f.rf, 15f.rf)),
      )
    val settings = LottieSettings(currentFrame = 0f.rf)
    val styledShapes =
      gatherShapes(listOf(group, trailingRect), settings, inheritedStyle = NoopStyle())

    // Reversed order: trailingRect emitted first, then group
    assertThat(styledShapes).hasSize(2)
    assertThat(styledShapes[0].shapes).hasSize(1) // trailingRect
    assertThat(styledShapes[1].shapes).hasSize(1) // group
    val remoteGroup = styledShapes[1].shapes[0] as? RemoteGroup
    assertThat(remoteGroup).isNotNull()
    assertThat(remoteGroup!!.childShapes).hasSize(1)
    assertThat(remoteGroup.childShapes[0].shapes).hasSize(1)
  }

  @Test
  fun decodeAnimation_withMultilineTextAndStrokeOverFill_decodesDocument() {
    val json =
      """
      {
        "v": "5.9.6",
        "fr": 30.0,
        "ip": 0.0,
        "op": 30.0,
        "w": 100,
        "h": 100,
        "layers": [
          {
            "ty": 5,
            "nm": "MultilineLayer",
            "ind": 1,
            "ip": 0.0,
            "op": 30.0,
            "t": {
              "d": {
                "k": [
                  {
                    "s": {
                      "t": "Line1\nLine2\nLine3",
                      "s": 16.0,
                      "f": "FontName",
                      "j": 0,
                      "tr": 0.0,
                      "lh": 22.0,
                      "ls": 2.0,
                      "fc": [1.0, 1.0, 1.0, 1.0],
                      "sc": [0.5, 0.5, 0.5, 1.0],
                      "sw": 1.5,
                      "of": false
                    },
                    "t": 0.0
                  }
                ]
              }
            }
          }
        ]
      }
      """
        .trimIndent()

    val animation = Animation.decodeFromString(json)
    val layer = animation.layers[0] as TextLayer
    val doc = layer.text?.document?.keyframes?.get(0)?.start
    assertThat(doc).isNotNull()
    assertThat(doc?.text).isEqualTo("Line1\nLine2\nLine3")
    assertThat(doc?.lineHeight).isEqualTo(22f)
    assertThat(doc?.baselineShift).isEqualTo(2f)
    assertThat(doc?.strokeOverFill).isFalse()
  }
}
