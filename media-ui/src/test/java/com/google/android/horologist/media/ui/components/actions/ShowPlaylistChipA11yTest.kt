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

package com.google.android.horologist.media.ui.components.actions

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.google.android.horologist.annotations.ExperimentalHorologistApi
import com.google.android.horologist.compose.tools.coil.FakeImageLoader
import com.google.android.horologist.media.ui.R
import com.google.android.horologist.screenshots.ScreenshotTest
import org.junit.Test

class ShowPlaylistChipA11yTest : ScreenshotTest() {
    init {
        enableA11yTest()
        fakeImageLoader = FakeImageLoader.Resources
        screenTimeText = {}
    }

    @Test
    fun a11y() {
        takeScreenshot {
            Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                ShowPlaylistChip(
                    artworkUri = R.drawable.horologist_logo,
                    name = "Playlists",
                    onClick = {}
                )
            }
        }
    }
}
