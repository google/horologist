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

package com.google.android.horologist.composables

import androidx.compose.foundation.Indication
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.semantics.Role
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * This modifier provides functionality to increment or decrement values repeatedly by holding down
 * the composable. Should be used instead of clickable modifier to achieve clickable and repeatable
 * clickable behavior. Can't be used along with clickable modifier as it already implements it.
 *
 * Code from https://cs.android.com/androidx/platform/frameworks/support/+/androidx-main:wear/compose/compose-material-core/src/main/java/androidx/wear/compose/materialcore/RepeatableClickable.kt
 *
 * Callbacks [onClick] and [onLongRepeatableClick] are different. [onClick] is triggered only when the
 * hold duration is shorter than [initialDelay] and no repeatable clicks happened.
 * [onLongRepeatableClick] is repeatedly triggered when the hold duration is longer than [initialDelay]
 * with [incrementalDelay] intervals. If [onLongRepeatableClickEnd] is supplied, then it will be called following the onLongRepeatableClick call(s).
 *
 * @param interactionSource [MutableInteractionSource] that will be used to dispatch
 *   [PressInteraction.Press] when this clickable is pressed. Only the initial (first) press will be
 *   recorded and dispatched with [MutableInteractionSource].
 * @param indication indication to be shown when modified element is pressed. By default, indication
 *   from [LocalIndication] will be used. Pass `null` to show no indication, or current value from
 *   [LocalIndication] to show theme default
 * @param enabled Controls the enabled state. When `false`, [onClick], and this modifier will appear
 *   disabled for accessibility services
 * @param onClickLabel semantic / accessibility label for the [onClick] action
 * @param role the type of user interface element. Accessibility services might use this to describe
 *   the element or do customizations
 * @param initialDelay The initial delay before the click starts repeating, in ms
 * @param incrementalDelay The delay between each repeated click, in ms
 * @param onClick will be called when user clicks on the element
 * @param onLongRepeatableClick will be called after the [initialDelay] with [incrementalDelay] between
 *   each call until the touch is released
 * @param onLongRepeatableClickEnd will be called after the after the onLongRepeatableClick call(s).
 */
public fun Modifier.repeatableClickable(
    interactionSource: MutableInteractionSource,
    indication: Indication?,
    enabled: Boolean = true,
    onClickLabel: String? = null,
    role: Role? = null,
    initialDelay: Long = 500L,
    incrementalDelay: Long = 60L,
    onClick: () -> Unit,
    onLongRepeatableClick: () -> Unit = onClick,
    onLongRepeatableClickEnd: () -> Unit = {},
): Modifier = composed {
    val currentOnRepeatableClick by rememberUpdatedState(onLongRepeatableClick)
    val currentOnRepeatableClickEnd by rememberUpdatedState(onLongRepeatableClickEnd)
    val currentOnClick by rememberUpdatedState(onClick)
    // This flag is used for checking whether the onClick should be ignored or not.
    // If this flag is true, then it means that repeatable click happened and onClick
    // shouldn't be triggered.
    var repeatableClickTriggered by remember { mutableStateOf(false) }
    // Repeatable logic should always follow the clickable, as the lowest modifier finishes first,
    // and we have to be sure that repeatable goes before clickable.
    clickable(
        interactionSource = interactionSource,
        indication = indication,
        enabled = enabled,
        onClickLabel = onClickLabel,
        role = role,
        onClick = {
            if (!repeatableClickTriggered) {
                currentOnClick()
            }
            repeatableClickTriggered = false
        },
    )
        .pointerInput(enabled) {
            coroutineScope {
                awaitEachGesture {
                    awaitFirstDown()
                    repeatableClickTriggered = false
                    val repeatingJob = launch {
                        delay(initialDelay)
                        repeatableClickTriggered = true
                        while (enabled) {
                            currentOnRepeatableClick()
                            delay(incrementalDelay)
                        }
                    }
                    // Waiting for up or cancellation of the gesture.
                    waitForUpOrCancellation()
                    repeatingJob.cancel()
                    if (repeatableClickTriggered) {
                        currentOnRepeatableClickEnd()
                    }
                }
            }
        }
}
