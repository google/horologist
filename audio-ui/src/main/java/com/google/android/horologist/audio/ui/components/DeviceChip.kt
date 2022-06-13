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

import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DeviceUnknown
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.VolumeOff
import androidx.compose.material.icons.filled.Watch
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.onClick
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.style.TextOverflow
import androidx.wear.compose.material.Chip
import androidx.wear.compose.material.ChipDefaults
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text
import com.google.android.horologist.audio.AudioOutput
import com.google.android.horologist.audio.ExperimentalHorologistAudioApi
import com.google.android.horologist.audio.VolumeState
import com.google.android.horologist.audio.ui.ExperimentalHorologistAudioUiApi
import com.google.android.horologist.audio.ui.R

@ExperimentalHorologistAudioUiApi
@Composable
public fun DeviceChip(
    volumeState: VolumeState,
    audioOutput: AudioOutput,
    onAudioOutputClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val stateDescriptionText = volumeDescription(volumeState, audioOutput)

    val onClickLabel = stringResource(id = R.string.horologist_volume_screen_change_audio_output)

    Chip(
        modifier = modifier
            .width(intrinsicSize = IntrinsicSize.Max)
            .semantics {
                stateDescription = stateDescriptionText
                onClick(onClickLabel) { onAudioOutputClick(); true }
            },
        label = {
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = audioOutput.name,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        },
        icon = {
            Icon(
                imageVector = audioOutput.icon(),
                contentDescription = audioOutput.name,
                tint = MaterialTheme.colors.onSurfaceVariant
            )
        },
        onClick = onAudioOutputClick,
        // Device chip uses secondary colors (surface/onSurface)
        colors = ChipDefaults.secondaryChipColors()
    )
}

@ExperimentalHorologistAudioUiApi
@OptIn(ExperimentalHorologistAudioApi::class)
@Composable
public fun AudioOutput.icon(): ImageVector {
    return when (this) {
        is AudioOutput.BluetoothHeadset -> Icons.Default.Headphones
        is AudioOutput.WatchSpeaker -> Icons.Default.Watch
        is AudioOutput.None -> Icons.Default.VolumeOff
        else -> Icons.Default.DeviceUnknown
    }
}

@Composable
private fun volumeDescription(volumeState: VolumeState, audioOutput: AudioOutput): String {
    return if (audioOutput is AudioOutput.BluetoothHeadset)
        stringResource(id = R.string.horologist_volume_screen_connected, volumeState.current)
    else
        stringResource(id = R.string.horologist_volume_screen_not_connected)
}
