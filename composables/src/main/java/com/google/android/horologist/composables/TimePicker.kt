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

@file:OptIn(ExperimentalFoundationApi::class)

package com.google.android.horologist.composables

import android.content.Context
import android.view.accessibility.AccessibilityManager
import androidx.annotation.PluralsRes
import androidx.annotation.VisibleForTesting
import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.focusable
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
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.focused
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.wear.compose.foundation.ExperimentalWearFoundationApi
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.TouchExplorationStateProvider
import com.google.android.horologist.composables.picker.PickerGroup
import com.google.android.horologist.composables.picker.PickerGroupItem
import com.google.android.horologist.composables.picker.PickerGroupState
import com.google.android.horologist.composables.picker.PickerScope
import com.google.android.horologist.composables.picker.PickerState
import com.google.android.horologist.composables.picker.rememberPickerGroupState
import com.google.android.horologist.composables.picker.rememberPickerState
import com.google.android.horologist.composables.picker.toRotaryScrollAdapter
import com.google.android.horologist.compose.layout.ScreenScaffold
import com.google.android.horologist.compose.rotaryinput.rotaryWithSnap
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
    showSeconds: Boolean = true,
) {
    val inspectionMode = LocalInspectionMode.current
    val fullyDrawn = remember { Animatable(if (inspectionMode) 1f else 0f) }

    val hourState = rememberPickerState(
        initialNumberOfOptions = 24,
        initiallySelectedOption = time.hour,
    )
    val minuteState = rememberPickerState(
        initialNumberOfOptions = 60,
        initiallySelectedOption = time.minute,
    )
    val secondState = rememberPickerState(
        initialNumberOfOptions = 60,
        initiallySelectedOption = time.second,
    )

    val touchExplorationStateProvider = remember { DefaultTouchExplorationStateProvider() }
    val touchExplorationServicesEnabled by touchExplorationStateProvider
        .touchExplorationState()

    // When the time picker loads, none of the individual pickers are selected in talkback mode,
    // otherwise hours picker should be focused.
    val pickerGroupState = if (touchExplorationServicesEnabled) {
        rememberPickerGroupState(FocusableElementsTimePicker.NONE.index)
    } else {
        rememberPickerGroupState(FocusableElementsTimePicker.HOURS.index)
    }

    val textStyle = with(LocalDensity.current) {
        if (showSeconds) {
            fontScaleIndependent(MaterialTheme.typography.display3)
        } else {
            fontScaleIndependent(MaterialTheme.typography.display1)
        }
    }

    val optionColor = MaterialTheme.colors.secondary
    val pickerOption = pickerTextOption(textStyle, { "%02d".format(it) })
    val focusRequesterConfirmButton = remember { FocusRequester() }

    val hourString = stringResource(R.string.horologist_time_picker_hour)
    val minuteString = stringResource(R.string.horologist_time_picker_minute)
    val secondString = stringResource(R.string.horologist_time_picker_second)

    val hourContentDescription = createDescription(
        pickerGroupState,
        hourState.selectedOption,
        hourString,
        R.plurals.horologist_time_picker_hours_content_description,
    )

    val minuteContentDescription = createDescription(
        pickerGroupState,
        minuteState.selectedOption,
        minuteString,
        R.plurals.horologist_time_picker_minutes_content_description,
    )

    val secondContentDescription = createDescription(
        pickerGroupState,
        secondState.selectedOption,
        secondString,
        R.plurals.horologist_time_picker_seconds_content_description,
    )

    val onPickerSelected =
        { current: FocusableElementsTimePicker, next: FocusableElementsTimePicker ->
            if (pickerGroupState.selectedIndex != current.index) {
                pickerGroupState.selectedIndex = current.index
            } else {
                pickerGroupState.selectedIndex = next.index
                if (next == FocusableElementsTimePicker.CONFIRM_BUTTON) {
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
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
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
                maxLines = 1,
            )
            val weightsToCenterVertically = 0.5f
            Spacer(
                Modifier
                    .fillMaxWidth()
                    .weight(weightsToCenterVertically),
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
            ) {
                val pickerGroupItems = mutableListOf(
                    pickerGroupItemWithRSB(
                        pickerState = hourState,
                        modifier = Modifier.size(52.dp, 100.dp),
                        onSelected = {
                            onPickerSelected(
                                FocusableElementsTimePicker.HOURS,
                                FocusableElementsTimePicker.MINUTES,
                            )
                        },
                        contentDescription = hourContentDescription,
                        option = pickerOption,
                    ),
                    pickerGroupItemWithRSB(
                        pickerState = minuteState,
                        modifier = Modifier.size(52.dp, 100.dp),
                        onSelected = {
                            onPickerSelected(
                                FocusableElementsTimePicker.MINUTES,
                                if (showSeconds) {
                                    FocusableElementsTimePicker.SECONDS
                                } else {
                                    FocusableElementsTimePicker.CONFIRM_BUTTON
                                },
                            )
                        },
                        contentDescription = minuteContentDescription,
                        option = pickerOption,
                    ),
                )
                if (showSeconds) {
                    pickerGroupItems.add(
                        pickerGroupItemWithRSB(
                            pickerState = secondState,
                            modifier = Modifier.size(52.dp, 100.dp),
                            onSelected = {
                                onPickerSelected(
                                    FocusableElementsTimePicker.SECONDS,
                                    FocusableElementsTimePicker.CONFIRM_BUTTON,
                                )
                            },
                            contentDescription = secondContentDescription,
                            option = pickerOption,
                        ),
                    )
                }
                PickerGroup(
                    *pickerGroupItems.toTypedArray(),
                    pickerGroupState = pickerGroupState,
                    separator = { Separator(textStyle) },
                    autoCenter = false,
                    touchExplorationStateProvider = touchExplorationStateProvider,
                )
            }
            Spacer(
                Modifier
                    .fillMaxWidth()
                    .weight(weightsToCenterVertically),
            )
            Button(
                onClick = {
                    val seconds = if (showSeconds) secondState.selectedOption else 0
                    val confirmedTime = LocalTime.of(
                        hourState.selectedOption,
                        minuteState.selectedOption,
                        seconds,
                    )
                    onTimeConfirm(confirmedTime)
                },
                modifier = Modifier
                    .semantics {
                        focused = pickerGroupState.selectedIndex ==
                            FocusableElementsTimePicker.CONFIRM_BUTTON.index
                    }
                    .focusRequester(focusRequesterConfirmButton)
                    .focusable(),
            ) {
                Icon(
                    imageVector = Icons.Filled.Check,
                    contentDescription = stringResource(R.string.horologist_picker_confirm_button_content_description),
                    modifier = Modifier
                        .size(24.dp)
                        .wrapContentSize(align = Alignment.Center),
                )
            }
            Spacer(Modifier.height(12.dp))
        }
    }

    if (!inspectionMode) {
        LaunchedEffect(Unit) {
            fullyDrawn.animateTo(1f)
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
    time: LocalTime = LocalTime.now(),
) {
    val inspectionMode = LocalInspectionMode.current
    val fullyDrawn = remember { Animatable(if (inspectionMode) 1f else 0f) }

    val hourState = rememberPickerState(
        initialNumberOfOptions = 12,
        initiallySelectedOption = time[ChronoField.CLOCK_HOUR_OF_AMPM] - 1,
    )
    val minuteState = rememberPickerState(
        initialNumberOfOptions = 60,
        initiallySelectedOption = time.minute,
    )
    val periodState = rememberPickerState(
        initialNumberOfOptions = 2,
        initiallySelectedOption = time[ChronoField.AMPM_OF_DAY],
        repeatItems = false,
    )

    val touchExplorationStateProvider = remember { DefaultTouchExplorationStateProvider() }

    val touchExplorationServicesEnabled by touchExplorationStateProvider
        .touchExplorationState()

    // When the time picker loads, none of the individual pickers are selected in talkback mode,
    // otherwise hours picker should be focused.
    val pickerGroupState =
        if (touchExplorationServicesEnabled) {
            rememberPickerGroupState(FocusableElement12Hour.NONE.index)
        } else {
            rememberPickerGroupState(FocusableElement12Hour.HOURS.index)
        }

    val textStyle =
        with(LocalDensity.current) { fontScaleIndependent(MaterialTheme.typography.display3) }

    val focusRequesterConfirmButton = remember { FocusRequester() }

    val hourString = stringResource(R.string.horologist_time_picker_hour)
    val minuteString = stringResource(R.string.horologist_time_picker_minute)
    val periodString = stringResource(R.string.horologist_time_picker_period)

    val hoursContentDescription = createDescription12Hour(
        pickerGroupState,
        hourState.selectedOption + 1,
        hourString,
        R.plurals.horologist_time_picker_hours_content_description,
    )

    val minutesContentDescription = createDescription12Hour(
        pickerGroupState,
        minuteState.selectedOption,
        minuteString,
        R.plurals.horologist_time_picker_minutes_content_description,
    )

    val amString = stringResource(R.string.horologist_time_picker_am)
    val pmString = stringResource(R.string.horologist_time_picker_pm)
    val periodContentDescription by remember(
        pickerGroupState.selectedIndex,
        periodState.selectedOption,
    ) {
        derivedStateOf {
            if (pickerGroupState.selectedIndex == FocusableElement12Hour.NONE.index) {
                periodString
            } else if (periodState.selectedOption == 0) {
                amString
            } else {
                pmString
            }
        }
    }

    ScreenScaffold(
        modifier = modifier
            .fillMaxSize()
            .alpha(fullyDrawn.value),
        timeText = {},
    ) {
        Column(
            modifier = modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
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
                maxLines = 1,
            )
            val weightsToCenterVertically = 0.5f
            Spacer(
                Modifier
                    .fillMaxWidth()
                    .weight(weightsToCenterVertically),
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
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

                // TODO remove the offsets below, once checked with UX
                // these are to refactor the code, remove spacers without changing
                // visible layout at the same time.
                PickerGroup(
                    pickerGroupItemWithRSB(
                        pickerState = hourState,
                        modifier = Modifier
                            .size(57.dp, 100.dp)
                            .offset(x = (2.5).dp),
                        onSelected = {
                            doubleTapToNext(
                                FocusableElement12Hour.HOURS,
                                FocusableElement12Hour.MINUTES,
                            )
                        },
                        contentDescription = hoursContentDescription,
                        option = pickerTextOption(textStyle, { "%02d".format(it + 1) }),
                    ),
                    pickerGroupItemWithRSB(
                        pickerState = minuteState,
                        modifier = Modifier
                            .size(53.dp, 100.dp)
                            .offset(x = (-0.5).dp),
                        onSelected = {
                            doubleTapToNext(
                                FocusableElement12Hour.MINUTES,
                                FocusableElement12Hour.PERIOD,
                            )
                        },
                        contentDescription = minutesContentDescription,
                        option = pickerTextOption(textStyle, { "%02d".format(it) }),
                    ),
                    pickerGroupItemWithRSB(
                        pickerState = periodState,
                        modifier = Modifier
                            .size(68.dp, 100.dp)
                            .offset(x = (3).dp),
                        contentDescription = periodContentDescription,
                        onSelected = {
                            doubleTapToNext(
                                FocusableElement12Hour.PERIOD,
                                FocusableElement12Hour.CONFIRM_BUTTON,
                            )
                        },
                        option = pickerTextOption(textStyle, {
                            if (it == 0) amString else pmString
                        }),
                    ),
                    autoCenter = false,
                    pickerGroupState = pickerGroupState,
                    separator = {
                        if (it == 0) {
                            Separator(textStyle)
                        }
                    },
                    touchExplorationStateProvider = touchExplorationStateProvider,
                )
            }
            Spacer(
                Modifier
                    .fillMaxWidth()
                    .weight(weightsToCenterVertically),
            )
            Button(
                onClick = {
                    val confirmedTime = LocalTime.of(
                        hourState.selectedOption + 1,
                        minuteState.selectedOption,
                        0,
                    ).with(ChronoField.AMPM_OF_DAY, periodState.selectedOption.toLong())
                    onTimeConfirm(confirmedTime)
                },
                modifier = Modifier
                    .semantics {
                        focused = pickerGroupState.selectedIndex ==
                            FocusableElement12Hour.CONFIRM_BUTTON.index
                    }
                    .focusRequester(focusRequesterConfirmButton)
                    .focusable(),
            ) {
                Icon(
                    imageVector = Icons.Filled.Check,
                    contentDescription = stringResource(R.string.horologist_picker_confirm_button_content_description),
                    modifier = Modifier
                        .size(24.dp)
                        .wrapContentSize(align = Alignment.Center),
                )
            }
            Spacer(Modifier.height(8.dp))
        }
    }

    if (!inspectionMode) {
        LaunchedEffect(Unit) {
            fullyDrawn.animateTo(1f)
        }
    }
}

@Composable
private fun Separator(textStyle: TextStyle) {
    Text(
        text = ":",
        style = textStyle,
        color = MaterialTheme.colors.onBackground,
        modifier = Modifier.clearAndSetSemantics {},
    )
}

// Omit scaling according to Settings > Display > Font size for this screen,
internal fun Density.fontScaleIndependent(style: TextStyle): TextStyle {
    return style.copy(
        fontSize = style.fontSize.value.dp.toSp(),
    )
}

@Composable
@OptIn(ExperimentalWearFoundationApi::class)
@VisibleForTesting(otherwise = VisibleForTesting.PACKAGE_PRIVATE)
internal fun pickerGroupItemWithRSB(
    pickerState: PickerState,
    modifier: Modifier,
    contentDescription: String?,
    onSelected: () -> Unit,
    readOnlyLabel: @Composable (BoxScope.() -> Unit)? = null,
    option: @Composable PickerScope.(optionIndex: Int, pickerSelected: Boolean) -> Unit,
): PickerGroupItem {
    return PickerGroupItem(
        pickerState = pickerState,
        modifier = modifier.rotaryWithSnap(
            pickerState.toRotaryScrollAdapter(),
        ),
        contentDescription = contentDescription,
        onSelected = onSelected,
        readOnlyLabel = readOnlyLabel,
        option = option,
    )
}

internal fun pickerTextOption(
    textStyle: TextStyle,
    indexToText: (Int) -> String,
    isValid: (Int) -> Boolean = { true },
): (@Composable PickerScope.(optionIndex: Int, pickerSelected: Boolean) -> Unit) = { value: Int, pickerSelected: Boolean ->
    Box(modifier = Modifier.fillMaxSize()) {
        Text(
            text = indexToText(value),
            maxLines = 1,
            style = textStyle,
            color = if (!isValid(value)) {
                Color(0xFF757575)
            } else if (pickerSelected) {
                MaterialTheme.colors.secondary
            } else {
                MaterialTheme.colors.onBackground
            },
            modifier = Modifier
                .align(Alignment.Center)
                .wrapContentSize(),
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
        listener.onAccessibilityStateChanged(accessibilityManager.isEnabled)
        listener.onTouchExplorationStateChanged(accessibilityManager.isTouchExplorationEnabled)

        LocalLifecycleOwner.current.lifecycle.ObserveState(
            handleEvent = { event ->
                if (event == Lifecycle.Event.ON_RESUME) {
                    listener.register(accessibilityManager)
                }
            },
            onDispose = {
                listener.unregister(accessibilityManager)
            },
        )

        return remember { derivedStateOf { listener.isEnabled() } }
    }

    @Composable
    private fun Lifecycle.ObserveState(
        handleEvent: (Lifecycle.Event) -> Unit = {},
        onDispose: () -> Unit = {},
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

    private class Listener :
        AccessibilityManager.AccessibilityStateChangeListener,
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

@Composable
private fun createDescription(
    pickerGroupState: PickerGroupState,
    selectedValue: Int,
    label: String,
    @PluralsRes resourceId: Int,
): String {
    return when (pickerGroupState.selectedIndex) {
        FocusableElementsTimePicker.NONE.index -> label
        else -> pluralStringResource(resourceId, selectedValue, selectedValue)
    }
}

@Composable
private fun createDescription12Hour(
    pickerGroupState: PickerGroupState,
    selectedValue: Int,
    label: String,
    @PluralsRes resourceId: Int,
): String {
    return when (pickerGroupState.selectedIndex) {
        FocusableElement12Hour.NONE.index -> label
        else -> pluralStringResource(resourceId, selectedValue, selectedValue)
    }
}

private enum class FocusableElementsTimePicker(val index: Int) {
    HOURS(0),
    MINUTES(1),
    SECONDS(2),
    CONFIRM_BUTTON(3),
    NONE(-1),
    ;

    companion object {
        private val map = entries.associateBy { it.index }
        operator fun get(value: Int) = map[value]
    }
}

private enum class FocusableElement12Hour(val index: Int) {
    HOURS(0),
    MINUTES(1),
    PERIOD(2),
    CONFIRM_BUTTON(3),
    NONE(-1),
    ;

    companion object {
        private val map = entries.associateBy { it.index }
        operator fun get(value: Int) = map[value]
    }
}
