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

@file:OptIn(
    ExperimentalCoroutinesApi::class,
    ExperimentalWearFoundationApi::class,
)
@file:Suppress("DEPRECATION")

package com.google.android.horologist.compose.navscaffold

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsFocused
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.get
import androidx.navigation.NavHostController
import androidx.test.filters.MediumTest
import androidx.wear.compose.foundation.ExperimentalWearFoundationApi
import androidx.wear.compose.foundation.RequestFocusWhenActive
import androidx.wear.compose.foundation.curvedComposable
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.TimeText
import androidx.wear.compose.navigation.rememberSwipeDismissableNavController
import com.google.android.horologist.compose.layout.ScalingLazyColumn
import com.google.android.horologist.compose.rotaryinput.rotaryWithScroll
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@MediumTest
@RunWith(RobolectricTestRunner::class)
@Config(
    sdk = [30],
    qualifiers = "w227dp-h227dp-small-notlong-round-watch-xhdpi-keyshidden-nonav",
)
class NavScaffoldTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    lateinit var navController: NavHostController

    @Test
    fun testNavScaffoldNavigation() {
        // Test navigating changes the composable that is shown correctly

        composeTestRule.setContent {
            navController = rememberSwipeDismissableNavController()

            WearNavScaffold(
                startDestination = "a",
                navController = navController,
            ) {
                scrollable(
                    route = "a",
                ) {
                    ScalingLazyColumn(
                        columnState = it.columnState,
                        modifier = Modifier.testTag("columna"),
                    ) {
                        items(100) {
                            Text("Item $it")
                        }
                    }
                }

                scrollStateComposable(
                    route = "b",
                    scrollStateBuilder = { ScrollState(0) },
                ) {
                    val focusRequester =
                        remember { FocusRequester() }
                    Column(
                        modifier = Modifier
                            .testTag("columnb")
                            .fillMaxSize()
                            .rotaryWithScroll(it.scrollableState, focusRequester)
                            .verticalScroll(it.scrollableState),
                    ) {
                        (1..100).forEach { i ->
                            Text("$i")
                        }
                    }
                    RequestFocusWhenActive(focusRequester)
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
                        startCurvedContent = {
                            curvedComposable {
                                Text(
                                    modifier = Modifier
                                        .testTag("timeText")
                                        .onGloballyPositioned {
                                            bounds = it.boundsInWindow()
                                        },
                                    text = "\uD83D\uDD23",
                                )
                            }
                        },
                        endLinearContent = {
                            Text(
                                modifier = Modifier
                                    .testTag("timeText")
                                    .onGloballyPositioned {
                                        bounds = it.boundsInWindow()
                                    },
                                text = "\uD83D\uDD23",
                            )
                        },
                    )
                },
            ) {
                composable(
                    route = "a",
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            modifier = Modifier.testTag("body"),
                            text = "Lorem Ipsum",
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

        timeText.assertIsNotDisplayed()
        body.assertIsDisplayed()
    }
}
