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

/**
 * A Focusable modifier, that depending on rotary resolution (by [isLowRes] parameter), accumulates
 * the input by [onRotaryInputAccumulated] modifier, and converts the accumulated input into a
 * target volume to pass into [onRotaryVolumeInput] for a corresponding volume change.
 */
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

    if (isLowRes) {
        lowResRotaryVolumeControls(
            volumeUiStateProvider = volumeUiStateProvider,
            onRotaryVolumeInput = onRotaryVolumeInput,
            localView = localView
        )
    } else {
        highResRotaryVolumeControls(
            volumeUiStateProvider = volumeUiStateProvider,
            onRotaryVolumeInput = onRotaryVolumeInput,
            localView = localView
        )
    }
        .focusRequester(localFocusRequester)
        .focusable()
}

/**
 * A low resolution rotary volume modifier, that treats the accumulated input with 1:1 volume
 * change to pass into [onRotaryVolumeInput] for a corresponding volume change. E.g. 2f change
 * would increase volume by 2 and -3f change would decrease volume by 3.
 */
@ExperimentalHorologistApi
public fun Modifier.lowResRotaryVolumeControls(
    volumeUiStateProvider: () -> VolumeUiState,
    onRotaryVolumeInput: (Int) -> Unit,
    localView: View
): Modifier = onRotaryInputAccumulated(
    rateLimitCoolDownMs = RATE_LIMITING_DISABLED,
    isLowRes = true
) { change ->
    Log.d(TAG, "maxVolume=${volumeUiStateProvider().max}")

    if (change != 0f) {
        val targetVolume =
            (volumeUiStateProvider().current + change.toInt()).coerceIn(0, volumeUiStateProvider().max)

        Log.d(
            TAG,
            "change=$change, " +
                "currentVolume=${volumeUiStateProvider().current}, " +
                "targetVolume=$targetVolume "
        )

        performHapticFeedback(
            targetVolume = targetVolume,
            volumeUiStateProvider = volumeUiStateProvider,
            localView = localView
        )

        onRotaryVolumeInput(targetVolume)
    }
}

/**
 * A high resolution rotary volume modifier, that uses [convertPixelToVolume] to convert
 * accumulated scrolled pixels to volume to pass into [onRotaryVolumeInput] for a corresponding
 * volume change
 */
@ExperimentalHorologistApi
public fun Modifier.highResRotaryVolumeControls(
    volumeUiStateProvider: () -> VolumeUiState,
    onRotaryVolumeInput: (Int) -> Unit,
    localView: View
): Modifier = onRotaryInputAccumulated(
    rateLimitCoolDownMs = RATE_LIMITING_DISABLED,
    isLowRes = false
) { change ->
    Log.d(TAG, "maxVolume=${volumeUiStateProvider().max}")

    if (change != 0f) {
        val targetVolume = convertPixelToVolume(change, volumeUiStateProvider)

        Log.d(
            TAG,
            "change=$change, " +
                "currentVolume=${volumeUiStateProvider().current}, " +
                "targetVolume=$targetVolume "
        )

        performHapticFeedback(
            targetVolume = targetVolume,
            volumeUiStateProvider = volumeUiStateProvider,
            localView = localView
        )

        onRotaryVolumeInput(targetVolume)
    }
}

/**
 * Converting of pixels to volume. Note this conversion is applicable to devices with high
 * resolution rotary only.
 *
 * Maps 1 pixel changes to 0.1% volume change. However, when max volume is small, we make sure to use
 * the threshold [VOLUME_FRACTION_PER_PIXEL] to trigger at least one volume change, otherwise the
 * devices with max volume that are less than ~20 would need significant quicker turning to
 * trigger a volume change (which is bad experience).
 */
@VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
internal fun convertPixelToVolume(change: Float, volumeUiStateProvider: () -> VolumeUiState): Int {
    val scale =
        max(volumeUiStateProvider().max * VOLUME_FRACTION_PER_PIXEL, 1 / VOLUME_PERCENT_CHANGE_PIXEL)

    return (volumeUiStateProvider().current + (change * scale).roundToInt())
        .coerceIn(0, volumeUiStateProvider().max)
}

/**
 * Performs haptic feedback on the view. If there is a change in the volume, returns a strong
 * feedback [HapticFeedbackConstants.LONG_PRESS] if reaching the limit (either max or min) of the
 * volume range, otherwise returns [HapticFeedbackConstants.KEYBOARD_TAP]
 */
private fun performHapticFeedback(
    targetVolume: Int,
    volumeUiStateProvider: () -> VolumeUiState,
    localView: View
) {
    if (targetVolume != volumeUiStateProvider().current) {
        if (targetVolume == volumeUiStateProvider().min || targetVolume == volumeUiStateProvider().max) {
            // Use stronger haptic feedback when reaching max or min
            localView.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)
        } else {
            localView.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
        }
    }
}
