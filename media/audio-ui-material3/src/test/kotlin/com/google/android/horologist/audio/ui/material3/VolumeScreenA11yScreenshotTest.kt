/*
 * Copyright 2025 The Android Open Source Project
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

package com.google.android.horologist.audio.ui.material3

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.wear.compose.material3.MaterialTheme
import androidx.wear.compose.material3.ScreenScaffold
import com.google.android.horologist.audio.AudioOutput
import com.google.android.horologist.audio.VolumeState
import com.google.android.horologist.screenshots.rng.WearLegacyA11yTest
import org.junit.Test

class VolumeScreenA11yScreenshotTest : WearLegacyA11yTest() {

    @Test
    fun volumeScreenAtMinimum() {
        val volumeState = VolumeState(
            current = 0,
            max = 100,
        )
        val audioOutput = AudioOutput.BluetoothHeadset("id", "Pixelbuds")

        runScreenTest {
            VolumeScreenTest.TestVolumeScreen(
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
            VolumeScreenTest.TestVolumeScreen(
                volumeState = volumeState,
                audioOutput = audioOutput,
            )
        }
    }

    @Test
    fun volumeScreenWatchSpeakerConnected() {
        val volumeState = VolumeState(
            current = 0,
            max = 100,
        )

        // Media Router returns "Phone"
        val audioOutput = AudioOutput.WatchSpeaker("id", "Phone", true)

        runScreenTest {
            VolumeScreenTest.TestVolumeScreen(
                volumeState = volumeState,
                audioOutput = audioOutput,
            )
        }
    }

    @Composable
    override fun TestScaffold(content: @Composable () -> Unit) {
        ScreenScaffold(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
            timeText = {},
        ) {
            content()
        }
    }
}
