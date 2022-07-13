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

package com.google.android.horologist.mediasample.ui.library

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.res.stringResource
import androidx.wear.compose.material.ScalingLazyListState
import com.google.android.horologist.compose.layout.StateUtils.rememberStateWithLifecycle
import com.google.android.horologist.media.ui.screens.playlist.PlaylistScreen
import com.google.android.horologist.media.ui.screens.playlist.PlaylistScreenState
import com.google.android.horologist.mediasample.R
import com.google.android.horologist.mediasample.domain.model.Settings

@Composable
fun UampPlaylistsScreen(
    uampPlaylistsScreenViewModel: UampPlaylistsScreenViewModel,
    onPlaylistItemClick: () -> Unit,
    settingsState: Settings?,
    focusRequester: FocusRequester,
    scalingLazyListState: ScalingLazyListState,
    modifier: Modifier = Modifier,
) {
    val uiState by rememberStateWithLifecycle(uampPlaylistsScreenViewModel.uiState)

    val playlistScreenState = when (uiState) {

        is UampPlaylistsScreenViewModel.UiState.Loading -> PlaylistScreenState.Loading

        is UampPlaylistsScreenViewModel.UiState.Loaded -> {
            val items = (uiState as UampPlaylistsScreenViewModel.UiState.Loaded).items

            PlaylistScreenState.Loaded(
                items.map {
                    PlaylistUiModelMapper.map(
                        mediaItemUiModel = it,
                        defaultTitle = stringResource(id = R.string.horologist_no_title),
                        shouldMapArtworkUri = settingsState?.showArtworkOnChip == true
                    )
                }
            )
        }
    }

    PlaylistScreen(
        playlistScreenState = playlistScreenState,
        onPlaylistItemClick = { selected ->
            val item =
                (uiState as? UampPlaylistsScreenViewModel.UiState.Loaded)?.items?.find {
                    it.title == selected.title
                }

            if (item != null) {
                uampPlaylistsScreenViewModel.play(item)
            }
            onPlaylistItemClick()
        },
        focusRequester = focusRequester,
        scalingLazyListState = scalingLazyListState,
        modifier = modifier,
    )
}
