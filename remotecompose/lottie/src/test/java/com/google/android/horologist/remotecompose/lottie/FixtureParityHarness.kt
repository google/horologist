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
import android.graphics.Bitmap
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.test.core.app.ApplicationProvider
import com.airbnb.lottie.LottieCompositionFactory
import com.airbnb.lottie.compose.LottieAnimation as ReferenceLottie
import com.google.android.horologist.remotecompose.lottie.format.Animation
import com.google.common.truth.Truth.assertWithMessage
import java.io.File
import kotlin.math.abs

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
      json,
      context.resources.getResourceEntryName(resource),
      animated,
      background,
      maxForegroundError,
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
    val progress = mutableFloatStateOf(0f)
    composeRule.setContent {
      Column {
        Box(Modifier.size(renderSizeDp.dp).background(background).testTag("motion")) {
          // lottie-android subtracts 0.01 from op. Compare the same authored frame, not two
          // slightly different normalized-progress domains at an internal layer boundary.
          val authoredFrame = reference.startFrame + progress.floatValue * reference.durationFrames
          val totalRcDuration = (decoded.endFrame - decoded.startFrame).toFloat().coerceAtLeast(1f)
          val rcProgress = (authoredFrame - decoded.startFrame) / totalRcDuration
          LottiePreview(decoded, modifier = Modifier.size(renderSizeDp.dp), progress = rcProgress)
        }
        Box(Modifier.size(renderSizeDp.dp).background(background).testTag("reference")) {
          ReferenceLottie(
            reference,
            progress = { progress.floatValue },
            modifier = Modifier.size(renderSizeDp.dp),
            enableMergePaths = enableMergePaths,
          )
        }
      }
    }
    var movingFrames = 0
    var firstPixels: IntArray? = null
    val output = File("build/outputs/lottie-parity/$name")
    val metrics = mutableListOf("progress,mean_rgb_error,foreground_rgb_error,reference_pixels")
    val failures = mutableListOf<String>()
    val frames = ((0..10).map { it / 10f } + listOf(0.25f, 0.75f)).distinct().sorted()
    for (frame in frames) {
      composeRule.runOnIdle { progress.floatValue = frame }
      val actual = capture("motion")
      val expected = capture("reference")
      try {
        assertWithMessage("Reference and RC capture widths")
          .that(actual.width)
          .isEqualTo(expected.width)
        assertWithMessage("Reference and RC capture heights")
          .that(actual.height)
          .isEqualTo(expected.height)
        val count = actual.width * actual.height
        val pixels = IntArray(count)
        actual.getPixels(pixels, 0, actual.width, 0, 0, actual.width, actual.height)
        if (firstPixels == null) firstPixels = pixels
        else if (!pixels.contentEquals(firstPixels)) movingFrames++
        var error = 0.0
        var foregroundError = 0.0
        var foreground = 0
        var visible = 0
        for (y in 0 until actual.height) for (x in 0 until actual.width) {
          val a = Color(actual.getPixel(x, y))
          val e =
            Color(
              expected.getPixel(
                x * expected.width / actual.width,
                y * expected.height / actual.height,
              )
            )
          val difference =
            (abs(a.red - e.red) + abs(a.green - e.green) + abs(a.blue - e.blue)) / 3.0
          error += difference
          fun artwork(color: Color): Boolean =
            maxOf(
              abs(color.red - background.red),
              abs(color.green - background.green),
              abs(color.blue - background.blue),
            ) > 0.05f
          if (artwork(e)) visible++
          if (artwork(e) || artwork(a)) {
            foreground++
            foregroundError += difference
          }
        }
        val meanError = error / count
        val artworkError = foregroundError / foreground.coerceAtLeast(1)
        metrics += "$frame,$meanError,$artworkError,$visible"
        val hasError = visible <= 20 || meanError >= 0.03 || artworkError >= maxForegroundError
        if (hasError) {
          output.mkdirs()
          val prefix = "progress${(frame * 100).toInt()}"
          fun save(bitmap: Bitmap, suffix: String) {
            File(output, "$prefix-$suffix.png").outputStream().use {
              bitmap.compress(Bitmap.CompressFormat.PNG, 100, it)
            }
          }
          save(actual, "rc")
          save(expected, "reference")
          if (visible <= 20) failures += "progress=$frame: reference has no visible artwork"
          if (meanError >= 0.03 || artworkError >= maxForegroundError)
            failures += "progress=$frame: RGB MAE=$meanError, foreground MAE=$artworkError"
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
      File(output, "metrics.csv").writeText(metrics.joinToString("\n", postfix = "\n"))
    }
    assertWithMessage(
        "$name reference mismatches; diagnostics: ${output.absolutePath}\n${failures.joinToString("\n")}"
      )
      .that(failures)
      .isEmpty()
    if (animated)
      assertWithMessage("The recorded RC document must actually animate")
        .that(movingFrames)
        .isGreaterThan(0)
  }
}
