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

package com.google.android.horologist.datalayer.sample.screens

sealed class Screen(
    val route: String,
) {
    data object MenuScreen : Screen("menuScreen")
    data object AppHelperNodesScreen : Screen("appHelperNodesScreen")
    data object AppHelperNodesListenerScreen : Screen("appHelperNodesListenerScreen")
    data object InstallAppPromptDemoScreen : Screen("installAppPromptDemoScreen")
    data object ReEngagePromptDemoScreen : Screen("reEngagePromptDemoScreen")
    data object SignInPromptDemoScreen : Screen("signInPromptDemoScreen")
    data object InstallTilePromptDemoScreen : Screen("installTilePromptDemoScreen")
    data object InstallAppCustomPromptDemoScreen : Screen("installAppCustomPromptDemoScreen")
    data object ReEngageCustomPromptDemoScreen : Screen("reEngageCustomPromptDemoScreen")
    data object SignInCustomPromptDemoScreen : Screen("signInCustomPromptDemoScreen")
    data object InstallTileCustomPromptDemoScreen : Screen("installTileCustomPromptDemoScreen")
    data object CounterScreen : Screen("counterScreen")
}
