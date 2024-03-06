/*
 * Copyright 2023 The Android Open Source Project
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

package com.google.android.horologist.composables.picker

import androidx.compose.foundation.focusable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.AlignmentLine
import androidx.compose.ui.layout.VerticalAlignmentLine
import androidx.compose.ui.layout.layout
import androidx.wear.compose.foundation.ExperimentalWearFoundationApi
import androidx.wear.compose.foundation.HierarchicalFocusCoordinator
import androidx.wear.compose.foundation.rememberActiveFocusRequester
import androidx.wear.compose.material.TouchExplorationStateProvider
import com.google.android.horologist.composables.DefaultTouchExplorationStateProvider
import kotlinx.coroutines.coroutineScope
import kotlin.math.min

/**
 * This is a private copy of androidx.wear.compose.material.PickerGroup
 */
// TODO(b/294842202): Remove once rotary modifiers are in AndroidX

/**
 * A group of [Picker]s to build components where multiple pickers are required to be combined
 * together.
 * The component maintains the focus between different [Picker]s by using [PickerGroupState]. It can
 * be handled from outside the component using the same instance and its properties.
 * When touch exploration services are enabled, the focus moves to the picker which is clicked. To
 * handle clicks in a different manner, use the [onSelected] lambda to control the focus of talkback
 * and actual focus.
 *
 * It is recommended to ensure that a [Picker] in non read only mode should have user scroll enabled
 * when touch exploration services are running.
 *
 * Example of a sample picker group with an hour and minute picker (24 hour format)
 * @sample androidx.wear.compose.material.samples.PickerGroup24Hours
 *
 * Example of an auto centering picker group where the total width exceeds screen's width
 * @sample androidx.wear.compose.material.samples.AutoCenteringPickerGroup
 *
 * @param pickers List of [Picker]s represented using [PickerGroupItem] in the same order of
 * display from left to right.
 * @param modifier Modifier to be applied to the PickerGroup
 * @param pickerGroupState The state of the component
 * @param onSelected Action triggered when one of the [Picker] is selected inside the group
 * @param autoCenter Indicates whether the selected [Picker] should be centered on the screen. It is
 * recommended to set this as true when all the pickers cannot be fit into the screen. Or provide a
 * mechanism to navigate to pickers which are not visible on screen. If false, the whole row
 * containing pickers would be centered.
 * @param propagateMinConstraints Whether the incoming min constraints should be passed to content.
 * @param touchExplorationStateProvider A [TouchExplorationStateProvider] to provide the current
 * state of touch exploration service. This will be used to determine how the PickerGroup and
 * talkback focus behaves/reacts to click and scroll events.
 * @param separator A composable block which describes the separator between different [Picker]s.
 * The integer parameter to the composable depicts the index where it will be kept. For example, 0
 * would represent the separator between the first and second picker.
 */
