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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.android.horologist.compose.layout.ScalingLazyColumnState
import com.google.android.horologist.compose.layout.rememberResponsiveColumnState
import com.google.android.horologist.compose.material.AlertDialog
import com.google.android.horologist.media.ui.screens.entity.PlaylistDownloadScreen
import com.google.android.horologist.media.ui.screens.entity.PlaylistDownloadScreenState
import com.google.android.horologist.media.ui.state.model.DownloadMediaUiModel
import com.google.android.horologist.media.ui.state.model.PlaylistUiModel
import com.google.android.horologist.mediasample.R

@Composable
fun UampEntityScreen(
    columnState: ScalingLazyColumnState = rememberResponsiveColumnState(),
    playlistName: String,
    uampEntityScreenViewModel: UampEntityScreenViewModel,
    onDownloadItemClick: (DownloadMediaUiModel) -> Unit,
    onShuffleClick: (PlaylistUiModel) -> Unit,
    onPlayClick: (PlaylistUiModel) -> Unit,
    onErrorDialogCancelClick: () -> Unit,
) {
    val uiState by uampEntityScreenViewModel.uiState.collectAsStateWithLifecycle()

    var showCancelDownloadsDialog by rememberSaveable { mutableStateOf(false) }
    var showRemoveDownloadsDialog by rememberSaveable { mutableStateOf(false) }
    var showRemoveSingleMediaDownloadDialog by rememberSaveable { mutableStateOf(false) }

    var mediaIdToDelete: String? by rememberSaveable { mutableStateOf(null) }
    var mediaTitleToDelete: String by rememberSaveable { mutableStateOf("media title") }

    PlaylistDownloadScreen(
        playlistName = playlistName,
        playlistDownloadScreenState = uiState,
        onDownloadButtonClick = {
            uampEntityScreenViewModel.download()
        },
        onCancelDownloadButtonClick = {
            showCancelDownloadsDialog = true
        },
        onDownloadItemClick = {
            uampEntityScreenViewModel.play(it.id)
            onDownloadItemClick(it)
        },
        onDownloadItemInProgressClick = {
            mediaIdToDelete = it.id
            it.title?.let { title -> mediaTitleToDelete = title }
            showRemoveSingleMediaDownloadDialog = true
        },
        onShuffleButtonClick = {
            uampEntityScreenViewModel.shufflePlay()
            onShuffleClick(it)
        },
        onPlayButtonClick = {
            uampEntityScreenViewModel.play()
            onPlayClick(it)
        },
        columnState = columnState,
        onDownloadCompletedButtonClick = {
            showRemoveDownloadsDialog = true
        },
        onDownloadItemInProgressClickActionLabel = stringResource(id = R.string.entity_download_cancel_action_label),
    )

    // b/243381431 - it should stop listening to uiState emissions while dialog is presented
    if (uiState == PlaylistDownloadScreenState.Failed) {
        AlertDialog(
            message = stringResource(R.string.entity_no_playlists),
            onDismiss = onErrorDialogCancelClick,
            showDialog = true,
        )
    }

    AlertDialog(
        message = stringResource(R.string.entity_dialog_cancel_downloads),
        onCancel = {
            showCancelDownloadsDialog = false
        },
        onOk = {
            showCancelDownloadsDialog = false
            uampEntityScreenViewModel.remove()
        },
        showDialog = showCancelDownloadsDialog,
        okButtonContentDescription = stringResource(id = R.string.entity_dialog_proceed_button_content_description),
        cancelButtonContentDescription = stringResource(id = R.string.entity_dialog_cancel_button_content_description),
    )

    AlertDialog(
        message = stringResource(R.string.entity_dialog_remove_downloads, playlistName),
        onCancel = {
            showRemoveDownloadsDialog = false
        },
        onOk = {
            showRemoveDownloadsDialog = false
            uampEntityScreenViewModel.remove()
        },
        showDialog = showRemoveDownloadsDialog,
        okButtonContentDescription = stringResource(id = R.string.entity_dialog_proceed_button_content_description),
        cancelButtonContentDescription = stringResource(id = R.string.entity_dialog_cancel_button_content_description),
    )

    AlertDialog(
        message = stringResource(R.string.entity_dialog_remove_downloads, mediaTitleToDelete),
        onCancel = {
            showRemoveSingleMediaDownloadDialog = false
        },
        onOk = {
            showRemoveSingleMediaDownloadDialog = false
            mediaIdToDelete?.let { uampEntityScreenViewModel.removeMediaItem(it) }
        },
        showDialog = showRemoveSingleMediaDownloadDialog,
        okButtonContentDescription = stringResource(id = R.string.entity_dialog_proceed_button_content_description),
        cancelButtonContentDescription = stringResource(id = R.string.entity_dialog_cancel_button_content_description),

    )
}
