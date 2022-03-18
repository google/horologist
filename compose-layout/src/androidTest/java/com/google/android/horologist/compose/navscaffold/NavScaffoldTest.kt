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

import androidx.compose.foundation.gestures.ScrollableState
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.wear.compose.material.ScalingLazyColumn
import androidx.wear.compose.material.ScalingLazyListState
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.rememberScalingLazyListState
import androidx.wear.compose.navigation.rememberSwipeDismissableNavController
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.withContext
import org.junit.Assert.assertSame
import org.junit.Rule
import org.junit.Test

class NavScaffoldTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun testNavScaffold() = runTest {
        lateinit var aScrollState: ScalingLazyListState
        lateinit var bScrollState: ScalingLazyListState
        lateinit var navController: NavHostController

        var activeScrollState: ScrollableState? = null

        composeTestRule.setContent {
            navController = rememberSwipeDismissableNavController()
            aScrollState = rememberScalingLazyListState(initialCenterItemIndex = 0)
            bScrollState = rememberScalingLazyListState(initialCenterItemIndex = 10)

            WearNavScaffold(
                startDestination = "a",
                navController = navController,
                positionIndicator = {
                    SideEffect {
                        activeScrollState = it.scrollableState
                    }
                }
            ) {
                scrollingList("a", aScrollState)
                scrollingList("b", bScrollState)
            }
        }

        composeTestRule.awaitIdle()

        assertSame(aScrollState, activeScrollState)

        withContext(Dispatchers.Main) {
            navController.navigate("b")
        }

        composeTestRule.awaitIdle()

        assertSame(bScrollState, activeScrollState)
    }

    private fun NavGraphBuilder.scrollingList(
        route: String,
        bScrollState1: ScalingLazyListState
    ) {
        scalingLazyColumnComposable(
            route = route,
            scrollStateBuilder = { bScrollState1 }) {
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
}
