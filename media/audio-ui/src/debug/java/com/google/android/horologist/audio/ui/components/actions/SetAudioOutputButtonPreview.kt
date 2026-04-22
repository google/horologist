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

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.google.android.horologist.audio.ui.VolumeUiState
import com.google.android.horologist.audio.ui.components.AudioOutputUi

@Preview(
    name = "Other volume",
    backgroundColor = 0xff000000,
    showBackground = true,
    widthDp = 60,
)
@Composable
fun SetAudioOutputButtonPreview() {
    SetAudioOutputButton(
        onVolumeClick = {},
        volumeUiState = VolumeUiState(current = 4, max = 10),
        audioOutputUi = AudioOutputUi(displayName = "", imageVector = Icons.Default.Headphones, isConnected = true),
    )
}

@Preview(
    name = "Min volume",
    backgroundColor = 0xff000000,
    showBackground = true,
    widthDp = 60,
)
@Composable
fun SetAudioOutputButtonPreviewMinVolume() {
    SetAudioOutputButton(
        onVolumeClick = {},
        volumeUiState = VolumeUiState(current = 0, max = 10),
        audioOutputUi = AudioOutputUi(displayName = "", imageVector = Icons.Default.Headphones, isConnected = true),
    )
}

@Preview(
    name = "Max volume",
    backgroundColor = 0xff000000,
    showBackground = true,
    widthDp = 60,
)
@Composable
fun SetAudioOutputButtonPreviewMaxVolume() {
    SetAudioOutputButton(
        onVolumeClick = {},
        volumeUiState = VolumeUiState(current = 10, max = 10),
        audioOutputUi = AudioOutputUi(displayName = "", imageVector = Icons.Default.Headphones, isConnected = true),
    )
}

@Preview(
    name = "NoAudioOutput",
    backgroundColor = 0xff000000,
    showBackground = true,
    widthDp = 60,
)
@Composable
fun SetAudioOutputButtonPreviewNoOutput() {
    SetAudioOutputButton(
        onVolumeClick = {},
        volumeUiState = VolumeUiState(current = 10, max = 10),
        audioOutputUi = null,
    )
}
