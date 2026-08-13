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

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicText
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.takahirom.roborazzi.captureRoboImage
import org.junit.Test

/**
 * Screenshot tests comparing scaling behavior between lottie-android and Remote Compose across 16
 * combinations of Lottie content sizes and Compose UI container box sizes:
 *
 * Lottie Content Sizes:
 * - 64x64px (Square, small)
 * - 64x96px (Tall / portrait, 2:3 aspect ratio)
 * - 128x128px (Square, medium)
 * - 192x128px (Wide / landscape, 3:2 aspect ratio)
 *
 * Containing Box Sizes:
 * - 64x64dp
 * - 64x96dp
 * - 128x128dp
 * - 192x128dp
 */
class LottieScalingDiffScreenshotTest : LottieDiffScreenshotTest() {

  // --- Lottie 64x64 (Square, small) ---

  @Test fun lottie64x64_box64x64() = runScalingTest(64, 64, 64.dp, 64.dp)

  @Test fun lottie64x64_box64x96() = runScalingTest(64, 64, 64.dp, 96.dp)

  @Test fun lottie64x64_box128x128() = runScalingTest(64, 64, 128.dp, 128.dp)

  @Test fun lottie64x64_box192x128() = runScalingTest(64, 64, 192.dp, 128.dp)

  // --- Lottie 64x96 (Tall / 2:3 aspect ratio) ---

  @Test fun lottie64x96_box64x64() = runScalingTest(64, 96, 64.dp, 64.dp)

  @Test fun lottie64x96_box64x96() = runScalingTest(64, 96, 64.dp, 96.dp)

  @Test fun lottie64x96_box128x128() = runScalingTest(64, 96, 128.dp, 128.dp)

  @Test fun lottie64x96_box192x128() = runScalingTest(64, 96, 192.dp, 128.dp)

  // --- Lottie 128x128 (Square, medium) ---

  @Test fun lottie128x128_box64x64() = runScalingTest(128, 128, 64.dp, 64.dp)

  @Test fun lottie128x128_box64x96() = runScalingTest(128, 128, 64.dp, 96.dp)

  @Test fun lottie128x128_box128x128() = runScalingTest(128, 128, 128.dp, 128.dp)

  @Test fun lottie128x128_box192x128() = runScalingTest(128, 128, 192.dp, 128.dp)

  // --- Lottie 192x128 (Wide / 3:2 aspect ratio) ---

  @Test fun lottie192x128_box64x64() = runScalingTest(192, 128, 64.dp, 64.dp)

  @Test fun lottie192x128_box64x96() = runScalingTest(192, 128, 64.dp, 96.dp)

  @Test fun lottie192x128_box128x128() = runScalingTest(192, 128, 128.dp, 128.dp)

  @Test fun lottie192x128_box192x128() = runScalingTest(192, 128, 192.dp, 128.dp)

