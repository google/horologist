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
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.android.horologist.remotecompose.lottie.format.Animation
import com.google.android.horologist.remotecompose.lottie.format.asset.ImageAsset
import com.google.android.horologist.remotecompose.lottie.format.asset.PrecompAsset
import com.google.android.horologist.remotecompose.lottie.format.graphicelement.grouping.Transform
import com.google.android.horologist.remotecompose.lottie.format.layer.MatteMode
import com.google.android.horologist.remotecompose.lottie.format.layer.NullLayer
import com.google.android.horologist.remotecompose.lottie.format.layer.PrecompLayer
import com.google.android.horologist.remotecompose.lottie.format.layer.ShapeLayer
import com.google.android.horologist.remotecompose.lottie.format.properties.StaticPositionProperty
import com.google.android.horologist.remotecompose.lottie.format.properties.StaticScalarProperty
import com.google.android.horologist.remotecompose.lottie.format.properties.StaticVectorProperty
import com.google.android.horologist.remotecompose.lottie.format.values.Point
import com.google.android.horologist.remotecompose.lottie.renderer.layers.calculateLocalFrame
import com.google.common.truth.Truth.assertThat
import org.junit.Test
import org.junit.runner.RunWith

@SuppressLint("RestrictedApi")
@RunWith(AndroidJUnit4::class)
class PrecompLayerTest {

  private fun createDefaultTransform(name: String? = null): Transform =
    Transform(
      name = name,
      anchorPoint = StaticPositionProperty(value = Point(0f.rf, 0f.rf)),
      positionTranslation = StaticPositionProperty(value = Point(0f.rf, 0f.rf)),
      rotation = StaticScalarProperty(value = 0f.rf),
      scale = StaticVectorProperty(animated = false.rb, value = listOf(100f.rf, 100f.rf)),
      opacity = StaticScalarProperty(value = 100f.rf),
    )

  @Test
  fun buildAncestorTransforms_withBaseStack_prependsBaseStackToAllLayers() {
    val baseT = createDefaultTransform(name = "base_transform")
    val t1 = createDefaultTransform(name = "t1")
    val t2 = createDefaultTransform(name = "t2")

    val root =
      NullLayer(index = 1, parent = null, startFrame = 0f.rf, endFrame = 60f.rf, transform = t1)
    val child =
      NullLayer(index = 2, parent = 1, startFrame = 0f.rf, endFrame = 60f.rf, transform = t2)

    val transforms = buildAncestorTransforms(listOf(root, child), baseStack = listOf(baseT))

    assertThat(transforms[1]).isEqualTo(listOf(baseT))
    assertThat(transforms[2]).isEqualTo(listOf(baseT, t1))
    assertThat(transforms[null]).isEqualTo(listOf(baseT))
  }

  @Test
  fun buildAncestorTransforms_withoutBaseStack_defaultsToEmptyBase() {
    val t1 = createDefaultTransform(name = "t1")
    val t2 = createDefaultTransform(name = "t2")

    val root =
      NullLayer(index = 1, parent = null, startFrame = 0f.rf, endFrame = 60f.rf, transform = t1)
    val child =
      NullLayer(index = 2, parent = 1, startFrame = 0f.rf, endFrame = 60f.rf, transform = t2)

    val transforms = buildAncestorTransforms(listOf(root, child))

    assertThat(transforms[1]).isEmpty()
    assertThat(transforms[2]).isEqualTo(listOf(t1))
    assertThat(transforms[null]).isNull()
  }

  @Test
  fun animation_withPrecompLayer_decodesAndAssociatesAssets() {
    val json =
      """
      {
        "v": "5.9.6",
        "fr": 30.0,
        "ip": 0.0,
        "op": 60.0,
        "w": 300,
        "h": 300,
        "assets": [
          {
            "id": "comp_circle",
            "nm": "CirclePrecomp",
            "layers": [
              {
                "ty": 4,
                "nm": "ShapeInPrecomp",
                "ind": 1,
                "ip": 0.0,
                "op": 60.0,
                "shapes": []
              }
            ]
          }
        ],
        "layers": [
          {
            "ty": 0,
            "nm": "PrecompInstance",
            "refId": "comp_circle",
            "ind": 10,
            "ip": 0.0,
            "op": 60.0,
            "w": 100,
            "h": 100
          }
        ]
      }
      """
        .trimIndent()

    val animation = Animation.decodeFromString(json)
    assertThat(animation.assets).hasSize(1)
    assertThat(animation.layers).hasSize(1)

    val precompAsset = animation.assets[0] as PrecompAsset
    assertThat(precompAsset.id).isEqualTo("comp_circle")
    assertThat(precompAsset.layers).hasSize(1)

    val precompLayer = animation.layers[0] as PrecompLayer
    assertThat(precompLayer.refId).isEqualTo("comp_circle")
    assertThat(precompLayer.width).isEqualTo(100f)
    assertThat(precompLayer.height).isEqualTo(100f)
  }

