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
import com.google.android.horologist.media.model.MediaItem
import com.google.android.horologist.media.repository.PlayerRepository
import com.google.android.horologist.media.ui.snackbar.SnackbarManager
import com.google.android.horologist.media.ui.snackbar.UiMessage
import com.google.android.horologist.mediasample.domain.SettingsRepository
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
    private val playerRepository: PlayerRepository
) : ViewModel() {
    val uiState: StateFlow<UiState> = settingsRepository.settingsFlow.map {
        UiState(
            showTimeTextInfo = it.showTimeTextInfo,
            podcastControls = it.podcastControls,
            loadItemsAtStartup = it.loadItemsAtStartup,
            artworkGradient = it.artworkGradient,
            showArtworkOnChip = it.showArtworkOnChip,
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
        val debugOffload: Boolean = false
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

    fun setShowArtworkOnChip(enabled: Boolean) {
        viewModelScope.launch {
            settingsRepository.writeShowArtworkOnChip(enabled)
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

    fun playGapless1() {
        val mediaItems = listOf(
            "https://www2.iis.fraunhofer.de/AAC/gapless-sweep_part1_iis.m4a?delay=1600&padding=106",
            "https://www2.iis.fraunhofer.de/AAC/gapless-sweep_part2_iis.m4a?delay=110&padding=1024"
        ).mapIndexed { i, it ->
            MediaItem(
                id = i.toString(),
                uri = it,
                title = "Fraunhofer Gapless $i",
                artist = "fraunhofer",
                artworkUri = "https://www2.iis.fraunhofer.de/AAC/logo-fraunhofer.gif",
            )
        }

        playerRepository.setMediaItems(mediaItems)
        playerRepository.prepare()
        playerRepository.play()
    }

    fun playGapless2() {
        val mediaItems = listOf(
            "https://storage.googleapis.com/exoplayer-test-media-internal-63834241aced7884c2544af1a3452e01/m4a/gapless-asot-10.m4a",
            "https://storage.googleapis.com/exoplayer-test-media-internal-63834241aced7884c2544af1a3452e01/m4a/gapless-asot-11.m4a"
        ).mapIndexed { i, it ->
            MediaItem(
                id = i.toString(),
                uri = it,
                title = "Gapless $i",
                artist = "unknown",
                artworkUri = "https://www2.iis.fraunhofer.de/AAC/logo-fraunhofer.gif",
            )
        }

        playerRepository.setMediaItems(mediaItems)
        playerRepository.prepare()
        playerRepository.play()
    }

    fun playGapless3() {
        val mediaItems = listOf(
            "https://storage.googleapis.com/exoplayer-test-media-internal-63834241aced7884c2544af1a3452e01/m4a/gapless-asot-10-stripped.m4a",
            "https://storage.googleapis.com/exoplayer-test-media-internal-63834241aced7884c2544af1a3452e01/m4a/gapless-asot-11-stripped.m4a"
        ).mapIndexed { i, it ->
            MediaItem(
                id = i.toString(),
                uri = it,
                title = "Gapless (stripped) $i",
                artist = "unknown",
                artworkUri = "https://www2.iis.fraunhofer.de/AAC/logo-fraunhofer.gif",
            )
        }

        playerRepository.setMediaItems(mediaItems)
        playerRepository.prepare()
        playerRepository.play()
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
