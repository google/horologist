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

import androidx.wear.compose.material.MaterialTheme
import com.google.android.horologist.audio.AudioOutput
import com.google.android.horologist.audio.VolumeState
import com.google.android.horologist.screenshots.rng.WearLegacyA11yTest
import org.junit.Test

class VolumeScreenA11yScreenshotTest : WearLegacyA11yTest() {

    @Test
    fun volumeScreenAtMinimums() {
        val volumeState = VolumeState(
            current = 0,
            max = 100,
        )
        val audioOutput = AudioOutput.BluetoothHeadset("id", "Pixelbuds")

        runScreenTest {
            VolumeScreenTestCase(
                colors = MaterialTheme.colors,
                volumeState = volumeState,
                audioOutput = audioOutput,
            )
        }
    }

    @Test
    fun volumeScreenNotConnected() {
        val volumeState = VolumeState(
            current = 0,
            max = 100,
        )
        // Media Router returns "Phone"
        val audioOutput = AudioOutput.WatchSpeaker("id", "Phone", false)

        runScreenTest {
            VolumeScreenTestCase(
                colors = MaterialTheme.colors,
                volumeState = volumeState,
                audioOutput = audioOutput,
            )
        }
    }
}
