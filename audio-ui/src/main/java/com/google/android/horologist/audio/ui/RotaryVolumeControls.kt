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

import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import com.google.android.horologist.compose.navscaffold.ExperimentalHorologistComposeLayoutApi
import com.google.android.horologist.compose.rotaryinput.RotaryInputConfigDefaults.RATE_LIMITING_DISABLED
import com.google.android.horologist.compose.rotaryinput.onRotaryInputAccumulatedWithFocus

@ExperimentalHorologistComposeLayoutApi
public fun Modifier.rotaryVolumeControls(
    focusRequester: FocusRequester,
    volumeFractionPerPixel: Float = RotaryVolumeConfigDefaults.DEFAULT_VOLUME_FRACTION_PER_PIXEL,
    onRotaryVolumeInput: (Float) -> Unit
): Modifier =
    onRotaryInputAccumulatedWithFocus(
        focusRequester = focusRequester,
        rateLimitCoolDownMs = RATE_LIMITING_DISABLED
    ) { change ->
        if (change != 0f) {
            onRotaryVolumeInput(change * volumeFractionPerPixel)
        }
    }

public object RotaryVolumeConfigDefaults {
    public const val DEFAULT_VOLUME_FRACTION_PER_PIXEL: Float = 0.001f
}
