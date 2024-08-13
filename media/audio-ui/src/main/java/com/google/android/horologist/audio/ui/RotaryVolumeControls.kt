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

package com.google.android.horologist.audio.ui

import android.util.Log
import android.view.HapticFeedbackConstants
import android.view.View
import androidx.annotation.VisibleForTesting
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalView
import androidx.wear.compose.foundation.rotary.RotaryScrollableBehavior
import com.google.android.horologist.compose.rotaryinput.RotaryDefaults.isLowResInput
import com.google.android.horologist.compose.rotaryinput.RotaryInputConfigDefaults.RATE_LIMITING_DISABLED
import com.google.android.horologist.compose.rotaryinput.accumulatedBehavior
import kotlin.math.max
import kotlin.math.roundToInt

// Map 1 pixel to 0.1% volume change
private const val VOLUME_FRACTION_PER_PIXEL: Float = 0.001f
private const val VOLUME_PERCENT_CHANGE_PIXEL: Float = 48f

private const val TAG = "HorologistAudioUi"

@Composable
public fun volumeRotaryBehavior(
    volumeUiStateProvider: () -> VolumeUiState,
    onRotaryVolumeInput: (Int) -> Unit,
): RotaryScrollableBehavior {
    return if (isLowResInput()) {
        lowResVolumeRotaryBehavior(
            volumeUiStateProvider = volumeUiStateProvider,
            onRotaryVolumeInput = onRotaryVolumeInput,
        )
    } else {
        highResVolumeRotaryBehavior(
            volumeUiStateProvider = volumeUiStateProvider,
            onRotaryVolumeInput = onRotaryVolumeInput,
        )
    }
}

@Composable
public fun lowResVolumeRotaryBehavior(
    volumeUiStateProvider: () -> VolumeUiState,
    onRotaryVolumeInput: (Int) -> Unit,
): RotaryScrollableBehavior {
    val localView = LocalView.current

    return accumulatedBehavior(rateLimitCoolDownMs = RATE_LIMITING_DISABLED) { change ->
        Log.d(TAG, "maxVolume=${volumeUiStateProvider().max}")

        if (change != 0f) {
            val targetVolume =
                (volumeUiStateProvider().current + change.toInt()).coerceIn(
                    0,
                    volumeUiStateProvider().max,
                )

            Log.d(
                TAG,
                "change=$change, " +
                    "currentVolume=${volumeUiStateProvider().current}, " +
                    "targetVolume=$targetVolume ",
            )

            performHapticFeedback(
                targetVolume = targetVolume,
                volumeUiStateProvider = volumeUiStateProvider,
                localView = localView,
            )

            onRotaryVolumeInput(targetVolume)
        }
    }
}

@Composable
public fun highResVolumeRotaryBehavior(
    volumeUiStateProvider: () -> VolumeUiState,
    onRotaryVolumeInput: (Int) -> Unit,
): RotaryScrollableBehavior {
    val localView = LocalView.current

    return accumulatedBehavior(rateLimitCoolDownMs = RATE_LIMITING_DISABLED) { change ->
        Log.d(TAG, "maxVolume=${volumeUiStateProvider().max}")

        if (change != 0f) {
            val targetVolume = convertPixelToVolume(change, volumeUiStateProvider)

            Log.d(
                TAG,
                "change=$change, " +
                    "currentVolume=${volumeUiStateProvider().current}, " +
                    "targetVolume=$targetVolume ",
            )

            performHapticFeedback(
                targetVolume = targetVolume,
                volumeUiStateProvider = volumeUiStateProvider,
                localView = localView,
            )

            onRotaryVolumeInput(targetVolume)
        }
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
        max(
            volumeUiStateProvider().max * VOLUME_FRACTION_PER_PIXEL,
            1 / VOLUME_PERCENT_CHANGE_PIXEL,
        )

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
    localView: View,
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
