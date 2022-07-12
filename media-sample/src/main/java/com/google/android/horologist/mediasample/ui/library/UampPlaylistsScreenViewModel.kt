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

package com.google.android.horologist.mediasample.ui.library

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.google.android.horologist.media.repository.PlayerRepository
import com.google.android.horologist.media.ui.state.model.PlaylistUiModel
import com.google.android.horologist.mediasample.di.MediaApplicationContainer
import com.google.android.horologist.mediasample.domain.PlaylistRepository
import com.google.android.horologist.mediasample.domain.SettingsRepository
import com.google.android.horologist.mediasample.domain.model.Playlist
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class UampPlaylistsScreenViewModel(
    private val playlistRepository: PlaylistRepository,
    private val playerRepository: PlayerRepository,
    private val settingsRepository: SettingsRepository,
) : ViewModel() {

    private val playlists: StateFlow<List<Playlist>?> = playlistRepository.getPlaylists()
        .stateIn(viewModelScope, started = SharingStarted.Eagerly, initialValue = null)

    val uiState = combine(playlists, settingsRepository.settingsFlow) { playlists, settings ->
        Pair(playlists, settings)
    }.map { result ->
        val playlistList = result.first
        val settings = result.second

        if (playlistList != null) {
            UiState.Loaded(
                playlistList.map {
                    PlaylistUiModelMapper.map(
                        playlist = it,
                        shouldMapArtworkUri = settings.showArtworkOnChip
                    )
                }
            )
        } else {
            // it should only be null in the initial state
            UiState.Loading
        }
    }.stateIn(viewModelScope, started = SharingStarted.Eagerly, initialValue = UiState.Loading)

    fun play(playlistId: String) {
        val playlistList = playlists.value

        if (playlistList != null) {
            val playlist = playlistList.find { it.id == playlistId }
                ?: playlistList.first()

            playerRepository.setMediaItems(playlist.mediaItems)
            playerRepository.prepare()
            playerRepository.play()
        } else {
            // TODO warning
        }
    }

    sealed class UiState {
        object Loading : UiState()

        data class Loaded(
            val items: List<PlaylistUiModel>
        ) : UiState()
    }

    companion object {
        val Factory = viewModelFactory {
            initializer {
                UampPlaylistsScreenViewModel(
                    playlistRepository = this[MediaApplicationContainer.PlaylistRepositoryKey]!!,
                    playerRepository = this[MediaApplicationContainer.PlayerRepositoryImplKey]!!,
                    settingsRepository = this[MediaApplicationContainer.SettingsRepositoryKey]!!,
                )
            }
        }
    }
}
