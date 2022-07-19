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
import com.google.android.horologist.media.ui.state.model.PlaylistUiModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.google.android.horologist.media.ui.screens.playlist.PlaylistScreenState
import com.google.android.horologist.mediasample.di.MediaApplicationContainer
import com.google.android.horologist.mediasample.domain.PlaylistRepository
import com.google.android.horologist.mediasample.domain.SettingsRepository
import com.google.android.horologist.mediasample.domain.model.Playlist
import com.google.android.horologist.mediasample.ui.mapper.PlaylistUiModelMapper
import dagger.hilt.android.lifecycle.HiltViewModel
import com.google.android.horologist.mediasample.ui.mapper.PlaylistUiModelMapper
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class UampPlaylistsScreenViewModel @Inject constructor(
    private val playlistRepository: PlaylistRepository,
    private val settingsRepository: SettingsRepository,
) : ViewModel() {

    val uiState = combine(
        playlistRepository.getPlaylists(),
        settingsRepository.settingsFlow
    ) { playlists, settings ->
        Pair(playlists, settings)
    }.map { (playlists, settings) ->
        PlaylistScreenState.Loaded(
            playlists.map {
                PlaylistUiModelMapper.map(
                    playlist = it,
                    shouldMapArtworkUri = settings.showArtworkOnChip
                )
            }
        )
    }.stateIn(
        viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = PlaylistScreenState.Loading
    )
}
