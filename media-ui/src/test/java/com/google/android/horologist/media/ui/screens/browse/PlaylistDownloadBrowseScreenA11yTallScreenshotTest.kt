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
    ExperimentalHorologistMediaUiApi::class,
    ExperimentalHorologistPaparazziApi::class,
    ExperimentalHorologistComposeToolsApi::class
)

package com.google.android.horologist.media.ui.screens.browse

import app.cash.paparazzi.DeviceConfig
import com.android.ide.common.rendering.api.SessionParams
import com.google.android.horologist.compose.layout.ScalingLazyColumnConfigDefaults
import com.google.android.horologist.compose.tools.ExperimentalHorologistComposeToolsApi
import com.google.android.horologist.compose.tools.a11y.ComposeA11yExtension
import com.google.android.horologist.compose.tools.a11y.TallPreview
import com.google.android.horologist.compose.tools.a11y.forceState
import com.google.android.horologist.media.ui.ExperimentalHorologistMediaUiApi
import com.google.android.horologist.media.ui.PlayerLibraryPreview
import com.google.android.horologist.paparazzi.ExperimentalHorologistPaparazziApi
import com.google.android.horologist.paparazzi.RoundNonFullScreenDevice
import com.google.android.horologist.paparazzi.WearPaparazzi
import com.google.android.horologist.paparazzi.a11y.A11ySnapshotHandler
import com.google.android.horologist.paparazzi.determineHandler
import org.junit.Rule
import org.junit.Test

class PlaylistDownloadBrowseScreenA11yTallScreenshotTest {
    private val maxPercentDifference = 1.0

    private val composeA11yExtension = ComposeA11yExtension()

    @get:Rule
    val paparazzi = WearPaparazzi(
        deviceConfig = RoundNonFullScreenDevice,
        maxPercentDifference = maxPercentDifference,
        renderExtensions = setOf(composeA11yExtension),
        renderingMode = SessionParams.RenderingMode.V_SCROLL,
        snapshotHandler = A11ySnapshotHandler(
            delegate = determineHandler(
                maxPercentDifference = maxPercentDifference
            ),
            accessibilityStateFn = { composeA11yExtension.accessibilityState }
        )
    )

    @Test
    fun browseScreen() {
        val screenState = BrowseScreenState.Loaded(downloadList)

        paparazzi.snapshot {
            TallPreview(
                width = DeviceConfig.GALAXY_WATCH4_CLASSIC_LARGE.screenWidth,
                height = 650
            ) { scalingParams ->
                val config = ScalingLazyColumnConfigDefaults.rememberTopAlignedConfig()
                    .copy(scalingParams = scalingParams)
                config.state.forceState(0, 0)

                PlayerLibraryPreview(state = config.state, round = false) {
                    PlaylistDownloadBrowseScreen(
                        browseScreenState = screenState,
                        onDownloadItemClick = { },
                        onDownloadItemInProgressClick = { },
                        onPlaylistsClick = { },
                        onSettingsClick = { },
                        config = config,
                        onDownloadItemInProgressClickActionLabel = "cancel"
                    )
                }
            }
        }
    }
}
