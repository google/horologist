/*
 * Copyright 2022 The Android Open Source Project
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

@file:OptIn(ExperimentalWearFoundationApi::class)

package com.google.android.horologist.composables

import android.app.Application
import android.view.accessibility.AccessibilityManager
import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.performClick
import androidx.test.core.app.ApplicationProvider
import androidx.test.filters.FlakyTest
import androidx.wear.compose.foundation.ExperimentalWearFoundationApi
import com.google.android.horologist.screenshots.ScreenshotBaseTest
import com.google.android.horologist.screenshots.ScreenshotTestRule.Companion.screenshotTestRuleParams
import org.junit.Test
import org.robolectric.Shadows
import java.time.LocalDate

@FlakyTest(detail = "https://github.com/google/horologist/issues/1806")
class DatePickerA11yTest : ScreenshotBaseTest(
    screenshotTestRuleParams {
        screenTimeText = {}
        enableA11y = true
    },
) {
    @Test
    fun interactionTest() {
        enableTouchExploration()

        screenshotTestRule.setContent {
            DatePicker(
                onDateConfirm = {},
                date = LocalDate.of(2022, 4, 25),
            )
        }

        screenshotTestRule.interact {
            onNodeWithContentDescription("Next")
                .assertHasClickAction()

            waitForIdle()
//            onNodeWithContentDescription("Day, 25")
//                .assertIsFocused()
        }

        screenshotTestRule.takeScreenshot()

        screenshotTestRule.interact {
            onNodeWithContentDescription("Next")
                .performClick()

            waitForIdle()
//            waitUntil {
//                onNodeWithContentDescription("April")
//                    .fetchSemanticsNode()
//                    .config[SemanticsProperties.Focused]
//            }
        }

        screenshotTestRule.takeScreenshot()
    }

    companion object {
        fun enableTouchExploration() {
            val applicationContext = ApplicationProvider.getApplicationContext<Application>()
            val a11yManager = applicationContext.getSystemService(AccessibilityManager::class.java)
            val shadow = Shadows.shadowOf(a11yManager)

            shadow.setEnabled(true)
            shadow.setTouchExplorationEnabled(true)
        }
    }
}
