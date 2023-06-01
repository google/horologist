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

import android.media.AudioManager
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.VolumeDown
import androidx.compose.material.icons.outlined.VolumeUp
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.wear.compose.material.InlineSlider
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Stepper
import androidx.wear.compose.material.Text
import com.google.android.horologist.annotations.ExperimentalHorologistApi
import com.google.android.horologist.audio.AudioOutput
import com.google.android.horologist.audio.ui.components.AudioOutputUi
import com.google.android.horologist.audio.ui.components.DeviceChip
import com.google.android.horologist.audio.ui.components.toAudioOutputUi
import com.google.android.horologist.compose.material.Icon
import com.google.android.horologist.compose.material.IconRtlMode
import com.google.android.horologist.compose.rotaryinput.RotaryDefaults.isLowResInput

/**
 * Volume Screen with an [InlineSlider] and Increase/Decrease buttons for the Audio Stream Volume.
 *
 * Contains a Stepper with Up and Down buttons, plus a button to show the current [AudioOutput] and
 * prompt to select a new one.
 *
 * The volume and audio output come indirectly from the [AudioManager] and accessed via
 * [VolumeViewModel].
 *
 * See [VolumeViewModel]
 * See [AudioManager.STREAM_MUSIC]
 */
@Composable
@ExperimentalHorologistApi
public fun VolumeScreen(
    modifier: Modifier = Modifier,
    volumeViewModel: VolumeViewModel = viewModel(factory = VolumeViewModel.Factory),
    showVolumeIndicator: Boolean = true,
    increaseIcon: @Composable () -> Unit = { VolumeScreenDefaults.IncreaseIcon() },
    decreaseIcon: @Composable () -> Unit = { VolumeScreenDefaults.DecreaseIcon() }
) {
    val volumeUiState by volumeViewModel.volumeUiState.collectAsState()
    val audioOutput by volumeViewModel.audioOutput.collectAsState()

    VolumeScreen(
        modifier = modifier
            .rotaryVolumeControlsWithFocus(
                volumeUiStateProvider = { volumeViewModel.volumeUiState.value },
                onRotaryVolumeInput = { newVolume -> volumeViewModel.setVolume(newVolume) },
                localView = LocalView.current,
                isLowRes = isLowResInput()
            ),
        volume = { volumeUiState },
        audioOutputUi = audioOutput.toAudioOutputUi(),
        increaseVolume = { volumeViewModel.increaseVolume() },
        decreaseVolume = { volumeViewModel.decreaseVolume() },
        onAudioOutputClick = { volumeViewModel.launchOutputSelection() },
        showVolumeIndicator = showVolumeIndicator,
        increaseIcon = increaseIcon,
        decreaseIcon = decreaseIcon
    )
}

/**
 * Volume Screen with a Output Device chip.
 */
@Composable
public fun VolumeScreen(
    volume: () -> VolumeUiState,
    audioOutputUi: AudioOutputUi,
    increaseVolume: () -> Unit,
    decreaseVolume: () -> Unit,
    onAudioOutputClick: () -> Unit,
    modifier: Modifier = Modifier,
    increaseIcon: @Composable () -> Unit = { VolumeScreenDefaults.IncreaseIcon() },
    decreaseIcon: @Composable () -> Unit = { VolumeScreenDefaults.DecreaseIcon() },
    showVolumeIndicator: Boolean = true
) {
    VolumeScreen(
        volume = volume,
        contentSlot = {
            val volumeState = volume()
            DeviceChip(
                modifier = Modifier.padding(horizontal = 18.dp),
                volumeDescription = volumeDescription(volumeState, audioOutputUi.isConnected),
                deviceName = audioOutputUi.displayName,
                icon = {
                    Icon(
                        imageVector = audioOutputUi.imageVector,
                        contentDescription = audioOutputUi.displayName,
                        tint = MaterialTheme.colors.onSurfaceVariant
                    )
                },
                onAudioOutputClick = onAudioOutputClick
            )
        },
        increaseVolume = increaseVolume,
        decreaseVolume = decreaseVolume,
        modifier = modifier,
        increaseIcon = increaseIcon,
        decreaseIcon = decreaseIcon,
        showVolumeIndicator = showVolumeIndicator
    )
}

/**
 * Volume Screen with a simple "Volume" label.
 */
@Composable
public fun VolumeWithLabelScreen(
    volume: () -> VolumeUiState,
    increaseVolume: () -> Unit,
    decreaseVolume: () -> Unit,
    modifier: Modifier = Modifier,
    increaseIcon: @Composable () -> Unit = { VolumeScreenDefaults.IncreaseIcon() },
    decreaseIcon: @Composable () -> Unit = { VolumeScreenDefaults.DecreaseIcon() },
    showVolumeIndicator: Boolean = true
) {
    VolumeScreen(
        volume = volume,
        contentSlot = {
            Text(
                stringResource(id = R.string.horologist_volume_screen_volume_label),
                style = MaterialTheme.typography.button,
                maxLines = 1,
                overflow = TextOverflow.Clip
            )
        },
        increaseVolume = increaseVolume,
        decreaseVolume = decreaseVolume,
        modifier = modifier,
        increaseIcon = increaseIcon,
        decreaseIcon = decreaseIcon,
        showVolumeIndicator = showVolumeIndicator
    )
}

@Composable
internal fun VolumeScreen(
    volume: () -> VolumeUiState,
    contentSlot: @Composable () -> Unit,
    increaseVolume: () -> Unit,
    decreaseVolume: () -> Unit,
    modifier: Modifier = Modifier,
    increaseIcon: @Composable () -> Unit = { VolumeScreenDefaults.IncreaseIcon() },
    decreaseIcon: @Composable () -> Unit = { VolumeScreenDefaults.DecreaseIcon() },
    showVolumeIndicator: Boolean = true
) {
    Box(modifier = modifier.fillMaxSize())
    val volumeState = volume()
    Stepper(
        value = volumeState.current.toFloat(),
        onValueChange = { if (it > volumeState.current) increaseVolume() else decreaseVolume() },
        steps = volumeState.max - 1,
        valueRange = (0f..volumeState.max.toFloat()),
        increaseIcon = {
            increaseIcon()
        },
        decreaseIcon = {
            decreaseIcon()
        }
    ) {
        contentSlot()
    }
    if (showVolumeIndicator) {
        VolumePositionIndicator(
            volumeUiState = { volume() }
        )
    }
}

public object VolumeScreenDefaults {
    @Composable
    public fun IncreaseIcon() {
        Icon(
            modifier = Modifier.size(26.dp),
            imageVector = Icons.Outlined.VolumeUp,
            contentDescription = stringResource(id = R.string.horologist_volume_screen_volume_up_content_description),
            rtlMode = IconRtlMode.Mirrored
        )
    }

    @Composable
    public fun DecreaseIcon() {
        Icon(
            modifier = Modifier.size(26.dp),
            imageVector = Icons.Outlined.VolumeDown,
            contentDescription = stringResource(id = R.string.horologist_volume_screen_volume_down_content_description),
            rtlMode = IconRtlMode.Mirrored
        )
    }
}

@Composable
private fun volumeDescription(volumeUiState: VolumeUiState, isAudioOutputConnected: Boolean): String {
    return if (isAudioOutputConnected) {
        stringResource(id = R.string.horologist_volume_screen_connected_state, volumeUiState.current)
    } else {
        stringResource(id = R.string.horologist_volume_screen_not_connected_state)
    }
}
