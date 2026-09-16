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

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import com.google.android.horologist.remotecompose.lottie.renderer.shapes.androidPathToBezierValues
import com.google.android.horologist.remotecompose.lottie.renderer.shapes.buildAndroidPathFromBezier
import com.google.android.horologist.remotecompose.lottie.renderer.shapes.conicToCubics
import com.google.common.truth.Truth.assertThat
import com.google.common.truth.Truth.assertWithMessage
import java.io.File
import kotlin.math.abs
import org.junit.Test
import org.robolectric.annotation.Config

/** Native iterator payload and independently rasterized path round trips on API 35. */
class NativePathConversionTest : MotionPixelHarness() {
  @Test
  fun lineKeepsItsActualEndpoint() {
    val output =
      androidPathToBezierValues(
          Path().apply {
            moveTo(7f, 11f)
            lineTo(41f, 37f)
          }
        )
        .single()
    assertThat(output.vertices.map { p -> p.map { it.constantValue } })
      .containsExactly(listOf(7f, 11f), listOf(41f, 37f))
      .inOrder()
  }

  @Test
  fun quadraticDegreeElevationPreservesControls() {
    val output =
      androidPathToBezierValues(
          Path().apply {
            moveTo(8f, 32f)
            quadTo(32f, 8f, 56f, 32f)
          }
        )
        .single()
    assertThat(output.vertices.map { p -> p.map { it.constantValue } })
      .containsExactly(listOf(8f, 32f), listOf(56f, 32f))
      .inOrder()
    assertThat(output.outTangents.first().map { it.constantValue })
      .containsExactly(16f, -16f)
      .inOrder()
    assertThat(output.inTangents.last().map { it.constantValue })
      .containsExactly(-16f, -16f)
      .inOrder()
    roundTrip(
      "quadratic",
      Path().apply {
        moveTo(8f, 32f)
        quadTo(32f, 8f, 56f, 32f)
      },
    )
  }

  @Test
  fun cubicKeepsBothControlsAndEndpoint() {
    val output =
      androidPathToBezierValues(
          Path().apply {
            moveTo(8f, 40f)
            cubicTo(12f, 4f, 52f, 8f, 56f, 44f)
          }
        )
        .single()
    assertThat(output.vertices.map { p -> p.map { it.constantValue } })
      .containsExactly(listOf(8f, 40f), listOf(56f, 44f))
      .inOrder()
    assertThat(output.outTangents.first().map { it.constantValue })
      .containsExactly(4f, -36f)
      .inOrder()
    assertThat(output.inTangents.last().map { it.constantValue })
      .containsExactly(-4f, -36f)
      .inOrder()
    roundTrip(
      "cubic",
      Path().apply {
        moveTo(8f, 40f)
        cubicTo(12f, 4f, 52f, 8f, 56f, 44f)
      },
    )
  }

  @Test
  fun closingCurveKeepsItsIncomingHandle() {
    val path =
      Path().apply {
        moveTo(8f, 32f)
        cubicTo(8f, 8f, 56f, 8f, 56f, 32f)
        cubicTo(56f, 56f, 8f, 56f, 8f, 32f)
        close()
      }
    val output = androidPathToBezierValues(path).single()
    assertThat(output.closed).isTrue()
    assertThat(output.vertices).hasSize(2)
    assertThat(output.inTangents.first().map { it.constantValue })
      .containsExactly(0f, 24f)
      .inOrder()
    roundTrip("closed-cubic", path)
  }

  @Test
  fun closeDoesNotWeldNearbyDistinctVertices() {
    val output =
      androidPathToBezierValues(
          Path().apply {
            moveTo(8f, 8f)
            lineTo(56f, 56f)
            lineTo(8.0005f, 8f)
            close()
          }
        )
        .single()
    assertThat(output.closed).isTrue()
    assertThat(output.vertices).hasSize(3)
    assertThat(output.vertices.last()[0].constantValue).isEqualTo(8.0005f)
  }

