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

package com.google.android.horologist.media.ui.material3.screens.browse

import com.google.android.horologist.media.ui.state.model.PlaylistDownloadUiModel
import com.google.android.horologist.media.ui.state.model.PlaylistUiModel
import com.google.android.horologist.screenshots.rng.WearLegacyA11yTest
import org.junit.Ignore
import org.junit.Test

@Ignore("Working through alpha changes")
class PlaylistDownloadBrowseScreenA11yScreenshotTest : WearLegacyA11yTest() {

    @Test
    fun browseScreen() {
        val screenState = BrowseScreenState.Loaded(downloadList)

        runScreenTest {
            PlaylistDownloadBrowseScreen(
                browseScreenState = screenState,
                onDownloadItemClick = { },
                onDownloadItemInProgressClick = { },
                onPlaylistsClick = { },
                onSettingsClick = { },
                onDownloadItemInProgressClickActionLabel = "cancel",
            )
        }
    }

    @Test
    fun secondPage() {
        val screenState = BrowseScreenState.Loaded(downloadList)

        composeRule.setContent {
            TestScaffold {
                PlaylistDownloadBrowseScreen(
                    browseScreenState = screenState,
                    onDownloadItemClick = { },
                    onDownloadItemInProgressClick = { },
                    onPlaylistsClick = { },
                    onSettingsClick = { },
                    onDownloadItemInProgressClickActionLabel = "cancel",
                )
            }
        }

        // TODO https://github.com/google/horologist/issues/2237
//        composeRule.onNode(hasScrollToNodeAction())
//            .performTouchInput { repeat(10) { swipeUp() } }
//
//        captureScreenshot()
    }
}

internal val downloadList = buildList {
    add(
        PlaylistDownloadUiModel.InProgress(
            PlaylistUiModel(
                id = "id",
                title = "Rock Classics",
                artworkUri = "https://www.example.com/album1.png",
            ),
            percentage = 15,
        ),
    )

    add(
        PlaylistDownloadUiModel.Completed(
            PlaylistUiModel(
                id = "id",
                title = "Pop Punk",
                artworkUri = "https://www.example.com/album2.png",
            ),
        ),
    )
}
