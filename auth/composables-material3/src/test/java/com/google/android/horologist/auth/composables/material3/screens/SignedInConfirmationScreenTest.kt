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

package com.google.android.horologist.auth.composables.material3.screens

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import com.google.android.horologist.auth.composables.material3.R
import com.google.android.horologist.images.base.paintable.DrawableResPaintable
import com.google.android.horologist.screenshots.rng.WearLegacyScreenTest
import org.junit.Test

class SignedInConfirmationScreenTest : WearLegacyScreenTest() {

    @Test
    fun signedInConfirmationScreen() {
        runTest {
            SignedInConfirmationDialogContent(
                modifier = Modifier.fillMaxSize(),
                name = "Maggie",
                email = "maggie123@example.com",
                avatar = DrawableResPaintable(R.drawable.horologist_avatar_small_3),
            )
        }
    }

    @Test
    fun signedInConfirmationScreenLongEmailAndName() {
        runTest {
            SignedInConfirmationDialogContent(
                modifier = Modifier.fillMaxSize(),
                name = "Extenta Namuratus Hereditus III",
                email = "thisisaverylongemailaccountsample@example.com",
                avatar = DrawableResPaintable(R.drawable.horologist_avatar_small_3),
            )
        }
    }

    @Test
    fun signedInConfirmationNoAvatar() {
        runTest {
            SignedInConfirmationDialogContent(
                modifier = Modifier.fillMaxSize(),
                name = "Maggie",
                email = "maggie123@example.com",
            )
        }
    }
}
