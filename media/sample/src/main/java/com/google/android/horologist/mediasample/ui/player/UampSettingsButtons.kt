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
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import com.google.android.horologist.audio.AudioOutput
import com.google.android.horologist.audio.ui.VolumeUiState
import com.google.android.horologist.audio.ui.components.AudioOutputUi
import com.google.android.horologist.audio.ui.material3.components.actions.VolumeButtonWithBadge
import com.google.android.horologist.audio.ui.material3.components.toAudioOutputUi
import kotlin.math.ceil

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
    val horizontalPadding = remember(configuration) {
        ceil(configuration.screenWidthDp * 10f / 100f).dp
    }
    val bottomPadding = remember(configuration) {
        ceil(configuration.screenHeightDp * 12f / 100f).dp
    }

    Row(
        modifier = modifier
            .padding(horizontal = horizontalPadding)
            .padding(bottom = bottomPadding),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceEvenly,
    ) {
        Box(modifier = Modifier.weight(1f).fillMaxHeight()) {
            VolumeButtonWithBadge(
                onOutputClick = onVolumeClick,
                audioOutputUi = AudioOutput.BluetoothHeadset(id = "id", name = "name")
                    .toAudioOutputUi(),
                volumeUiState = volumeUiState,
                enabled = enabled,
            )
        }

        Box(modifier = Modifier.weight(1f).fillMaxHeight()) {
            FavoriteButton()
        }
    }
}
