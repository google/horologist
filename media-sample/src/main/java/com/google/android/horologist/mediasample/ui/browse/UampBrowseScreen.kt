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

package com.google.android.horologist.mediasample.ui.browse

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.wear.compose.material.ScalingLazyListState
import com.google.android.horologist.media.ui.screens.browse.PlaylistDownloadBrowseScreen
import com.google.android.horologist.media.ui.state.model.PlaylistDownloadUiModel
import com.google.android.horologist.mediasample.R

@Composable
fun UampBrowseScreen(
    uampBrowseScreenViewModel: UampBrowseScreenViewModel,
    onDownloadItemClick: (PlaylistDownloadUiModel) -> Unit,
    onPlaylistsClick: () -> Unit,
    onSettingsClick: () -> Unit,
    focusRequester: FocusRequester,
    scalingLazyListState: ScalingLazyListState,
    modifier: Modifier = Modifier
) {
    val uiState by uampBrowseScreenViewModel.uiState.collectAsStateWithLifecycle()

    PlaylistDownloadBrowseScreen(
        browseScreenState = uiState,
        onDownloadItemClick = onDownloadItemClick,
        onDownloadItemInProgressClick = {
            // TODO: https://github.com/google/horologist/issues/678
        },
        onPlaylistsClick = onPlaylistsClick,
        onSettingsClick = onSettingsClick,
        focusRequester = focusRequester,
        scalingLazyListState = scalingLazyListState,
        modifier = modifier,
        onDownloadItemInProgressClickActionLabel = stringResource(id = R.string.browse_download_cancel_action_label)
    )
}
