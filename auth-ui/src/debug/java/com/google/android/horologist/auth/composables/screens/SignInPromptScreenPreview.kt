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

@file:OptIn(ExperimentalHorologistAuthUiApi::class)

package com.google.android.horologist.auth.composables.screens

import androidx.compose.runtime.Composable
import com.google.android.horologist.auth.composables.ExperimentalHorologistAuthComposablesApi
import androidx.wear.compose.material.ScalingLazyListState
import com.google.android.horologist.auth.composables.chips.GuestModeChip
import com.google.android.horologist.auth.composables.chips.SignInChip
import com.google.android.horologist.auth.ui.ExperimentalHorologistAuthUiApi
import com.google.android.horologist.auth.ui.common.screens.SignInPromptScreen
import com.google.android.horologist.base.ui.components.StandardChipType
import com.google.android.horologist.compose.layout.belowTimeTextPreview
import com.google.android.horologist.compose.tools.WearPreviewDevices

@WearPreviewDevices
@Composable
fun SignInPromptScreenPreview() {
    SignInPromptScreen(
        message = "Send messages and create chat groups with your friends",
        onAlreadySignedIn = { },
        columnState = belowTimeTextPreview()
    ) {
        item {
            SignInChip(
                onClick = { },
                chipType = StandardChipType.Secondary
            )
        }
        item {
            GuestModeChip(
                onClick = { },
                chipType = StandardChipType.Secondary
            )
        }
    }
}
