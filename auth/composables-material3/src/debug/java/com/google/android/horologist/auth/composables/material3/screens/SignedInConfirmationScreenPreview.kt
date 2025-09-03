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

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.wear.compose.ui.tooling.preview.WearPreviewDevices
import com.google.android.horologist.auth.composables.material3.R
import com.google.android.horologist.auth.composables.material3.theme.HorologistMaterialTheme
import com.google.android.horologist.images.base.paintable.DrawableResPaintable

@WearPreviewDevices
@Composable
fun SignedInConfirmationScreenPreview() {
    HorologistMaterialTheme {
        SignedInConfirmationDialogContent(
            modifier = Modifier.fillMaxSize(),
            name = "Maggie",

            avatar = DrawableResPaintable(R.drawable.horologist_avatar_small_3),
        )
    }
}

@WearPreviewDevices
@Composable
fun SignedInConfirmationMMMScreenPreview() {
    HorologistMaterialTheme {
        SignedInConfirmationDialogContent(
            modifier = Modifier.fillMaxSize(),
            name = "MMMMMMMMM",
            email = "MMMMMMMMMMMMMMMMMMMMMMMM",
            avatar = DrawableResPaintable(R.drawable.horologist_avatar_small_3),
        )
    }
}

@WearPreviewDevices
@Composable
fun SignedInConfirmationScreenContentPreview() {
    HorologistMaterialTheme {
        SignedInConfirmationDialogContent(
            modifier = Modifier.fillMaxSize(),
            name = "Maggie",
            email = "maggiesveryveryverylongworkemail@example.com",
            avatar = DrawableResPaintable(R.drawable.horologist_avatar_small_3),
        )
    }
}

@WearPreviewDevices
@Composable
fun SignedInConfirmationNoAvatar() {
    HorologistMaterialTheme {
        SignedInConfirmationDialogContent(
            modifier = Modifier.fillMaxSize(),
            name = "Timothy",
            email = "timandrews123@example.com",
        )
    }
}

@WearPreviewDevices
@Composable
fun SignedInConfirmationScreenPreviewNoName() {
    SignedInConfirmationScreen(
        onDismissOrTimeout = {},
        email = "timandrews123@example.com",
        avatar = DrawableResPaintable(R.drawable.horologist_avatar_small_3),
    )
}

@WearPreviewDevices
@Composable
fun SignedInConfirmationScreenPreviewNoEmail() {
    SignedInConfirmationScreen(
        onDismissOrTimeout = {},
        name = "Maggie",
    )
}

@WearPreviewDevices
@Composable
fun SignedInConfirmationScreenPreviewNoInformation() {
    SignedInConfirmationScreen(onDismissOrTimeout = {})
}

@WearPreviewDevices
@Composable
fun SignedInConfirmationScreenPreviewTruncation() {
    SignedInConfirmationScreen(
        onDismissOrTimeout = {},
        name = "Wolfeschlegelsteinhausenbergerdorff",
        email = "wolfeschlegelsteinhausenbergerdorff@example.com",
    )
}
