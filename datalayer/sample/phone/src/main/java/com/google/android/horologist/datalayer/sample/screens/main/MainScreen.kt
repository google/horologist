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
import com.google.android.horologist.datalayer.sample.screens.inappprompts.InstallPlaystoreAppPromptDemoScreen
import com.google.android.horologist.datalayer.sample.screens.inappprompts.InstallSampleAppPromptDemoScreen
import com.google.android.horologist.datalayer.sample.screens.menu.MenuScreen
import com.google.android.horologist.datalayer.sample.screens.nodes.NodesScreen

@Composable
fun MainScreen(
    onShowInstallAppPrompt: () -> Unit,
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
                composable(route = Screen.InstallAppPromptDemoScreen.route) {
                    InstallPlaystoreAppPromptDemoScreen(onShowInstallAppPrompt = onShowInstallAppPrompt)
                }
                composable(route = Screen.InstallAppPromptDemo2Screen.route) {
                    InstallSampleAppPromptDemoScreen()
                }
                composable(route = Screen.CounterScreen.route) {
                    CounterScreen()
                }
            }
        }
    }
}
