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

package com.google.android.horologist.media.ui.screens.playlist

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
import com.google.android.horologist.media.ui.components.base.SecondaryChip
import com.google.android.horologist.media.ui.components.base.SecondaryPlaceholderChip
import com.google.android.horologist.media.ui.components.base.Title
import com.google.android.horologist.media.ui.state.model.PlaylistUiModel

@ExperimentalHorologistMediaUiApi
@Composable
public fun PlaylistScreen(
    playlistScreenState: PlaylistScreenState,
    onPlaylistItemClick: (PlaylistUiModel) -> Unit,
    focusRequester: FocusRequester,
    scalingLazyListState: ScalingLazyListState,
    modifier: Modifier = Modifier,
    playlistItemArtworkPlaceholder: Painter? = null
) {
    ScalingLazyColumn(
        modifier = modifier
            .fillMaxSize()
            .scrollableColumn(focusRequester, scalingLazyListState),
        state = scalingLazyListState,
    ) {
        item {
            Title(
                textId = R.string.horologist_browse_playlist_title,
                modifier = Modifier.padding(bottom = 12.dp),
            )
        }

        if (playlistScreenState is PlaylistScreenState.Loaded) {
            items(count = playlistScreenState.playlistList.size) { index ->

                val playlist = playlistScreenState.playlistList[index]

                SecondaryChip(
                    label = playlist.title,
                    onClick = { onPlaylistItemClick(playlist) },
                    icon = playlist.artworkUri,
                    largeIcon = true,
                    placeholder = playlistItemArtworkPlaceholder
                )
            }
        } else if (playlistScreenState is PlaylistScreenState.Loading) {
            items(count = 4) {
                SecondaryPlaceholderChip()
            }
        }
    }
}

/**
 * Represents the state of [PlaylistScreen].
 */
@ExperimentalHorologistMediaUiApi
public sealed class PlaylistScreenState {

    public object Loading : PlaylistScreenState()

    public data class Loaded(
        val playlistList: List<PlaylistUiModel>,
    ) : PlaylistScreenState()
}
