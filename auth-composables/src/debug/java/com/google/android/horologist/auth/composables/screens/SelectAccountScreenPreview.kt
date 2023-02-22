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

@file:OptIn(ExperimentalHorologistAuthComposablesApi::class)

package com.google.android.horologist.auth.composables.screens

import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Android
import androidx.compose.material.icons.filled.Face
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material.MaterialTheme
import com.google.android.horologist.auth.composables.ExperimentalHorologistAuthComposablesApi
import com.google.android.horologist.auth.composables.model.AccountUiModel
import com.google.android.horologist.base.ui.util.DECORATIVE_ELEMENT_CONTENT_DESCRIPTION
import com.google.android.horologist.compose.layout.belowTimeTextPreview
import com.google.android.horologist.compose.tools.WearPreviewDevices

@WearPreviewDevices
@Composable
fun SelectAccountScreenPreview() {
    SelectAccountScreen(
        accounts = listOf(
            AccountUiModel(email = "maggie@example.com"),
            AccountUiModel(email = "thisisaverylongemail@example.com")
        ),
        onAccountClicked = { _, _ -> },
        columnState = belowTimeTextPreview()
    )
}

@WearPreviewDevices
@Composable
fun SelectAccountScreenPreviewNoAvatar() {
    SelectAccountScreen(
        accounts = listOf(
            AccountUiModel(email = "maggie@example.com"),
            AccountUiModel(email = "thisisaverylongemailaccountsample@example.com")
        ),
        onAccountClicked = { _, _ -> },
        columnState = belowTimeTextPreview(),
        defaultAvatar = null
    )
}

@WearPreviewDevices
@Composable
fun SelectAccountScreenPreviewCustomIcon() {
    data class MyAccountModel(val label: String)

    SelectAccountScreen(
        accounts = listOf(
            MyAccountModel(label = "maggie@example.com"),
            MyAccountModel(label = "thisisaverylongemail@example.com")
        ),
        label = MyAccountModel::label,
        avatarContent = { account ->
            val icon = if (account.label.startsWith("m")) {
                Icons.Default.Android
            } else {
                Icons.Default.Face
            }
            Icon(
                imageVector = icon,
                contentDescription = DECORATIVE_ELEMENT_CONTENT_DESCRIPTION,
                modifier = Modifier
                    .align(Alignment.Center)
                    .size(32.dp)
                    .clip(CircleShape),
                tint = MaterialTheme.colors.onSurfaceVariant
            )
        },
        onAccountClicked = { _, _ -> },
        columnState = belowTimeTextPreview()
    )
}
