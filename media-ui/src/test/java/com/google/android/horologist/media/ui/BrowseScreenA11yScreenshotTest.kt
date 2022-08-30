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

@file:OptIn(
    ExperimentalHorologistMediaUiApi::class, ExperimentalHorologistPaparazziApi::class,
    ExperimentalHorologistComposeToolsApi::class
)

package com.google.android.horologist.media.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Color
import androidx.wear.compose.material.PositionIndicator
import androidx.wear.compose.material.Scaffold
import androidx.wear.compose.material.ScalingLazyListState
import androidx.wear.compose.material.TimeSource
import androidx.wear.compose.material.TimeText
import app.cash.paparazzi.Paparazzi
import com.google.android.horologist.compose.pager.PagerScreen
import com.google.android.horologist.compose.tools.ExperimentalHorologistComposeToolsApi
import com.google.android.horologist.compose.tools.RoundPreview
import com.google.android.horologist.compose.tools.a11y.ComposeA11yExtension
import com.google.android.horologist.media.ui.compose.forceState
import com.google.android.horologist.media.ui.screens.browse.BrowseScreen
import com.google.android.horologist.media.ui.screens.browse.BrowseScreenState
import com.google.android.horologist.media.ui.state.model.PlaylistDownloadUiModel
import com.google.android.horologist.media.ui.state.model.PlaylistUiModel
import com.google.android.horologist.paparazzi.ExperimentalHorologistPaparazziApi
import com.google.android.horologist.paparazzi.GALAXY_WATCH4_CLASSIC_LARGE
import com.google.android.horologist.paparazzi.WearSnapshotHandler
import com.google.android.horologist.paparazzi.a11y.A11ySnapshotHandler
import com.google.android.horologist.paparazzi.determineHandler
import org.junit.Rule
import org.junit.Test

class BrowseScreenA11yScreenshotTest {
    private val maxPercentDifference = 0.1

    val composeA11yExtension = ComposeA11yExtension()

    @get:Rule
    val paparazzi = Paparazzi(
        deviceConfig = GALAXY_WATCH4_CLASSIC_LARGE,
        theme = "android:ThemeOverlay.Material.Dark",
        maxPercentDifference = maxPercentDifference,
        renderExtensions = setOf(composeA11yExtension),
        snapshotHandler = WearSnapshotHandler(
            A11ySnapshotHandler(
                delegate = determineHandler(
                    maxPercentDifference = maxPercentDifference
                ),
                accessibilityStateFn = { composeA11yExtension.accessibilityState }
            )
        )
    )

    @Test
    fun browseScreen() {
        val scrollState = ScalingLazyListState()
        scrollState.forceState(0, -40)

        val screenState = BrowseScreenState.Loaded(downloadList)

        paparazzi.snapshot {
            PlayerPreview(state = scrollState) {
                BrowseScreen(
                    browseScreenState = screenState,
                    onDownloadItemClick = { },
                    onPlaylistsClick = { },
                    onSettingsClick = { },
                    focusRequester = FocusRequester(),
                    scalingLazyListState = scrollState
                )
            }
        }
    }

    @Test
    fun secondPage() {
        val scrollState = ScalingLazyListState()
        scrollState.forceState(4, 0)

        val screenState = BrowseScreenState.Loaded(downloadList)

        paparazzi.snapshot {
            PlayerPreview(state = scrollState) {
                BrowseScreen(
                    browseScreenState = screenState,
                    onDownloadItemClick = { },
                    onPlaylistsClick = { },
                    onSettingsClick = { },
                    focusRequester = FocusRequester(),
                    scalingLazyListState = scrollState
                )
            }
        }
    }
}

@Composable
fun PlayerPreview(
    state: ScalingLazyListState? = null,
    round: Boolean = true,
    function: @Composable () -> Unit
) {
    RoundPreview(round = round) {
        PagerScreen(count = 2) {
            if (it == 0) {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    timeText = {
                        TimeText(
                            timeSource = object : TimeSource {
                                override val currentTime: String
                                    @Composable get() = "10:10"
                            }
                        )
                    },
                    positionIndicator = {
                        if (state != null) {
                            PositionIndicator(state)
                        }
                    }
                ) {
                    Box(modifier = Modifier.background(Color.Black)) {
                        function()
                    }
                }
            }
        }
    }
}

internal val downloadList = buildList {
    add(
        PlaylistDownloadUiModel.InProgress(
            PlaylistUiModel(
                id = "id",
                title = "Rock Classics",
                artworkUri = "https://www.example.com/album1.png"
            ),
            percentage = 15
        )
    )

    add(
        PlaylistDownloadUiModel.Completed(
            PlaylistUiModel(
                id = "id",
                title = "Pop Punk",
                artworkUri = "https://www.example.com/album2.png"
            )
        )
    )
}
