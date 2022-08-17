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
import androidx.compose.ui.focus.FocusRequester
import androidx.wear.compose.material.ScalingLazyListState
import com.google.android.horologist.compose.layout.StateUtils
import com.google.android.horologist.media.ui.screens.entity.PlaylistDownloadScreen
import com.google.android.horologist.media.ui.state.model.DownloadMediaUiModel
import com.google.android.horologist.media.ui.state.model.PlaylistUiModel

@Composable
fun UampEntityScreen(
    playlistName: String,
    uampEntityScreenViewModel: UampEntityScreenViewModel,
    onDownloadItemClick: (DownloadMediaUiModel) -> Unit,
    onShuffleClick: (PlaylistUiModel) -> Unit,
    onPlayClick: (PlaylistUiModel) -> Unit,
    focusRequester: FocusRequester,
    scalingLazyListState: ScalingLazyListState
) {
    val uiState by StateUtils.rememberStateWithLifecycle(flow = uampEntityScreenViewModel.uiState)

    PlaylistDownloadScreen(
        playlistName = playlistName,
        playlistDownloadScreenState = uiState,
        onDownloadClick = {
            uampEntityScreenViewModel.download()
        },
        onDownloadItemClick = {
            uampEntityScreenViewModel.play(it.mediaUiModel.id)
            onDownloadItemClick(it)
        },
        onShuffleClick = {
            uampEntityScreenViewModel.shufflePlay()
            onShuffleClick(it)
        },
        onPlayClick = {
            uampEntityScreenViewModel.play()
            onPlayClick(it)
        },
        focusRequester = focusRequester,
        scalingLazyListState = scalingLazyListState
    )
}
