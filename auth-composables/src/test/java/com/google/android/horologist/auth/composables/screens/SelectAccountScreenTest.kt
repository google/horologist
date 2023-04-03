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

@file:OptIn(
    ExperimentalHorologistApi::class
)

package com.google.android.horologist.auth.composables.screens

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Face
import com.google.android.horologist.annotations.ExperimentalHorologistApi
import com.google.android.horologist.auth.composables.model.AccountUiModel
import com.google.android.horologist.screenshots.ScreenshotTest
import com.google.android.horologist.test.toolbox.positionedState
import org.junit.Test

class SelectAccountScreenTest: ScreenshotTest() {
    init {
        screenTimeText = {}
    }

    @Test
    fun selectAccountScreen() {
        takeScreenshot {
            SelectAccountScreen(
                accounts = listOf(
                    AccountUiModel(
                        email = "maggie@example.com",
                        avatar = Icons.Default.Face
                    ),
                    AccountUiModel(email = "thisisaverylongemail@example.com")
                ),
                onAccountClicked = { _, _ -> },
                columnState = positionedState(0, 0)
            )
        }
    }

    @Test
    fun selectAccountScreenNoAvatar() {
        takeScreenshot {
            SelectAccountScreen(
                accounts = listOf(
                    AccountUiModel(email = "maggie@example.com"),
                    AccountUiModel(email = "thisisaverylongemailaccountsample@example.com")
                ),
                onAccountClicked = { _, _ -> },
                columnState = positionedState(0, 0),
                defaultAvatar = null
            )
        }
    }
}
