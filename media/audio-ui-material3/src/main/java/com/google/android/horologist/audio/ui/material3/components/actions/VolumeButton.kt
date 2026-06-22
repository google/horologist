/*
 * Copyright 2025 The Android Open Source Project
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

package com.google.android.horologist.audio.ui.material3.components.actions

import androidx.compose.foundation.BorderStroke
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.VolumeDown
import androidx.compose.material.icons.automirrored.rounded.VolumeMute
import androidx.compose.material.icons.automirrored.rounded.VolumeUp
import androidx.compose.material.icons.rounded.Radio
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.wear.compose.material3.IconButtonColors
import com.google.android.horologist.audio.ui.VolumeUiState
import com.google.android.horologist.audio.ui.material3.R
import com.google.android.horologist.audio.ui.material3.components.AudioOutputUi
import com.google.android.horologist.audio.ui.model.R as ModelR

/**
 * A composable function that creates a volume button.
 *
 * @param onVolumeClick The callback invoked when the button is clicked.
 * @param modifier The modifier to be applied to the button.
 * @param imageVector The [ImageVector] to be used for the button's icon.
 * @param enabled Controls the enabled state of the button. When `false`, the button will not
 *   respond to user input.
 * @param alignment The alignment of the button within the tap area.
 * @param buttonColors The colors to be used for the button.
 * @param contentDescription The content description to be used for the button.
 */
@Composable
public fun VolumeButton(
    onVolumeClick: () -> Unit,
    modifier: Modifier = Modifier,
    imageVector: ImageVector = Icons.Rounded.Radio,
    enabled: Boolean = true,
    alignment: Alignment = Alignment.Center,
    buttonColors: IconButtonColors = SettingsButtonDefaults.buttonColors(),
    contentDescription: String = stringResource(ModelR.string.horologist_set_volume_content_description),
    border: BorderStroke? = null,
) {
    SettingsButton(
        modifier = modifier,
        onClick = onVolumeClick,
        enabled = enabled,
        imageVector = imageVector,
        alignment = alignment,
        contentDescription = contentDescription,
        buttonColors = buttonColors,
        border = border,
    )
}

/**
 * A composable function that creates a volume button with dynamic icon based on the
 * [volumeUiState].
 *
 * The button's icon changes depending on the volume state provided. If the volume is at its
 * minimum, the button displays a mute icon. If the volume is not at its maximum, the button shows a
 * volume down icon. In all other cases, including when no volume state is provided or the volume is
 * at its maximum, the button displays a volume up icon..
 *
 * @param onVolumeClick The callback invoked when the button is clicked.
 * @param modifier The modifier to be applied to the button.
 * @param volumeUiState The state of the volume UI, used to determine the button's icon.
 * @param enabled Controls the enabled state of the button. When `false`, the button will not
 *   respond to user input.
 * @param alignment The alignment of the button within the tap area.
 * @param buttonColors The colors to be used for the button.
 * @param contentDescription The content description to be used for the button.
 */
@Composable
public fun VolumeButton(
    onVolumeClick: () -> Unit,
    modifier: Modifier = Modifier,
    volumeUiState: VolumeUiState?,
    enabled: Boolean = true,
    alignment: Alignment = Alignment.Center,
    buttonColors: IconButtonColors = SettingsButtonDefaults.buttonColors(),
    contentDescription: String = stringResource(ModelR.string.horologist_set_volume_content_description),
    border: BorderStroke? = null,
) {
    VolumeButton(
        onVolumeClick = onVolumeClick,
        modifier = modifier,
        imageVector = volumeUiState.getVolumeIndicatorIcon(),
        enabled = enabled,
        alignment = alignment,
        buttonColors = buttonColors,
        contentDescription = contentDescription,
        border = border,
    )
}

/**
 * A composable function that creates an audio output button with an optional badge.
 *
 * @param onOutputClick The callback invoked when the button is clicked.
 * @param modifier The modifier to be applied to the button.
 * @param enabled Controls the enabled state of the button. When `false`, the button will not
 *   respond to user input.
 * @param alignment The alignment of the button within the tap area.
 * @param buttonColors The colors to be used for the button.
 * @param badgeColors The colors to be used for the badge.
 * @param contentDescription The content description to be used for the button.
 */
