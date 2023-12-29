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

package com.google.android.horologist.media.ui.components.actions

import com.google.android.horologist.images.base.paintable.DrawableResPaintable
import com.google.android.horologist.logo.R
import com.google.android.horologist.screenshots.ScreenshotBaseTest
import org.junit.Test

class ShowPlaylistChipTest : ScreenshotBaseTest() {

    @Test
    fun givenArtwork_thenDisplaysArtwork() {
        screenshotTestRule.setContent(isComponent = true, takeScreenshot = true) {
            ShowPlaylistChip(
                artworkPaintable = DrawableResPaintable(R.drawable.horologist_logo),
                name = "Playlists",
                onClick = {},
            )
        }
    }

    @Test
    fun givenNOArtwork_thenDoesNOTDisplayArtwork() {
        screenshotTestRule.setContent(isComponent = true, takeScreenshot = true) {
            ShowPlaylistChip(
                artworkPaintable = null,
                name = "Playlists",
                onClick = {},
            )
        }
    }

    @Test
    fun givenNOName_thenDoesDisplayArtwork() {
        screenshotTestRule.setContent(isComponent = true, takeScreenshot = true) {
            ShowPlaylistChip(
                artworkPaintable = DrawableResPaintable(R.drawable.horologist_logo),
                name = null,
                onClick = {},
            )
        }
    }

    @Test
    fun givenVeryLongTitle_thenEllipsizeAt2ndLine() {
        screenshotTestRule.setContent(isComponent = true, takeScreenshot = true) {
            ShowPlaylistChip(
                artworkPaintable = DrawableResPaintable(R.drawable.horologist_logo),
                name = "Very very very very very very very very very very very very very very very very very very very long title",
                onClick = {},
            )
        }
    }
}
