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

package com.google.android.horologist.screensizes

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.wear.compose.material.PositionIndicator
import androidx.wear.compose.material.Scaffold
import androidx.wear.compose.material.TimeText
import com.google.android.horologist.compose.layout.ScalingLazyColumnDefaults
import com.google.android.horologist.compose.layout.scrollAway
import com.google.android.horologist.compose.material.util.rememberVectorPainter
import com.google.android.horologist.compose.pager.PagerScreen
import com.google.android.horologist.compose.tools.Device
import com.google.android.horologist.media.ui.screens.entity.PlaylistDownloadScreen
import com.google.android.horologist.media.ui.screens.entity.createPlaylistDownloadScreenStateLoaded
import com.google.android.horologist.media.ui.state.model.DownloadMediaUiModel
import com.google.android.horologist.media.ui.state.model.PlaylistUiModel
import com.google.android.horologist.screenshots.FixedTimeSource

class MediaPlayerLibraryTest(device: Device) : ScreenSizeTest(device = device, showTimeText = true) {

    @Composable
    override fun Content() {

        val playlistUiModel = PlaylistUiModel(
            id = "id",
            title = "Playlist name",
        )

        val notDownloaded = listOf(
            DownloadMediaUiModel.NotDownloaded(
                id = "id",
                title = "Song name",
                artist = "Artist name",
                artworkUri = "artworkUri",
            ),
            DownloadMediaUiModel.NotDownloaded(
                id = "id 2",
                title = "Song name 2",
                artist = "Artist name 2",
                artworkUri = "artworkUri",
            ),
        )

        val columnState = ScalingLazyColumnDefaults.belowTimeText().create()
        PagerScreen(
            state = rememberPagerState(1) {
                2
            },
        ) {
            if (it == 1) {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    timeText = {
                        TimeText(
                            modifier = Modifier.scrollAway(scalingLazyColumnState = columnState),
                            timeSource = FixedTimeSource,
                        )
                    },
                    positionIndicator = {
                        PositionIndicator(columnState.state)
                    },
                ) {
                    Box(modifier = Modifier.background(Color.Black)) {
                        PlaylistDownloadScreen(
                            playlistName = "Playlist name",
                            playlistDownloadScreenState = createPlaylistDownloadScreenStateLoaded(
                                playlistModel = playlistUiModel,
                                downloadMediaList = notDownloaded,
                            ),
                            onDownloadButtonClick = { },
                            onCancelDownloadButtonClick = { },
                            onDownloadItemClick = { },
                            onDownloadItemInProgressClick = { },
                            onShuffleButtonClick = { },
                            onPlayButtonClick = { },
                            columnState = columnState,
                            downloadItemArtworkPlaceholder = rememberVectorPainter(
                                image = Icons.Default.MusicNote,
                                tintColor = Color.Blue,
                            ),
                            onDownloadItemInProgressClickActionLabel = "cancel",
                        )
                    }
                }
            }
        }
    }

}


