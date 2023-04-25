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

@file:OptIn(ExperimentalFoundationApi::class)

package com.google.android.horologist.navsample

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.Chip
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.VignettePosition
import androidx.wear.compose.material.dialog.Alert
import androidx.wear.compose.material.edgeSwipeToDismiss
import androidx.wear.compose.material.rememberSwipeToDismissBoxState
import androidx.wear.compose.navigation.rememberSwipeDismissableNavHostState
import com.google.android.horologist.audio.ui.VolumeScreen
import com.google.android.horologist.compose.navscaffold.NavScaffoldViewModel
import com.google.android.horologist.compose.navscaffold.WearNavScaffold
import com.google.android.horologist.compose.navscaffold.composable
import com.google.android.horologist.compose.navscaffold.scrollStateComposable
import com.google.android.horologist.compose.navscaffold.scrollable
import com.google.android.horologist.compose.pager.PagerScreen
import com.google.android.horologist.compose.snackbar.DialogSnackbarHost
import com.google.android.horologist.navsample.snackbar.SnackbarViewModel
import com.google.android.horologist.networks.NetworkStatusViewModel
import com.google.android.horologist.networks.ui.DataUsageTimeText

@Composable
fun NavWearApp(
    navController: NavHostController
) {
    val snackbarViewModel = viewModel<SnackbarViewModel>(factory = SnackbarViewModel.Factory)
    val networkStatusViewModel =
        viewModel<NetworkStatusViewModel>(factory = NetworkStatusViewModel.Factory)

    val swipeDismissState = rememberSwipeToDismissBoxState()
    val navState = rememberSwipeDismissableNavHostState(swipeDismissState)

    val state by networkStatusViewModel.state.collectAsStateWithLifecycle()

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
        scrollable(
            NavScreen.Menu.route
        ) {
            NavMenuScreen(
                navigateToRoute = { route -> navController.navigate(route) },
                scrollState = it.scrollableState
            )
        }

        scrollable(
            NavScreen.ScalingLazyColumn.route
        ) {
            it.timeTextMode = NavScaffoldViewModel.TimeTextMode.ScrollAway
            it.viewModel.vignettePosition =
                NavScaffoldViewModel.VignetteMode.On(VignettePosition.TopAndBottom)
            it.positionIndicatorMode =
                NavScaffoldViewModel.PositionIndicatorMode.On

            BigScalingLazyColumn(
                scrollState = it.scrollableState
            )
        }

        scrollStateComposable(
            NavScreen.Column.route,
            scrollStateBuilder = { ScrollState(initial = 0) }
        ) {
            BigColumn(
                scrollState = it.scrollableState
            )
        }

        composable(NavScreen.Dialog.route) {
            it.timeTextMode = NavScaffoldViewModel.TimeTextMode.Off

            Alert(title = { Text("Error") }) {
                item {
                    Chip(onClick = {}, label = { Text("Hello") })
                }
            }
        }

        composable(NavScreen.Snackbar.route) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Button(onClick = { snackbarViewModel.showMessage("Test") }) {
                    Text(text = "Test")
                }
            }
        }

        composable(NavScreen.Pager.route) {
            it.timeTextMode = NavScaffoldViewModel.TimeTextMode.Off

            val pagerState = rememberPagerState { 10 }
            PagerScreen(
                // When using Modifier.edgeSwipeToDismiss, it is required that the element on
                // which the modifier applies exists within a SwipeToDismissBox which shares
                // the same state. Here, swipeDismissState is shared with
                // our SwipeDismissableNavHost, which in turns passes it to its SwipeToDismissBox.
                modifier = Modifier
                    .fillMaxSize()
                    .edgeSwipeToDismiss(swipeDismissState),
                state = pagerState
            ) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(text = "Screen $it")
                }
            }
        }

        composable(NavScreen.Volume.route) {
            VolumeScreen()
        }
    }
}
