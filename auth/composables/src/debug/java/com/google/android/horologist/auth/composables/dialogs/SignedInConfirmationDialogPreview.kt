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

package com.google.android.horologist.auth.composables.dialogs

import androidx.compose.runtime.Composable
import androidx.wear.compose.ui.tooling.preview.WearPreviewDevices

@WearPreviewDevices
@Composable
fun SignedInConfirmationDialogPreview() {
    SignedInConfirmationDialogContent(
        name = "Maggie",
        email = "maggie@example.com",
    )
}

@WearPreviewDevices
@Composable
fun SignedInConfirmationDialogPreviewNoName() {
    SignedInConfirmationDialogContent(
        email = "maggie@example.com",
    )
}

@WearPreviewDevices
@Composable
fun SignedInConfirmationDialogPreviewNoEmail() {
    SignedInConfirmationDialogContent(
        name = "Maggie",
    )
}

@WearPreviewDevices
@Composable
fun SignedInConfirmationDialogPreviewNoInformation() {
    SignedInConfirmationDialogContent()
}

@WearPreviewDevices
@Composable
fun SignedInConfirmationDialogPreviewTruncation() {
    SignedInConfirmationDialogContent(
        name = "Wolfeschlegelsteinhausenbergerdorff",
        email = "wolfeschlegelsteinhausenbergerdorff@example.com",
    )
}
