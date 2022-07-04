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

@file:OptIn(ExperimentalHorologistMediaUiApi::class)

package com.google.android.horologist.media.ui.screens.browse

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FeaturedPlayList
import androidx.compose.runtime.Composable
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.wear.compose.material.rememberScalingLazyListState
import com.google.android.horologist.media.ui.ExperimentalHorologistMediaUiApi
import com.google.android.horologist.media.ui.state.model.DownloadPlaylistUiModel
import com.google.android.horologist.media.ui.uamp.UampTheme
import com.google.android.horologist.media.ui.utils.rememberVectorPainter

@Preview(
    group = "Large Round",
    device = Devices.WEAR_OS_LARGE_ROUND,
    showSystemUi = true,
    backgroundColor = BACKGROUND_COLOR,
    showBackground = true
)
@Preview(
    group = "Small Round",
    device = Devices.WEAR_OS_SMALL_ROUND,
    showSystemUi = true,
    backgroundColor = BACKGROUND_COLOR,
    showBackground = true
)
@Preview(
    group = "Square",
    device = Devices.WEAR_OS_SQUARE,
    showSystemUi = true,
    backgroundColor = BACKGROUND_COLOR,
    showBackground = true
)
@Composable
fun BrowseScreenPreview() {
    BrowseScreen(
        browseScreenState = BrowseScreenState.Loaded(downloadList),
        onDownloadItemClick = { },
        onPlaylistsClick = { },
        onSettingsClick = { },
        focusRequester = FocusRequester(),
        scalingLazyListState = rememberScalingLazyListState(),
        downloadItemArtworkPlaceholder = rememberVectorPainter(
            image = Icons.Default.FeaturedPlayList,
            tintColor = Color.Green,
        )
    )
}

@Preview(
    name = "No downloads",
    group = "Large Round",
    device = Devices.WEAR_OS_LARGE_ROUND,
    showSystemUi = true,
    backgroundColor = BACKGROUND_COLOR,
    showBackground = true
)
@Preview(
    name = "No downloads",
    group = "Small Round",
    device = Devices.WEAR_OS_SMALL_ROUND,
    showSystemUi = true,
    backgroundColor = BACKGROUND_COLOR,
    showBackground = true
)
@Preview(
    name = "No downloads",
    group = "Square",
    device = Devices.WEAR_OS_SQUARE,
    showSystemUi = true,
    backgroundColor = BACKGROUND_COLOR,
    showBackground = true
)
@Composable
fun BrowseScreenPreviewNoDownloads() {
    BrowseScreen(
        browseScreenState = BrowseScreenState.Loaded(emptyList()),
        onDownloadItemClick = { },
        onPlaylistsClick = { },
        onSettingsClick = { },
        focusRequester = FocusRequester(),
        scalingLazyListState = rememberScalingLazyListState(),
    )
}

@Preview(
    name = "Loading",
    group = "Large Round",
    device = Devices.WEAR_OS_LARGE_ROUND,
    showSystemUi = true,
    backgroundColor = BACKGROUND_COLOR,
    showBackground = true
)
@Preview(
    name = "Loading",
    group = "Small Round",
    device = Devices.WEAR_OS_SMALL_ROUND,
    showSystemUi = true,
    backgroundColor = BACKGROUND_COLOR,
    showBackground = true
)
@Preview(
    name = "Loading",
    group = "Square",
    device = Devices.WEAR_OS_SQUARE,
    showSystemUi = true,
    backgroundColor = BACKGROUND_COLOR,
    showBackground = true
)
@Composable
fun BrowseScreenPreviewLoading() {
    BrowseScreen(
        browseScreenState = BrowseScreenState.Loading,
        onDownloadItemClick = { },
        onPlaylistsClick = { },
        onSettingsClick = { },
        focusRequester = FocusRequester(),
        scalingLazyListState = rememberScalingLazyListState(),
    )
}

@Preview(
    name = "Uamp theme",
    group = "Large Round",
    device = Devices.WEAR_OS_LARGE_ROUND,
    showSystemUi = true,
    backgroundColor = BACKGROUND_COLOR,
    showBackground = true
)
@Preview(
    name = "Uamp theme",
    group = "Small Round",
    device = Devices.WEAR_OS_SMALL_ROUND,
    showSystemUi = true,
    backgroundColor = BACKGROUND_COLOR,
    showBackground = true
)
@Preview(
    name = "Uamp theme",
    group = "Square",
    device = Devices.WEAR_OS_SQUARE,
    showSystemUi = true,
    backgroundColor = BACKGROUND_COLOR,
    showBackground = true
)
@Composable
fun BrowseScreenPreviewUamp() {
    UampTheme {
        BrowseScreen(
            browseScreenState = BrowseScreenState.Loaded(downloadList),
            onDownloadItemClick = { },
            onPlaylistsClick = { },
            onSettingsClick = { },
            focusRequester = FocusRequester(),
            scalingLazyListState = rememberScalingLazyListState(),
            downloadItemArtworkPlaceholder = rememberVectorPainter(
                image = Icons.Default.FeaturedPlayList,
                tintColor = Color.Green,
            )
        )
    }
}

private val downloadList = buildList {
    add(
        DownloadPlaylistUiModel.InProgress(
            title = "Rock Classics",
            artworkUri = "https://www.example.com/album1.png",
            percentage = 15,
        )
    )

    add(
        DownloadPlaylistUiModel.Completed(
            title = "Pop Punk",
            artworkUri = "https://www.example.com/album2.png"
        )
    )
}

private const val BACKGROUND_COLOR = 0xFF000000
