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
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.PickerState
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.rememberPickerState
import java.text.SimpleDateFormat
import java.util.Calendar

@ExperimentalComposablesApi
@Composable
public fun DatePicker(
    check: @Composable () -> Unit,
    @Suppress("UNUSED_PARAMETER") onClick: () -> Unit
) {
    // Omit scaling according to Settings > Display > Font size for this screen
    val typography = MaterialTheme.typography.copy(
        display2 = MaterialTheme.typography.display2.copy(
            fontSize = with(LocalDensity.current) { 34.dp.toSp() }
        )
    )
    MaterialTheme(typography = typography) {
        val today = remember { Calendar.getInstance() }
        val yearState = rememberPickerState(
            initialNumberOfOptions = 3000,
            initiallySelectedOption = today.get(Calendar.YEAR) - 1
        )
        val monthState = rememberPickerState(
            initialNumberOfOptions = 12,
            initiallySelectedOption = today.get(Calendar.MONTH)
        )
        val monthCalendar = remember { Calendar.getInstance() }
        val maxDayInMonth by remember {
            derivedStateOf {
                monthCalendar.set(yearState.selectedOption + 1, monthState.selectedOption, 1)
                val max = monthCalendar.getActualMaximum(Calendar.DAY_OF_MONTH)
                max
            }
        }
        val dayState = rememberPickerState(
            initialNumberOfOptions = maxDayInMonth,
            initiallySelectedOption = today.get(Calendar.DAY_OF_MONTH) - 1
        )
        val focusRequester1 = remember { FocusRequester() }
        val focusRequester2 = remember { FocusRequester() }
        val focusRequester3 = remember { FocusRequester() }

        LaunchedEffect(maxDayInMonth) {
            if (maxDayInMonth != dayState.numberOfOptions) {
                dayState.numberOfOptions = maxDayInMonth
            }
        }
        val monthNames = remember {
            val months = 0..11
            months.map {
                // Translate month index into 3-character month string e.g. Jan.
                // Using deprecated Date constructor rather than LocalDate in order to avoid
                // requirement for API 26+.
                val calendar = Calendar.getInstance()
                calendar.set(2022, it, 1)
                SimpleDateFormat("MMM").format(calendar.time)
            }
        }
        var selectedColumn by remember { mutableStateOf(0) }
        BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
            val boxConstraints = this
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(Modifier.height(16.dp))
                Text(
                    text = when (selectedColumn) {
                        0 -> "Day"
                        1 -> "Month"
                        else -> "Year"
                    },
                    color = MaterialTheme.colors.secondary,
                    style = MaterialTheme.typography.button,
                    maxLines = 1,
                )
                val weightsToCenterVertically = 0.5f
                Spacer(Modifier.fillMaxWidth().weight(weightsToCenterVertically))
                val spacerWidth = 8.dp
                val dayWidth = 54.dp
                val monthWidth = 80.dp
                val yearWidth = 128.dp
                val offset = when (selectedColumn) {
                    0 -> (boxConstraints.maxWidth - dayWidth) / 2
                    1 -> (boxConstraints.maxWidth - monthWidth) / 2 - dayWidth - spacerWidth
                    else -> (boxConstraints.maxWidth - yearWidth) / 2 - monthWidth
                }
                Row(modifier = Modifier.fillMaxWidth().offset(offset)) {
                    if (selectedColumn < 2) {
                        DatePickerImpl(
                            state = dayState,
                            readOnly = selectedColumn != 0,
                            onSelected = { selectedColumn = 0 },
                            text = { day: Int -> "%d".format(day + 1) },
                            width = dayWidth,
                            focusRequester = focusRequester1
                        )
                        Spacer(modifier = Modifier.width(spacerWidth))
                    }
                    DatePickerImpl(
                        state = monthState,
                        readOnly = selectedColumn != 1,
                        onSelected = { selectedColumn = 1 },
                        text = { month: Int -> monthNames[month] },
                        width = monthWidth,
                        focusRequester = focusRequester2
                    )
                    if (selectedColumn > 0) {
                        Spacer(modifier = Modifier.width(spacerWidth))
                        DatePickerImpl(
                            state = yearState,
                            readOnly = selectedColumn != 2,
                            onSelected = { selectedColumn = 2 },
                            text = { year: Int -> "%4d".format(year + 1) },
                            width = yearWidth,
                            focusRequester = focusRequester3
                        )
                    }
                }
                Spacer(Modifier.fillMaxWidth().weight(weightsToCenterVertically))
                Button(
                    onClick = onClick,
                ) {
                    check()
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
    width: Dp
) {
    PickerWithRSB(
        readOnly = readOnly,
        state = state,
        focusRequester = focusRequester,
        modifier = Modifier.size(width, 100.dp),
    ) { option ->
        TimePiece(
            selected = !readOnly,
            onSelected = onSelected,
            text = text(option),
            style = MaterialTheme.typography.display2,
        )
    }
}
