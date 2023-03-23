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

package com.google.android.horologist.audio.ui.components.actions

import androidx.compose.runtime.Composable
import com.google.android.horologist.audio.ui.R
import com.google.android.horologist.audio.ui.VolumeUiState
import com.google.android.horologist.audio.ui.components.SettingsButtons
import com.google.android.horologist.audio.ui.components.SettingsButtonsDefaults.BrandIcon
import com.google.android.horologist.compose.tools.WearPreview

@WearPreview
@Composable
fun SettingsButtonsPreview() {
    SettingsButtons(
        volumeUiState = VolumeUiState(4, 10),
        onVolumeClick = {},
        onOutputClick = {}
    )
}

@WearPreview
@Composable
fun SettingsButtonsWithBrandIconPreview() {
    SettingsButtons(
        volumeUiState = VolumeUiState(5, 10),
        onVolumeClick = {},
        onOutputClick = {},
        brandIcon = {
            BrandIcon(R.drawable.ic_stat_horologist, enabled = true)
        }
    )
}

@WearPreview
@Composable
fun SettingsButtonsDisabledPreview() {
    SettingsButtons(
        volumeUiState = VolumeUiState(5, 10),
        onVolumeClick = {},
        onOutputClick = {},
        enabled = false,
        brandIcon = {
            BrandIcon(R.drawable.ic_stat_horologist, enabled = false)
        }
    )
}
