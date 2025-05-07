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

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeDown
import androidx.compose.material.icons.automirrored.filled.VolumeMute
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.MaterialTheme
import com.google.android.horologist.audio.ui.R
import com.google.android.horologist.audio.ui.VolumeUiState
import com.google.android.horologist.audio.ui.components.AudioOutputUi

/**
 * Button to launch a screen to control the system audio output and volume.
 *
 * Using media output off icon as default if no [audioOutputUi] is passed in.
 * Using volume up icon as default if no [volumeUiState] is passed in.
 *
 * See [AudioOutputUi]
 * See [VolumeUiState]
 */
@Composable
public fun SetAudioOutputButton(
    onVolumeClick: () -> Unit,
    modifier: Modifier = Modifier,
    volumeUiState: VolumeUiState? = null,
    audioOutputUi: AudioOutputUi? = null,
    enabled: Boolean = true,
    badgeColor: Color = MaterialTheme.colors.primary,
    iconAlignment: Alignment = Alignment.Center,
    iconPadding: PaddingValues = PaddingValues(all = 0.dp),
) {
    SettingsButton(
        modifier = modifier,
        onClick = onVolumeClick,
        enabled = enabled,
        imageVector = when {
            audioOutputUi?.isConnected == true -> audioOutputUi.imageVector
            else -> ImageVector.vectorResource(R.drawable.media_output_off_24)
        },
        badgeVector = if (audioOutputUi?.isConnected == true) {
            when {
                volumeUiState?.isMin == true -> Icons.AutoMirrored.Default.VolumeMute
                volumeUiState?.isMax == false -> Icons.AutoMirrored.Default.VolumeDown
                else -> Icons.AutoMirrored.Default.VolumeUp // volumeUiState == null || volumeUiState.isMax == true
            }
        } else {
            null
        },
        badgeColor = badgeColor,
        contentDescription = stringResource(
            com.google.android.horologist.audio.ui.model.R.string.horologist_set_volume_content_description,
        ),
        iconAlignment = iconAlignment,
        iconPadding = iconPadding,
    )
}
