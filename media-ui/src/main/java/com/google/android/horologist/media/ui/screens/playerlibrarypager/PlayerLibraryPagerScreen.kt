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

@file:OptIn(ExperimentalPagerApi::class)

package com.google.android.horologist.media.ui.screens.playerlibrarypager

import androidx.compose.foundation.focusable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.unit.dp
import androidx.navigation.NavBackStackEntry
import androidx.wear.compose.material.PositionIndicator
import androidx.wear.compose.material.Scaffold
import androidx.wear.compose.material.scrollAway
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.PagerState
import com.google.android.horologist.audio.VolumeState
import com.google.android.horologist.audio.ui.VolumePositionIndicator
import com.google.android.horologist.compose.focus.rememberActiveFocusRequester
import com.google.android.horologist.compose.layout.ScalingLazyColumnConfig
import com.google.android.horologist.compose.layout.ScalingLazyColumnConfigDefaults
import com.google.android.horologist.compose.navscaffold.ExperimentalHorologistComposeLayoutApi
import com.google.android.horologist.compose.pager.PagerScreen
import com.google.android.horologist.compose.rotaryinput.onRotaryInputAccumulated
import com.google.android.horologist.media.ui.navigation.NavigationScreens
import java.util.concurrent.CancellationException

/**
 * A HorizontalPager with a player screen, using volume control on the left,
 * and library screen with column scrolling on the right.
 */
@OptIn(ExperimentalHorologistComposeLayoutApi::class)
@Composable
public fun PlayerLibraryPagerScreen(
    pagerState: PagerState,
    onVolumeChangeByScroll: (scrollPixels: Float) -> Unit,
    volumeState: () -> VolumeState,
    timeText: @Composable (Modifier) -> Unit,
    playerScreen: @Composable () -> Unit,
    libraryScreen: @Composable (ScalingLazyColumnConfig) -> Unit,
    backStack: NavBackStackEntry,
    modifier: Modifier = Modifier
) {
    val pageParam = NavigationScreens.Player.getPageParam(backStack, remove = true)

    LaunchedEffect(pageParam) {
        if (pageParam != null) {
            try {
                pagerState.animateScrollToPage(pageParam)
            } catch (e: CancellationException) {
                // Not sure why we get a cancellation here, but we want the page
                // nav to take effect and persist
                pagerState.scrollToPage(pageParam)
            }
        }
    }

    PagerScreen(
        modifier = modifier,
        count = 2,
        state = pagerState
    ) { page ->
        when (page) {
            0 -> {
                val focusRequester =
                    rememberActiveFocusRequester()

                Scaffold(
                    modifier = Modifier
                        .onRotaryInputAccumulated(onVolumeChangeByScroll)
                        .focusRequester(focusRequester)
                        .focusable(),
                    timeText = {
                        timeText(Modifier)
                    },
                    positionIndicator = {
                        VolumePositionIndicator(volumeState = volumeState)
                    }
                ) {
                    playerScreen()
                }
            }

            1 -> {
                val config = ScalingLazyColumnConfigDefaults.rememberTopAlignedConfig()
                Scaffold(
                    timeText = {
                        timeText(Modifier.scrollAway(config.state, 1, 0.dp))
                    },
                    positionIndicator = {
                        PositionIndicator(
                            scalingLazyListState = config.state
                        )
                    }
                ) {
                    libraryScreen(config)
                }
            }
        }
    }
}
