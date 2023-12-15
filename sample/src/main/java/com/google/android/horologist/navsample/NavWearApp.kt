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
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.wear.compose.foundation.edgeSwipeToDismiss
import androidx.wear.compose.foundation.rememberSwipeToDismissBoxState
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.Chip
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.dialog.Alert
import androidx.wear.compose.navigation.SwipeDismissableNavHost
import androidx.wear.compose.navigation.composable
import androidx.wear.compose.navigation.rememberSwipeDismissableNavHostState
import com.google.android.horologist.audio.ui.VolumeScreen
import com.google.android.horologist.compose.layout.AppScaffold
import com.google.android.horologist.compose.layout.ScalingLazyColumnDefaults
import com.google.android.horologist.compose.layout.ScreenScaffold
import com.google.android.horologist.compose.layout.rememberColumnState
import com.google.android.horologist.compose.pager.PagerScreen
import com.google.android.horologist.compose.snackbar.DialogSnackbarHost
import com.google.android.horologist.navsample.snackbar.SnackbarViewModel
import com.google.android.horologist.networks.NetworkStatusViewModel
import com.google.android.horologist.networks.ui.DataUsageTimeText

@Composable
fun NavWearApp(
    navController: NavHostController,
) {
    val snackbarViewModel = viewModel<SnackbarViewModel>(factory = SnackbarViewModel.Factory)
    val networkStatusViewModel =
        viewModel<NetworkStatusViewModel>(factory = NetworkStatusViewModel.Factory)

    val swipeDismissState = rememberSwipeToDismissBoxState()
    val navState = rememberSwipeDismissableNavHostState(swipeDismissState)

    val state by networkStatusViewModel.state.collectAsStateWithLifecycle()

    AppScaffold(
        timeText = {
            DataUsageTimeText(
                showData = true,
                networkStatus = state.networks,
                networkUsage = state.dataUsage,
            )
        },
        snackbar = {
            DialogSnackbarHost(
                hostState = snackbarViewModel.snackbarHostState,
                modifier = Modifier.fillMaxSize(),
            )
        },
    ) {
        SwipeDismissableNavHost(
            startDestination = NavScreen.Menu.route,
            navController = navController,
            state = navState,
        ) {
            composable(
                NavScreen.Menu.route,
            ) {
                val columnState = rememberColumnState()

                ScreenScaffold(scrollState = columnState) {
                    NavMenuScreen(
                        navigateToRoute = { route -> navController.navigate(route) },
                        columnState = columnState,
                    )
                }
            }

            composable(
                NavScreen.ScalingLazyColumn.route,
            ) {
                val columnState = rememberColumnState()

                // TODO move all inside Screen
                ScreenScaffold(scrollState = columnState) {
                    BigScalingLazyColumn(
                        columnState = columnState,
                    )
                }
            }

            composable(
                NavScreen.Column.route,
            ) {
                val scrollState = rememberScrollState()

                // TODO move all inside PagerScreen
                ScreenScaffold(scrollState = scrollState) {
                    BigColumn(
                        scrollState = scrollState,
                    )
                }
            }

            composable(NavScreen.Dialog.route) {
                ScreenScaffold {
                    Alert(title = { Text("Error") }) {
                        item {
                            Chip(onClick = {}, label = { Text("Hello") })
                        }
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
                // TODO move all inside PagerScreen
                ScreenScaffold {
                    val pagerState = rememberPagerState { 10 }
                    PagerScreen(
                        // When using Modifier.edgeSwipeToDismiss, it is required that the element on
                        // which the modifier applies exists within a SwipeToDismissBox which shares
                        // the same state. Here, swipeDismissState is shared with
                        // our SwipeDismissableNavHost, which in turns passes it to its SwipeToDismissBox.
                        modifier = Modifier
                            .fillMaxSize()
                            .edgeSwipeToDismiss(swipeDismissState),
                        state = pagerState,
                    ) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "Screen $it")
                        }
                    }
                }
            }

            composable(NavScreen.Volume.route) {
                VolumeScreen()
            }
        }
    }
}
