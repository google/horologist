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

package com.google.android.horologist.audio.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DeviceUnknown
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.VolumeOff
import androidx.compose.material.icons.filled.Watch
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import com.google.android.horologist.audio.AudioOutput
import com.google.android.horologist.audio.AudioOutput.Companion.TYPE_HEADPHONES
import com.google.android.horologist.audio.AudioOutput.Companion.TYPE_NONE
import com.google.android.horologist.audio.AudioOutput.Companion.TYPE_WATCH
import com.google.android.horologist.audio.ui.R

/**
 * UI representation of [AudioOutput].
 */
public data class AudioOutputUi(
    val displayName: String,
    val imageVector: ImageVector,
    val isConnected: Boolean
)

@Composable
public fun AudioOutput.toAudioOutputUi(): AudioOutputUi {
    return AudioOutputUi(
        displayName = when (type) {
            TYPE_WATCH -> stringResource(id = R.string.horologist_speaker_name)
            TYPE_NONE -> stringResource(id = R.string.horologist_output_none)
            else -> name
        },
        imageVector = when (type) {
            TYPE_HEADPHONES -> Icons.Default.Headphones
            TYPE_WATCH -> Icons.Default.Watch
            TYPE_NONE -> Icons.Default.VolumeOff
            else -> Icons.Default.DeviceUnknown
        },
        isConnected = this is AudioOutput.BluetoothHeadset
    )
}
