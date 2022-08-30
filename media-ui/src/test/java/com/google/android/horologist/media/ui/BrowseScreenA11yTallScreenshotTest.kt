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

@file:OptIn(ExperimentalHorologistMediaUiApi::class, ExperimentalHorologistPaparazziApi::class,
    ExperimentalHorologistComposeToolsApi::class
)

package com.google.android.horologist.media.ui

import androidx.compose.ui.focus.FocusRequester
import androidx.wear.compose.material.ScalingLazyListState
import app.cash.paparazzi.Paparazzi
import com.android.ide.common.rendering.api.SessionParams
import com.google.android.horologist.compose.tools.ExperimentalHorologistComposeToolsApi
import com.google.android.horologist.compose.tools.a11y.ComposeA11yExtension
import com.google.android.horologist.media.ui.compose.TallPreview
import com.google.android.horologist.media.ui.compose.forceState
import com.google.android.horologist.media.ui.screens.browse.BrowseScreen
import com.google.android.horologist.media.ui.screens.browse.BrowseScreenState
import com.google.android.horologist.paparazzi.ExperimentalHorologistPaparazziApi
import com.google.android.horologist.paparazzi.GALAXY_WATCH4_CLASSIC_LARGE
import com.google.android.horologist.paparazzi.a11y.A11ySnapshotHandler
import com.google.android.horologist.paparazzi.determineHandler
import org.junit.Rule
import org.junit.Test

class BrowseScreenA11yTallScreenshotTest {
    private val maxPercentDifference = 0.1

    val composeA11yExtension = ComposeA11yExtension()

    @get:Rule
    val paparazzi = Paparazzi(
        deviceConfig = GALAXY_WATCH4_CLASSIC_LARGE,
        theme = "android:ThemeOverlay.Material.Dark",
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
        val scrollState = ScalingLazyListState()
        scrollState.forceState(0, 0)

        val screenState = BrowseScreenState.Loaded(downloadList)

        paparazzi.snapshot {
            PlayerPreview(state = scrollState) {
                TallPreview(
                    width = GALAXY_WATCH4_CLASSIC_LARGE.screenWidth,
                    height = 650
                ) { scalingParams ->
                    BrowseScreen(
                        browseScreenState = screenState,
                        onDownloadItemClick = { },
                        onPlaylistsClick = { },
                        onSettingsClick = { },
                        focusRequester = FocusRequester(),
                        scalingLazyListState = scrollState,
                        scalingParams = scalingParams
                    )
                }
            }
        }
    }
}
