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

package com.google.android.horologist.media.ui.screens.playlists

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.ChipDefaults
import com.google.android.horologist.annotations.ExperimentalHorologistApi
import com.google.android.horologist.base.ui.components.StandardChip
import com.google.android.horologist.base.ui.components.StandardChipType
import com.google.android.horologist.composables.PlaceholderChip
import com.google.android.horologist.composables.Section
import com.google.android.horologist.composables.SectionedList
import com.google.android.horologist.compose.layout.ScalingLazyColumnState
import com.google.android.horologist.compose.material.Title
import com.google.android.horologist.media.ui.R
import com.google.android.horologist.media.ui.state.model.PlaylistUiModel

@ExperimentalHorologistApi
@Composable
public fun <T> PlaylistsScreen(
    columnState: ScalingLazyColumnState,
    playlists: List<T>,
    playlistContent: @Composable (playlist: T) -> Unit,
    modifier: Modifier = Modifier
) {
    PlaylistsScreen(
        playlistsScreenState = PlaylistsScreenState.Loaded(playlists),
        playlistContent = playlistContent,
        columnState = columnState,
        modifier = modifier
    )
}

@ExperimentalHorologistApi
@Composable
public fun <T> PlaylistsScreen(
    columnState: ScalingLazyColumnState,
    playlistsScreenState: PlaylistsScreenState<T>,
    playlistContent: @Composable (playlist: T) -> Unit,
    modifier: Modifier = Modifier
) {
    SectionedList(
        modifier = modifier,
        columnState = columnState
    ) {
        val sectionState = when (playlistsScreenState) {
            is PlaylistsScreenState.Loaded<T> -> {
                Section.State.Loaded(playlistsScreenState.playlistList)
            }

            PlaylistsScreenState.Failed -> Section.State.Failed
            PlaylistsScreenState.Loading -> Section.State.Loading
        }

        section(state = sectionState) {
            header {
                Title(
                    R.string.horologist_browse_playlist_title,
                    Modifier.padding(bottom = 12.dp)
                )
            }

            loaded { playlistContent(it) }

            loading(count = 4) {
                Column {
                    PlaceholderChip(colors = ChipDefaults.secondaryChipColors())
                }
            }
        }
    }
}

@ExperimentalHorologistApi
@Composable
public fun PlaylistsScreen(
    columnState: ScalingLazyColumnState,
    playlistsScreenState: PlaylistsScreenState<PlaylistUiModel>,
    onPlaylistItemClick: (PlaylistUiModel) -> Unit,
    modifier: Modifier = Modifier,
    playlistItemArtworkPlaceholder: Painter? = null
) {
    val playlistContent: @Composable (playlist: PlaylistUiModel) -> Unit = { playlist ->
        StandardChip(
            label = playlist.title,
            onClick = { onPlaylistItemClick(playlist) },
            icon = playlist.artworkUri,
            largeIcon = true,
            placeholder = playlistItemArtworkPlaceholder,
            chipType = StandardChipType.Secondary
        )
    }

    PlaylistsScreen(
        columnState = columnState,
        playlistsScreenState = playlistsScreenState,
        playlistContent = playlistContent,
        modifier = modifier
    )
}

/**
 * Represents the state of [PlaylistsScreen].
 */
@ExperimentalHorologistApi
public sealed class PlaylistsScreenState<out T> {

    public object Loading : PlaylistsScreenState<Nothing>()

    public data class Loaded<T>(
        val playlistList: List<T>
    ) : PlaylistsScreenState<T>()

    public object Failed : PlaylistsScreenState<Nothing>()
}
