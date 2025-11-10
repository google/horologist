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

package com.google.android.horologist.auth.ui.material3.common.screens.prompt

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.wear.compose.foundation.lazy.ScalingLazyListScope
import androidx.wear.compose.material.Text
import com.google.android.horologist.auth.composables.common.AccountUiModel
import com.google.android.horologist.auth.composables.material3.buttons.GuestModeButton
import com.google.android.horologist.auth.composables.material3.buttons.SignInButton
import com.google.android.horologist.screenshots.rng.WearLegacyScreenTest
import org.junit.Test

class SignInPromptScreenTest : WearLegacyScreenTest() {

    @Test
    fun idle() {
        runTest {
            SignInPromptScreen(
                state = SignInPromptScreenState.Idle,
                title = "Sign in",
                message = "Send messages and create chat groups with your friends",
                onIdleStateObserved = { },
                onAlreadySignedIn = { },
            ) {
                testContent()
            }
        }
    }

    @Test
    fun loading() {
        runTest {
            SignInPromptScreen(
                state = SignInPromptScreenState.Loading,
                title = "Sign in",
                message = "Send messages and create chat groups with your friends",
                onIdleStateObserved = { },
                onAlreadySignedIn = { },
            ) {
                testContent()
            }
        }
    }

    @Test
    fun customLoading() {
        runTest {
            SignInPromptScreen(
                state = SignInPromptScreenState.Loading,
                title = "Sign in",
                message = "Send messages and create chat groups with your friends",
                onIdleStateObserved = { },
                onAlreadySignedIn = { },
                loadingContent = {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("Loading...")
                    }
                },
            ) {
                testContent()
            }
        }
    }

    @Test
    fun signedIn() {
        runTest {
            SignInPromptScreen(
                state = SignInPromptScreenState.SignedIn(
                    AccountUiModel(
                        "user@example.com",
                        "John Doe",
                    ),
                ),
                title = "Sign in",
                message = "Send messages and create chat groups with your friends",
                onIdleStateObserved = { },
                onAlreadySignedIn = { },
            ) {
                testContent()
            }
        }
    }

    @Test
    fun signedOut() {
        runTest {
            SignInPromptScreen(
                state = SignInPromptScreenState.SignedOut,
                title = "Sign in",
                message = "Send messages and create chat groups with your friends",
                onIdleStateObserved = { },
                onAlreadySignedIn = { },
            ) {
                testContent()
            }
        }
    }

    @Test
    fun signedOutTruncation() {
        runTest {
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
            ) {
                testContent()
            }
        }
    }

    private fun ScalingLazyListScope.testContent() {
        item {
            SignInButton(
                onClick = { },
                modifier = Modifier.fillMaxWidth(),
            )
        }
        item {
            GuestModeButton(
                onClick = { },
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}
