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
import androidx.compose.runtime.Composable
import androidx.wear.compose.material.ScalingLazyListState
import androidx.wear.compose.navigation.composable
import androidx.wear.compose.navigation.rememberSwipeDismissableNavController
import com.google.android.horologist.compose.navscaffold.NavScaffold
import com.google.android.horologist.compose.navscaffold.scaffoldComposable

@Composable
fun NavWearApp() {
    val swipeDismissableNavController = rememberSwipeDismissableNavController()

    NavScaffold(
        startDestination = NavScreen.Menu.route,
        navController = swipeDismissableNavController
    ) {
        scaffoldComposable(
            NavScreen.Menu.route,
            scrollStateBuilder = { ScalingLazyListState() }
        ) { (_, scrollState) ->
            NavMenuScreen(
                navigateToRoute = { swipeDismissableNavController.navigate(it) },
                scrollState = scrollState,
            )
        }
        scaffoldComposable(
            NavScreen.ScalingLazyColumn.route,
            scrollStateBuilder = { ScalingLazyListState() }
        ) { (_, scrollState) ->
            BigScalingLazyColumn(scrollState = scrollState)
        }
        scaffoldComposable(
            NavScreen.Column.route,
            scrollStateBuilder = { ScrollState(0) }) { (_, scrollState) ->

            BigColumn(scrollState = scrollState)
        }
        composable(NavScreen.Volume.route) {
            FillerScreen(label = "Volume")
        }
    }
}