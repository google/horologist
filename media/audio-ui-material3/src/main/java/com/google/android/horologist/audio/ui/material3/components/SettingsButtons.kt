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

package com.google.android.horologist.audio.ui.material3.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.google.android.horologist.audio.ui.VolumeUiState
import com.google.android.horologist.audio.ui.material3.R
import com.google.android.horologist.audio.ui.material3.components.actions.VolumeButton

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
    val screenHeightDp = LocalConfiguration.current.screenHeightDp.dp
    Row(
        modifier = modifier.padding(
            top = screenHeightDp * TOP_PADDING,
            start = screenHeightDp * HORIZONTAL_PADDING,
            end = screenHeightDp * HORIZONTAL_PADDING,
            bottom = screenHeightDp * BOTTOM_PADDING,
        ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
    ) {
        Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.TopCenter) {
            VolumeButton(
                onVolumeClick = onVolumeClick,
                volumeUiState = volumeUiState,
                enabled = enabled,
            )
        }

        Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.BottomCenter) { brandIcon() }

        Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.TopCenter) {
            VolumeButton(
                onVolumeClick = onOutputClick,
                enabled = enabled,
                contentDescription = stringResource(R.string.horologist_audio_output_content_description),
            )
        }
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
            modifier =
                modifier.size(18.dp).clip(CircleShape).let { if (enabled) it else it.alpha(0.38f) },
            painter = painterResource(id = iconId),
            contentDescription = null,
        )
    }
}

private const val TOP_PADDING = 0.03f
private const val HORIZONTAL_PADDING = 0.125f
private const val BOTTOM_PADDING = 0.06f
