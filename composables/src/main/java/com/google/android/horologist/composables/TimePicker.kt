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

import android.view.MotionEvent
import androidx.compose.foundation.focusable
import androidx.compose.foundation.gestures.FlingBehavior
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.ChipDefaults
import androidx.wear.compose.material.CompactChip
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Picker
import androidx.wear.compose.material.PickerDefaults
import androidx.wear.compose.material.PickerScope
import androidx.wear.compose.material.PickerState
import androidx.wear.compose.material.Text
import com.google.android.horologist.compose.rotaryinput.onRotaryInputAccumulated
import kotlinx.coroutines.launch
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoField

/**
 * A full screen TimePicker with hours, minutes and seconds.
 *
 * This component is designed to take most/all of the screen and utilizes large fonts. In order to
 * ensure that it will draw properly on smaller screens it does not take account of user font size
 * overrides for MaterialTheme.typography.display3 which is used to display the main picker
 * value.
 *
 * @param onTimeConfirm the button event handler.
 * @param modifier the modifiers for the `Box` containing the UI elements.
 * @param time the initial value to seed the picker with.
 * @param showSeconds flag to indicate whether to show seconds as well as hours and minutes. If true
 * then the user will be able to select seconds as well as hours and minutes. If false then no
 * seconds picker will be shown and the seconds will be set to 0 in the time returned in
 * onTimeConfirm.
 */
