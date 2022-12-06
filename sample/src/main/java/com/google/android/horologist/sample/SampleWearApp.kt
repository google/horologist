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

package com.google.android.horologist.sample

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.navArgument
import androidx.wear.compose.navigation.rememberSwipeDismissableNavController
import com.google.android.horologist.audio.ui.VolumeScreen
import com.google.android.horologist.auth.AuthMenuScreen
import com.google.android.horologist.auth.googlesignin.GoogleSignInPromptSampleScreen
import com.google.android.horologist.auth.googlesignin.GoogleSignInSampleScreen
import com.google.android.horologist.auth.googlesignin.GoogleSignOutScreen
import com.google.android.horologist.auth.oauth.devicegrant.AuthDeviceGrantSampleScreen
import com.google.android.horologist.auth.oauth.devicegrant.AuthDeviceGrantSignInPromptScreen
import com.google.android.horologist.auth.oauth.pkce.AuthPKCESampleScreen
import com.google.android.horologist.auth.oauth.pkce.AuthPKCESignInPromptScreen
import com.google.android.horologist.composables.DatePicker
import com.google.android.horologist.composables.TimePicker
import com.google.android.horologist.composables.TimePickerWith12HourClock
import com.google.android.horologist.compose.layout.ScalingLazyColumnDefaults
import com.google.android.horologist.compose.navscaffold.NavScaffoldViewModel
import com.google.android.horologist.compose.navscaffold.WearNavScaffold
import com.google.android.horologist.compose.navscaffold.composable
import com.google.android.horologist.compose.navscaffold.lazyListComposable
import com.google.android.horologist.compose.navscaffold.scrollStateComposable
import com.google.android.horologist.datalayer.DataLayerNodesScreen
import com.google.android.horologist.datalayer.DataLayerNodesViewModel
import com.google.android.horologist.networks.NetworkScreen
import com.google.android.horologist.paging.PagingItemScreen
import com.google.android.horologist.paging.PagingScreen
import com.google.android.horologist.rotary.RotaryMenuScreen
import com.google.android.horologist.rotary.RotaryScrollScreen
import com.google.android.horologist.rotary.RotaryScrollWithFlingOrSnapScreen
import com.google.android.horologist.sectionedlist.SectionedListMenuScreen
import com.google.android.horologist.sectionedlist.expandable.SectionedListExpandableScreen
import com.google.android.horologist.sectionedlist.stateful.SectionedListStatefulScreen
import com.google.android.horologist.sectionedlist.stateless.SectionedListStatelessScreen
import java.time.LocalDateTime

