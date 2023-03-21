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
    ExperimentalHorologistApi::class,
    ExperimentalHorologistApi::class,
    ExperimentalHorologistApi::class
)

package com.google.android.horologist.media.ui.screens.browse

import com.google.android.horologist.annotations.ExperimentalHorologistApi
import com.google.android.horologist.compose.tools.a11y.ComposeA11yExtension
import com.google.android.horologist.compose.tools.coil.FakeImageLoader
import com.google.android.horologist.annotations.ExperimentalHorologistApi
import com.google.android.horologist.media.ui.PlayerLibraryPreview
import com.google.android.horologist.media.ui.components.positionedState
import com.google.android.horologist.media.ui.state.model.PlaylistDownloadUiModel
import com.google.android.horologist.media.ui.state.model.PlaylistUiModel
import com.google.android.horologist.annotations.ExperimentalHorologistApi
import com.google.android.horologist.paparazzi.RoundNonFullScreenDevice
import com.google.android.horologist.paparazzi.WearPaparazzi
import com.google.android.horologist.paparazzi.WearSnapshotHandler
import com.google.android.horologist.paparazzi.a11y.A11ySnapshotHandler
import com.google.android.horologist.paparazzi.determineHandler
import org.junit.Rule
import org.junit.Test

class PlaylistDownloadBrowseScreenA11yScreenshotTest {
    private val maxPercentDifference = 1.0

    private val composeA11yExtension = ComposeA11yExtension()

    @get:Rule
    val paparazzi = WearPaparazzi(
        deviceConfig = RoundNonFullScreenDevice,
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
        val screenState = BrowseScreenState.Loaded(downloadList)

        paparazzi.snapshot {
            val columnState = positionedState(0, -40)

            PlayerLibraryPreview(state = columnState.state) {
                PlaylistDownloadBrowseScreen(
                    browseScreenState = screenState,
                    onDownloadItemClick = { },
                    onDownloadItemInProgressClick = { },
                    onPlaylistsClick = { },
                    onSettingsClick = { },
                    columnState = columnState,
                    onDownloadItemInProgressClickActionLabel = "cancel"
                )
            }
        }
    }

    @Test
    fun secondPage() {
        FakeImageLoader.NotFound.override {
            val screenState = BrowseScreenState.Loaded(downloadList)

            paparazzi.snapshot {
                val columnState = positionedState(4, 0)
                PlayerLibraryPreview(state = columnState.state) {
                    PlaylistDownloadBrowseScreen(
                        browseScreenState = screenState,
                        onDownloadItemClick = { },
                        onDownloadItemInProgressClick = { },
                        onPlaylistsClick = { },
                        onSettingsClick = { },
                        columnState = columnState,
                        onDownloadItemInProgressClickActionLabel = "cancel"
                    )
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
