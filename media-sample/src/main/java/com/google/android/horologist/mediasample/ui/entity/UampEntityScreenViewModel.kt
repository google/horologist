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
import com.google.android.horologist.media.repository.PlayerRepository
import com.google.android.horologist.media.ui.navigation.NavigationScreens
import com.google.android.horologist.media.ui.screens.entity.EntityScreenState
import com.google.android.horologist.mediasample.domain.PlaylistDownloadRepository
import com.google.android.horologist.mediasample.domain.model.MediaDownload
import com.google.android.horologist.mediasample.domain.model.PlaylistDownload
import com.google.android.horologist.mediasample.ui.mapper.PlaylistUiModelMapper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class UampEntityScreenViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val playlistDownloadRepository: PlaylistDownloadRepository,
    private val playerRepository: PlayerRepository,
) : ViewModel() {
    private val playlistId: String = savedStateHandle[NavigationScreens.Collection.id]!!
    private val playlistName: String = savedStateHandle[NavigationScreens.Collection.name]!!

    private val playlistDownload: StateFlow<PlaylistDownload?> =
        playlistDownloadRepository.get(playlistId)
            .stateIn(viewModelScope, started = SharingStarted.Eagerly, initialValue = null)

    val uiState: StateFlow<EntityScreenState> = playlistDownload.map { playlistDownload ->
        if (playlistDownload != null) {
            EntityScreenState.Loaded(
                playlistUiModel = PlaylistUiModelMapper.map(playlistDownload.playlist),
                downloadList = playlistDownload.mediaList.map(DownloadMediaUiModelMapper::map),
                downloading = playlistDownload.mediaList.any { (_, status) -> status == MediaDownload.Status.InProgress },
            )
        } else {
            EntityScreenState.Loading(playlistName)
        }
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        EntityScreenState.Loading(playlistName)
    )

    fun play() {
        play(shuffled = false)
    }

    fun shufflePlay() {
        play(shuffled = true)
    }

    private fun play(shuffled: Boolean) {
        playlistDownload.value?.let { playlistDownload ->
            playerRepository.setShuffleModeEnabled(shuffled)

            playerRepository.setMediaList(playlistDownload.playlist.mediaList)
            playerRepository.prepare()
            playerRepository.play()
        }
    }

    fun download() = playlistDownload.value?.let { playlistDownloadRepository.download(it.playlist) }
}
