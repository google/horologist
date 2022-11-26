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

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.wear.compose.navigation.rememberSwipeDismissableNavController
import com.google.android.horologist.audio.ui.VolumeScreen
import com.google.android.horologist.auth.AuthMenuScreen
import com.google.android.horologist.auth.oauth.devicegrant.AuthDeviceGrantScreen
import com.google.android.horologist.auth.oauth.pkce.AuthPKCEScreen
import com.google.android.horologist.composables.DatePicker
import com.google.android.horologist.composables.TimePicker
import com.google.android.horologist.composables.TimePickerWith12HourClock
import com.google.android.horologist.compose.navscaffold.WearNavScaffold
import com.google.android.horologist.compose.navscaffold.lazyListComposable
import com.google.android.horologist.compose.navscaffold.scalingLazyColumn
import com.google.android.horologist.compose.navscaffold.scalingLazyColumnComposable
import com.google.android.horologist.compose.navscaffold.scrollStateComposable
import com.google.android.horologist.compose.navscaffold.wearNav
import com.google.android.horologist.datalayer.DataLayerNodesScreen
import com.google.android.horologist.datalayer.DataLayerNodesViewModel
import com.google.android.horologist.networks.NetworkScreen
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
        scalingLazyColumn(route = Screen.Menu.route) {
            MenuScreen(
                modifier = Modifier.fillMaxSize(),
                navigateToRoute = { route -> navController.navigate(route) },
                time = time,
                config = it.columnConfig
            )
        }
        scalingLazyColumn(Screen.DataLayerNodes.route) {
            DataLayerNodesScreen(
                viewModel = viewModel(factory = DataLayerNodesViewModel.Factory),
                config = it.columnConfig
            )
        }
        scalingLazyColumn(Screen.Network.route) {
            NetworkScreen(
                config = it.columnConfig
            )
        }
        wearNav(Screen.FillMaxRectangle.route) {
            FillMaxRectangleScreen()
        }
        wearNav(Screen.Volume.route) {
            VolumeScreen()
        }
        lazyListComposable(Screen.ScrollAway.route) {
            ScrollScreenLazyColumn(
                state = it.scrollableState
            )
        }
        scalingLazyColumn(Screen.ScrollAwaySLC.route) {
            ScrollAwayScreenScalingLazyColumn(
                config = it.columnConfig
            )
        }
        scrollStateComposable(
            Screen.ScrollAwayColumn.route) {
            ScrollAwayScreenColumn(
                state = it.scrollableState
            )
        }
        wearNav(Screen.DatePicker.route) {
            DatePicker(
                date = time.toLocalDate(),
                onDateConfirm = {
                    time = time.toLocalTime().atDate(it)
                    navController.popBackStack()
                },
            )
        }
        wearNav(Screen.TimePicker.route) {
            TimePickerWith12HourClock(
                time = time.toLocalTime(),
                onTimeConfirm = {
                    time = time.toLocalDate().atTime(it)
                    navController.popBackStack()
                }
            )
        }
        wearNav(Screen.TimeWithSecondsPicker.route) {
            TimePicker(
                time = time.toLocalTime(),
                onTimeConfirm = {
                    time = time.toLocalDate().atTime(it)
                    navController.popBackStack()
                }
            )
        }
        wearNav(Screen.TimeWithoutSecondsPicker.route) {
            TimePicker(
                time = time.toLocalTime(),
                onTimeConfirm = {
                    time = time.toLocalDate().atTime(it)
                    navController.popBackStack()
                },
                showSeconds = false
            )
        }
        scalingLazyColumn(route = Screen.SectionedListMenuScreen.route) {
            SectionedListMenuScreen(
                modifier = Modifier.fillMaxSize(),
                navigateToRoute = { route -> navController.navigate(route) },
                config = it.columnConfig
            )
        }
        scalingLazyColumn(Screen.SectionedListStatelessScreen.route) {
            SectionedListStatelessScreen(
                config = it.columnConfig
            )
        }
        scalingLazyColumn(Screen.SectionedListStatefulScreen.route) {
            SectionedListStatefulScreen(
                config = it.columnConfig
            )
        }
        scalingLazyColumn(Screen.SectionedListExpandableScreen.route) {
            SectionedListExpandableScreen(
                config = it.columnConfig
            )
        }
        scalingLazyColumn(route = Screen.RotaryMenuScreen.route) {
            RotaryMenuScreen(
                modifier = Modifier.fillMaxSize(),
                navigateToRoute = { route -> navController.navigate(route) },
                config = it.columnConfig
            )
        }
        scalingLazyColumn(route = Screen.RotaryScrollScreen.route) {
            RotaryScrollScreen(
                config = it.columnConfig
            )
        }
        scalingLazyColumnComposable(route = Screen.RotaryScrollWithFlingScreen.route) {
            RotaryScrollWithFlingOrSnapScreen(isFling = true, isSnap = false)
        }
        scalingLazyColumnComposable(route = Screen.RotarySnapListScreen.route) {
            RotaryScrollWithFlingOrSnapScreen(isFling = false, isSnap = true)
        }
        scalingLazyColumn(route = Screen.AuthMenuScreen.route) {
            AuthMenuScreen(
                modifier = Modifier.fillMaxSize(),
                navigateToRoute = { route -> navController.navigate(route) },
                config = it.columnConfig
            )
        }
        wearNav(route = Screen.AuthPKCEScreen.route) {
            AuthPKCEScreen()
        }
        wearNav(route = Screen.AuthDeviceGrantScreen.route) {
            AuthDeviceGrantScreen()
        }
    }
}
