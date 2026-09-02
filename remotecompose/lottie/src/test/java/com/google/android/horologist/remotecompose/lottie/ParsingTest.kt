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
import androidx.compose.remote.creation.compose.state.rc
import androidx.compose.remote.creation.compose.state.rf
import androidx.compose.ui.graphics.Color
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
import com.google.android.horologist.remotecompose.lottie.format.values.ColorStop
import com.google.android.horologist.remotecompose.lottie.format.values.GradientValue
import com.google.android.horologist.remotecompose.lottie.format.values.GradientValueSerializer
import com.google.android.horologist.remotecompose.lottie.format.values.Point
import com.google.android.horologist.remotecompose.lottie.renderer.properties.animateScalar
import com.google.android.horologist.remotecompose.lottie.renderer.properties.toRemote
import com.google.common.truth.Truth.assertThat
import kotlinx.serialization.SerializationException
import org.junit.Assert.assertThrows
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
    assertThat(bezier0.vertices).containsExactly(Point(10f, 20f))

    val jsonOne = """{"c": 1, "v": [[10.0, 20.0]], "i": [[0.0, 0.0]], "o": [[0.0, 0.0]]}"""
    val bezier1 = LottieDecoder.json.decodeFromString(BezierValue.serializer(), jsonOne)
    assertThat(bezier1.closed).isTrue()
    assertThat(bezier1.vertices).containsExactly(Point(10f, 20f))
  }

  @Test
  fun bezierValue_booleanClosedFlag_deserializes() {
    val jsonFalse = """{"c": false, "v": [], "i": [], "o": []}"""
    val bezierFalse = LottieDecoder.json.decodeFromString(BezierValue.serializer(), jsonFalse)
    assertThat(bezierFalse.closed).isFalse()
    assertThat(bezierFalse.vertices).isEmpty()

    val jsonTrue = """{"c": true, "v": [], "i": [], "o": []}"""
    val bezierTrue = LottieDecoder.json.decodeFromString(BezierValue.serializer(), jsonTrue)
    assertThat(bezierTrue.closed).isTrue()

    val jsonNull = """{"c": null, "v": [], "i": [], "o": []}"""
    val bezierNull = LottieDecoder.json.decodeFromString(BezierValue.serializer(), jsonNull)
    assertThat(bezierNull.closed).isFalse()
  }

  @Test
  fun bezierValue_invalidClosedFlag_throwsSerializationException() {
    assertThrows(SerializationException::class.java) {
      LottieDecoder.json.decodeFromString(
        BezierValue.serializer(),
        """{"c": "invalid", "v": [], "i": [], "o": []}""",
      )
    }

    assertThrows(SerializationException::class.java) {
      LottieDecoder.json.decodeFromString(
        BezierValue.serializer(),
        """{"c": {}, "v": [], "i": [], "o": []}""",
      )
    }

    assertThrows(SerializationException::class.java) {
      LottieDecoder.json.decodeFromString(
        BezierValue.serializer(),
        """{"c": [1], "v": [], "i": [], "o": []}""",
      )
    }

    assertThrows(SerializationException::class.java) {
      LottieDecoder.json.decodeFromString(
        BezierValue.serializer(),
        """{"c": 1.5, "v": [], "i": [], "o": []}""",
      )
    }
  }

  @Test
  fun bezierValue_missingRequiredFields_throwsSerializationException() {
    assertThrows(SerializationException::class.java) {
      LottieDecoder.json.decodeFromString(BezierValue.serializer(), """{}""")
    }
    assertThrows(SerializationException::class.java) {
      LottieDecoder.json.decodeFromString(
        BezierValue.serializer(),
        """{"v": [], "i": []}""", // missing "o"
      )
    }
  }

  @Test
  fun bezierValue_malformedCoordinates_throwsSerializationException() {
    // 1-element point array (< 2 coordinates)
    val json1D = """{"v": [[10.0]], "i": [[0.0, 0.0]], "o": [[0.0, 0.0]]}"""
    assertThrows(SerializationException::class.java) {
      LottieDecoder.json.decodeFromString(BezierValue.serializer(), json1D)
    }

    // Empty point array (< 2 coordinates)
    val jsonEmpty = """{"v": [[]], "i": [[0.0, 0.0]], "o": [[0.0, 0.0]]}"""
    assertThrows(SerializationException::class.java) {
      LottieDecoder.json.decodeFromString(BezierValue.serializer(), jsonEmpty)
    }

    // Non-array point coordinate
    val jsonNonArray = """{"v": [10.0], "i": [[0.0, 0.0]], "o": [[0.0, 0.0]]}"""
    assertThrows(SerializationException::class.java) {
      LottieDecoder.json.decodeFromString(BezierValue.serializer(), jsonNonArray)
    }

    // Non-float coordinate
    val jsonNonFloat = """{"v": [["invalid", "coord"]], "i": [[0.0, 0.0]], "o": [[0.0, 0.0]]}"""
    assertThrows(SerializationException::class.java) {
      LottieDecoder.json.decodeFromString(BezierValue.serializer(), jsonNonFloat)
    }
  }

  @Test
  fun bezierValue_extraCoordinates_discards3DExtraDimensions() {
    // 3D coordinates (length 3): extracts x and y, discards 3rd coordinate
    val json3D = """{"v": [[10.0, 20.0, 30.0]], "i": [[1.0, 2.0, 0.0]], "o": [[3.0, 4.0, 0.0]]}"""
    val bezier = LottieDecoder.json.decodeFromString(BezierValue.serializer(), json3D)
    assertThat(bezier.vertices).containsExactly(Point(10f, 20f))
    assertThat(bezier.inTangents).containsExactly(Point(1f, 2f))
    assertThat(bezier.outTangents).containsExactly(Point(3f, 4f))

    // Serializes strictly as 2D coordinates [x, y]
    val serialized = LottieDecoder.json.encodeToString(BezierValue.serializer(), bezier)
    assertThat(serialized).contains(""""v":[[10.0,20.0]]""")
    assertThat(serialized).contains(""""i":[[1.0,2.0]]""")
    assertThat(serialized).contains(""""o":[[3.0,4.0]]""")
  }

  @Test
  fun bezierValue_toRemote_convertsToRemoteFloats() {
    val bezier =
      BezierValue(
        closed = true,
        inTangents = listOf(Point(1f, 2f)),
        outTangents = listOf(Point(3f, 4f)),
        vertices = listOf(Point(10f, 20f)),
      )
    val remote = bezier.toRemote()
    assertThat(remote.closed).isTrue()
    assertThat(remote.vertices).hasSize(1)
    assertThat(remote.vertices[0].x.constantValueOrNull).isEqualTo(10f)
    assertThat(remote.vertices[0].y.constantValueOrNull).isEqualTo(20f)
    assertThat(remote.inTangents[0].x.constantValueOrNull).isEqualTo(1f)
    assertThat(remote.inTangents[0].y.constantValueOrNull).isEqualTo(2f)
    assertThat(remote.outTangents[0].x.constantValueOrNull).isEqualTo(3f)
    assertThat(remote.outTangents[0].y.constantValueOrNull).isEqualTo(4f)
  }

  @Test
  fun bezierValue_roundTripSerialization_emitsCanonicalBooleanClosedFlag() {
    val jsonIntegerClosed = """{"c":1,"v":[[10.0,20.0]],"i":[[0.0,0.0]],"o":[[0.0,0.0]]}"""
    val deserialized =
      LottieDecoder.json.decodeFromString(BezierValue.serializer(), jsonIntegerClosed)
    val serialized = LottieDecoder.json.encodeToString(BezierValue.serializer(), deserialized)

    // Emits canonical boolean "c":true
    assertThat(serialized).contains(""""c":true""")
    val roundTripped = LottieDecoder.json.decodeFromString(BezierValue.serializer(), serialized)
    assertThat(roundTripped).isEqualTo(deserialized)
  }

  @Test
  fun gradientValue_flatFloatArray_decodesColorCountAndValues() {
    val json = "[0.0, 1.0, 0.0, 0.0, 1.0, 0.0, 1.0, 0.0]"
    val gradientValue = LottieDecoder.json.decodeFromString(GradientValue.serializer(), json)

    assertThat(gradientValue.colorStops).hasSize(2)
    assertThat(gradientValue.transparencyStops).isEmpty()

    val colorStart = gradientValue.getColorForPosition(0f).constantValueOrNull
    assertThat(colorStart?.red).isEqualTo(1f)
    assertThat(colorStart?.green).isEqualTo(0f)
    assertThat(colorStart?.blue).isEqualTo(0f)
    assertThat(colorStart?.alpha).isEqualTo(1f)

    val colorEnd = gradientValue.getColorForPosition(1f).constantValueOrNull
    assertThat(colorEnd?.red).isEqualTo(0f)
    assertThat(colorEnd?.green).isEqualTo(1f)
    assertThat(colorEnd?.blue).isEqualTo(0f)
    assertThat(colorEnd?.alpha).isEqualTo(1f)
  }

  @Test
  fun gradientValue_transparentFloatArray_resolvesColorAndOpacityStops() {
    val json =
      """
      {
        "p": 2,
        "k": [0.0, 1.0, 0.0, 0.0, 1.0, 0.0, 1.0, 0.0, 0.0, 1.0, 1.0, 0.5]
      }
      """
        .trimIndent()
    val gradientValue = LottieDecoder.json.decodeFromString(GradientValue.serializer(), json)

    assertThat(gradientValue.colorStops).hasSize(2)
    assertThat(gradientValue.transparencyStops).hasSize(2)

    val colorStart = gradientValue.getColorForPosition(0f).constantValueOrNull
    assertThat(colorStart?.red).isEqualTo(1f)
    assertThat(colorStart?.alpha).isEqualTo(1f)

    val colorEnd = gradientValue.getColorForPosition(1f).constantValueOrNull
    assertThat(colorEnd?.green).isEqualTo(1f)
    assertThat(colorEnd?.alpha).isWithin(0.01f).of(0.5f)
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

    assertThat(gradientValue.colorStops).hasSize(3)
    assertThat(gradientValue.transparencyStops).isEmpty()
    val colorMid = gradientValue.getColorForPosition(0.5f).constantValueOrNull
    assertThat(colorMid?.green).isEqualTo(1f)
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
    val colorStart = gradientValue.getColorForPosition(0f).constantValueOrNull
    assertThat(colorStart?.red).isEqualTo(1f)
    assertThat(colorStart?.alpha).isEqualTo(1f)
    val colorEnd = gradientValue.getColorForPosition(1f).constantValueOrNull
    assertThat(colorEnd?.green).isEqualTo(1f)
    assertThat(colorEnd?.alpha).isWithin(0.01f).of(128f / 255f)
  }

  // [SP_LOT_VAL_02_03_01]
  @Test
  fun gradientValue_getColorForPosition_returnsTransparentWhenDegenerate() {
    val gradient = GradientValue(emptyList(), emptyList())
    val color = gradient.getColorForPosition(0.5f).constantValueOrNull
    assertThat(color?.alpha).isEqualTo(0f)
  }

  // [SP_LOT_VAL_02_03_02]
  @Test
  fun gradientValue_getColorForPosition_returnsSolidColorWhenSingleStop() {
    val gradient =
      GradientValue(
        colorStops = listOf(ColorStop(0f, Color(1f, 0f, 0f).rc)),
        transparencyStops = emptyList(),
      )
    val color = gradient.getColorForPosition(0.5f).constantValueOrNull
    assertThat(color?.red).isEqualTo(1f)
    assertThat(color?.green).isEqualTo(0f)
    assertThat(color?.blue).isEqualTo(0f)
    assertThat(color?.alpha).isEqualTo(1f)

    val colorBelow = gradient.getColorForPosition(-0.5f).constantValueOrNull
    assertThat(colorBelow?.red).isEqualTo(1f)
    val colorAbove = gradient.getColorForPosition(1.5f).constantValueOrNull
    assertThat(colorAbove?.red).isEqualTo(1f)
  }

  // [SP_LOT_VAL_02_03_03] [SP_LOT_VAL_02_03_04] [SP_LOT_VAL_02_03_05] [SP_LOT_VAL_02_03_06]
  // [SP_LOT_VAL_02_03_07]
  @Test
  fun gradientValue_getColorForPosition_evaluatesTwoStopOpaqueGradient() {
    // 0.0 -> Black (0, 0, 0), 1.0 -> White (1, 1, 1)
    val json = "[0.0, 0.0, 0.0, 0.0, 1.0, 1.0, 1.0, 1.0]"
    val gradient = LottieDecoder.json.decodeFromString(GradientValue.serializer(), json)

    // Underflow clamped to start stop
    val underflow = gradient.getColorForPosition(-0.2f).constantValueOrNull
    assertThat(underflow?.red).isEqualTo(0f)
    assertThat(underflow?.alpha).isEqualTo(1f)

    // Exact lower bound
    val start = gradient.getColorForPosition(0.0f).constantValueOrNull
    assertThat(start?.red).isEqualTo(0f)
    assertThat(start?.green).isEqualTo(0f)
    assertThat(start?.blue).isEqualTo(0f)
    assertThat(start?.alpha).isEqualTo(1f)

    // Midpoint linear interpolation
    val mid = gradient.getColorForPosition(0.5f).constantValueOrNull
    assertThat(mid?.red).isWithin(0.01f).of(0.5f)
    assertThat(mid?.green).isWithin(0.01f).of(0.5f)
    assertThat(mid?.blue).isWithin(0.01f).of(0.5f)
    assertThat(mid?.alpha).isEqualTo(1f)

    // Exact upper bound
    val end = gradient.getColorForPosition(1.0f).constantValueOrNull
    assertThat(end?.red).isEqualTo(1f)
    assertThat(end?.green).isEqualTo(1f)
    assertThat(end?.blue).isEqualTo(1f)
    assertThat(end?.alpha).isEqualTo(1f)

    // Overflow clamped to end stop
    val overflow = gradient.getColorForPosition(1.2f).constantValueOrNull
    assertThat(overflow?.red).isEqualTo(1f)
    assertThat(overflow?.alpha).isEqualTo(1f)
  }

  // [SP_LOT_VAL_02_03_08] [SP_LOT_VAL_02_03_09]
  @Test
  fun gradientValue_getColorForPosition_evaluatesMultiSegmentStops() {
    // 0.0: Red, 0.5: Green, 1.0: Blue
    val json =
      """
      {
        "p": 3,
        "k": [0.0, 1.0, 0.0, 0.0, 0.5, 0.0, 1.0, 0.0, 1.0, 0.0, 0.0, 1.0]
      }
      """
        .trimIndent()
    val gradient = LottieDecoder.json.decodeFromString(GradientValue.serializer(), json)

    // At 0.25 (between Red and Green): r=0.5, g=0.5, b=0.0
    val firstHalf = gradient.getColorForPosition(0.25f).constantValueOrNull
    assertThat(firstHalf?.red).isWithin(0.01f).of(0.5f)
    assertThat(firstHalf?.green).isWithin(0.01f).of(0.5f)
    assertThat(firstHalf?.blue).isEqualTo(0f)

    // At 0.75 (between Green and Blue): r=0.0, g=0.5, b=0.5
    val secondHalf = gradient.getColorForPosition(0.75f).constantValueOrNull
    assertThat(secondHalf?.red).isEqualTo(0f)
    assertThat(secondHalf?.green).isWithin(0.01f).of(0.5f)
    assertThat(secondHalf?.blue).isWithin(0.01f).of(0.5f)
  }

  // [SP_LOT_VAL_02_03_10]
  @Test
  fun gradientValue_getColorForPosition_interpolatesOpaqueAndTranslucentColors() {
    // Colors: 0.0 (Red), 1.0 (Blue). Opacity: 0.0 (alpha 0.0), 1.0 (alpha 1.0)
    val json =
      """
      {
        "p": 2,
        "k": [0.0, 1.0, 0.0, 0.0, 1.0, 0.0, 0.0, 1.0, 0.0, 0.0, 1.0, 1.0]
      }
      """
        .trimIndent()
    val gradient = LottieDecoder.json.decodeFromString(GradientValue.serializer(), json)

    val mid = gradient.getColorForPosition(0.5f).constantValueOrNull
    assertThat(mid?.red).isWithin(0.01f).of(0.5f)
    assertThat(mid?.blue).isWithin(0.01f).of(0.5f)
    assertThat(mid?.alpha).isWithin(0.01f).of(0.5f)
  }

  // [SP_LOT_VAL_02_03_11] [SP_LOT_VAL_02_03_12] [SP_LOT_VAL_02_03_13]
  @Test
  fun gradientValue_getColorForPosition_handlesIndependentOpacityOffsets() {
    // Colors: 0.0 (Red) -> 1.0 (Blue)
    // Opacities: 0.25 (alpha 0.8) -> 0.75 (alpha 0.4)
    val json =
      """
      {
        "p": 2,
        "k": [
          0.0, 1.0, 0.0, 0.0,
          1.0, 0.0, 0.0, 1.0,
          0.25, 0.8,
          0.75, 0.4
        ]
      }
      """
        .trimIndent()
    val gradient = LottieDecoder.json.decodeFromString(GradientValue.serializer(), json)

    // Position 0.1: below opacity start (0.25), clamped to 0.8
    val c01 = gradient.getColorForPosition(0.1f).constantValueOrNull
    assertThat(c01?.alpha).isWithin(0.01f).of(0.8f)

    // Position 0.5: midpoint of opacity [0.25, 0.75] -> 0.6
    val c05 = gradient.getColorForPosition(0.5f).constantValueOrNull
    assertThat(c05?.alpha).isWithin(0.01f).of(0.6f)

    // Position 0.9: above opacity end (0.75), clamped to 0.4
    val c09 = gradient.getColorForPosition(0.9f).constantValueOrNull
    assertThat(c09?.alpha).isWithin(0.01f).of(0.4f)
  }

  // [SP_LOT_VAL_02_03_14]
  @Test
  fun gradientValue_getColorForPosition_handlesCoincidentStopsWithoutDivisionByZero() {
    // Zero-width step stop at 0.5: Red at 0.5 and Blue at 0.5
    val json =
      """
      {
        "p": 2,
        "k": [0.5, 1.0, 0.0, 0.0, 0.5, 0.0, 0.0, 1.0]
      }
      """
        .trimIndent()
    val gradient = LottieDecoder.json.decodeFromString(GradientValue.serializer(), json)
    val color = gradient.getColorForPosition(0.5f).constantValueOrNull
    assertThat(color).isNotNull()
  }

  // [SP_LOT_VAL_02_03_15]
  @Test
  fun gradientValue_getColorForPosition_normalizes255ScaledValues() {
    val json =
      """
      {
        "p": 2,
        "k": [0.0, 255.0, 0.0, 0.0, 1.0, 0.0, 255.0, 0.0, 0.0, 255.0, 1.0, 128.0]
      }
      """
        .trimIndent()
    val gradient = LottieDecoder.json.decodeFromString(GradientValue.serializer(), json)

    val c0 = gradient.getColorForPosition(0.0f).constantValueOrNull
    assertThat(c0?.red).isEqualTo(1f)
    assertThat(c0?.alpha).isEqualTo(1f)

    val c1 = gradient.getColorForPosition(1.0f).constantValueOrNull
    assertThat(c1?.green).isEqualTo(1f)
    assertThat(c1?.alpha).isWithin(0.01f).of(128f / 255f)
  }

  @Test
  fun gradientValue_invalidValuesLength_throwsSerializationException() {
    // p = 2 requires at least 8 floats; only 7 provided
    val json =
      """
      {
        "p": 2,
        "k": [0.0, 1.0, 0.0, 0.0, 1.0, 0.0, 1.0]
      }
      """
        .trimIndent()
    assertThrows(SerializationException::class.java) {
      LottieDecoder.json.decodeFromString(GradientValue.serializer(), json)
    }
  }

  @Test
  fun gradientValue_invalidOpacityLength_throwsSerializationException() {
    // p = 2 (8 color floats) + 1 float left over (not a multiple of 2 for opacity pairs)
    val json =
      """
      {
        "p": 2,
        "k": [0.0, 1.0, 0.0, 0.0, 1.0, 0.0, 1.0, 0.0, 0.5]
      }
      """
        .trimIndent()
    assertThrows(SerializationException::class.java) {
      LottieDecoder.json.decodeFromString(GradientValue.serializer(), json)
    }
  }

  @Test
  fun gradientValue_nonNumberCoordinate_throwsSerializationException() {
    val json =
      """
      {
        "p": 1,
        "k": [0.0, "red", 0.0, 0.0]
      }
      """
        .trimIndent()
    assertThrows(SerializationException::class.java) {
      LottieDecoder.json.decodeFromString(GradientValue.serializer(), json)
    }
  }

  @Test
  fun gradientValue_serialization_roundTrip() {
    val original =
      GradientValue(
        colorStops =
          listOf(ColorStop(0.0f, Color(1f, 0f, 0f).rc), ColorStop(1.0f, Color(0f, 1f, 0f).rc)),
        transparencyStops = emptyList(),
      )
    val serialized = LottieDecoder.json.encodeToString(GradientValue.serializer(), original)
    val deserialized = LottieDecoder.json.decodeFromString(GradientValue.serializer(), serialized)
    assertThat(deserialized.colorStops.size).isEqualTo(original.colorStops.size)
    assertThat(deserialized.colorStops.map { it.offset })
      .isEqualTo(original.colorStops.map { it.offset })
  }

  @Test
  fun gradientValue_invalidOpaqueArrayLength_throwsSerializationException() {
    val json = "[0.0, 1.0, 0.0]"
    assertThrows(SerializationException::class.java) {
      LottieDecoder.json.decodeFromString(GradientValue.serializer(), json)
    }
  }

  @Test
  fun gradientValue_negativeColorStopCount_throwsSerializationException() {
    val json = """{"p": -1, "k": [0.0, 1.0, 0.0, 0.0]}"""
    assertThrows(SerializationException::class.java) {
      LottieDecoder.json.decodeFromString(GradientValue.serializer(), json)
    }
  }

  @Test
  fun gradientValue_invalidJsonType_throwsSerializationException() {
    val jsonString = "\"not a gradient\""
    assertThrows(SerializationException::class.java) {
      LottieDecoder.json.decodeFromString(GradientValue.serializer(), jsonString)
    }
  }

  @Test
  fun gradientValue_emptyArray_deserializesEmpty() {
    val json = "[]"
    val gradient = LottieDecoder.json.decodeFromString(GradientValue.serializer(), json)
    assertThat(gradient.colorStops).isEmpty()
    assertThat(gradient.transparencyStops).isEmpty()
  }

  @Test
  fun createGradientValue_infersCountFromValues() {
    val values = listOf(0f, 1f, 0f, 0f, 1f, 0f, 1f, 0f)
    val gradient =
      GradientValueSerializer.createGradientValue(values = values, colorStopCount = null)
    assertThat(gradient.colorStops).hasSize(2)
  }

  @Test
  fun createGradientValue_explicitCount_validatesAndConstructs() {
    val values = listOf(0f, 1f, 0f, 0f, 1f, 0.5f)
    val gradient = GradientValueSerializer.createGradientValue(values = values, colorStopCount = 1)
    assertThat(gradient.colorStops).hasSize(1)
    assertThat(gradient.transparencyStops).hasSize(1)
  }

  @Test
  fun createGradientValue_invalidInferredLength_throwsSerializationException() {
    val values = listOf(0f, 1f, 0f)
    assertThrows(SerializationException::class.java) {
      GradientValueSerializer.createGradientValue(values = values, colorStopCount = null)
    }
  }

  @Test
  fun validateGradient_validGradientValue_succeeds() {
    val values = listOf(0f, 1f, 0f, 0f)
    GradientValueSerializer.validateGradient(colorStopCount = 1, values = values)
  }

  @Test
  fun validateGradient_negativeCount_throwsSerializationException() {
    assertThrows(SerializationException::class.java) {
      GradientValueSerializer.validateGradient(colorStopCount = -1, values = listOf(0f, 1f, 0f, 0f))
    }
  }

  @Test
  fun validateGradient_insufficientLength_throwsSerializationException() {
    val values = listOf(0f, 1f, 0f, 0f, 1f, 0f, 1f)
    assertThrows(SerializationException::class.java) {
      GradientValueSerializer.validateGradient(colorStopCount = 2, values = values)
    }
  }

  @Test
  fun validateGradient_oddOpacityStopsLength_throwsSerializationException() {
    val values = listOf(0f, 1f, 0f, 0f, 1f)
    assertThrows(SerializationException::class.java) {
      GradientValueSerializer.validateGradient(colorStopCount = 1, values = values)
    }
  }
}
