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

@file:OptIn(ExperimentalFoundationApi::class)

package com.google.android.horologist.sample

import androidx.compose.foundation.ExperimentalFoundationApi
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
import com.google.android.horologist.compose.layout.rememberColumnState
import com.google.android.horologist.materialcomponents.SampleAlertDialog
import com.google.android.horologist.materialcomponents.SampleButtonScreen
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
                val columnState = rememberColumnState()

                ScreenScaffold(scrollState = columnState) {
                    MenuScreen(
                        navigateToRoute = { route -> navController.navigate(route) },
                        time = time,
                        columnState = columnState,
                    )
                }
            }
            composable(
                Screen.Network.route,
            ) {
                val columnState = rememberColumnState()

                ScreenScaffold(scrollState = columnState) {
                    NetworkScreen(
                        columnState = columnState,
                    )
                }
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
                val columnState = rememberColumnState()

                ScreenScaffold(scrollState = columnState) {
                    ScrollAwayScreenScalingLazyColumn(
                        columnState = columnState,
                    )
                }
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
                ScreenScaffold(timeText = {}) {
                    SampleAlertDialog()
                }
            }
            composable(
                route = Screen.MaterialButtonsScreen.route,
            ) {
                val columnState = rememberColumnState()

                ScreenScaffold(scrollState = columnState) {
                    SampleButtonScreen(columnState = columnState)
                }
            }
            composable(
                route = Screen.MaterialChipsScreen.route,
            ) {
                val columnState = rememberColumnState()

                ScreenScaffold(scrollState = columnState) {
                    SampleChipScreen(columnState = columnState)
                }
            }
            composable(
                route = Screen.MaterialChipIconWithProgressScreen.route,
            ) {
                val columnState = rememberColumnState()

                ScreenScaffold(scrollState = columnState) {
                    SampleChipIconWithProgressScreen(columnState = columnState)
                }
            }
            composable(
                route = Screen.MaterialCompactChipsScreen.route,
            ) {
                val columnState = rememberColumnState()

                ScreenScaffold(scrollState = columnState) {
                    SampleCompactChipScreen(columnState = columnState)
                }
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
                val columnState = rememberColumnState()

                ScreenScaffold(scrollState = columnState) {
                    SampleIconScreen(columnState = columnState)
                }
            }
            composable(
                route = Screen.MaterialOutlinedChipScreen.route,
            ) {
                val columnState = rememberColumnState()

                ScreenScaffold(scrollState = columnState) {
                    SampleOutlinedChipScreen(columnState = columnState)
                }
            }
            composable(
                route = Screen.MaterialOutlinedCompactChipScreen.route,
            ) {
                val columnState = rememberColumnState()

                ScreenScaffold(scrollState = columnState) {
                    SampleOutlinedCompactChipScreen(columnState = columnState)
                }
            }
            composable(
                route = Screen.MaterialSplitToggleChipScreen.route,
            ) {
                val columnState = rememberColumnState()

                ScreenScaffold(scrollState = columnState) {
                    SampleSplitToggleChipScreen(columnState = columnState)
                }
            }
            composable(
                route = Screen.MaterialStepperScreen.route,
            ) {
                SampleStepperScreen()
            }
            composable(
                route = Screen.MaterialTitleScreen.route,
            ) {
                val columnState = rememberColumnState()

                ScreenScaffold(scrollState = columnState) {
                    SampleTitleScreen(columnState = columnState)
                }
            }
            composable(
                route = Screen.MaterialToggleButtonScreen.route,
            ) {
                val columnState = rememberColumnState()

                ScreenScaffold(scrollState = columnState) {
                    SampleToggleButtonScreen(columnState = columnState)
                }
            }
            composable(
                route = Screen.MaterialToggleChipScreen.route,
            ) {
                val columnState = rememberColumnState()

                ScreenScaffold(scrollState = columnState) {
                    SampleToggleChipScreen(columnState = columnState)
                }
            }
            composable(
                route = Screen.SectionedListMenuScreen.route,
            ) {
                val columnState = rememberColumnState()

                ScreenScaffold(scrollState = columnState) {
                    SectionedListMenuScreen(
                        navigateToRoute = { route -> navController.navigate(route) },
                        columnState = columnState,
                    )
                }
            }
            composable(
                Screen.SectionedListStatelessScreen.route,
            ) {
                val columnState = rememberColumnState()

                ScreenScaffold(scrollState = columnState) {
                    SectionedListStatelessScreen(
                        columnState = columnState,
                    )
                }
            }
            composable(
                Screen.SectionedListStatefulScreen.route,
            ) {
                val columnState = rememberColumnState()

                ScreenScaffold(scrollState = columnState) {
                    SectionedListStatefulScreen(
                        columnState = columnState,
                    )
                }
            }
            composable(
                Screen.SectionedListExpandableScreen.route,
            ) {
                val columnState = rememberColumnState()

                ScreenScaffold(scrollState = columnState) {
                    SectionedListExpandableScreen(
                        columnState = columnState,
                    )
                }
            }
            composable(
                route = Screen.RotaryMenuScreen.route,
            ) {
                val columnState = rememberColumnState()

                ScreenScaffold(scrollState = columnState) {
                    RotaryMenuScreen(
                        navigateToRoute = { route -> navController.navigate(route) },
                        columnState = columnState,
                    )
                }
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
                val columnState = rememberColumnState()

                ScreenScaffold(scrollState = columnState) {
                    PagingScreen(navController = navController, columnState = columnState)
                }
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
