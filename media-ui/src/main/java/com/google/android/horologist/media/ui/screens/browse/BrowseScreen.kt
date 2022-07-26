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

package com.google.android.horologist.media.ui.screens.browse

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Downloading
import androidx.compose.material.icons.filled.PlaylistPlay
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.ScalingLazyColumn
import androidx.wear.compose.material.ScalingLazyListState
import androidx.wear.compose.material.Text
import com.google.android.horologist.compose.navscaffold.scrollableColumn
import com.google.android.horologist.media.ui.ExperimentalHorologistMediaUiApi
import com.google.android.horologist.media.ui.R
import com.google.android.horologist.media.ui.components.base.SecondaryPlaceholderChip
import com.google.android.horologist.media.ui.components.base.StandardChip
import com.google.android.horologist.media.ui.components.base.StandardChipType
import com.google.android.horologist.media.ui.components.base.Title
import com.google.android.horologist.media.ui.state.model.DownloadPlaylistUiModel

/**
 * A screen to:
 * - display user's [downloaded media][DownloadPlaylistUiModel] list;
 * - provide access to libraries;
 * - provide access to settings;
 */
@ExperimentalHorologistMediaUiApi
@Composable
public fun BrowseScreen(
    browseScreenState: BrowseScreenState,
    onDownloadItemClick: (DownloadPlaylistUiModel) -> Unit,
    onPlaylistsClick: () -> Unit,
    onSettingsClick: () -> Unit,
    focusRequester: FocusRequester,
    scalingLazyListState: ScalingLazyListState,
    modifier: Modifier = Modifier,
    downloadItemArtworkPlaceholder: Painter? = null
) {
    ScalingLazyColumn(
        modifier = modifier
            .fillMaxSize()
            .scrollableColumn(focusRequester, scalingLazyListState),
        state = scalingLazyListState,
    ) {
        item {
            Title(
                textId = R.string.horologist_browse_downloads_title,
                modifier = Modifier.padding(bottom = 12.dp),
            )
        }

        if (browseScreenState is BrowseScreenState.Loaded) {
            val downloadList = browseScreenState.downloadList

            if (downloadList.isEmpty()) {
                item {
                    Text(
                        text = stringResource(id = R.string.horologist_browse_downloads_empty),
                        modifier = Modifier.padding(top = 8.dp, bottom = 8.dp),
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.body2,
                    )
                }
            } else {
                items(count = downloadList.size) { index ->
                    when (val download = downloadList[index]) {
                        is DownloadPlaylistUiModel.Completed -> {
                            StandardChip(
                                label = download.playlistUiModel.title,
                                onClick = { onDownloadItemClick(download) },
                                icon = download.playlistUiModel.artworkUri,
                                largeIcon = true,
                                placeholder = downloadItemArtworkPlaceholder,
                                chipType = StandardChipType.Secondary,
                            )
                        }
                        is DownloadPlaylistUiModel.InProgress -> {
                            StandardChip(
                                label = download.playlistUiModel.title,
                                onClick = { onDownloadItemClick(download) },
                                secondaryLabel = stringResource(
                                    id = R.string.horologist_browse_downloads_progress,
                                    download.percentage
                                ),
                                icon = Icons.Default.Downloading,
                                placeholder = downloadItemArtworkPlaceholder,
                                chipType = StandardChipType.Secondary,
                            )
                        }
                    }
                }
            }
        } else if (browseScreenState is BrowseScreenState.Loading) {
            item {
                SecondaryPlaceholderChip()
            }
        }

        item {
            Title(
                textId = R.string.horologist_browse_library_playlists,
                modifier = Modifier.padding(top = 12.dp),
            )
        }

        item {
            StandardChip(
                label = stringResource(id = R.string.horologist_browse_library_playlists),
                icon = Icons.Default.PlaylistPlay,
                onClick = onPlaylistsClick,
                chipType = StandardChipType.Secondary,
            )
        }

        item {
            StandardChip(
                label = stringResource(id = R.string.horologist_browse_library_settings),
                icon = Icons.Default.Settings,
                onClick = onSettingsClick,
                chipType = StandardChipType.Secondary,
            )
        }
    }
}

/**
 * Represents the state of [BrowseScreen].
 */
@ExperimentalHorologistMediaUiApi
public sealed class BrowseScreenState {

    public object Loading : BrowseScreenState()

    public data class Loaded(
        val downloadList: List<DownloadPlaylistUiModel>,
    ) : BrowseScreenState()
}
