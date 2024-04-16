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

package com.google.android.horologist.composables

import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.click
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onParent
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.printToString
import androidx.compose.ui.test.requestFocus
import com.google.android.horologist.screenshots.rng.WearLegacyA11yTest
import org.junit.Test
import java.time.LocalTime

class TimePicker12hA11yTest : WearLegacyA11yTest() {

    @Test
    fun initial() {
        enableTouchExploration()

        runScreenTest {
            TimePickerWith12HourClock(
                time = LocalTime.of(10, 10, 0),
                onTimeConfirm = {},
            )
        }

        composeRule.onNodeWithContentDescription("Confirm")
            .assertHasClickAction()
    }

    @Test
    fun interactionTest() {
        enableTouchExploration()

        runScreenTest {
            TimePickerWith12HourClock(
                time = LocalTime.of(10, 10, 0),
                onTimeConfirm = {},
            )
        }

        println(composeRule.onRoot().printToString())
        composeRule.onNodeWithContentDescription("Hour")
            .assertHasClickAction()
            .performClick()

        captureScreenshot("_1")

        composeRule.onNodeWithContentDescription("Minute")
            .assertHasClickAction()
            .performClick()

        captureScreenshot("_2")

        composeRule.onNodeWithContentDescription("Period")
            .assertHasClickAction()
            .performClick()

        captureScreenshot("_3")
    }
}
