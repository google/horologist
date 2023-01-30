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

package com.google.android.horologist.media.ui.components

import androidx.compose.foundation.layout.height
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.android.horologist.compose.tools.ExperimentalHorologistComposeToolsApi
import com.google.android.horologist.compose.tools.coil.FakeImageLoader
import com.google.android.horologist.compose.tools.snapshotInABox
import com.google.android.horologist.media.ui.ExperimentalHorologistMediaUiApi
import com.google.android.horologist.media.ui.R
import com.google.android.horologist.media.ui.state.model.MediaUiModel
import com.google.android.horologist.paparazzi.ExperimentalHorologistPaparazziApi
import com.google.android.horologist.paparazzi.WearPaparazzi
import org.junit.Rule
import org.junit.Test

class MediaChipTest {
    @get:Rule
    val paparazzi = WearPaparazzi()

    @Test
    fun givenMediaWithArtwork_thenDisplaysArtwork() {
        paparazzi.snapshotInABox {
            FakeImageLoader.Resources.override {
                MediaChip(
                    title = "Red Hot Chilli Peppers",
                    artworkUri = R.drawable.ic_uamp,
                    onClick = {}
                )
            }
        }
    }

    @Test
    fun givenMediaWithNOArtwork_thenDoesNOTDisplayArtwork() {
        paparazzi.snapshotInABox {
            FakeImageLoader.Resources.override {
                MediaChip(
                    title = "Red Hot Chilli Peppers",
                    artworkUri = null,
                    onClick = {}
                )
            }
        }
    }

    @Test
    fun givenVeryLongTitle_thenEllipsizeAt2ndLine() {
        paparazzi.snapshotInABox {
            FakeImageLoader.Resources.override {
                MediaChip(
                    title = "Very very very very very very very very very very very long title",
                    artworkUri = R.drawable.ic_uamp,
                    onClick = {}
                )
            }
        }
    }

    @Test
    fun givenNOTitle_thenDisplaysDefaultTitle() {
        paparazzi.snapshotInABox {
            FakeImageLoader.Resources.override {
                MediaChip(
                    media = MediaUiModel(
                        id = "id",
                        artworkUri = FakeImageLoader.TestIconResourceUri
                    ),
                    onClick = {},
                    defaultTitle = "No title"
                )
            }
        }
    }

    @Test
    fun givenModifier_thenAppliesModifierCorrectly() {
        paparazzi.snapshotInABox {
            FakeImageLoader.Resources.override {
                MediaChip(
                    media = MediaUiModel(
                        id = "id",
                        title = "Red Hot Chilli Peppers",
                        artworkUri = FakeImageLoader.TestIconResourceUri
                    ),
                    onClick = {},
                    modifier = Modifier
                        .height(120.dp)
                )
            }
        }
    }
}
