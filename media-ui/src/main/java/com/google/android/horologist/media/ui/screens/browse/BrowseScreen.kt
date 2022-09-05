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
import androidx.wear.compose.material.ChipDefaults
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.ScalingLazyColumnDefaults
import androidx.wear.compose.material.ScalingLazyListState
import androidx.wear.compose.material.ScalingParams
import androidx.wear.compose.material.Text
import com.google.android.horologist.composables.PlaceholderChip
import com.google.android.horologist.media.ui.ExperimentalHorologistMediaUiApi
import com.google.android.horologist.media.ui.R
import com.google.android.horologist.media.ui.components.base.StandardChip
import com.google.android.horologist.media.ui.components.base.StandardChipType
import com.google.android.horologist.media.ui.components.base.Title
import com.google.android.horologist.media.ui.components.list.sectioned.Section
import com.google.android.horologist.media.ui.components.list.sectioned.SectionedList
import com.google.android.horologist.media.ui.state.model.PlaylistDownloadUiModel

/**
 * A screen to:
 * - display user's list of [downloaded playlists][PlaylistDownloadUiModel];
 * - provide access to libraries;
 * - provide access to settings;
 */
@ExperimentalHorologistMediaUiApi
@Composable
public fun BrowseScreen(
    browseScreenState: BrowseScreenState,
    onDownloadItemClick: (PlaylistDownloadUiModel) -> Unit,
    onPlaylistsClick: () -> Unit,
    onSettingsClick: () -> Unit,
    focusRequester: FocusRequester,
    scalingLazyListState: ScalingLazyListState,
    modifier: Modifier = Modifier,
    downloadItemArtworkPlaceholder: Painter? = null,
    scalingParams: ScalingParams = ScalingLazyColumnDefaults.scalingParams()
) {
    SectionedList(
        focusRequester = focusRequester,
        scalingLazyListState = scalingLazyListState,
        modifier = modifier,
        scalingParams = scalingParams
    ) {
        val downloadsSectionState = when (browseScreenState) {
            is BrowseScreenState.Loading -> Section.State.Loading
            is BrowseScreenState.Loaded -> {
                if (browseScreenState.downloadList.isEmpty()) {
                    Section.State.Empty
                } else {
                    Section.State.Loaded(browseScreenState.downloadList)
                }
            }
            is BrowseScreenState.Failed ->
                // display empty state
                Section.State.Empty
        }

        section(state = downloadsSectionState) {
            header {
                Title(
                    textId = R.string.horologist_browse_downloads_title,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

            loading {
                PlaceholderChip(colors = ChipDefaults.secondaryChipColors())
            }

            loaded { download: PlaylistDownloadUiModel ->
                when (download) {
                    is PlaylistDownloadUiModel.Completed -> {
                        StandardChip(
                            label = download.playlistUiModel.title,
                            onClick = { onDownloadItemClick(download) },
                            icon = download.playlistUiModel.artworkUri,
                            largeIcon = true,
                            placeholder = downloadItemArtworkPlaceholder,
                            chipType = StandardChipType.Secondary
                        )
                    }
                    is PlaylistDownloadUiModel.InProgress -> {
                        StandardChip(
                            label = download.playlistUiModel.title,
                            onClick = { onDownloadItemClick(download) },
                            secondaryLabel = stringResource(
                                id = R.string.horologist_browse_downloads_progress,
                                download.percentage
                            ),
                            icon = Icons.Default.Downloading,
                            placeholder = downloadItemArtworkPlaceholder,
                            chipType = StandardChipType.Secondary
                        )
                    }
                }
            }

            empty {
                Text(
                    text = stringResource(id = R.string.horologist_browse_downloads_empty),
                    modifier = Modifier.padding(top = 8.dp, bottom = 8.dp),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.body2
                )
            }
        }

        section(
            list = listOf(
                Triple(
                    R.string.horologist_browse_library_playlists,
                    Icons.Default.PlaylistPlay,
                    onPlaylistsClick
                ),
                Triple(
                    R.string.horologist_browse_library_settings,
                    Icons.Default.Settings,
                    onSettingsClick
                )
            )
        ) {
            header {
                Title(
                    textId = R.string.horologist_browse_library_playlists,
                    modifier = Modifier.padding(top = 8.dp, bottom = 8.dp)
                )
            }

            loaded { item ->
                StandardChip(
                    label = stringResource(id = item.first),
                    onClick = item.third,
                    icon = item.second,
                    chipType = StandardChipType.Secondary
                )
            }
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
        val downloadList: List<PlaylistDownloadUiModel>
    ) : BrowseScreenState()

    public object Failed : BrowseScreenState()
}
