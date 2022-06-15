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

@file:OptIn(ExperimentalComposeUiApi::class)

package com.google.android.horologist.audio.ui

import android.media.AudioManager
import androidx.compose.foundation.focusable
import androidx.compose.foundation.gestures.ScrollableState
import androidx.compose.foundation.gestures.scrollBy
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.rotary.onRotaryScrollEvent
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material.InlineSlider
import androidx.wear.compose.material.Stepper
import com.google.android.horologist.audio.AudioOutput
import com.google.android.horologist.audio.ExperimentalHorologistAudioApi
import com.google.android.horologist.audio.VolumeState
import com.google.android.horologist.audio.ui.VolumeScreenDefaults.DecreaseIcon
import com.google.android.horologist.audio.ui.VolumeScreenDefaults.IncreaseIcon
import com.google.android.horologist.audio.ui.components.DeviceChip
import kotlinx.coroutines.launch

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
@ExperimentalHorologistAudioUiApi
@OptIn(ExperimentalHorologistAudioApi::class)
@Composable
public fun VolumeScreen(
    modifier: Modifier = Modifier,
    volumeViewModel: VolumeViewModel = viewModel(factory = VolumeViewModel.Factory),
    showVolumeIndicator: Boolean = true,
    focusRequester: FocusRequester = remember { FocusRequester() },
    increaseIcon: @Composable () -> Unit = { IncreaseIcon() },
    decreaseIcon: @Composable () -> Unit = { DecreaseIcon() },
) {
    val volumeState by volumeViewModel.volumeState.collectAsState()
    val audioOutput by volumeViewModel.audioOutput.collectAsState()

    VolumeScreen(
        modifier = modifier,
        volume = { volumeState },
        audioOutput = audioOutput,
        increaseVolume = { volumeViewModel.increaseVolume() },
        decreaseVolume = { volumeViewModel.decreaseVolume() },
        onAudioOutputClick = { volumeViewModel.launchOutputSelection() },
        showVolumeIndicator = showVolumeIndicator,
        focusRequester = focusRequester,
        scrollableState = volumeViewModel.volumeScrollableState,
        increaseIcon = increaseIcon,
        decreaseIcon = decreaseIcon,
    )
}

@ExperimentalHorologistAudioUiApi
@OptIn(ExperimentalHorologistAudioApi::class)
@Composable
public fun VolumeScreen(
    volume: () -> VolumeState,
    audioOutput: AudioOutput,
    increaseVolume: () -> Unit,
    decreaseVolume: () -> Unit,
    onAudioOutputClick: () -> Unit,
    modifier: Modifier = Modifier,
    increaseIcon: @Composable () -> Unit = { IncreaseIcon() },
    decreaseIcon: @Composable () -> Unit = { DecreaseIcon() },
    showVolumeIndicator: Boolean = true,
    focusRequester: FocusRequester = remember { FocusRequester() },
    scrollableState: ScrollableState? = null
) {
    Box(
        modifier = modifier.fillMaxSize().run {
            if (scrollableState != null) {
                val coroutineScope = rememberCoroutineScope()

                onRotaryScrollEvent {
                    coroutineScope.launch {
                        scrollableState.scrollBy(it.verticalScrollPixels)
                    }
                    true
                }
                    .focusRequester(focusRequester)
                    .focusable()
            } else {
                this
            }
        },
    ) {
        val volumeState = volume()
        Stepper(
            value = volumeState.current.toFloat(),
            onValueChange = { if (it > volumeState.current) increaseVolume() else decreaseVolume() },
            steps = volumeState.max - 1,
            valueRange = (0f..volumeState.max.toFloat()),
            increaseIcon = {
                if (volumeState.isMax) {
                    Box(modifier = Modifier.alpha(0.38f)) {
                        increaseIcon()
                    }
                } else {
                    increaseIcon()
                }
            },
            decreaseIcon = {
                if (volumeState.current == 0) {
                    Box(modifier = Modifier.alpha(0.38f)) {
                        decreaseIcon()
                    }
                } else {
                    decreaseIcon()
                }
            },
        ) {
            DeviceChip(
                modifier = Modifier.padding(horizontal = 18.dp),
                volumeState = volumeState,
                audioOutput = audioOutput,
                onAudioOutputClick = onAudioOutputClick
            )
        }
        if (showVolumeIndicator) {
            VolumePositionIndicator(volumeState = volume)
        }
    }
}

public object VolumeScreenDefaults {
    @Composable
    public fun IncreaseIcon() {
        Icon(
            modifier = Modifier
                .size(26.dp),
            imageVector = Icons.Default.VolumeUp,
            contentDescription = stringResource(id = R.string.horologist_volume_screen_volume_up),
        )
    }

    @Composable
    public fun DecreaseIcon() {
        Icon(
            modifier = Modifier
                .size(26.dp),
            imageVector = Icons.Default.VolumeDown,
            contentDescription = stringResource(id = R.string.horologist_volume_screen_volume_down),
        )
    }
}
