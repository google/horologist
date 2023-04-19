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

package com.google.android.horologist.audio.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.Modifier
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.PositionIndicator
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest

/**
 * A PositionIndicator that is tied to the system audio volume.
 *
 * Shows on the left side to match Bezel behaviour. If [displayIndicatorEvents] is non-null, the
 * indicator is initially hidden and only displays when that flow emits, then auto-hides after two
 * seconds.
 */
@Composable
public fun VolumePositionIndicator(
    volumeUiState: () -> VolumeUiState,
    modifier: Modifier = Modifier,
    displayIndicatorEvents: Flow<Unit>? = null
) {
    val visible by produceState(displayIndicatorEvents == null, displayIndicatorEvents) {
        displayIndicatorEvents?.collectLatest {
            value = true
            delay(2000)
            value = false
        }
    }
    val uiState = volumeUiState()

    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(),
        exit = fadeOut()
    ) {
        PositionIndicator(
            modifier = modifier,
            // RSB indicator uses secondary colors (surface/onSurface)
            color = MaterialTheme.colors.secondary,
            value = {
                uiState.current.toFloat()
            },
            range = uiState.min.toFloat().rangeTo(
                uiState.max.toFloat()
            )
        )
    }
}
