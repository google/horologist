/*
 * Copyright 2025 The Android Open Source Project
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

package com.google.android.horologist.audio.ui.material3

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.Modifier
import androidx.wear.compose.material3.ColorScheme
import androidx.wear.compose.material3.LevelIndicatorDefaults
import androidx.wear.compose.material3.MaterialTheme
import androidx.wear.compose.material3.StepperLevelIndicator
import com.google.android.horologist.audio.ui.VolumeUiState
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest

/**
 * A [LevelIndicator] that is tied to the system audio volume.
 *
 * Shows on the left side to match Bezel behaviour. If [displayIndicatorEvents] is non-null, the
 * indicator is initially hidden and only displays when that flow emits, then auto-hides after two
 * seconds.
 *
 * @param volumeUiState The current volume state.
 * @param modifier The [Modifier] to apply to the indicator.
 * @param displayIndicatorEvents If non-null, the indicator is initially hidden and only displays
 *   when this flow emits, then auto-hides after two seconds.
 * @param colorScheme The [ColorScheme] to use for the [LevelIndicator].
 */
@Composable
public fun VolumeLevelIndicator(
    volumeUiState: () -> VolumeUiState,
    modifier: Modifier = Modifier,
    displayIndicatorEvents: Flow<Unit>? = null,
    colorScheme: ColorScheme = MaterialTheme.colorScheme,
) {
    val visible by
    produceState(displayIndicatorEvents == null, displayIndicatorEvents) {
        displayIndicatorEvents?.collectLatest {
            value = true
            delay(2500)
            value = false
        }
    }
    val uiState = volumeUiState()

    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(animationSpec = MaterialTheme.motionScheme.fastEffectsSpec()),
        exit = fadeOut(animationSpec = MaterialTheme.motionScheme.slowEffectsSpec()),
    ) {
        StepperLevelIndicator(
            modifier = modifier.fillMaxSize(),
            value = { uiState.current.toFloat() },
            valueRange = (uiState.min.toFloat())..(uiState.max.toFloat()),
            colors =
                LevelIndicatorDefaults.colors(
                    indicatorColor = colorScheme.secondaryDim,
                    trackColor = colorScheme.surfaceContainer,
                    disabledIndicatorColor = colorScheme.onSurface.toDisabledColor(DisabledContentAlpha),
                    disabledTrackColor = colorScheme.onSurface.toDisabledColor(DisabledContainerAlpha),
                ),
        )
    }
}
