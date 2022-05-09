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

package com.google.android.horologist.audioui

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import com.google.android.horologist.audio.AudioOutput
import com.google.android.horologist.audio.ExperimentalHorologistAudioApi
import com.google.android.horologist.audio.VolumeState

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
@OptIn(ExperimentalHorologistAudioApi::class, ExperimentalHorologistAudioUiApi::class)
@Composable
fun VolumeScreenPreview() {
    val volume = VolumeState(5, 10)
    val audioOutput = AudioOutput.BluetoothHeadset(id = "1", name = "EarBuds")

    VolumeScreen(
        volume = { volume },
        audioOutput = audioOutput,
        increaseVolume = { /*TODO*/ },
        decreaseVolume = { /*TODO*/ },
        onAudioOutputClick = {},
    )
}