  @Test
  fun precompLayer_localFrameCalculation_withTimingAndStretch() {
    // Precomp layer at startTime = 15, timeStretch = 1.5
    val currentFrame = 45f.rf
    val localFrame = calculateLocalFrame(currentFrame, startTime = 15f, timeStretch = 1.5f)
    // (45 - 15) / 1.5 = 20.0
    assertThat(localFrame.constantValueOrNull).isWithin(0.001f).of(20f)
  }

  @Test
  fun precompLayer_withNonPrecompAsset_isIgnored() {
    val imageAsset = ImageAsset(id = "img_0", width = 100f, height = 100f, path = "test.png")
    val assetMap = mapOf("img_0" to imageAsset)
    val settings = LottieSettings(currentFrame = 0f.rf, assets = assetMap)

    val precompLayer = PrecompLayer(refId = "img_0", startFrame = 0f.rf, endFrame = 60f.rf)
    // Should safely handle non-PrecompAsset without throwing
    val asset = settings.assets[precompLayer.refId] as? PrecompAsset
    assertThat(asset).isNull()
  }

  @Test
  fun precompLayer_withMissingAsset_isHandledSafely() {
    val settings = LottieSettings(currentFrame = 0f.rf, assets = emptyMap())
    val precompLayer = PrecompLayer(refId = "missing_ref", startFrame = 0f.rf, endFrame = 60f.rf)
    val asset = settings.assets[precompLayer.refId] as? PrecompAsset
    assertThat(asset).isNull()
  }

  @Test
  fun precompLayer_recursionGuard_detectsActivePrecomps() {
    val settings = LottieSettings(currentFrame = 0f.rf, activePrecomps = setOf("comp_recursive"))
    val precompLayer = PrecompLayer(refId = "comp_recursive", startFrame = 0f.rf, endFrame = 60f.rf)
    assertThat(precompLayer.refId in settings.activePrecomps).isTrue()
  }

  @Test
  fun precompLayer_trackMatteResolution_insidePrecompAsset() {
    val matteLayer =
      ShapeLayer(
        index = 1,
        startFrame = 0f.rf,
        endFrame = 60f.rf,
        shapes = emptyList(),
        matteTarget = 1,
      )
    val maskedLayer =
      ShapeLayer(
        index = 2,
        startFrame = 0f.rf,
        endFrame = 60f.rf,
        shapes = emptyList(),
        matteMode = MatteMode.InvertedAlpha,
        matteParent = 1,
      )
    val precompAsset = PrecompAsset(id = "comp_matte", layers = listOf(maskedLayer, matteLayer))

    val matteTargetIndices = precompAsset.layers.mapNotNull { it.matteParent }.toSet()
    assertThat(matteTargetIndices).containsExactly(1)

    val resolvedMatte = precompAsset.layers.firstOrNull { it.index == maskedLayer.matteParent }
    assertThat(resolvedMatte).isEqualTo(matteLayer)
  }

  @Test
  fun precompLayer_decodesTimeRemappingProperty() {
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
            "id": "comp_1",
            "layers": []
          }
        ],
        "layers": [
          {
            "ty": 0,
            "refId": "comp_1",
            "ip": 0.0,
            "op": 60.0,
            "tm": {
              "a": 1,
              "k": [
                { "t": 0.0, "s": [0.0] },
                { "t": 60.0, "s": [2.0] }
              ]
            }
          }
        ]
      }
      """
        .trimIndent()

    val animation = Animation.decodeFromString(json)
    val precompLayer = animation.layers[0] as PrecompLayer
    assertThat(precompLayer.timeRemap).isNotNull()
  }
}
