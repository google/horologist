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
import androidx.wear.compose.navigation.composable
import androidx.wear.compose.navigation.rememberSwipeDismissableNavController
import com.google.android.horologist.audio.ui.VolumeScreen
import com.google.android.horologist.auth.AuthMenuScreen
import com.google.android.horologist.auth.googlesignin.GoogleSignInSampleScreen
import com.google.android.horologist.auth.googlesignin.GoogleSignOutScreen
import com.google.android.horologist.auth.oauth.devicegrant.AuthDeviceGrantSampleScreen
import com.google.android.horologist.auth.oauth.pkce.AuthPKCESampleScreen
import com.google.android.horologist.composables.DatePicker
import com.google.android.horologist.composables.TimePicker
import com.google.android.horologist.composables.TimePickerWith12HourClock
import com.google.android.horologist.compose.navscaffold.WearNavScaffold
import com.google.android.horologist.compose.navscaffold.composableLazyList
import com.google.android.horologist.compose.navscaffold.composableScalingLazyColumn
import com.google.android.horologist.compose.navscaffold.composableWearNav
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
        composableScalingLazyColumn(route = Screen.Menu.route) {
            MenuScreen(
                modifier = Modifier.fillMaxSize(),
                navigateToRoute = { route -> navController.navigate(route) },
                time = time,
                config = it.columnConfig
            )
        }
        composableScalingLazyColumn(Screen.DataLayerNodes.route) {
            DataLayerNodesScreen(
                viewModel = viewModel(factory = DataLayerNodesViewModel.Factory),
                config = it.columnConfig
            )
        }
        composableScalingLazyColumn(Screen.Network.route) {
            NetworkScreen(
                config = it.columnConfig
            )
        }
        composableWearNav(Screen.FillMaxRectangle.route) {
            FillMaxRectangleScreen()
        }
        composableWearNav(Screen.Volume.route) {
            VolumeScreen()
        }
        composableLazyList(Screen.ScrollAway.route) {
            ScrollScreenLazyColumn(
                scrollState = it.scrollableState
            )
        }
        composableScalingLazyColumn(Screen.ScrollAwaySLC.route) {
            ScrollAwayScreenScalingLazyColumn(
                config = it.columnConfig
            )
        }
        scrollStateComposable(
            Screen.ScrollAwayColumn.route
        ) {
            ScrollAwayScreenColumn(
                scrollState = it.scrollableState
            )
        }
        composableWearNav(Screen.DatePicker.route) {
            DatePicker(
                date = time.toLocalDate(),
                onDateConfirm = {
                    time = time.toLocalTime().atDate(it)
                    navController.popBackStack()
                }
            )
        }
        composableWearNav(Screen.TimePicker.route) {
            TimePickerWith12HourClock(
                time = time.toLocalTime(),
                onTimeConfirm = {
                    time = time.toLocalDate().atTime(it)
                    navController.popBackStack()
                }
            )
        }
        composableWearNav(Screen.TimeWithSecondsPicker.route) {
            TimePicker(
                time = time.toLocalTime(),
                onTimeConfirm = {
                    time = time.toLocalDate().atTime(it)
                    navController.popBackStack()
                }
            )
        }
        composableWearNav(Screen.TimeWithoutSecondsPicker.route) {
            TimePicker(
                time = time.toLocalTime(),
                onTimeConfirm = {
                    time = time.toLocalDate().atTime(it)
                    navController.popBackStack()
                },
                showSeconds = false
            )
        }
        composableScalingLazyColumn(route = Screen.SectionedListMenuScreen.route) {
            SectionedListMenuScreen(
                modifier = Modifier.fillMaxSize(),
                navigateToRoute = { route -> navController.navigate(route) },
                config = it.columnConfig
            )
        }
        composableScalingLazyColumn(Screen.SectionedListStatelessScreen.route) {
            SectionedListStatelessScreen(
                config = it.columnConfig
            )
        }
        composableScalingLazyColumn(Screen.SectionedListStatefulScreen.route) {
            SectionedListStatefulScreen(
                config = it.columnConfig
            )
        }
        composableScalingLazyColumn(Screen.SectionedListExpandableScreen.route) {
            SectionedListExpandableScreen(
                config = it.columnConfig
            )
        }
        composable(route = Screen.RotaryMenuScreen.route) {
            RotaryMenuScreen(
                modifier = Modifier.fillMaxSize(),
                navigateToRoute = { route -> navController.navigate(route) }
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
        composableScalingLazyColumn(route = Screen.AuthMenuScreen.route) {
            AuthMenuScreen(
                modifier = Modifier.fillMaxSize(),
                navigateToRoute = { route -> navController.navigate(route) },
                config = it.columnConfig
            )
        }
        composableWearNav(route = Screen.AuthPKCEScreen.route) {
            AuthPKCESampleScreen(
                onAuthSuccess = { navController.popBackStack() }
            )
        }
        composableWearNav(route = Screen.AuthDeviceGrantScreen.route) {
            AuthDeviceGrantSampleScreen(
                onAuthSuccess = { navController.popBackStack() }
            )
        }
        wearNav(route = Screen.AuthPKCEScreen.route) {
            AuthPKCESampleScreen(
                onAuthSuccess = { navController.popBackStack() }
            )
        }
        wearNav(route = Screen.AuthDeviceGrantScreen.route) {
            AuthDeviceGrantSampleScreen(
                onAuthSuccess = { navController.popBackStack() }
            )
        }
        wearNav(route = Screen.AuthGoogleSignInScreen.route) {
            GoogleSignInSampleScreen(
                onAuthSuccess = { navController.popBackStack() }
            )
        }
        wearNav(route = Screen.AuthGoogleSignOutScreen.route) {
            GoogleSignOutScreen(navController = navController)
        }
        wearNav(route = Screen.Paging.route) {
            PagingScreen(navController)
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
