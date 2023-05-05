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

@file:OptIn(ExperimentalWearFoundationApi::class)

package com.google.android.horologist.audio.ui

import android.util.Log
import android.view.HapticFeedbackConstants
import android.view.View
import androidx.annotation.VisibleForTesting
import androidx.compose.foundation.focusable
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.wear.compose.foundation.ExperimentalWearFoundationApi
import androidx.wear.compose.foundation.RequestFocusWhenActive
import androidx.wear.compose.foundation.rememberActiveFocusRequester
import com.google.android.horologist.annotations.ExperimentalHorologistApi
import com.google.android.horologist.compose.rotaryinput.RotaryInputConfigDefaults.RATE_LIMITING_DISABLED
import com.google.android.horologist.compose.rotaryinput.onRotaryInputAccumulated
import kotlin.math.max
import kotlin.math.roundToInt

// Map 1 pixel to 0.1% volume change
private const val VOLUME_FRACTION_PER_PIXEL: Float = 0.001f
private const val VOLUME_PERCENT_CHANGE_PIXEL: Float = 48f

private const val TAG = "HorologistAudioUi"

@ExperimentalHorologistApi
public fun Modifier.rotaryVolumeControlsWithFocus(
    focusRequester: FocusRequester? = null,
    volumeUiStateProvider: () -> VolumeUiState,
    onRotaryVolumeInput: (Int) -> Unit,
    localView: View,
    isLowRes: Boolean
): Modifier = composed {
    val localFocusRequester = focusRequester ?: rememberActiveFocusRequester()
    RequestFocusWhenActive(localFocusRequester)

    rotaryVolumeControls(
        volumeUiStateProvider = volumeUiStateProvider,
        onRotaryVolumeInput = onRotaryVolumeInput,
        localView = localView,
        isLowRes = isLowRes
    )
        .focusRequester(localFocusRequester)
        .focusable()
}

@ExperimentalHorologistApi
public fun Modifier.rotaryVolumeControls(
    volumeUiStateProvider: () -> VolumeUiState,
    onRotaryVolumeInput: (Int) -> Unit,
    localView: View,
    isLowRes: Boolean
): Modifier = composed {
    onRotaryInputAccumulated(
        rateLimitCoolDownMs = RATE_LIMITING_DISABLED,
        isLowRes = isLowRes
    ) { change ->
        Log.d(TAG, "maxVolume=${volumeUiStateProvider().max}, " + "isLowRes=$isLowRes ")

        if (change != 0f) {
            val targetVolume = if (isLowRes) {
                (volumeUiStateProvider().current + change.toInt()).coerceIn(0, volumeUiStateProvider().max)
            } else {
                convertPixelToVolume(change, volumeUiStateProvider)
            }

            Log.d(
                TAG,
                "change=$change, " +
                    "currentVolume=${volumeUiStateProvider().current}, " +
                    "targetVolume=$targetVolume "
            )

            if (targetVolume != volumeUiStateProvider().current) {
                if (targetVolume == volumeUiStateProvider().min || targetVolume == volumeUiStateProvider().max) {
                    // Use stronger haptic feedback when reaching max or min
                    localView.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)
                } else {
                    localView.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                }
            }

            onRotaryVolumeInput(targetVolume)
        }
    }
}

// Conversino of pixels to volume is for device with high resolution rotary only.
@VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
internal fun convertPixelToVolume(change: Float, volumeUiStateProvider: () -> VolumeUiState): Int {
    // Map pixel changes to 0.1% volume change. However, when max volume is small, we make sure to use
    // the threshold VOLUME_FRACTION_PER_PIXEL to trigger at least one volume change, otherwise the
    // devices with max volume that are less than ~20 would need significant quicker turning to
    // trigger a volume change (which is bad experience).
    val scale =
        max(volumeUiStateProvider().max * VOLUME_FRACTION_PER_PIXEL, 1 / VOLUME_PERCENT_CHANGE_PIXEL)

    return (volumeUiStateProvider().current + (change * scale).roundToInt())
        .coerceIn(0, volumeUiStateProvider().max)
}
