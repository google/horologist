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
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.focused
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.PickerGroup
import androidx.wear.compose.material.PickerGroupState
import androidx.wear.compose.material.PickerState
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.rememberPickerGroupState
import com.google.android.horologist.compose.layout.FontScaleIndependent
import com.google.android.horologist.compose.layout.ScreenScaffold
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
    toDate: LocalDate? = null,
) {
    val inspectionMode = LocalInspectionMode.current
    val fullyDrawn = remember { Animatable(if (inspectionMode) 1f else 0f) }

    if (fromDate != null && toDate != null) {
        verifyDates(date, fromDate, toDate)
    }

    val datePickerState = remember(date) {
        DatePickerState(date, fromDate, toDate)
    }

    val touchExplorationStateProvider = remember { DefaultTouchExplorationStateProvider() }
    val touchExplorationServicesEnabled by touchExplorationStateProvider
        .touchExplorationState()

    // When the time picker loads, none of the individual pickers are selected in talkback mode,
    // otherwise day picker should be focused.
    val pickerGroupState = if (touchExplorationServicesEnabled) {
        rememberPickerGroupState(FocusableElementDatePicker.NONE.index)
    } else {
        rememberPickerGroupState(FocusableElementDatePicker.DAY.index)
    }

    val isLargeScreen = LocalConfiguration.current.screenWidthDp > 225
    val textStyle = if (isLargeScreen) {
        MaterialTheme.typography.display2
    } else {
        MaterialTheme.typography.display3
    }

    val optionColor = MaterialTheme.colors.secondary
    val focusRequesterConfirmButton = remember { FocusRequester() }

    val yearString = stringResource(R.string.horologist_picker_year)
    val monthString = stringResource(R.string.horologist_picker_month)
    val dayString = stringResource(R.string.horologist_picker_day)

    LaunchedEffect(
        datePickerState.yearState.selectedOption,
        datePickerState.monthState.selectedOption,
    ) {
        if (datePickerState.maxDaysInMonth != datePickerState.dayState.numberOfOptions) {
            if (datePickerState.dayState.selectedOption >= datePickerState.maxDaysInMonth) {
                datePickerState.dayState.animateScrollToOption(datePickerState.maxDaysInMonth - 1)
            }
            datePickerState.dayState.numberOfOptions = datePickerState.maxDaysInMonth
        }
    }
    val shortMonthNames = remember { getMonthNames("MMM") }
    val fullMonthNames = remember { getMonthNames("MMMM") }
    val yearContentDescription by remember(
        pickerGroupState.selectedIndex,
        datePickerState.currentYear(),
    ) {
        derivedStateOf {
            createDescriptionDatePicker(
                pickerGroupState,
                datePickerState.currentYear(),
                yearString,
            )
        }
    }
    val monthContentDescription by remember(
        pickerGroupState.selectedIndex,
        datePickerState.currentMonth(),
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
        datePickerState.currentDay(),
    ) {
        derivedStateOf {
            createDescriptionDatePicker(
                pickerGroupState,
                datePickerState.currentDay(),
                dayString,
            )
        }
    }
    val onPickerSelected =
        { current: FocusableElementDatePicker, next: FocusableElementDatePicker ->
            if (pickerGroupState.selectedIndex != current.index) {
                pickerGroupState.selectedIndex = current.index
            } else {
                pickerGroupState.selectedIndex = next.index
                if (next == FocusableElementDatePicker.CONFIRM_BUTTON) {
                    focusRequesterConfirmButton.requestFocus()
                }
            }
        }

    ScreenScaffold(
        modifier = modifier
            .fillMaxSize()
            .alpha(fullyDrawn.value),
        timeText = {},
    ) {
        BoxWithConstraints(
            modifier = modifier
                .fillMaxSize(),
        ) {
            val boxConstraints = this
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Spacer(Modifier.height(14.dp))
                Text(
                    text = when (FocusableElementDatePicker[pickerGroupState.selectedIndex]) {
                        FocusableElementDatePicker.DAY -> dayString
                        FocusableElementDatePicker.MONTH -> monthString
                        FocusableElementDatePicker.YEAR -> yearString
                        else -> ""
                    },
                    color = optionColor,
                    style = MaterialTheme.typography.button,
                    maxLines = 1,
                )
                Spacer(Modifier.height(4.dp))
                val spacerWidth = if (isLargeScreen) 6.dp else 2.dp
                val textPadding = 6.dp // Update if UX decides a different value should be used.

                FontScaleIndependent {
                    val measurer = rememberTextMeasurer()
                    val density = LocalDensity.current
                    val (digitWidth, maxMonthWidth) = remember(
                        density.density,
                        LocalConfiguration.current.screenWidthDp,
                    ) {
                        val mm = measurer.measure(
                            "0123456789\n" + shortMonthNames.joinToString("\n"),
                            style = textStyle,
                            density = density,
                        )

                        ((0..9).maxOf { mm.getBoundingBox(it).width }) to
                            ((1..12).maxOf { mm.getLineRight(it) - mm.getLineLeft(it) })
                    }

                    // Add spaces on to allow room to grow
                    val dayWidth =
                        with(LocalDensity.current) { (digitWidth * 2).toDp() } + spacerWidth + textPadding
                    val monthWidth =
                        with(LocalDensity.current) { maxMonthWidth.toDp() } + spacerWidth + textPadding
                    val yearWidth =
                        with(LocalDensity.current) { (digitWidth * 4).toDp() } + spacerWidth + textPadding
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f) // Take remaining vertical space.
                            .offset(
                                getPickerGroupRowOffset(
                                    boxConstraints.maxWidth,
                                    dayWidth,
                                    monthWidth,
                                    yearWidth,
                                    touchExplorationServicesEnabled,
                                    pickerGroupState,
                                ),
                            ),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                    ) {
                        PickerGroup(
                            pickerGroupItemWithRSB(
                                pickerState = datePickerState.dayState,
                                modifier = Modifier
                                    .width(dayWidth)
                                    .fillMaxHeight(),
                                onSelected = {
                                    onPickerSelected(
                                        FocusableElementDatePicker.DAY,
                                        FocusableElementDatePicker.MONTH,
                                    )
                                },
                                contentDescription = dayContentDescription,
                                option = pickerTextOption(
                                    textStyle = textStyle,
                                    indexToText = {
                                        "%d".format(datePickerState.currentDay(it))
                                    },
                                    isValid = {
                                        datePickerState.isDayValid(datePickerState.currentDay(it))
                                    },
                                ),
                            ),
                            pickerGroupItemWithRSB(
                                pickerState = datePickerState.monthState,
                                modifier = Modifier
                                    .width(monthWidth)
                                    .fillMaxHeight(),
                                onSelected = {
                                    onPickerSelected(
                                        FocusableElementDatePicker.MONTH,
                                        FocusableElementDatePicker.YEAR,
                                    )
                                },
                                contentDescription = monthContentDescription,
                                option = pickerTextOption(
                                    textStyle = textStyle,
                                    indexToText = {
                                        shortMonthNames[(datePickerState.currentMonth(it) - 1) % 12]
                                    },
                                    isValid = {
                                        datePickerState.isMonthValid(datePickerState.currentMonth(it))
                                    },
                                ),
                            ),
                            pickerGroupItemWithRSB(
                                pickerState = datePickerState.yearState,
                                modifier = Modifier
                                    .width(yearWidth)
                                    .fillMaxHeight(),
                                onSelected = {
                                    onPickerSelected(
                                        FocusableElementDatePicker.YEAR,
                                        FocusableElementDatePicker.CONFIRM_BUTTON,
                                    )
                                },
                                contentDescription = yearContentDescription,
                                option = pickerTextOption(
                                    textStyle = textStyle,
                                    indexToText = {
                                        "%4d".format(datePickerState.currentYear(it))
                                    },
                                    isValid = {
                                        datePickerState.isYearValid(datePickerState.currentYear(it))
                                    },
                                ),
                            ),
                            pickerGroupState = pickerGroupState,
                            autoCenter = true,
                            separator = { },
                            touchExplorationStateProvider = touchExplorationStateProvider,
                        )
                    }
                }
                Spacer(Modifier.height(4.dp))
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
                                FocusableElementDatePicker.MONTH,
                            )
                        } else if (pickerGroupState.selectedIndex == FocusableElementDatePicker.MONTH.index) {
                            onPickerSelected(
                                FocusableElementDatePicker.MONTH,
                                FocusableElementDatePicker.YEAR,
                            )
                        } else {
                            onPickerSelected(
                                FocusableElementDatePicker.NONE,
                                FocusableElementDatePicker.DAY,
                            )
                        }
                    },
                    modifier = Modifier
                        .semantics {
                            focused = pickerGroupState.selectedIndex ==
                                FocusableElementDatePicker.CONFIRM_BUTTON.index
                        }
                        .focusRequester(focusRequesterConfirmButton)
                        .focusable(),
                    enabled = if (pickerGroupState.selectedIndex >= 2) {
                        datePickerState.isDayValid()
                    } else {
                        true
                    },
                ) {
                    Icon(
                        imageVector =
                        if (pickerGroupState.selectedIndex < 2) {
                            Icons.Filled.ChevronRight
                        } else {
                            Icons.Filled.Check
                        },
                        contentDescription =
                        if (pickerGroupState.selectedIndex >= 2) {
                            stringResource(R.string.horologist_picker_confirm_button_content_description)
                        } else {
                            stringResource(R.string.horologist_picker_next_button_content_description)
                        },
                        modifier = Modifier
                            .size(24.dp)
                            .wrapContentSize(align = Alignment.Center),
                    )
                }
                Spacer(Modifier.height(8.dp))
            }
        }
    }

    if (!inspectionMode) {
        LaunchedEffect(Unit) {
            fullyDrawn.animateTo(1f)
        }
    }
}

