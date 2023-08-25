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

import com.google.android.horologist.health.composables.model.MetricUiModel
import com.google.android.horologist.health.composables.theme.HR_HARD
import com.google.android.horologist.health.composables.theme.HR_LIGHT
import com.google.android.horologist.health.composables.theme.HR_MAXIMUM
import com.google.android.horologist.health.composables.theme.HR_MODERATE
import com.google.android.horologist.screenshots.ScreenshotBaseTest
import com.google.android.horologist.screenshots.ScreenshotTestRule.Companion.screenshotTestRuleParams
import org.junit.Test

class MetricDisplayTest : ScreenshotBaseTest(
    screenshotTestRuleParams {
        screenTimeText = {}
    }
) {
    @Test
    fun metricDisplay() {
        screenshotTestRule.setContent(isComponent = true, takeScreenshot = true) {
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

    @Test
    fun metricDisplayTextOnly() {
        screenshotTestRule.setContent(isComponent = true, takeScreenshot = true) {
            MetricDisplay(
                metric = MetricUiModel(
                    text = "18:52",
                    color = HR_LIGHT
                )
            )
        }
    }

    @Test
    fun metricDisplayTopRightText() {
        screenshotTestRule.setContent(isComponent = true, takeScreenshot = true) {
            MetricDisplay(
                metric = MetricUiModel(
                    text = "8'32\"",
                    topRightText = ":15",
                    color = HR_MODERATE
                )
            )
        }
    }

    @Test
    fun metricDisplayBottomRightText() {
        screenshotTestRule.setContent(isComponent = true, takeScreenshot = true) {
            MetricDisplay(
                metric = MetricUiModel(
                    text = "2.1",
                    bottomRightText = "/3 mi",
                    color = HR_MAXIMUM
                )
            )
        }
    }

    @Test
    fun metricDisplayTextTruncation() {
        screenshotTestRule.setContent(isComponent = true, takeScreenshot = true) {
            MetricDisplay(
                metric = MetricUiModel(
                    text = "Very very long text",
                    topRightText = "Vigorous",
                    bottomRightText = "bpm"
                )
            )
        }
    }

    @Test
    fun metricDisplayBothRightTextTruncation() {
        screenshotTestRule.setContent(isComponent = true, takeScreenshot = true) {
            MetricDisplay(
                metric = MetricUiModel(
                    text = "139",
                    topRightText = "Very very very very long text",
                    bottomRightText = "Very very very very long text"
                )
            )
        }
    }

    @Test
    fun metricDisplayTopRightTextTruncation() {
        screenshotTestRule.setContent(isComponent = true, takeScreenshot = true) {
            MetricDisplay(
                metric = MetricUiModel(
                    text = "139",
                    topRightText = "Very very very very long text"
                )
            )
        }
    }

    @Test
    fun metricDisplayBottomRightTextTruncation() {
        screenshotTestRule.setContent(isComponent = true, takeScreenshot = true) {
            MetricDisplay(
                metric = MetricUiModel(
                    text = "139",
                    bottomRightText = "Very very very very long text"
                )
            )
        }
    }
}
