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

import android.graphics.Path
import android.graphics.PathMeasure
import com.google.android.horologist.remotecompose.lottie.format.LottieDecoder
import com.google.android.horologist.remotecompose.lottie.format.properties.BasePositionPropertySerializer
import com.google.android.horologist.remotecompose.lottie.format.properties.BaseVectorPropertySerializer
import kotlin.math.roundToInt
import org.junit.Test

class PositionInterpolationTest : MotionPixelHarness() {
  // [SP_MOTION_01] The symmetric spatial cubic reaches (32,8), not the straight-line midpoint.
  @Test
  fun followsSpatialTangentsAtMidpointAndRetainsEndpoints() {
    val progress = show(markerAt(animated("[8,32]", "[56,32]", extra = tangents)))
    assertPixels(Probe(8, 32, 1f), Probe(32, 8, 0f))
    advance(progress, 5f)
    assertPixels(Probe(32, 8, 1f), Probe(32, 32, 0f), Probe(8, 32, 0f))
    advance(progress, 10f)
    assertPixels(Probe(56, 32, 1f), Probe(32, 8, 0f))
  }

  // [SP_MOTION_02] A hold keyframe ignores spatial tangents until the next keyframe.
  @Test
  fun holdsPositionDespiteSpatialTangentsUntilNextKeyframe() {
    val progress =
      show(markerAt(animated("[8,32]", "[56,32]", easing = "\"h\":1", extra = tangents)))
    advance(progress, 5f)
    assertPixels(Probe(8, 32, 1f), Probe(32, 8, 0f))
    advance(progress, 9f)
    assertPixels(Probe(8, 32, 1f), Probe(56, 32, 0f))
    advance(progress, 10f)
    assertPixels(Probe(56, 32, 1f), Probe(8, 32, 0f))
  }

  // [SP_MOTION_03] Missing spatial tangents preserves scalar linear easing on both dimensions.
  @Test
  fun movesLinearlyWhenSpatialTangentsAreAbsent() {
    val progress = show(markerAt(animated("[8,8]", "[56,56]")))
    advance(progress, 5f)
    assertPixels(Probe(32, 32, 1f), Probe(14, 50, 0f))
  }

  // [SP_MOTION_04] Position axes use their own easing: half-time factors are (0.125,0.875).
  @Test
  fun easesLayerPositionIndependentlyOnEachAxis() {
    val progress = show(markerAt(animated("[8,8]", "[56,56]", axisEasing)))
    assertPixels(Probe(8, 8, 1f))
    advance(progress, 5f)
    assertPixels(Probe(14, 50, 1f), Probe(14, 14, 0f), Probe(32, 32, 0f))
    advance(progress, 10f)
    assertPixels(Probe(56, 56, 1f), Probe(14, 50, 0f))
  }

  // [SP_MOTION_05] General vector dimensions use independent easing, giving size (12,36).
  @Test
  fun easesRectangleWidthAndHeightIndependently() {
    val shape = rectangle(fixed("[32,32]"), animated("[8,8]", "[40,40]", axisEasing))
    val progress = show(animation("$shape,$redFill"))
    assertPixels(Probe(32, 32, 1f), Probe(32, 46, 0f))
    advance(progress, 5f)
    assertPixels(Probe(32, 46, 1f), Probe(42, 32, 0f), Probe(32, 52, 0f))
    advance(progress, 10f)
    assertPixels(Probe(46, 32, 1f), Probe(32, 46, 1f))
  }

  // [SP_MOTION_06] Shape geometry positions also retain independent vector-axis easing.
  @Test
  fun easesShapePositionIndependentlyOnEachAxis() {
    val position = animated("[8,8]", "[56,56]", axisEasing)
    val progress = show(animation("${rectangle(position, fixed("[6,6]"))},$redFill"))
    advance(progress, 5f)
    assertPixels(Probe(14, 50, 1f), Probe(14, 14, 0f), Probe(32, 32, 0f))
  }

  // [SP_MOTION_07] Spatial progress measures distance along an asymmetric cubic, as in
  // Lottie-Android v6.7.1 PathKeyframeAnimation, rather than using progress as the cubic parameter.
  @Test
  fun traversesAsymmetricSpatialCurveByArcLengthAtQuarterFrames() {
    val position = animated("[8,48]", "[56,48]", extra = """, "to":[0,-44],"ti":[-48,-44]""")
    val progress = show(markerAt(position))
    val referencePath =
      Path().apply {
        moveTo(8f, 48f)
        cubicTo(8f, 4f, 8f, 4f, 56f, 48f)
      }
    val measure = PathMeasure(referencePath, false)
    val point = FloatArray(2)
    for (fraction in listOf(0.25f, 0.75f)) {
      check(measure.getPosTan(measure.length * fraction, point, null))
      advance(progress, fraction * 10f)
      assertPixels(
        Probe(point[0].roundToInt(), point[1].roundToInt(), 1f),
        Probe((8f + 48f * fraction).roundToInt(), 48, 0f),
      )
    }
  }

