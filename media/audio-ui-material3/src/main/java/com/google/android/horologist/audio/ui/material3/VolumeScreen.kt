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

package com.google.android.horologist.audio.ui.material3

import android.media.AudioManager
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.VolumeDown
import androidx.compose.material.icons.automirrored.rounded.VolumeUp
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.wear.compose.foundation.rotary.rotaryScrollable
import androidx.wear.compose.material3.ButtonDefaults
import androidx.wear.compose.material3.ButtonDefaults.ButtonExtraLargeIconStartPadding
import androidx.wear.compose.material3.ColorScheme
import androidx.wear.compose.material3.Icon
import androidx.wear.compose.material3.MaterialTheme
import androidx.wear.compose.material3.Stepper
import androidx.wear.compose.material3.StepperDefaults
import androidx.wear.compose.material3.Text
import com.google.android.horologist.audio.AudioOutput
import com.google.android.horologist.audio.ui.VolumeUiState
import com.google.android.horologist.audio.ui.VolumeViewModel
import com.google.android.horologist.audio.ui.material3.components.AudioOutputUi
import com.google.android.horologist.audio.ui.material3.components.DeviceButton
import com.google.android.horologist.audio.ui.material3.components.toAudioOutputUi
import com.google.android.horologist.audio.ui.model.R
import kotlin.math.roundToInt

/**
 * Volume Screen with an [Stepper] and Increase/Decrease buttons for the Audio Stream Volume.
 *
 * Contains a Stepper with Up and Down buttons, plus a button to show the current [AudioOutput] and
 * prompt to select a new one.
 *
 * The volume and audio output come indirectly from the [AudioManager] and accessed via
 * [VolumeViewModel].
 *
 * See [VolumeViewModel] See [AudioManager.STREAM_MUSIC]
 */
@Composable
public fun VolumeScreen(
    modifier: Modifier = Modifier,
    volumeViewModel: VolumeViewModel = viewModel(factory = VolumeViewModel.Factory),
    showVolumeIndicator: Boolean = true,
    increaseIcon: @Composable () -> Unit = { VolumeScreenDefaults.IncreaseIcon() },
    decreaseIcon: @Composable () -> Unit = { VolumeScreenDefaults.DecreaseIcon() },
    colorScheme: ColorScheme = MaterialTheme.colorScheme,
) {
    val volumeUiState by volumeViewModel.volumeUiState.collectAsState()
    val audioOutput by volumeViewModel.audioOutput.collectAsState()

    VolumeScreen(
        modifier =
            modifier.rotaryScrollable(
                volumeRotaryBehavior(
                    volumeUiStateProvider = { volumeViewModel.volumeUiState.value },
                    onRotaryVolumeInput = { newVolume -> volumeViewModel.setVolume(newVolume) },
                ),
                focusRequester = remember { FocusRequester() },
            ),
        volume = { volumeUiState },
        audioOutputUi = audioOutput.toAudioOutputUi(),
        increaseVolume = { volumeViewModel.increaseVolume() },
        decreaseVolume = { volumeViewModel.decreaseVolume() },
        onAudioOutputClick = { volumeViewModel.launchOutputSelection() },
        showVolumeIndicator = showVolumeIndicator,
        increaseIcon = increaseIcon,
        decreaseIcon = decreaseIcon,
        colorScheme = colorScheme,
    )
}

/** Volume Screen with a Output Device button. */
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
    showVolumeIndicator: Boolean = true,
    colorScheme: ColorScheme = MaterialTheme.colorScheme,
) {
    VolumeScreen(
        volume = volume,
        content = {
            DeviceButton(
                modifier = Modifier.padding(horizontal = 18.dp),
                volumeDescription =
                    if (audioOutputUi.isConnected) {
                        stringResource(id = R.string.horologist_volume_screen_connected_state)
                    } else {
                        stringResource(id = R.string.horologist_volume_screen_not_connected_state)
                    },
                deviceName = audioOutputUi.displayName,
                icon = { Icon(imageVector = audioOutputUi.imageVector, contentDescription = null) },
                onAudioOutputClick = onAudioOutputClick,
                colors =
                    ButtonDefaults.buttonColors(
                        containerColor = colorScheme.primary,
                        contentColor = colorScheme.onPrimary,
                        secondaryContentColor = colorScheme.onPrimary.copy(alpha = 0.8f),
                        iconColor = colorScheme.onPrimary,
                    ),
            )
        },
        increaseVolume = increaseVolume,
        decreaseVolume = decreaseVolume,
        modifier = modifier,
        increaseIcon = increaseIcon,
        decreaseIcon = decreaseIcon,
        showVolumeIndicator = showVolumeIndicator,
        colorScheme = colorScheme,
    )
}

/** Volume Screen with default label. */
@Composable
public fun VolumeWithDefaultLabel(
    volume: () -> VolumeUiState,
    increaseVolume: () -> Unit,
    decreaseVolume: () -> Unit,
    modifier: Modifier = Modifier,
    increaseIcon: @Composable () -> Unit = { VolumeScreenDefaults.IncreaseIcon() },
    decreaseIcon: @Composable () -> Unit = { VolumeScreenDefaults.DecreaseIcon() },
    showVolumeIndicator: Boolean = true,
    colorScheme: ColorScheme = MaterialTheme.colorScheme,
) {
    VolumeScreen(
        volume = volume,
        content = {
            Text(
                stringResource(id = R.string.horologist_volume_screen_volume_label),
                maxLines = 1,
                overflow = TextOverflow.Clip,
                style = MaterialTheme.typography.labelMedium,
            )
        },
        increaseVolume = increaseVolume,
        decreaseVolume = decreaseVolume,
        modifier = modifier,
        increaseIcon = increaseIcon,
        decreaseIcon = decreaseIcon,
        showVolumeIndicator = showVolumeIndicator,
        colorScheme = colorScheme,
    )
}

