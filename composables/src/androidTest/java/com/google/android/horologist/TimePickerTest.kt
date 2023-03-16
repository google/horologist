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

@file:OptIn(ExperimentalHorologistComposablesApi::class, ExperimentalHorologistComposablesApi::class)

package com.google.android.horologist

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.wear.compose.material.Text
import com.google.android.horologist.composables.ExperimentalHorologistComposablesApi
import com.google.android.horologist.composables.TimePicker
import com.google.android.horologist.composables.TimePickerWith12HourClock
import org.junit.Ignore
import org.junit.Rule
import org.junit.Test
import java.time.LocalTime

@Ignore("Race condition in tests on beta02")
class TimePickerTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun testTime() {
        var time by mutableStateOf<LocalTime?>(null)
        composeTestRule.setContent {
            if (time == null) {
                TimePicker(
                    onTimeConfirm = {
                        time = it
                    },
                    time = LocalTime.of(11, 59, 31),
                    showSeconds = false
                )
            } else {
                Text(modifier = Modifier.testTag("time"), text = "$time")
            }
        }

        composeTestRule.onNodeWithContentDescription("Confirm").performClick()

        composeTestRule.onNodeWithTag("time").assertTextEquals("11:59")
    }

    @Test
    fun testTimeWithSeconds() {
        var time by mutableStateOf<LocalTime?>(null)
        composeTestRule.setContent {
            if (time == null) {
                TimePicker(
                    onTimeConfirm = {
                        time = it
                    },
                    time = LocalTime.of(11, 59, 31)
                )
            } else {
                Text(modifier = Modifier.testTag("time"), text = "$time")
            }
        }

        composeTestRule.onNodeWithContentDescription("Confirm").performClick()

        composeTestRule.onNodeWithTag("time").assertTextEquals("11:59:31")
    }

    @Test
    fun test12hour() {
        var time by mutableStateOf<LocalTime?>(null)
        composeTestRule.setContent {
            if (time == null) {
                TimePickerWith12HourClock(
                    onTimeConfirm = {
                        time = it
                    },
                    time = LocalTime.of(11, 59)
                )
            } else {
                Text(modifier = Modifier.testTag("time"), text = "$time")
            }
        }

        composeTestRule.onNodeWithContentDescription("Confirm").performClick()

        composeTestRule.onNodeWithTag("time").assertTextEquals("11:59")
    }

    @Test
    fun content_description_exists_for_plural_hours() {
        composeTestRule.setContent {
            TimePicker(
                onTimeConfirm = {},
                time = LocalTime.of(11, 59, 31)
            )
        }

        composeTestRule.onNodeWithContentDescription("11 hours", ignoreCase = true).assertExists()
    }

    @Test
    fun content_description_exists_for_singular_hour() {
        composeTestRule.setContent {
            TimePicker(
                onTimeConfirm = {},
                time = LocalTime.of(1, 1, 1)
            )
        }

        composeTestRule.onNodeWithContentDescription("1 hour", ignoreCase = true).assertExists()
    }

    @Test
    fun content_description_exists_for_plural_minutes() {
        composeTestRule.setContent {
            TimePicker(
                onTimeConfirm = {},
                time = LocalTime.of(11, 59, 31)
            )
        }

        composeTestRule.onNodeWithContentDescription("59 minutes", ignoreCase = true).assertExists()
    }

    @Test
    fun content_description_exists_for_singular_minute() {
        composeTestRule.setContent {
            TimePicker(
                onTimeConfirm = {},
                time = LocalTime.of(1, 1, 1)
            )
        }

        composeTestRule.onNodeWithContentDescription("1 minute", ignoreCase = true).assertExists()
    }

    @Test
    fun content_description_exists_for_plural_seconds() {
        composeTestRule.setContent {
            TimePicker(
                onTimeConfirm = {},
                time = LocalTime.of(11, 59, 31)
            )
        }

        composeTestRule.onNodeWithContentDescription("31 seconds", ignoreCase = true).assertExists()
    }

    @Test
    fun content_description_exists_for_singular_second() {
        composeTestRule.setContent {
            TimePicker(
                onTimeConfirm = {},
                time = LocalTime.of(1, 1, 1)
            )
        }

        composeTestRule.onNodeWithContentDescription("1 second", ignoreCase = true).assertExists()
    }
}
