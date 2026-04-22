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

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.horologist.media.model.Playlist
import com.google.android.horologist.media.repository.PlaylistRepository
import com.google.android.horologist.media.ui.screens.browse.BrowseScreenState
import com.google.android.horologist.media.ui.state.mapper.PlaylistDownloadUiModelMapper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class UampBrowseScreenViewModel
    @Inject
    constructor(
        playlistRepository: PlaylistRepository,
    ) : ViewModel() {

        private val playlists: StateFlow<List<Playlist>?> = playlistRepository.getAllDownloaded()
            .stateIn(
                viewModelScope,
                started = SharingStarted.Eagerly,
                initialValue = null,
            )

        val uiState = playlists.map { playlists ->
            playlists?.let {
                BrowseScreenState.Loaded(it.map(PlaylistDownloadUiModelMapper::map))
            } ?: BrowseScreenState.Loading
        }.catch {
            BrowseScreenState.Failed
        }.stateIn(
            viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = BrowseScreenState.Loading,
        )
    }
