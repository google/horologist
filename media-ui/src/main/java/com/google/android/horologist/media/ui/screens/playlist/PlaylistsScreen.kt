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

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.ScalingLazyColumn
import androidx.wear.compose.material.ScalingLazyListState
import com.google.android.horologist.compose.layout.RequestFocusWhenActive
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
public fun PlaylistsScreen(
    playlistsScreenState: PlaylistsScreenState,
    onPlaylistItemClick: (PlaylistUiModel) -> Unit,
    scalingLazyListState: ScalingLazyListState,
    modifier: Modifier = Modifier,
    playlistItemArtworkPlaceholder: Painter? = null
) {
    val focusRequester = remember {
        FocusRequester()
    }
    RequestFocusWhenActive(focusRequester = focusRequester)

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

        if (playlistsScreenState is PlaylistsScreenState.Loaded) {
            items(count = playlistsScreenState.playlistList.size) { index ->

                val playlist = playlistsScreenState.playlistList[index]

                StandardChip(
                    label = playlist.title,
                    onClick = { onPlaylistItemClick(playlist) },
                    icon = playlist.artworkUri,
                    largeIcon = true,
                    placeholder = playlistItemArtworkPlaceholder,
                    chipType = StandardChipType.Secondary,
                )
            }
        } else if (playlistsScreenState is PlaylistsScreenState.Loading) {
            items(count = 4) {
                SecondaryPlaceholderChip()
            }
        }
    }
}

/**
 * Represents the state of [PlaylistsScreen].
 */
@ExperimentalHorologistMediaUiApi
public sealed class PlaylistsScreenState {

    public object Loading : PlaylistsScreenState()

    public data class Loaded(
        val playlistList: List<PlaylistUiModel>,
    ) : PlaylistsScreenState()

    public data class Failed(
        @StringRes val errorMessage: Int
    ) : PlaylistsScreenState()
}
