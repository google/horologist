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
import androidx.compose.ui.graphics.Color
import java.io.File
import kotlin.math.abs

/**
 * Summary metrics from comparing an actual rendered bitmap against an expected reference bitmap.
 *
 * @property meanRgbError Mean Absolute Error across all pixels and RGB channels, normalized to
 *   [0.0, 1.0].
 * @property foregroundRgbError Mean Absolute Error across foreground artwork pixels (excluding
 *   background).
 * @property visibleArtworkPixels Count of reference pixels that differ from the background color.
 * @property foregroundPixels Total count of foreground pixels found in either bitmap.
 */
internal data class BitmapDiffMetrics(
  val meanRgbError: Double,
  val foregroundRgbError: Double,
  val visibleArtworkPixels: Int,
  val foregroundPixels: Int,
) {
  fun hasError(
    maxForegroundError: Double,
    minVisiblePixels: Int = MIN_VISIBLE_PIXELS,
    maxMeanError: Double = MAX_MEAN_RGB_ERROR,
  ): Boolean =
    visibleArtworkPixels <= minVisiblePixels ||
      meanRgbError >= maxMeanError ||
      foregroundRgbError >= maxForegroundError

  companion object {
    const val MIN_VISIBLE_PIXELS: Int = 20
    const val MAX_MEAN_RGB_ERROR: Double = 0.03
  }
}

internal object BitmapComparisonUtil {

  /**
   * Compares two bitmaps of the same dimensions pixel-by-pixel, computing global Mean Absolute
   * Error and foreground artwork error relative to [backgroundColor].
   */
  fun computeDiffMetrics(
    actual: Bitmap,
    expected: Bitmap,
    backgroundColor: Color,
    artworkThreshold: Float = 0.05f,
  ): BitmapDiffMetrics {
    require(actual.width == expected.width && actual.height == expected.height) {
      "Bitmap dimensions must match: actual=(${actual.width}x${actual.height}), expected=(${expected.width}x${expected.height})"
    }

    val width = actual.width
    val height = actual.height
    val count = width * height

    val actualPixels = IntArray(count)
    val expectedPixels = IntArray(count)
    actual.getPixels(actualPixels, 0, width, 0, 0, width, height)
    expected.getPixels(expectedPixels, 0, width, 0, 0, width, height)

    var totalRgbError = 0.0
    var foregroundRgbError = 0.0
    var foregroundCount = 0
    var visibleCount = 0

    val bgR = backgroundColor.red
    val bgG = backgroundColor.green
    val bgB = backgroundColor.blue

    for (i in 0 until count) {
      val aColor = actualPixels[i]
      val eColor = expectedPixels[i]

      val aR = ((aColor ushr 16) and 0xFF) / 255f
      val aG = ((aColor ushr 8) and 0xFF) / 255f
      val aB = (aColor and 0xFF) / 255f

      val eR = ((eColor ushr 16) and 0xFF) / 255f
      val eG = ((eColor ushr 8) and 0xFF) / 255f
      val eB = (eColor and 0xFF) / 255f

      val diff = (abs(aR - eR) + abs(aG - eG) + abs(aB - eB)) / 3.0
      totalRgbError += diff

      val isExpectedArtwork =
        abs(eR - bgR) > artworkThreshold ||
          abs(eG - bgG) > artworkThreshold ||
          abs(eB - bgB) > artworkThreshold

      val isActualArtwork =
        abs(aR - bgR) > artworkThreshold ||
          abs(aG - bgG) > artworkThreshold ||
          abs(aB - bgB) > artworkThreshold

      if (isExpectedArtwork) visibleCount++
      if (isExpectedArtwork || isActualArtwork) {
        foregroundCount++
        foregroundRgbError += diff
      }
    }

    return BitmapDiffMetrics(
      meanRgbError = totalRgbError / count,
      foregroundRgbError = foregroundRgbError / foregroundCount.coerceAtLeast(1),
      visibleArtworkPixels = visibleCount,
      foregroundPixels = foregroundCount,
    )
  }

  /** Saves both actual and expected bitmaps to disk for debugging and visual diagnostics. */
  fun saveDiagnosticBitmaps(outputDir: File, namePrefix: String, actual: Bitmap, expected: Bitmap) {
    outputDir.mkdirs()
    saveBitmap(File(outputDir, "$namePrefix-rc.png"), actual)
    saveBitmap(File(outputDir, "$namePrefix-reference.png"), expected)
  }

  private fun saveBitmap(file: File, bitmap: Bitmap) {
    file.outputStream().use { stream -> bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream) }
  }
}
