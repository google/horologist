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

@file:OptIn(ExperimentalHorologistComposeToolsApi::class)

package com.google.android.horologist.media.ui

import androidx.compose.runtime.Composable
import app.cash.paparazzi.Paparazzi
import com.google.android.horologist.audio.AudioOutput
import com.google.android.horologist.audio.VolumeState
import com.google.android.horologist.audio.ui.VolumeScreen
import com.google.android.horologist.compose.tools.ExperimentalHorologistComposeToolsApi
import com.google.android.horologist.compose.tools.RoundPreview
import com.google.android.horologist.media.ui.uamp.UampTheme
import com.google.android.horologist.paparazzi.GALAXY_WATCH4_CLASSIC_LARGE
import com.google.android.horologist.paparazzi.WearSnapshotHandler
import com.google.android.horologist.paparazzi.determineHandler
import org.junit.Rule
import org.junit.Test

class FigmaVolumeScreenTest {
    @get:Rule
    val paparazzi = Paparazzi(
        deviceConfig = GALAXY_WATCH4_CLASSIC_LARGE,
        theme = "android:ThemeOverlay.Material.Dark",
        maxPercentDifference = 5.0,
        snapshotHandler = WearSnapshotHandler(determineHandler(5.0))
    )

    @Test
    fun volumePlayerScreen() {
        paparazzi.snapshot {
            UampRoundPreview {
                VolumeScreen(
                    volume = { VolumeState(5, 10) },
                    audioOutput = AudioOutput.BluetoothHeadset("1", "Device"),
                    increaseVolume = { },
                    decreaseVolume = { },
                    onAudioOutputClick = { }
                )
            }
        }
    }
}

@Composable
fun UampRoundPreview(content: @Composable () -> Unit) {
    RoundPreview {
        UampTheme {
            content()
        }
    }
}
