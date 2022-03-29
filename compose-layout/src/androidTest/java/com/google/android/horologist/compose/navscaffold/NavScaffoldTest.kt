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

package com.google.android.horologist.compose.navscaffold

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsFocused
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.get
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.wear.compose.foundation.curvedComposable
import androidx.wear.compose.material.ScalingLazyColumn
import androidx.wear.compose.material.ScalingLazyListState
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.TimeText
import androidx.wear.compose.material.rememberScalingLazyListState
import androidx.wear.compose.navigation.rememberSwipeDismissableNavController
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.withContext
import org.junit.Assert.assertSame
import org.junit.Rule
import org.junit.Test

class NavScaffoldTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    lateinit var navController: NavHostController

    @Test
    fun testNavScaffoldScroll() = runTest {
        // Test that when we move between two scrollable destinations
        // we get the right scroll state used at the right times

        lateinit var aScrollState: ScalingLazyListState
        lateinit var bScrollState: ScalingLazyListState
        lateinit var navController: NavHostController

        fun NavGraphBuilder.scrollingList(
            route: String,
            scrollState: ScalingLazyListState
        ) {
            scalingLazyColumnComposable(
                route = route,
                scrollStateBuilder = { scrollState }
            ) {
                ScalingLazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .scrollableColumn(
                            it.viewModel.focusRequester,
                            it.scrollableState
                        ),
                    state = it.scrollableState,
                    horizontalAlignment = Alignment.CenterHorizontally,
                    autoCentering = true,
                ) {
                    items(11) {
                        Text(text = "Item $it")
                    }
                }
            }
        }

        composeTestRule.setContent {
            navController = rememberSwipeDismissableNavController()
            aScrollState = rememberScalingLazyListState(initialCenterItemIndex = 0)
            bScrollState = rememberScalingLazyListState(initialCenterItemIndex = 10)

            WearNavScaffold(
                startDestination = "a",
                navController = navController
            ) {
                scrollingList("a", aScrollState)
                scrollingList("b", bScrollState)
            }
        }

        composeTestRule.awaitIdle()

        val aViewModel =
            ViewModelProvider(navController.currentBackStackEntry!!).get<NavScaffoldViewModel>()
        assertSame(aScrollState, aViewModel.scrollableState)

        withContext(Dispatchers.Main) {
            navController.navigate("b")
        }

        composeTestRule.awaitIdle()

        val bViewModel =
            ViewModelProvider(navController.currentBackStackEntry!!).get<NavScaffoldViewModel>()
        assertSame(bScrollState, bViewModel.scrollableState)
    }

    @Test
    fun testNavScaffoldNavigation() {
        // Test navigating changes the composable that is shown correctly

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
                            .scrollableColumn(it.viewModel.focusRequester, it.scrollableState)
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

        columnA.assertIsDisplayed().assertIsFocused()
        columnB.assertDoesNotExist()

        composeTestRule.runOnUiThread {
            navController.navigate("b")
        }
        composeTestRule.waitForIdle()

        columnA.assertDoesNotExist()
        columnB.assertIsDisplayed().assertIsFocused()

        composeTestRule.runOnUiThread {
            navController.popBackStack()
        }
        composeTestRule.waitForIdle()

        columnA.assertIsDisplayed().assertIsFocused()
        columnB.assertDoesNotExist()
    }

    @Test
    fun testTimeTextCanBeToggled() {
        // Test that timetext shows up, and via custom leading text is shown near the top of the
        // screen, the disappears after mode is toggled off

        var bounds: Rect? = null

        composeTestRule.setContent {
            navController = rememberSwipeDismissableNavController()

            WearNavScaffold(
                startDestination = "a",
                navController = navController,
                timeText = {
                    TimeText(
                        modifier = it,
                        leadingCurvedContent = {
                            curvedComposable {
                                Text(
                                    modifier = Modifier
                                        .testTag("timeText")
                                        .onGloballyPositioned {
                                            bounds = it.boundsInWindow()
                                        },
                                    text = "\uD83D\uDD23"
                                )
                            }
                        },
                        leadingLinearContent = {
                            Text(
                                modifier = Modifier
                                    .testTag("timeText")
                                    .onGloballyPositioned {
                                        bounds = it.boundsInWindow()
                                    },
                                text = "\uD83D\uDD23"
                            )
                        }
                    )
                }
            ) {
                wearNavComposable(
                    route = "a",
                ) { _, _ ->
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            modifier = Modifier.testTag("body"),
                            text = "Lorem Ipsum"
                        )
                    }
                }
            }
        }

        composeTestRule.waitForIdle()

        val timeText = composeTestRule.onNodeWithTag("timeText")
        val body = composeTestRule.onNodeWithTag("body")

        // expect time text in the top 50 pixels
        assertThat(bounds?.bottom).isLessThan(75)

        timeText.assertIsDisplayed()
        body.assertIsDisplayed()

        val viewModel =
            ViewModelProvider(navController.currentBackStackEntry!!).get<NavScaffoldViewModel>()

        viewModel.timeTextMode = NavScaffoldViewModel.TimeTextMode.Off
        composeTestRule.waitForIdle()

        timeText.assertDoesNotExist()
        body.assertIsDisplayed()
    }
}
