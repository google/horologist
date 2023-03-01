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

@file:OptIn(ExperimentalHorologistAudioUiApi::class)

package com.google.android.horologist.audio.ui.components.animated

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.google.android.horologist.audio.ui.ExperimentalHorologistAudioUiApi
import com.google.android.horologist.audio.ui.VolumeScreen
import com.google.android.horologist.audio.ui.state.model.VolumeUiState
import com.google.android.horologist.compose.tools.WearSmallRoundDevicePreview

@WearSmallRoundDevicePreview
@Composable
fun AnimatedSetVolumeButtonPreview() {
    var volumeUiState by remember { mutableStateOf(VolumeUiState(0.6f)) }

    InteractivePreviewAware {
        VolumeScreen(
            volumeUiState = volumeUiState,
            increaseVolume = {
                volumeUiState =
                    VolumeUiState((volumeUiState.current!! + 0.2f).coerceAtMost(1f))
            },
            decreaseVolume = {
                volumeUiState =
                    VolumeUiState((volumeUiState.current!! - 0.2f).coerceAtLeast(0f))
            },
            contentSlot = {
                AnimatedSetVolumeButton(onVolumeClick = { }, volumeUiState = volumeUiState)
            }
        )
    }
}
