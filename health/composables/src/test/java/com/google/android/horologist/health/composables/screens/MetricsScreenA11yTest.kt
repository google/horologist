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

package com.google.android.horologist.health.composables.screens

import androidx.compose.ui.unit.LayoutDirection
import com.google.accompanist.testharness.TestHarness
import com.google.android.horologist.health.composables.model.MetricUiModel
import com.google.android.horologist.health.composables.theme.HR_MAXIMUM
import com.google.android.horologist.screenshots.ScreenshotBaseTest
import com.google.android.horologist.screenshots.ScreenshotTestRule.Companion.screenshotTestRuleParams
import org.junit.Test

class MetricsScreenA11yTest : ScreenshotBaseTest(
    screenshotTestRuleParams {
        enableA11y = true
        screenTimeText = {}
    },
) {

    @Test
    fun metricsScreenTwoMetrics() {
        screenshotTestRule.setContent(takeScreenshot = true) {
            MetricsScreen(
                firstMetric = MetricUiModel(
                    text = "198",
                    bottomRightText = "Peak",
                    color = HR_MAXIMUM,
                ),
                secondMetric = MetricUiModel(
                    text = "2.7",
                    bottomRightText = "mi",
                ),
            )
        }
    }

    @Test
    fun metricsScreenTwoMetrics_rtl() {
        screenshotTestRule.setContent(takeScreenshot = true) {
            TestHarness(layoutDirection = LayoutDirection.Rtl) {
                MetricsScreen(
                    firstMetric = MetricUiModel(
                        text = "198",
                        bottomRightText = "Peak",
                        color = HR_MAXIMUM,
                    ),
                    secondMetric = MetricUiModel(
                        text = "2.7",
                        bottomRightText = "mi",
                    ),
                )
            }
        }
    }
}
