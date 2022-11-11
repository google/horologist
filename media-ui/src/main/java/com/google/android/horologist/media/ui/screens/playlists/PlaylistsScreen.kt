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

@file:OptIn(ExperimentalHorologistComposablesApi::class)

package com.google.android.horologist.media.ui.screens.playlists

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.AutoCenteringParams
import androidx.wear.compose.material.ChipDefaults
import androidx.wear.compose.material.ScalingLazyListState
import com.google.android.horologist.base.ui.components.StandardChip
import com.google.android.horologist.base.ui.components.StandardChipType
import com.google.android.horologist.base.ui.components.Title
import com.google.android.horologist.composables.ExperimentalHorologistComposablesApi
import com.google.android.horologist.composables.PlaceholderChip
import com.google.android.horologist.composables.Section
import com.google.android.horologist.composables.SectionedList
import com.google.android.horologist.compose.focus.rememberActiveFocusRequester
import com.google.android.horologist.media.ui.ExperimentalHorologistMediaUiApi
import com.google.android.horologist.media.ui.R
import com.google.android.horologist.media.ui.state.model.PlaylistUiModel

@ExperimentalHorologistMediaUiApi
@Composable
public fun <T> PlaylistsScreen(
    playlists: List<T>,
    playlistContent: @Composable (playlist: T) -> Unit,
    scalingLazyListState: ScalingLazyListState,
    modifier: Modifier = Modifier
) {
    PlaylistsScreen(
        playlistsScreenState = PlaylistsScreenState.Loaded(playlists),
        playlistContent = playlistContent,
        scalingLazyListState = scalingLazyListState,
        modifier = modifier
    )
}

@ExperimentalHorologistMediaUiApi
@Composable
public fun <T> PlaylistsScreen(
    playlistsScreenState: PlaylistsScreenState<T>,
    playlistContent: @Composable (playlist: T) -> Unit,
    scalingLazyListState: ScalingLazyListState,
    modifier: Modifier = Modifier,
    autoCentering: AutoCenteringParams? = AutoCenteringParams()
) {
    val focusRequester = rememberActiveFocusRequester()
    SectionedList(
        focusRequester = focusRequester,
        scalingLazyListState = scalingLazyListState,
        modifier = modifier,
        autoCentering = autoCentering
    ) {
        val sectionState = when (playlistsScreenState) {
            is PlaylistsScreenState.Loaded<T> -> {
                Section.State.Loaded(playlistsScreenState.playlistList)
            }

            is PlaylistsScreenState.Failed -> Section.State.Failed()
            is PlaylistsScreenState.Loading -> Section.State.Loading()
        }

        section(state = sectionState) {
            header {
                Title(
                    textId = R.string.horologist_browse_playlist_title,
                    modifier = Modifier.padding(bottom = 12.dp)
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

@ExperimentalHorologistMediaUiApi
@Composable
public fun PlaylistsScreen(
    playlistsScreenState: PlaylistsScreenState<PlaylistUiModel>,
    onPlaylistItemClick: (PlaylistUiModel) -> Unit,
    scalingLazyListState: ScalingLazyListState,
    modifier: Modifier = Modifier,
    autoCentering: AutoCenteringParams? = AutoCenteringParams(),
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
        scalingLazyListState = scalingLazyListState,
        modifier = modifier,
        autoCentering = autoCentering
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
