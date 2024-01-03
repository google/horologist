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

package com.google.android.horologist.compose.material

import androidx.compose.foundation.layout.BoxScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.util.lerp
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.StepperDefaults
import androidx.wear.compose.material.contentColorFor
import com.google.android.horologist.compose.rotaryinput.RotaryDefaults
import com.google.android.horologist.compose.rotaryinput.onRotaryInputAccumulatedWithFocus
import com.google.android.horologist.images.base.paintable.ImageVectorPaintable.Companion.asPaintable
import kotlin.math.roundToInt
/**
 * Wrapper for androidx.wear.compose.material.Stepper with default RSB scroll support.
 *
 * @see androidx.wear.compose.material.Stepper
 */
@Composable
public fun Stepper(
    value: Float,
    onValueChange: (Float) -> Unit,
    steps: Int,
    modifier: Modifier = Modifier,
    decreaseIcon: @Composable () -> Unit = {
        Icon(
            StepperDefaults.Decrease.asPaintable(),
            stringResource(R.string.horologist_stepper_decrease_content_description),
        )
    },
    increaseIcon: @Composable () -> Unit = {
        Icon(
            StepperDefaults.Increase.asPaintable(),
            stringResource(R.string.horologist_stepper_increase_content_description),
        )
    },
    valueRange: ClosedFloatingPointRange<Float> = 0f..(steps + 1).toFloat(),
    backgroundColor: Color = MaterialTheme.colors.background,
    contentColor: Color = contentColorFor(backgroundColor),
    iconColor: Color = contentColor,
    enableRangeSemantics: Boolean = true,
    content: @Composable BoxScope.() -> Unit,
) {
    val isLowRes = RotaryDefaults.isLowResInput()

    val currentStep = remember(value, valueRange, steps) {
        snapValueToStep(
            value,
            valueRange,
            steps,
        )
    }

    val updateValue: (Int) -> Unit = { stepDiff ->
        val newValue = calculateCurrentStepValue(currentStep + stepDiff, steps, valueRange)
        if (newValue != value) onValueChange(newValue)
    }

    androidx.wear.compose.material.Stepper(
        value,
        onValueChange,
        steps,
        decreaseIcon,
        increaseIcon,
        modifier.onRotaryInputAccumulatedWithFocus(isLowRes = isLowRes, onValueChange = {
            if (it < 0f) {
                updateValue(1)
            } else if (it > 0f) {
                updateValue(-1)
            }
        }),
        valueRange,
        backgroundColor,
        contentColor,
        iconColor,
        enableRangeSemantics,
        content,
    )
}

/**
 * Wrapper for androidx.wear.compose.material.Stepper with default RSB scroll support.
 *
 * @see androidx.wear.compose.material.Stepper
 */
@Composable
public fun Stepper(
    value: Int,
    onValueChange: (Int) -> Unit,
    valueProgression: IntProgression,
    modifier: Modifier = Modifier,
    decreaseIcon: @Composable () -> Unit = {
        Icon(
            StepperDefaults.Decrease.asPaintable(),
            stringResource(R.string.horologist_stepper_decrease_content_description),
        )
    },
    increaseIcon: @Composable () -> Unit = {
        Icon(
            StepperDefaults.Increase.asPaintable(),
            stringResource(R.string.horologist_stepper_increase_content_description),
        )
    },
    backgroundColor: Color = MaterialTheme.colors.background,
    contentColor: Color = contentColorFor(backgroundColor),
    iconColor: Color = contentColor,
    enableRangeSemantics: Boolean = true,
    content: @Composable BoxScope.() -> Unit,
) {
    val isLowRes = RotaryDefaults.isLowResInput()
    androidx.wear.compose.material.Stepper(
        value,
        onValueChange,
        valueProgression,
        decreaseIcon,
        increaseIcon,
        modifier.onRotaryInputAccumulatedWithFocus(isLowRes = isLowRes, onValueChange = {
            if (it < 0f) {
                val newValue = (value + valueProgression.step)
                if (newValue <= valueProgression.last) {
                    onValueChange(newValue)
                }
            } else if (it > 0f) {
                val newValue = (value - valueProgression.step)
                if (newValue >= valueProgression.first) {
                    onValueChange(newValue)
                }
            }
        }),
        backgroundColor,
        contentColor,
        iconColor,
        enableRangeSemantics,
        content,
    )
}

/**
 * Calculates value of [currentStep] in [valueRange] depending on number of [steps]
 */
internal fun calculateCurrentStepValue(
    currentStep: Int,
    steps: Int,
    valueRange: ClosedFloatingPointRange<Float>,
): Float = lerp(
    valueRange.start,
    valueRange.endInclusive,
    currentStep.toFloat() / (steps + 1).toFloat(),
).coerceIn(valueRange)

/**
 * Snaps [value] to the closest [step] in the [valueRange]
 */
internal fun snapValueToStep(
    value: Float,
    valueRange: ClosedFloatingPointRange<Float>,
    steps: Int,
): Int = ((value - valueRange.start) / (valueRange.endInclusive - valueRange.start) * (steps + 1)).roundToInt()
    .coerceIn(0, steps + 1)