@Composable
fun SampleWearApp() {
    val navController = rememberSwipeDismissableNavController()

    var time by remember { mutableStateOf(LocalDateTime.now()) }

    WearNavScaffold(startDestination = Screen.Menu.route, navController = navController) {
        composable(
            route = Screen.Menu.route,
            columnStateFactory = ScalingLazyColumnDefaults.belowTimeText()
        ) {
            MenuScreen(
                modifier = Modifier.fillMaxSize(),
                navigateToRoute = { route -> navController.navigate(route) },
                time = time,
                columnConfig = it.columnConfig
            )
        }
        composable(
            Screen.DataLayerNodes.route,
            columnStateFactory = ScalingLazyColumnDefaults.belowTimeText()
        ) {
            DataLayerNodesScreen(
                viewModel = viewModel(factory = DataLayerNodesViewModel.Factory),
                columnConfig = it.columnConfig
            )
        }
        composable(
            Screen.Network.route,
            columnStateFactory = ScalingLazyColumnDefaults.belowTimeText(firstItemIsFullWidth = true)
        ) {
            NetworkScreen(
                columnConfig = it.columnConfig
            )
        }
        composable(Screen.FillMaxRectangle.route) {
            FillMaxRectangleScreen()
        }
        composable(Screen.Volume.route) {
            VolumeScreen()
        }
        lazyListComposable(Screen.ScrollAway.route) {
            ScrollScreenLazyColumn(
                scrollState = it.scrollableState
            )
        }
        composable(
            Screen.ScrollAwaySLC.route,
            columnStateFactory = ScalingLazyColumnDefaults.belowTimeText()
        ) {
            ScrollAwayScreenScalingLazyColumn(
                columnConfig = it.columnConfig
            )
        }
        scrollStateComposable(
            Screen.ScrollAwayColumn.route
        ) {
            ScrollAwayScreenColumn(
                scrollState = it.scrollableState
            )
        }
        composable(Screen.DatePicker.route) {
            it.timeTextMode = NavScaffoldViewModel.TimeTextMode.Off

            DatePicker(
                date = time.toLocalDate(),
                onDateConfirm = {
                    time = time.toLocalTime().atDate(it)
                    navController.popBackStack()
                }
            )
        }
        composable(Screen.TimePicker.route) {
            it.timeTextMode = NavScaffoldViewModel.TimeTextMode.Off

            TimePickerWith12HourClock(
                time = time.toLocalTime(),
                onTimeConfirm = {
                    time = time.toLocalDate().atTime(it)
                    navController.popBackStack()
                }
            )
        }
        composable(Screen.TimeWithSecondsPicker.route) {
            it.timeTextMode = NavScaffoldViewModel.TimeTextMode.Off

            TimePicker(
                time = time.toLocalTime(),
                onTimeConfirm = {
                    time = time.toLocalDate().atTime(it)
                    navController.popBackStack()
                }
            )
        }
        composable(Screen.TimeWithoutSecondsPicker.route) {
            it.timeTextMode = NavScaffoldViewModel.TimeTextMode.Off

            TimePicker(
                time = time.toLocalTime(),
                onTimeConfirm = {
                    time = time.toLocalDate().atTime(it)
                    navController.popBackStack()
                },
                showSeconds = false
            )
        }
        composable(
            route = Screen.SectionedListMenuScreen.route,
            columnStateFactory = ScalingLazyColumnDefaults.belowTimeText()
        ) {
            SectionedListMenuScreen(
                modifier = Modifier.fillMaxSize(),
                navigateToRoute = { route -> navController.navigate(route) },
                columnConfig = it.columnConfig
            )
        }
        composable(
            Screen.SectionedListStatelessScreen.route,
            columnStateFactory = ScalingLazyColumnDefaults.belowTimeText()
        ) {
            SectionedListStatelessScreen(
                columnConfig = it.columnConfig
            )
        }
        composable(
            Screen.SectionedListStatefulScreen.route,
            columnStateFactory = ScalingLazyColumnDefaults.belowTimeText()
        ) {
            SectionedListStatefulScreen(
                columnConfig = it.columnConfig
            )
        }
        composable(
            Screen.SectionedListExpandableScreen.route,
            columnStateFactory = ScalingLazyColumnDefaults.belowTimeText()
        ) {
            SectionedListExpandableScreen(
                columnConfig = it.columnConfig
            )
        }
        composable(
            route = Screen.RotaryMenuScreen.route,
            columnStateFactory = ScalingLazyColumnDefaults.belowTimeText()
        ) {
            RotaryMenuScreen(
                modifier = Modifier.fillMaxSize(),
                navigateToRoute = { route -> navController.navigate(route) },
                columnConfig = it.columnConfig
            )
        }
        composable(route = Screen.RotaryScrollScreen.route) {
            RotaryScrollScreen()
        }
        composable(route = Screen.RotaryScrollWithFlingScreen.route) {
            RotaryScrollWithFlingOrSnapScreen(isFling = true, isSnap = false)
        }
        composable(route = Screen.RotarySnapListScreen.route) {
            RotaryScrollWithFlingOrSnapScreen(isFling = false, isSnap = true)
        }
        composable(
            route = Screen.AuthMenuScreen.route,
            columnStateFactory = ScalingLazyColumnDefaults.belowTimeText()
        ) {
            AuthMenuScreen(
                navigateToRoute = { route -> navController.navigate(route) },
                modifier = Modifier.fillMaxSize(),
                columnConfig = it.columnConfig
            )
        }
        composable(
            route = Screen.AuthPKCESignInPromptScreen.route,
            columnStateFactory = ScalingLazyColumnDefaults.belowTimeText()
        ) {
            AuthPKCESignInPromptScreen(
                navController = navController,
                columnConfig = it.columnConfig
            )
        }
        composable(route = Screen.AuthPKCEScreen.route) {
            AuthPKCESampleScreen(
                onAuthSuccess = { navController.popBackStack() }
            )
        }
        composable(
            route = Screen.AuthDeviceGrantSignInPromptScreen.route,
            columnStateFactory = ScalingLazyColumnDefaults.belowTimeText()
        ) {
            AuthDeviceGrantSignInPromptScreen(
                navController = navController,
                columnConfig = it.columnConfig
            )
        }
        composable(route = Screen.AuthDeviceGrantScreen.route) {
            AuthDeviceGrantSampleScreen(
                onAuthSuccess = { navController.popBackStack() }
            )
        }
        composable(
            route = Screen.GoogleSignInPromptSampleScreen.route,
            columnStateFactory = ScalingLazyColumnDefaults.belowTimeText()
        ) {
            GoogleSignInPromptSampleScreen(
                navController = navController,
                columnConfig = it.columnConfig
            )
        }
        composable(route = Screen.AuthGoogleSignInScreen.route) {
            GoogleSignInSampleScreen(
                onAuthSuccess = { navController.popBackStack() }
            )
        }
        composable(route = Screen.AuthGoogleSignOutScreen.route) {
            GoogleSignOutScreen(navController = navController)
        }
        composable(
            route = Screen.AuthMenuScreen.route,
            columnStateFactory = ScalingLazyColumnDefaults.belowTimeText()
        ) {
            AuthMenuScreen(
                modifier = Modifier.fillMaxSize(),
                navigateToRoute = { route -> navController.navigate(route) },
                columnConfig = it.columnConfig
            )
        }
        composable(
            route = Screen.AuthPKCESignInPromptScreen.route,
            columnStateFactory = ScalingLazyColumnDefaults.belowTimeText()
        ) {
            AuthPKCESignInPromptScreen(
                navController = navController,
                columnConfig = it.columnConfig
            )
        }
        composable(route = Screen.AuthPKCEScreen.route) {
            AuthPKCESampleScreen(
                onAuthSuccess = { navController.popBackStack() }
            )
        }
        composable(route = Screen.AuthDeviceGrantScreen.route) {
            AuthDeviceGrantSampleScreen(
                onAuthSuccess = { navController.popBackStack() }
            )
        }
        composable(route = Screen.AuthPKCEScreen.route) {
            AuthPKCESampleScreen(
                onAuthSuccess = { navController.popBackStack() }
            )
        }
        composable(
            route = Screen.AuthDeviceGrantSignInPromptScreen.route,
            columnStateFactory = ScalingLazyColumnDefaults.belowTimeText()
        ) {
            AuthDeviceGrantSignInPromptScreen(
                navController = navController,
                columnConfig = it.columnConfig
            )
        }
        composable(route = Screen.AuthDeviceGrantScreen.route) {
            AuthDeviceGrantSampleScreen(
                onAuthSuccess = { navController.popBackStack() }
            )
        }
        composable(
            route = Screen.GoogleSignInPromptSampleScreen.route,
            columnStateFactory = ScalingLazyColumnDefaults.belowTimeText()
        ) {
            GoogleSignInPromptSampleScreen(
                navController = navController,
                columnConfig = it.columnConfig
            )
        }
        composable(route = Screen.AuthGoogleSignInScreen.route) {
            GoogleSignInSampleScreen(
                onAuthSuccess = { navController.popBackStack() }
            )
        }
        composable(route = Screen.AuthGoogleSignOutScreen.route) {
            GoogleSignOutScreen(navController = navController)
        }
        composable(
            route = Screen.Paging.route,
            columnStateFactory = ScalingLazyColumnDefaults.belowTimeText(firstItemIsFullWidth = true)
        ) {
            PagingScreen(navController = navController, columnConfig = it.columnConfig)
        }
        composable(
            route = Screen.PagingItem.route,
            arguments = listOf(
                navArgument("id") {
                    type = NavType.IntType
                }
            )
        ) {
            PagingItemScreen(it.arguments!!.getInt("id"))
        }
    }
}
