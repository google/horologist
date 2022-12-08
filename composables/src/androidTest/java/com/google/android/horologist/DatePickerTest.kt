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

@file:OptIn(ExperimentalHorologistComposablesApi::class)

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
import androidx.wear.compose.material.PickerState
import androidx.wear.compose.material.Text
import com.google.android.horologist.composables.DatePicker
import com.google.android.horologist.composables.DatePickerMonthState
import com.google.android.horologist.composables.DatePickerYearState
import com.google.android.horologist.composables.ExperimentalHorologistComposablesApi
import com.google.common.truth.Truth.assertThat
import org.junit.Ignore
import org.junit.Rule
import org.junit.Test
import java.time.LocalDate

@Ignore("Race condition in tests on beta02")
class DatePickerTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun testDate() {
        var date by mutableStateOf<LocalDate?>(null)
        composeTestRule.setContent {
            if (date == null) {
                DatePicker(
                    onDateConfirm = {
                        date = it
                    },
                    date = LocalDate.of(2022, 4, 25)
                )
            } else {
                Text(modifier = Modifier.testTag("date"), text = "$date")
            }
        }

        composeTestRule.onNodeWithContentDescription("Confirm").performClick()

        composeTestRule.onNodeWithTag("date").assertTextEquals("2022-04-25")
    }

    @Test
    fun fromDate_after_toDate() {
        var exception: Exception? = null
        try {
            composeTestRule.setContent {
                DatePicker(
                    onDateConfirm = {},
                    date = LocalDate.of(2022, 3, 25),
                    fromDate = LocalDate.of(2022, 3, 25),
                    toDate = LocalDate.of(2022, 2, 25)
                )
            }
        } catch (e: Exception) {
            exception = e
        }

        composeTestRule.runOnIdle {
            assertThat(exception).isInstanceOf(IllegalArgumentException::class.java)
        }
    }

    @Test
    fun date_outsideRange() {
        var exception: Exception? = null
        try {
            composeTestRule.setContent {
                DatePicker(
                    onDateConfirm = {},
                    date = LocalDate.of(2022, 4, 25),
                    fromDate = LocalDate.of(2022, 2, 25),
                    toDate = LocalDate.of(2022, 3, 25)
                )
            }
        } catch (e: Exception) {
            exception = e
        }

        composeTestRule.runOnIdle {
            assertThat(exception).isInstanceOf(IllegalArgumentException::class.java)
        }
    }

    @Test
    fun testDatePickerState() {
        val yearState: PickerState = PickerState(initialNumberOfOptions = 100, initiallySelectedOption = 0)
        val monthState: PickerState = PickerState(initialNumberOfOptions = 12, initiallySelectedOption = 0)

        val datePickerYearState = DatePickerYearState(yearState)
        val datePickerMonthState = DatePickerMonthState(datePickerYearState, monthState)

        assertThat(datePickerYearState.selectedYearEqualsFromYear).isTrue()
        assertThat(datePickerYearState.selectedYearEqualsToYear).isFalse()
        assertThat(datePickerMonthState.selectedMonthEqualsFromMonth).isTrue()
        assertThat(datePickerMonthState.selectedMonthEqualsToMonth).isFalse()
    }
}
