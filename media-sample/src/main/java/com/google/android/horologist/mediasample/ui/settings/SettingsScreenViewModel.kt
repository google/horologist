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

package com.google.android.horologist.mediasample.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.google.android.horologist.media.ui.snackbar.SnackbarManager
import com.google.android.horologist.media.ui.snackbar.SnackbarViewModel
import com.google.android.horologist.media.ui.snackbar.UiMessage
import com.google.android.horologist.mediasample.di.MediaApplicationContainer
import com.google.android.horologist.mediasample.domain.SettingsRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsScreenViewModel(
    private val settingsRepository: SettingsRepository,
    private val snackbarManager: SnackbarManager,
) : ViewModel() {
    val uiState: StateFlow<UiState> = settingsRepository.settingsFlow.map {
        UiState(
            podcastControls = it.podcastControls,
            loadItemsAtStartup = it.loadItemsAtStartup,
            artworkGradient = it.artworkGradient,
            writable = true
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = UiState(writable = false)
    )

    data class UiState(
        val podcastControls: Boolean = false,
        val loadItemsAtStartup: Boolean = true,
        val artworkGradient: Boolean = true,
        val writable: Boolean = false,
    )

    fun setPodcastControls(enabled: Boolean) {
        viewModelScope.launch {
            settingsRepository.writePodcastControls(enabled)
        }
    }

    fun setLoadItemsAtStartup(enabled: Boolean) {
        viewModelScope.launch {
            settingsRepository.writeLoadItemsAtStartup(enabled)
        }
    }

    fun setArtworkGradient(enabled: Boolean) {
        viewModelScope.launch {
            settingsRepository.writeArtworkGradient(enabled)
        }
    }

    fun logout() {
        // TODO login and logout functionality
    }

    fun showDialog() {
        snackbarManager.showMessage(UiMessage(
            message = "An error occurred",
            error = true
        ))
    }

    companion object {
        val Factory = viewModelFactory {
            initializer {
                SettingsScreenViewModel(
                    settingsRepository = this[MediaApplicationContainer.SettingsRepositoryKey]!!,
                    snackbarManager = this[SnackbarViewModel.SnackbarManagerKey]!!,
                )
            }
        }
    }
}
