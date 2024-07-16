/*
 * Copyright 2024 The Android Open Source Project
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

package com.google.android.horologist.audit

import androidx.compose.runtime.Composable
import com.google.android.horologist.audio.AudioOutput
import com.google.android.horologist.audio.VolumeState
import com.google.android.horologist.audio.ui.VolumeScreen
import com.google.android.horologist.audio.ui.components.toAudioOutputUi
import com.google.android.horologist.audio.ui.mapper.VolumeUiStateMapper
import com.google.android.horologist.compose.layout.ScreenScaffold

@Composable
fun VolumeRsbAudit(route: AuditNavigation.VolumeRsb.Audit) {
    val volume =
        when (route.config) {
            AuditNavigation.VolumeRsb.Config.TopLong -> {
                VolumeState(19, 20)
            }

            AuditNavigation.VolumeRsb.Config.TopShort -> {
                VolumeState(4, 4)
            }

            AuditNavigation.VolumeRsb.Config.BottomLong -> {
                VolumeState(1, 20)
            }

            AuditNavigation.VolumeRsb.Config.BottomShort -> {
                VolumeState(0, 4)
            }

            AuditNavigation.VolumeRsb.Config.MiddleLong -> {
                VolumeState(10, 20)
            }

            AuditNavigation.VolumeRsb.Config.MiddleShort -> {
                VolumeState(2, 4)
            }
        }
    val volumeUiState = VolumeUiStateMapper.map(volumeState = volume)

    ScreenScaffold(timeText = {}) {
        VolumeScreen(
            volume = { volumeUiState },
            audioOutputUi = AudioOutput.BluetoothHeadset(id = "1", name = "Galaxy Watch 4")
                .toAudioOutputUi(),
            increaseVolume = { },
            decreaseVolume = { },
            onAudioOutputClick = {},
        )
    }
}
