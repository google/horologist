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

import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.NavType
import androidx.navigation.navArgument
import androidx.wear.compose.foundation.rememberSwipeToDismissBoxState
import androidx.wear.compose.navigation.SwipeDismissableNavHost
import androidx.wear.compose.navigation.composable
import androidx.wear.compose.navigation.rememberSwipeDismissableNavController
import androidx.wear.compose.navigation.rememberSwipeDismissableNavHostState
import com.google.android.horologist.audio.ui.VolumeScreen
import com.google.android.horologist.composables.DatePicker
import com.google.android.horologist.composables.TimePicker
import com.google.android.horologist.composables.TimePickerWith12HourClock
import com.google.android.horologist.compose.layout.AppScaffold
import com.google.android.horologist.compose.layout.ScalingLazyColumnState.RotaryMode
import com.google.android.horologist.compose.layout.ScreenScaffold
import com.google.android.horologist.materialcomponents.SampleAlertDialog
import com.google.android.horologist.materialcomponents.SampleButtonScreen
import com.google.android.horologist.materialcomponents.SampleCardScreen
import com.google.android.horologist.materialcomponents.SampleChipIconWithProgressScreen
import com.google.android.horologist.materialcomponents.SampleChipScreen
import com.google.android.horologist.materialcomponents.SampleCompactChipScreen
import com.google.android.horologist.materialcomponents.SampleConfirmationLauncher
import com.google.android.horologist.materialcomponents.SampleConfirmationScreen
import com.google.android.horologist.materialcomponents.SampleIconScreen
import com.google.android.horologist.materialcomponents.SampleOutlinedChipScreen
import com.google.android.horologist.materialcomponents.SampleOutlinedCompactChipScreen
import com.google.android.horologist.materialcomponents.SampleSplitToggleChipScreen
import com.google.android.horologist.materialcomponents.SampleStepperScreen
import com.google.android.horologist.materialcomponents.SampleTitleScreen
import com.google.android.horologist.materialcomponents.SampleToggleButtonScreen
import com.google.android.horologist.materialcomponents.SampleToggleChipScreen
import com.google.android.horologist.networks.NetworkScreen
import com.google.android.horologist.pager.SamplePagerScreen
import com.google.android.horologist.pager.SampleVerticalPagerScreen
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
    val swipeToDismissBoxState = rememberSwipeToDismissBoxState()
    val navHostState =
        rememberSwipeDismissableNavHostState(swipeToDismissBoxState = swipeToDismissBoxState)
    val navController = rememberSwipeDismissableNavController()

    var time by remember { mutableStateOf(LocalDateTime.now()) }

    AppScaffold {
        SwipeDismissableNavHost(
            startDestination = Screen.Menu.route,
            navController = navController,
            state = navHostState,
        ) {
            composable(
                route = Screen.Menu.route,
            ) {
                MenuScreen(
                    navigateToRoute = { route -> navController.navigate(route) },
                    time = time,
                )
            }
            composable(
                Screen.Network.route,
            ) {
                NetworkScreen()
            }
            composable(Screen.FillMaxRectangle.route) {
                FillMaxRectangleScreen()
            }
            composable(Screen.Volume.route) {
                VolumeScreen()
            }
            composable(Screen.ScrollAway.route) {
                val scrollState = rememberLazyListState()
                ScreenScaffold(scrollState = scrollState) {
                    ScrollScreenLazyColumn(
                        scrollState = scrollState,
                    )
                }
            }
            composable(
                Screen.ScrollAwaySLC.route,
            ) {
                ScrollAwayScreenScalingLazyColumn()
            }
            composable(
                Screen.ScrollAwayColumn.route,
            ) {
                val scrollState = rememberScrollState()
                ScreenScaffold(scrollState = scrollState) {
                    ScrollAwayScreenColumn(
                        scrollState = scrollState,
                    )
                }
            }
            composable(Screen.DatePicker.route) {
                DatePicker(
                    date = time.toLocalDate(),
                    onDateConfirm = {
                        time = time.toLocalTime().atDate(it)
                        navController.popBackStack()
                    },
                )
            }
            composable(Screen.FromDatePicker.route) {
                val date = time.toLocalDate()
                DatePicker(
                    date = date,
                    fromDate = date,
                    onDateConfirm = {
                        time = time.toLocalTime().atDate(it)
                        navController.popBackStack()
                    },
                )
            }
            composable(Screen.ToDatePicker.route) {
                val date = time.toLocalDate()
                DatePicker(
                    date = date,
                    toDate = date,
                    onDateConfirm = {
                        time = time.toLocalTime().atDate(it)
                        navController.popBackStack()
                    },
                )
            }
            composable(Screen.TimePicker.route) {
                TimePickerWith12HourClock(
                    time = time.toLocalTime(),
                    onTimeConfirm = {
                        time = time.toLocalDate().atTime(it)
                        navController.popBackStack()
                    },
                )
            }
            composable(Screen.TimeWithSecondsPicker.route) {
                TimePicker(
                    time = time.toLocalTime(),
                    onTimeConfirm = {
                        time = time.toLocalDate().atTime(it)
                        navController.popBackStack()
                    },
                )
            }
            composable(Screen.TimeWithoutSecondsPicker.route) {
                TimePicker(
                    time = time.toLocalTime(),
                    onTimeConfirm = {
                        time = time.toLocalDate().atTime(it)
                        navController.popBackStack()
                    },
                    showSeconds = false,
                )
            }
            composable(
                route = Screen.MaterialAlertDialog.route,
            ) {
                SampleAlertDialog()
            }
            composable(
                route = Screen.MaterialButtonsScreen.route,
            ) {
                SampleButtonScreen()
            }
            composable(
                route = Screen.MaterialCardsScreen.route,
            ) {
                SampleCardScreen()
            }
            composable(
                route = Screen.MaterialChipsScreen.route,
            ) {
                SampleChipScreen()
            }
            composable(
                route = Screen.MaterialChipIconWithProgressScreen.route,
            ) {
                SampleChipIconWithProgressScreen()
            }
            composable(
                route = Screen.MaterialCompactChipsScreen.route,
            ) {
                SampleCompactChipScreen()
            }
            composable(
                route = Screen.MaterialConfirmationScreen.route,
            ) {
                ScreenScaffold(timeText = {}) {
                    SampleConfirmationScreen()
                }
            }
            composable(
                route = Screen.MaterialConfirmationLauncher.route,
            ) {
                ScreenScaffold(timeText = {}) {
                    SampleConfirmationLauncher()
                }
            }
            composable(
                route = Screen.MaterialIconScreen.route,
            ) {
                SampleIconScreen()
            }
            composable(
                route = Screen.MaterialOutlinedChipScreen.route,
            ) {
                SampleOutlinedChipScreen()
            }
            composable(
                route = Screen.MaterialOutlinedCompactChipScreen.route,
            ) {
                SampleOutlinedCompactChipScreen()
            }
            composable(
                route = Screen.MaterialSplitToggleChipScreen.route,
            ) {
                SampleSplitToggleChipScreen()
            }
            composable(
                route = Screen.MaterialStepperScreen.route,
            ) {
                SampleStepperScreen()
            }
            composable(
                route = Screen.MaterialTitleScreen.route,
            ) {
                SampleTitleScreen()
            }
            composable(
                route = Screen.MaterialToggleButtonScreen.route,
            ) {
                SampleToggleButtonScreen()
            }
            composable(
                route = Screen.MaterialToggleChipScreen.route,
            ) {
                SampleToggleChipScreen()
            }
            composable(
                route = Screen.SectionedListMenuScreen.route,
            ) {
                SectionedListMenuScreen(
                    navigateToRoute = { route -> navController.navigate(route) },
                )
            }
            composable(
                Screen.SectionedListStatelessScreen.route,
            ) {
                SectionedListStatelessScreen()
            }
            composable(
                Screen.SectionedListStatefulScreen.route,
            ) {
                SectionedListStatefulScreen()
            }
            composable(
                Screen.SectionedListExpandableScreen.route,
            ) {
                SectionedListExpandableScreen()
            }
            composable(
                route = Screen.RotaryMenuScreen.route,
            ) {
                RotaryMenuScreen(
                    navigateToRoute = { route -> navController.navigate(route) },
                )
            }
            composable(route = Screen.RotaryScrollScreen.route) {
                ScreenScaffold(timeText = {}) {
                    RotaryScrollScreen()
                }
            }
            composable(route = Screen.RotaryScrollReversedScreen.route) {
                ScreenScaffold(timeText = {}) {
                    RotaryScrollScreen(reverseDirection = true)
                }
            }
            composable(route = Screen.RotaryScrollWithFlingScreen.route) {
                ScreenScaffold(timeText = {}) {
                    RotaryScrollWithFlingOrSnapScreen(RotaryMode.Scroll)
                }
            }
            composable(route = Screen.RotarySnapListScreen.route) {
                ScreenScaffold(timeText = {}) {
                    RotaryScrollWithFlingOrSnapScreen(RotaryMode.Snap)
                }
            }
            composable(
                route = Screen.Paging.route,
            ) {
                PagingScreen(navController = navController)
            }
            composable(
                route = Screen.PagingItem.route,
                arguments = listOf(
                    navArgument("id") {
                        type = NavType.IntType
                    },
                ),
            ) {
                PagingItemScreen(it.arguments!!.getInt("id"))
            }
            composable(route = Screen.PagerScreen.route) {
                SamplePagerScreen(swipeToDismissBoxState)
            }
            composable(route = Screen.VerticalPagerScreen.route) {
                SampleVerticalPagerScreen(swipeToDismissBoxState)
            }
        }
    }
}
