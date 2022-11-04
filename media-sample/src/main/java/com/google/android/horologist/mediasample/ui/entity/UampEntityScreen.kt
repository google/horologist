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

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.ButtonDefaults
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.ScalingLazyListState
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.dialog.Alert
import androidx.wear.compose.material.dialog.Dialog
import com.google.android.horologist.base.ui.components.AlertDialog
import com.google.android.horologist.compose.layout.StateUtils
import com.google.android.horologist.media.ui.screens.entity.PlaylistDownloadScreen
import com.google.android.horologist.media.ui.screens.entity.PlaylistDownloadScreenState
import com.google.android.horologist.media.ui.state.model.DownloadMediaUiModel
import com.google.android.horologist.media.ui.state.model.PlaylistUiModel
import com.google.android.horologist.mediasample.R

@Composable
fun UampEntityScreen(
    playlistName: String,
    uampEntityScreenViewModel: UampEntityScreenViewModel,
    onDownloadItemClick: (DownloadMediaUiModel) -> Unit,
    onShuffleClick: (PlaylistUiModel) -> Unit,
    onPlayClick: (PlaylistUiModel) -> Unit,
    onErrorDialogCancelClick: () -> Unit,
    focusRequester: FocusRequester,
    scalingLazyListState: ScalingLazyListState
) {
    val uiState by StateUtils.rememberStateWithLifecycle(flow = uampEntityScreenViewModel.uiState)

    var showCancelDownloadsDialog by rememberSaveable { mutableStateOf(false) }
    var showRemoveDownloadsDialog by rememberSaveable { mutableStateOf(false) }

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
            // TODO: https://github.com/google/horologist/issues/682
        },
        onShuffleButtonClick = {
            uampEntityScreenViewModel.shufflePlay()
            onShuffleClick(it)
        },
        onPlayButtonClick = {
            uampEntityScreenViewModel.play()
            onPlayClick(it)
        },
        focusRequester = focusRequester,
        scalingLazyListState = scalingLazyListState,
        onDownloadCompletedButtonClick = {
            showRemoveDownloadsDialog = true
        },
        onDownloadItemInProgressClickActionLabel = stringResource(id = R.string.entity_download_cancel_action_label)
    )

    // b/243381431 - it should stop listening to uiState emissions while dialog is presented
    if (uiState is PlaylistDownloadScreenState.Failed) {
        Dialog(
            showDialog = true,
            onDismissRequest = onErrorDialogCancelClick,
            scrollState = scalingLazyListState
        ) {
            Alert(
                title = {
                    Text(
                        text = stringResource(R.string.entity_no_playlists),
                        color = MaterialTheme.colors.onBackground,
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.title3
                    )
                }
            ) {
                item {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Button(
                            onClick = onErrorDialogCancelClick,
                            colors = ButtonDefaults.secondaryButtonColors()
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = stringResource(id = R.string.entity_failed_dialog_cancel_button_content_description),
                                modifier = Modifier
                                    .size(24.dp)
                                    .wrapContentSize(align = Alignment.Center)
                            )
                        }
                    }
                }
            }
        }
    }

    AlertDialog(
        body = stringResource(R.string.entity_dialog_cancel_downloads),
        onCancelButtonClick = {
            showCancelDownloadsDialog = false
        },
        onOKButtonClick = {
            showCancelDownloadsDialog = false
            uampEntityScreenViewModel.remove()
        },
        showDialog = showCancelDownloadsDialog,
        scalingLazyListState = scalingLazyListState,
        okButtonContentDescription = stringResource(id = R.string.entity_dialog_proceed_button_content_description),
        cancelButtonContentDescription = stringResource(id = R.string.entity_dialog_cancel_button_content_description)
    )

    AlertDialog(
        body = stringResource(R.string.entity_dialog_remove_downloads, playlistName),
        onCancelButtonClick = {
            showRemoveDownloadsDialog = false
        },
        onOKButtonClick = {
            showRemoveDownloadsDialog = false
            uampEntityScreenViewModel.remove()
        },
        showDialog = showRemoveDownloadsDialog,
        scalingLazyListState = scalingLazyListState,
        okButtonContentDescription = stringResource(id = R.string.entity_dialog_proceed_button_content_description),
        cancelButtonContentDescription = stringResource(id = R.string.entity_dialog_cancel_button_content_description)
    )
}
