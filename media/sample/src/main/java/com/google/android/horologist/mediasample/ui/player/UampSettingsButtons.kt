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

package com.google.android.horologist.mediasample.ui.player

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import com.google.android.horologist.audio.ui.VolumeUiState
import com.google.android.horologist.audio.ui.components.AudioOutputUi
import com.google.android.horologist.audio.ui.components.SettingsButtonsDefaults
import com.google.android.horologist.audio.ui.components.actions.SetAudioOutputButton
import com.google.android.horologist.logo.R

/**
 * Settings buttons for the UAMP media app.
 * Favorite item and Set Volume.
 */
@Composable
public fun UampSettingsButtons(
    volumeUiState: VolumeUiState,
    audioOutputUi: AudioOutputUi,
    onVolumeClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    val configuration = LocalConfiguration.current
    val verticalPadding = (configuration.screenWidthDp * VERTICAL_PADDING_SCREEN_PERCENTAGE).dp

    Row(
        modifier = modifier
            .fillMaxSize()
            .padding(vertical = verticalPadding),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
    ) {
        FavoriteButton(
            modifier = Modifier.weight(1f),
            iconAlignment = Alignment.TopCenter,
        )

        SettingsButtonsDefaults.BrandIcon(
            modifier = Modifier
                .align(Alignment.Bottom)
                .weight(1f)
                .size(16.dp),
            iconId = R.drawable.ic_stat_horologist,
            enabled = enabled,
        )

        SetAudioOutputButton(
            modifier = Modifier.weight(1f),
            onVolumeClick = onVolumeClick,
            volumeUiState = volumeUiState,
            audioOutputUi = audioOutputUi,
            iconAlignment = Alignment.TopCenter,
        )
    }
}

private const val VERTICAL_PADDING_SCREEN_PERCENTAGE = 0.026f
