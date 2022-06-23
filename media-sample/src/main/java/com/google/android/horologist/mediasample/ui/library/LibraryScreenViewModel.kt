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
import com.google.android.horologist.media.model.MediaItem
import com.google.android.horologist.media.repository.PlayerRepository
import com.google.android.horologist.media.ui.state.mapper.MediaItemUiModelMapper
import com.google.android.horologist.media.ui.state.model.MediaItemUiModel
import com.google.android.horologist.mediasample.catalog.UampService
import com.google.android.horologist.mediasample.di.MediaApplicationContainer
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class LibraryScreenViewModel(
    uampService: UampService,
    private val playerRepository: PlayerRepository
) : ViewModel() {
    fun play(mediaItemUiModel: MediaItemUiModel) {
        val mediaItems = items.value

        if (mediaItems != null) {
            playerRepository.setMediaItems(mediaItems)
            playerRepository.prepare()
            playerRepository.play(mediaItems.indexOfFirst { it.id == mediaItemUiModel.id }
                .coerceAtLeast(0))
        } else {
            // TODO warning
        }
    }

    val items: StateFlow<List<MediaItem>?> = flow {
        emit(uampService.catalog().music.map {
            it.toMediaItem()
        })
    }.stateIn(viewModelScope, started = SharingStarted.Eagerly, initialValue = null)

    val uiState = items.map { items ->
        UiState(items?.map { MediaItemUiModelMapper.map(it) })
    }.stateIn(viewModelScope, started = SharingStarted.Eagerly, initialValue = UiState())

    data class UiState(
        val items: List<MediaItemUiModel>? = null,
    )

    companion object {
        val Factory = viewModelFactory {
            initializer {
                LibraryScreenViewModel(
                    uampService = this[MediaApplicationContainer.UampServiceKey]!!,
                    playerRepository = this[MediaApplicationContainer.PlayerRepositoryImplKey]!!
                )
            }
        }
    }
}
