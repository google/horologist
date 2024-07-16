/*
 * Copyright 2024 The Android Open Source Project
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

package com.google.android.horologist.audit

import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.google.android.horologist.audit.BulkAuditScreenshotTest.TestInstance
import com.google.android.horologist.compose.layout.ScalingLazyColumnState
import org.junit.Test
import org.robolectric.ParameterizedRobolectricTestRunner

public class PickersScreenshotTest(val testInstance: TestInstance) :
    AuditScreenshotTest(testInstance.device) {

        override val audit: AuditNavigation.SingleAuditScreen<*>
            get() = testInstance.audit

        @Test
        fun mainMenu() {
            lateinit var columnState: ScalingLazyColumnState

            runTest(captureScreenshot = false) {
                audit.compose()
            }

            captureScreenshot()

            if (audit.config == AuditNavigation.Pickers.Config.Date) {
                composeRule.onNodeWithContentDescription("Next")
                    .assertHasClickAction()
                    .performClick()

                composeRule.onNodeWithText("Month")
                    .assertExists()
                captureScreenshot("_month")

                composeRule.onNodeWithContentDescription("Next")
                    .assertHasClickAction()
                    .performClick()

                composeRule.onNodeWithText("Year")
                    .assertExists()
                captureScreenshot("_year")
            } else {
                composeRule.onNodeWithContentDescription("10 Minutes")
                    .assertHasClickAction()
                    .performClick()

                composeRule.onNodeWithText("Minute")
                    .assertExists()
                captureScreenshot("_minutes")

                if (audit.config == AuditNavigation.Pickers.Config.Time24hWithSeconds) {
                    composeRule.onNodeWithContentDescription("0 Seconds")
                        .assertHasClickAction()
                        .performClick()

                    composeRule.onNodeWithText("Second")
                        .assertExists()
                    captureScreenshot("_seconds")
                } else if (audit.config == AuditNavigation.Pickers.Config.Time12h) {
                    composeRule.onNodeWithContentDescription("AM")
                        .assertHasClickAction()
                        .performClick()

                    composeRule.onNodeWithText("")
                        .assertExists()
                    captureScreenshot("_ampm")
                }
            }
        }

        companion object {

            @JvmStatic
            @ParameterizedRobolectricTestRunner.Parameters
            public fun devices(): List<TestInstance> =
                AuditScreenshotTest.devices().flatMap { d ->
                    AuditNavigation.Pickers.screens.map { a -> TestInstance(a, d) }
                }
        }
    }
