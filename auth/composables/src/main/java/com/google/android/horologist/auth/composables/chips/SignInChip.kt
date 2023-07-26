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
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.wear.compose.material.ChipColors
import androidx.wear.compose.material.ChipDefaults
import com.google.android.horologist.annotations.ExperimentalHorologistApi
import com.google.android.horologist.auth.composables.R
import com.google.android.horologist.compose.material.Chip

/**
 * An opinionated [Chip] to represent the "Sign in" action.
 *
 * <img src="https://media.githubusercontent.com/media/google/horologist/main/docs/auth-composables/sign_in_chip.png" height="120" width="120" >
 *
 * @sample com.google.android.horologist.auth.sample.screens.googlesignin.prompt.GoogleSignInPromptSampleScreen
 */
@Composable
public fun SignInChip(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    label: String = stringResource(id = R.string.horologist_sign_in_chip_label),
    colors: ChipColors = ChipDefaults.primaryChipColors(),
    enabled: Boolean = true
) {
    Chip(
        label = label,
        onClick = onClick,
        modifier = modifier,
        icon = Icons.Default.AccountCircle,
        colors = colors,
        enabled = enabled
    )
}
