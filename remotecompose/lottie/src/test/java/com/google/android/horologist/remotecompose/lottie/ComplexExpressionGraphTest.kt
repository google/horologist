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
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.remote.core.Limits
import androidx.compose.remote.creation.Rc
import androidx.compose.remote.creation.compose.state.RemoteFloat
import androidx.compose.remote.creation.compose.state.rf
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.unit.dp
import com.github.takahirom.roborazzi.ExperimentalRoborazziApi
import com.github.takahirom.roborazzi.RoborazziOptions
import com.github.takahirom.roborazzi.RoborazziTaskType
import com.github.takahirom.roborazzi.captureRoboImage
import com.google.android.horologist.remotecompose.lottie.format.values.Point
import com.google.android.horologist.remotecompose.lottie.renderer.layers.calculateLocalFrame
import com.google.android.horologist.remotecompose.lottie.renderer.lookupValueInBezier
import com.google.android.horologist.remotecompose.lottie.renderer.properties.gammaLerp
import com.google.android.horologist.remotecompose.lottie.renderer.properties.sampleSpatialPosition
import com.google.android.horologist.remotecompose.lottie.renderer.shapes.repeaterCopyOpacity
import com.google.android.horologist.remotecompose.lottie.renderer.shapes.repeaterCopyScale
import com.google.android.horologist.remotecompose.lottie.renderer.shapes.roundedStraightParameter
import com.google.android.horologist.remotecompose.lottie.renderer.shapes.sliceCubic
import com.google.android.horologist.remotecompose.lottie.renderer.shapes.straightParameter
import com.google.android.horologist.screenshots.rng.WearScreenshotTest
import com.google.common.truth.Truth.assertThat
import com.google.common.truth.Truth.assertWithMessage
import java.io.File
import kotlin.math.roundToInt
import org.junit.Test

/**
 * Verifies every complex [RemoteFloat] expression shown in [ExpressionGraphPreviews], covering both
 * constant-input optimization paths and live [RemoteFloat] expression paths, as well as verifying
 * that [ExpressionGraphPreview] animates the live marker along the static reference curve across
 * progress steps without recomposing the document.
 */
@SuppressLint("RestrictedApi")
@OptIn(ExperimentalRoborazziApi::class)
class ComplexExpressionGraphTest : WearScreenshotTest() {
  init {
    Limits.MAX_OP_COUNT = 500_000
  }

  private fun assertExpressionGraphTracksReferenceCurve(
    name: String,
    expectedY: (Float) -> Float,
    liveY: (RemoteFloat) -> RemoteFloat,
    yMin: Float = 0f,
    yMax: Float = 1f,
    progressSteps: List<Float> = listOf(0.2f, 0.5f, 0.8f),
  ) {
    val progressState = mutableFloatStateOf(progressSteps.first())
    composeRule.setContent {
      Box(Modifier.size(120.dp).background(Color(0xFF111318)).testTag("graph")) {
        ExpressionGraphPreview(
          expectedY = expectedY,
          liveY = liveY,
          modifier = Modifier.size(120.dp),
          progress = progressState.floatValue,
          yMin = yMin,
          yMax = yMax,
        )
      }
    }

    val ySpan = (yMax - yMin).coerceAtLeast(1e-4f)
    for (p in progressSteps) {
      composeRule.runOnIdle { progressState.floatValue = p }
      val bitmap = captureGraph()
      try {
        val pad = GRAPH_PADDING
        val plotW = GRAPH_VIRTUAL_SIZE - 2f * pad
        val plotH = GRAPH_VIRTUAL_SIZE - 2f * pad
        val vx = pad + plotW * p
        val normY = ((expectedY(p) - yMin) / ySpan).coerceIn(-0.25f, 1.25f)
        val vy = pad + plotH * (1f - normY)
        val expectedPx =
          (vx * bitmap.width / GRAPH_VIRTUAL_SIZE).roundToInt().coerceIn(0, bitmap.width - 1)
        val expectedPy =
          (vy * bitmap.height / GRAPH_VIRTUAL_SIZE).roundToInt().coerceIn(0, bitmap.height - 1)

        // Verify the live marker (red/white fill & stroke) is centered at (expectedPx, expectedPy).
        var maxMarkerScore = 0f
        for (dy in -5..5) {
          for (dx in -5..5) {
            val x = (expectedPx + dx).coerceIn(0, bitmap.width - 1)
            val y = (expectedPy + dy).coerceIn(0, bitmap.height - 1)
            val c = Color(bitmap.getPixel(x, y))
            val score = c.red - c.blue * 0.5f
            if (score > maxMarkerScore) {
              maxMarkerScore = score
            }
          }
        }
        assertWithMessage(
            "$name at progress=$p: expected live marker near ($expectedPx, $expectedPy) for y=${expectedY(p)}"
          )
          .that(maxMarkerScore)
          .isGreaterThan(0.5f)
      } finally {
        bitmap.recycle()
      }
    }
  }

