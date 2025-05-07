/*
 * Copyright 2025 The Android Open Source Project
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

package com.google.android.horologist.media.ui.material3.screens.browse

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.PlaylistPlay
import androidx.compose.material.icons.filled.Downloading
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.onClick
import androidx.compose.ui.semantics.semantics
import androidx.wear.compose.material3.ButtonDefaults
import androidx.wear.compose.material3.FilledTonalButton
import androidx.wear.compose.material3.Icon
import androidx.wear.compose.material3.Text
import com.google.android.horologist.annotations.ExperimentalHorologistApi
import com.google.android.horologist.images.coil.CoilPaintable
import com.google.android.horologist.media.ui.material3.composables.PlaceholderButton
import com.google.android.horologist.media.ui.material3.composables.Section
import com.google.android.horologist.media.ui.model.R
import com.google.android.horologist.media.ui.state.model.PlaylistDownloadUiModel

/**
 * An implementation of [BrowseScreen] using [PlaylistDownloadUiModel] as model.
 */
@ExperimentalHorologistApi
@Composable
public fun PlaylistDownloadBrowseScreen(
    browseScreenState: BrowseScreenState,
    onDownloadItemClick: (PlaylistDownloadUiModel) -> Unit,
    onDownloadItemInProgressClick: (PlaylistDownloadUiModel) -> Unit,
    onPlaylistsClick: () -> Unit,
    onSettingsClick: () -> Unit,
    modifier: Modifier = Modifier,
    downloadItemArtworkPlaceholder: Painter? = null,
    onDownloadItemInProgressClickActionLabel: String? = null,
) {
    BrowseScreen(
        modifier = modifier,
    ) {
        PlaylistDownloadBrowseScreenContent(
            browseScreenState,
            onDownloadItemClick,
            onDownloadItemInProgressClick,
            onPlaylistsClick,
            onSettingsClick,
            downloadItemArtworkPlaceholder,
            onDownloadItemInProgressClickActionLabel,
        )
    }
}

internal fun BrowseScreenScope.PlaylistDownloadBrowseScreenContent(
    browseScreenState: BrowseScreenState,
    onDownloadItemClick: (PlaylistDownloadUiModel) -> Unit,
    onDownloadItemInProgressClick: (PlaylistDownloadUiModel) -> Unit,
    onPlaylistsClick: () -> Unit,
    onSettingsClick: () -> Unit,
    downloadItemArtworkPlaceholder: Painter? = null,
    onDownloadItemInProgressClickActionLabel: String? = null,
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
            PlaceholderButton(
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.filledTonalButtonColors(),
            )
        }

        loaded { download: PlaylistDownloadUiModel ->
            when (download) {
                is PlaylistDownloadUiModel.Completed -> {
                    FilledTonalButton(
                        label = { Text(download.playlistUiModel.title) },
                        onClick = { onDownloadItemClick(download) },
                        icon = {
                            Icon(
                                painter = CoilPaintable(
                                    download.playlistUiModel.artworkUri,
                                    downloadItemArtworkPlaceholder,
                                ).rememberPainter(),
                                contentDescription = null,
                            )
                        },
                    )
                }

                is PlaylistDownloadUiModel.InProgress -> {
                    val customModifier = onDownloadItemInProgressClickActionLabel?.let {
                        Modifier.semantics {
                            onClick(
                                label = onDownloadItemInProgressClickActionLabel,
                                action = null,
                            )
                        }
                    } ?: Modifier

                    FilledTonalButton(
                        modifier = customModifier,
                        label = { Text(download.playlistUiModel.title) },
                        secondaryLabel = {
                            Text(
                                stringResource(
                                    id = R.string.horologist_browse_downloads_progress,
                                    download.percentage,
                                ),
                            )
                        },
                        onClick = { onDownloadItemInProgressClick(download) },
                        icon = {
                            Icon(
                                imageVector = Icons.Default.Downloading,
                                contentDescription = null,
                            )
                        },
                    )
                }
            }
        }
    }

    playlistsSection(
        buttons = listOf(
            BrowseScreenPlaylistsSectionButton(
                textId = R.string.horologist_browse_library_playlists_button,
                icon = Icons.AutoMirrored.Default.PlaylistPlay,
                onClick = onPlaylistsClick,
            ),
            BrowseScreenPlaylistsSectionButton(
                textId = R.string.horologist_browse_library_settings_button,
                icon = Icons.Default.Settings,
                onClick = onSettingsClick,
            ),
        ),
    )
}
