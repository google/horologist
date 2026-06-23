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

package com.google.android.horologist.media.ui.material3.screens.playlists

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.unit.dp
import androidx.wear.compose.foundation.lazy.rememberScalingLazyListState
import androidx.wear.compose.material3.ButtonDefaults
import androidx.wear.compose.material3.FilledTonalButton
import androidx.wear.compose.material3.Icon
import androidx.wear.compose.material3.ListHeader
import androidx.wear.compose.material3.ScreenScaffold
import androidx.wear.compose.material3.Text
import androidx.wear.compose.material3.rememberPlaceholderState
import com.google.android.horologist.annotations.ExperimentalHorologistApi
import com.google.android.horologist.images.coil.CoilPaintable
import com.google.android.horologist.media.ui.material3.composables.PlaceholderButton
import com.google.android.horologist.media.ui.material3.composables.Section
import com.google.android.horologist.media.ui.material3.composables.SectionedList
import com.google.android.horologist.media.ui.model.R
import com.google.android.horologist.media.ui.state.model.PlaylistUiModel

@ExperimentalHorologistApi
@Composable
public fun <T> PlaylistsScreen(
    playlists: List<T>,
    playlistContent: @Composable (playlist: T) -> Unit,
    modifier: Modifier = Modifier,
) {
    PlaylistsScreen(
        playlistsScreenState = PlaylistsScreenState.Loaded(playlists),
        playlistContent = playlistContent,
        modifier = modifier,
    )
}

@ExperimentalHorologistApi
@Composable
public fun <T> PlaylistsScreen(
    playlistsScreenState: PlaylistsScreenState<T>,
    playlistContent: @Composable (playlist: T) -> Unit,
    modifier: Modifier = Modifier,
) {
    val scrollState = rememberScalingLazyListState()

    // TODO This should be folded into SectionedList
    val placeholderState =
        rememberPlaceholderState(playlistsScreenState is PlaylistsScreenState.Loading)

    ScreenScaffold(scrollState = scrollState) {
        SectionedList(
            modifier = modifier,
            scrollState = scrollState,
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
                    ListHeader(
                        modifier = Modifier.padding(bottom = 12.dp),
                    ) { Text(text = stringResource(id = R.string.horologist_browse_playlist_title)) }
                }

                loaded { playlistContent(it) }

                loading(count = 4) {
                    Column {
                        PlaceholderButton(
                            modifier = Modifier.fillMaxWidth(),
                            placeholderState = placeholderState,
                            colors = ButtonDefaults.filledTonalButtonColors(),
                            secondaryLabel = false,
                        )
                    }
                }
            }
        }
    }
}

@ExperimentalHorologistApi
@Composable
public fun PlaylistsScreen(
    playlistsScreenState: PlaylistsScreenState<PlaylistUiModel>,
    onPlaylistItemClick: (PlaylistUiModel) -> Unit,
    modifier: Modifier = Modifier,
    playlistItemArtworkPlaceholder: Painter? = null,
) {
    val playlistContent: @Composable (playlist: PlaylistUiModel) -> Unit = { playlist ->
        FilledTonalButton(
            modifier = modifier.fillMaxWidth(),
            label = { Text(playlist.title) },
            onClick = { onPlaylistItemClick(playlist) },
            icon = {
                Icon(
                    painter = CoilPaintable(playlist.artworkUri, playlistItemArtworkPlaceholder).rememberPainter(),
                    contentDescription = null,
                )
            },
        )
    }

    PlaylistsScreen(
        playlistsScreenState = playlistsScreenState,
        playlistContent = playlistContent,
        modifier = modifier,
    )
}

/**
 * Represents the state of [PlaylistsScreen].
 */
@ExperimentalHorologistApi
public sealed class PlaylistsScreenState<out T> {

    public object Loading : PlaylistsScreenState<Nothing>()

    public data class Loaded<T>(
        val playlistList: List<T>,
    ) : PlaylistsScreenState<T>()

    public object Failed : PlaylistsScreenState<Nothing>()
}
