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

import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.onClick
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.style.TextOverflow
import androidx.wear.compose.material.Chip
import androidx.wear.compose.material.ChipDefaults
import androidx.wear.compose.material.Text
import com.google.android.horologist.audio.ui.R

@Composable
public fun DeviceChip(
    volumeDescription: String,
    deviceName: String,
    icon: @Composable BoxScope.() -> Unit,
    onAudioOutputClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val onClickLabel = stringResource(id = R.string.horologist_volume_screen_change_audio_output)

    Chip(
        modifier = modifier
            .width(intrinsicSize = IntrinsicSize.Max)
            .semantics {
                stateDescription = volumeDescription
                onClick(onClickLabel) {
                    onAudioOutputClick()
                    true
                }
            },
        label = {
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = deviceName,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
        },
        icon = icon,
        onClick = onAudioOutputClick,
        // Device chip uses secondary colors (surface/onSurface)
        colors = ChipDefaults.secondaryChipColors(),
    )
}