@OptIn(ExperimentalWearFoundationApi::class)
@Composable
internal fun PickerGroup(
    vararg pickers: PickerGroupItem,
    modifier: Modifier = Modifier,
    pickerGroupState: PickerGroupState = rememberPickerGroupState(),
    onSelected: (selectedIndex: Int) -> Unit = {},
    autoCenter: Boolean = true,
    expandToFillWidth: Boolean = false,
    touchExplorationStateProvider: TouchExplorationStateProvider =
        DefaultTouchExplorationStateProvider(),
    separator: (@Composable (Int) -> Unit)? = null,
) {
    val touchExplorationServicesEnabled by touchExplorationStateProvider.touchExplorationState()

    Row(
        modifier = modifier
            .then(
                // When touch exploration services are enabled, send the scroll events on the parent
                // composable to selected picker
                if (touchExplorationServicesEnabled &&
                    pickerGroupState.selectedIndex in pickers.indices
                ) {
                    Modifier.scrollablePicker(
                        pickers[pickerGroupState.selectedIndex].pickerState,
                    )
                } else {
                    Modifier
                },
            ).alignToAutoCenterTarget(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = if (expandToFillWidth) Arrangement.SpaceBetween else Arrangement.Center,
    ) {
        // When no Picker is selected, provide an empty composable as a placeholder
        // and tell the HierarchicalFocusCoordinator to clear the focus.
        HierarchicalFocusCoordinator(requiresFocus = {
            !pickers.indices.contains(pickerGroupState.selectedIndex)
        }) {}
        pickers.forEachIndexed { index, pickerData ->
            val pickerSelected = index == pickerGroupState.selectedIndex
            val flingBehavior = PickerDefaults.flingBehavior(state = pickerData.pickerState)
            HierarchicalFocusCoordinator(requiresFocus = { pickerSelected }) {
                val focusRequester = pickerData.focusRequester ?: rememberActiveFocusRequester()
                Picker(
                    state = pickerData.pickerState,
                    contentDescription = pickerData.contentDescription,
                    readOnly = !pickerSelected,
                    modifier = pickerData.modifier
                        .then(
                            // If auto center is enabled, apply auto centering modifier on selected
                            // picker to center it
                            if (pickerSelected && autoCenter) {
                                Modifier.autoCenteringTarget()
                            } else {
                                Modifier
                            },
                        )
                        .focusRequester(focusRequester)
                        .focusable(),
                    readOnlyLabel = pickerData.readOnlyLabel,
                    flingBehavior = flingBehavior,
                    onSelected = pickerData.onSelected,
                    userScrollEnabled = !touchExplorationServicesEnabled || pickerSelected,
                    option = { optionIndex ->
                        with(pickerData) {
                            Box(
                                if (touchExplorationServicesEnabled || pickerSelected) {
                                    Modifier
                                } else {
                                    Modifier.pointerInput(Unit) {
                                        coroutineScope {
                                            // Keep looking for touch events on the picker if it is not
                                            // selected
                                            while (true) {
                                                awaitEachGesture {
                                                    awaitFirstDown(requireUnconsumed = false)
                                                    pickerGroupState.selectedIndex = index
                                                    onSelected(index)
                                                }
                                            }
                                        }
                                    }
                                },
                            ) {
                                option(optionIndex, pickerSelected)
                            }
                        }
                    },
                )
            }
            if (index < pickers.size - 1) {
                separator?.invoke(index)
            }
        }
    }
}

/**
 * Creates a [PickerGroupState] that is remembered across compositions.
 *
 * @param initiallySelectedIndex the picker index that will be initially focused
 */
@Composable
internal fun rememberPickerGroupState(
    initiallySelectedIndex: Int = 0,
): PickerGroupState = rememberSaveable(
    initiallySelectedIndex,
    saver = PickerGroupState.Saver,
) {
    PickerGroupState(initiallySelectedIndex)
}

/**
 * A state object that can be used to observe the selected [Picker].
 *
 * @param initiallySelectedIndex the picker index that will be initially selected
 */
internal class PickerGroupState constructor(
    initiallySelectedIndex: Int = 0,
) {

    /**
     * The current selected [Picker] index.
     */
    var selectedIndex by mutableIntStateOf(initiallySelectedIndex)

    internal companion object {
        val Saver = listSaver<PickerGroupState, Any?>(
            save = {
                listOf(
                    it.selectedIndex,
                )
            },
            restore = { saved ->
                PickerGroupState(
                    initiallySelectedIndex = saved[0] as Int,
                )
            },
        )
    }
}

/**
 * A class for representing [Picker] which will be composed inside a [PickerGroup].
 *
 * @param pickerState The state of the picker
 * @param modifier Modifier to be applied to the Picker
 * @param contentDescription Text used by accessibility services to describe what the
 * selected option represents. This text should be localized, such as by using
 * [androidx.compose.ui.res.stringResource] or similar. Typically, the content description is
 * inferred via derivedStateOf to avoid unnecessary recompositions, like this:
 * val description by remember { derivedStateOf { /* expression using state.selectedOption */ } }
 * @param focusRequester Optional [FocusRequester] for the [Picker]. If not provided, a local
 * instance of [FocusRequester] will be created to handle the focus between different pickers
 * @param onSelected Action triggered when the Picker is selected by clicking
 * @param readOnlyLabel A slot for providing a label, displayed above the selected option
 * when the [Picker] is read-only. The label is overlaid with the currently selected
 * option within a Box, so it is recommended that the label is given [Alignment.TopCenter].
 * @param option A block which describes the content. The integer parameter to the composable
 * denotes the index of the option and boolean denotes whether the picker is selected or not.
 */
internal class PickerGroupItem(
    val pickerState: PickerState,
    val modifier: Modifier = Modifier,
    val contentDescription: String? = null,
    val focusRequester: FocusRequester? = null,
    val onSelected: () -> Unit = {},
    val readOnlyLabel: @Composable (BoxScope.() -> Unit)? = null,
    val option: @Composable PickerScope.(optionIndex: Int, pickerSelected: Boolean) -> Unit,
)

/**
 * A scrollable modifier which can be applied on a composable to propagate the scrollable events to
 * the specified [Picker] defined by the [PickerState].
 */
private fun Modifier.scrollablePicker(
    pickerState: PickerState,
) = composed {
    this.scrollable(
        state = pickerState,
        orientation = Orientation.Vertical,
        flingBehavior = PickerDefaults.flingBehavior(state = pickerState),
        reverseDirection = true,
    )
}

// Define a Vertical Alignment line at the center of this component to be used for autocenter.
internal fun Modifier.autoCenteringTarget() = this.layout { measurable, constraints ->
    val placeable = measurable.measure(constraints)
    layout(placeable.width, placeable.height, alignmentLines = mapOf(AutoCenteringLine to placeable.width / 2)) {
        placeable.place(0, 0)
    }
}

// Horizontally aligns the content of this component so that it's autocentering alignment line is
// at the center if this component. If no alignment line is defined, this is equivalent to
// centering.
// Vertically, it centers each item.
internal fun Modifier.alignToAutoCenterTarget() = layout { measurable, constraints ->
    val placeable = measurable.measure(constraints)
    val centeringTarget = placeable[AutoCenteringLine].let { if (it == AlignmentLine.Unspecified) placeable.width / 2 else it }
    val rowWidth =
        if (constraints.hasBoundedWidth) {
            constraints.maxWidth
        } else {
            constraints.minWidth
        }
    val rowHeight = placeable.height.coerceIn(constraints.minHeight, constraints.maxHeight)
    layout(rowWidth, rowHeight) {
        placeable.place(rowWidth / 2 - centeringTarget, (rowHeight - placeable.height) / 2)
    }
}

private val AutoCenteringLine: AlignmentLine = VerticalAlignmentLine(::min)
