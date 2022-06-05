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

@file:OptIn(ExperimentalHorologistAudioApi::class)

package com.google.android.horologist.audio.ui

import androidx.wear.compose.material.MaterialTheme
import app.cash.paparazzi.Paparazzi
import com.google.android.horologist.audio.AudioOutput
import com.google.android.horologist.audio.ExperimentalHorologistAudioApi
import com.google.android.horologist.audio.VolumeState
import com.google.android.horologist.paparazzi.GALAXY_WATCH4_CLASSIC_LARGE
import com.google.android.horologist.paparazzi.WearSnapshotHandler
import com.google.android.horologist.paparazzi.determineHandler
import org.junit.Rule
import org.junit.Test

class VolumeScreen2Test() {
    @get:Rule
    val paparazzi = Paparazzi(
        deviceConfig = GALAXY_WATCH4_CLASSIC_LARGE,
        theme = "android:ThemeOverlay.Material.Dark",
        maxPercentDifference = 0.0,
        snapshotHandler = WearSnapshotHandler(determineHandler(0.1))
    )

    @Test
    fun volumeScreenAtMinimum() {
        val volumeState = VolumeState(
            current = 0,
            max = 100,
        )
        val audioOutput = AudioOutput.BluetoothHeadset("id", "Pixelbuds")

        paparazzi.snapshot {
            VolumeScreenTestCase(
                colors = MaterialTheme.colors,
                volumeState = volumeState,
                audioOutput = audioOutput
            )
        }
    }

    @Test
    fun volumeScreenAtMaximum() {
        val volumeState = VolumeState(
            current = 100,
            max = 100,
        )
        val audioOutput = AudioOutput.BluetoothHeadset("id", "Pixelbuds")

        paparazzi.snapshot {
            VolumeScreenTestCase(
                colors = MaterialTheme.colors,
                volumeState = volumeState,
                audioOutput = audioOutput
            )
        }
    }
}
