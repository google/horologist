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
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.VolumeDown
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.wear.compose.foundation.rememberActiveFocusRequester
import androidx.wear.compose.material.ContentAlpha
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material.InlineSlider
import androidx.wear.compose.material.LocalContentAlpha
import androidx.wear.compose.material.LocalContentColor
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.contentColorFor
import com.google.android.horologist.audio.AudioOutput
import com.google.android.horologist.audio.ui.components.AudioOutputUi
import com.google.android.horologist.audio.ui.components.DeviceChip
import com.google.android.horologist.audio.ui.components.toAudioOutputUi
import com.google.android.horologist.audio.ui.state.VolumeViewModel
import com.google.android.horologist.audio.ui.state.model.VolumeUiState
import com.google.android.horologist.compose.navscaffold.ExperimentalHorologistComposeLayoutApi

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
@ExperimentalHorologistAudioUiApi
@OptIn(ExperimentalHorologistComposeLayoutApi::class)
public fun VolumeScreen(
    modifier: Modifier = Modifier,
    volumeViewModel: VolumeViewModel = viewModel(factory = VolumeViewModel.Factory),
    showVolumeIndicator: Boolean = true,
    increaseIcon: @Composable () -> Unit = { VolumeScreenDefaults.IncreaseIcon() },
    decreaseIcon: @Composable () -> Unit = { VolumeScreenDefaults.DecreaseIcon() }
) {
    val volumeUiState by volumeViewModel.volumeUiState.collectAsState()
    val audioOutput by volumeViewModel.audioOutput.collectAsState()
    val focusRequester: FocusRequester = rememberActiveFocusRequester()

    VolumeScreen(
        modifier = modifier.rotaryVolumeControls(
            focusRequester = focusRequester,
            onRotaryVolumeInput = volumeViewModel::changeVolume
        ),
        volumeUiState = volumeUiState,
        audioOutputUi = audioOutput.toAudioOutputUi(),
        increaseVolume = volumeViewModel::increaseVolume,
        decreaseVolume = volumeViewModel::decreaseVolume,
        onAudioOutputClick = volumeViewModel::launchOutputSelection,
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
    volumeUiState: VolumeUiState,
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
        volumeUiState = volumeUiState,
        contentSlot = {
            DeviceChip(
                modifier = Modifier.padding(horizontal = 18.dp),
                volumeDescription = volumeDescription(volumeUiState, audioOutputUi.isConnected),
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
    volumeUiState: VolumeUiState,
    increaseVolume: () -> Unit,
    decreaseVolume: () -> Unit,
    modifier: Modifier = Modifier,
    increaseIcon: @Composable () -> Unit = { VolumeScreenDefaults.IncreaseIcon() },
    decreaseIcon: @Composable () -> Unit = { VolumeScreenDefaults.DecreaseIcon() },
    showVolumeIndicator: Boolean = true
) {
    VolumeScreen(
        volumeUiState = volumeUiState,
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
    volumeUiState: VolumeUiState,
    contentSlot: @Composable () -> Unit,
    increaseVolume: () -> Unit,
    decreaseVolume: () -> Unit,
    modifier: Modifier = Modifier,
    increaseIcon: @Composable () -> Unit = { VolumeScreenDefaults.IncreaseIcon() },
    decreaseIcon: @Composable () -> Unit = { VolumeScreenDefaults.DecreaseIcon() },
    showVolumeIndicator: Boolean = true
) {
    Box(modifier = modifier.fillMaxSize())
    val backgroundColor = MaterialTheme.colors.background
    val contentColor = contentColorFor(backgroundColor)
    Column(
        modifier = modifier.fillMaxSize().background(backgroundColor),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Increase button.
        FullScreenButton(
            onClick = { increaseVolume() },
            contentAlignment = Alignment.TopCenter,
            paddingValues = PaddingValues(top = 22.dp),
            iconColor = contentColor,
            enabled = !volumeUiState.isMax,
            content = increaseIcon
        )
        Box(
            modifier = Modifier.fillMaxWidth().weight(0.3f),
            contentAlignment = Alignment.Center
        ) {
            CompositionLocalProvider(
                LocalContentColor provides contentColor
            ) {
                contentSlot()
            }
        }
        // Decrease button.
        FullScreenButton(
            onClick = { decreaseVolume() },
            contentAlignment = Alignment.BottomCenter,
            paddingValues = PaddingValues(bottom = 22.dp),
            iconColor = contentColor,
            enabled = !volumeUiState.isMin,
            content = decreaseIcon
        )
    }
    if (showVolumeIndicator && volumeUiState.current != null) {
        VolumePositionIndicator(
            volume = volumeUiState.current,
            autoHide = false
        )
    }
}

@Composable
private fun ColumnScope.FullScreenButton(
    onClick: () -> Unit,
    contentAlignment: Alignment,
    paddingValues: PaddingValues,
    iconColor: Color,
    enabled: Boolean,
    content: @Composable () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val contentColor = if (enabled) iconColor else iconColor.copy(alpha = ContentAlpha.disabled)
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .weight(0.35f)
            .clickable(
                interactionSource,
                null,
                onClick = onClick,
                enabled = enabled,
                role = Role.Button
            )
            .wrapContentWidth()
            .indication(interactionSource, rememberRipple(bounded = false))
            .padding(paddingValues),
        contentAlignment = contentAlignment
    ) {
        CompositionLocalProvider(
            LocalContentColor provides contentColor,
            LocalContentAlpha provides contentColor.alpha,
            content = content
        )
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
private fun volumeDescription(volumeUiState: VolumeUiState, isAudioOutputConnected: Boolean): String {
    return if (isAudioOutputConnected) {
        if (volumeUiState.current != null) {
            stringResource(
                id = R.string.horologist_volume_screen_connected_state,
                volumeUiState.current
            )
        } else {
            stringResource(id = R.string.horologist_volume_screen_not_connected_state)
        }
    } else {
        stringResource(id = R.string.horologist_volume_screen_not_connected_state)
    }
}
