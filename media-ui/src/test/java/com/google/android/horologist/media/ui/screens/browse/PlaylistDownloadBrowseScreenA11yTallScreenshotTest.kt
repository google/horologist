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

@file:Suppress("ObjectLiteralToLambda")

package com.google.android.horologist.media.ui.screens.browse

import androidx.wear.compose.foundation.lazy.ScalingParams
import com.google.android.horologist.compose.layout.ScalingLazyColumnDefaults
import com.google.android.horologist.compose.layout.ScalingLazyColumnState
import com.google.android.horologist.compose.tools.a11y.forceState
import com.google.android.horologist.media.ui.PlayerLibraryPreview
import com.google.android.horologist.screenshots.ScreenshotTest
import org.junit.Test
import org.robolectric.annotation.Config

@Config(
    sdk = [30],
    qualifiers = "w227dp-h400dp-small-notlong-notround-watch-xhdpi-keyshidden-nonav"
)
class PlaylistDownloadBrowseScreenA11yTallScreenshotTest : ScreenshotTest() {

    @Test
    fun browseScreen() {
        val screenState = BrowseScreenState.Loaded(downloadList)

        takeScreenshot {
            val scalingParams =
                androidx.wear.compose.foundation.lazy.ScalingLazyColumnDefaults.scalingParams(
                    edgeScale = 1f,
                    edgeAlpha = 1f
                )
            val columnState: ScalingLazyColumnState = ScalingLazyColumnDefaults.belowTimeText()
                .create()
                .copy(scalingParams = scalingParams)
            columnState.state.forceState(0, 0)

            PlayerLibraryPreview(state = columnState.state, round = false) {
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

public fun ScalingLazyColumnState.copy(scalingParams: ScalingParams): ScalingLazyColumnState = ScalingLazyColumnState(
    initialScrollPosition,
    autoCentering,
    anchorType,
    contentPadding,
    rotaryMode,
    reverseLayout,
    verticalArrangement,
    horizontalAlignment,
    flingBehavior,
    userScrollEnabled,
    scalingParams
)
