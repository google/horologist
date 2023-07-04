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

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.SendToMobile
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.wear.compose.material.ChipColors
import androidx.wear.compose.material.ChipDefaults
import androidx.wear.compose.material.Icon
import com.google.android.horologist.annotations.ExperimentalHorologistApi
import com.google.android.horologist.auth.composables.R
import com.google.android.horologist.compose.material.Chip
import com.google.android.horologist.compose.material.util.DECORATIVE_ELEMENT_CONTENT_DESCRIPTION

/**
 * An opinionated [Chip] to represent the "Create account" action.
 *
 * <img src="https://media.githubusercontent.com/media/google/horologist/main/docs/auth-composables/create_account_chip.png" height="120" width="120" >
 */
@ExperimentalHorologistApi
@Composable
public fun CreateAccountChip(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    label: String = stringResource(id = R.string.horologist_create_account_chip_label),
    largeIconSpace: Boolean = false,
    colors: ChipColors = ChipDefaults.primaryChipColors(),
    enabled: Boolean = true
) {
    if (largeIconSpace) {
        Chip(
            label = label,
            onClick = onClick,
            modifier = modifier,
            icon = {
                Box(
                    modifier = Modifier.size(ChipDefaults.LargeIconSize),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.SendToMobile,
                        contentDescription = DECORATIVE_ELEMENT_CONTENT_DESCRIPTION,
                        modifier = Modifier.size(ChipDefaults.IconSize)
                    )
                }
            },
            largeIcon = true,
            colors = colors,
            enabled = enabled
        )
    } else {
        Chip(
            label = label,
            onClick = onClick,
            modifier = modifier,
            icon = Icons.Outlined.SendToMobile,
            colors = colors,
            enabled = enabled
        )
    }
}
