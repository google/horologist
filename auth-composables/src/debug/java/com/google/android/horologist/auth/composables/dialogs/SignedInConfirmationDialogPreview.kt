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

package com.google.android.horologist.auth.composables.dialogs

import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Face
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.wear.compose.material.Icon
import com.google.android.horologist.auth.composables.ExperimentalHorologistAuthComposablesApi
import com.google.android.horologist.base.ui.util.DECORATIVE_ELEMENT_CONTENT_DESCRIPTION
import com.google.android.horologist.compose.tools.WearPreviewDevices

@WearPreviewDevices
@Composable
fun SignedInConfirmationDialogPreview() {
    SignedInConfirmationDialogContent(
        name = "Maggie",
        email = "maggie@example.com"
    )
}

@WearPreviewDevices
@Composable
fun SignedInConfirmationDialogPreviewNoName() {
    SignedInConfirmationDialogContent(
        email = "maggie@example.com"
    )
}

@WearPreviewDevices
@Composable
fun SignedInConfirmationDialogPreviewNoEmail() {
    SignedInConfirmationDialogContent(
        name = "Maggie"
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
        email = "wolfeschlegelsteinhausenbergerdorff@example.com"
    )
}

@WearPreviewDevices
@Composable
fun SignedInConfirmationDialogPreviewCustomAvatar() {
    SignedInConfirmationDialogContent(
        name = "Maggie",
        email = "maggie@example.com",
        avatar = {
            Icon(
                imageVector = Icons.Default.Face,
                contentDescription = DECORATIVE_ELEMENT_CONTENT_DESCRIPTION,
                modifier = Modifier
                    .size(AVATAR_SIZE)
                    .clip(CircleShape),
                tint = Color.Yellow
            )
        }
    )
}