private fun verifyDates(
    date: LocalDate,
    fromDate: LocalDate,
    toDate: LocalDate,
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
    touchExplorationServicesEnabled: Boolean,
    pickerGroupState: PickerGroupState,
): Dp {
    val currentOffset = (rowWidth - (dayPickerWidth + monthPickerWidth + yearPickerWidth)) / 2

    return if (touchExplorationServicesEnabled &&
        pickerGroupState.selectedIndex < 0
    ) {
        ((rowWidth - dayPickerWidth) / 2) - currentOffset
    } else if (touchExplorationServicesEnabled &&
        pickerGroupState.selectedIndex > 2
    ) {
        ((rowWidth - yearPickerWidth) / 2) - (dayPickerWidth + monthPickerWidth + currentOffset)
    } else {
        0.dp
    }
}

internal class DatePickerState constructor(
    private val date: LocalDate,
    private val fromDate: LocalDate?,
    private val toDate: LocalDate?,
) {
    // Year range 1900 - 2100 was suggested in b/277885199
    private val startYear = if (fromDate != null && fromDate.year < 1900) {
        fromDate.year
    } else {
        1900
    }
    private val numOfYears = if (toDate != null && (toDate.year - startYear) > 200) {
        toDate.year - startYear + 1
    } else {
        201
    }
    val yearState =
        PickerState(
            initialNumberOfOptions = 201,
            initiallySelectedOption = date.year - startYear,
        )

    val selectedYearEqualsFromYear: Boolean
        get() = fromDate?.year == currentYear()

    val selectedYearEqualsToYear: Boolean
        get() = toDate?.year == currentYear()

    fun isYearValid(year: Int = currentYear()): Boolean {
        return (
            if (fromDate?.year != null) {
                fromDate.year <= year
            } else {
                true
            }
            ) && (
            if (toDate?.year != null) {
                year <= toDate.year
            } else {
                true
            }
            )
    }

    val monthState =
        PickerState(
            initialNumberOfOptions = 12,
            initiallySelectedOption = date.monthValue - 1,
        )

    val selectedMonthEqualsFromMonth: Boolean
        get() = selectedYearEqualsFromYear && fromDate?.monthValue == currentMonth()

    val selectedMonthEqualsToMonth: Boolean
        get() = selectedYearEqualsToYear && toDate?.monthValue == currentMonth()

    fun isMonthValid(month: Int = currentMonth()): Boolean {
        return isYearValid() &&
            (
                if (fromDate != null && selectedYearEqualsFromYear) {
                    fromDate.monthValue <= month
                } else {
                    true
                }
                ) && (
            if (toDate != null && selectedYearEqualsToYear) {
                month <= toDate.monthValue
            } else {
                true
            }
            )
    }

    private val firstDayOfMonth: LocalDate
        get() = LocalDate.of(
            currentYear(),
            currentMonth(),
            1,
        )
    val maxDaysInMonth: Int
        get() = firstDayOfMonth.with(TemporalAdjusters.lastDayOfMonth()).dayOfMonth

    val dayState =
        PickerState(
            initialNumberOfOptions = maxDaysInMonth,
            initiallySelectedOption = date.dayOfMonth - 1,
        )

    fun isDayValid(day: Int = currentDay()): Boolean {
        return isMonthValid() &&
            (
                if (fromDate != null && selectedMonthEqualsFromMonth) {
                    fromDate.dayOfMonth <= day
                } else {
                    true
                }
                ) && (
            if (toDate != null && selectedMonthEqualsToMonth) {
                day <= toDate.dayOfMonth
            } else {
                true
            }
            )
    }

    fun currentYear(year: Int = yearState.selectedOption): Int {
        return year + startYear
    }

    fun currentMonth(month: Int = monthState.selectedOption): Int {
        return month + 1
    }

    fun currentDay(day: Int = dayState.selectedOption): Int {
        return day + 1
    }
}

private fun createDescriptionDatePicker(
    pickerGroupState: PickerGroupState,
    selectedValue: Int,
    label: String,
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
    NONE(-1),
    ;

    companion object {
        private val map = entries.associateBy { it.index }
        operator fun get(value: Int) = map[value]
    }
}
