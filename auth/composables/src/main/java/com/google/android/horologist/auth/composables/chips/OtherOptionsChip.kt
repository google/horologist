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

package com.google.android.horologist.auth.composables.chips

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SendToMobile
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.google.android.horologist.annotations.ExperimentalHorologistApi
import com.google.android.horologist.auth.composables.R
import com.google.android.horologist.compose.material.Chip
import com.google.android.horologist.compose.material.ChipType

/**
 * An opinionated [Chip] to represent the "Other options to authentication" action.
 *
 * <img src="https://media.githubusercontent.com/media/google/horologist/main/docs/auth-composables/other_options_chip.png" height="120" width="120" >
 */
@ExperimentalHorologistApi
@Composable
public fun OtherOptionsChip(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    label: String = stringResource(id = R.string.horologist_other_options_chip_label),
    chipType: ChipType = ChipType.Primary,
    enabled: Boolean = true
) {
    Chip(
        label = label,
        onClick = onClick,
        modifier = modifier,
        icon = Icons.Default.SendToMobile,
        chipType = chipType,
        enabled = enabled
    )
}
