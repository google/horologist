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
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.google.android.horologist.audio.VolumeState
import com.google.android.horologist.audio.ui.ExperimentalHorologistAudioUiApi
import com.google.android.horologist.audio.ui.components.actions.AudioOutputButton
import com.google.android.horologist.audio.ui.components.actions.SetVolumeButton

/**
 * Settings buttons for a typical media app.
 * Set Volume and Select Audio Output.
 */
@ExperimentalHorologistAudioUiApi
@Composable
public fun SettingsButtons(
    volumeState: VolumeState,
    onVolumeClick: () -> Unit,
    onOutputClick: () -> Unit,
    brandIcon: @Composable () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        SetVolumeButton(
            onVolumeClick = onVolumeClick,
            volumeState = volumeState
        )
        Spacer(modifier = Modifier.size(8.dp))
        brandIcon()
        Spacer(modifier = Modifier.size(8.dp))
        AudioOutputButton(
            onOutputClick = onOutputClick
        )
    }
}

public object SettingsButtonsDefaults {
    @Composable
    fun BrandIcon(@DrawableRes iconId: Int) {
        Image(
            modifier = Modifier.size(18.dp).clip(CircleShape),
            painter = painterResource(id = iconId),
            contentDescription = null
        )
    }
}
