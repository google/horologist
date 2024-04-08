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

package com.google.android.horologist.media.ui.components

import androidx.compose.foundation.layout.height
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.android.horologist.images.base.paintable.DrawableResPaintable
import com.google.android.horologist.logo.R
import com.google.android.horologist.media.ui.state.model.MediaUiModel
import com.google.android.horologist.screenshots.rng.WearLegacyComponentTest
import org.junit.Test

class MediaChipTest : WearLegacyComponentTest() {

    @Test
    fun givenMediaWithArtwork_thenDisplaysArtwork() {
        runComponentTest {
            MediaChip(
                title = "Red Hot Chilli Peppers",
                artworkPaintable = DrawableResPaintable(R.drawable.horologist_logo),
                onClick = {},
            )
        }
    }

    @Test
    fun givenMediaWithNOArtwork_thenDoesNOTDisplayArtwork() {
        runComponentTest {
            MediaChip(
                title = "Red Hot Chilli Peppers",
                artworkPaintable = null,
                onClick = {},
            )
        }
    }

    @Test
    fun givenVeryLongTitle_thenEllipsizeAt2ndLine() {
        runComponentTest {
            MediaChip(
                title = "Very very very very very very very very very very very long title",
                artworkPaintable = DrawableResPaintable(R.drawable.horologist_logo),
                onClick = {},
            )
        }
    }

    @Test
    fun givenNOTitle_thenDisplaysDefaultTitle() {
        runComponentTest {
            MediaChip(
                media = MediaUiModel(
                    id = "id",
                    title = "",
                ),
                onClick = {},
                defaultTitle = "No title",
            )
        }
    }

    @Test
    fun givenModifier_thenAppliesModifierCorrectly() {
        runComponentTest {
            MediaChip(
                media = MediaUiModel(
                    id = "id",
                    title = "Red Hot Chilli Peppers",
                ),
                onClick = {},
                modifier = Modifier
                    .height(120.dp),
            )
        }
    }
}
