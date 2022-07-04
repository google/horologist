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

@file:OptIn(ExperimentalHorologistMediaUiApi::class)

package com.google.android.horologist.media.ui.screens

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FeaturedPlayList
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Color
import androidx.wear.compose.material.rememberScalingLazyListState
import app.cash.paparazzi.Paparazzi
import com.google.android.horologist.media.ui.ExperimentalHorologistMediaUiApi
import com.google.android.horologist.media.ui.screens.browse.BrowseScreen
import com.google.android.horologist.media.ui.screens.browse.BrowseScreenState
import com.google.android.horologist.media.ui.state.model.DownloadPlaylistUiModel
import com.google.android.horologist.media.ui.utils.rememberVectorPainter
import com.google.android.horologist.paparazzi.GALAXY_WATCH4_CLASSIC_LARGE
import com.google.android.horologist.paparazzi.WearSnapshotHandler
import com.google.android.horologist.paparazzi.determineHandler
import org.junit.Ignore
import org.junit.Rule
import org.junit.Test

@Ignore("Can't generate golden images - recording task is generating empty ones")
class BrowseScreenTest {

    @get:Rule
    val paparazzi = Paparazzi(
        deviceConfig = GALAXY_WATCH4_CLASSIC_LARGE,
        theme = "android:ThemeOverlay.Material.Dark",
        maxPercentDifference = 0.0,
        snapshotHandler = WearSnapshotHandler(determineHandler(0.1))
    )

    @Test
    fun default() {
        paparazzi.snapshot {
            BrowseScreen(
                browseScreenState = BrowseScreenState.Loaded(downloadList),
                onDownloadItemClick = { },
                onPlaylistsClick = { },
                onSettingsClick = { },
                focusRequester = FocusRequester(),
                scalingLazyListState = rememberScalingLazyListState(),
                downloadItemArtworkPlaceholder = rememberVectorPainter(
                    image = Icons.Default.FeaturedPlayList,
                    tintColor = Color.Green,
                )
            )
        }
    }

    @Test
    fun noDownloads() {
        paparazzi.snapshot {
            BrowseScreen(
                browseScreenState = BrowseScreenState.Loaded(emptyList()),
                onDownloadItemClick = { },
                onPlaylistsClick = { },
                onSettingsClick = { },
                focusRequester = FocusRequester(),
                scalingLazyListState = rememberScalingLazyListState(),
            )
        }
    }

    @Test
    fun loading() {
        paparazzi.snapshot {
            BrowseScreen(
                browseScreenState = BrowseScreenState.Loading,
                onDownloadItemClick = { },
                onPlaylistsClick = { },
                onSettingsClick = { },
                focusRequester = FocusRequester(),
                scalingLazyListState = rememberScalingLazyListState(),
            )
        }
    }

    companion object {

        private val downloadList = buildList {
            add(
                DownloadPlaylistUiModel.InProgress(
                    title = "Rock Classics",
                    artworkUri = "https://www.example.com/album1.png",
                    percentage = 15,
                )
            )

            add(
                DownloadPlaylistUiModel.Completed(
                    title = "Pop Punk",
                    artworkUri = "https://www.example.com/album2.png"
                )
            )
        }
    }
}
