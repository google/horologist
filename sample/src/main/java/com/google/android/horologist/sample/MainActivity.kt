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

@file:OptIn(ExperimentalWearMaterialApi::class)

package com.google.android.horologist.sample

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.wear.compose.material.ExperimentalWearMaterialApi
import androidx.wear.compose.material.Scaffold
import androidx.wear.compose.navigation.SwipeDismissableNavHost
import androidx.wear.compose.material.ScalingLazyListState
import androidx.wear.compose.navigation.composable
import androidx.wear.compose.navigation.rememberSwipeDismissableNavController
import com.google.android.horologist.audioui.VolumeScreen
import com.google.android.horologist.compose.navscaffold.WearNavScaffold
import com.google.android.horologist.compose.navscaffold.scalingLazyColumnComposable

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            WearApp()
        }
    }
}

@Composable
fun WearApp() {
    val swipeDismissableNavController = rememberSwipeDismissableNavController()

    WearNavScaffold(
        startDestination = Screen.Menu.route,
        navController = swipeDismissableNavController
    ) {
        scalingLazyColumnComposable(
            route = Screen.Menu.route,
            scrollStateBuilder = { ScalingLazyListState(initialCenterItemIndex = 0) }
        ) {
            MenuScreen(
                modifier = Modifier.fillMaxSize(),
                navigateToRoute = { swipeDismissableNavController.navigate(it) },
                scrollState = it.scrollableState,
                focusRequester = it.viewModel.focusRequester
            )
        }
        composable(Screen.FillMaxRectangle.route) {
            FillMaxRectangleScreen()
        }
        composable(Screen.Volume.route) {
            val focusRequester = remember { FocusRequester() }

            VolumeScreen(focusRequester = focusRequester)

            LaunchedEffect(Unit) {
                focusRequester.requestFocus()
            }
            composable(Screen.FadeAway.route) {
                FadeAwayScreenLazyColumn()
            }
            composable(Screen.FadeAwaySLC.route) {
                FadeAwayScreenSLC()
            }
            composable(Screen.FadeAwayColumn.route) {
                FadeAwayScreenColumn()
            }
        }
    }
}
