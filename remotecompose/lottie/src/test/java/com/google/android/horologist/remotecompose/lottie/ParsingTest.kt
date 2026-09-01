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
import com.google.android.horologist.remotecompose.lottie.renderer.animateScalar
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
}
