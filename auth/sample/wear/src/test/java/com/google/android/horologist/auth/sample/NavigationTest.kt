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

package com.google.android.horologist.auth.sample

import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onFirst
import androidx.compose.ui.test.performClick
import androidx.navigation.testing.TestNavHostController
import androidx.wear.compose.navigation.WearNavigator
import com.google.common.truth.Truth.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class NavigationTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val rootViewTag = "root_view"
    private lateinit var navController: TestNavHostController

    @Before
    fun setUp() {
        composeTestRule.setContent {
            navController = TestNavHostController(LocalContext.current)
            navController.navigatorProvider.addNavigator(WearNavigator())
            WearApp(modifier = Modifier.testTag(rootViewTag), navController = navController)
        }
    }

    @Test
    fun checkBackNavigation() {
        assertThat(navController.currentBackStackEntry?.destination?.route)
            .isEqualTo("mainScreen")

        composeTestRule.onAllNodesWithText("Sign in")
            .onFirst()
            .performClick()

        assertThat(navController.currentBackStackEntry?.destination?.route)
            .isEqualTo("pkceSignInPromptScreen")

        navigateBack()

        assertThat(navController.currentBackStackEntry?.destination?.route)
            .isEqualTo("mainScreen")

        navigateBack()

        assertThat(navController.currentBackStackEntry)
            .isNull()
    }

    private fun navigateBack() {
        // It should ideally use Espresso.pressBack() but needs further investigation why is not
        // working. Also tried composeTestRule.activity.onBackPressedDispatcher.onBackPressed().
        navController.popBackStack()
    }
}
