/*
 * Copyright 2021 Google Inc. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

@file:OptIn(ExperimentalComposeUiApi::class)

package com.google.android.horologist.systemui

import android.media.AudioManager
import androidx.compose.foundation.focusable
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DeviceUnknown
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.Speaker
import androidx.compose.material.icons.filled.VolumeDown
import androidx.compose.material.icons.filled.VolumeOff
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.rotary.onPreRotaryScrollEvent
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.ButtonDefaults
import androidx.wear.compose.material.Chip
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material.InlineSlider
import androidx.wear.compose.material.PositionIndicator
import androidx.wear.compose.material.Stepper
import androidx.wear.compose.material.Text
import com.google.android.horologist.system.AudioOutput
import com.google.android.horologist.system.AudioOutputRepository
import com.google.android.horologist.system.VolumeRepository
import com.google.android.horologist.system.VolumeState
import kotlinx.coroutines.launch

/**
 * Volume Screen with an [InlineSlider] and Increase/Decrease
 * buttons for the Audio Stream Volume.
 *
 * See [VolumeViewModel]
 * See [AudioManager.STREAM_MUSIC]
 */
@Composable
fun VolumeScreen(
    modifier: Modifier = Modifier,
    showVolumeIndicator: Boolean = true
) {
    val context = LocalContext.current
    val lifecycle = LocalLifecycleOwner.current.lifecycle

    val volumeRepository = VolumeRepository.fromContext(context, lifecycle)
    val audioOutputRepository = AudioOutputRepository.fromContext(context, lifecycle)
    val volumeViewModel = VolumeViewModel(volumeRepository, audioOutputRepository)

    VolumeScreen(
        modifier = modifier,
        volumeViewModel = volumeViewModel,
        showVolumeIndicator = showVolumeIndicator
    )
}

/**
 * Volume Screen with an [InlineSlider] and Increase/Decrease
 * buttons for the Audio Stream Volume.
 *
 * See [VolumeViewModel]
 * See [AudioManager.STREAM_MUSIC]
 */
@Composable
fun VolumeScreen(
    modifier: Modifier = Modifier,
    volumeViewModel: VolumeViewModel,
    showVolumeIndicator: Boolean = true
) {
    val volumeScrollableState = remember {
        VolumeScrollableState(volumeViewModel.volumeRepository)
    }
    val volumeState by volumeViewModel.volumeState.collectAsState()
    val audioOutput by volumeViewModel.audioOutput.collectAsState()
    val coroutineScope = rememberCoroutineScope()
    val focusRequester = remember { FocusRequester() }

    VolumeScreen(
        modifier = modifier.onPreRotaryScrollEvent {
            coroutineScope.launch {
                volumeScrollableState.scrollBy(it.verticalScrollPixels)
            }
            true
        }
            .focusRequester(focusRequester)
            .focusable(),
        volume = volumeState,
        audioOutput = audioOutput,
        increaseVolume = { volumeViewModel.increaseVolume() },
        decreaseVolume = { volumeViewModel.decreaseVolume() },
        onAudioOutputClick = { volumeViewModel.launchOutputSelection() },
        showVolumeIndicator = showVolumeIndicator
    )

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }
}

@Composable
fun VolumeScreen(
    modifier: Modifier = Modifier,
    volume: VolumeState,
    audioOutput: AudioOutput,
    increaseVolume: () -> Unit,
    decreaseVolume: () -> Unit,
    onAudioOutputClick: () -> Unit,
    showVolumeIndicator: Boolean = true
) {
    Box(
        modifier = modifier.fillMaxSize(),
    ) {
        Stepper(
            value = volume.current.toFloat(),
            onValueChange = { if (it > volume.current) increaseVolume() else decreaseVolume() },
            steps = volume.max - 1,
            valueRange = (0f..volume.max.toFloat()),
            increaseIcon = {
                Icon(
                    modifier = Modifier
                        .size(ButtonDefaults.DefaultButtonSize)
                        .padding(top = 8.dp),
                    imageVector = Icons.Default.VolumeUp,
                    contentDescription = "Volume Up"
                )
            },
            decreaseIcon = {
                Icon(
                    modifier = Modifier
                        .size(ButtonDefaults.DefaultButtonSize)
                        .padding(bottom = 8.dp),
                    imageVector = Icons.Default.VolumeDown,
                    contentDescription = "Volume Down",
                )
            },
        ) {
            Chip(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .align(Alignment.Center),
                label = { Text(text = audioOutput.name) },
                icon = {
                    Icon(
                        modifier = Modifier,
                        imageVector = audioOutput.icon(),
                        contentDescription = audioOutput.name,
                    )
                },
                onClick = onAudioOutputClick
            )
        }

        if (showVolumeIndicator) {
            PositionIndicator(
                value = { volume.current.toFloat() },
                range = (volume.min.toFloat()..volume.max.toFloat())
            )
        }
    }
}

@Composable
private fun AudioOutput.icon(): ImageVector {
    return when (this) {
        is AudioOutput.BluetoothHeadset -> Icons.Default.Headphones
        is AudioOutput.WatchSpeaker -> Icons.Default.Speaker
        is AudioOutput.None -> Icons.Default.VolumeOff
        is AudioOutput.Unknown -> Icons.Default.DeviceUnknown
    }
}