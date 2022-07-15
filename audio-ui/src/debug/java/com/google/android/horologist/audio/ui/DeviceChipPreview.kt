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

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import com.google.android.horologist.audio.AudioOutput
import com.google.android.horologist.audio.VolumeState
import com.google.android.horologist.audio.ui.components.DeviceChip
import com.google.android.horologist.compose.tools.WearPreview
import com.google.android.horologist.compose.tools.WidthConstrainedBox

@WearPreview
@Composable
fun DeviceChipPreview() {
    val volume = VolumeState(10, 10)

    WidthConstrainedBox(
        widths = listOf(100.dp, 164.dp, 192.dp, 227.dp),
        comfortableHeight = 100.dp
    ) {
        DeviceChip(
            volumeState = volume,
            audioOutput = AudioOutput.BluetoothHeadset(id = "1", name = "Galaxy Watch 4"),
            onAudioOutputClick = {
            }
        )
    }
}