@Composable
public fun TimePicker(
    onTimeConfirm: (LocalTime) -> Unit,
    modifier: Modifier = Modifier,
    time: LocalTime = LocalTime.now(),
    showSeconds: Boolean = true
) {
    // Omit scaling according to Settings > Display > Font size for this screen
    val typography = MaterialTheme.typography.copy(
        display3 = MaterialTheme.typography.display3.copy(
            fontSize = with(LocalDensity.current) { 30.dp.toSp() }
        )
    )
    val hourState = rememberPickerState(
        initialNumberOfOptions = 24,
        initiallySelectedOption = time.hour
    )
    val minuteState = rememberPickerState(
        initialNumberOfOptions = 60,
        initiallySelectedOption = time.minute
    )
    val secondsState = rememberPickerState(
        initialNumberOfOptions = 60,
        initiallySelectedOption = time.second
    )
    MaterialTheme(typography = typography) {
        var selectedColumn by remember { mutableStateOf(0) }
        val textStyle = MaterialTheme.typography.display3
        val optionColor = MaterialTheme.colors.secondary
        val focusRequester1 = remember { FocusRequester() }
        val focusRequester2 = remember { FocusRequester() }
        val focusRequester3 = remember { FocusRequester() }
        Box(modifier = modifier.fillMaxSize()) {
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(Modifier.height(12.dp))
                Text(
                    text = when (selectedColumn) {
                        0 -> stringResource(R.string.horologist_time_picker_hour)
                        1 -> stringResource(R.string.horologist_time_picker_minute)
                        else -> stringResource(R.string.horologist_time_picker_second)
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
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    PickerWithRSB(
                        readOnly = selectedColumn != 0,
                        state = hourState,
                        focusRequester = focusRequester1,
                        modifier = Modifier.size(40.dp, 100.dp),
                        contentDescription = "%02d".format(hourState.selectedOption),
                        onSelected = { selectedColumn = 0 }
                    ) { hour: Int ->
                        val hourString = "%02d".format(hour)
                        TimePiece(
                            selected = selectedColumn == 0,
                            text = hourString,
                            style = textStyle,
                            onSelected = { selectedColumn = 0 }
                        )
                    }
                    Separator(6.dp, textStyle)
                    PickerWithRSB(
                        readOnly = selectedColumn != 1,
                        state = minuteState,
                        focusRequester = focusRequester2,
                        modifier = Modifier.size(40.dp, 100.dp),
                        contentDescription = "%02d".format(minuteState.selectedOption),
                        onSelected = { selectedColumn = 1 }
                    ) { minute: Int ->
                        TimePiece(
                            selected = selectedColumn == 1,
                            text = "%02d".format(minute),
                            style = textStyle,
                            onSelected = { selectedColumn = 1 }
                        )
                    }
                    if (showSeconds) {
                        Separator(6.dp, textStyle)
                        PickerWithRSB(
                            readOnly = selectedColumn != 2,
                            state = secondsState,
                            focusRequester = focusRequester3,
                            modifier = Modifier.size(40.dp, 100.dp),
                            contentDescription = "%02d".format(secondsState.selectedOption),
                            onSelected = { selectedColumn = 2 }
                        ) { second: Int ->
                            TimePiece(
                                selected = selectedColumn == 2,
                                text = "%02d".format(second),
                                style = textStyle,
                                onSelected = { selectedColumn = 2 }
                            )
                        }
                    }
                }
                Spacer(
                    Modifier
                        .fillMaxWidth()
                        .weight(weightsToCenterVertically)
                )
                Button(onClick = {
                    val seconds = if (showSeconds) secondsState.selectedOption else 0
                    val confirmedTime = LocalTime.of(
                        hourState.selectedOption,
                        minuteState.selectedOption,
                        seconds
                    )
                    onTimeConfirm(confirmedTime)
                }) {
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
                if (showSeconds) {
                    listOf(focusRequester1, focusRequester2, focusRequester3)[selectedColumn]
                        .requestFocus()
                } else {
                    listOf(focusRequester1, focusRequester2)[selectedColumn]
                        .requestFocus()
                }
            }
        }
    }
}

/**
 * A full screen TimePicker with hours and minutes and AM/PM selector.
 *
 * This component is designed to take most/all of the screen and utilizes large fonts. In order to
 * ensure that it will draw properly on smaller screens it does not take account of user font size
 * overrides for MaterialTheme.typography.display1 which is used to display the main picker
 * value.
 *
 * @param onTimeConfirm the button event handler.
 * @param modifier the modifiers for the `Column` containing the UI elements.
 * @param time the initial value to seed the picker with.
 */
@Composable
public fun TimePickerWith12HourClock(
    onTimeConfirm: (LocalTime) -> Unit,
    modifier: Modifier = Modifier,
    time: LocalTime = LocalTime.now()
) {
    // Omit scaling according to Settings > Display > Font size for this screen,
    val typography = MaterialTheme.typography.copy(
        display1 = MaterialTheme.typography.display1.copy(
            fontSize = with(LocalDensity.current) { 40.dp.toSp() }
        )
    )
    val hourState = rememberPickerState(
        initialNumberOfOptions = 12,
        initiallySelectedOption = time[ChronoField.CLOCK_HOUR_OF_AMPM] - 1
    )
    val minuteState = rememberPickerState(
        initialNumberOfOptions = 60,
        initiallySelectedOption = time.minute
    )
    var amPm by remember { mutableStateOf(time[ChronoField.AMPM_OF_DAY]) }
    // TODO check the results of talkback with these internally localised values
    // move to stringResources() otherwise.
    val amString = remember {
        LocalTime.of(6, 0).format(DateTimeFormatter.ofPattern("a"))
    }
    val pmString = remember {
        LocalTime.of(18, 0).format(DateTimeFormatter.ofPattern("a"))
    }
    MaterialTheme(typography = typography) {
        var selectedColumn by remember { mutableStateOf(0) }
        val textStyle = MaterialTheme.typography.display1
        val focusRequester1 = remember { FocusRequester() }
        val focusRequester2 = remember { FocusRequester() }

        Column(
            modifier = modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(8.dp))
            CompactChip(
                onClick = { amPm = 1 - amPm },
                modifier = Modifier.size(width = 50.dp, height = 40.dp),
                label = {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = if (amPm == 0) amString else pmString,
                            color = MaterialTheme.colors.onPrimary,
                            style = MaterialTheme.typography.button
                        )
                    }
                },
                colors = ChipDefaults.chipColors(backgroundColor = MaterialTheme.colors.secondary),
                contentPadding = PaddingValues(vertical = 0.dp)
            )
            Spacer(
                Modifier
                    .fillMaxWidth()
                    .weight(0.5f)
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Spacer(Modifier.width(8.dp))
                PickerWithRSB(
                    readOnly = selectedColumn != 0,
                    state = hourState,
                    focusRequester = focusRequester1,
                    modifier = Modifier.size(64.dp, 100.dp),
                    readOnlyLabel = { LabelText(stringResource(R.string.horologist_time_picker_hour)) },
                    contentDescription = "%02d".format(hourState.selectedOption + 1),
                    onSelected = { selectedColumn = 0 }
                ) { hour: Int ->
                    TimePiece(
                        selected = selectedColumn == 0,
                        text = "%02d".format(hour + 1),
                        style = textStyle,
                        onSelected = { selectedColumn = 0 }
                    )
                }
                Separator(8.dp, textStyle)

                PickerWithRSB(
                    readOnly = selectedColumn != 1,
                    state = minuteState,
                    focusRequester = focusRequester2,
                    modifier = Modifier.size(64.dp, 100.dp),
                    readOnlyLabel = { LabelText(stringResource(R.string.horologist_time_picker_min)) },
                    contentDescription = "%02d".format(minuteState.selectedOption),
                    onSelected = { selectedColumn = 1 }
                ) { minute: Int ->
                    TimePiece(
                        selected = selectedColumn == 1,
                        text = "%02d".format(minute),
                        style = textStyle,
                        onSelected = { selectedColumn = 1 }
                    )
                }
                Spacer(Modifier.width(8.dp))
            }
            Spacer(
                Modifier
                    .fillMaxWidth()
                    .weight(0.5f)
            )
            Button(onClick = {
                val confirmedTime = LocalTime.of(
                    hourState.selectedOption + 1,
                    minuteState.selectedOption,
                    0
                ).with(ChronoField.AMPM_OF_DAY, amPm.toLong())
                onTimeConfirm(confirmedTime)
            }) {
                Icon(
                    imageVector = Icons.Filled.Check,
                    contentDescription = stringResource(id = R.string.horologist_picker_confirm_button_content_description),
                    modifier = Modifier
                        .size(24.dp)
                        .wrapContentSize(align = Alignment.Center)
                )
            }
            Spacer(Modifier.height(8.dp))
            LaunchedEffect(selectedColumn) {
                listOf(focusRequester1, focusRequester2)[selectedColumn].requestFocus()
            }
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
internal fun TimePiece(
    selected: Boolean,
    onSelected: () -> Unit,
    text: String,
    style: TextStyle
) {
    Box(modifier = Modifier.fillMaxSize()) {
        val modifier = Modifier
            .align(Alignment.Center)
            .wrapContentSize()
        Text(
            text = text,
            maxLines = 1,
            style = style,
            color = if (selected) MaterialTheme.colors.secondary
            else MaterialTheme.colors.onBackground,
            modifier = if (selected) {
                modifier
            } else {
                modifier.pointerInteropFilter {
                    if (it.action == MotionEvent.ACTION_DOWN) onSelected()
                    true
                }
            }
        )
    }
}

@Composable
private fun BoxScope.LabelText(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.caption1,
        color = MaterialTheme.colors.onSurfaceVariant,
        modifier = Modifier
            .align(Alignment.TopCenter)
            .offset(y = 8.dp)
    )
}

