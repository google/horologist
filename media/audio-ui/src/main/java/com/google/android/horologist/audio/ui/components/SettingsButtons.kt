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

package com.google.android.horologist.audio.ui.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.google.android.horologist.audio.ui.VolumeUiState
import com.google.android.horologist.audio.ui.components.actions.AudioOutputButton
import com.google.android.horologist.audio.ui.components.actions.SetVolumeButton

/**
 * Settings buttons for a typical media app.
 * Set Volume and Select Audio Output.
 */
@Composable
public fun SettingsButtons(
    volumeUiState: VolumeUiState,
    onVolumeClick: () -> Unit,
    onOutputClick: () -> Unit,
    modifier: Modifier = Modifier,
    brandIcon: @Composable () -> Unit = {},
    enabled: Boolean = true,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
    ) {
        SetVolumeButton(
            onVolumeClick = onVolumeClick,
            volumeUiState = volumeUiState,
            enabled = enabled,
        )
        brandIcon()
        AudioOutputButton(
            onOutputClick = onOutputClick,
            enabled = enabled,
        )
    }
}

public object SettingsButtonsDefaults {
    @Composable
    public fun BrandIcon(
        @DrawableRes iconId: Int,
        modifier: Modifier = Modifier,
        enabled: Boolean = true,
    ) {
        Image(
            modifier = modifier.size(18.dp).clip(CircleShape).let {
                if (enabled) it else it.alpha(0.38f)
            },
            painter = painterResource(id = iconId),
            contentDescription = null,
        )
    }
}
