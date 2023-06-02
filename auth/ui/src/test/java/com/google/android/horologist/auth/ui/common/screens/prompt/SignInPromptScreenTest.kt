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

package com.google.android.horologist.auth.ui.common.screens.prompt

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.wear.compose.foundation.lazy.ScalingLazyListScope
import androidx.wear.compose.material.Text
import com.google.android.horologist.auth.composables.chips.GuestModeChip
import com.google.android.horologist.auth.composables.chips.SignInChip
import com.google.android.horologist.auth.composables.model.AccountUiModel
import com.google.android.horologist.compose.material.ChipType
import com.google.android.horologist.screenshots.ScreenshotBaseTest
import com.google.android.horologist.screenshots.ScreenshotTestRule.Companion.screenshotTestRuleParams
import com.google.android.horologist.test.toolbox.composables.positionedState
import org.junit.Test

class SignInPromptScreenTest : ScreenshotBaseTest(
    screenshotTestRuleParams {
        screenTimeText = {}
    }
) {

    @Test
    fun idle() {
        screenshotTestRule.setContent(takeScreenshot = true) {
            SignInPromptScreen(
                state = SignInPromptScreenState.Idle,
                title = "Sign in",
                message = "Send messages and create chat groups with your friends",
                onIdleStateObserved = { },
                onAlreadySignedIn = { },
                columnState = positionedState()
            ) {
                testContent()
            }
        }
    }

    @Test
    fun loading() {
        screenshotTestRule.setContent(takeScreenshot = true) {
            SignInPromptScreen(
                state = SignInPromptScreenState.Loading,
                title = "Sign in",
                message = "Send messages and create chat groups with your friends",
                onIdleStateObserved = { },
                onAlreadySignedIn = { },
                columnState = positionedState()
            ) {
                testContent()
            }
        }
    }

    @Test
    fun customLoading() {
        screenshotTestRule.setContent(takeScreenshot = true) {
            SignInPromptScreen(
                state = SignInPromptScreenState.Loading,
                title = "Sign in",
                message = "Send messages and create chat groups with your friends",
                onIdleStateObserved = { },
                onAlreadySignedIn = { },
                columnState = positionedState(0, 0),
                loadingContent = {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("Loading...")
                    }
                }
            ) {
                testContent()
            }
        }
    }

    @Test
    fun signedIn() {
        screenshotTestRule.setContent(takeScreenshot = true) {
            SignInPromptScreen(
                state = SignInPromptScreenState.SignedIn(AccountUiModel("user@example.com")),
                title = "Sign in",
                message = "Send messages and create chat groups with your friends",
                onIdleStateObserved = { },
                onAlreadySignedIn = { },
                columnState = positionedState()
            ) {
                testContent()
            }
        }
    }

    @Test
    fun signedOut() {
        screenshotTestRule.setContent(takeScreenshot = true) {
            SignInPromptScreen(
                state = SignInPromptScreenState.SignedOut,
                title = "Sign in",
                message = "Send messages and create chat groups with your friends",
                onIdleStateObserved = { },
                onAlreadySignedIn = { },
                columnState = positionedState()
            ) {
                testContent()
            }
        }
    }

    @Test
    fun signedOutTruncation() {
        screenshotTestRule.setContent(takeScreenshot = true) {
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
                columnState = positionedState(0, -64)
            ) {
                testContent()
            }
        }
    }

    private fun ScalingLazyListScope.testContent() {
        item {
            SignInChip(
                onClick = { },
                chipType = ChipType.Secondary
            )
        }
        item {
            GuestModeChip(
                onClick = { },
                chipType = ChipType.Secondary
            )
        }
    }
}
