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

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Downloading
import androidx.compose.material.icons.filled.PlaylistPlay
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.onClick
import androidx.compose.ui.semantics.semantics
import androidx.wear.compose.material.ChipDefaults
import com.google.android.horologist.annotations.ExperimentalHorologistApi
import com.google.android.horologist.composables.PlaceholderChip
import com.google.android.horologist.composables.Section
import com.google.android.horologist.compose.layout.ScalingLazyColumnState
import com.google.android.horologist.compose.material.Chip
import com.google.android.horologist.media.ui.R
import com.google.android.horologist.media.ui.state.model.PlaylistDownloadUiModel

/**
 * An implementation of [BrowseScreen] using [PlaylistDownloadUiModel] as model.
 */
@ExperimentalHorologistApi
@Composable
public fun PlaylistDownloadBrowseScreen(
    columnState: ScalingLazyColumnState,
    browseScreenState: BrowseScreenState,
    onDownloadItemClick: (PlaylistDownloadUiModel) -> Unit,
    onDownloadItemInProgressClick: (PlaylistDownloadUiModel) -> Unit,
    onPlaylistsClick: () -> Unit,
    onSettingsClick: () -> Unit,
    modifier: Modifier = Modifier,
    downloadItemArtworkPlaceholder: Painter? = null,
    onDownloadItemInProgressClickActionLabel: String? = null
) {
    BrowseScreen(
        columnState = columnState,
        modifier = modifier
    ) {
        val downloadsSectionState = when (browseScreenState) {
            BrowseScreenState.Loading -> Section.State.Loading
            is BrowseScreenState.Loaded -> {
                if (browseScreenState.downloadList.isEmpty()) {
                    Section.State.Empty
                } else {
                    Section.State.Loaded(browseScreenState.downloadList)
                }
            }

            BrowseScreenState.Failed ->
                // display empty state
                Section.State.Empty
        }

        downloadsSection(state = downloadsSectionState) {
            loading {
                PlaceholderChip(colors = ChipDefaults.secondaryChipColors())
            }

            loaded { download: PlaylistDownloadUiModel ->
                when (download) {
                    is PlaylistDownloadUiModel.Completed -> {
                        Chip(
                            label = download.playlistUiModel.title,
                            onClick = { onDownloadItemClick(download) },
                            icon = download.playlistUiModel.artworkUri,
                            largeIcon = true,
                            placeholder = downloadItemArtworkPlaceholder,
                            colors = ChipDefaults.secondaryChipColors()
                        )
                    }

                    is PlaylistDownloadUiModel.InProgress -> {
                        val customModifier = onDownloadItemInProgressClickActionLabel?.let {
                            Modifier.semantics {
                                onClick(
                                    label = onDownloadItemInProgressClickActionLabel,
                                    action = null
                                )
                            }
                        } ?: Modifier

                        Chip(
                            label = download.playlistUiModel.title,
                            onClick = { onDownloadItemInProgressClick(download) },
                            modifier = customModifier,
                            secondaryLabel = stringResource(
                                id = R.string.horologist_browse_downloads_progress,
                                download.percentage
                            ),
                            icon = Icons.Default.Downloading,
                            placeholder = downloadItemArtworkPlaceholder,
                            colors = ChipDefaults.secondaryChipColors()
                        )
                    }
                }
            }
        }

        playlistsSection(
            buttons = listOf(
                BrowseScreenPlaylistsSectionButton(
                    textId = R.string.horologist_browse_library_playlists_button,
                    icon = Icons.Default.PlaylistPlay,
                    onClick = onPlaylistsClick
                ),
                BrowseScreenPlaylistsSectionButton(
                    textId = R.string.horologist_browse_library_settings_button,
                    icon = Icons.Default.Settings,
                    onClick = onSettingsClick
                )
            )
        )
    }
}
