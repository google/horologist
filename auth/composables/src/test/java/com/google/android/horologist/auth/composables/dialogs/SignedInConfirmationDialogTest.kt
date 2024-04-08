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

import android.R
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.google.android.horologist.images.base.paintable.DrawableResPaintable
import com.google.android.horologist.screenshots.rng.WearLegacyScreenTest
import org.junit.Test

class SignedInConfirmationDialogTest : WearLegacyScreenTest() {

    @Test
    fun signedInConfirmationDialog() {
        runTest {
            Box(
                modifier = Modifier.background(Color.Black),
                contentAlignment = Alignment.Center,
            ) {
                SignedInConfirmationDialog(
                    onDismissOrTimeout = {},
                    name = "Maggie",
                    email = "maggie@example.com",
                    avatar = DrawableResPaintable(R.mipmap.sym_def_app_icon),
                )
            }
        }
    }

    @Test
    fun signedInConfirmationDialogNoName() {
        runTest {
            SignedInConfirmationDialog(
                onDismissOrTimeout = {},
                email = "maggie@example.com",
                avatar = DrawableResPaintable(R.mipmap.sym_def_app_icon),
            )
        }
    }

    @Test
    fun signedInConfirmationDialogEmptyName() {
        runTest {
            SignedInConfirmationDialog(
                onDismissOrTimeout = {},
                name = "",
                email = "maggie@example.com",
                avatar = DrawableResPaintable(R.mipmap.sym_def_app_icon),
            )
        }
    }

    @Test
    fun signedInConfirmationDialogNoNameNoAvatar() {
        runTest {
            SignedInConfirmationDialog(
                onDismissOrTimeout = {},
                email = "maggie@example.com",
            )
        }
    }

    @Test
    fun signedInConfirmationDialogNoEmail() {
        runTest {
            SignedInConfirmationDialog(
                onDismissOrTimeout = {},
                name = "Maggie",
                avatar = DrawableResPaintable(R.mipmap.sym_def_app_icon),
            )
        }
    }

    @Test
    fun signedInConfirmationDialogNoInformation() {
        runTest {
            SignedInConfirmationDialog(onDismissOrTimeout = {})
        }
    }

    @Test
    fun signedInConfirmationDialogTruncation() {
        runTest {
            SignedInConfirmationDialog(
                onDismissOrTimeout = {},
                name = "Wolfeschlegelsteinhausenbergerdorff",
                email = "wolfeschlegelsteinhausenbergerdorff@example.com",
                avatar = DrawableResPaintable(R.mipmap.sym_def_app_icon),
            )
        }
    }
}
