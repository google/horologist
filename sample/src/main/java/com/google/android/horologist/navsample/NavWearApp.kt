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
import androidx.wear.compose.material.VignettePosition
import androidx.wear.compose.navigation.composable
import androidx.wear.compose.navigation.rememberSwipeDismissableNavController
import com.google.android.horologist.compose.navscaffold.NavScaffoldViewModel
import com.google.android.horologist.compose.navscaffold.WearNavScaffold
import com.google.android.horologist.compose.navscaffold.scalingLazyColumnComposable
import com.google.android.horologist.compose.navscaffold.scrollStateComposable
import com.google.android.horologist.compose.navscaffold.wearNavComposable

@Composable
fun NavWearApp() {
    val swipeDismissableNavController = rememberSwipeDismissableNavController()

    WearNavScaffold(
        startDestination = NavScreen.Menu.route,
        navController = swipeDismissableNavController
    ) {
        scalingLazyColumnComposable(
            NavScreen.Menu.route,
            scrollStateBuilder = { ScalingLazyListState() }
        ) {
            NavMenuScreen(
                navigateToRoute = { swipeDismissableNavController.navigate(it) },
                scrollState = it.scrollableState,
                focusRequester = it.viewModel.focusRequester
            )
        }

        scalingLazyColumnComposable(
            NavScreen.ScalingLazyColumn.route,
            scrollStateBuilder = { ScalingLazyListState() }
        ) {
            it.viewModel.timeTextMode = NavScaffoldViewModel.TimeTextMode.Off
            it.viewModel.vignettePosition = NavScaffoldViewModel.VignetteMode.On(VignettePosition.TopAndBottom)
            it.viewModel.positionIndicatorMode = NavScaffoldViewModel.PositionIndicatorMode.On

            BigScalingLazyColumn(scrollState = it.scrollableState, focusRequester = it.viewModel.focusRequester)
        }

        scrollStateComposable(NavScreen.Column.route, scrollStateBuilder = { ScrollState(0) }) {
            BigColumn(scrollState = it.scrollableState, focusRequester = it.viewModel.focusRequester)
        }

        wearNavComposable(NavScreen.Volume.route) { _, viewModel ->
            viewModel.timeTextMode = NavScaffoldViewModel.TimeTextMode.Off

            FillerScreen(label = "Volume")
        }

        composable(NavScreen.Pager.route) {
            FillerScreen(label = "Pager (TODO)")
        }

        composable(NavScreen.Volume.route) {
            FillerScreen(label = "Normal (TODO)")
        }

        composable(NavScreen.NoScrolling.route) {
            FillerScreen(label = "No Scrolling")
        }
    }
}