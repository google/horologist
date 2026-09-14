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
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.MutableFloatState
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.test.core.app.ApplicationProvider
import com.airbnb.lottie.LottieComposition
import com.airbnb.lottie.LottieCompositionFactory
import com.airbnb.lottie.compose.LottieAnimation as ReferenceLottie
import com.google.android.horologist.remotecompose.lottie.format.Animation
import com.google.common.truth.Truth.assertWithMessage
import java.io.File

/** One mounted RC document, reference frames and diagnostics for each bundled fixture. */
abstract class FixtureParityHarness : MotionPixelHarness() {
  protected fun compare(
    resource: Int,
    animated: Boolean = true,
    background: Color = Color.White,
    maxForegroundError: Double = 0.15,
  ) {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val json = context.resources.openRawResource(resource).bufferedReader().use { it.readText() }
    compare(
      json = json,
      name = context.resources.getResourceEntryName(resource),
      animated = animated,
      background = background,
      maxForegroundError = maxForegroundError,
    )
  }

  protected fun compare(
    json: String,
    name: String,
    animated: Boolean = true,
    background: Color = Color.White,
    maxForegroundError: Double = 0.15,
    enableMergePaths: Boolean = false,
    referenceJson: String = json,
    renderSizeDp: Int = 80,
  ) {
    val decoded = Animation.decodeFromString(json)
    val reference =
      checkNotNull(LottieCompositionFactory.fromJsonStringSync(referenceJson, null).value)
    val progressState = mutableFloatStateOf(0f)

    mountCompositions(
      decoded = decoded,
      reference = reference,
      progressState = progressState,
      background = background,
      renderSizeDp = renderSizeDp,
      enableMergePaths = enableMergePaths,
    )

    val output = File("build/outputs/lottie-parity/$name")
    val metricsLog = mutableListOf("progress,mean_rgb_error,foreground_rgb_error,reference_pixels")
    val failures = mutableListOf<String>()

    var movingFrames = 0
    var firstPixels: IntArray? = null

    for (step in SAMPLE_PROGRESS_STEPS) {
      composeRule.runOnIdle { progressState.floatValue = step }
      val actual = capture("motion")
      val expected = capture("reference")

      try {
        assertWithMessage("Reference and RC capture widths")
          .that(actual.width)
          .isEqualTo(expected.width)
        assertWithMessage("Reference and RC capture heights")
          .that(actual.height)
          .isEqualTo(expected.height)

        val pixels = IntArray(actual.width * actual.height)
        actual.getPixels(pixels, 0, actual.width, 0, 0, actual.width, actual.height)
        if (firstPixels == null) {
          firstPixels = pixels
        } else if (!pixels.contentEquals(firstPixels)) {
          movingFrames++
        }

        val metrics = BitmapComparisonUtil.computeDiffMetrics(actual, expected, background)
        metricsLog +=
          "$step,${metrics.meanRgbError},${metrics.foregroundRgbError},${metrics.visibleArtworkPixels}"

        if (metrics.hasError(maxForegroundError)) {
          val prefix = "progress${(step * 100).toInt()}"
          BitmapComparisonUtil.saveDiagnosticBitmaps(output, prefix, actual, expected)

          if (metrics.visibleArtworkPixels <= BitmapDiffMetrics.MIN_VISIBLE_PIXELS) {
            failures += "progress=$step: reference has no visible artwork"
          }
          if (
            metrics.meanRgbError >= BitmapDiffMetrics.MAX_MEAN_RGB_ERROR ||
              metrics.foregroundRgbError >= maxForegroundError
          ) {
            failures +=
              "progress=$step: RGB MAE=${metrics.meanRgbError}, foreground MAE=${metrics.foregroundRgbError}"
          }
        }
      } finally {
        actual.recycle()
        expected.recycle()
      }
    }

    if (failures.isNotEmpty()) {
      output.mkdirs()
      if (referenceJson != json) {
        File(output, "input.json").writeText(json)
        File(output, "reference.json").writeText(referenceJson)
      }
      File(output, "metrics.csv").writeText(metricsLog.joinToString("\n", postfix = "\n"))
    }

    assertWithMessage(
        "$name reference mismatches; diagnostics: ${output.absolutePath}\n${failures.joinToString("\n")}"
      )
      .that(failures)
      .isEmpty()

    if (animated) {
      assertWithMessage("The recorded RC document must actually animate")
        .that(movingFrames)
        .isGreaterThan(0)
    }
  }

  private fun mountCompositions(
    decoded: Animation,
    reference: LottieComposition,
    progressState: MutableFloatState,
    background: Color,
    renderSizeDp: Int,
    enableMergePaths: Boolean,
  ) {
    composeRule.setContent {
      Column {
        Box(Modifier.size(renderSizeDp.dp).background(background).testTag("motion")) {
          // lottie-android subtracts 0.01 from op. Compare the same authored frame, not two
          // slightly different normalized-progress domains at an internal layer boundary.
          val authoredFrame =
            reference.startFrame + progressState.floatValue * reference.durationFrames
          val totalRcDuration = (decoded.endFrame - decoded.startFrame).toFloat().coerceAtLeast(1f)
          val rcProgress = (authoredFrame - decoded.startFrame) / totalRcDuration
          LottiePreview(decoded, modifier = Modifier.size(renderSizeDp.dp), progress = rcProgress)
        }
        Box(Modifier.size(renderSizeDp.dp).background(background).testTag("reference")) {
          ReferenceLottie(
            reference,
            progress = { progressState.floatValue },
            modifier = Modifier.size(renderSizeDp.dp),
            enableMergePaths = enableMergePaths,
          )
        }
      }
    }
  }

  companion object {
    /** 13 progress steps sampling start, end, and internal intervals. */
    private val SAMPLE_PROGRESS_STEPS: List<Float> =
      ((0..10).map { it / 10f } + listOf(0.25f, 0.75f)).distinct().sorted()
  }
}
