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
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.VolumeDown
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material.InlineSlider
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Stepper
import com.google.android.horologist.audio.AudioOutput
import com.google.android.horologist.audio.VolumeState
import com.google.android.horologist.audio.ui.components.AudioOutputUi
import com.google.android.horologist.audio.ui.components.DeviceChip
import com.google.android.horologist.audio.ui.components.toAudioOutputUi
import com.google.android.horologist.compose.focus.RequestFocusWhenActive
import com.google.android.horologist.compose.rotaryinput.onRotaryInputAccumulated

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
@OptIn(ExperimentalHorologistAudioUiApi::class)
@Composable
public fun VolumeScreen(
    modifier: Modifier = Modifier,
    volumeViewModel: VolumeViewModel = viewModel(factory = VolumeViewModel.Factory),
    showVolumeIndicator: Boolean = true,
    increaseIcon: @Composable () -> Unit = { VolumeScreenDefaults.IncreaseIcon() },
    decreaseIcon: @Composable () -> Unit = { VolumeScreenDefaults.DecreaseIcon() }
) {
    val volumeState by volumeViewModel.volumeState.collectAsState()
    val audioOutput by volumeViewModel.audioOutput.collectAsState()

    VolumeScreen(
        modifier = modifier,
        volume = { volumeState },
        audioOutputUi = audioOutput.toAudioOutputUi(),
        increaseVolume = { volumeViewModel.increaseVolume() },
        decreaseVolume = { volumeViewModel.decreaseVolume() },
        onAudioOutputClick = { volumeViewModel.launchOutputSelection() },
        showVolumeIndicator = showVolumeIndicator,
        onVolumeChangeByScroll = volumeViewModel::onVolumeChangeByScroll,
        increaseIcon = increaseIcon,
        decreaseIcon = decreaseIcon
    )
}

@Composable
public fun VolumeScreen(
    volume: () -> VolumeState,
    audioOutputUi: AudioOutputUi,
    increaseVolume: () -> Unit,
    decreaseVolume: () -> Unit,
    onAudioOutputClick: () -> Unit,
    modifier: Modifier = Modifier,
    increaseIcon: @Composable () -> Unit = { VolumeScreenDefaults.IncreaseIcon() },
    decreaseIcon: @Composable () -> Unit = { VolumeScreenDefaults.DecreaseIcon() },
    showVolumeIndicator: Boolean = true,
    onVolumeChangeByScroll: ((scrollPixels: Float) -> Unit)? = null
) {
    val focusRequester = remember(onVolumeChangeByScroll) {
        if (onVolumeChangeByScroll != null) {
            FocusRequester()
        } else {
            null
        }
    }

    Box(
        modifier = modifier.fillMaxSize().run {
            onVolumeChangeByScroll?.let {
                onRotaryInputAccumulated(it)
                    .focusRequester(focusRequester!!)
                    .focusable()
            } ?: this
        }
    ) {
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
        }
        if (showVolumeIndicator) {
            VolumePositionIndicator(
                volumeState = volume,
                autoHide = false
            )
        }
    }

    if (focusRequester != null) {
        RequestFocusWhenActive(focusRequester)
    }
}

public object VolumeScreenDefaults {
    @Composable
    public fun IncreaseIcon() {
        Icon(
            modifier = Modifier
                .size(26.dp),
            imageVector = Icons.Default.VolumeUp,
            contentDescription = stringResource(id = R.string.horologist_volume_screen_volume_up_content_description)
        )
    }

    @Composable
    public fun DecreaseIcon() {
        Icon(
            modifier = Modifier
                .size(26.dp),
            imageVector = Icons.Default.VolumeDown,
            contentDescription = stringResource(id = R.string.horologist_volume_screen_volume_down_content_description)
        )
    }
}

@Composable
private fun volumeDescription(volumeState: VolumeState, isAudioOutputConnected: Boolean): String {
    return if (isAudioOutputConnected) {
        stringResource(id = R.string.horologist_volume_screen_connected_state, volumeState.current)
    } else {
        stringResource(id = R.string.horologist_volume_screen_not_connected_state)
    }
}
