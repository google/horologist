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

import android.content.Context
import androidx.compose.remote.creation.compose.state.rf
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.android.horologist.remotecompose.lottie.format.Animation
import com.google.android.horologist.remotecompose.lottie.format.LottieDecoder
import com.google.android.horologist.remotecompose.lottie.format.graphicelement.ShapeType
import com.google.android.horologist.remotecompose.lottie.format.graphicelement.geometry.Ellipse
import com.google.android.horologist.remotecompose.lottie.format.graphicelement.geometry.Path
import com.google.android.horologist.remotecompose.lottie.format.graphicelement.geometry.PolyStar
import com.google.android.horologist.remotecompose.lottie.format.graphicelement.geometry.PolyStarType
import com.google.android.horologist.remotecompose.lottie.format.graphicelement.geometry.Rectangle
import com.google.android.horologist.remotecompose.lottie.format.graphicelement.grouping.Group
import com.google.android.horologist.remotecompose.lottie.format.graphicelement.styles.Fill
import com.google.android.horologist.remotecompose.lottie.format.layer.LayerType
import com.google.android.horologist.remotecompose.lottie.format.layer.ShapeLayer
import com.google.android.horologist.remotecompose.lottie.format.properties.AnimatedBezierProperty
import com.google.android.horologist.remotecompose.lottie.format.properties.AnimatedVectorProperty
import com.google.android.horologist.remotecompose.lottie.format.properties.StaticPositionProperty
import com.google.android.horologist.remotecompose.lottie.format.properties.StaticScalarProperty
import com.google.android.horologist.remotecompose.lottie.format.properties.StaticVectorProperty
import com.google.android.horologist.remotecompose.lottie.format.values.BezierValue
import com.google.android.horologist.remotecompose.lottie.format.values.GradientValue
import com.google.android.horologist.remotecompose.lottie.renderer.properties.animateScalar
import com.google.common.truth.Truth.assertThat
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ParsingTest {

  private fun loadGeometry(): Animation {
    val context = ApplicationProvider.getApplicationContext<Context>()
    return Animation.load(R.raw.geometry, context)
  }

  @Test
  fun geometryTest() {
    val animation = loadGeometry()

    assertThat(animation).isNotNull()
    assertThat(animation.name).isEqualTo("[lottie] geometry")
    assertThat(animation.version).isEqualTo("0.0.0")
    assertThat(animation.layers).hasSize(2)
  }

  @Test
  fun layerPolymorphism_deserializes() {
    val animation = loadGeometry()

    assertThat(animation.layers[0].name).isEqualTo("Scale (Import Fix)")
    assertThat(animation.layers[0].type).isEqualTo(LayerType.Null)
    assertThat(animation.layers[0].index).isEqualTo(1)
  }

  @Test
  fun layerTypeEnum_deserializes() {
    val animation = loadGeometry()

    assertThat(animation.layers[0].type).isEqualTo(LayerType.Null)
    assertThat(animation.layers[1].type).isEqualTo(LayerType.Shape)
  }

  @Test
  fun shapeTypePolymorphism_deserializes() {
    val animation = loadGeometry()

    val shapeLayer = animation.layers[1] as ShapeLayer
    val group = shapeLayer.shapes[0] as Group

    assertThat(group.shapes.size).isEqualTo(3)
  }

  @Test
  fun fillColorSlotId_deserializes() {
    val animation = loadGeometry()

    val shapeLayer = animation.layers[1] as ShapeLayer
    val group = shapeLayer.shapes[0] as Group
    val fill = group.shapes[1] as Fill

    assertThat(fill.color.slotId).isEqualTo("color.primary")
  }

  @Test
  fun shapeTypeEnum_deserializes() {
    val animation = loadGeometry()

    val shapeLayer = animation.layers[1] as ShapeLayer

    assertThat(shapeLayer.shapes[0].type).isEqualTo(ShapeType.Group)
  }

  @Test
  fun animatedBezierProperty_deserializes() {
    val animation = loadGeometry()

    val shapeLayer = animation.layers[1] as ShapeLayer
    val group = shapeLayer.shapes[0] as Group
    val path = group.shapes[0] as Path

    val animatedShape = path.shape as AnimatedBezierProperty

    assertThat(animatedShape.keyframes).hasSize(5)
    assertThat(animatedShape.keyframes[0].inTangent?.x).isEqualTo(0.999f)
    assertThat(animatedShape.keyframes[0].inTangent?.y).isEqualTo(1f)
  }

  @Test
  fun animatedVectorProperty_deserializes() {
    val animation = loadGeometry()

    val shapeLayer = animation.layers[1] as ShapeLayer
    val transform = shapeLayer.transform!!

    assertThat(transform.scale.animated).isTrue()
    val animatedScale = transform.scale as AnimatedVectorProperty

    assertThat(animatedScale.keyframes).hasSize(5)
    assertThat(animatedScale.keyframes[0].inTangent?.x).isEqualTo(0.999f)
  }

  /**
   * Tests deserialization of parametric rectangle and ellipse shapes.
   *
   * Source:
   * [Lottie Format Feature Support & Sample Test Suite](https://docs.google.com/document/d/1jXj3kbXL57kxjRc0soUqst2poa2-Lrc2qZAIzEmbB8w/edit)
   */
  @Test
  fun rectEllipse_deserializes() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val animation = Animation.load(R.raw.rect_ellipse, context)

    assertThat(animation).isNotNull()
    val shapeLayer = animation.layers[0] as ShapeLayer
    assertThat(shapeLayer.shapes).hasSize(4)

    val group1 = shapeLayer.shapes[0] as Group
    val rect = group1.shapes[0] as Rectangle
    assertThat(rect.type).isEqualTo(ShapeType.Rectangle)
    assertThat(rect.position.animated).isFalse()
    assertThat((rect.position as StaticPositionProperty).value).isEqualTo(floatArrayOf(36f, 36f))
    assertThat(rect.size.animated).isFalse()
    assertThat((rect.size as StaticVectorProperty).value).isEqualTo(floatArrayOf(48f, 40f))
    assertThat(rect.cornerRadius.animated).isFalse()
    assertThat((rect.cornerRadius as StaticScalarProperty).value).isEqualTo(10f)

    val settings = LottieSettings(0.rf, SlotMap.Empty)
    val cornerRadiusRf = animateScalar(rect.cornerRadius, settings)
    assertThat(cornerRadiusRf.constantValueOrNull).isEqualTo(10f)

    val group3 = shapeLayer.shapes[2] as Group
    val ellipse = group3.shapes[0] as Ellipse
    assertThat(ellipse.type).isEqualTo(ShapeType.Ellipse)
    assertThat(ellipse.position.animated).isFalse()
    assertThat((ellipse.position as StaticPositionProperty).value).isEqualTo(floatArrayOf(36f, 92f))
    assertThat(ellipse.size.animated).isFalse()
    assertThat((ellipse.size as StaticVectorProperty).value).isEqualTo(floatArrayOf(42f, 42f))
  }

  /**
   * Tests deserialization of parametric star and polygon shapes.
   *
   * Source:
   * [Lottie Format Feature Support & Sample Test Suite](https://docs.google.com/document/d/1jXj3kbXL57kxjRc0soUqst2poa2-Lrc2qZAIzEmbB8w/edit)
   */
  @Test
  fun polystar_deserializes() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val animation = Animation.load(R.raw.polystar, context)

    assertThat(animation).isNotNull()
    val shapeLayer = animation.layers[0] as ShapeLayer
    assertThat(shapeLayer.shapes).hasSize(4)

    val starGroup = shapeLayer.shapes[0] as Group
    val star = starGroup.shapes[0] as PolyStar
    assertThat(star.type).isEqualTo(ShapeType.PolyStar)
    assertThat(star.starType).isEqualTo(PolyStarType.Star)
    assertThat(star.points.animated).isFalse()
    assertThat((star.points as StaticScalarProperty).value).isEqualTo(5f)
    assertThat((star.outerRadius as StaticScalarProperty).value).isEqualTo(26f)
    assertThat((star.innerRadius as StaticScalarProperty).value).isEqualTo(13f)

    val polygonGroup = shapeLayer.shapes[1] as Group
    val polygon = polygonGroup.shapes[0] as PolyStar
    assertThat(polygon.type).isEqualTo(ShapeType.PolyStar)
    assertThat(polygon.starType).isEqualTo(PolyStarType.Polygon)
    assertThat(polygon.points.animated).isFalse()
    assertThat((polygon.points as StaticScalarProperty).value).isEqualTo(6f)
    assertThat((polygon.outerRadius as StaticScalarProperty).value).isEqualTo(24f)

    val settings = LottieSettings(0.rf, SlotMap.Empty)
    val pointsRf = animateScalar(polygon.points, settings)
    assertThat(pointsRf.constantValueOrNull).isEqualTo(6f)
    val outerRadiusRf = animateScalar(polygon.outerRadius, settings)
    assertThat(outerRadiusRf.constantValueOrNull).isEqualTo(24f)
  }

  @Test
  fun bezierValue_integerClosedFlag_deserializes() {
    val jsonZero = """{"c": 0, "v": [[10.0, 20.0]], "i": [[0.0, 0.0]], "o": [[0.0, 0.0]]}"""
    val bezier0 = LottieDecoder.json.decodeFromString(BezierValue.serializer(), jsonZero)
    assertThat(bezier0.closed).isFalse()
    assertThat(bezier0.vertices).containsExactly(listOf(10f, 20f))

    val jsonOne = """{"c": 1, "v": [[10.0, 20.0]], "i": [[0.0, 0.0]], "o": [[0.0, 0.0]]}"""
    val bezier1 = LottieDecoder.json.decodeFromString(BezierValue.serializer(), jsonOne)
    assertThat(bezier1.closed).isTrue()
    assertThat(bezier1.vertices).containsExactly(listOf(10f, 20f))
  }

  @Test
  fun bezierValue_booleanClosedFlag_deserializes() {
    val jsonFalse = """{"c": false, "v": []}"""
    val bezierFalse = LottieDecoder.json.decodeFromString(BezierValue.serializer(), jsonFalse)
    assertThat(bezierFalse.closed).isFalse()
    assertThat(bezierFalse.vertices).isEmpty()

    val jsonTrue = """{"c": true, "v": []}"""
    val bezierTrue = LottieDecoder.json.decodeFromString(BezierValue.serializer(), jsonTrue)
    assertThat(bezierTrue.closed).isTrue()
  }

  @Test
  fun bezierValue_resilientPointParsing_handlesEmptyAndPartialArrays() {
    val jsonEmpty = """{}"""
    val bezierEmpty = LottieDecoder.json.decodeFromString(BezierValue.serializer(), jsonEmpty)
    assertThat(bezierEmpty.closed).isFalse()
    assertThat(bezierEmpty.vertices).isEmpty()
    assertThat(bezierEmpty.inTangents).isEmpty()
    assertThat(bezierEmpty.outTangents).isEmpty()
  }

  @Test
  fun gradientValue_opaqueFloatArray_decodesColorStops() {
    val json = "[0.0, 1.0, 0.0, 0.0, 1.0, 0.0, 1.0, 0.0]"
    val gradientValue = LottieDecoder.json.decodeFromString(GradientValue.serializer(), json)

    assertThat(gradientValue.numberOfColors).isEqualTo(2)
    assertThat(gradientValue.values).hasSize(8)
    assertThat(gradientValue.hasTransparency).isFalse()
    assertThat(gradientValue.colorStops).hasSize(2)
    assertThat(gradientValue.opacityStops).isEmpty()

    val stop1 = gradientValue.colorStops[0]
    assertThat(stop1.offset).isEqualTo(0f)
    assertThat(stop1.red).isEqualTo(1f)
    assertThat(stop1.green).isEqualTo(0f)
    assertThat(stop1.blue).isEqualTo(0f)

    val stop2 = gradientValue.colorStops[1]
    assertThat(stop2.offset).isEqualTo(1f)
    assertThat(stop2.red).isEqualTo(0f)
    assertThat(stop2.green).isEqualTo(1f)
    assertThat(stop2.blue).isEqualTo(0f)
  }

  @Test
  fun gradientValue_transparentFloatArray_decodesColorAndOpacityStops() {
    val json =
      """
      {
        "p": 2,
        "k": [0.0, 1.0, 0.0, 0.0, 1.0, 0.0, 1.0, 0.0, 0.0, 1.0, 1.0, 0.5]
      }
      """
        .trimIndent()
    val gradientValue = LottieDecoder.json.decodeFromString(GradientValue.serializer(), json)

    assertThat(gradientValue.numberOfColors).isEqualTo(2)
    assertThat(gradientValue.values).hasSize(12)
    assertThat(gradientValue.hasTransparency).isTrue()
    assertThat(gradientValue.colorStops).hasSize(2)
    assertThat(gradientValue.opacityStops).hasSize(2)

    val oStop1 = gradientValue.opacityStops[0]
    assertThat(oStop1.offset).isEqualTo(0f)
    assertThat(oStop1.alpha).isEqualTo(1f)

    val oStop2 = gradientValue.opacityStops[1]
    assertThat(oStop2.offset).isEqualTo(1f)
    assertThat(oStop2.alpha).isEqualTo(0.5f)
  }

  @Test
  fun gradientValue_nestedObject_decodesColorCountAndValues() {
    val json =
      """
      {
        "p": 3,
        "k": [0.0, 1.0, 0.0, 0.0, 0.5, 0.0, 1.0, 0.0, 1.0, 0.0, 0.0, 1.0]
      }
      """
        .trimIndent()
    val gradientValue = LottieDecoder.json.decodeFromString(GradientValue.serializer(), json)

    assertThat(gradientValue.numberOfColors).isEqualTo(3)
    assertThat(gradientValue.values).hasSize(12)
    assertThat(gradientValue.colorStops).hasSize(3)
    assertThat(gradientValue.colorStops[1].offset).isEqualTo(0.5f)
    assertThat(gradientValue.colorStops[1].green).isEqualTo(1f)
  }

  @Test
  fun gradientValue_stopDecomposition_normalizes255ScaledValues() {
    val json =
      """
      {
        "p": 2,
        "k": [0.0, 255.0, 0.0, 0.0, 1.0, 0.0, 255.0, 0.0, 0.0, 255.0, 1.0, 128.0]
      }
      """
        .trimIndent()
    val gradientValue = LottieDecoder.json.decodeFromString(GradientValue.serializer(), json)

    assertThat(gradientValue.colorStops[0].red).isEqualTo(1f)
    assertThat(gradientValue.colorStops[1].green).isEqualTo(1f)
    assertThat(gradientValue.opacityStops[0].alpha).isEqualTo(1f)
    assertThat(gradientValue.opacityStops[1].alpha).isWithin(0.01f).of(128f / 255f)
  }

  @Test
  fun gradientValue_resolveStops_interpolatesOpaqueAndTransparent() {
    val jsonOpaque = "[0.0, 1.0, 0.0, 0.0, 1.0, 0.0, 0.0, 1.0]"
    val gradOpaque = LottieDecoder.json.decodeFromString(GradientValue.serializer(), jsonOpaque)
    val resolvedOpaque = gradOpaque.resolveStops()
    assertThat(resolvedOpaque).hasSize(2)
    assertThat(resolvedOpaque[0].offset).isEqualTo(0f)
    assertThat(resolvedOpaque[0].color.red).isEqualTo(1f)
    assertThat(resolvedOpaque[0].color.alpha).isEqualTo(1f)
    assertThat(resolvedOpaque[1].offset).isEqualTo(1f)
    assertThat(resolvedOpaque[1].color.blue).isEqualTo(1f)
    assertThat(resolvedOpaque[1].color.alpha).isEqualTo(1f)

    // With opacity stops at different offsets
    val jsonTransparent =
      """
      {
        "p": 2,
        "k": [0.0, 1.0, 0.0, 0.0, 1.0, 0.0, 0.0, 1.0, 0.0, 1.0, 0.5, 0.5, 1.0, 0.0]
      }
      """
        .trimIndent()
    val gradTransparent =
      LottieDecoder.json.decodeFromString(GradientValue.serializer(), jsonTransparent)
    val resolvedTrans = gradTransparent.resolveStops()
    assertThat(resolvedTrans).hasSize(3)
    assertThat(resolvedTrans.map { it.offset }).containsExactly(0f, 0.5f, 1f).inOrder()
    // At offset 0.5f, color is interpolated between stop 0 (red) and stop 1 (blue) -> r=0.5, b=0.5
    assertThat(resolvedTrans[1].color.red).isWithin(0.01f).of(0.5f)
    assertThat(resolvedTrans[1].color.blue).isWithin(0.01f).of(0.5f)
    assertThat(resolvedTrans[1].color.alpha).isWithin(0.01f).of(0.5f)
  }

  @Test
  fun gradientValue_serialization_roundTrip() {
    val original =
      GradientValue(
        numberOfColors = 2,
        values = listOf(0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 1.0f, 0.0f),
      )
    val serialized = LottieDecoder.json.encodeToString(GradientValue.serializer(), original)
    val deserialized = LottieDecoder.json.decodeFromString(GradientValue.serializer(), serialized)
    assertThat(deserialized.numberOfColors).isEqualTo(original.numberOfColors)
    assertThat(deserialized.values).isEqualTo(original.values)
  }
}
