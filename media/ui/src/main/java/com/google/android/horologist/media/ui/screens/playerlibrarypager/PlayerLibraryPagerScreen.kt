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

package com.google.android.horologist.media.ui.screens.playerlibrarypager

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.pager.PagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavBackStackEntry
import androidx.navigation.toRoute
import com.google.android.horologist.audio.ui.VolumePositionIndicator
import com.google.android.horologist.audio.ui.VolumeUiState
import com.google.android.horologist.compose.layout.ScreenScaffold
import com.google.android.horologist.compose.layout.rememberResponsiveColumnState
import com.google.android.horologist.compose.pager.PagerScreen
import com.google.android.horologist.media.ui.navigation.NavigationScreen
import kotlinx.coroutines.flow.Flow
import java.util.concurrent.CancellationException

/**
 * A HorizontalPager with a player screen, using volume control on the left,
 * and library screen with column scrolling on the right.
 */
@Composable
public fun PlayerLibraryPagerScreen(
    pagerState: PagerState,
    volumeUiState: () -> VolumeUiState,
    displayVolumeIndicatorEvents: Flow<Unit>,
    playerScreen: @Composable () -> Unit,
    libraryScreen: @Composable () -> Unit,
    backStack: NavBackStackEntry,
    modifier: Modifier = Modifier,
) {
    val route = backStack.toRoute<NavigationScreen.Player>()
    var pageApplied by rememberSaveable(backStack) { mutableStateOf(false) }

    LaunchedEffect(route.page) {
        if (route.page != -1 && !pageApplied) {
            try {
                pagerState.animateScrollToPage(route.page)
            } catch (e: CancellationException) {
                // Not sure why we get a cancellation here, but we want the page
                // nav to take effect and persist
                pagerState.scrollToPage(route.page)
            }
            pageApplied = true
        }
    }

    PagerScreen(
        modifier = modifier.background(Color.Transparent),
        state = pagerState,
    ) { page ->
        when (page) {
            0 -> {
                ScreenScaffold(
                    positionIndicator = {
                        VolumePositionIndicator(volumeUiState = volumeUiState, displayIndicatorEvents = displayVolumeIndicatorEvents)
                    },
                ) {
                    playerScreen()
                }
            }

            1 -> {
                val config = rememberResponsiveColumnState()
                ScreenScaffold(
                    scrollState = config,
                ) {
                    libraryScreen()
                }
            }
        }
    }
}
