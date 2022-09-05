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
import com.google.android.horologist.media.repository.PlaylistRepository
import com.google.android.horologist.media.ui.screens.playlists.PlaylistsScreenState
import com.google.android.horologist.media.ui.state.mapper.PlaylistUiModelMapper
import com.google.android.horologist.media.ui.state.model.PlaylistUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class UampPlaylistsScreenViewModel @Inject constructor(
    playlistRepository: PlaylistRepository
) : ViewModel() {

    val uiState: StateFlow<PlaylistsScreenState<PlaylistUiModel>> =
        playlistRepository.getAll().map {
            if (it.isNotEmpty()) {
                PlaylistsScreenState.Loaded(it.map(PlaylistUiModelMapper::map))
            } else {
                PlaylistsScreenState.Failed()
            }
        }.catch {
            emit(PlaylistsScreenState.Failed())
        }.stateIn(
            viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = PlaylistsScreenState.Loading()
        )
}
