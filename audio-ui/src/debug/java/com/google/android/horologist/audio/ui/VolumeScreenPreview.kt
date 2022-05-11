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

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.wear.compose.material.Scaffold
import com.google.android.horologist.audio.AudioOutput
import com.google.android.horologist.audio.VolumeState

@Preview(
    device = Devices.WEAR_OS_LARGE_ROUND,
    showSystemUi = true,
    backgroundColor = 0xff000000,
    showBackground = true
)
@Composable
@OptIn(ExperimentalHorologistAudioUiApi::class)
fun VolumeScreenGuide() {
    val volume = VolumeState(10, 10)

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            positionIndicator = {
                VolumePositionIndicator(
                    volumeState = { volume.copy(current = 5) },
                    autoHide = false
                )
            }
        ) {
            VolumeScreen(
                volume = { volume },
                audioOutput = AudioOutput.BluetoothHeadset(id = "1", name = "PixelBuds"),
                increaseVolume = { },
                decreaseVolume = { },
                onAudioOutputClick = {},
            )
        }
        VolumeScreenUxGuide()
    }
}

@Preview(
    device = Devices.WEAR_OS_LARGE_ROUND,
    showSystemUi = true,
    backgroundColor = 0xff000000,
    showBackground = true
)
@Preview(
    device = Devices.WEAR_OS_SMALL_ROUND,
    showSystemUi = true,
    backgroundColor = 0xff000000,
    showBackground = true
)
@Preview(
    device = Devices.WEAR_OS_SQUARE,
    showSystemUi = true,
    backgroundColor = 0xff000000,
    showBackground = true
)
@Composable
@OptIn(ExperimentalHorologistAudioUiApi::class)
fun VolumeScreenPreview(
    @PreviewParameter(AudioOutputProvider::class) audioOutput: AudioOutput
) {
    val volume = VolumeState(5, 10)

    Scaffold(
        positionIndicator = {
            VolumePositionIndicator(
                volumeState = { volume },
                autoHide = false
            )
        }
    ) {
        VolumeScreen(
            volume = { volume },
            audioOutput = audioOutput,
            increaseVolume = { },
            decreaseVolume = { },
            onAudioOutputClick = {},
        )
    }
}

@OptIn(ExperimentalHorologistAudioUiApi::class)
class AudioOutputProvider : PreviewParameterProvider<AudioOutput> {
    override val values = sequenceOf(
        AudioOutput.BluetoothHeadset(id = "1", name = "PixelBuds"),
        AudioOutput.WatchSpeaker(id = "2", name = "Galaxy Watch 4"),
        AudioOutput.BluetoothHeadset(id = "3", name = "Sennheiser Momentum Wireless"),
    )
}
