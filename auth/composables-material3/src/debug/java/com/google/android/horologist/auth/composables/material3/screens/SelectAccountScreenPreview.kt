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

package com.google.android.horologist.auth.composables.material3.screens

import androidx.compose.runtime.Composable
import androidx.wear.compose.ui.tooling.preview.WearPreviewDevices
import com.google.android.horologist.auth.composables.material3.R
import com.google.android.horologist.auth.composables.material3.models.AccountUiModel
import com.google.android.horologist.auth.composables.material3.theme.HorologistMaterialTheme
import com.google.android.horologist.images.base.paintable.DrawableResPaintable

@WearPreviewDevices
@Composable
fun SelectAccountScreenPreview() {
    HorologistMaterialTheme {
        SelectAccountScreen(
            accounts = listOf(
                AccountUiModel(
                    email = "john@example.com",
                    name = "John Doe",
                ),
                AccountUiModel(
                    email = "tim@example.com",
                    name = "Timothy Andrews",
                    avatar = DrawableResPaintable(R.drawable.horologist_avatar_small_1),
                ),
                AccountUiModel(
                    email = "thisisaverylongemailaccountsample@example.com",
                    name = "Kim Wong",
                    avatar = DrawableResPaintable(R.drawable.horologist_avatar_small_2),
                ),
            ),
            onAccountClicked = { _, _ -> },
            title = "Select Account",
        )
    }
}

@WearPreviewDevices
@Composable
fun SelectAccountScreenManyAccountsPreview() {
    HorologistMaterialTheme {
        SelectAccountScreen(
            accounts = listOf(
                AccountUiModel(
                    email = "thisisaverylongemailaccountsample@example.com",
                    name = "Extenta Namuratus Hereditus III",
                ),
                AccountUiModel(
                    email = "timandrews123@example.com",
                    name = "Timothy Andrews",
                ),
                AccountUiModel(
                    email = "john@example.com",
                    name = "John Doe",
                ),
                AccountUiModel(
                    email = "john@example.com",
                    name = "John Doe",
                ),
            ),
            onAccountClicked = { _, _ -> },
            title = "Select Account",
        )
    }
}

@WearPreviewDevices
@Composable
fun SelectAccountScreenOneLineAccountsPreview() {
    HorologistMaterialTheme {
        SelectAccountScreen(
            accounts = listOf(
                AccountUiModel(
                    email = "john@example.com",
                    name = "John Doe",
                ),
                AccountUiModel(
                    email = "timandrews123@example.com",
                    name = "Tim Andrews",
                    avatar = DrawableResPaintable(R.drawable.horologist_avatar_small_1),
                ),
            ),
            onAccountClicked = { _, _ -> },
            title = "Select Account",
        )
    }
}
