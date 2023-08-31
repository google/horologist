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

import androidx.wear.compose.material.MaterialTheme
import com.google.android.horologist.compose.tools.ThemeValues
import com.google.android.horologist.compose.tools.themeValues
import com.google.android.horologist.screenshots.ScreenshotBaseTest
import com.google.android.horologist.screenshots.ScreenshotTestRule.Companion.screenshotTestRuleParams
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.ParameterizedRobolectricTestRunner
import java.time.LocalDate
import java.time.LocalTime

@RunWith(ParameterizedRobolectricTestRunner::class)
class PickerThemeTest(
    private val themeValue: ThemeValues,
) : ScreenshotBaseTest(
    screenshotTestRuleParams {
        screenTimeText = {}
        testLabel = themeValue.safeName
    },
) {

    @Test
    fun datePicker() {
        screenshotTestRule.setContent(takeScreenshot = true) {
            MaterialTheme(colors = themeValue.colors) {
                DatePicker(
                    onDateConfirm = {},
                    date = LocalDate.of(2022, 4, 25),
                )
            }
        }
    }

    @Test
    fun timePicker() {
        screenshotTestRule.setContent(takeScreenshot = true) {
            MaterialTheme(colors = themeValue.colors) {
                TimePicker(
                    time = LocalTime.of(10, 10, 0),
                    onTimeConfirm = {},
                )
            }
        }
    }

    @Test
    fun timePicker12h() {
        screenshotTestRule.setContent(takeScreenshot = true) {
            MaterialTheme(colors = themeValue.colors) {
                TimePickerWith12HourClock(
                    time = LocalTime.of(10, 10, 0),
                    onTimeConfirm = {},
                )
            }
        }
    }

    companion object {
        @JvmStatic
        @ParameterizedRobolectricTestRunner.Parameters
        fun colors() = themeValues
    }
}
