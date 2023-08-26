/*
 * Copyright 2023 The Android Open Source Project
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

package com.google.android.horologist.health.composables.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.LayoutDirection
import com.google.accompanist.testharness.TestHarness
import com.google.android.horologist.health.composables.model.MetricUiModel
import com.google.android.horologist.health.composables.theme.HR_HARD
import com.google.android.horologist.health.composables.theme.HR_MODERATE
import com.google.android.horologist.screenshots.ScreenshotBaseTest
import com.google.android.horologist.screenshots.ScreenshotTestRule.Companion.screenshotTestRuleParams
import org.junit.Test

class MetricDisplayA11yTest : ScreenshotBaseTest(
    screenshotTestRuleParams {
        enableA11y = true
        screenTimeText = {}
    }
) {

    @Test
    fun metricDisplay() {
        screenshotTestRule.setContent(takeScreenshot = true) {
            Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                MetricDisplay(
                    metric = MetricUiModel(
                        text = "139",
                        topRightText = "Vigorous",
                        bottomRightText = "bpm",
                        color = HR_MODERATE
                    )
                )
            }
        }
    }

    @Test
    fun rtl() {
        screenshotTestRule.setContent(takeScreenshot = true) {
            TestHarness(layoutDirection = LayoutDirection.Rtl) {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                    MetricDisplay(
                        metric = MetricUiModel(
                            text = "139",
                            topRightText = "Vigorous",
                            bottomRightText = "bpm",
                            color = HR_HARD
                        )
                    )
                }
            }
        }
    }
}
