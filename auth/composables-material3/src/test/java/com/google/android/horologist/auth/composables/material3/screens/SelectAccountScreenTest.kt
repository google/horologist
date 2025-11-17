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

import com.google.android.horologist.auth.composables.material3.R
import com.google.android.horologist.auth.composables.material3.models.AccountUiModel
import com.google.android.horologist.images.base.paintable.DrawableResPaintable
import com.google.android.horologist.screenshots.rng.WearLegacyScreenTest
import org.junit.Test

class SelectAccountScreenTest : WearLegacyScreenTest() {

    @Test
    fun selectAccountScreen() {
        runTest {
            SelectAccountScreen(
                accounts = listOf(
                    AccountUiModel(
                        email = "timandrews123@example.com",
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

    @Test
    fun selectAccountScreenNoAvatar() {
        runTest {
            SelectAccountScreen(
                accounts = listOf(
                    AccountUiModel(
                        email = "thisisaverylongemailaccountsample@example.com",
                        name = "Extenta Namuratus Hereditus III",
                        avatar = null,
                    ),
                    AccountUiModel(
                        email = "timandrews123@example.com",
                        name = "Timothy Andrews",
                        avatar = null,
                    ),
                    AccountUiModel(
                        email = "thisisaverylongemailaccountsample@example.com",
                        name = "Kim Wong",
                        avatar = null,
                    ),
                ),
                onAccountClicked = { _, _ -> },
                title = "Select Account",
            )
        }
    }
}
