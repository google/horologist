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

@file:OptIn(ExperimentalHorologistMediaUiApi::class, ExperimentalHorologistPaparazziApi::class)

package com.google.android.horologist.media.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Album
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.unit.dp
import com.google.android.horologist.compose.tools.coil.FakeImageLoader
import com.google.android.horologist.media.ui.ExperimentalHorologistMediaUiApi
import com.google.android.horologist.media.ui.state.model.MediaUiModel
import com.google.android.horologist.media.ui.utils.rememberVectorPainter
import com.google.android.horologist.paparazzi.ExperimentalHorologistPaparazziApi
import com.google.android.horologist.paparazzi.WearPaparazzi
import org.junit.Rule
import org.junit.Test

class MediaChipTest {
    @get:Rule
    val paparazzi = WearPaparazzi()

    @Test
    fun givenMediaWithArtwork_thenDisplaysArtwork() {
        paparazzi.snapshot {
            FakeImageLoader.Never.override {
                Box(
                    modifier = Modifier.background(Color.Black),
                    contentAlignment = Alignment.Center
                ) {
                    MediaChip(
                        title = "Red Hot Chilli Peppers",
                        artworkUri = "artworkUri",
                        onClick = {},
                        placeholder = rememberVectorPainter(image = Icons.Default.Album)
                    )
                }
            }
        }
    }

    @Test
    fun givenMediaWithNOArtwork_thenDoesNOTDisplayArtwork() {
        paparazzi.snapshot {
            FakeImageLoader.NotFound.override {
                Box(
                    modifier = Modifier.background(Color.Black),
                    contentAlignment = Alignment.Center
                ) {
                    MediaChip(
                        title = "Red Hot Chilli Peppers",
                        artworkUri = null,
                        onClick = {},
                        placeholder = rememberVectorPainter(image = Icons.Default.Album)
                    )
                }
            }
        }
    }

    @Test
    fun givenVeryLongTitle_thenEllipsizeAt2ndLine() {
        paparazzi.snapshot {
            FakeImageLoader.NotFound.override {
                Box(
                    modifier = Modifier.background(Color.Black),
                    contentAlignment = Alignment.Center
                ) {
                    MediaChip(
                        title = "Very very very very very very very very very very very long title",
                        artworkUri = "artworkUri",
                        onClick = {},
                        placeholder = rememberVectorPainter(image = Icons.Default.Album)
                    )
                }
            }
        }
    }

    @Test
    fun givenNOTitle_thenDisplaysDefaultTitle() {
        paparazzi.snapshot {
            FakeImageLoader.Never.override {
                Box(
                    modifier = Modifier.background(Color.Black),
                    contentAlignment = Alignment.Center
                ) {
                    MediaChip(
                        media = MediaUiModel(id = "id", artworkUri = "artworkUri"),
                        onClick = {},
                        defaultTitle = "No title",
                        placeholder = rememberVectorPainter(
                            image = Icons.Default.Album,
                            tintColor = Color.Blue
                        )
                    )
                }
            }
        }
    }

    @Test
    fun givenModifier_thenAppliesModifierCorrectly() {
        paparazzi.snapshot {
            FakeImageLoader.Never.override {
                Box(
                    modifier = Modifier.background(Color.Black),
                    contentAlignment = Alignment.Center
                ) {
                    MediaChip(
                        media = MediaUiModel(
                            id = "id",
                            title = "Red Hot Chilli Peppers",
                            artworkUri = "artworkUri"
                        ),
                        onClick = {},
                        modifier = Modifier
                            .height(120.dp),
                        placeholder = rememberVectorPainter(
                            image = Icons.Default.Album,
                            tintColor = Color.Blue
                        )
                    )
                }
            }
        }
    }
}
