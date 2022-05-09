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

package com.google.android.horologist.sample.media

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.google.android.horologist.media.ui.state.PlayerViewModel

class MediaPlayerScreenViewModel(
    playerRepository: PlayerRepositoryImpl
) : PlayerViewModel(playerRepository) {

    init {
        playerRepository.setCoroutineScope(viewModelScope)
        playerRepository.fetchData()
    }

    class Factory(
        private val playerRepositoryFactory: PlayerRepositoryImpl.Factory
    ) : ViewModelProvider.Factory {

        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
            if (modelClass.isAssignableFrom(MediaPlayerScreenViewModel::class.java)) {
                return MediaPlayerScreenViewModel(playerRepositoryFactory.create()) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
