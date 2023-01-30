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

package com.google.android.horologist.media.ui.components.actions

import com.google.android.horologist.compose.tools.ExperimentalHorologistComposeToolsApi
import com.google.android.horologist.compose.tools.coil.FakeImageLoader
import com.google.android.horologist.compose.tools.snapshotInABox
import com.google.android.horologist.media.ui.ExperimentalHorologistMediaUiApi
import com.google.android.horologist.media.ui.R
import com.google.android.horologist.paparazzi.ExperimentalHorologistPaparazziApi
import com.google.android.horologist.paparazzi.WearPaparazzi
import org.junit.Rule
import org.junit.Test

class ShowPlaylistChipTest {

    @get:Rule
    val paparazzi = WearPaparazzi(
        maxPercentDifference = 0.3
    )

    @Test
    fun givenArtwork_thenDisplaysArtwork() {
        paparazzi.snapshotInABox {
            FakeImageLoader.Resources.override {
                ShowPlaylistChip(
                    artworkUri = R.drawable.ic_uamp,
                    name = "Playlists",
                    onClick = {}
                )
            }
        }
    }

    @Test
    fun givenNOArtwork_thenDoesNOTDisplayArtwork() {
        paparazzi.snapshotInABox {
            FakeImageLoader.Resources.override {
                ShowPlaylistChip(
                    artworkUri = null,
                    name = "Playlists",
                    onClick = {}
                )
            }
        }
    }

    @Test
    fun givenNOName_thenDoesDisplayArtwork() {
        paparazzi.snapshotInABox {
            FakeImageLoader.Resources.override {
                ShowPlaylistChip(
                    artworkUri = R.drawable.ic_uamp,
                    name = null,
                    onClick = {}
                )
            }
        }
    }

    @Test
    fun givenVeryLongTitle_thenEllipsizeAt2ndLine() {
        paparazzi.snapshotInABox {
            FakeImageLoader.Resources.override {
                ShowPlaylistChip(
                    artworkUri = R.drawable.ic_uamp,
                    name = "Very very very very very very very very very very very very very very very very very very very long title",
                    onClick = {}
                )
            }
        }
    }
}
