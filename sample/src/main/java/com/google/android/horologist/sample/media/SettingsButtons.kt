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

package com.google.android.horologist.sample.media

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.android.horologist.audio.VolumeState

/**
 * Settings buttons for a typical media app.
 * Set Volume and Select Audio Output.
 */
@Composable
fun SettingsButtons(
    volumeState: VolumeState,
    onVolumeClick: () -> Unit,
    onOutputClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(modifier = modifier) {
        SetVolumeButton(
            onVolumeClick = onVolumeClick,
            volumeState = volumeState
        )
        Spacer(modifier = Modifier.size(16.dp))
        AudioOutputButton(
            onOutputClick = onOutputClick
        )
    }
}
