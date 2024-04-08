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

package com.google.android.horologist.audio.ui.components.actions

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.google.android.horologist.audio.ui.VolumeUiState
import com.google.android.horologist.screenshots.rng.WearLegacyComponentTest
import org.junit.Test

class SetVolumeButtonTest : WearLegacyComponentTest() {

    @Test
    fun givenCurrentVolumeIsNotMaxAndNotMin_thenIconIsVolumeDown() {
        val currentVolume = 5

        runComponentTest {
            SetVolumeButton(
                onVolumeClick = {},
                volumeUiState = VolumeUiState(current = currentVolume, max = 10),
            )
        }
    }

    @Test
    fun givenCurrentVolumeIsMinimum_thenIconIsVolumeMute() {
        val currentVolume = 0

        runComponentTest {
            SetVolumeButton(
                onVolumeClick = {},
                volumeUiState = VolumeUiState(current = currentVolume),
            )
        }
    }

    @Test
    fun givenCurrentVolumeIsMaximum_thenIconIsVolumeUp() {
        val currentVolume = 1

        runComponentTest {
            SetVolumeButton(
                onVolumeClick = {},
                volumeUiState = VolumeUiState(current = currentVolume),
            )
        }
    }

    @Test
    fun givenNoVolumeUiState_thenIconIsVolumeUp() {
        runComponentTest {
            SetVolumeButton(onVolumeClick = {})
        }
    }

    @Composable
    override fun ComponentScaffold(content: @Composable () -> Unit) {
        Box(
            modifier = Modifier
                .wrapContentSize()
                .background(Color.Black.copy(alpha = 0.5f))
                .border(1.dp, Color.White),
        ) {
            content()
        }
    }
}
