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

@file:OptIn(ExperimentalHorologistComposeToolsApi::class, ExperimentalHorologistMediaUiApi::class)

package com.google.android.horologist.media.ui.tiles

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.wear.tiles.ActionBuilders
import app.cash.paparazzi.DeviceConfig
import app.cash.paparazzi.Paparazzi
import com.google.android.horologist.compose.tools.ExperimentalHorologistComposeToolsApi
import com.google.android.horologist.compose.tools.RoundPreview
import com.google.android.horologist.compose.tools.TileLayoutPreview
import com.google.android.horologist.media.ui.ExperimentalHorologistMediaUiApi
import com.google.android.horologist.media.ui.R
import com.google.android.horologist.media.ui.uamp.UampColors
import com.google.android.horologist.paparazzi.GALAXY_WATCH4_CLASSIC_LARGE
import com.google.android.horologist.paparazzi.WEAR_OS_SMALL_ROUND
import com.google.android.horologist.paparazzi.WEAR_OS_SQUARE
import com.google.android.horologist.paparazzi.WearSnapshotHandler
import com.google.android.horologist.paparazzi.determineHandler
import com.google.android.horologist.tiles.images.drawableResToImageResource
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized

@RunWith(Parameterized::class)
class MediaCollectionsTileTest(
    private val deviceConfig: DeviceConfig
) {
    @get:Rule
    val paparazzi = Paparazzi(
        deviceConfig = deviceConfig,
        theme = "android:ThemeOverlay.Material.Dark",
        maxPercentDifference = 0.1,
        snapshotHandler = if (deviceConfig == WEAR_OS_SQUARE) {
            determineHandler(0.1)
        } else {
            WearSnapshotHandler(determineHandler(0.1))
        }
    )

    val name = when (deviceConfig) {
        WEAR_OS_SQUARE -> "square"
        WEAR_OS_SMALL_ROUND -> "small_round"
        GALAXY_WATCH4_CLASSIC_LARGE -> "large_round"
        else -> "unknown"
    }

    @Test
    fun mediaCollectionsTile() {
        paparazzi.snapshot(name = name) {
            RoundPreview(round = deviceConfig != WEAR_OS_SQUARE) {
                Box(modifier = Modifier.background(Color.Black)) {
                    SampleTilePreview()
                }
            }
        }
    }

    @Composable
    fun SampleTilePreview() {
        val context = LocalContext.current

        val action = ActionBuilders.LaunchAction.Builder()
            .build()

        val tileState = remember {
            MediaCollectionsTileRenderer.MediaCollectionsState(
                chipName = R.string.horologist_sample_playlists_name,
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
                appIcon = R.drawable.ic_uamp,
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

    companion object {
        @JvmStatic
        @Parameterized.Parameters
        fun devices() = listOf(WEAR_OS_SQUARE, WEAR_OS_SMALL_ROUND, GALAXY_WATCH4_CLASSIC_LARGE)
    }
}
