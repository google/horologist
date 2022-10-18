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

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.horologist.media.model.PlaylistDownload
import com.google.android.horologist.media.repository.PlayerRepository
import com.google.android.horologist.media.repository.PlaylistDownloadRepository
import com.google.android.horologist.media.ui.navigation.NavigationScreens
import com.google.android.horologist.media.ui.screens.entity.PlaylistDownloadScreenState
import com.google.android.horologist.media.ui.screens.entity.createPlaylistDownloadScreenStateLoaded
import com.google.android.horologist.media.ui.state.mapper.DownloadMediaUiModelMapper
import com.google.android.horologist.media.ui.state.mapper.PlaylistUiModelMapper
import com.google.android.horologist.media.ui.state.model.DownloadMediaUiModel
import com.google.android.horologist.media.ui.state.model.PlaylistUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class UampEntityScreenViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val playlistDownloadRepository: PlaylistDownloadRepository,
    private val playerRepository: PlayerRepository
) : ViewModel() {
    private val playlistId: String = savedStateHandle[NavigationScreens.Collection.id]!!

    private val playlistDownload: StateFlow<PlaylistDownload?> =
        playlistDownloadRepository.get(playlistId)
            .stateIn(viewModelScope, started = SharingStarted.Eagerly, initialValue = null)

    val uiState: StateFlow<PlaylistDownloadScreenState<PlaylistUiModel, DownloadMediaUiModel>> =
        playlistDownload.map { playlistDownload ->
            if (playlistDownload != null) {
                createPlaylistDownloadScreenStateLoaded(
                    playlistModel = PlaylistUiModelMapper.map(playlistDownload.playlist),
                    downloadMediaList = playlistDownload.mediaList.map(DownloadMediaUiModelMapper::map)
                )
            } else {
                PlaylistDownloadScreenState.Failed()
            }
        }.catch {
            emit(PlaylistDownloadScreenState.Failed())
        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            PlaylistDownloadScreenState.Loading()
        )

    fun play(mediaId: String? = null) {
        play(shuffled = false, mediaId = mediaId)
    }

    fun shufflePlay() {
        play(shuffled = true)
    }

    private fun play(shuffled: Boolean, mediaId: String? = null) {
        playlistDownload.value?.let { playlistDownload ->
            val index = playlistDownload.mediaList
                .indexOfFirst { it.media.id == mediaId }
                .coerceAtLeast(0)

            playerRepository.setShuffleModeEnabled(shuffled)
            playerRepository.setCurrentMediaListId(playlistId)
            playerRepository.setMediaListAndPlay(playlistDownload.playlist.mediaList, index)
        }
    }

    fun download() = playlistDownload.value?.let { playlistDownloadRepository.download(it.playlist) }

    fun remove() = playlistDownload.value?.let { playlistDownloadRepository.remove(it.playlist) }
}
