/*
 * Copyright 2023 The Android Open Source Project
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

@file:OptIn(ExperimentalCoroutinesApi::class)

package com.google.android.horologist.auth.sample.streamline

import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onChild
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollToNode
import androidx.navigation.testing.TestNavHostController
import androidx.wear.compose.navigation.WearNavigator
import com.google.android.horologist.auth.sample.WearApp
import com.google.android.horologist.test.toolbox.rules.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class StreamlineSignInNavigationTest {

    @get:Rule
    var coroutineRule = MainDispatcherRule()

    @get:Rule
    val composeTestRule = createComposeRule()

    private val rootViewTag = "root_view"
    private val streamLineSignInChipText = "Streamline Sign-in"
    private lateinit var navController: TestNavHostController

    @Before
    fun setUp() {
        composeTestRule.setContent {
            navController = TestNavHostController(LocalContext.current)
            navController.navigatorProvider.addNavigator(WearNavigator())
            WearApp(modifier = Modifier.testTag(rootViewTag), navController = navController)
        }

        composeTestRule.onNodeWithTag(rootViewTag)
            .onChild()
            .performScrollToNode(hasText(streamLineSignInChipText))

        composeTestRule.onNodeWithText(streamLineSignInChipText)
            .performClick()
    }

    @Test
    fun streamlineSignIn_singleAccount() = runTest {
        composeTestRule
            .onNodeWithText("Single account available")
            .performClick()

        composeTestRule.waitUntil {
            advanceUntilIdle()
            composeTestRule.onAllNodesWithText("Hi, Maggie").fetchSemanticsNodes().size == 1
        }
        composeTestRule
            .onNodeWithText("Hi, Maggie")
            .assertIsDisplayed()
    }

    @Test
    fun streamlineSignIn_multipleAccounts() = runTest {
        composeTestRule
            .onNodeWithText("Multiple accounts available")
            .performClick()

        composeTestRule.waitUntil {
            advanceUntilIdle()
            composeTestRule.onAllNodesWithText("Select account").fetchSemanticsNodes().size == 1
        }

        composeTestRule
            .onNodeWithText("maggie@example.com")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("john@example.com")
            .assertIsDisplayed()
            .performClick()

        composeTestRule
            .onNodeWithText("Hi, John")
            .assertIsDisplayed()
    }

    @Test
    fun streamlineSignIn_noAccounts() = runTest {
        composeTestRule
            .onNodeWithText("No accounts available")
            .performClick()

        composeTestRule.waitUntil {
            advanceUntilIdle()
            composeTestRule.onAllNodesWithText("Should navigate to add account screen.")
                .fetchSemanticsNodes().size == 1
        }
        composeTestRule
            .onNodeWithText("Should navigate to add account screen.")
            .assertIsDisplayed()
    }
}
