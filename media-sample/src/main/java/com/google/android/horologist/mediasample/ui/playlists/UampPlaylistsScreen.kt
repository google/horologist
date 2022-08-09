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

package com.google.android.horologist.mediasample.ui.playlists

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.res.stringResource
import androidx.wear.compose.material.ScalingLazyListState
import com.google.android.horologist.compose.layout.StateUtils.rememberStateWithLifecycle
import com.google.android.horologist.media.ui.screens.playlist.PlaylistsScreen
import com.google.android.horologist.media.ui.screens.playlist.PlaylistsScreenState
import com.google.android.horologist.media.ui.state.model.PlaylistUiModel
import com.google.android.horologist.mediasample.R

@Composable
fun UampPlaylistsScreen(
    uampPlaylistsScreenViewModel: UampPlaylistsScreenViewModel,
    onPlaylistItemClick: (PlaylistUiModel) -> Unit,
    focusRequester: FocusRequester,
    scalingLazyListState: ScalingLazyListState,
    modifier: Modifier = Modifier
) {
    val uiState by rememberStateWithLifecycle(uampPlaylistsScreenViewModel.uiState)

    val modifiedState = when (uiState) {
        is PlaylistsScreenState.Loaded -> {
            val modifiedPlaylistList = (uiState as PlaylistsScreenState.Loaded).playlistList.map {
                it.takeIf { it.title.isNotEmpty() }
                    ?: it.copy(title = stringResource(id = R.string.no_title))
            }

            PlaylistsScreenState.Loaded(modifiedPlaylistList)
        }
        else -> uiState
    }

    PlaylistsScreen(
        playlistsScreenState = modifiedState,
        onPlaylistItemClick = {
            onPlaylistItemClick(it)
        },
        focusRequester = focusRequester,
        scalingLazyListState = scalingLazyListState,
        modifier = modifier
    )
}
