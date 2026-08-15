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
import androidx.compose.foundation.layout.Box
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.onNodeWithTag
import com.github.takahirom.roborazzi.captureRoboImage
import com.google.android.horologist.screenshots.rng.WearScreenshotTest
import org.junit.Test

class LottieBasicScreenshotTest : WearScreenshotTest() {

  @Test
  fun geometry() {
    composeRule.setContent {
      Box(modifier = Modifier.background(Color.Black).testTag("Box")) {
        LottiePreview(R.raw.geometry, clock = SettableRemoteClock())
      }
    }

    composeRule.onNodeWithTag("Box").captureRoboImage(testName(""))
  }
}
