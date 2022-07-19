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

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.google.android.horologist.media.repository.PlayerRepository
import com.google.android.horologist.media.ui.screens.entity.EntityScreenState
import com.google.android.horologist.mediasample.di.MediaApplicationContainer
import com.google.android.horologist.mediasample.domain.PlaylistDownloadRepository
import com.google.android.horologist.mediasample.domain.PlaylistRepository
import com.google.android.horologist.mediasample.domain.model.Playlist
import com.google.android.horologist.mediasample.domain.model.PlaylistDownload
import com.google.android.horologist.mediasample.ui.mapper.PlaylistUiModelMapper
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class UampEntityScreenViewModel(
    private val playlistId: String,
    private val playlistName: String,
    private val playlistRepository: PlaylistRepository,
    private val playlistDownloadRepository: PlaylistDownloadRepository,
    private val playerRepository: PlayerRepository,
) : ViewModel() {

    private lateinit var playlistCached: Playlist

    val uiState: StateFlow<EntityScreenState> = flow<EntityScreenState> {

        playlistRepository.getPlaylist(playlistId)?.let { playlist ->
            playlistCached = playlist

            emitAll(
                playlistDownloadRepository.get(playlist)
                    .map {
                        EntityScreenState.Loaded(
                            playlistUiModel = PlaylistUiModelMapper.map(it.playlist),
                            downloadList = DownloadMediaItemUiModelMapper.map(it.mediaList),
                            downloading = it.mediaList.any { pair -> pair.second == PlaylistDownload.Status.InProgress },
                        )
                    }
            )
        }
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        EntityScreenState.Loading(playlistName)
    )

    fun play() {
        playerRepository.setShuffleModeEnabled(false)
        playerRepository.setMediaItems(playlistCached.mediaItems)
        playerRepository.prepare()
        playerRepository.play()
    }

    fun shufflePlay() {
        playerRepository.setShuffleModeEnabled(true)
        playerRepository.setMediaItems(playlistCached.mediaItems)
        playerRepository.prepare()
        playerRepository.play()
    }

    companion object {
        fun getFactory(playlistId: String, playlistName: String) = viewModelFactory {
            initializer {
                UampEntityScreenViewModel(
                    playlistId = playlistId,
                    playlistName = playlistName,
                    playlistRepository = this[MediaApplicationContainer.PlaylistRepositoryKey]!!,
                    playlistDownloadRepository = this[MediaApplicationContainer.PlaylistDownloadRepositoryKey]!!,
                    playerRepository = this[MediaApplicationContainer.PlayerRepositoryImplKey]!!,
                )
            }
        }
    }
}
