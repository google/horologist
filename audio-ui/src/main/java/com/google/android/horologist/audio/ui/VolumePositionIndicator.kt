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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.PositionIndicator
import com.google.android.horologist.audio.VolumeState
import kotlinx.coroutines.delay

/**
 * A PositionIndicator that is tied to the system audio volume.
 *
 * Shows on the left side to match Bezel behaviour and shows
 * for 2 seconds after any volume change, including when a new
 * output device is selected.
 */
@Composable
public fun VolumePositionIndicator(
    volumeState: () -> VolumeState,
    modifier: Modifier = Modifier,
    autoHide: Boolean = true
) {
    var actuallyVisible by remember { mutableStateOf(!autoHide) }
    var isInitial by remember { mutableStateOf(true) }

    if (autoHide) {
        val current = volumeState().current
        val updating = volumeState().updating
        LaunchedEffect(current, updating) {
            if (isInitial) {
                isInitial = false
            } else {
                actuallyVisible = true
                delay(2000)
                actuallyVisible = false
                volumeState().updating = false
            }
        }
    }

    AnimatedVisibility(
        visible = actuallyVisible,
        enter = fadeIn(),
        exit = fadeOut()
    ) {
        PositionIndicator(
            modifier = modifier,
            // RSB indicator uses secondary colors (surface/onSurface)
            color = MaterialTheme.colors.secondary,
            value = {
                volumeState().current.toFloat()
            },
            range = 0F.rangeTo(
                volumeState().max.toFloat()
            )
        )
    }
}
