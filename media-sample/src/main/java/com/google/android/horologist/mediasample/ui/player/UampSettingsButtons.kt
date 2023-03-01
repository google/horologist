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

import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.google.android.horologist.audio.ui.components.SettingsButtonsDefaults
import com.google.android.horologist.audio.ui.components.actions.SetVolumeButton
import com.google.android.horologist.audio.ui.state.model.VolumeUiState
import com.google.android.horologist.mediasample.R

/**
 * Settings buttons for the UAMP media app.
 * Favorite item and Set Volume.
 */
@Composable
public fun UampSettingsButtons(
    volumeUiState: VolumeUiState,
    onVolumeClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        FavoriteButton()

        SettingsButtonsDefaults.BrandIcon(
            iconId = R.drawable.ic_uamp,
            enabled = enabled
        )

        SetVolumeButton(
            onVolumeClick = onVolumeClick,
            volumeUiState = volumeUiState
        )
    }
}
