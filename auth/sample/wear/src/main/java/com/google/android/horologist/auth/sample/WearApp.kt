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

package com.google.android.horologist.auth.sample

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.wear.compose.navigation.rememberSwipeDismissableNavController
import com.google.android.horologist.auth.data.watch.oauth.common.impl.google.api.DeviceCodeResponse
import com.google.android.horologist.auth.data.watch.oauth.common.impl.google.api.TokenResponse
import com.google.android.horologist.auth.data.watch.oauth.devicegrant.impl.DeviceGrantDefaultConfig
import com.google.android.horologist.auth.data.watch.oauth.pkce.impl.PKCEDefaultConfig
import com.google.android.horologist.auth.data.watch.oauth.pkce.impl.google.PKCEOAuthCodeGooglePayload
import com.google.android.horologist.auth.sample.screens.MainScreen
import com.google.android.horologist.auth.sample.screens.common.streamline.StreamlineSignInMenuScreen
import com.google.android.horologist.auth.sample.screens.common.streamline.StreamlineSignInSampleScreen
import com.google.android.horologist.auth.sample.screens.googlesignin.prompt.GoogleSignInPromptSampleScreen
import com.google.android.horologist.auth.sample.screens.googlesignin.signin.GoogleSignInSampleViewModelFactory
import com.google.android.horologist.auth.sample.screens.googlesignin.signout.GoogleSignOutScreen
import com.google.android.horologist.auth.sample.screens.oauth.devicegrant.prompt.DeviceGrantSignInPromptScreen
import com.google.android.horologist.auth.sample.screens.oauth.devicegrant.signin.DeviceGrantSampleViewModelFactory
import com.google.android.horologist.auth.sample.screens.oauth.devicegrant.signout.DeviceGrantSignOutScreen
import com.google.android.horologist.auth.sample.screens.oauth.pkce.prompt.PKCESignInPromptScreen
import com.google.android.horologist.auth.sample.screens.oauth.pkce.signin.PKCESampleViewModelFactory
import com.google.android.horologist.auth.sample.screens.oauth.pkce.signout.PKCESignOutScreen
import com.google.android.horologist.auth.sample.screens.tokenshare.customkey.TokenShareCustomKeyScreen
import com.google.android.horologist.auth.sample.screens.tokenshare.defaultkey.TokenShareDefaultKeyScreen
import com.google.android.horologist.auth.ui.googlesignin.signin.GoogleSignInScreen
import com.google.android.horologist.auth.ui.oauth.devicegrant.signin.DeviceGrantSignInScreen
import com.google.android.horologist.auth.ui.oauth.pkce.signin.PKCESignInScreen
import com.google.android.horologist.compose.navscaffold.WearNavScaffold
import com.google.android.horologist.compose.navscaffold.composable
import com.google.android.horologist.compose.navscaffold.scrollable

@Composable
fun WearApp(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberSwipeDismissableNavController()
) {
    WearNavScaffold(startDestination = Screen.MainScreen.route, navController = navController) {
        scrollable(
            route = Screen.MainScreen.route
        ) {
            MainScreen(
                navigateToRoute = navController::navigate,
                modifier = modifier,
                columnState = it.columnState
            )
        }
        scrollable(
            route = Screen.PKCESignInPromptScreen.route
        ) {
            PKCESignInPromptScreen(
                navController = navController,
                modifier = modifier,
                columnState = it.columnState
            )
        }
        composable(route = Screen.PKCESignInScreen.route) {
            PKCESignInScreen<PKCEDefaultConfig, PKCEOAuthCodeGooglePayload, TokenResponse>(
                onAuthSucceed = navController::popBackStack,
                modifier = modifier,
                viewModel = viewModel(factory = PKCESampleViewModelFactory)
            )
        }
        composable(route = Screen.PKCESignOutScreen.route) {
            PKCESignOutScreen(navController = navController)
        }
        scrollable(
            route = Screen.DeviceGrantSignInPromptScreen.route
        ) {
            DeviceGrantSignInPromptScreen(
                navController = navController,
                columnState = it.columnState,
                modifier = modifier
            )
        }
        composable(route = Screen.DeviceGrantSignInScreen.route) {
            DeviceGrantSignInScreen<DeviceGrantDefaultConfig, DeviceCodeResponse, String>(
                onAuthSucceed = navController::popBackStack,
                modifier = modifier,
                viewModel = viewModel(factory = DeviceGrantSampleViewModelFactory)
            )
        }
        composable(route = Screen.DeviceGrantSignOutScreen.route) {
            DeviceGrantSignOutScreen(navController = navController)
        }
        scrollable(
            route = Screen.GoogleSignInPromptSampleScreen.route
        ) {
            GoogleSignInPromptSampleScreen(
                navController = navController,
                columnState = it.columnState,
                modifier = modifier
            )
        }
        scrollable(route = Screen.StreamlineSignInMenuScreen.route) {
            StreamlineSignInMenuScreen(
                navController = navController,
                columnState = it.columnState,
                modifier = modifier
            )
        }
        scrollable(route = Screen.StreamlineSignInSampleScreen.route) {
            StreamlineSignInSampleScreen(
                navController = navController,
                columnState = it.columnState,
                modifier = modifier
            )
        }
        composable(route = Screen.GoogleSignInScreen.route) {
            GoogleSignInScreen(
                onAuthCancelled = navController::popBackStack,
                onAuthSucceed = navController::popBackStack,
                modifier = modifier,
                viewModel = viewModel(factory = GoogleSignInSampleViewModelFactory)
            )
        }
        composable(route = Screen.GoogleSignOutScreen.route) {
            GoogleSignOutScreen(navController = navController)
        }
        scrollable(route = Screen.TokenShareDefaultKeyScreen.route) {
            TokenShareDefaultKeyScreen(columnState = it.columnState, modifier = modifier)
        }
        scrollable(route = Screen.TokenShareCustomKeyScreen.route) {
            TokenShareCustomKeyScreen(columnState = it.columnState, modifier = modifier)
        }
    }
}

@Preview(device = Devices.WEAR_OS_SMALL_ROUND, showSystemUi = true)
@Composable
fun DefaultPreview() {
    WearApp()
}
