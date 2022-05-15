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

package com.google.android.horologist.navsample

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.Chip
import androidx.wear.compose.material.ScalingLazyListState
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.VignettePosition
import androidx.wear.compose.material.dialog.Alert
import androidx.wear.compose.material.edgeSwipeToDismiss
import androidx.wear.compose.material.rememberSwipeToDismissBoxState
import androidx.wear.compose.navigation.rememberSwipeDismissableNavHostState
import com.google.accompanist.pager.rememberPagerState
import com.google.android.horologist.audio.ui.VolumeScreen
import com.google.android.horologist.compose.navscaffold.NavScaffoldViewModel
import com.google.android.horologist.compose.navscaffold.WearNavScaffold
import com.google.android.horologist.compose.navscaffold.scalingLazyColumnComposable
import com.google.android.horologist.compose.navscaffold.scrollStateComposable
import com.google.android.horologist.compose.navscaffold.wearNavComposable
import com.google.android.horologist.compose.pager.PagerScreen
import com.google.android.horologist.compose.snackbar.DialogSnackbarHost
import com.google.android.horologist.navsample.snackbar.SnackbarViewModel
import com.google.android.horologist.networks.NetworksViewModel
import com.google.android.horologist.networks.ui.DataUsageTimeText
import com.google.android.horologist.utils.rememberStateWithLifecycle

@Composable
fun NavWearApp(
    navController: NavHostController,
) {
    val snackbarViewModel = viewModel<SnackbarViewModel>(factory = SnackbarViewModel.Factory)
    val networksViewModel = viewModel<NetworksViewModel>(factory = NetworksViewModel.Factory)

    val swipeDismissState = rememberSwipeToDismissBoxState()
    val navState = rememberSwipeDismissableNavHostState(swipeDismissState)

    val state by rememberStateWithLifecycle(networksViewModel.state)

    WearNavScaffold(
        startDestination = NavScreen.Menu.route,
        navController = navController,
        snackbar = {
            DialogSnackbarHost(
                hostState = snackbarViewModel.snackbarHostState,
                modifier = Modifier.fillMaxSize()
            )
        },
        timeText = {
            DataUsageTimeText(
                modifier = it,
                showData = true,
                networkStatus = state.networks,
                networkUsage = state.dataUsage
            )
        },
        state = navState
    ) {
        scalingLazyColumnComposable(
            NavScreen.Menu.route,
            scrollStateBuilder = { ScalingLazyListState(initialCenterItemIndex = 0) }
        ) {
            NavMenuScreen(
                navigateToRoute = { route -> navController.navigate(route) },
                scrollState = it.scrollableState,
                focusRequester = it.viewModel.focusRequester
            )
        }

        scalingLazyColumnComposable(
            NavScreen.ScalingLazyColumn.route,
            scrollStateBuilder = { ScalingLazyListState() }
        ) {
            it.viewModel.timeTextMode = NavScaffoldViewModel.TimeTextMode.FadeAway
            it.viewModel.vignettePosition =
                NavScaffoldViewModel.VignetteMode.On(VignettePosition.TopAndBottom)
            it.viewModel.positionIndicatorMode =
                NavScaffoldViewModel.PositionIndicatorMode.On

            BigScalingLazyColumn(
                scrollState = it.scrollableState,
                focusRequester = it.viewModel.focusRequester
            )
        }

        scrollStateComposable(
            NavScreen.Column.route,
            scrollStateBuilder = { ScrollState(0) }
        ) {
            BigColumn(
                scrollState = it.scrollableState,
                focusRequester = it.viewModel.focusRequester
            )
        }

        wearNavComposable(NavScreen.Dialog.route) { _, viewModel ->
            viewModel.timeTextMode = NavScaffoldViewModel.TimeTextMode.Off

            Alert(title = { Text("Error") }) {
                item {
                    Chip(onClick = {}, label = { Text("Hello") })
                }
            }
        }

        wearNavComposable(NavScreen.Snackbar.route) { _, _ ->
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Button(onClick = { snackbarViewModel.showMessage("Test") }) {
                    Text(text = "Test")
                }
            }
        }

        wearNavComposable(NavScreen.Pager.route) { _, viewModel ->
            viewModel.timeTextMode = NavScaffoldViewModel.TimeTextMode.Off

            val pagerState = rememberPagerState()
            PagerScreen(
                modifier = Modifier
                    .fillMaxSize()
                    .edgeSwipeToDismiss(swipeDismissState),
                count = 10,
                state = pagerState
            ) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(text = "Screen $it")
                }
            }
        }

        wearNavComposable(NavScreen.Volume.route) { _, viewModel ->
            VolumeScreen(focusRequester = viewModel.focusRequester)
        }
    }
}