@Composable
private fun Separator(width: Dp, textStyle: TextStyle) {
    Spacer(Modifier.width(width))
    Text(
        text = ":",
        style = textStyle,
        color = MaterialTheme.colors.onBackground
    )
    Spacer(Modifier.width(width))
}

@Composable
internal fun PickerWithRSB(
    state: PickerState,
    readOnly: Boolean,
    modifier: Modifier,
    focusRequester: FocusRequester,
    contentDescription: String?,
    readOnlyLabel: @Composable (BoxScope.() -> Unit)? = null,
    flingBehavior: FlingBehavior = PickerDefaults.flingBehavior(state = state),
    onSelected: () -> Unit = {},
    option: @Composable PickerScope.(optionIndex: Int) -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    Picker(
        state = state,
        contentDescription = contentDescription,
        onSelected = onSelected,
        modifier = modifier
            .onRotaryInputAccumulated {
                coroutineScope.launch {
                    if (it > 0) {
                        state.scrollToOption(state.selectedOption + 1)
                    } else {
                        state.scrollToOption(state.selectedOption - 1)
                    }
                }
            }
            .focusRequester(focusRequester)
            .focusable(),
        flingBehavior = flingBehavior,
        readOnly = readOnly,
        readOnlyLabel = readOnlyLabel,
        option = option
    )
}
