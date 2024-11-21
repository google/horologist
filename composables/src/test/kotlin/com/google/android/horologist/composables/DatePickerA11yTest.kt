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

@file:OptIn(ExperimentalRoborazziApi::class)

package com.google.android.horologist.composables

import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performClick
import com.github.takahirom.roborazzi.ExperimentalRoborazziApi
import com.google.android.apps.common.testing.accessibility.framework.AccessibilityViewCheckResult
import com.google.android.apps.common.testing.accessibility.framework.checks.TouchTargetSizeCheck
import com.google.android.horologist.screenshots.rng.WearLegacyA11yTest
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.TypeSafeMatcher
import org.junit.Test
import java.time.LocalDate

class DatePickerA11yTest : WearLegacyA11yTest() {
    override fun accessibilitySuppressions(): Matcher<in AccessibilityViewCheckResult> {
        // Year is off screen initially
        return object : TypeSafeMatcher<AccessibilityViewCheckResult>() {
            override fun matchesSafely(item: AccessibilityViewCheckResult): Boolean {
                val isTouchTargetCheck =
                    item.accessibilityHierarchyCheck == TouchTargetSizeCheck::class.java
                return (isTouchTargetCheck && item.element?.boundsInScreen?.right == 454)
            }

            override fun describeTo(description: Description) {
                description.appendText("a TouchTargetSizeCheck on the screen edge")
            }
        }
    }

    // run during test
    override val runAtf: Boolean = false

    @Test
    fun screenshot() {
        enableTouchExploration()

        runScreenTest {
            DatePicker(
                onDateConfirm = {},
                date = LocalDate.of(2022, 4, 25),
            )
        }

        composeRule.onRoot().runAccessibilityChecks()
    }

    @Test
    fun interactionTest() {
        enableTouchExploration()

        runScreenTest {
            DatePicker(
                onDateConfirm = {},
                date = LocalDate.of(2022, 4, 25),
            )
        }

        composeRule.onRoot().runAccessibilityChecks()

        composeRule.onNodeWithContentDescription("Next").assertHasClickAction().performClick()

        composeRule.onNodeWithText("Day").assertExists()
        captureScreenshot("_1")

        composeRule.onRoot().runAccessibilityChecks()

        composeRule.onNodeWithContentDescription("Next").assertHasClickAction().performClick()

        composeRule.onNodeWithText("Month").assertExists()
        captureScreenshot("_2")

        composeRule.onRoot().runAccessibilityChecks()

        composeRule.onNodeWithContentDescription("Next").assertHasClickAction().performClick()

        composeRule.onNodeWithText("Year").assertExists()
        captureScreenshot("_3")

        composeRule.onRoot().runAccessibilityChecks()
    }
}
