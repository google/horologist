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
    ExperimentalHorologistApi::class
)
@file:Suppress("ObjectLiteralToLambda")

package com.google.android.horologist.media.ui.screens.browse

import androidx.compose.runtime.Composable
import androidx.wear.compose.foundation.lazy.ScalingParams
import app.cash.paparazzi.DeviceConfig
import com.google.android.horologist.annotations.ExperimentalHorologistApi
import com.google.android.horologist.compose.layout.ScalingLazyColumnDefaults
import com.google.android.horologist.compose.layout.ScalingLazyColumnState
import com.google.android.horologist.compose.tools.a11y.TallPreview
import com.google.android.horologist.compose.tools.a11y.forceState
import com.google.android.horologist.media.ui.PlayerLibraryPreview
import com.google.android.horologist.screenshots.ScreenshotTest
import org.junit.Ignore
import org.junit.Test

class PlaylistDownloadBrowseScreenA11yTallScreenshotTest: ScreenshotTest() {

    @Test
    @Ignore("Need to update post other failures")
    fun browseScreen() {
        val screenState = BrowseScreenState.Loaded(downloadList)

        takeScreenshot {
            TallPreview(
                width = DeviceConfig.GALAXY_WATCH4_CLASSIC_LARGE.screenWidth,
                height = 650
            ) { scalingParams ->
                val columnState = ScalingLazyColumnDefaults.belowTimeText()
                    .copy(scalingParams = scalingParams).create()
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

public fun ScalingLazyColumnState.Factory.copy(scalingParams: ScalingParams): ScalingLazyColumnState.Factory {
    return object : ScalingLazyColumnState.Factory {
        @Composable
        override fun create(): ScalingLazyColumnState =
            this@copy.create().copy(scalingParams = scalingParams)
    }
}
