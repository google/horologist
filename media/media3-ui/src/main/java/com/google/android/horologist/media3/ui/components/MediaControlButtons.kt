/*
 * Copyright 2024 The Android Open Source Project
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

package com.google.android.horologist.media3.ui.components

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.media3.common.Player
import androidx.wear.compose.material.ButtonColors
import com.google.android.horologist.annotations.ExperimentalHorologistApi
import com.google.android.horologist.media.ui.components.ControlButtonLayout
import com.google.android.horologist.media.ui.components.controls.MediaButtonDefaults
import com.google.android.horologist.media.ui.util.isLargeScreen
import com.google.android.horologist.media3.ui.components.controls.SeekToNextButton
import com.google.android.horologist.media3.ui.components.controls.SeekToPreviousButton

@ExperimentalHorologistApi
@Composable
public fun MediaControlButtons(
    player: Player,
    modifier: Modifier = Modifier,
    colors: ButtonColors = MediaButtonDefaults.mediaButtonDefaultColors,
) {
    val playPauseSize = if (LocalConfiguration.current.isLargeScreen) 38.dp else 32.dp
    ControlButtonLayout(
        leftButton = { SeekToPreviousButton(player, Modifier.fillMaxSize(), colors) },
        middleButton = {
            // TODO: show progress
            PlayPauseButton(player, Modifier.fillMaxSize(), colors, playPauseSize)
        },
        rightButton = { SeekToNextButton(player, Modifier.fillMaxSize(), colors) },
        modifier = modifier
    )
}