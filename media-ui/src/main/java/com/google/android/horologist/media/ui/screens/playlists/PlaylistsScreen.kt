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

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.ScalingLazyColumn
import androidx.wear.compose.material.ScalingLazyListState
import com.google.android.horologist.compose.navscaffold.scrollableColumn
import com.google.android.horologist.media.ui.ExperimentalHorologistMediaUiApi
import com.google.android.horologist.media.ui.R
import com.google.android.horologist.media.ui.components.base.SecondaryPlaceholderChip
import com.google.android.horologist.media.ui.components.base.StandardChip
import com.google.android.horologist.media.ui.components.base.StandardChipType
import com.google.android.horologist.media.ui.components.base.Title
import com.google.android.horologist.media.ui.state.model.PlaylistUiModel

@ExperimentalHorologistMediaUiApi
@Composable
public fun <T> PlaylistsScreen(
    playlists: List<T>,
    playlistContent: @Composable (playlist: T) -> Unit,
    focusRequester: FocusRequester,
    scalingLazyListState: ScalingLazyListState,
    modifier: Modifier = Modifier
) {
    PlaylistsScreen(
        playlistsScreenState = PlaylistsScreenState.Loaded(playlists),
        playlistContent = playlistContent,
        focusRequester = focusRequester,
        scalingLazyListState = scalingLazyListState,
        modifier = modifier
    )
}

@ExperimentalHorologistMediaUiApi
@Composable
public fun <T> PlaylistsScreen(
    playlistsScreenState: PlaylistsScreenState<T>,
    playlistContent: @Composable (playlist: T) -> Unit,
    focusRequester: FocusRequester,
    scalingLazyListState: ScalingLazyListState,
    modifier: Modifier = Modifier
) {
    ScalingLazyColumn(
        modifier = modifier
            .fillMaxSize()
            .scrollableColumn(focusRequester, scalingLazyListState),
        state = scalingLazyListState
    ) {
        item {
            Title(
                textId = R.string.horologist_browse_playlist_title,
                modifier = Modifier.padding(bottom = 12.dp)
            )
        }

        when (playlistsScreenState) {
            is PlaylistsScreenState.Loaded<*> -> {
                items(count = playlistsScreenState.playlistList.size) { index ->

                    @Suppress("UNCHECKED_CAST")
                    // Suppress reason: PlaylistsScreen and PlaylistsScreenState share same T type.
                    playlistContent(
                        playlist = playlistsScreenState.playlistList[index] as T
                    )
                }
            }
            is PlaylistsScreenState.Loading<*> -> {
                items(count = 4) {
                    SecondaryPlaceholderChip()
                }
            }
            // renders empty as it should display an error dialog
            is PlaylistsScreenState.Failed -> Unit
        }
    }
}

@ExperimentalHorologistMediaUiApi
@Composable
public fun PlaylistsScreen(
    playlistsScreenState: PlaylistsScreenState<PlaylistUiModel>,
    onPlaylistItemClick: (PlaylistUiModel) -> Unit,
    focusRequester: FocusRequester,
    scalingLazyListState: ScalingLazyListState,
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
        playlistsScreenState = playlistsScreenState,
        playlistContent = playlistContent,
        focusRequester = focusRequester,
        scalingLazyListState = scalingLazyListState,
        modifier = modifier
    )
}

/**
 * Represents the state of [PlaylistsScreen].
 */
@ExperimentalHorologistMediaUiApi
public sealed class PlaylistsScreenState<T> {

    public class Loading<T> : PlaylistsScreenState<T>()

    public data class Loaded<T>(
        val playlistList: List<T>
    ) : PlaylistsScreenState<T>()

    public class Failed<T> : PlaylistsScreenState<T>()
}
