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
    ExperimentalHorologistPaparazziApi::class,
    ExperimentalHorologistAuthUiApi::class,
    ExperimentalHorologistComposeToolsApi::class
)

package com.google.android.horologist.auth.ui.common.screens.prompt

import androidx.wear.compose.foundation.lazy.ScalingLazyListScope
import com.google.android.horologist.auth.composables.chips.GuestModeChip
import com.google.android.horologist.auth.composables.chips.SignInChip
import com.google.android.horologist.auth.composables.model.AccountUiModel
import com.google.android.horologist.auth.ui.ExperimentalHorologistAuthUiApi
import com.google.android.horologist.base.ui.components.StandardChipType
import com.google.android.horologist.compose.tools.ExperimentalHorologistComposeToolsApi
import com.google.android.horologist.compose.tools.snapshotInABox
import com.google.android.horologist.paparazzi.ExperimentalHorologistPaparazziApi
import com.google.android.horologist.paparazzi.WearPaparazzi
import com.google.android.horologist.test.toolbox.composables.positionedState
import org.junit.Rule
import org.junit.Test

class SignInPromptScreenTest {

    @get:Rule
    val paparazzi = WearPaparazzi()

    @Test
    fun idle() {
        paparazzi.snapshotInABox {
            SignInPromptScreen(
                state = SignInPromptScreenState.Idle,
                title = "Sign in",
                message = "Send messages and create chat groups with your friends",
                onIdleStateObserved = { },
                onAlreadySignedIn = { },
                columnState = positionedState(0, 0)
            ) {
                testContent()
            }
        }
    }

    @Test
    fun loading() {
        paparazzi.snapshotInABox {
            SignInPromptScreen(
                state = SignInPromptScreenState.Loading,
                title = "Sign in",
                message = "Send messages and create chat groups with your friends",
                onIdleStateObserved = { },
                onAlreadySignedIn = { },
                columnState = positionedState(0, 0)
            ) {
                testContent()
            }
        }
    }

    @Test
    fun signedIn() {
        paparazzi.snapshotInABox {
            SignInPromptScreen(
                state = SignInPromptScreenState.SignedIn(AccountUiModel("user@example.com")),
                title = "Sign in",
                message = "Send messages and create chat groups with your friends",
                onIdleStateObserved = { },
                onAlreadySignedIn = { },
                columnState = positionedState(0, 0)
            ) {
                testContent()
            }
        }
    }

    @Test
    fun signedOut() {
        paparazzi.snapshotInABox {
            SignInPromptScreen(
                state = SignInPromptScreenState.SignedOut,
                title = "Sign in",
                message = "Send messages and create chat groups with your friends",
                onIdleStateObserved = { },
                onAlreadySignedIn = { },
                columnState = positionedState(0, 0)
            ) {
                testContent()
            }
        }
    }

    @Test
    fun signedOutTruncation() {
        paparazzi.snapshotInABox {
            SignInPromptScreen(
                state = SignInPromptScreenState.SignedOut,
                title = "WWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWW",
                message = "WWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWW" +
                    "WWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWW" +
                    "WWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWW" +
                    "WWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWW" +
                    "WWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWW" +
                    "WWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWW",
                onIdleStateObserved = { },
                onAlreadySignedIn = { },
                columnState = positionedState(0, 0)
            ) {
                testContent()
            }
        }
    }

    private fun ScalingLazyListScope.testContent() {
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