  private fun runScalingTest(lottieWidth: Int, lottieHeight: Int, boxWidth: Dp, boxHeight: Dp) {
    val json = createLottieJson(lottieWidth, lottieHeight)
    val useVerticalStack = boxWidth > 84.dp

    composeRule.setContent {
      Column(
        modifier = Modifier.background(Color(0xFF1E1E1E)).padding(8.dp).testTag("LottieDiff"),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(6.dp),
      ) {
        BasicText(
          text =
            "Lottie: ${lottieWidth}x${lottieHeight}px | Box: ${boxWidth.value.toInt()}x${boxHeight.value.toInt()}dp",
          style = TextStyle(color = Color.White, fontSize = 10.sp),
        )

        if (useVerticalStack) {
          Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(6.dp),
          ) {
            LottieAndroidPreview(json = json, boxWidth = boxWidth, boxHeight = boxHeight)
            LottieRcPreview(json = json, boxWidth = boxWidth, boxHeight = boxHeight)
          }
        } else {
          Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
          ) {
            LottieAndroidPreview(json = json, boxWidth = boxWidth, boxHeight = boxHeight)
            LottieRcPreview(json = json, boxWidth = boxWidth, boxHeight = boxHeight)
          }
        }
      }
    }

    composeRule.onNodeWithTag("LottieDiff").captureRoboImage(screenshotFilePath(""))
  }

  companion object {
    /**
     * Generates a Lottie animation JSON string for the given canvas dimensions. Contains an outer
     * bounding rectangle and a centered circle to clearly highlight scaling, aspect ratio
     * preservation, letterboxing/pillarboxing, and centering.
     */
    fun createLottieJson(width: Int, height: Int): String {
      val w = width.toFloat()
      val h = height.toFloat()
      val cx = w / 2f
      val cy = h / 2f
      val r = minOf(w, h) / 4f
      val handle = r * 0.55228475f

      return """
      {
        "v": "5.9.6",
        "fr": 60,
        "ip": 0,
        "op": 60,
        "w": $width,
        "h": $height,
        "nm": "test_${width}x${height}",
        "layers": [
          {
            "ty": 4,
            "nm": "Shape Layer",
            "ind": 1,
            "ip": 0,
            "op": 60,
            "ks": {
              "a": { "a": 0, "k": [0, 0, 0] },
              "p": { "a": 0, "k": [0, 0, 0] },
              "r": { "a": 0, "k": 0 },
              "s": { "a": 0, "k": [100, 100] },
              "o": { "a": 0, "k": 100 }
            },
            "shapes": [
              {
                "ty": "gr",
                "nm": "Center Circle",
                "it": [
                  {
                    "ty": "sh",
                    "nm": "Circle Path",
                    "ks": {
                      "a": 0,
                      "k": {
                        "c": true,
                        "v": [[0, -$r], [$r, 0], [0, $r], [-$r, 0]],
                        "i": [[-$handle, 0], [0, -$handle], [$handle, 0], [0, $handle]],
                        "o": [[$handle, 0], [0, $handle], [-$handle, 0], [0, -$handle]]
                      }
                    }
                  },
                  {
                    "ty": "fl",
                    "nm": "Circle Fill",
                    "c": { "a": 0, "k": [0.95, 0.25, 0.2, 1.0] },
                    "o": { "a": 0, "k": 100 }
                  },
                  {
                    "ty": "tr",
                    "nm": "Transform",
                    "a": { "a": 0, "k": [0, 0] },
                    "p": { "a": 0, "k": [$cx, $cy] },
                    "s": { "a": 0, "k": [100, 100] },
                    "r": { "a": 0, "k": 0 },
                    "o": { "a": 0, "k": 100 }
                  }
                ]
              },
              {
                "ty": "gr",
                "nm": "Outer Rect",
                "it": [
                  {
                    "ty": "sh",
                    "nm": "Rect Path",
                    "ks": {
                      "a": 0,
                      "k": {
                        "c": true,
                        "v": [[2, 2], [${width - 2}, 2], [${width - 2}, ${height - 2}], [2, ${height - 2}]],
                        "i": [[0, 0], [0, 0], [0, 0], [0, 0]],
                        "o": [[0, 0], [0, 0], [0, 0], [0, 0]]
                      }
                    }
                  },
                  {
                    "ty": "fl",
                    "nm": "Rect Fill",
                    "c": { "a": 0, "k": [0.2, 0.5, 0.9, 1.0] },
                    "o": { "a": 0, "k": 100 }
                  },
                  {
                    "ty": "tr",
                    "nm": "Transform",
                    "a": { "a": 0, "k": [0, 0] },
                    "p": { "a": 0, "k": [0, 0] },
                    "s": { "a": 0, "k": [100, 100] },
                    "r": { "a": 0, "k": 0 },
                    "o": { "a": 0, "k": 100 }
                  }
                ]
              }
            ]
          }
        ]
      }
      """
        .trimIndent()
    }
  }
}
