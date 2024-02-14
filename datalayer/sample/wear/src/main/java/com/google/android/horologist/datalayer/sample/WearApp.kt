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

@file:OptIn(ExperimentalFoundationApi::class)

package com.google.android.horologist.datalayer.sample

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.wear.compose.navigation.SwipeDismissableNavHost
import androidx.wear.compose.navigation.composable
import androidx.wear.compose.navigation.rememberSwipeDismissableNavController
import androidx.wear.compose.ui.tooling.preview.WearPreviewSmallRound
import com.google.android.horologist.compose.layout.AppScaffold
import com.google.android.horologist.compose.layout.ScreenScaffold
import com.google.android.horologist.compose.layout.rememberColumnState
import com.google.android.horologist.datalayer.sample.screens.MainScreen
import com.google.android.horologist.datalayer.sample.screens.datalayer.DataLayerScreen
import com.google.android.horologist.datalayer.sample.screens.info.infoScreen
import com.google.android.horologist.datalayer.sample.screens.info.navigateToInfoScreen
import com.google.android.horologist.datalayer.sample.screens.nodes.DataLayerNodesScreen
import com.google.android.horologist.datalayer.sample.screens.nodesactions.NodesActionsScreen
import com.google.android.horologist.datalayer.sample.screens.nodesactions.navigateToNodeDetailsScreen
import com.google.android.horologist.datalayer.sample.screens.nodesactions.nodeDetailsScreen
import com.google.android.horologist.datalayer.sample.screens.nodeslistener.NodesListenerScreen
import com.google.android.horologist.datalayer.sample.screens.tracking.TrackingScreen

@Composable
fun WearApp(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberSwipeDismissableNavController(),
) {
    AppScaffold {
        SwipeDismissableNavHost(
            startDestination = Screen.MainScreen.route,
            navController = navController,
            modifier = modifier,
        ) {
            composable(
                route = Screen.MainScreen.route,
            ) {
                val columnState = rememberColumnState()

                ScreenScaffold(scrollState = columnState) {
                    MainScreen(
                        navigateToRoute = navController::navigate,
                        columnState = columnState,
                    )
                }
            }
            composable(route = Screen.CounterScreen.route) {
                val columnState = rememberColumnState()

                ScreenScaffold(scrollState = columnState) {
                    DataLayerScreen(columnState = columnState)
                }
            }
            composable(route = Screen.ListNodesScreen.route) {
                val columnState = rememberColumnState()

                ScreenScaffold(scrollState = columnState) {
                    DataLayerNodesScreen(columnState = columnState)
                }
            }
            composable(route = Screen.AppHelperTrackingScreen.route) {
                val columnState = rememberColumnState()

                ScreenScaffold(scrollState = columnState) {
                    TrackingScreen(
                        onDisplayInfoClicked = navController::navigateToInfoScreen,
                        columnState = columnState,
                    )
                }
            }
            composable(route = Screen.AppHelperNodesActionsScreen.route) {
                val columnState = rememberColumnState()

                ScreenScaffold(scrollState = columnState) {
                    NodesActionsScreen(
                        onNodeClick = navController::navigateToNodeDetailsScreen,
                        columnState = columnState,
                    )
                }
            }
            composable(route = Screen.AppHelperNodesListenerScreen.route) {
                val columnState = rememberColumnState()

                ScreenScaffold(scrollState = columnState) {
                    NodesListenerScreen(columnState = columnState)
                }
            }
            nodeDetailsScreen()
            infoScreen(
                onDismissClick = navController::popBackStack,
            )
        }
    }
}

@WearPreviewSmallRound
@Composable
fun DefaultPreview() {
    WearApp()
}
