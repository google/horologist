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

@file:OptIn(
    ExperimentalHorologistApi::class
)

package com.google.android.horologist.audio.ui.components.actions

import com.google.android.horologist.annotations.ExperimentalHorologistApi
import com.google.android.horologist.audio.ui.VolumeUiState
import com.google.android.horologist.compose.tools.snapshotInABox
import com.google.android.horologist.paparazzi.WearPaparazzi
import org.junit.Rule
import org.junit.Test

class SetVolumeButtonTest {

    @get:Rule
    val paparazzi = WearPaparazzi()

    @Test
    fun givenCurrentVolumeIsNotMaxAndNotMin_thenIconIsVolumeDown() {
        val currentVolume = 5

        paparazzi.snapshotInABox {
            SetVolumeButton(
                onVolumeClick = {},
                volumeUiState = VolumeUiState(current = currentVolume, max = 10)
            )
        }
    }

    @Test
    fun givenCurrentVolumeIsMinimum_thenIconIsVolumeMute() {
        paparazzi.snapshotInABox {
            SetVolumeButton(
                onVolumeClick = {},
                volumeUiState = VolumeUiState(isMin = true)
            )
        }
    }

    @Test
    fun givenCurrentVolumeIsMaximum_thenIconIsVolumeUp() {
        paparazzi.snapshotInABox {
            SetVolumeButton(
                onVolumeClick = {},
                volumeUiState = VolumeUiState(isMax = true)
            )
        }
    }
}
