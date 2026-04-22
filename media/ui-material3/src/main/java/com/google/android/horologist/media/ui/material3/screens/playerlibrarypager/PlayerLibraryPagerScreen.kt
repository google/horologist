/*
 * Copyright 2025 The Android Open Source Project
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

package com.google.android.horologist.media.ui.material3.screens.playerlibrarypager

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavBackStackEntry
import androidx.wear.compose.foundation.lazy.rememberTransformingLazyColumnState
import androidx.wear.compose.foundation.pager.HorizontalPager
import androidx.wear.compose.foundation.pager.PagerState
import androidx.wear.compose.material3.HorizontalPagerScaffold
import androidx.wear.compose.material3.ScreenScaffold
import com.google.android.horologist.audio.ui.VolumeUiState
import com.google.android.horologist.audio.ui.material3.VolumeLevelIndicator
import com.google.android.horologist.media.ui.material3.navigation.NavigationScreens
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
    val pageParam = NavigationScreens.Player.getPageParam(backStack)
    var pageApplied by rememberSaveable(backStack) { mutableStateOf(false) }

    LaunchedEffect(pageParam) {
        if (pageParam != null && !pageApplied) {
            try {
                pagerState.animateScrollToPage(pageParam)
            } catch (e: CancellationException) {
                // Not sure why we get a cancellation here, but we want the page
                // nav to take effect and persist
                pagerState.scrollToPage(pageParam)
            }
            pageApplied = true
        }
    }

    HorizontalPagerScaffold(
        modifier = modifier,
        pagerState = pagerState,
    ) {
        HorizontalPager(
            state = pagerState,
        ) { page ->
            when (page) {
                0 -> {
                    ScreenScaffold(
                        scrollIndicator = {
                            VolumeLevelIndicator(
                                volumeUiState = volumeUiState,
                                displayIndicatorEvents = displayVolumeIndicatorEvents,
                            )
                        },
                    ) {
                        playerScreen()
                    }
                }

                1 -> {
                    ScreenScaffold(
                        scrollState = rememberTransformingLazyColumnState(),
                    ) {
                        libraryScreen()
                    }
                }
            }
        }
    }
}
