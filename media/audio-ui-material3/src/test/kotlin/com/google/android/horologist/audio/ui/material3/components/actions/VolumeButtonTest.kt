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

package com.google.android.horologist.audio.ui.material3.components.actions

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material3.MaterialTheme
import com.google.android.horologist.audio.AudioOutput
import com.google.android.horologist.audio.ui.VolumeUiState
import com.google.android.horologist.audio.ui.material3.components.toAudioOutputUi
import com.google.android.horologist.screenshots.rng.WearLegacyComponentTest
import org.junit.Test

class VolumeButtonTest : WearLegacyComponentTest() {

    @Test
    fun givenCurrentVolumeIsNotMaxAndNotMin_thenIconIsVolumeDown() {
        val currentVolume = 5

        runComponentTest {
            VolumeButton(
                onVolumeClick = {},
                volumeUiState = VolumeUiState(current = currentVolume, max = 10),
            )
        }
    }

    @Test
    fun givenCurrentVolumeIsMinimum_thenIconIsVolumeMute() {
        val currentVolume = 0

        runComponentTest {
            VolumeButton(
                onVolumeClick = {},
                volumeUiState = VolumeUiState(current = currentVolume),
            )
        }
    }

    @Test
    fun givenCurrentVolumeIsMaximum_thenIconIsVolumeUp() {
        val currentVolume = 1

        runComponentTest {
            VolumeButton(
                onVolumeClick = {},
                volumeUiState = VolumeUiState(current = currentVolume),
            )
        }
    }

    @Test
    fun givenNoVolumeUiState_thenIconIsVolumeUp() {
        runComponentTest {
            VolumeButton(onVolumeClick = {})
        }
    }

    @Test
    fun volumeButtonWithBadge_givenCurrentVolumeIsNotMaxAndNotMin_thenIconIsVolumeDown() {
        val currentVolume = 5

        runComponentTest {
            VolumeButtonWithBadge(
                onOutputClick = {},
                volumeUiState = VolumeUiState(current = currentVolume, max = 10),
                audioOutputUi = AudioOutput.BluetoothHeadset(id = "id", name = "name").toAudioOutputUi(),
            )
        }
    }

    @Test
    fun volumeButtonWithBadge_givenCurrentVolumeIsMinimum_thenIconIsVolumeMute() {
        val currentVolume = 0

        runComponentTest {
            VolumeButtonWithBadge(
                onOutputClick = {},
                volumeUiState = VolumeUiState(current = currentVolume, max = 10),
                audioOutputUi = AudioOutput.BluetoothHeadset(id = "id", name = "name").toAudioOutputUi(),
            )
        }
    }

    @Test
    fun volumeButtonWithBadge_givenCurrentVolumeIsMaximum_thenIconIsVolumeUp() {
        val currentVolume = 10

        runComponentTest {
            VolumeButtonWithBadge(
                onOutputClick = {},
                volumeUiState = VolumeUiState(current = currentVolume, max = 10),
                audioOutputUi = AudioOutput.BluetoothHeadset(id = "id", name = "name").toAudioOutputUi(),
            )
        }
    }

    @Test
    fun volumeButtonWithBadge_givenNoVolumeUiState_thenIconIsVolumeUp() {
        runComponentTest {
            VolumeButtonWithBadge(
                onOutputClick = {},
                volumeUiState = null,
                audioOutputUi = AudioOutput.BluetoothHeadset(id = "id", name = "name").toAudioOutputUi(),
            )
        }
    }

    @Test
    fun volumeButtonWithBadge_givenNoneAudioOutputUi_thenIconIsMediaOutputOff() {
        runComponentTest {
            VolumeButtonWithBadge(
                onOutputClick = {},
                audioOutputUi = AudioOutput.None.toAudioOutputUi(),
                volumeUiState = null,
            )
        }
    }

    @Composable
    override fun ComponentScaffold(content: @Composable () -> Unit) {
        Box(
            modifier = Modifier
                .wrapContentSize()
                .background(MaterialTheme.colorScheme.background)
                .border(1.dp, Color.White),
        ) {
            content()
        }
    }
}
