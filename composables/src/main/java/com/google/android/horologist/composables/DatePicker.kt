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

import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.focusable
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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
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
import androidx.wear.compose.material.Text
import com.google.android.horologist.composables.picker.PickerGroup
import com.google.android.horologist.composables.picker.PickerGroupState
import com.google.android.horologist.composables.picker.PickerState
import com.google.android.horologist.composables.picker.rememberPickerGroupState
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
    val fullyDrawn = remember { Animatable(0f) }

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
    val touchExplorationStateProvider = remember { DefaultTouchExplorationStateProvider() }
    val touchExplorationServicesEnabled by touchExplorationStateProvider
        .touchExplorationState()

    MaterialTheme(typography = typography) {
        // When the time picker loads, none of the individual pickers are selected in talkback mode,
        // otherwise day picker should be focused.
        val pickerGroupState = if (touchExplorationServicesEnabled) {
            rememberPickerGroupState(FocusableElementDatePicker.NONE.index)
        } else {
            rememberPickerGroupState(FocusableElementDatePicker.DAY.index)
        }
        val textStyle = MaterialTheme.typography.display3
        val optionColor = MaterialTheme.colors.secondary
        val focusRequesterConfirmButton = remember { FocusRequester() }

        val yearString = stringResource(R.string.horologist_picker_year)
        val monthString = stringResource(R.string.horologist_picker_month)
        val dayString = stringResource(R.string.horologist_picker_day)

        LaunchedEffect(
            datePickerState.yearState.selectedOption,
            datePickerState.monthState.selectedOption
        ) {
            if (datePickerState.numOfMonths != datePickerState.monthState.numberOfOptions) {
                datePickerState.monthState.numberOfOptions = datePickerState.numOfMonths
            }
            if (datePickerState.numOfDays != datePickerState.dayState.numberOfOptions) {
                if (datePickerState.dayState.selectedOption >= datePickerState.numOfDays) {
                    datePickerState.dayState.animateScrollToOption(datePickerState.numOfDays - 1)
                }
                datePickerState.dayState.numberOfOptions = datePickerState.numOfDays
            }
        }
        val shortMonthNames = remember { getMonthNames("MMM") }
        val fullMonthNames = remember { getMonthNames("MMMM") }
        val yearContentDescription by remember(
            pickerGroupState.selectedIndex,
            datePickerState.currentYear()
        ) {
            derivedStateOf {
                createDescriptionDatePicker(
                    pickerGroupState,
                    datePickerState.currentYear(),
                    yearString
                )
            }
        }
        val monthContentDescription by remember(
            pickerGroupState.selectedIndex,
            datePickerState.currentMonth()
        ) {
            derivedStateOf {
                if (pickerGroupState.selectedIndex == FocusableElementDatePicker.NONE.index) {
                    monthString
                } else {
                    fullMonthNames[(datePickerState.currentMonth() - 1) % 12]
                }
            }
        }
        val dayContentDescription by remember(
            pickerGroupState.selectedIndex,
            datePickerState.currentDay()
        ) {
            derivedStateOf {
                createDescriptionDatePicker(
                    pickerGroupState,
                    datePickerState.currentDay(),
                    dayString
                )
            }
        }

        BoxWithConstraints(
            modifier = modifier.fillMaxSize().alpha(fullyDrawn.value)
        ) {
            val boxConstraints = this
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(Modifier.height(16.dp))
                Text(
                    text = when (FocusableElementDatePicker[pickerGroupState.selectedIndex]) {
                        FocusableElementDatePicker.DAY -> dayString
                        FocusableElementDatePicker.MONTH -> monthString
                        FocusableElementDatePicker.YEAR -> yearString
                        else -> ""
                    },
                    color = optionColor,
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
                val yearWidth = 100.dp
                val onPickerSelected = { current: FocusableElementDatePicker, next: FocusableElementDatePicker ->
                    if (pickerGroupState.selectedIndex != current.index) {
                        pickerGroupState.selectedIndex = current.index
                    } else {
                        pickerGroupState.selectedIndex = next.index
                        if (next == FocusableElementDatePicker.CONFIRM_BUTTON) {
                            focusRequesterConfirmButton.requestFocus()
                        }
                    }
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .offset(
                            getPickerGroupRowOffset(
                                boxConstraints.maxWidth,
                                dayWidth,
                                monthWidth,
                                yearWidth,
                                spacerWidth,
                                touchExplorationServicesEnabled,
                                pickerGroupState
                            )
                        ),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    PickerGroup(
                        pickerGroupItemWithRSB(
                            pickerState = datePickerState.dayState,
                            modifier = Modifier.size(dayWidth, 100.dp),
                            onSelected = {
                                onPickerSelected(
                                    FocusableElementDatePicker.DAY,
                                    FocusableElementDatePicker.MONTH
                                )
                            },
                            contentDescription = dayContentDescription,
                            option = pickerTextOption(textStyle) {
                                "%d".format(datePickerState.currentDay(it))
                            }
                        ),
                        pickerGroupItemWithRSB(
                            pickerState = datePickerState.monthState,
                            modifier = Modifier.size(monthWidth, 100.dp),
                            onSelected = {
                                onPickerSelected(
                                    FocusableElementDatePicker.MONTH,
                                    FocusableElementDatePicker.YEAR
                                )
                            },
                            contentDescription = monthContentDescription,
                            option = pickerTextOption(textStyle) {
                                shortMonthNames[(datePickerState.currentMonth(it) - 1) % 12]
                            }
                        ),
                        pickerGroupItemWithRSB(
                            pickerState = datePickerState.yearState,
                            modifier = Modifier.size(yearWidth, 100.dp),
                            onSelected = {
                                onPickerSelected(
                                    FocusableElementDatePicker.YEAR,
                                    FocusableElementDatePicker.CONFIRM_BUTTON
                                )
                            },
                            contentDescription = yearContentDescription,
                            option = pickerTextOption(textStyle) {
                                "%4d".format(datePickerState.currentYear(it))
                            }
                        ),
                        pickerGroupState = pickerGroupState,
                        autoCenter = true,
                        separator = { Spacer(modifier = Modifier.width(spacerWidth)) },
                        touchExplorationStateProvider = touchExplorationStateProvider
                    )
                }
                Spacer(
                    Modifier
                        .fillMaxWidth()
                        .weight(weightsToCenterVertically)
                )
                Button(
                    onClick = {
                        if (pickerGroupState.selectedIndex >= 2) {
                            val confirmedYear: Int = datePickerState.currentYear()
                            val confirmedMonth: Int = datePickerState.currentMonth()
                            val confirmedDay: Int = datePickerState.currentDay()
                            val confirmedDate =
                                LocalDate.of(confirmedYear, confirmedMonth, confirmedDay)
                            onDateConfirm(confirmedDate)
                        } else if (pickerGroupState.selectedIndex == FocusableElementDatePicker.DAY.index) {
                            onPickerSelected(
                                FocusableElementDatePicker.DAY,
                                FocusableElementDatePicker.MONTH
                            )
                        } else if (pickerGroupState.selectedIndex == FocusableElementDatePicker.MONTH.index) {
                            onPickerSelected(
                                FocusableElementDatePicker.MONTH,
                                FocusableElementDatePicker.YEAR
                            )
                        }
                    },
                    modifier = Modifier
                        .semantics {
                            focused = pickerGroupState.selectedIndex ==
                                FocusableElementDatePicker.CONFIRM_BUTTON.index
                        }
                        .focusRequester(focusRequesterConfirmButton)
                        .focusable()
                ) {
                    Icon(
                        imageVector =
                        if (pickerGroupState.selectedIndex < 2) Icons.Filled.ChevronRight
                        else Icons.Filled.Check,
                        contentDescription =
                        if (pickerGroupState.selectedIndex >= 2) {
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
        }
    }

    LaunchedEffect(Unit) {
        fullyDrawn.animateTo(1f)
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

private fun getPickerGroupRowOffset(
    rowWidth: Dp,
    dayPickerWidth: Dp,
    monthPickerWidth: Dp,
    yearPickerWidth: Dp,
    spacerWidth: Dp,
    touchExplorationServicesEnabled: Boolean,
    pickerGroupState: PickerGroupState
): Dp {
    val currentOffset = (
        rowWidth -
            (dayPickerWidth + monthPickerWidth + yearPickerWidth + spacerWidth.times(2))
        ) / 2

    return if (touchExplorationServicesEnabled &&
        pickerGroupState.selectedIndex < 0
    ) {
        ((rowWidth - dayPickerWidth) / 2) - currentOffset
    } else if (touchExplorationServicesEnabled &&
        pickerGroupState.selectedIndex > 2
    ) {
        ((rowWidth - yearPickerWidth) / 2) -
            (dayPickerWidth + monthPickerWidth + spacerWidth.times(2) + currentOffset)
    } else {
        0.dp
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
    pickerGroupState: PickerGroupState,
    selectedValue: Int,
    label: String
): String {
    return when (pickerGroupState.selectedIndex) {
        FocusableElementDatePicker.NONE.index -> label
        else -> "$label, $selectedValue"
    }
}

private enum class FocusableElementDatePicker(val index: Int) {
    DAY(0),
    MONTH(1),
    YEAR(2),
    CONFIRM_BUTTON(3),
    NONE(-1);

    companion object {
        private val map = FocusableElementDatePicker.values().associateBy { it.index }
        operator fun get(value: Int) = map[value]
    }
}
