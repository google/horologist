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

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.PickerState
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.rememberPickerState
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.TemporalAdjusters

/**
 * Full screen date picker with day, month, year.
 *
 * This component is designed to take most/all of the screen and utilizes large fonts. In order to
 * ensure that it will draw properly on smaller screens it does not take account of user font size
 * overrides for MaterialTheme.typography.display2 which is used to display the main picker
 * value.
 *
 * @param onDateConfirm the button event handler.
 * @param modifier the modifiers for the `Box` containing the UI elements.
 * @param date the initial value to seed the picker with.
 * @param fromDate the minimum date to be selected in picker
 * @param toDate the maximum date to be selected in picker
 */
@Composable
public fun DatePicker(
    onDateConfirm: (LocalDate) -> Unit,
    modifier: Modifier = Modifier,
    date: LocalDate = LocalDate.now(),
    fromDate: LocalDate = date.minusYears(100),
    toDate: LocalDate = date.plusYears(100)
) {
    verifyDates(date, fromDate, toDate)

    // Omit scaling according to Settings > Display > Font size for this screen
    val typography = MaterialTheme.typography.copy(
        display2 = MaterialTheme.typography.display2.copy(
            fontSize = with(LocalDensity.current) { 34.dp.toSp() }
        )
    )
    MaterialTheme(typography = typography) {
        val yearState =
            rememberPickerState(
                initialNumberOfOptions = toDate.year - fromDate.year.minus(1),
                initiallySelectedOption = date.year - fromDate.year
            )
        val datePickerYearState by
        remember(yearState) { derivedStateOf { DatePickerYearState(yearState) } }
        val numOfMonths by
        remember(yearState) {
            derivedStateOf {
                (
                    if (datePickerYearState.selectedYearEqualsToYear) {
                        toDate.monthValue
                    } else 12
                    ).minus(
                    if (datePickerYearState.selectedYearEqualsFromYear) {
                        fromDate.monthValue.minus(1)
                    } else 0
                )
            }
        }
        val monthState =
            rememberPickerState(
                initialNumberOfOptions = numOfMonths,
                initiallySelectedOption =
                date.monthValue -
                    (
                        if (datePickerYearState.selectedYearEqualsFromYear) {
                            fromDate.monthValue
                        } else 1
                        )
            )

        val datePickerMonthState by
        remember(yearState, monthState) {
            derivedStateOf { DatePickerMonthState(datePickerYearState, monthState) }
        }
        val maxDayInMonth by
        remember(yearState, monthState) {
            derivedStateOf {
                val firstDayOfMonth =
                    LocalDate.of(
                        yearState.selectedOption + fromDate.year,
                        monthState.selectedOption +
                            (
                                if (datePickerYearState.selectedYearEqualsFromYear) {
                                    fromDate.monthValue
                                } else 1
                                ),
                        1
                    )
                firstDayOfMonth.with(TemporalAdjusters.lastDayOfMonth()).dayOfMonth
            }
        }
        val numOfDays by
        remember(monthState) {
            derivedStateOf {
                (
                    if (datePickerMonthState.selectedMonthEqualsToMonth) {
                        toDate.dayOfMonth
                    } else maxDayInMonth
                    ).minus(
                    if (datePickerMonthState.selectedMonthEqualsFromMonth) {
                        fromDate.dayOfMonth.minus(1)
                    } else 0
                )
            }
        }
        val dayState =
            rememberPickerState(
                initialNumberOfOptions = numOfDays,
                initiallySelectedOption =
                date.dayOfMonth -
                    (
                        if (datePickerMonthState.selectedMonthEqualsFromMonth) {
                            fromDate.dayOfMonth
                        } else 1
                        )
            )
        val focusRequester1 = remember { FocusRequester() }
        val focusRequester2 = remember { FocusRequester() }
        val focusRequester3 = remember { FocusRequester() }

        LaunchedEffect(numOfMonths) {
            if (numOfMonths != monthState.numberOfOptions) {
                monthState.numberOfOptions = numOfMonths
            }
        }
        LaunchedEffect(numOfDays) {
            if (numOfDays != dayState.numberOfOptions) {
                dayState.numberOfOptions = numOfDays
            }
        }
        val monthNames = remember {
            val monthFormatter = DateTimeFormatter.ofPattern("MMM")
            val months = 1..12
            months.map {
                // Translate month index into 3-character month string e.g. Jan.
                LocalDate.of(2022, it, 1).format(monthFormatter)
            }
        }
        var selectedColumn by remember { mutableStateOf(0) }
        BoxWithConstraints(modifier = modifier.fillMaxSize()) {
            val boxConstraints = this
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(Modifier.height(16.dp))
                Text(
                    text = when (selectedColumn) {
                        0 -> stringResource(R.string.horologist_picker_day)
                        1 -> stringResource(R.string.horologist_picker_month)
                        else -> stringResource(R.string.horologist_picker_year)
                    },
                    color = MaterialTheme.colors.secondary,
                    style = MaterialTheme.typography.button,
                    maxLines = 1
                )
                val weightsToCenterVertically = 0.5f
                Spacer(
                    Modifier
                        .fillMaxWidth()
                        .weight(weightsToCenterVertically)
                )
                val spacerWidth = 8.dp
                val dayWidth = 54.dp
                val monthWidth = 80.dp
                val yearWidth = 128.dp
                val offset = when (selectedColumn) {
                    0 -> (boxConstraints.maxWidth - dayWidth) / 2
                    1 -> (boxConstraints.maxWidth - monthWidth) / 2 - dayWidth - spacerWidth
                    else -> (boxConstraints.maxWidth - yearWidth) / 2 - monthWidth
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .offset(offset)
                ) {
                    if (selectedColumn < 2) {
                        DatePickerImpl(
                            state = dayState,
                            readOnly = selectedColumn != 0,
                            onSelected = { selectedColumn = 0 },
                            text = { day: Int ->
                                "%d".format(
                                    day + (if (datePickerMonthState.selectedMonthEqualsFromMonth) fromDate.dayOfMonth else 1)
                                )
                            },
                            width = dayWidth,
                            focusRequester = focusRequester1,
                            contentDescription =
                            "%d".format(
                                dayState.selectedOption +
                                    (if (datePickerMonthState.selectedMonthEqualsFromMonth) fromDate.dayOfMonth else 1)
                            )
                        )
                        Spacer(modifier = Modifier.width(spacerWidth))
                    }
                    DatePickerImpl(
                        state = monthState,
                        readOnly = selectedColumn != 1,
                        onSelected = { selectedColumn = 1 },
                        text = { month: Int ->
                            monthNames[
                                (
                                    month +
                                        (
                                            if (datePickerYearState.selectedYearEqualsFromYear) fromDate.monthValue.minus(1)
                                            else 0
                                            )
                                    )
                            ]
                        },
                        width = monthWidth,
                        focusRequester = focusRequester2,
                        contentDescription =
                        monthNames[
                            (
                                monthState.selectedOption +
                                    (if (datePickerYearState.selectedYearEqualsFromYear) fromDate.monthValue.minus(1) else 0)
                                )
                        ]
                    )
                    if (selectedColumn > 0) {
                        Spacer(modifier = Modifier.width(spacerWidth))
                        DatePickerImpl(
                            state = yearState,
                            readOnly = selectedColumn != 2,
                            onSelected = { selectedColumn = 2 },
                            text = { year: Int -> "%4d".format(year + fromDate.year) },
                            width = yearWidth,
                            focusRequester = focusRequester3,
                            contentDescription = "%4d".format(yearState.selectedOption + fromDate.year)
                        )
                    }
                }
                Spacer(
                    Modifier
                        .fillMaxWidth()
                        .weight(weightsToCenterVertically)
                )
                Button(
                    onClick = {
                        val confirmedYear: Int = yearState.selectedOption + fromDate.year
                        val confirmedMonth: Int =
                            monthState.selectedOption +
                                (
                                    if (datePickerYearState.selectedYearEqualsFromYear) {
                                        fromDate.monthValue
                                    } else 1
                                    )
                        val confirmedDay: Int =
                            dayState.selectedOption +
                                (
                                    if (datePickerMonthState.selectedMonthEqualsFromMonth) {
                                        fromDate.dayOfMonth
                                    } else 1
                                    )
                        val confirmedDate = LocalDate.of(confirmedYear, confirmedMonth, confirmedDay)
                        onDateConfirm(confirmedDate)
                    }
                ) {
                    Icon(
                        imageVector = Icons.Filled.Check,
                        contentDescription = stringResource(id = R.string.horologist_picker_confirm_button_content_description),
                        modifier = Modifier
                            .size(24.dp)
                            .wrapContentSize(align = Alignment.Center)
                    )
                }
                Spacer(Modifier.height(12.dp))
            }
            LaunchedEffect(selectedColumn) {
                listOf(focusRequester1, focusRequester2, focusRequester3)[selectedColumn]
                    .requestFocus()
            }
        }
    }
}