  @Test
  fun returningToStartDoesNotCloseAnOpenContour() {
    val output =
      androidPathToBezierValues(
          Path().apply {
            moveTo(8f, 8f)
            lineTo(56f, 56f)
            lineTo(8f, 8f)
          }
        )
        .single()
    assertThat(output.closed).isFalse()
    assertThat(output.vertices).hasSize(3)
  }

  @Test
  fun mixedOpenAndClosedContoursStaySeparate() {
    val path =
      Path().apply {
        addRect(8f, 8f, 24f, 24f, Path.Direction.CW)
        moveTo(32f, 48f)
        cubicTo(40f, 8f, 56f, 8f, 56f, 48f)
      }
    val output = androidPathToBezierValues(path)
    assertThat(output.map { it.closed }).containsExactly(true, false).inOrder()
    roundTrip("mixed-contours", path)
  }

  @Test
  fun emptyPathStaysEmpty() {
    assertThat(androidPathToBezierValues(Path())).isEmpty()
  }

  @Test
  @Config(sdk = [33])
  fun legacyEmptyPathStaysEmpty() {
    assertThat(androidPathToBezierValues(Path())).isEmpty()
  }

  @Test
  @Config(sdk = [33])
  fun legacyOpenReturnDoesNotAcquireAClosingSeam() {
    val output =
      androidPathToBezierValues(
          Path().apply {
            moveTo(8f, 8f)
            lineTo(56f, 8f)
            lineTo(56f, 56f)
            lineTo(8f, 8f)
          }
        )
        .single()
    assertThat(output.closed).isFalse()
    assertThat(output.vertices).hasSize(4)
  }

  @Test fun weightedConicBelowOneRoundTrips() = conic("conic-quarter", .25f)

  @Test fun circularConicRoundTrips() = conic("conic-circle", kotlin.math.sqrt(.5f))

  @Test fun weightedConicAboveOneRoundTrips() = conic("conic-double", 2f)

  @Test fun zeroWeightConicMatchesNativeDegeneracy() = conic("conic-zero", 0f)

  @Test fun unitWeightConicMatchesQuadratic() = conic("conic-unit", 1f)

  @Test
  fun nativeOvalKeepsItsConicArcs() =
    roundTrip("oval", Path().apply { addOval(8f, 16f, 56f, 48f, Path.Direction.CW) })

  @Test
  fun conicCoordinatesMatchIndependentRationalEvaluation() {
    for (weight in listOf(.00001f, .25f, kotlin.math.sqrt(.5f), 1f, 2f, 10000f)) {
      // Deliberately non-axis-aligned, with different signs on both coordinates.
      val input = floatArrayOf(-15f, 34f, 38f, -29f, 52f, 41f, weight)
      val pieces = conicToCubics(input)
      assertThat(pieces.first().from).isEqualTo(0.0)
      assertThat(pieces.last().to).isEqualTo(1.0)
      for ((index, piece) in pieces.withIndex()) {
        if (index > 0) assertThat(piece.from).isEqualTo(pieces[index - 1].to)
        for (step in 0..100) {
          val u = step / 100.0
          val t = piece.from + (piece.to - piece.from) * u
          val a = (1 - t) * (1 - t)
          val b = 2 * weight * t * (1 - t)
          val c = t * t
          val v = 1 - u
          for (axis in 0..1) {
            val expected =
              (a * input[axis] + b * input[axis + 2] + c * input[axis + 4]) / (a + b + c)
            val p = piece.points
            val actual =
              v * v * v * p[axis] +
                3 * v * v * u * p[axis + 2] +
                3 * v * u * u * p[axis + 4] +
                u * u * u * p[axis + 6]
            assertWithMessage("weight=$weight piece=$index u=$u axis=$axis")
              .that(actual)
              .isWithin(.00011)
              .of(expected)
          }
        }
      }
    }
  }

