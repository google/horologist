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

import androidx.compose.runtime.Composable
import androidx.wear.compose.material.Colors
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Scaffold
import com.google.android.horologist.audio.AudioOutput
import com.google.android.horologist.audio.VolumeState
import com.google.android.horologist.audio.ui.components.toAudioOutputUi
import com.google.android.horologist.audio.ui.mapper.VolumeUiStateMapper
import com.google.android.horologist.compose.tools.RoundPreview

@Composable
fun VolumeScreenTestCase(
    colors: Colors = MaterialTheme.colors,
    volumeState: VolumeState,
    audioOutput: AudioOutput
) {
    val volumeUiState = VolumeUiStateMapper.map(volumeState = volumeState)
    RoundPreview {
        MaterialTheme(colors = colors) {
            Scaffold(
                positionIndicator = {
                    VolumePositionIndicator(
                        volumeUiState = { volumeUiState }
                    )
                }
            ) {
                VolumeScreen(
                    volume = { volumeUiState },
                    audioOutputUi = audioOutput.toAudioOutputUi(),
                    increaseVolume = { },
                    decreaseVolume = { },
                    onAudioOutputClick = { },
                    showVolumeIndicator = false
                )
            }
        }
    }
}
