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

package com.google.android.horologist.mediasample.ui.entity

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.android.horologist.media.ui.screens.entity.PlaylistStreamingScreen
import com.google.android.horologist.media.ui.state.model.DownloadMediaUiModel
import com.google.android.horologist.media.ui.state.model.PlaylistUiModel

@Composable
fun UampStreamingPlaylistScreen(
    playlistName: String,
    viewModel: UampStreamingPlaylistScreenViewModel,
    onDownloadItemClick: (DownloadMediaUiModel) -> Unit,
    onShuffleClick: (PlaylistUiModel?) -> Unit,
    onPlayClick: (PlaylistUiModel?) -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    PlaylistStreamingScreen(
        playlistName = playlistName,
        playlistDownloadScreenState = uiState,
        onShuffleButtonClick = {
            viewModel.shufflePlay()
            onShuffleClick(null)
        },
        onPlayButtonClick = {
            viewModel.play()
            onPlayClick(null)
        },
        onPlayItemClick = {
            viewModel.play(it.id)
            onDownloadItemClick(it)
        },
    )
}
