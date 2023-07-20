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

package com.google.android.horologist.media.ui.screens.browse

import com.google.android.horologist.compose.tools.coil.FakeImageLoader
import com.google.android.horologist.media.ui.PlayerLibraryPreview
import com.google.android.horologist.media.ui.components.positionedState
import com.google.android.horologist.media.ui.state.model.PlaylistDownloadUiModel
import com.google.android.horologist.media.ui.state.model.PlaylistUiModel
import com.google.android.horologist.screenshots.ScreenshotBaseTest
import com.google.android.horologist.screenshots.ScreenshotTestRule.Companion.screenshotTestRuleParams
import org.junit.Test

class PlaylistDownloadBrowseScreenA11yScreenshotTest : ScreenshotBaseTest(
    screenshotTestRuleParams {
        enableA11y = true
    }
) {

    @Test
    fun browseScreen() {
        val screenState = BrowseScreenState.Loaded(downloadList)

        screenshotTestRule.setContent(takeScreenshot = true) {
            val columnState = positionedState(0, -40)

            PlayerLibraryPreview(columnState = columnState) {
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

            screenshotTestRule.setContent(takeScreenshot = true) {
                val columnState = positionedState(4, 0)
                PlayerLibraryPreview(columnState = columnState) {
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
