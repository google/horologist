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
import android.view.accessibility.AccessibilityManager
import androidx.compose.foundation.focusable
import androidx.compose.foundation.gestures.FlingBehavior
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
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
import androidx.wear.compose.material.PickerGroup
import androidx.wear.compose.material.PickerGroupItem
import androidx.wear.compose.material.PickerGroupState
import androidx.wear.compose.material.PickerScope
import androidx.wear.compose.material.PickerState
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.TouchExplorationStateProvider
import androidx.wear.compose.material.rememberPickerGroupState
import androidx.wear.compose.material.rememberPickerState
import com.google.android.horologist.compose.rotaryinput.onRotaryInputAccumulated
import kotlinx.coroutines.launch
import java.time.LocalTime
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
    val secondState = rememberPickerState(
        initialNumberOfOptions = 60,
        initiallySelectedOption = time.second
    )

    val touchExplorationStateProvider = DefaultTouchExplorationStateProvider()
    val touchExplorationServicesEnabled by touchExplorationStateProvider
        .touchExplorationState()

    MaterialTheme(typography = typography) {
        // When the time picker loads, none of the individual pickers are selected in talkback mode,
        // otherwise hours picker should be focused.
        val pickerGroupState = if (touchExplorationServicesEnabled) {
            rememberPickerGroupState(FocusableElementsTimePicker.NONE.index)
        } else {
            rememberPickerGroupState(FocusableElementsTimePicker.HOURS.index)
        }
        val textStyle = MaterialTheme.typography.display3
        val optionColor = MaterialTheme.colors.secondary
        val pickerOption = pickerTextOption(textStyle) { "%02d".format(it) }
        val focusRequesterConfirmButton = remember { FocusRequester() }

        val hourString = stringResource(R.string.horologist_time_picker_hour)
        val minuteString = stringResource(R.string.horologist_time_picker_minute)
        val secondString = stringResource(R.string.horologist_time_picker_second)
        val zeroString = stringResource(R.string.horologist_picker_zero_content_description)

        val hourContentDescription by remember(
            pickerGroupState.selectedIndex,
            hourState.selectedOption
        ) {
            derivedStateOf {
                createDescription(
                    pickerGroupState,
                    hourState.selectedOption,
                    hourString,
                    zeroString
                )
            }
        }
        val minuteContentDescription by remember(
            pickerGroupState.selectedIndex,
            minuteState.selectedOption
        ) {
            derivedStateOf {
                createDescription(
                    pickerGroupState,
                    minuteState.selectedOption,
                    minuteString,
                    zeroString
                )
            }
        }
        val secondContentDescription by remember(
            pickerGroupState.selectedIndex,
            secondState.selectedOption
        ) {
            derivedStateOf {
                createDescription(
                    pickerGroupState,
                    secondState.selectedOption,
                    secondString,
                    zeroString
                )
            }
        }

        val onPickerSelected = { current: FocusableElementsTimePicker,
                                 next: FocusableElementsTimePicker ->
            if (pickerGroupState.selectedIndex != current.index) {
                pickerGroupState.selectedIndex = current.index
            } else {
                pickerGroupState.selectedIndex = next.index
                if (next == FocusableElementsTimePicker.CONFIRM_BUTTON) {
                    focusRequesterConfirmButton.requestFocus()
                }
            }
        }

        Box(modifier = modifier.fillMaxSize()) {
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(Modifier.height(12.dp))
                Text(
                    text = when (FocusableElementsTimePicker[pickerGroupState.selectedIndex]) {
                        FocusableElementsTimePicker.HOURS -> hourString
                        FocusableElementsTimePicker.MINUTES -> minuteString
                        FocusableElementsTimePicker.SECONDS -> secondString
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
                    val pickerGroupItems = mutableListOf(
                        PickerGroupItemWithRSB(
                            pickerState = hourState,
                            modifier = Modifier.size(40.dp, 100.dp),
                            focusRequester = remember { FocusRequester() },
                            onSelected = {
                                onPickerSelected(
                                    FocusableElementsTimePicker.HOURS,
                                    FocusableElementsTimePicker.MINUTES
                                )
                            },
                            contentDescription = hourContentDescription,
                            option = pickerOption
                        ),
                        PickerGroupItemWithRSB(
                            pickerState = minuteState,
                            modifier = Modifier.size(40.dp, 100.dp),
                            focusRequester = remember { FocusRequester() },
                            onSelected = {
                                onPickerSelected(
                                    FocusableElementsTimePicker.MINUTES,
                                    if (showSeconds) FocusableElementsTimePicker.SECONDS
                                    else FocusableElementsTimePicker.CONFIRM_BUTTON
                                )
                            },
                            contentDescription = minuteContentDescription,
                            option = pickerOption
                        )
                    )
                    if (showSeconds) {
                        pickerGroupItems.add(
                            PickerGroupItemWithRSB(
                                pickerState = secondState,
                                modifier = Modifier.size(40.dp, 100.dp),
                                focusRequester = remember { FocusRequester() },
                                onSelected = {
                                    onPickerSelected(
                                        FocusableElementsTimePicker.SECONDS,
                                        FocusableElementsTimePicker.CONFIRM_BUTTON
                                    )
                                },
                                contentDescription = secondContentDescription,
                                option = pickerOption
                            )
                        )
                    }
                    PickerGroup(
                        *pickerGroupItems.toTypedArray(),
                        pickerGroupState = pickerGroupState,
                        separator = { Separator(6.dp, textStyle) },
                        autoCenter = false,
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
                            focused = pickerGroupState.selectedIndex ==
                                FocusableElementsTimePicker.CONFIRM_BUTTON.index
                        }
                        .focusRequester(focusRequesterConfirmButton)
                        .focusable()
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
        repeatItems = false
    )

    val touchExplorationStateProvider = DefaultTouchExplorationStateProvider()
    val touchExplorationServicesEnabled by touchExplorationStateProvider
        .touchExplorationState()

    MaterialTheme(typography = typography) {
        val pickerGroupState =
            if (touchExplorationServicesEnabled) {
                rememberPickerGroupState(FocusableElement12Hour.NONE.index)
            } else {
                rememberPickerGroupState(FocusableElement12Hour.HOURS.index)
            }
        val textStyle = MaterialTheme.typography.display3
        val focusRequesterConfirmButton = remember { FocusRequester() }

        val hourString = stringResource(R.string.horologist_time_picker_hour)
        val minuteString = stringResource(R.string.horologist_time_picker_minute)
        val periodString = stringResource(R.string.horologist_time_picker_period)
        val zeroString = stringResource(R.string.horologist_picker_zero_content_description)

        val hoursContentDescription by remember(
            pickerGroupState.selectedIndex,
            hourState.selectedOption
        ) {
            derivedStateOf {
                createDescription12Hour(
                    pickerGroupState,
                    hourState.selectedOption + 1,
                    hourString,
                    zeroString
                )
            }
        }
        val minutesContentDescription by remember(
            pickerGroupState.selectedIndex,
            minuteState.selectedOption
        ) {
            derivedStateOf {
                createDescription12Hour(
                    pickerGroupState,
                    minuteState.selectedOption,
                    minuteString,
                    zeroString
                )
            }
        }

        val amString = stringResource(R.string.horologist_time_picker_am)
        val pmString = stringResource(R.string.horologist_time_picker_pm)
        val periodContentDescription by remember(
            pickerGroupState.selectedIndex,
            periodState.selectedOption
        ) {
            derivedStateOf {
                if (pickerGroupState.selectedIndex == FocusableElement12Hour.NONE.index)
                    periodString
                else if (periodState.selectedOption == 0)
                    amString
                else pmString
            }
        }
        Box(
            modifier = modifier.fillMaxSize()
        ) {
            Column(
                modifier = modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(Modifier.height(12.dp))
                Text(
                    text = when (FocusableElement12Hour[pickerGroupState.selectedIndex]) {
                        FocusableElement12Hour.HOURS -> hourString
                        FocusableElement12Hour.MINUTES -> minuteString
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
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    val doubleTapToNext =
                        { current: FocusableElement12Hour, next: FocusableElement12Hour ->
                            if (pickerGroupState.selectedIndex != current.index) {
                                pickerGroupState.selectedIndex = current.index
                            } else {
                                pickerGroupState.selectedIndex = next.index
                                if (next == FocusableElement12Hour.CONFIRM_BUTTON) {
                                    focusRequesterConfirmButton.requestFocus()
                                }
                            }
                        }
                    Spacer(Modifier.width(8.dp))
                    PickerGroup(
                        PickerGroupItemWithRSB(
                            pickerState = hourState,
                            modifier = Modifier.size(48.dp, 100.dp),
                            focusRequester = remember { FocusRequester() },
                            onSelected = {
                                doubleTapToNext(
                                    FocusableElement12Hour.HOURS,
                                    FocusableElement12Hour.MINUTES
                                )
                            },
                            contentDescription = hoursContentDescription,
                            option = pickerTextOption(textStyle) { "%02d".format(it + 1) }
                        ),
                        PickerGroupItemWithRSB(
                            pickerState = minuteState,
                            modifier = Modifier.size(48.dp, 100.dp),
                            focusRequester = remember { FocusRequester() },

                            onSelected = {
                                doubleTapToNext(
                                    FocusableElement12Hour.MINUTES,
                                    FocusableElement12Hour.PERIOD
                                )
                            },
                            contentDescription = minutesContentDescription,
                            option = pickerTextOption(textStyle) { "%02d".format(it) }
                        ),
                        PickerGroupItemWithRSB(
                            pickerState = periodState,
                            modifier = Modifier.size(64.dp, 100.dp),
                            focusRequester = remember { FocusRequester() },
                            contentDescription = periodContentDescription,
                            onSelected = {
                                doubleTapToNext(
                                    FocusableElement12Hour.PERIOD,
                                    FocusableElement12Hour.CONFIRM_BUTTON
                                )
                            },
                            option = pickerTextOption(textStyle) {
                                if (it == 0) amString else pmString
                            }
                        ),
                        autoCenter = false,
                        pickerGroupState = pickerGroupState,
                        separator = {
                            if (it == 0) {
                                Separator(2.dp, textStyle)
                            } else Spacer(Modifier.width(8.dp))
                        },
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
                        val confirmedTime = LocalTime.of(
                            hourState.selectedOption + 1,
                            minuteState.selectedOption,
                            0
                        ).with(ChronoField.AMPM_OF_DAY, periodState.selectedOption.toLong())
                        onTimeConfirm(confirmedTime)
                    },
                    modifier = Modifier
                        .semantics {
                            focused = pickerGroupState.selectedIndex ==
                                FocusableElement12Hour.CONFIRM_BUTTON.index
                        }
                        .focusRequester(focusRequesterConfirmButton)
                        .focusable()
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
            }
        }
    }
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
internal fun PickerGroupItemWithRSB(
    pickerState: PickerState,
    modifier: Modifier,
    contentDescription: String?,
    focusRequester: FocusRequester?,
    onSelected: () -> Unit,
    readOnlyLabel: @Composable() (BoxScope.() -> Unit)? = null,
    option: @Composable() PickerScope.(optionIndex: Int, pickerSelected: Boolean) -> Unit
): PickerGroupItem {
    val coroutineScope = rememberCoroutineScope()
    return PickerGroupItem(
        pickerState = pickerState,
        modifier = modifier.onRotaryInputAccumulated {
            coroutineScope.launch {
                if (it > 0) {
                    pickerState.scrollToOption(pickerState.selectedOption + 1)
                } else {
                    pickerState.scrollToOption(pickerState.selectedOption - 1)
                }
            }
        },
        contentDescription = contentDescription,
        focusRequester = focusRequester,
        onSelected = onSelected,
        readOnlyLabel = readOnlyLabel,
        option = option
    )
}

internal fun pickerTextOption(textStyle: TextStyle, indexToText: (Int) -> String):
    (@Composable PickerScope.(optionIndex: Int, pickerSelected: Boolean) -> Unit) = { value: Int, pickerSelected: Boolean ->
    Box(modifier = Modifier.fillMaxSize()) {
        Text(
            text = indexToText(value),
            maxLines = 1,
            style = textStyle,
            color =
            if (pickerSelected) MaterialTheme.colors.secondary
            else MaterialTheme.colors.onBackground,
            modifier = Modifier
                .align(Alignment.Center)
                .wrapContentSize()
        )
    }
}

internal class DefaultTouchExplorationStateProvider : TouchExplorationStateProvider {

    @Composable
    public override fun touchExplorationState(): State<Boolean> {
        val context = LocalContext.current
        val accessibilityManager = remember {
            context.getSystemService(Context.ACCESSIBILITY_SERVICE) as AccessibilityManager
        }

        val listener = remember { Listener() }

        LocalLifecycleOwner.current.lifecycle.ObserveState(
            handleEvent = { event ->
                if (event == Lifecycle.Event.ON_RESUME) {
                    listener.register(accessibilityManager)
                }
            },
            onDispose = {
                listener.unregister(accessibilityManager)
            }
        )

        return remember { derivedStateOf { listener.isEnabled() } }
    }

    @Composable
    private fun Lifecycle.ObserveState(
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

    private class Listener : AccessibilityManager.AccessibilityStateChangeListener,
        AccessibilityManager.TouchExplorationStateChangeListener {

        private var accessibilityEnabled by mutableStateOf(false)
        private var touchExplorationEnabled by mutableStateOf(false)

        fun isEnabled() = accessibilityEnabled && touchExplorationEnabled

        override fun onAccessibilityStateChanged(it: Boolean) {
            accessibilityEnabled = it
        }

        override fun onTouchExplorationStateChanged(it: Boolean) {
            touchExplorationEnabled = it
        }

        fun register(am: AccessibilityManager) {
            accessibilityEnabled = am.isEnabled
            touchExplorationEnabled = am.isTouchExplorationEnabled

            am.addTouchExplorationStateChangeListener(this)
            am.addAccessibilityStateChangeListener(this)
        }

        fun unregister(am: AccessibilityManager) {
            am.removeTouchExplorationStateChangeListener(this)
            am.removeAccessibilityStateChangeListener(this)
        }
    }
}

private fun createDescription(
    pickerGroupState: PickerGroupState,
    selectedValue: Int,
    label: String,
    zeroString: String
): String {
    return when (pickerGroupState.selectedIndex) {
        FocusableElementsTimePicker.NONE.index -> label
        else -> zeroCorrectedContentDescription(
            value = selectedValue, zeroString = zeroString, suffix = label
        )
    }
}

private fun createDescription12Hour(
    pickerGroupState: PickerGroupState,
    selectedValue: Int,
    label: String,
    zeroString: String
): String {
    return when (pickerGroupState.selectedIndex) {
        FocusableElement12Hour.NONE.index -> label
        else -> zeroCorrectedContentDescription(
            value = selectedValue, zeroString = zeroString, suffix = label
        )
    }
}

internal fun zeroCorrectedContentDescription(
    value: Int,
    zeroString: String,
    prefix: String = "",
    suffix: String = ""
): String {
    val word = if (value == 0) {
        zeroString
    } else "$value"
    return "$prefix $word $suffix"
}

private enum class FocusableElementsTimePicker(val index: Int) {
    HOURS(0),
    MINUTES(1),
    SECONDS(2),
    CONFIRM_BUTTON(3),
    NONE(-1);

    companion object {
        private val map = FocusableElementsTimePicker.values().associateBy { it.index }
        operator fun get(value: Int) = map[value]
    }
}

private enum class FocusableElement12Hour(val index: Int) {
    HOURS(0),
    MINUTES(1),
    PERIOD(2),
    CONFIRM_BUTTON(3),
    NONE(-1);

    companion object {
        private val map = FocusableElement12Hour.values().associateBy { it.index }
        operator fun get(value: Int) = map[value]
    }
}
