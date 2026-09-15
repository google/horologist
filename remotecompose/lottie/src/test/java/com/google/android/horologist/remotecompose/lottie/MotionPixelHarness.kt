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
import android.graphics.BitmapFactory
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.MutableFloatState
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.unit.dp
import com.github.takahirom.roborazzi.ExperimentalRoborazziApi
import com.github.takahirom.roborazzi.RoborazziOptions
import com.github.takahirom.roborazzi.RoborazziTaskType
import com.github.takahirom.roborazzi.captureRoboImage
import com.google.android.horologist.remotecompose.lottie.format.Animation
import com.google.android.horologist.screenshots.rng.WearScreenshotTest
import com.google.common.truth.Truth.assertWithMessage
import java.io.File

@OptIn(ExperimentalRoborazziApi::class)
abstract class MotionPixelHarness : WearScreenshotTest() {
  protected fun show(json: String): MutableFloatState {
    val decoded = Animation.decodeFromString(json)
    val progress = mutableFloatStateOf(0f)
    composeRule.setContent {
      Box(Modifier.size(64.dp).background(Color.Black).testTag("motion")) {
        LottiePreview(decoded, modifier = Modifier.size(64.dp), progress = progress.floatValue)
      }
    }
    return progress
  }

  protected fun advance(progress: MutableFloatState, frame: Float) {
    composeRule.runOnIdle { progress.floatValue = frame / 40f }
  }

  protected data class Probe(val x: Int, val y: Int, val red: Float, val tolerance: Float = 0.02f)

  protected fun assertPixels(vararg probes: Probe) {
    val bitmap = capture("motion")
    try {
      for (probe in probes) {
        val pixel =
          Color(bitmap.getPixel(probe.x * bitmap.width / 64, probe.y * bitmap.height / 64))
        assertWithMessage("pixel (${probe.x}, ${probe.y}), expected red=${probe.red}, got $pixel")
          .that(pixel.red)
          .isWithin(probe.tolerance)
          .of(probe.red)
        assertWithMessage("green at (${probe.x}, ${probe.y})")
          .that(pixel.green)
          .isWithin(0.01f)
          .of(0f)
        assertWithMessage("blue at (${probe.x}, ${probe.y})")
          .that(pixel.blue)
          .isWithin(0.01f)
          .of(0f)
      }
    } catch (failure: AssertionError) {
      val output =
        File("build/outputs/motion-failures/${javaClass.simpleName}-${System.nanoTime()}.png")
      output.parentFile.mkdirs()
      output.outputStream().use { bitmap.compress(Bitmap.CompressFormat.PNG, 100, it) }
      throw AssertionError("${failure.message}\nActual image: ${output.absolutePath}", failure)
    } finally {
      bitmap.recycle()
    }
  }

