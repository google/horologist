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
import com.google.android.horologist.media.ui.snackbar.SnackbarManager
import com.google.android.horologist.media.ui.snackbar.UiMessage
import com.google.android.horologist.media3.logging.ErrorReporter
import com.google.android.horologist.media3.offload.AudioOffloadManager
import com.google.android.horologist.media3.offload.AudioOffloadStrategy
import com.google.android.horologist.media3.offload.BackgroundAudioOffloadStrategy
import com.google.android.horologist.mediasample.domain.SettingsRepository
import com.google.android.horologist.mediasample.domain.model.Settings
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsScreenViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository,
    private val snackbarManager: SnackbarManager,
    private val offloadManager: AudioOffloadManager,
    private val logger: ErrorReporter,
) : ViewModel() {
    val uiState: StateFlow<UiState> = settingsRepository.settingsFlow.map {
        UiState(
            showTimeTextInfo = it.showTimeTextInfo,
            podcastControls = it.podcastControls,
            loadItemsAtStartup = it.loadItemsAtStartup,
            artworkGradient = it.artworkGradient,
            offloadMode = it.offloadMode,
            animated = it.animated,
            debugOffload = it.debugOffload,
            writable = true
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = UiState(writable = false)
    )

    data class UiState(
        val showTimeTextInfo: Boolean = false,
        val podcastControls: Boolean = false,
        val loadItemsAtStartup: Boolean = true,
        val artworkGradient: Boolean = true,
        val writable: Boolean = false,
        val showArtworkOnChip: Boolean = true,
        val animated: Boolean = true,
        val debugOffload: Boolean = false,
        val offloadMode: Settings.OffloadMode = Settings.OffloadMode.Background
    )

    fun setShowTimeTextInfo(enabled: Boolean) {
        viewModelScope.launch {
            settingsRepository.writeShowTimeTextInfo(enabled)
        }
    }

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

    fun setAnimated(enabled: Boolean) {
        viewModelScope.launch {
            settingsRepository.writeAnimated(enabled)
        }
    }

    fun setDebugOffload(enabled: Boolean) {
        viewModelScope.launch {
            settingsRepository.writeDebugOffload(enabled)
        }
    }

    fun setOffloadMode(mode: Settings.OffloadMode) {
        viewModelScope.launch {
            settingsRepository.writeOffloadMode(mode)
            val strategy = when (mode) {
                Settings.OffloadMode.Background -> BackgroundAudioOffloadStrategy(logger)
                Settings.OffloadMode.Always -> AudioOffloadStrategy.Always
                Settings.OffloadMode.Never -> AudioOffloadStrategy.Never
            }
            offloadManager.setOffloadStrategy(strategy)
        }
    }

    fun logout() {
        // TODO login and logout functionality
    }

    fun showDialog(message: String) {
        snackbarManager.showMessage(
            UiMessage(
                message = message,
                error = true
            )
        )
    }
}
