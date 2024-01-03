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

package com.google.android.horologist.auth.sample

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.wear.compose.navigation.SwipeDismissableNavHost
import androidx.wear.compose.navigation.composable
import androidx.wear.compose.navigation.rememberSwipeDismissableNavController
import androidx.wear.compose.ui.tooling.preview.WearPreviewSmallRound
import com.google.android.horologist.auth.sample.screens.MainScreen
import com.google.android.horologist.auth.sample.screens.common.streamline.StreamlineSignInMenuScreen
import com.google.android.horologist.auth.sample.screens.common.streamline.StreamlineSignInSampleScreen
import com.google.android.horologist.auth.sample.screens.googlesignin.prompt.GoogleSignInPromptSampleScreen
import com.google.android.horologist.auth.sample.screens.googlesignin.signin.GoogleSignInSampleViewModelFactory
import com.google.android.horologist.auth.sample.screens.googlesignin.signout.GoogleSignOutScreen
import com.google.android.horologist.auth.sample.screens.tokenshare.customkey.TokenShareCustomKeyScreen
import com.google.android.horologist.auth.sample.screens.tokenshare.defaultkey.TokenShareDefaultKeyScreen
import com.google.android.horologist.auth.ui.googlesignin.signin.GoogleSignInScreen
import com.google.android.horologist.compose.layout.AppScaffold
import com.google.android.horologist.compose.layout.ScreenScaffold
import com.google.android.horologist.compose.layout.rememberColumnState

@Composable
fun WearApp(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberSwipeDismissableNavController(),
) {
    AppScaffold {
        SwipeDismissableNavHost(
            startDestination = Screen.MainScreen.route,
            navController = navController,
        ) {
            composable(
                route = Screen.MainScreen.route,
            ) {
                val columnState = rememberColumnState()

                ScreenScaffold(scrollState = columnState) {
                    MainScreen(
                        navigateToRoute = navController::navigate,
                        modifier = modifier,
                        columnState = columnState,
                    )
                }
            }
            composable(
                route = Screen.GoogleSignInPromptSampleScreen.route,
            ) {
                val columnState = rememberColumnState()

                ScreenScaffold(scrollState = columnState) {
                    GoogleSignInPromptSampleScreen(
                        navController = navController,
                        columnState = columnState,
                        modifier = modifier,
                    )
                }
            }
            composable(route = Screen.StreamlineSignInMenuScreen.route) {
                val columnState = rememberColumnState()

                ScreenScaffold(scrollState = columnState) {
                    StreamlineSignInMenuScreen(
                        navController = navController,
                        columnState = columnState,
                        modifier = modifier,
                    )
                }
            }
            composable(route = Screen.StreamlineSignInSampleScreen.route) {
                val columnState = rememberColumnState()

                ScreenScaffold(scrollState = columnState) {
                    StreamlineSignInSampleScreen(
                        navController = navController,
                        columnState = columnState,
                        modifier = modifier,
                    )
                }
            }
            composable(route = Screen.GoogleSignInScreen.route) {
                GoogleSignInScreen(
                    onAuthCancelled = navController::popBackStack,
                    onAuthSucceed = navController::popBackStack,
                    modifier = modifier,
                    viewModel = viewModel(factory = GoogleSignInSampleViewModelFactory),
                )
            }
            composable(route = Screen.GoogleSignOutScreen.route) {
                GoogleSignOutScreen(navController = navController)
            }
            composable(route = Screen.TokenShareDefaultKeyScreen.route) {
                val columnState = rememberColumnState()

                ScreenScaffold(scrollState = columnState) {
                    TokenShareDefaultKeyScreen(columnState = columnState, modifier = modifier)
                }
            }
            composable(route = Screen.TokenShareCustomKeyScreen.route) {
                val columnState = rememberColumnState()

                ScreenScaffold(scrollState = columnState) {
                    TokenShareCustomKeyScreen(columnState = columnState, modifier = modifier)
                }
            }
        }
    }
}

@WearPreviewSmallRound
@Composable
fun DefaultPreview() {
    WearApp()
}
