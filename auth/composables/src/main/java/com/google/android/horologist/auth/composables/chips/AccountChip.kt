/*
 * Copyright 2023 The Android Open Source Project
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
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.text.style.LineBreak
import androidx.wear.compose.material.ChipColors
import androidx.wear.compose.material.ChipDefaults
import androidx.wear.compose.material.MaterialTheme
import com.google.android.horologist.auth.composables.model.AccountUiModel
import com.google.android.horologist.compose.material.Chip

/**
 * A [Chip] to display the [AccountUiModel]'s email address and avatar.
 *
 * The email text has optimised line break parameters, in order to display as much text as it can.
 */
@Composable
public fun AccountChip(
    account: AccountUiModel,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    defaultAvatar: Any? = Icons.Default.AccountCircle,
    largeAvatar: Boolean = true,
    placeholder: Painter? = null,
    colors: ChipColors = ChipDefaults.primaryChipColors(),
    enabled: Boolean = true,
) {
    AccountChip(
        email = account.email,
        onClick = onClick,
        modifier = modifier,
        avatar = account.avatar,
        defaultAvatar = defaultAvatar,
        largeAvatar = largeAvatar,
        placeholder = placeholder,
        colors = colors,
        enabled = enabled,
    )
}

/**
 * A [Chip] to display an email address and avatar.
 *
 * The email text has optimised line break parameters, in order to display as much text as it can.
 */
@Composable
public fun AccountChip(
    email: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    avatar: Any? = null,
    defaultAvatar: Any? = Icons.Default.AccountCircle,
    largeAvatar: Boolean = true,
    placeholder: Painter? = null,
    colors: ChipColors = ChipDefaults.primaryChipColors(),
    enabled: Boolean = true,
) {
    MaterialTheme(
        typography = MaterialTheme.typography.copy(
            button = MaterialTheme.typography.button.copy(
                lineBreak = LineBreak(
                    strategy = LineBreak.Strategy.Balanced,
                    strictness = LineBreak.Strictness.Normal,
                    wordBreak = LineBreak.WordBreak.Default,
                ),
            ),
        ),
    ) {
        Chip(
            label = email,
            onClick = onClick,
            modifier = modifier,
            icon = avatar ?: defaultAvatar,
            largeIcon = largeAvatar,
            placeholder = placeholder,
            colors = colors,
            enabled = enabled,
        )
    }
}
