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
import androidx.compose.material.icons.filled.ChevronRight
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
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.focused
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.PickerState
import androidx.wear.compose.material.Text
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
    fromDate: LocalDate? = null,
    toDate: LocalDate? = null
) {
    if (fromDate != null && toDate != null) {
        verifyDates(date, fromDate, toDate)
    }

    val datePickerState = remember(date) {
        if (fromDate != null && toDate == null) {
            DatePickerState(date = date, fromDate = fromDate, toDate = fromDate.plusYears(3000))
        } else if (fromDate == null && toDate != null) {
            DatePickerState(date = date, fromDate = toDate.minusYears(3000), toDate = toDate)
        } else {
            DatePickerState(date, fromDate, toDate)
        }
    }

    // Omit scaling according to Settings > Display > Font size for this screen
    val typography = MaterialTheme.typography.copy(
        display2 = MaterialTheme.typography.display2.copy(
            fontSize = with(LocalDensity.current) { 34.dp.toSp() }
        )
    )
    val talkbackEnabled by rememberTalkBackState()

    MaterialTheme(typography = typography) {
        var focusedElement by remember {
            mutableStateOf(
                if (talkbackEnabled) {
                    FocusableElementDatePicker.NONE
                } else FocusableElementDatePicker.DAY
            )
        }
        val focusRequesterDay = remember { FocusRequester() }
        val focusRequesterMonth = remember { FocusRequester() }
        val focusRequesterYear = remember { FocusRequester() }
        val focusRequesterConfirmButton = remember { FocusRequester() }

        LaunchedEffect(
            datePickerState.yearState.selectedOption,
            datePickerState.monthState.selectedOption
        ) {
            if (datePickerState.numOfMonths != datePickerState.monthState.numberOfOptions) {
                datePickerState.monthState.numberOfOptions = datePickerState.numOfMonths
            }
            if (datePickerState.numOfDays != datePickerState.dayState.numberOfOptions) {
                datePickerState.dayState.numberOfOptions = datePickerState.numOfDays
            }
        }
        val shortMonthNames = remember { getMonthNames("MMM") }
        val fullMonthNames = remember { getMonthNames("MMMM") }
        val yearContentDescription by remember(focusedElement, datePickerState.currentYear()) {
            derivedStateOf {
                createDescriptionDatePicker(
                    focusedElement,
                    datePickerState.currentYear(),
                    "${datePickerState.currentYear()}"
                )
            }
        }
        val monthContentDescription by remember(focusedElement, datePickerState.currentMonth()) {
            derivedStateOf {
                createDescriptionDatePicker(
                    focusedElement,
                    datePickerState.currentMonth(),
                    fullMonthNames[(datePickerState.currentMonth() - 1) % 12]
                )
            }
        }
        val dayContentDescription by remember(focusedElement, datePickerState.currentDay()) {
            derivedStateOf {
                createDescriptionDatePicker(
                    focusedElement,
                    datePickerState.currentDay(),
                    "${datePickerState.currentDay()}"
                )
            }
        }

        BoxWithConstraints(
            modifier = modifier
                .fillMaxSize()
                .then(
                    if (talkbackEnabled) {
                        when (focusedElement) {
                            FocusableElementDatePicker.DAY ->
                                Modifier.scrollablePicker(datePickerState.dayState)

                            FocusableElementDatePicker.MONTH ->
                                Modifier.scrollablePicker(datePickerState.monthState)

                            FocusableElementDatePicker.YEAR ->
                                Modifier.scrollablePicker(datePickerState.yearState)

                            else -> Modifier
                        }
                    } else {
                        Modifier
                    }
                )
        ) {
            val boxConstraints = this
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(Modifier.height(16.dp))
                Text(
                    text = when (focusedElement) {
                        FocusableElementDatePicker.DAY -> stringResource(R.string.horologist_picker_day)
                        FocusableElementDatePicker.MONTH -> stringResource(R.string.horologist_picker_month)
                        FocusableElementDatePicker.YEAR -> stringResource(R.string.horologist_picker_year)
                        else -> ""
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
                val doubleTapToNext = {
                        position: FocusableElementDatePicker, next: FocusableElementDatePicker ->
                    focusedElement = when (focusedElement) {
                        position -> next
                        else -> position
                    }
                }
                val offset = when (focusedElement) {
                    FocusableElementDatePicker.DAY -> (boxConstraints.maxWidth - dayWidth) / 2
                    FocusableElementDatePicker.MONTH ->
                        (boxConstraints.maxWidth - monthWidth) / 2 - dayWidth - spacerWidth
                    FocusableElementDatePicker.NONE -> (boxConstraints.maxWidth - dayWidth) / 2
                    else -> (boxConstraints.maxWidth - yearWidth) / 2 - monthWidth
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .offset(offset)
                ) {
                    if (focusedElement.index < 2) {
                        DatePickerImpl(
                            state = datePickerState.dayState,
                            readOnly = focusedElement != FocusableElementDatePicker.DAY,
                            onSelected = {
                                doubleTapToNext(
                                    FocusableElementDatePicker.DAY,
                                    FocusableElementDatePicker.MONTH
                                )
                            },
                            text = { day: Int -> "%d".format(datePickerState.currentDay(day)) },
                            width = dayWidth,
                            focusRequester = focusRequesterDay,
                            contentDescription = dayContentDescription,
                            userScrollEnabled = !talkbackEnabled ||
                                focusedElement == FocusableElementDatePicker.DAY,
                            talkbackEnabled = talkbackEnabled
                        )
                        Spacer(modifier = Modifier.width(spacerWidth))
                    }
                    DatePickerImpl(
                        state = datePickerState.monthState,
                        readOnly = focusedElement != FocusableElementDatePicker.MONTH,
                        onSelected = {
                            doubleTapToNext(
                                FocusableElementDatePicker.MONTH,
                                FocusableElementDatePicker.YEAR
                            )
                        },
                        text = { month: Int ->
                            shortMonthNames[(datePickerState.currentMonth(month) - 1) % 12]
                        },
                        width = monthWidth,
                        focusRequester = focusRequesterMonth,
                        contentDescription = monthContentDescription,
                        userScrollEnabled = !talkbackEnabled ||
                            focusedElement == FocusableElementDatePicker.MONTH,
                        talkbackEnabled = talkbackEnabled
                    )
                    if (focusedElement.index > 0) {
                        Spacer(modifier = Modifier.width(spacerWidth))
                        DatePickerImpl(
                            state = datePickerState.yearState,
                            readOnly = focusedElement != FocusableElementDatePicker.YEAR,
                            onSelected = {
                                doubleTapToNext(
                                    FocusableElementDatePicker.YEAR,
                                    FocusableElementDatePicker.CONFIRM_BUTTON
                                )
                            },
                            text = { year: Int -> "%4d".format(datePickerState.currentYear(year)) },
                            width = yearWidth,
                            focusRequester = focusRequesterYear,
                            contentDescription = yearContentDescription,
                            userScrollEnabled = !talkbackEnabled ||
                                focusedElement == FocusableElementDatePicker.YEAR,
                            talkbackEnabled = talkbackEnabled
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
                        if (focusedElement.index >= 2) {
                            val confirmedYear: Int = datePickerState.currentYear()
                            val confirmedMonth: Int = datePickerState.currentMonth()
                            val confirmedDay: Int = datePickerState.currentDay()
                            val confirmedDate =
                                LocalDate.of(confirmedYear, confirmedMonth, confirmedDay)
                            onDateConfirm(confirmedDate)
                        } else if (focusedElement == FocusableElementDatePicker.DAY) {
                            doubleTapToNext(
                                FocusableElementDatePicker.DAY,
                                FocusableElementDatePicker.MONTH
                            )
                        } else if (focusedElement == FocusableElementDatePicker.MONTH) {
                            doubleTapToNext(
                                FocusableElementDatePicker.MONTH,
                                FocusableElementDatePicker.YEAR
                            )
                        }
                    },
                    modifier = Modifier
                        .semantics {
                            focused = focusedElement == FocusableElementDatePicker.CONFIRM_BUTTON
                        }
                        .focusRequester(focusRequesterConfirmButton)
                ) {
                    Icon(
                        imageVector =
                        if (focusedElement.index < 2) Icons.Filled.ChevronRight
                        else Icons.Filled.Check,
                        contentDescription =
                        if (focusedElement.index >= 2) {
                            stringResource(R.string.horologist_picker_confirm_button_content_description)
                        } else {
                            stringResource(R.string.horologist_picker_next_button_content_description)
                        },
                        modifier = Modifier
                            .size(24.dp)
                            .wrapContentSize(align = Alignment.Center)
                    )
                }
                Spacer(Modifier.height(12.dp))
            }
            LaunchedEffect(focusedElement) {
                if (focusedElement != FocusableElementDatePicker.NONE) {
                    listOf(
                        focusRequesterDay,
                        focusRequesterMonth,
                        focusRequesterYear,
                        focusRequesterConfirmButton
                    )[focusedElement.index].requestFocus()
                }
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
    width: Dp,
    contentDescription: String,
    userScrollEnabled: Boolean,
    talkbackEnabled: Boolean
) {
    PickerWithRSB(
        readOnly = readOnly,
        state = state,
        focusRequester = focusRequester,
        modifier = Modifier.size(width, 100.dp),
        onSelected = onSelected,
        contentDescription = contentDescription,
        userScrollEnabled = userScrollEnabled
    ) { option ->
        TimePiece(
            selected = !readOnly,
            onSelected = onSelected,
            text = text(option),
            style = MaterialTheme.typography.display2,
            talkbackEnabled = talkbackEnabled
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

private fun getMonthNames(pattern: String): List<String> {
    val monthFormatter = DateTimeFormatter.ofPattern(pattern)
    val months = 1..12
    return months.map {
        LocalDate.of(2022, it, 1).format(monthFormatter)
    }
}

internal class DatePickerState constructor(
    private val date: LocalDate,
    private val fromDate: LocalDate?,
    private val toDate: LocalDate?
) {
    private val yearOffset = fromDate?.year ?: 1
    val numOfYears = (toDate?.year ?: 3000) - (yearOffset - 1)
    val yearState =
        PickerState(
            initialNumberOfOptions = numOfYears,
            initiallySelectedOption = date.year - yearOffset
        )
    val selectedYearEqualsFromYear: Boolean
        get() = (yearState.selectedOption == 0)
    val selectedYearEqualsToYear: Boolean
        get() = (yearState.selectedOption == yearState.numberOfOptions - 1)

    private val monthOffset: Int
        get() = (if (selectedYearEqualsFromYear) (fromDate?.monthValue ?: 1) else 1)
    private val maxMonths: Int
        get() = (if (selectedYearEqualsToYear) (toDate?.monthValue ?: 12) else 12)
    val numOfMonths: Int
        get() = (maxMonths.minus(monthOffset - 1))
    val monthState =
        PickerState(
            initialNumberOfOptions = numOfMonths,
            initiallySelectedOption = date.monthValue - monthOffset
        )
    val selectedMonthEqualsFromMonth: Boolean
        get() = (selectedYearEqualsFromYear && monthState.selectedOption == 0)
    val selectedMonthEqualsToMonth: Boolean
        get() = (
            selectedYearEqualsToYear &&
                monthState.selectedOption == monthState.numberOfOptions - 1
            )

    private val dayOffset: Int
        get() = (if (selectedMonthEqualsFromMonth) (fromDate?.dayOfMonth ?: 1) else 1)
    private val firstDayOfMonth: LocalDate
        get() = LocalDate.of(
            currentYear(),
            currentMonth(),
            1
        )
    private val maxDaysInMonth: Int
        get() = (
            if (toDate != null && selectedMonthEqualsToMonth) {
                toDate.dayOfMonth
            } else {
                firstDayOfMonth.with(TemporalAdjusters.lastDayOfMonth()).dayOfMonth
            }
            )
    val numOfDays: Int
        get() = maxDaysInMonth.minus(dayOffset - 1)

    val dayState =
        PickerState(
            initialNumberOfOptions = numOfDays,
            initiallySelectedOption = date.dayOfMonth - dayOffset
        )

    fun currentYear(year: Int = yearState.selectedOption): Int {
        return year + yearOffset
    }
    fun currentMonth(month: Int = monthState.selectedOption): Int {
        return month + monthOffset
    }
    fun currentDay(day: Int = dayState.selectedOption): Int {
        return day + dayOffset
    }
}

private fun createDescriptionDatePicker(
    focusedElement: FocusableElementDatePicker,
    selectedValue: Int,
    label: String
): String {
    return when (focusedElement) {
        FocusableElementDatePicker.DAY -> {
            "$selectedValue"
        }
        FocusableElementDatePicker.YEAR -> {
            "$selectedValue"
        }
        else -> label
    }
}

private enum class FocusableElementDatePicker(val index: Int) {
    DAY(0),
    MONTH(1),
    YEAR(2),
    CONFIRM_BUTTON(3),
    NONE(-1)
}
