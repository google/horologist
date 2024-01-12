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

package com.google.android.horologist.datalayer.sample.screens.nodesactions

import androidx.lifecycle.SavedStateHandle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.navArgument
import androidx.wear.compose.navigation.composable
import com.google.android.horologist.compose.layout.ScreenScaffold
import com.google.android.horologist.compose.layout.rememberColumnState
import com.google.android.horologist.datalayer.sample.Screen
import java.net.URLDecoder
import java.net.URLEncoder

private const val nodeIdArg = "nodeId"
private const val routePrefix = "appHelperNodeDetailsScreen"

private val URL_CHARACTER_ENCODING = Charsets.UTF_8.name()

const val nodeDetailsScreenRoute = "$routePrefix/{$nodeIdArg}"

internal class NodeDetailsScreenArgs(val nodeId: String) {
    constructor(savedStateHandle: SavedStateHandle) :
        this(URLDecoder.decode(checkNotNull(savedStateHandle[nodeIdArg]), URL_CHARACTER_ENCODING))
}

fun NavController.navigateToNodeDetailsScreen(message: String) {
    val encodedMessage = URLEncoder.encode(message, URL_CHARACTER_ENCODING)
    this.navigate("$routePrefix/$encodedMessage")
}

fun NavGraphBuilder.nodeDetailsScreen() {
    composable(
        route = Screen.AppHelperNodeDetailsScreen.route,
        arguments = listOf(
            navArgument(nodeIdArg) { type = NavType.StringType },
        ),
    ) {
        val columnState = rememberColumnState()

        ScreenScaffold(scrollState = columnState) {
            NodeDetailsScreen(
                columnState = columnState,
            )
        }
    }
}
