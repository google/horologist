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

package com.google.android.horologist.navscaffold

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.navigation.NavHostController
import androidx.wear.compose.material.ScalingLazyColumn
import androidx.wear.compose.material.ScalingLazyListState
import androidx.wear.compose.material.Text
import androidx.wear.compose.navigation.rememberSwipeDismissableNavController
import com.google.android.horologist.compose.navscaffold.WearNavScaffold
import com.google.android.horologist.compose.navscaffold.scalingLazyColumnComposable
import com.google.android.horologist.compose.navscaffold.scrollStateComposable
import com.google.android.horologist.compose.navscaffold.scrollableColumn
import org.junit.Rule
import org.junit.Test

class NavScaffoldTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    lateinit var navController: NavHostController

    @Test
    fun testNavScaffold() {
        composeTestRule.setContent {
            navController = rememberSwipeDismissableNavController()

            WearNavScaffold(
                startDestination = "a",
                navController = navController
            ) {
                scalingLazyColumnComposable(
                    route = "a",
                    scrollStateBuilder = { ScalingLazyListState(initialCenterItemIndex = 0) }
                ) {
                    ScalingLazyColumn(
                        modifier = Modifier
                            .scrollableColumn(
                                it.viewModel.focusRequester,
                                it.scrollableState
                            )
                            .fillMaxSize()
                            .testTag("columna"),
                        state = it.scrollableState,
                        horizontalAlignment = Alignment.CenterHorizontally,
                        autoCentering = true,
                    ) {
                        items(100) {
                            Text("Item $it")
                        }
                    }
                }

                scrollStateComposable(
                    route = "b",
                    scrollStateBuilder = { ScrollState(0) }
                ) {
                    Column(
                        modifier = Modifier
                            .testTag("columnb")
                            .fillMaxSize()
                            .verticalScroll(it.scrollableState)
                    ) {
                        (1..100).forEach { i ->
                            Text("$i")
                        }
                    }
                }
            }
        }

        val columnA = composeTestRule.onNodeWithTag("columna")
        val columnB = composeTestRule.onNodeWithTag("columnb")

        columnA.assertIsDisplayed()
        columnB.assertDoesNotExist()

        composeTestRule.runOnUiThread {
            navController.navigate("b")
        }
        composeTestRule.waitForIdle()

        columnA.assertDoesNotExist()
        columnB.assertIsDisplayed()
    }
}