@Composable
private fun DatePickerImpl(
    state: PickerState,
    readOnly: Boolean,
    onSelected: () -> Unit,
    text: (option: Int) -> String,
    focusRequester: FocusRequester,
    contentDescription: String?,
    width: Dp
) {
    PickerWithRSB(
        readOnly = readOnly,
        state = state,
        focusRequester = focusRequester,
        modifier = Modifier.size(width, 100.dp),
        contentDescription = contentDescription,
        onSelected = onSelected
    ) { option ->
        TimePiece(
            selected = !readOnly,
            onSelected = onSelected,
            text = text(option),
            style = MaterialTheme.typography.display2
        )
    }
}

private fun verifyDates(
    date: LocalDate,
    fromDate: LocalDate,
    toDate: LocalDate
) {
    require(toDate >= fromDate) { "toDate should be greater than or equal to fromDate" }
    require(date in fromDate..toDate) { "date should lie between fromDate and toDate" }
}

internal class DatePickerYearState constructor(
    private val yearState: PickerState
) {
    val selectedYearEqualsFromYear: Boolean = yearState.selectedOption == 0
    val selectedYearEqualsToYear: Boolean = yearState.selectedOption == yearState.numberOfOptions - 1
}

internal class DatePickerMonthState constructor(
    private val datePickerYearState: DatePickerYearState,
    private val monthState: PickerState
) {
    val selectedMonthEqualsFromMonth: Boolean = datePickerYearState.selectedYearEqualsFromYear && monthState.selectedOption == 0
    val selectedMonthEqualsToMonth: Boolean = datePickerYearState.selectedYearEqualsToYear && monthState.selectedOption == monthState.numberOfOptions - 1
}
