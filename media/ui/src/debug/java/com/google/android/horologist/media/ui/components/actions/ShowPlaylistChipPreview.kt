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

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FeaturedPlayList
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import com.google.android.horologist.compose.material.util.rememberVectorPainter
import com.google.android.horologist.compose.tools.WearPreview

@WearPreview
@Composable
fun ShowPlaylistChipPreview() {
    ShowPlaylistChip(
        artworkUri = "artworkUri",
        name = "Playlists",
        onClick = {},
        placeholder = rememberVectorPainter(
            image = Icons.Default.FeaturedPlayList,
            tintColor = Color.Green
        )
    )
}

@Preview(
    name = "No artwork",
    backgroundColor = 0xff000000,
    showBackground = true
)
@Composable
fun ShowPlaylistChipPreviewNoArtwork() {
    ShowPlaylistChip(
        artworkUri = null,
        name = "Playlists",
        onClick = {}
    )
}

@Preview(
    name = "No name",
    backgroundColor = 0xff000000,
    showBackground = true
)
@Composable
fun ShowPlaylistChipPreviewNoName() {
    ShowPlaylistChip(
        artworkUri = "artworkUri",
        name = null,
        onClick = {},
        placeholder = rememberVectorPainter(
            image = Icons.Default.FeaturedPlayList,
            tintColor = Color.Green
        )
    )
}

@Preview(
    name = "Very long name",
    backgroundColor = 0xff000000,
    showBackground = true
)
@Composable
fun ShowPlaylistChipPreviewVeryLongName() {
    ShowPlaylistChip(
        artworkUri = "artworkUri",
        name = "Very very very very very very very very very very very very very very very very very very very long title",
        onClick = {},
        placeholder = rememberVectorPainter(
            image = Icons.Default.FeaturedPlayList,
            tintColor = Color.Green
        )
    )
}