/** Volume Screen with audio output as the label. */
@Composable
public fun VolumeWithAudioOutputAsLabel(
    volume: () -> VolumeUiState,
    audioOutputUi: AudioOutputUi,
    increaseVolume: () -> Unit,
    decreaseVolume: () -> Unit,
    modifier: Modifier = Modifier,
    increaseIcon: @Composable () -> Unit = { VolumeScreenDefaults.IncreaseIcon() },
    decreaseIcon: @Composable () -> Unit = { VolumeScreenDefaults.DecreaseIcon() },
    showVolumeIndicator: Boolean = true,
    colorScheme: ColorScheme = MaterialTheme.colorScheme,
) {
    VolumeScreen(
        volume = volume,
        content = {
            Row(
                modifier = Modifier.padding(horizontal = 18.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(
                    modifier = Modifier.wrapContentSize(align = Alignment.Center),
                    content = {
                        Icon(
                            imageVector = audioOutputUi.imageVector,
                            contentDescription = null,
                            tint = colorScheme.primary,
                        )
                    },
                )
                Spacer(modifier = Modifier.width(ButtonExtraLargeIconStartPadding))
                Box(
                    modifier = Modifier.wrapContentSize(align = Alignment.Center),
                    content = {
                        if (audioOutputUi.audioSourceDisplayName != null) {
                            Column {
                                Row(
                                    content = {
                                        Text(
                                            text = audioOutputUi.audioSourceDisplayName,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis,
                                            color = colorScheme.onSurface,
                                        )
                                    },
                                )
                                Spacer(modifier = Modifier.size(2.dp))
                                Row(
                                    content = {
                                        Text(
                                            text = audioOutputUi.displayName,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis,
                                            color = colorScheme.onSurfaceVariant,
                                            style = MaterialTheme.typography.labelSmall,
                                        )
                                    },
                                )
                            }
                        } else {
                            Text(
                                text = audioOutputUi.displayName,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis,
                                color = colorScheme.onSurface,
                            )
                        }
                    },
                )
            }
        },
        increaseVolume = increaseVolume,
        decreaseVolume = decreaseVolume,
        modifier = modifier,
        increaseIcon = increaseIcon,
        decreaseIcon = decreaseIcon,
        showVolumeIndicator = showVolumeIndicator,
        colorScheme = colorScheme,
    )
}

@Composable
internal fun VolumeScreen(
    volume: () -> VolumeUiState,
    increaseVolume: () -> Unit,
    decreaseVolume: () -> Unit,
    modifier: Modifier = Modifier,
    increaseIcon: @Composable () -> Unit = { VolumeScreenDefaults.IncreaseIcon() },
    decreaseIcon: @Composable () -> Unit = { VolumeScreenDefaults.DecreaseIcon() },
    showVolumeIndicator: Boolean = true,
    colorScheme: ColorScheme = MaterialTheme.colorScheme,
    content: @Composable () -> Unit,
) {
    val volumeState = volume()
    val volumePercent = (100f * volumeState.current / volumeState.max).roundToInt()
    val volumeDescription =
        if (volumeState.current == 0) {
            stringResource(id = R.string.horologist_volume_screen_volume_zero)
        } else {
            stringResource(id = R.string.horologist_volume_screen_volume_percent, volumePercent)
        }

    Box(modifier = Modifier.fillMaxSize()) {
        val currentValue = volumeState.current.toFloat()
        Stepper(
            modifier =
                modifier.semantics {
                    liveRegion = LiveRegionMode.Assertive
                    contentDescription = volumeDescription
                },
            value = currentValue,
            onValueChange = { if (it > volumeState.current) increaseVolume() else decreaseVolume() },
            steps = volumeState.max - 1,
            increaseIcon = { increaseIcon() },
            decreaseIcon = { decreaseIcon() },
            colors =
                StepperDefaults.colors(
                    contentColor = colorScheme.onSurface,
                    buttonContainerColor = colorScheme.primaryContainer,
                    buttonIconColor = colorScheme.primary,
                    disabledContentColor = colorScheme.onSurface.toDisabledColor(DisabledContentAlpha),
                    disabledButtonContainerColor =
                        colorScheme.onSurface.toDisabledColor(DisabledContainerAlpha),
                    disabledButtonIconColor = colorScheme.onSurface.toDisabledColor(DisabledContentAlpha),
                ),
        ) {
            content()
        }
        if (showVolumeIndicator) {
            VolumeLevelIndicator(volumeUiState = volume, colorScheme = colorScheme)
        }
    }
}

public object VolumeScreenDefaults {
    @Composable
    public fun IncreaseIcon() {
        Icon(
            modifier = Modifier.size(StepperDefaults.IconSize),
            imageVector = Icons.AutoMirrored.Rounded.VolumeUp,
            contentDescription =
                stringResource(id = R.string.horologist_volume_screen_volume_up_content_description),
        )
    }

    @Composable
    public fun DecreaseIcon() {
        Icon(
            modifier = Modifier.size(StepperDefaults.IconSize),
            imageVector = Icons.AutoMirrored.Rounded.VolumeDown,
            contentDescription =
                stringResource(id = R.string.horologist_volume_screen_volume_down_content_description),
        )
    }
}