  private fun captureGraph(): Bitmap {
    composeRule.waitForIdle()
    val screenshot = File.createTempFile("expression-graph-", ".png")
    try {
      composeRule
        .onNodeWithTag("graph")
        .captureRoboImage(
          screenshot.absolutePath,
          roborazziOptions = RoborazziOptions(taskType = RoborazziTaskType.Record),
        )
      return checkNotNull(BitmapFactory.decodeFile(screenshot.absolutePath))
    } finally {
      screenshot.delete()
    }
  }

  // 1. Cubic Bezier S-Curve Easing (plus constant vs non-constant diagonal & non-diagonal paths)
  @Test
  fun cubicEasingSCurveMatchesExpectedAndCoversConstantAndLivePaths() {
    val animTime = RemoteFloat(Rc.Time.ANIMATION_TIME)
    val liveZero = animTime - animTime

    // Diagonal fast-path (a == b && c == d) with constant handles
    val constDiag = lookupValueInBezier(0.25f.rf, 0.25f.rf, 0.75f.rf, 0.75f.rf, 10f, 4f.rf)
    assertThat(constDiag.constantValue).isWithin(1e-4f).of(0.4f)

    // Diagonal handles with non-constant RemoteFloat expressions (exercises cubicEasing expression)
    val liveDiag =
      lookupValueInBezier(
        0.25f.rf + liveZero,
        0.25f.rf + liveZero,
        0.75f.rf + liveZero,
        0.75f.rf + liveZero,
        10f,
        4f.rf,
      )
    assertThat(liveDiag.constantValueOrNull).isNull()

    // Non-diagonal S-curve with constant and live handles rendered on the XY graph
    assertExpressionGraphTracksReferenceCurve(
      name = "CubicEasingSCurve",
      expectedY = { x -> ComplexExpressionSpecs.cubicBezierSolveY(0.42f, 0f, 0.58f, 1f, x) },
      liveY = { progress ->
        lookupValueInBezier(0.42f.rf + (progress - progress), 0f.rf, 0.58f.rf, 1f.rf, 1f, progress)
      },
    )
  }

  // 2. Overshooting Cubic Bezier Easing
  @Test
  fun cubicEasingOvershootMatchesExpectedCurveAboveOne() {
    val expectedMid = ComplexExpressionSpecs.cubicBezierSolveY(0.34f, 1.56f, 0.64f, 1f, 0.5f)
    assertThat(expectedMid).isGreaterThan(1.0f)

    assertExpressionGraphTracksReferenceCurve(
      name = "CubicEasingOvershoot",
      expectedY = { x -> ComplexExpressionSpecs.cubicBezierSolveY(0.34f, 1.56f, 0.64f, 1f, x) },
      liveY = { progress ->
        lookupValueInBezier(0.34f.rf, 1.56f.rf, 0.64f.rf, 1f.rf, 1f, progress)
      },
      yMin = -0.05f,
      yMax = 1.25f,
    )
  }

  // 3. Linear-Light sRGB Gamma Color Channel Interpolation
  @Test
  fun gammaColorLerpMatchesExpectedCurveAndConstantEvaluation() {
    val constMid = gammaLerp(0.05f.rf, 0.95f.rf, 0.5f.rf)
    assertThat(constMid.constantValue)
      .isWithin(1e-3f)
      .of(ComplexExpressionSpecs.expectedGammaLerp(0.05f, 0.95f, 0.5f))

    assertExpressionGraphTracksReferenceCurve(
      name = "GammaColorLerp",
      expectedY = { x -> ComplexExpressionSpecs.expectedGammaLerp(0.05f, 0.95f, x) },
      liveY = { progress -> gammaLerp(0.05f.rf, 0.95f.rf, progress) },
    )
  }

  // 4. Spatial Cubic Bezier Arc-Length Reparameterization (zero-length fast-path vs curved table)
  @Test
  fun spatialBezierArcLengthMatchesExpectedAndCoversLinearAndCurvedPaths() {
    // Zero-length fast-path (start == end, zero tangents) returns start directly
    val zeroLength =
      sampleSpatialPosition(
        start = Point(42f.rf, 84f.rf),
        end = Point(42f.rf, 84f.rf),
        outgoing = Point(0f.rf, 0f.rf),
        incoming = Point(0f.rf, 0f.rf),
        progress = 0.5f.rf,
      )
    assertThat(zeroLength.x.constantValue).isEqualTo(42f)
    assertThat(zeroLength.y.constantValue).isEqualTo(84f)

    assertExpressionGraphTracksReferenceCurve(
      name = "SpatialBezierArcLength",
      expectedY = { x -> ComplexExpressionSpecs.expectedSpatialArcLengthX(x) },
      liveY = { progress ->
        sampleSpatialPosition(
            start = Point(0f.rf, 0f.rf),
            end = Point(100f.rf, 100f.rf),
            outgoing = Point(85f.rf, 0f.rf),
            incoming = Point(0f.rf, (-85f).rf),
            progress = progress,
          )
          .x / 100f.rf
      },
    )
  }

