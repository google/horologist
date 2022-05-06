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

import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material.Text
import com.google.android.horologist.composables.ExperimentalHorologistComposablesApi
import com.google.android.horologist.composables.TimePicker
import com.google.android.horologist.composables.TimePickerWith12HourClock
import org.junit.Rule
import org.junit.Test
import java.time.LocalTime

class TimePickerTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun testTimeWithSeconds() {
        var time by mutableStateOf<LocalTime?>(null)
        composeTestRule.setContent {
            if (time == null) {
                TimePicker(
                    buttonIcon = {
                        Icon(
                            imageVector = Icons.Filled.Check,
                            contentDescription = "check",
                            modifier = Modifier
                                .size(24.dp)
                                .wrapContentSize(align = Alignment.Center),
                        )
                    },
                    onClick = {
                        time = it
                    },
                    initial = LocalTime.of(11, 59, 31)
                )
            } else {
                Text(modifier = Modifier.testTag("time"), text = "$time")
            }
        }

        composeTestRule.onNodeWithContentDescription("check").performClick()

        composeTestRule.onNodeWithTag("time").assertTextEquals("11:59:31")
    }

    @Test
    fun test12hour() {
        var time by mutableStateOf<LocalTime?>(null)
        composeTestRule.setContent {
            if (time == null) {
                TimePickerWith12HourClock(
                    buttonIcon = {
                        Icon(
                            imageVector = Icons.Filled.Check,
                            contentDescription = "check",
                            modifier = Modifier
                                .size(24.dp)
                                .wrapContentSize(align = Alignment.Center),
                        )
                    },
                    onClick = {
                        time = it
                    },
                    initial = LocalTime.of(11, 59)
                )
            } else {
                Text(modifier = Modifier.testTag("time"), text = "$time")
            }
        }

        composeTestRule.onNodeWithContentDescription("check").performClick()

        composeTestRule.onNodeWithTag("time").assertTextEquals("11:59")
    }
}