  /** Compares each playback frame with a visible static fixture at the specified geometry. */
  protected fun assertMatchesStaticFrames(
    json: String,
    referenceProbeAt: (Int) -> Probe,
    artifactName: String? = null,
    frames: List<Int> = listOf(0, 5, 10),
    maxMeanRedError: Float? = null,
    maxPeakRedError: Float = 0.25f,
    minVisiblePixels: Int = 20,
    maxMeanEdgeRedError: Float? = null,
    staticAt: (Int) -> String,
  ) {
    val decoded = Animation.decodeFromString(json)
    val reference = mutableStateOf(Animation.decodeFromString(staticAt(0)))
    val progress = mutableFloatStateOf(0f)
    composeRule.setContent {
      Column {
        Box(Modifier.size(64.dp).background(Color.Black).testTag("motion")) {
          LottiePreview(decoded, modifier = Modifier.size(64.dp), progress = progress.floatValue)
        }
        Box(Modifier.size(64.dp).background(Color.Black).testTag("reference")) {
          // Each static fixture needs its own recorded document; playback stays mounted above.
          key(reference.value) {
            LottiePreview(reference.value, modifier = Modifier.size(64.dp), progress = 0f)
          }
        }
      }
    }
    val failures = mutableListOf<String>()
    for (frame in frames) {
      composeRule.runOnIdle {
        reference.value = Animation.decodeFromString(staticAt(frame))
        progress.floatValue = frame / 40f
      }
      val expected = capture("reference")
      val actual = capture("motion")
      try {
        artifactName?.let { name ->
          val output = File("build/outputs/lottie-motion/$name").apply { mkdirs() }
          for ((kind, bitmap) in listOf("reference" to expected, "rc" to actual)) {
            File(output, "frame$frame-$kind.png").outputStream().use {
              bitmap.compress(Bitmap.CompressFormat.PNG, 100, it)
            }
          }
        }
        val probe = referenceProbeAt(frame)
        val referencePixel =
          Color(expected.getPixel(probe.x * expected.width / 64, probe.y * expected.height / 64))
        assertWithMessage("static fixture must reach (${probe.x}, ${probe.y}) at frame $frame")
          .that(referencePixel.red)
          .isWithin(probe.tolerance)
          .of(probe.red)
        var visible = 0
        var different = 0
        for (y in 0 until 64) {
          for (x in 0 until 64) {
            val e = Color(expected.getPixel(x * expected.width / 64, y * expected.height / 64)).red
            val a = Color(actual.getPixel(x * actual.width / 64, y * actual.height / 64)).red
            if (e > 0.5f) visible++
            if (kotlin.math.abs(e - a) > 0.05f) different++
          }
        }
        assertWithMessage("static fixture must contain visible geometry at frame $frame")
          .that(visible)
          .isGreaterThan(minVisiblePixels)
        if (maxMeanRedError == null) {
          assertWithMessage("moving geometry differs from static geometry at frame $frame")
            .that(different)
            .isEqualTo(0)
        } else {
          // Opt-in for independently generated geometry whose float arithmetic can shift
          // edge coverage. Inspect every output pixel, with both mean and outlier bounds.
          assertWithMessage("raster dimensions").that(actual.width).isEqualTo(expected.width)
          assertWithMessage("raster dimensions").that(actual.height).isEqualTo(expected.height)
          var sum = 0.0
          var largest = 0f
          var edgeCount = 0
          var edgeSum = 0.0
          var offEdgeDifferences = 0
          for (y in 0 until actual.height) for (x in 0 until actual.width) {
            val error =
              kotlin.math.abs(Color(actual.getPixel(x, y)).red - Color(expected.getPixel(x, y)).red)
            sum += error
            largest = maxOf(largest, error)
            if (maxMeanEdgeRedError != null) {
              var low = 1f
              var high = 0f
              for (dy in -1..1) for (dx in -1..1) {
                val red =
                  Color(
                      expected.getPixel(
                        (x + dx).coerceIn(0, expected.width - 1),
                        (y + dy).coerceIn(0, expected.height - 1),
                      )
                    )
                    .red
                low = minOf(low, red)
                high = maxOf(high, red)
              }
              if (low != high) {
                edgeCount++
                edgeSum += error
              } else if (error != 0f) offEdgeDifferences++
            }
          }
          if (maxMeanEdgeRedError == null) {
            assertWithMessage("mean raster error at frame $frame")
              .that(sum / (actual.width * actual.height))
              .isAtMost(maxMeanRedError.toDouble())
          } else {
            // Explicit opt-in for native fill AA variance backed by independent geometry checks.
            assertWithMessage("off-edge differences at frame $frame")
              .that(offEdgeDifferences)
              .isEqualTo(0)
            assertWithMessage("mean edge error at frame $frame")
              .that(edgeSum / maxOf(edgeCount, 1))
              .isAtMost(maxMeanEdgeRedError.toDouble())
          }
          assertWithMessage("largest raster error at frame $frame")
            .that(largest)
            .isAtMost(maxPeakRedError)
        }
      } catch (failure: AssertionError) {
        // Capture every requested frame even when an early frame differs, so one failing edge
        // does not hide later topology or timing failures from the review artifacts.
        failures.add("Frame $frame: ${failure.message}")
      } finally {
        expected.recycle()
        actual.recycle()
      }
    }
    if (failures.isNotEmpty()) throw AssertionError(failures.joinToString("\n"))
  }

  protected fun capture(tag: String): Bitmap {
    composeRule.waitForIdle()
    val screenshot = File.createTempFile("motion-regression-", ".png")
    try {
      composeRule
        .onNodeWithTag(tag)
        .captureRoboImage(
          screenshot.absolutePath,
          roborazziOptions = RoborazziOptions(taskType = RoborazziTaskType.Record),
        )
      return checkNotNull(BitmapFactory.decodeFile(screenshot.absolutePath))
    } finally {
      screenshot.delete()
    }
  }

  protected fun animation(shapes: String, position: String = fixed("[0,0]")): String =
    """{"v":"5.7.4","fr":20,"ip":0,"op":40,"w":64,"h":64,"ddd":0,"assets":[],
      "layers":[{"ty":4,"ind":1,"ip":0,"op":40,"st":0,"sr":1,
      "ks":{"a":${fixed("[0,0]")},"p":$position,"r":${fixed("0")},
      "s":${fixed("[100,100]")},"o":${fixed("100")}},"shapes":[$shapes]}]}"""

  protected fun fixed(value: String): String = """{"a":0,"k":$value}"""

  protected fun animated(
    start: String,
    end: String,
    easing: String = linear,
    extra: String = "",
  ): String = """{"a":1,"k":[{"t":0,"s":$start,$easing$extra},{"t":10,"s":$end}]}"""

  protected val linear: String = """"o":{"x":0,"y":0},"i":{"x":1,"y":1}"""

  protected val redFill: String =
    """{"ty":"fl","r":1,"c":${fixed("[1,0,0,1]")},"o":${fixed("100")}}"""

  protected fun rectangle(
    position: String,
    size: String = fixed("[16,16]"),
    radius: String = fixed("0"),
  ): String = """{"ty":"rc","d":1,"p":$position,"s":$size,"r":$radius}"""
}
