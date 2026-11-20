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

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.google.android.horologist.remotecompose.lottie.format.Animation
import com.google.common.truth.Truth.assertThat
import org.junit.Test

class DynamicColorPlaybackTest : MotionPixelHarness() {
  // [SP_REVIEW_COLOR_FINAL] Dynamic playback reaches and holds the final color after a hold.
  @Test
  fun changesFromHeldRedToFinalBlueWhenProgressReachesLastColorKeyframe() {
    val animation = Animation.decodeFromString(heldColorRectangle)
    val progress = mutableFloatStateOf(0f)

    composeRule.setContent {
      Box(Modifier.size(64.dp).testTag("animation")) {
        LottiePreview(
          animation = animation,
          modifier = Modifier.size(64.dp),
          progress = progress.floatValue,
        )
      }
    }

    assertCenterPixel(Color.Red)
    composeRule.runOnIdle { progress.floatValue = 0.5f }
    assertCenterPixel(Color.Blue)
    composeRule.runOnIdle { progress.floatValue = 0.75f }
    assertCenterPixel(Color.Blue)
  }

  private fun assertCenterPixel(expected: Color) {
    val bitmap = capture("animation")
    try {
      assertThat(bitmap.getPixel(bitmap.width / 2, bitmap.height / 2)).isEqualTo(expected.toArgb())
    } finally {
      bitmap.recycle()
    }
  }

  private val heldColorRectangle =
    """
    {
      "v": "5.7.4", "fr": 60, "ip": 0, "op": 20, "w": 64, "h": 64,
      "nm": "Held color rectangle", "ddd": 0, "assets": [],
      "layers": [{
        "ddd": 0, "ind": 1, "ty": 4, "nm": "Rectangle", "sr": 1,
        "ip": 0, "op": 20, "st": 0, "bm": 0,
        "ks": {
          "a": {"a": 0, "k": [0, 0, 0]},
          "p": {"a": 0, "k": [0, 0, 0]},
          "r": {"a": 0, "k": 0},
          "s": {"a": 0, "k": [100, 100, 100]},
          "o": {"a": 0, "k": 100}
        },
        "shapes": [
          {
            "ty": "rc", "nm": "Rectangle path", "d": 1,
            "p": {"a": 0, "k": [32, 32]},
            "s": {"a": 0, "k": [64, 64]},
            "r": {"a": 0, "k": 0}
          },
          {
            "ty": "fl", "nm": "Held fill", "r": 1,
            "o": {"a": 0, "k": 100},
            "c": {"a": 1, "k": [
              {"t": 0, "s": [1, 0, 0, 1], "h": 1},
              {"t": 10, "s": [0, 0, 1, 1]}
            ]}
          }
        ]
      }]
    }
    """
      .trimIndent()
}