  @Test
  fun exactQuadraticElevationHasNativeRasterVariance() {
    // This is analytic degree elevation, independent of all production conversion helpers.
    val quadratic =
      Path().apply {
        moveTo(8f, 32f)
        quadTo(32f, 8f, 56f, 32f)
      }
    val exactCubic =
      Path().apply {
        moveTo(8f, 32f)
        cubicTo(24f, 16f, 40f, 16f, 56f, 32f)
      }
    assertThat(androidPathToBezierValues(quadratic).single().vertices).hasSize(2)
    compareRaster("analytic-degree-elevation", quadratic, exactCubic)
  }

  @Test
  fun curvedBooleanOutputRoundTrips() {
    val a = Path().apply { addCircle(24f, 32f, 20f, Path.Direction.CW) }
    val b = Path().apply { addCircle(40f, 32f, 20f, Path.Direction.CW) }
    for (op in listOf(Path.Op.UNION, Path.Op.INTERSECT, Path.Op.DIFFERENCE, Path.Op.XOR)) {
      val result = Path()
      assertThat(result.op(a, b, op)).isTrue()
      roundTrip("boolean-${op.name.lowercase()}", result)
    }
  }

  private fun conic(name: String, weight: Float) =
    roundTrip(
      name,
      Path().apply {
        moveTo(8f, 48f)
        conicTo(8f, 8f, 56f, 8f, weight)
      },
    )

  private fun roundTrip(name: String, source: Path) {
    val rebuilt =
      buildAndroidPathFromBezier(androidPathToBezierValues(source)).apply {
        fillType = source.fillType
      }
    compareRaster(name, source, rebuilt)
  }

  private fun compareRaster(name: String, source: Path, rebuilt: Path) {
    val folder = File("build/outputs/lottie-native-path/$name").apply { mkdirs() }
    for (style in listOf(Paint.Style.FILL, Paint.Style.STROKE)) {
      fun render(path: Path): Bitmap =
        Bitmap.createBitmap(256, 256, Bitmap.Config.ARGB_8888).also {
          Canvas(it).apply {
            drawColor(Color.BLACK)
            scale(4f, 4f)
            drawPath(
              path,
              Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = Color.RED
                this.style = style
                strokeWidth = 2f
                strokeCap = Paint.Cap.ROUND
              },
            )
          }
        }
      val reference = render(source)
      val actual = render(rebuilt)
      try {
        var sum = 0.0
        var peak = 0.0
        var visible = 0
        var offEdge = 0
        var edgePixels = 0
        for (y in 0 until 256) for (x in 0 until 256) {
          val red = Color.red(reference.getPixel(x, y))
          if (red > 127) visible++
          val error = abs(red - Color.red(actual.getPixel(x, y))) / 255.0
          sum += error
          peak = maxOf(peak, error)
          val neighbors = buildList {
            for (yy in maxOf(0, y - 1)..minOf(255, y + 1)) for (xx in
              maxOf(0, x - 1)..minOf(255, x + 1)) add(Color.red(reference.getPixel(xx, yy)))
          }
          val edge = !(neighbors.all { it == 0 } || neighbors.all { it == 255 })
          if (edge) edgePixels++ else if (error > 0) offEdge++
        }
        for ((kind, bitmap) in listOf("reference" to reference, "rc" to actual)) {
          File(folder, "${style.name.lowercase()}-$kind.png").outputStream().use {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, it)
          }
        }
        if (name == "conic-zero" && style == Paint.Style.FILL) {
          assertThat(visible).isEqualTo(0)
          assertThat(sum).isEqualTo(0.0)
        } else {
          assertWithMessage("$name/$style reference must be visible")
            .that(visible)
            .isGreaterThan(20)
          // Native conic/quad and cubic scan conversion differ even for exact degree elevation.
          // Independent coordinate checks above use a much tighter geometric error bound.
          assertWithMessage("$name/$style differences outside a one-pixel edge band")
            .that(offEdge)
            .isEqualTo(0)
          assertWithMessage("$name/$style mean edge coverage error")
            .that(sum / edgePixels)
            .isAtMost(.1)
          assertWithMessage("$name/$style peak error").that(peak).isAtMost(.5)
        }
      } finally {
        reference.recycle()
        actual.recycle()
      }
    }
  }
}