@Composable
public fun VolumeButtonWithBadge(
    onOutputClick: () -> Unit,
    imageVector: ImageVector,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    alignment: Alignment = Alignment.Center,
    buttonColors: IconButtonColors = SettingsButtonDefaults.buttonColors(),
    badgeVector: ImageVector? = null,
    badgeColors: IconButtonColors = SettingsButtonDefaults.badgeColors(),
    contentDescription: String = stringResource(ModelR.string.horologist_set_volume_content_description),
    border: BorderStroke? = null,
) {
    SettingsButton(
        modifier = modifier,
        onClick = onOutputClick,
        enabled = enabled,
        alignment = alignment,
        buttonColors = buttonColors,
        imageVector = imageVector,
        badgeVector = badgeVector,
        badgeColors = badgeColors,
        contentDescription = contentDescription,
        border = border,
    )
}

/**
 * A composable function that creates an audio output button with an optional badge, dynamically
 * displaying icons based on the audio output and volume states.
 *
 * The default button's main icon is determined by the audio output's connection status. If the
 * audio output is connected, the button displays the icon provided by the audio output UI state. If
 * the audio output is disconnected, the button displays a "media output off" icon.
 *
 * The default badge icon, if present, represents the volume level and is only displayed when the
 * audio output is connected. If the volume is at its minimum, the badge displays a mute icon. If
 * the volume is not at its maximum, the badge displays a volume down icon. Otherwise, including
 * when the volume state is null or the volume is at its maximum, the badge displays a volume up
 * icon. If the audio output is disconnected, no badge is displayed.
 *
 * @param onOutputClick The callback invoked when the button is clicked.
 * @param audioOutputUi The UI state of the audio output, including connection status and icon.
 * @param volumeUiState The UI state of the volume, used to determine the badge icon.
 * @param modifier The modifier to be applied to the button.
 * @param enabled Controls the enabled state of the button. When `false`, the button will not
 *   respond to user input.
 * @param alignment The alignment of the button within the tap area.
 * @param buttonColors The colors to be used for the button.
 * @param badgeColors The colors to be used for the badge.
 * @param contentDescription The content description to be used for the button.
 */
@Composable
public fun VolumeButtonWithBadge(
    onOutputClick: () -> Unit,
    audioOutputUi: AudioOutputUi,
    volumeUiState: VolumeUiState?,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    alignment: Alignment = Alignment.Center,
    buttonColors: IconButtonColors = SettingsButtonDefaults.buttonColors(),
    badgeColors: IconButtonColors = SettingsButtonDefaults.badgeColors(),
    contentDescription: String = stringResource(ModelR.string.horologist_set_volume_content_description),
    border: BorderStroke? = null,
) {
    VolumeButtonWithBadge(
        onOutputClick = onOutputClick,
        imageVector = audioOutputUi.getAudioOutputConnectionIcon(),
        modifier = modifier,
        enabled = enabled,
        alignment = alignment,
        badgeVector = if (audioOutputUi.isConnected) {
            volumeUiState.getVolumeIndicatorIcon()
        } else {
            null
        },
        buttonColors = buttonColors,
        badgeColors = badgeColors,
        contentDescription = contentDescription,
        border = border,
    )
}

@Composable
private fun AudioOutputUi.getAudioOutputConnectionIcon(): ImageVector =
    when {
        this.isConnected -> this.imageVector
        else -> ImageVector.vectorResource(R.drawable.rounded_media_output_off_24)
    }

private fun VolumeUiState?.getVolumeIndicatorIcon(): ImageVector =
    when {
        this?.isMin == true -> Icons.AutoMirrored.Rounded.VolumeMute
        this?.isMax == false -> Icons.AutoMirrored.Rounded.VolumeDown
        else ->
            Icons.AutoMirrored.Rounded.VolumeUp // volumeUiState == null || volumeUiState.isMax == true
    }
