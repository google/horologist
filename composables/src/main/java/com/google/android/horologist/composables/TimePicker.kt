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

import android.content.Context
import android.view.MotionEvent
import android.view.accessibility.AccessibilityManager
import androidx.compose.foundation.focusable
import androidx.compose.foundation.gestures.FlingBehavior
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
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
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.focused
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Picker
import androidx.wear.compose.material.PickerDefaults
import androidx.wear.compose.material.PickerScope
import androidx.wear.compose.material.PickerState
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.rememberPickerState
import com.google.android.horologist.compose.rotaryinput.onRotaryInputAccumulated
import kotlinx.coroutines.launch
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoField

private var touchExplorationEnabled = false
private lateinit var touchExplorationStateChangeListener: AccessibilityManager.TouchExplorationStateChangeListener

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
    val secondState = rememberPickerState(
        initialNumberOfOptions = 60,
        initiallySelectedOption = time.second
    )
    val talkbackEnabled by rememberTalkBackState()

    MaterialTheme(typography = typography) {
        // When the time picker loads, none of the individual pickers are selected in talkback mode,
        // otherwise hours picker should be focused.
        var focusedElement by remember {
            mutableStateOf(
                if (talkbackEnabled) FocusableElement.NONE else FocusableElement.HOURS
            )
        }

        val textStyle = MaterialTheme.typography.display3
        val optionColor = MaterialTheme.colors.secondary
        val focusRequesterHours = remember { FocusRequester() }
        val focusRequesterMinutes = remember { FocusRequester() }
        val focusRequesterSeconds = remember { FocusRequester() }
        val focusRequesterConfirmButton = remember { FocusRequester() }

        val hourString = stringResource(R.string.horologist_time_picker_hour)
        val minuteString = stringResource(R.string.horologist_time_picker_minute)
        val secondString = stringResource(R.string.horologist_time_picker_second)

        val hourContentDescription by remember {
            derivedStateOf {
                createDescription(focusedElement, hourState.selectedOption, hourString)
            }
        }
        val minuteContentDescription by remember {
            derivedStateOf {
                createDescription(focusedElement, minuteState.selectedOption, minuteString)
            }
        }
        val secondContentDescription by remember {
            derivedStateOf {
                createDescription(focusedElement, secondState.selectedOption, secondString)
            }
        }

        Box(
            modifier = modifier
                .fillMaxSize()
                .then(
                    if (talkbackEnabled) {
                        when (focusedElement) {
                            FocusableElement.HOURS -> Modifier.scrollablePicker(hourState)
                            FocusableElement.MINUTES -> Modifier.scrollablePicker(minuteState)
                            FocusableElement.SECONDS -> Modifier.scrollablePicker(secondState)
                            else -> Modifier
                        }
                    } else {
                        Modifier
                    }
                )
        ) {
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(Modifier.height(12.dp))
                Text(
                    text = when (focusedElement) {
                        FocusableElement.HOURS -> hourString
                        FocusableElement.MINUTES -> minuteString
                        FocusableElement.SECONDS -> secondString
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
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    val doubleTapToNext = { position: FocusableElement, next: FocusableElement ->
                        focusedElement = when (focusedElement) {
                            position -> next
                            else -> position
                        }
                    }
                    PickerWithRSB(
                        readOnly = focusedElement != FocusableElement.HOURS,
                        state = hourState,
                        focusRequester = focusRequesterHours,
                        modifier = Modifier.size(40.dp, 100.dp),
                        onSelected = {
                            doubleTapToNext(FocusableElement.HOURS, FocusableElement.MINUTES)
                        },
                        contentDescription = hourContentDescription,
                        userScrollEnabled = !talkbackEnabled ||
                            focusedElement == FocusableElement.HOURS
                    ) { hour: Int ->
                        TimePiece(
                            selected = focusedElement == FocusableElement.HOURS,
                            onSelected = {
                                doubleTapToNext(FocusableElement.HOURS, FocusableElement.MINUTES)
                            },
                            text = "%02d".format(hour),
                            style = textStyle,
                            talkbackEnabled = talkbackEnabled
                        )
                    }
                    Separator(6.dp, textStyle)
                    PickerWithRSB(
                        readOnly = focusedElement != FocusableElement.MINUTES,
                        state = minuteState,
                        focusRequester = focusRequesterMinutes,
                        modifier = Modifier.size(40.dp, 100.dp),
                        onSelected = {
                            doubleTapToNext(FocusableElement.MINUTES, FocusableElement.SECONDS)
                        },
                        contentDescription = minuteContentDescription,
                        userScrollEnabled = !talkbackEnabled ||
                            focusedElement == FocusableElement.MINUTES
                    ) { minute: Int ->
                        TimePiece(
                            selected = focusedElement == FocusableElement.MINUTES,
                            onSelected = {
                                doubleTapToNext(FocusableElement.MINUTES, FocusableElement.SECONDS)
                            },
                            text = "%02d".format(minute),
                            style = textStyle,
                            talkbackEnabled = talkbackEnabled
                        )
                    }
                    if (showSeconds) {
                        Separator(6.dp, textStyle)
                        PickerWithRSB(
                            readOnly = focusedElement != FocusableElement.SECONDS,
                            state = secondState,
                            focusRequester = focusRequesterSeconds,
                            modifier = Modifier.size(40.dp, 100.dp),
                            onSelected = {
                                doubleTapToNext(
                                    FocusableElement.SECONDS,
                                    FocusableElement.CONFIRM_BUTTON
                                )
                            },
                            contentDescription = secondContentDescription,
                            userScrollEnabled = !talkbackEnabled ||
                                focusedElement == FocusableElement.SECONDS
                        ) { second: Int ->
                            TimePiece(
                                selected = focusedElement == FocusableElement.SECONDS,
                                onSelected = {
                                    doubleTapToNext(
                                        FocusableElement.SECONDS,
                                        FocusableElement.CONFIRM_BUTTON
                                    )
                                },
                                text = "%02d".format(second),
                                style = textStyle,
                                talkbackEnabled = talkbackEnabled
                            )
                        }
                    }
                }
                Spacer(
                    Modifier
                        .fillMaxWidth()
                        .weight(weightsToCenterVertically)
                )
                Button(
                    onClick = {
                        val seconds = if (showSeconds) secondState.selectedOption else 0
                        val confirmedTime = LocalTime.of(
                            hourState.selectedOption,
                            minuteState.selectedOption,
                            seconds
                        )
                        onTimeConfirm(confirmedTime)
                    },
                    modifier = Modifier
                        .semantics {
                            focused = focusedElement == FocusableElement.CONFIRM_BUTTON
                        }
                        .focusRequester(focusRequesterConfirmButton)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Check,
                        contentDescription = stringResource(R.string.horologist_picker_confirm_button_content_description),
                        modifier = Modifier
                            .size(24.dp)
                            .wrapContentSize(align = Alignment.Center)
                    )
                }
                Spacer(Modifier.height(12.dp))
            }
            LaunchedEffect(focusedElement) {
                if (focusedElement != FocusableElement.NONE) {
                    listOf(
                        focusRequesterHours,
                        focusRequesterMinutes,
                        focusRequesterSeconds,
                        focusRequesterConfirmButton
                    )[focusedElement.index].requestFocus()
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
    val periodState = rememberPickerState(
        initialNumberOfOptions = 2,
        initiallySelectedOption = time[ChronoField.AMPM_OF_DAY],
        // TODO check the results of talkback with these internally localised values
        // move to stringResources() otherwise.
        repeatItems = false
    )
    val talkbackEnabled by rememberTalkBackState()

    MaterialTheme(typography = typography) {
        var focusedElement by remember {
            mutableStateOf(
                if (talkbackEnabled) FocusableElement12Hour.NONE else FocusableElement12Hour.HOURS
            )
        }
        val textStyle = MaterialTheme.typography.display3
        val focusRequesterHours = remember { FocusRequester() }
        val focusRequesterMinutes = remember { FocusRequester() }
        val focusRequesterPeriod = remember { FocusRequester() }
        val focusRequesterConfirmButton = remember { FocusRequester() }

        val hourString = stringResource(R.string.horologist_time_picker_hour)
        val minuteString = stringResource(R.string.horologist_time_picker_minute)
        val periodString = stringResource(R.string.horologist_time_picker_period)

        val hoursContentDescription by remember {
            derivedStateOf {
                createDescription12Hour(focusedElement, hourState.selectedOption + 1, hourString)
            }
        }
        val minutesContentDescription by remember {
            derivedStateOf {
                createDescription12Hour(focusedElement, minuteState.selectedOption, minuteString)
            }
        }

        val amString = remember {
            LocalTime.of(6, 0).format(DateTimeFormatter.ofPattern("a"))
        }
        val pmString = remember {
            LocalTime.of(18, 0).format(DateTimeFormatter.ofPattern("a"))
        }
        val periodContentDescription by remember {
            derivedStateOf {
                if (focusedElement == FocusableElement12Hour.NONE) {
                    createDescription12Hour(focusedElement, periodState.selectedOption, periodString)
                } else if (periodState.selectedOption == 0) {
                    createDescription12Hour(focusedElement, periodState.selectedOption, amString)
                } else createDescription12Hour(focusedElement, periodState.selectedOption, pmString)
            }
        }
        Box(
            modifier = modifier
                .fillMaxSize()
                .then(
                    if (talkbackEnabled) {
                        when (focusedElement) {
                            FocusableElement12Hour.HOURS -> Modifier.scrollablePicker(hourState)
                            FocusableElement12Hour.MINUTES -> Modifier.scrollablePicker(minuteState)
                            FocusableElement12Hour.PERIOD -> Modifier.scrollablePicker(periodState)
                            else -> Modifier
                        }
                    } else {
                        Modifier
                    }
                )
        ) {
            Column(
                modifier = modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(Modifier.height(12.dp))
                Text(
                    text = when (focusedElement) {
                        FocusableElement12Hour.HOURS -> hourString
                        FocusableElement12Hour.MINUTES -> minuteString
                        else -> ""
                    },
                    color = MaterialTheme.colors.secondary,
                    style = MaterialTheme.typography.button,
                    maxLines = 1
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
                    val doubleTapToNext =
                        { position: FocusableElement12Hour, next: FocusableElement12Hour ->
                            focusedElement = when (focusedElement) {
                                position -> next
                                else -> position
                            }
                        }
                    Spacer(Modifier.width(8.dp))
                    PickerWithRSB(
                        readOnly = focusedElement != FocusableElement12Hour.HOURS,
                        state = hourState,
                        focusRequester = focusRequesterHours,
                        modifier = Modifier.size(48.dp, 100.dp),
                        onSelected = {
                            doubleTapToNext(
                                FocusableElement12Hour.HOURS,
                                FocusableElement12Hour.MINUTES
                            )
                        },
                        contentDescription = hoursContentDescription,
                        userScrollEnabled = !talkbackEnabled ||
                            focusedElement == FocusableElement12Hour.HOURS
                    ) { hour: Int ->
                        TimePiece(
                            selected = focusedElement == FocusableElement12Hour.HOURS,
                            onSelected = {
                                doubleTapToNext(
                                    FocusableElement12Hour.HOURS,
                                    FocusableElement12Hour.MINUTES
                                )
                            },
                            text = "%02d".format(hour + 1),
                            style = textStyle,
                            talkbackEnabled = talkbackEnabled
                        )
                    }
                    Separator(2.dp, textStyle)
                    PickerWithRSB(
                        readOnly = focusedElement != FocusableElement12Hour.MINUTES,
                        state = minuteState,
                        focusRequester = focusRequesterMinutes,
                        modifier = Modifier.size(48.dp, 100.dp),
                        onSelected = {
                            doubleTapToNext(
                                FocusableElement12Hour.MINUTES,
                                FocusableElement12Hour.PERIOD
                            )
                        },
                        contentDescription = minutesContentDescription,
                        userScrollEnabled = !talkbackEnabled ||
                            focusedElement == FocusableElement12Hour.MINUTES
                    ) { minute: Int ->
                        TimePiece(
                            selected = focusedElement == FocusableElement12Hour.MINUTES,
                            onSelected = {
                                doubleTapToNext(
                                    FocusableElement12Hour.MINUTES,
                                    FocusableElement12Hour.PERIOD
                                )
                            },
                            text = "%02d".format(minute),
                            style = textStyle,
                            talkbackEnabled = talkbackEnabled
                        )
                    }
                    Spacer(Modifier.width(6.dp))
                    PickerWithRSB(
                        readOnly = focusedElement != FocusableElement12Hour.PERIOD,
                        state = periodState,
                        focusRequester = focusRequesterPeriod,
                        modifier = Modifier.size(64.dp, 100.dp),
                        onSelected = {
                            doubleTapToNext(
                                FocusableElement12Hour.PERIOD,
                                FocusableElement12Hour.CONFIRM_BUTTON
                            )
                        },
                        contentDescription = periodContentDescription,
                        userScrollEnabled = !talkbackEnabled ||
                            focusedElement == FocusableElement12Hour.PERIOD
                    ) { period: Int ->
                        TimePiece(
                            selected = focusedElement == FocusableElement12Hour.PERIOD,
                            onSelected = {
                                doubleTapToNext(
                                    FocusableElement12Hour.PERIOD,
                                    FocusableElement12Hour.CONFIRM_BUTTON
                                )
                            },
                            text = if (period == 0) amString else pmString,
                            style = textStyle,
                            talkbackEnabled = talkbackEnabled
                        )
                    }
                }
                Spacer(
                    Modifier
                        .fillMaxWidth()
                        .weight(0.5f)
                )
                Button(
                    onClick = {
                        val confirmedTime = LocalTime.of(
                            hourState.selectedOption + 1,
                            minuteState.selectedOption,
                            0
                        ).with(ChronoField.AMPM_OF_DAY, periodState.selectedOption.toLong())
                        onTimeConfirm(confirmedTime)
                    },
                    modifier = Modifier
                        .semantics {
                            focused = focusedElement == FocusableElement12Hour.CONFIRM_BUTTON
                        }
                        .focusRequester(focusRequesterConfirmButton)

                ) {
                    Icon(
                        imageVector = Icons.Filled.Check,
                        contentDescription = stringResource(R.string.horologist_picker_confirm_button_content_description),
                        modifier = Modifier
                            .size(24.dp)
                            .wrapContentSize(align = Alignment.Center)
                    )
                }
                Spacer(Modifier.height(8.dp))
                LaunchedEffect(focusedElement) {
                    if (focusedElement != FocusableElement12Hour.NONE) {
                        listOf(
                            focusRequesterHours,
                            focusRequesterMinutes,
                            focusRequesterPeriod,
                            focusRequesterConfirmButton
                        )[focusedElement.index].requestFocus()
                    }
                }
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
    style: TextStyle,
    talkbackEnabled: Boolean = false
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
            modifier = if (selected || talkbackEnabled) {
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
        color = MaterialTheme.colors.onBackground,
        modifier = Modifier.clearAndSetSemantics {}
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
    userScrollEnabled: Boolean = true,
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
        userScrollEnabled = userScrollEnabled,
        option = option
    )
}

@Composable
internal fun rememberTalkBackState(): MutableState<Boolean> {
    val context = LocalContext.current
    val accessibilityManager = setupAccessibilityManager(context)
    var initialContent by remember { mutableStateOf(true) }

    if (initialContent) {
        touchExplorationEnabled = accessibilityManager.isTouchExplorationEnabled
        initialContent = false
    }
    var talkbackEnabled = remember {
        mutableStateOf(accessibilityManager.isEnabled && touchExplorationEnabled)
    }

    LocalLifecycleOwner.current.lifecycle.ObserveState(
        handleEvent = { event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                talkbackEnabled.value = accessibilityManager.isEnabled && touchExplorationEnabled
            }
        },
        onDispose = {
            accessibilityManager.removeTouchExplorationStateChangeListener(
                touchExplorationStateChangeListener
            )
        }
    )
    return talkbackEnabled
}

@Composable
internal fun Lifecycle.ObserveState(
    handleEvent: (Lifecycle.Event) -> Unit = {},
    onDispose: () -> Unit = {}
) {
    DisposableEffect(this) {
        val observer = LifecycleEventObserver { _, event ->
            handleEvent(event)
        }
        this@ObserveState.addObserver(observer)
        onDispose {
            onDispose()
            this@ObserveState.removeObserver(observer)
        }
    }
}

internal fun Modifier.scrollablePicker(
    pickerState: PickerState
) = Modifier.composed {
    this.scrollable(
        state = pickerState,
        orientation = Orientation.Vertical,
        flingBehavior = PickerDefaults.flingBehavior(state = pickerState),
        reverseDirection = true
    )
}

private fun setupAccessibilityManager(context: Context): AccessibilityManager {
    val accessibilityManager =
        context.getSystemService(Context.ACCESSIBILITY_SERVICE) as AccessibilityManager
    accessibilityManager.addTouchExplorationStateChangeListener(
        touchExplorationStateListener()
    )
    return accessibilityManager
}

private fun touchExplorationStateListener(): AccessibilityManager.TouchExplorationStateChangeListener {
    touchExplorationStateChangeListener =
        AccessibilityManager.TouchExplorationStateChangeListener { isEnabled ->
            touchExplorationEnabled = isEnabled
        }
    return touchExplorationStateChangeListener
}

private fun createDescription(
    focusedElement: FocusableElement,
    selectedValue: Int,
    label: String
): String {
    return when (focusedElement) {
        FocusableElement.NONE -> {
            label
        }
        else -> "$selectedValue" + label
    }
}

private fun createDescription12Hour(
    focusedElement: FocusableElement12Hour,
    selectedValue: Int,
    label: String
): String {
    return when (focusedElement) {
        FocusableElement12Hour.HOURS -> {
            "$selectedValue" + label
        }
        FocusableElement12Hour.MINUTES -> {
            "$selectedValue" + label
        }
        else -> label
    }
}

private enum class FocusableElement(val index: Int) {
    HOURS(0),
    MINUTES(1),
    SECONDS(2),
    CONFIRM_BUTTON(3),
    NONE(-1)
}

private enum class FocusableElement12Hour(val index: Int) {
    HOURS(0),
    MINUTES(1),
    PERIOD(2),
    CONFIRM_BUTTON(3),
    NONE(-1)
}
