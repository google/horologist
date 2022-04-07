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

@file:OptIn(ExperimentalAudioUiApi::class, ExperimentalAudioApi::class)

package com.google.android.horologist.audioui

import app.cash.paparazzi.DeviceConfig.Companion.PIXEL_5
import app.cash.paparazzi.Paparazzi
import com.google.android.horologist.audio.AudioOutput
import com.google.android.horologist.audio.ExperimentalAudioApi
import com.google.android.horologist.audio.VolumeState
import org.junit.Rule
import org.junit.Test

class VolumeScreenTest {
    @get:Rule
    val paparazzi = Paparazzi(
        deviceConfig = PIXEL_5,
        theme = "android:Theme.Material.Light.NoActionBar"
        // ...see docs for more options
    )

    @Test
    fun compose() {
        paparazzi.snapshot {
            VolumeScreen(
                volume = {
                    VolumeState(
                        current = 50,
                        min = 0,
                        max = 100,
                        isMute = false
                    )
                },
                audioOutput = AudioOutput.BluetoothHeadset("id", "Pixelbuds"),
                increaseVolume = { },
                decreaseVolume = { },
                onAudioOutputClick = { })
        }
    }
}
