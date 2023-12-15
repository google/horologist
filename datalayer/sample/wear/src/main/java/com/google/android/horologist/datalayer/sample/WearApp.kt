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

package com.google.android.horologist.datalayer.sample

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.wear.compose.navigation.rememberSwipeDismissableNavController
import androidx.wear.compose.ui.tooling.preview.WearPreviewSmallRound
import com.google.android.horologist.compose.navscaffold.WearNavScaffold
import com.google.android.horologist.compose.navscaffold.scrollable
import com.google.android.horologist.datalayer.sample.screens.MainScreen
import com.google.android.horologist.datalayer.sample.screens.datalayer.DataLayerScreen
import com.google.android.horologist.datalayer.sample.screens.nodes.DataLayerNodesScreen
import com.google.android.horologist.datalayer.sample.screens.nodes.DataLayerNodesViewModel
import com.google.android.horologist.datalayer.sample.screens.nodesactions.NodesActionsScreen
import com.google.android.horologist.datalayer.sample.screens.tracking.TrackingScreen
import com.google.android.horologist.datalayer.sample.screens.tracking.TrackingScreenViewModel

@Composable
fun WearApp(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberSwipeDismissableNavController(),
) {
    WearNavScaffold(
        startDestination = Screen.MainScreen.route,
        navController = navController,
        modifier = modifier,
    ) {
        scrollable(
            route = Screen.MainScreen.route,
        ) {
            MainScreen(
                navigateToRoute = navController::navigate,
                columnState = it.columnState,
            )
        }
        scrollable(route = Screen.CounterScreen.route) {
            DataLayerScreen(columnState = it.columnState, modifier = modifier)
        }
        scrollable(route = Screen.ListNodesScreen.route) {
            DataLayerNodesScreen(
                viewModel = viewModel(factory = DataLayerNodesViewModel.Factory),
                columnState = it.columnState,
            )
        }
        scrollable(route = Screen.AppHelperTrackingScreen.route) {
            TrackingScreen(
                viewModel = viewModel(factory = TrackingScreenViewModel.Factory),
                columnState = it.columnState,
            )
        }
        scrollable(route = Screen.AppHelperNodesActionsScreen.route) {
            NodesActionsScreen(columnState = it.columnState)
        }
    }
}

@WearPreviewSmallRound
@Composable
fun DefaultPreview() {
    WearApp()
}