  // [SP_MOTION_08] Decoding then encoding a position preserves independent easing on both axes.
  @Test
  fun retainsPerAxisPositionEasingAfterJsonRoundTrip() {
    val property =
      LottieDecoder.json.decodeFromString(
        BasePositionPropertySerializer,
        animated("[8,8]", "[56,56]", axisEasing),
      )
    val encoded = LottieDecoder.json.encodeToString(BasePositionPropertySerializer, property)
    val progress = show(markerAt(encoded))
    advance(progress, 5f)
    assertPixels(Probe(14, 50, 1f), Probe(14, 14, 0f), Probe(32, 32, 0f))
  }

  // [SP_MOTION_09] General vector serialization preserves easing for every dimension as well.
  @Test
  fun retainsPerDimensionSizeEasingAfterJsonRoundTrip() {
    val property =
      LottieDecoder.json.decodeFromString(
        BaseVectorPropertySerializer,
        animated("[8,8]", "[40,40]", axisEasing),
      )
    val encoded = LottieDecoder.json.encodeToString(BaseVectorPropertySerializer, property)
    val progress = show(animation("${rectangle(fixed("[32,32]"), encoded)},$redFill"))
    advance(progress, 5f)
    assertPixels(Probe(32, 46, 1f), Probe(42, 32, 0f))
  }

  // [SP_MOTION_10] A scalar easing handle applies the same nonlinear factor to both position axes.
  @Test
  fun appliesScalarEasingToBothPositionAxes() {
    val easing = """"o":{"x":0.333333333,"y":0},"i":{"x":0.666666667,"y":0}"""
    val progress = show(markerAt(animated("[8,8]", "[56,56]", easing)))
    advance(progress, 5f)
    assertPixels(Probe(14, 14, 1f), Probe(32, 32, 0f), Probe(14, 50, 0f))
  }

  // [SP_MOTION_11] A singleton easing array supplies its first value for both position dimensions.
  @Test
  fun reusesSingletonArrayEasingForBothPositionAxes() {
    val progress = show(markerAt(animated("[8,8]", "[56,56]", singletonEasing)))
    advance(progress, 5f)
    assertPixels(Probe(14, 14, 1f), Probe(32, 32, 0f), Probe(14, 50, 0f))
  }

  // [SP_MOTION_12] A singleton easing array supplies its first value for both vector dimensions.
  @Test
  fun reusesSingletonArrayEasingForWidthAndHeight() {
    val shape = rectangle(fixed("[32,32]"), animated("[8,8]", "[40,40]", singletonEasing))
    val progress = show(animation("$shape,$redFill"))
    advance(progress, 5f)
    assertPixels(Probe(36, 32, 1f), Probe(32, 36, 1f), Probe(42, 32, 0f), Probe(32, 42, 0f))
  }

  // [SP_MOTION_13] Eased distance beyond the path extends along the final unit tangent.
  @Test fun extendsSpatialOvershootAlongTheEndTangent() = assertSpatialOvershoot(1.5f, 1.25f)

  // [SP_MOTION_14] Negative eased distance extends backwards along the initial unit tangent.
  @Test
  fun extendsSpatialUndershootBackwardsAlongTheStartTangent() =
    assertSpatialOvershoot(-0.5f, -0.25f)

  private fun assertSpatialOvershoot(handleY: Float, easedFraction: Float) {
    val easing = """"o":{"x":0.333333333,"y":$handleY},"i":{"x":0.666666667,"y":$handleY}"""
    val position = animated("[16,24]", "[48,24]", easing, """, "to":[0,-16],"ti":[0,-16]""")
    val progress = show(markerAt(position))
    val path =
      Path().apply {
        moveTo(16f, 24f)
        cubicTo(16f, 8f, 48f, 8f, 48f, 24f)
      }
    val measure = PathMeasure(path, false)
    val endpoint = FloatArray(2)
    val tangent = FloatArray(2)
    val endpointDistance = if (easedFraction < 0f) 0f else measure.length
    check(measure.getPosTan(endpointDistance, endpoint, tangent))
    val extraDistance = measure.length * easedFraction - endpointDistance
    val x = (endpoint[0] + tangent[0] * extraDistance).roundToInt()
    val y = (endpoint[1] + tangent[1] * extraDistance).roundToInt()
    advance(progress, 5f)
    assertPixels(Probe(x, y, 1f), Probe(endpoint[0].roundToInt(), endpoint[1].roundToInt(), 0f))
  }

  private fun markerAt(position: String): String =
    animation("${rectangle(fixed("[0,0]"), fixed("[6,6]"))},$redFill", position)

  private val tangents = """, "to":[0,-32],"ti":[0,-32]"""

  private val singletonEasing =
    """"o":{"x":[0.333333333],"y":[0]},"i":{"x":[0.666666667],"y":[0]}"""

  private val axisEasing =
    """"o":{"x":[0.333333333,0.333333333],"y":[0,1]},
      "i":{"x":[0.666666667,0.666666667],"y":[0,1]}"""
}
