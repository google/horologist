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

package com.google.android.horologist.datalayer.sample.screens.main

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.android.horologist.datalayer.sample.screens.Screen
import com.google.android.horologist.datalayer.sample.screens.counter.CounterScreen
import com.google.android.horologist.datalayer.sample.screens.inappprompts.custom.installapp.InstallAppCustomPromptDemoScreen
import com.google.android.horologist.datalayer.sample.screens.inappprompts.custom.installtile.InstallTileCustomPromptDemoScreen
import com.google.android.horologist.datalayer.sample.screens.inappprompts.custom.reengage.ReEngageCustomPromptDemoScreen
import com.google.android.horologist.datalayer.sample.screens.inappprompts.custom.signin.SignInCustomPromptDemoScreen
import com.google.android.horologist.datalayer.sample.screens.inappprompts.installapp.InstallAppPromptDemoScreen
import com.google.android.horologist.datalayer.sample.screens.inappprompts.installtile.InstallTilePromptDemoScreen
import com.google.android.horologist.datalayer.sample.screens.inappprompts.reengage.ReEngagePromptDemoScreen
import com.google.android.horologist.datalayer.sample.screens.inappprompts.signin.SignInPromptDemoScreen
import com.google.android.horologist.datalayer.sample.screens.menu.MenuScreen
import com.google.android.horologist.datalayer.sample.screens.nodes.NodesScreen
import com.google.android.horologist.datalayer.sample.screens.nodeslistener.NodesListenerScreen

@Composable
fun MainScreen(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
) {
    Scaffold(
        modifier = modifier,
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            NavHost(
                navController = navController,
                startDestination = Screen.MenuScreen.route,
                modifier = modifier,
            ) {
                composable(route = Screen.MenuScreen.route) {
                    MenuScreen(navController = navController)
                }
                composable(route = Screen.AppHelperNodesScreen.route) {
                    NodesScreen()
                }
                composable(route = Screen.AppHelperNodesListenerScreen.route) {
                    NodesListenerScreen()
                }
                composable(route = Screen.InstallAppPromptDemoScreen.route) {
                    InstallAppPromptDemoScreen()
                }
                composable(route = Screen.ReEngagePromptDemoScreen.route) {
                    ReEngagePromptDemoScreen()
                }
                composable(route = Screen.SignInPromptDemoScreen.route) {
                    SignInPromptDemoScreen()
                }
                composable(route = Screen.InstallTilePromptDemoScreen.route) {
                    InstallTilePromptDemoScreen()
                }
                composable(route = Screen.InstallAppCustomPromptDemoScreen.route) {
                    InstallAppCustomPromptDemoScreen()
                }
                composable(route = Screen.ReEngageCustomPromptDemoScreen.route) {
                    ReEngageCustomPromptDemoScreen()
                }
                composable(route = Screen.SignInCustomPromptDemoScreen.route) {
                    SignInCustomPromptDemoScreen()
                }
                composable(route = Screen.InstallTileCustomPromptDemoScreen.route) {
                    InstallTileCustomPromptDemoScreen()
                }
                composable(route = Screen.CounterScreen.route) {
                    CounterScreen()
                }
            }
        }
    }
}
