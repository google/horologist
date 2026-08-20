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
import android.graphics.Color
import androidx.compose.remote.creation.compose.state.rf
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.android.horologist.remotecompose.lottie.format.AnimatedBezierProperty
import com.google.android.horologist.remotecompose.lottie.format.AnimatedColorProperty
import com.google.android.horologist.remotecompose.lottie.format.AnimatedPositionProperty
import com.google.android.horologist.remotecompose.lottie.format.AnimatedVectorProperty
import com.google.android.horologist.remotecompose.lottie.format.Animation
import com.google.android.horologist.remotecompose.lottie.format.GradientValue
import com.google.android.horologist.remotecompose.lottie.format.GraphicElement
import com.google.android.horologist.remotecompose.lottie.format.Layer
import com.google.android.horologist.remotecompose.lottie.format.LayerType
import com.google.android.horologist.remotecompose.lottie.format.PolyStarType
import com.google.android.horologist.remotecompose.lottie.format.ShapeType
import com.google.android.horologist.remotecompose.lottie.format.SplitPositionProperty
import com.google.android.horologist.remotecompose.lottie.format.StaticPositionProperty
import com.google.android.horologist.remotecompose.lottie.format.StaticScalarProperty
import com.google.android.horologist.remotecompose.lottie.format.StaticVectorProperty
import com.google.android.horologist.remotecompose.lottie.renderer.animateScalar
import com.google.common.truth.Truth.assertThat
import kotlinx.serialization.json.Json
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

    val shapeLayer = animation.layers[1] as Layer.ShapeLayer
    val group = shapeLayer.shapes[0] as GraphicElement.Group

    assertThat(group.shapes.size).isEqualTo(3)
  }

  @Test
  fun fillColorSlotId_deserializes() {
    val animation = loadGeometry()

    val shapeLayer = animation.layers[1] as Layer.ShapeLayer
    val group = shapeLayer.shapes[0] as GraphicElement.Group
    val fill = group.shapes[1] as GraphicElement.Fill

    assertThat(fill.color.slotId).isEqualTo("color.primary")
  }

  @Test
  fun shapeTypeEnum_deserializes() {
    val animation = loadGeometry()

    val shapeLayer = animation.layers[1] as Layer.ShapeLayer

    assertThat(shapeLayer.shapes[0].type).isEqualTo(ShapeType.Group)
  }

  @Test
  fun animatedBezierProperty_deserializes() {
    val animation = loadGeometry()

    val shapeLayer = animation.layers[1] as Layer.ShapeLayer
    val group = shapeLayer.shapes[0] as GraphicElement.Group
    val path = group.shapes[0] as GraphicElement.Path

    val animatedShape = path.shape as AnimatedBezierProperty

    assertThat(animatedShape.keyframes).hasSize(5)
    assertThat(animatedShape.keyframes[0].inTangent?.x).isEqualTo(0.999f)
    assertThat(animatedShape.keyframes[0].inTangent?.y).isEqualTo(1f)
  }

  @Test
  fun animatedVectorProperty_deserializes() {
    val animation = loadGeometry()

    val shapeLayer = animation.layers[1] as Layer.ShapeLayer
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
    val shapeLayer = animation.layers[0] as Layer.ShapeLayer
    assertThat(shapeLayer.shapes).hasSize(4)

    val group1 = shapeLayer.shapes[0] as GraphicElement.Group
    val rect = group1.shapes[0] as GraphicElement.Rectangle
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

    val group3 = shapeLayer.shapes[2] as GraphicElement.Group
    val ellipse = group3.shapes[0] as GraphicElement.Ellipse
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
    val shapeLayer = animation.layers[0] as Layer.ShapeLayer
    assertThat(shapeLayer.shapes).hasSize(4)

    val starGroup = shapeLayer.shapes[0] as GraphicElement.Group
    val star = starGroup.shapes[0] as GraphicElement.PolyStar
    assertThat(star.type).isEqualTo(ShapeType.PolyStar)
    assertThat(star.starType).isEqualTo(PolyStarType.Star)
    assertThat(star.points.animated).isFalse()
    assertThat((star.points as StaticScalarProperty).value).isEqualTo(5f)
    assertThat((star.outerRadius as StaticScalarProperty).value).isEqualTo(26f)
    assertThat((star.innerRadius as StaticScalarProperty).value).isEqualTo(13f)

    val polygonGroup = shapeLayer.shapes[1] as GraphicElement.Group
    val polygon = polygonGroup.shapes[0] as GraphicElement.PolyStar
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
  fun animatedColorProperty_deserializes() {
    // BEFORE: `GraphicElement.Fill.color` was hardcoded to `StaticColorProperty`.
    // It expected `"k"` to be a flat float[] like `[1, 0, 0, 1]`.
    // When encountering `"a": 1` (animated) where `"k"` contains keyframe objects,
    // kotlinx.serialization would throw an exception because it expected a float array.
    //
    // AFTER: `color` is now `BaseColorProperty` and evaluates `"a"`.
    // It delegates to `AnimatedColorProperty` which successfully parses keyframe structures.
    val context = ApplicationProvider.getApplicationContext<Context>()
    val animation = Animation.load(R.raw.animated_color, context)

    val shapeLayer = animation.layers[0] as Layer.ShapeLayer
    val fill = shapeLayer.shapes[0] as GraphicElement.Fill

    assertThat(fill.color.animated).isTrue()
    val animatedColor = fill.color as AnimatedColorProperty
    assertThat(animatedColor.keyframes).hasSize(2)
    assertThat(animatedColor.keyframes[0].frame).isEqualTo(0f)
    assertThat(animatedColor.keyframes[0].value).isEqualTo(Color.RED)
    assertThat(animatedColor.keyframes[1].frame).isEqualTo(60f)
    assertThat(animatedColor.keyframes[1].value).isEqualTo(Color.GREEN)
  }

  @Test
  fun splitPositionProperty_deserializes() {
    // BEFORE: `PositionProperty` implementations strictly expected an array `k` representing
    // `[x,y]`.
    // When After Effects exported separated dimensions (`"s": true`), the JSON provided `"x"` and
    // `"y"`
    // objects instead. This caused parsers lacking polymorphic support to crash since `"k"` was
    // missing
    // or incorrectly typed.
    //
    // AFTER: `BasePositionPropertySerializer` inspects `"s"`.
    // It properly hands off parsing to `SplitPositionProperty`, preserving independent x/y
    // properties.
    val context = ApplicationProvider.getApplicationContext<Context>()
    val animation = Animation.load(R.raw.split_position, context)

    val shapeLayer = animation.layers[0] as Layer.ShapeLayer
    val rect = shapeLayer.shapes[0] as GraphicElement.Rectangle

    assertThat(rect.position.animated).isTrue()
    val splitPos = rect.position as SplitPositionProperty
    assertThat(splitPos.x.animated).isFalse()
    assertThat((splitPos.x as StaticScalarProperty).value).isEqualTo(15f)
    assertThat(splitPos.y.animated).isTrue()
  }

  @Test
  fun vectorKeyframeSpatialTangents_deserializes() {
    // BEFORE: Spatial tangents (`"ti"`, `"to"`) were not consistently modeled on all
    // multi-dimensional properties.
    // If a property like a 2D Position suddenly gained spatial tangents (due to a bezier path
    // interpolation),
    // strict decoders would throw "Unknown key ti" or fail to cast.
    //
    // AFTER: `VectorPropertyKeyframe` accepts `ti` and `to` optionally.
    // This removes the need for complicated secondary data classes and safely captures the tangents
    // without crashing.
    val context = ApplicationProvider.getApplicationContext<Context>()
    val animation = Animation.load(R.raw.spatial_tangents, context)

    val shapeLayer = animation.layers[0] as Layer.ShapeLayer
    val rect = shapeLayer.shapes[0] as GraphicElement.Rectangle
    val animPos = rect.position as AnimatedPositionProperty

    val kf = animPos.keyframes[0]
    assertThat(kf.inSpatialTangent).isEqualTo(floatArrayOf(1f, 2f))
    assertThat(kf.outSpatialTangent).isEqualTo(floatArrayOf(3f, 4f))
  }

  @Test
  fun gradientValue_deserializes() {
    val json =
      """
      {
        "p": 2,
        "k": [0, 1, 0, 0, 1, 0, 0, 1]
      }
      """
        .trimIndent()

    val gradient = Json.decodeFromString(GradientValue.serializer(), json)
    assertThat(gradient.numberOfColors).isEqualTo(2)
    assertThat(gradient.stops).isEqualTo(floatArrayOf(0f, 1f, 0f, 0f, 1f, 0f, 0f, 1f))
  }

  @Test
  fun scalarPropertySerialization_emitsArray() {
    val prop = StaticScalarProperty(value = 42f)
    val json = Json.encodeToString(StaticScalarProperty.serializer(), prop)
    assertThat(json).contains("\"k\":[42.0]")
  }
}
