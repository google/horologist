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

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.wear.compose.material3.AlertDialog
import androidx.wear.compose.material3.Icon
import androidx.wear.compose.material3.MaterialTheme
import androidx.wear.compose.material3.Text
import com.google.android.horologist.media.ui.material3.screens.playlists.PlaylistsScreen
import com.google.android.horologist.media.ui.material3.screens.playlists.PlaylistsScreenState
import com.google.android.horologist.media.ui.state.model.PlaylistUiModel
import com.google.android.horologist.mediasample.R

@Composable
fun UampPlaylistsScreen(
    uampPlaylistsScreenViewModel: UampPlaylistsScreenViewModel,
    onPlaylistItemClick: (PlaylistUiModel) -> Unit,
    onErrorDialogCancelClick: () -> Unit,
) {
    val uiState by uampPlaylistsScreenViewModel.uiState.collectAsStateWithLifecycle()

    val modifiedState = when (uiState) {
        is PlaylistsScreenState.Loaded -> {
            val modifiedPlaylistList = (uiState as PlaylistsScreenState.Loaded).playlistList.map {
                it.takeIf { it.title.isNotEmpty() }
                    ?: it.copy(title = stringResource(id = R.string.no_title))
            }

            PlaylistsScreenState.Loaded(modifiedPlaylistList)
        }

        PlaylistsScreenState.Failed,
        PlaylistsScreenState.Loading,
        -> uiState
    }

    PlaylistsScreen(
        playlistsScreenState = modifiedState,
        onPlaylistItemClick = {
            onPlaylistItemClick(it)
        },
    )

    // b/242302037 - it should stop listening to uiState emissions while dialog is presented
    if (modifiedState == PlaylistsScreenState.Failed) {
        AlertDialog(
            visible = true,
            onDismissRequest = onErrorDialogCancelClick,
            title = {
                Text(
                    text = stringResource(R.string.playlists_no_playlists),
                    color = MaterialTheme.colorScheme.onBackground,
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.titleSmall,
                )
            },
            icon = {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription =
                        stringResource(id = R.string.playlists_failed_dialog_cancel_button_content_description),
                )
            },
        )
    }
}
