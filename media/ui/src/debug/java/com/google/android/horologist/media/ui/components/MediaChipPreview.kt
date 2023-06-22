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

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Album
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import com.google.android.horologist.compose.material.util.rememberVectorPainter
import com.google.android.horologist.compose.tools.WearPreview
import com.google.android.horologist.media.ui.state.model.MediaUiModel

@WearPreview
@Composable
fun MediaChipPreview() {
    MediaChip(
        media = MediaUiModel(
            id = "id",
            title = "Red Hot Chilli Peppers",
            artworkUri = "artworkUri"
        ),
        onClick = {},
        placeholder = rememberVectorPainter(
            image = Icons.Default.Album,
            tintColor = Color.Blue
        )
    )
}

@Preview(
    name = "No artwork",
    backgroundColor = 0xff000000,
    showBackground = true
)
@Composable
fun MediaChipPreviewNoArtwork() {
    MediaChip(
        media = MediaUiModel(id = "id", title = "Red Hot Chilli Peppers"),
        onClick = {},
        placeholder = rememberVectorPainter(
            image = Icons.Default.Album,
            tintColor = Color.Blue
        )
    )
}

@Preview(
    name = "No title",
    backgroundColor = 0xff000000,
    showBackground = true
)
@Composable
fun MediaChipPreviewNoTitle() {
    MediaChip(
        media = MediaUiModel(id = "id", title = "title", artworkUri = "artworkUri"),
        onClick = {},
        defaultTitle = "No title",
        placeholder = rememberVectorPainter(
            image = Icons.Default.Album,
            tintColor = Color.Blue
        )
    )
}

@Preview(
    name = "Very long title",
    backgroundColor = 0xff000000,
    showBackground = true
)
@Composable
fun MediaChipPreviewVeryLongTitle() {
    MediaChip(
        media = MediaUiModel(
            id = "id",
            title = "Very very very very very very very very very very very very very very very very very very very long title",
            artworkUri = "artworkUri"
        ),
        onClick = {},
        placeholder = rememberVectorPainter(
            image = Icons.Default.Album,
            tintColor = Color.Blue
        )
    )
}
