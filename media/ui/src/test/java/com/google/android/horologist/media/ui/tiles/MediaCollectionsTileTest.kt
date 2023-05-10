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

package com.google.android.horologist.media.ui.tiles

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.wear.protolayout.ActionBuilders
import com.google.android.horologist.compose.tools.TileLayoutPreview
import com.google.android.horologist.media.ui.R
import com.google.android.horologist.media.ui.uamp.UampColors
import com.google.android.horologist.screenshots.ScreenshotBaseTest
import com.google.android.horologist.screenshots.ScreenshotTestRule
import com.google.android.horologist.tiles.images.drawableResToImageResource
import org.junit.Test
import org.robolectric.annotation.Config

class MediaCollectionsTileTest : ScreenshotBaseTest(
    ScreenshotTestRule.screenshotTestRuleParams {
        screenTimeText = {}
    }
) {
    @Test
    fun largeRound() {
        tileScreenshot()
    }

    @Config(
        sdk = [30],
        qualifiers = "+w192dp-h192dp"
    )
    @Test
    fun smallRound() {
        tileScreenshot()
    }

    @Config(
        sdk = [30],
        qualifiers = "w192dp-h192dp-small-notlong-round-watch-hdpi-keyshidden-nonav"
    )
    @Test
    fun square() {
        tileScreenshot()
    }

    fun tileScreenshot() {
        screenshotTestRule.setContent(takeScreenshot = true) {
            SampleTilePreview()
        }
    }

    @Composable
    fun SampleTilePreview() {
        val context = LocalContext.current

        val action = ActionBuilders.LaunchAction.Builder()
            .build()

        val tileState = remember {
            MediaCollectionsTileRenderer.MediaCollectionsState(
                chipName = R.string.sample_playlists_name,
                chipAction = action,
                collection1 = MediaCollectionsTileRenderer.MediaCollection(
                    name = "Liked Songs",
                    artworkId = "1",
                    action = action
                ),
                collection2 = MediaCollectionsTileRenderer.MediaCollection(
                    name = "Podcasts",
                    artworkId = "2",
                    action = action
                )
            )
        }

        val resourceState = remember {
            MediaCollectionsTileRenderer.ResourceState(
                appIcon = com.google.android.horologist.logo.R.drawable.ic_stat_horologist,
                images = mapOf(
                    "1" to drawableResToImageResource(R.drawable.ic_baseline_queue_music_24),
                    "2" to drawableResToImageResource(R.drawable.ic_baseline_podcasts_24)
                )
            )
        }

        val renderer = remember {
            MediaCollectionsTileRenderer(
                context = context,
                materialTheme = UampColors.toTileColors(),
                debugResourceMode = false
            )
        }

        TileLayoutPreview(
            tileState,
            resourceState,
            renderer
        )
    }
}
