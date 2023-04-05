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

import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsFocused
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.performClick
import com.google.android.horologist.screenshots.ScreenshotTest
import org.junit.Test
import java.time.LocalDate

class DatePickerA11yTest : ScreenshotTest() {
    init {
        screenTimeText = {}
        enableA11yTest()
    }

    @Test
    fun initial() {
        takeScreenshot(
            checks = {
                rule.onNodeWithContentDescription("Next")
                    .assertHasClickAction()

                rule.onNodeWithContentDescription("Day, 25")
                    .assertIsFocused()
            }
        ) {
            DatePicker(
                onDateConfirm = {},
                date = LocalDate.of(2022, 4, 25)
            )
        }
    }

    @Test
    fun next() {
        takeScreenshot(
            checks = {
                rule.onNodeWithContentDescription("Next")
                    .performClick()

                rule.waitUntil {
                    rule.onNodeWithContentDescription("April")
                        .fetchSemanticsNode()
                        .config[SemanticsProperties.Focused]
                }
            }
        ) {
            DatePicker(
                onDateConfirm = {},
                date = LocalDate.of(2022, 4, 25)
            )
        }
    }
}
