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

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.horologist.media.ui.screens.playlist.PlaylistScreenState
import com.google.android.horologist.media.ui.snackbar.SnackbarManager
import com.google.android.horologist.media.ui.snackbar.UiMessage
import com.google.android.horologist.mediasample.R
import com.google.android.horologist.mediasample.domain.PlaylistRepository
import com.google.android.horologist.mediasample.domain.model.Playlist
import com.google.android.horologist.mediasample.ui.mapper.PlaylistUiModelMapper
import com.google.android.horologist.mediasample.util.ResourceProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class UampPlaylistsScreenViewModel @Inject constructor(
    playlistRepository: PlaylistRepository,
    private val snackbarManager: SnackbarManager,
    private val resourceProvider: ResourceProvider
) : ViewModel() {

    val uiState: StateFlow<PlaylistScreenState> =
        playlistRepository.getAllPopulated().map<List<Playlist>, PlaylistScreenState> {
            PlaylistScreenState.Loaded(it.map(PlaylistUiModelMapper::map))
        }.catch { throwable ->
            when (throwable) {
                is IOException -> {
                    snackbarManager.showMessage(
                        UiMessage(
                            message = resourceProvider.getString(R.string.horologist_sample_network_error),
                            error = true
                        )
                    )
                    emit(PlaylistScreenState.Failed(R.string.horologist_sample_network_error))
                }
                else -> throw throwable
            }
        }.stateIn(
            viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = PlaylistScreenState.Loading
        )
}
