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
import androidx.wear.compose.material.Text
import com.google.android.horologist.composables.DatePicker
import com.google.android.horologist.composables.DatePickerState
import com.google.android.horologist.composables.ExperimentalHorologistComposablesApi
import com.google.common.truth.Truth.assertThat
import org.junit.Assert
import org.junit.Ignore
import org.junit.Rule
import org.junit.Test
import java.time.LocalDate

@Ignore("Race condition in tests on beta02")
class DatePickerTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun onDateConfirm_called_when_confirm_clicked() {
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
    fun should_throw_if_fromDate_after_toDate() {
        Assert.assertThrows(IllegalArgumentException::class.java) {
            composeTestRule.setContent {
                DatePicker(
                    onDateConfirm = {},
                    date = LocalDate.of(2022, 3, 25),
                    fromDate = LocalDate.of(2022, 3, 25),
                    toDate = LocalDate.of(2022, 2, 25)
                )
            }
        }
    }

    @Test
    fun should_throw_if_date_outside_from_to_range() {
        Assert.assertThrows(IllegalArgumentException::class.java) {
            composeTestRule.setContent {
                DatePicker(
                    onDateConfirm = {},
                    date = LocalDate.of(2022, 4, 25),
                    fromDate = LocalDate.of(2022, 2, 25),
                    toDate = LocalDate.of(2022, 3, 25)
                )
            }
        }
    }

    @Test
    fun current_values_checked_in_date_picker_state() {
        val date = LocalDate.of(2022, 4, 25)

        val datePickerState = DatePickerState(date, date, date)

        assertThat(datePickerState.currentYear()).isEqualTo(2022)
        assertThat(datePickerState.currentMonth()).isEqualTo(4)
        assertThat(datePickerState.currentDay()).isEqualTo(25)
    }

    @Test
    fun year_state_initialised_to_fromYear() {
        val date = LocalDate.of(2022, 4, 25)

        val datePickerState = DatePickerState(date = date, fromDate = date, toDate = date.plusYears(1))

        assertThat(datePickerState.selectedYearEqualsFromYear).isTrue()
        assertThat(datePickerState.selectedYearEqualsToYear).isFalse()
        assertThat(datePickerState.numOfMonths).isEqualTo(9)
    }

    @Test
    fun month_state_initialised_to_fromMonth() {
        val date = LocalDate.of(2022, 4, 25)

        val datePickerState = DatePickerState(date = date, fromDate = date, toDate = date.plusYears(1))

        assertThat(datePickerState.selectedMonthEqualsFromMonth).isTrue()
        assertThat(datePickerState.selectedMonthEqualsToMonth).isFalse()
        assertThat(datePickerState.numOfDays).isEqualTo(6)
    }

    @Test
    fun year_state_initialised_to_toYear() {
        val date = LocalDate.of(2022, 4, 25)

        val datePickerState = DatePickerState(date = date, fromDate = date.minusYears(1), toDate = date)

        assertThat(datePickerState.selectedYearEqualsFromYear).isFalse()
        assertThat(datePickerState.selectedYearEqualsToYear).isTrue()
        assertThat(datePickerState.numOfMonths).isEqualTo(4)
    }

    @Test
    fun month_state_initialised_to_toMonth() {
        val date = LocalDate.of(2022, 4, 25)

        val datePickerState = DatePickerState(date = date, fromDate = date.minusYears(1), toDate = date)

        assertThat(datePickerState.selectedMonthEqualsFromMonth).isFalse()
        assertThat(datePickerState.selectedMonthEqualsToMonth).isTrue()
        assertThat(datePickerState.numOfDays).isEqualTo(25)
    }
}
