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

@file:OptIn(ExperimentalFoundationApi::class, ExperimentalCoilApi::class)

package com.google.android.horologist.screensizes

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.wear.compose.material.TimeText
import coil.annotation.ExperimentalCoilApi
import coil.test.FakeImageLoaderEngine
import com.google.android.horologist.compose.layout.AppScaffold
import com.google.android.horologist.compose.pager.PagerScreen
import com.google.android.horologist.compose.tools.Device
import com.google.android.horologist.images.base.util.rememberVectorPainter
import com.google.android.horologist.images.coil.FakeImageLoader.Companion.TestIconResourceUri
import com.google.android.horologist.media.ui.screens.entity.PlaylistDownloadScreen
import com.google.android.horologist.media.ui.screens.entity.createPlaylistDownloadScreenStateLoaded
import com.google.android.horologist.media.ui.state.model.DownloadMediaUiModel
import com.google.android.horologist.media.ui.state.model.PlaylistUiModel
import com.google.android.horologist.screenshots.FixedTimeSource
import kotlinx.coroutines.awaitCancellation

class MediaPlayerLibraryTest(device: Device) :
    WearLegacyScreenSizeTest(device = device, showTimeText = false) {

        override val imageLoader = FakeImageLoaderEngine.Builder()
            .intercept(
                predicate = {
                    it == TestIconResourceUri
                },
                interceptor = {
                    awaitCancellation()
                },
            )
            .build()

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
                    artworkUri = TestIconResourceUri,
                ),
                DownloadMediaUiModel.NotDownloaded(
                    id = "id 2",
                    title = "Song name 2",
                    artist = "Artist name 2",
                    artworkUri = TestIconResourceUri,
                ),
            )

            AppScaffold(
                timeText = {
                    TimeText(
                        timeSource = FixedTimeSource,
                    )
                },
            ) {
                PagerScreen(
                    state = rememberPagerState(1) {
                        2
                    },
                ) {
                    if (it == 1) {
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