  // 5. Straight Zero-Handle Cubic Arc-Length Parameter Inversion
  @Test
  fun straightTrimParameterInvertsZeroHandleCubicPolynomial() {
    for (u in listOf(0f, 0.25f, 0.5f, 0.75f, 1f)) {
      val t = straightParameter(u.rf).constantValue
      assertThat(t).isWithin(2e-2f).of(ComplexExpressionSpecs.expectedStraightParameter(u))
    }

    assertExpressionGraphTracksReferenceCurve(
      name = "StraightTrimParameter",
      expectedY = { x -> ComplexExpressionSpecs.expectedStraightParameter(x) },
      liveY = { progress -> straightParameter(progress) },
    )
  }

  // 6. Rounded-Corner Straight Cubic Piecewise Inversion
  @Test
  fun roundedStraightTrimParameterMatchesPiecewiseLinearInversion() {
    for (u in listOf(0f, 0.25f, 0.5f, 0.75f, 1f)) {
      val t = roundedStraightParameter(u.rf).constantValue
      assertThat(t).isWithin(1e-4f).of(ComplexExpressionSpecs.expectedRoundedStraightParameter(u))
    }

    assertExpressionGraphTracksReferenceCurve(
      name = "RoundedStraightTrimParameter",
      expectedY = { x -> ComplexExpressionSpecs.expectedRoundedStraightParameter(x) },
      liveY = { progress -> roundedStraightParameter(progress) },
    )
  }

  // 7. Repeater Compound Scale & Opacity Ramp
  @Test
  fun repeaterCompoundScaleAndOpacityMatchExpectedAndCoverOptimizationPaths() {
    val liveExp = RemoteFloat(Rc.Time.ANIMATION_TIME)
    val unitScale = repeaterCopyScale(100f.rf, 2f.rf)
    assertThat(unitScale.constantValue).isWithin(1e-4f).of(1f)

    val compoundScale = repeaterCopyScale(150f.rf, 2f.rf)
    assertThat(compoundScale.constantValue).isWithin(1e-4f).of(2.25f)

    // Layer local frame fast-path (st=0, sr=1) vs stretched/shifted path
    assertThat(calculateLocalFrame(liveExp, 0f, 1f)).isSameInstanceAs(liveExp)
    assertThat(calculateLocalFrame(10f.rf, 2f, 2f).constantValue).isWithin(1e-4f).of(4f)

    assertExpressionGraphTracksReferenceCurve(
      name = "RepeaterCompoundScaleOpacity",
      expectedY = { x -> ComplexExpressionSpecs.expectedRepeaterCompound(x) },
      liveY = { progress ->
        val scale = repeaterCopyScale(150f.rf, progress * 2f.rf)
        val opacity = repeaterCopyOpacity(progress * 3f.rf, 4f.rf, 100f.rf, 20f.rf)
        (scale * opacity) / 2.25f.rf
      },
    )
  }

  // 8. De Casteljau Cubic Subdivision Trajectory (covering both constant and live slice paths)
  @Test
  fun deCasteljauSliceCubicMatchesExpectedCurveEndpoint() {
    val points = listOf(0f.rf, 0f.rf, 0.25f.rf, 1.2f.rf, 0.75f.rf, (-0.2f).rf, 1f.rf, 1f.rf)

    // Full-range constant fast-path (a=0, b=1)
    val fullSlice = sliceCubic(points, 0f.rf, 1f.rf)
    assertThat(fullSlice[7].constantValue).isWithin(1e-4f).of(1f)

    // Collapsed constant fast-path (a == b)
    val collapsedSlice = sliceCubic(points, 0.5f.rf, 0.5f.rf)
    assertThat(collapsedSlice[1].constantValue)
      .isWithin(1e-4f)
      .of(ComplexExpressionSpecs.expectedSliceCubicEndY(0.5f))

    // Constant sub-segment path
    val midSlice = sliceCubic(points, 0f.rf, 0.5f.rf)
    assertThat(midSlice[7].constantValue)
      .isWithin(1e-4f)
      .of(ComplexExpressionSpecs.expectedSliceCubicEndY(0.5f))

    // Live RemoteFloat sub-segment path (exercises cubicSliceFunction)
    val animTime = RemoteFloat(Rc.Time.ANIMATION_TIME)
    val liveEnd = 0.5f.rf + (animTime - animTime)
    val liveSlice = sliceCubic(points, 0f.rf, liveEnd)
    assertThat(liveSlice[7].constantValueOrNull).isNull()

    assertExpressionGraphTracksReferenceCurve(
      name = "DeCasteljauSliceCubic",
      expectedY = { x -> ComplexExpressionSpecs.expectedSliceCubicEndY(x) },
      liveY = { progress -> sliceCubic(points, 0f.rf, progress)[7] },
      yMin = -0.1f,
      yMax = 1.1f,
    )
  }
}
