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

import androidx.wear.compose.material.PositionIndicator
import com.google.accompanist.testharness.TestHarness
import com.google.android.horologist.health.composables.model.MetricUiModel
import com.google.android.horologist.health.composables.theme.HR_HARD
import com.google.android.horologist.health.composables.theme.HR_MAXIMUM
import com.google.android.horologist.screenshots.ScreenshotBaseTest
import com.google.android.horologist.screenshots.ScreenshotTestRule
import com.google.android.horologist.screenshots.ScreenshotTestRule.Companion.screenshotTestRuleParams
import org.junit.Test
import org.robolectric.annotation.Config

class MetricsScreenTest : ScreenshotBaseTest(
    screenshotTestRuleParams {
        record = ScreenshotTestRule.RecordMode.Record
    },
) {
    @Test
    fun metricsScreenOneMetric() {
        screenshotTestRule.setContent(takeScreenshot = true) {
            MetricsScreen(
                firstMetric = MetricUiModel(
                    text = "21:34",
                    bottomRightText = "6",
                ),
                positionIndicator = {
                    PositionIndicator(
                        value = { 0.25f },
                    )
                },
            )
        }
    }

    @Test
    fun metricsScreenTwoMetrics() {
        screenshotTestRule.setContent(takeScreenshot = true) {
            MetricsScreen(
                firstMetric = MetricUiModel(
                    text = "21:34",
                    bottomRightText = "6",
                ),
                secondMetric = MetricUiModel(
                    text = "138",
                    bottomRightText = "cal",
                ),
                positionIndicator = {
                    PositionIndicator(
                        value = { 0.5f },
                    )
                },
            )
        }
    }

    @Test
    fun metricsScreenThreeMetrics() {
        screenshotTestRule.setContent(takeScreenshot = true) {
            MetricsScreen(
                firstMetric = MetricUiModel(
                    text = "164",
                    bottomRightText = "Vigorous",
                    color = HR_HARD,
                ),
                secondMetric = MetricUiModel(
                    text = "2.7",
                    bottomRightText = "mi",
                ),
                thirdMetric = MetricUiModel(
                    text = "21:34",
                    bottomRightText = "6",
                ),
                positionIndicator = {
                    PositionIndicator(
                        value = { 0.75f },
                    )
                },
            )
        }
    }

    @Test
    fun metricsScreenFourMetrics() {
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
                thirdMetric = MetricUiModel(
                    text = "8'51\"",
                    bottomRightText = "pace",
                ),
                fourthMetric = MetricUiModel(
                    text = "21:34",
                    bottomRightText = "6",
                ),
                positionIndicator = {
                    PositionIndicator(
                        value = { 1f },
                    )
                },
            )
        }
    }

    @Test
    fun metricsScreenMetricsSkipped() {
        screenshotTestRule.setContent(takeScreenshot = true) {
            MetricsScreen(
                firstMetric = MetricUiModel(
                    text = "198",
                    bottomRightText = "Peak",
                    color = HR_MAXIMUM,
                ),
                fourthMetric = MetricUiModel(
                    text = "21:34",
                    bottomRightText = "6",
                ),
                positionIndicator = {
                    PositionIndicator(
                        value = { 0.75f },
                    )
                },
            )
        }
    }

    @Config(
        sdk = [30],
        qualifiers = largeScreen,
    )
    @Test
    fun metricsScreenFourMetrics_largeScreen_smallestFont() {
        screenshotTestRule.setContent(takeScreenshot = true) {
            TestHarness(fontScale = smallestFontScale) {
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
                    thirdMetric = MetricUiModel(
                        text = "8'51\"",
                        bottomRightText = "pace",
                    ),
                    fourthMetric = MetricUiModel(
                        text = "21:34",
                        bottomRightText = "6",
                    ),
                    positionIndicator = {
                        PositionIndicator(
                            value = { 0.75f },
                        )
                    },
                )
            }
        }
    }

    @Config(
        sdk = [30],
        qualifiers = smallScreen,
    )
    @Test
    fun metricsScreenFourMetrics_smallScreen_largestFont() {
        screenshotTestRule.setContent(takeScreenshot = true) {
            TestHarness(fontScale = largestFontScale) {
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
                    thirdMetric = MetricUiModel(
                        text = "8'51\"",
                        bottomRightText = "pace",
                    ),
                    fourthMetric = MetricUiModel(
                        text = "21:34",
                        bottomRightText = "6",
                    ),
                    positionIndicator = {
                        PositionIndicator(
                            value = { 0.75f },
                        )
                    },
                )
            }
        }
    }

    companion object {
        private const val smallestFontScale = 0.94f
        private const val largestFontScale = 1.24f
        private const val smallScreen = "+w192dp-h192dp"
        private const val largeScreen = "+w213dp-h213dp"
    }
}
