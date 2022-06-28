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

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.VolumeDown
import androidx.compose.material.icons.filled.VolumeMute
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.ButtonDefaults
import androidx.wear.compose.material.Icon
import com.google.android.horologist.audio.VolumeState
import com.google.android.horologist.audio.ui.ExperimentalHorologistAudioUiApi
import com.google.android.horologist.audio.ui.R
import com.google.android.horologist.audio.ui.semantics.CustomSemanticsProperties.iconImageVector

/**
 * Button to launch a screen to control the system volume.
 *
 * See [VolumeState]
 */
@ExperimentalHorologistAudioUiApi
@Composable
public fun SetVolumeButton(
    onVolumeClick: () -> Unit,
    volumeState: VolumeState,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    Button(
        modifier = modifier.size(ButtonDefaults.SmallButtonSize),
        onClick = onVolumeClick,
        colors = ButtonDefaults.iconButtonColors(),
        enabled = enabled
    ) {
        val imageVector = when {
            volumeState.current == 0 -> Icons.Default.VolumeMute
            volumeState.isMax -> Icons.Default.VolumeUp
            else -> Icons.Default.VolumeDown
        }

        Icon(
            imageVector = imageVector,
            contentDescription = stringResource(R.string.horologist_set_volume_content_description),
            modifier = Modifier.semantics { iconImageVector = imageVector }
        )
    }
}
