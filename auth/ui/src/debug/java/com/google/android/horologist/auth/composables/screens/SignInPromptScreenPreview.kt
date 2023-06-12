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

package com.google.android.horologist.auth.composables.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.wear.compose.material.ChipDefaults
import androidx.wear.compose.material.Text
import androidx.wear.compose.ui.tooling.preview.WearPreviewDevices
import com.google.android.horologist.auth.composables.chips.GuestModeChip
import com.google.android.horologist.auth.composables.chips.SignInChip
import com.google.android.horologist.auth.ui.common.screens.prompt.SignInPromptScreen
import com.google.android.horologist.auth.ui.common.screens.prompt.SignInPromptScreenState
import com.google.android.horologist.compose.layout.belowTimeTextPreview

@WearPreviewDevices
@Composable
fun SignInPromptScreenPreviewSignedOut() {
    SignInPromptScreen(
        state = SignInPromptScreenState.SignedOut,
        title = "Sign in",
        message = "Send messages and create chat groups with your friends",
        onIdleStateObserved = { },
        onAlreadySignedIn = { },
        columnState = belowTimeTextPreview()
    ) {
        item {
            SignInChip(
                onClick = { },
                colors = ChipDefaults.secondaryChipColors()
            )
        }
        item {
            GuestModeChip(
                onClick = { },
                colors = ChipDefaults.secondaryChipColors()
            )
        }
    }
}

@WearPreviewDevices
@Composable
fun SignInPromptScreenPreviewLoading() {
    SignInPromptScreen(
        state = SignInPromptScreenState.Loading,
        title = "Sign in",
        message = "Send messages and create chat groups with your friends",
        onIdleStateObserved = { },
        onAlreadySignedIn = { },
        columnState = belowTimeTextPreview()
    ) {
        item {
            SignInChip(
                onClick = { },
                colors = ChipDefaults.secondaryChipColors()
            )
        }
        item {
            GuestModeChip(
                onClick = { },
                colors = ChipDefaults.secondaryChipColors()
            )
        }
    }
}

@WearPreviewDevices
@Composable
fun SignInPromptScreenPreviewCustomLoading() {
    SignInPromptScreen(
        state = SignInPromptScreenState.Loading,
        title = "Sign in",
        message = "Send messages and create chat groups with your friends",
        onIdleStateObserved = { },
        onAlreadySignedIn = { },
        columnState = belowTimeTextPreview(),
        loadingContent = {
            Box(contentAlignment = Alignment.Center) {
                Text("Loading...")
            }
        }
    ) {
        item {
            SignInChip(
                onClick = { },
                colors = ChipDefaults.secondaryChipColors()
            )
        }
        item {
            GuestModeChip(
                onClick = { },
                colors = ChipDefaults.secondaryChipColors()
            )
        }
    }
}
