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
import androidx.compose.material.icons.filled.VolumeDown
import androidx.compose.material.icons.filled.VolumeMute
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.google.android.horologist.audio.ui.R
import com.google.android.horologist.audio.ui.VolumeUiState
import com.google.android.horologist.compose.material.IconRtlMode

/**
 * Button to launch a screen to control the system volume, using volume up icon as
 * default if no [volumeUiState] is passed in.
 *
 * See [VolumeUiState]
 */
@Composable
public fun SetVolumeButton(
    onVolumeClick: () -> Unit,
    modifier: Modifier = Modifier,
    volumeUiState: VolumeUiState? = null,
    enabled: Boolean = true
) {
    SettingsButton(
        modifier = modifier,
        onClick = onVolumeClick,
        enabled = enabled,
        imageVector = when {
            volumeUiState?.isMin == true -> Icons.Default.VolumeMute
            volumeUiState?.isMax == false -> Icons.Default.VolumeDown
            else -> Icons.Default.VolumeUp // volumeUiState == null || volumeUiState.isMax == true
        },
        iconRtlMode = IconRtlMode.Mirrored,
        contentDescription = stringResource(R.string.horologist_set_volume_content_description)
    )
}
