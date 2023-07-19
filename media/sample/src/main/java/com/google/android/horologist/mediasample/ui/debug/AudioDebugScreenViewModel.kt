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

package com.google.android.horologist.mediasample.ui.debug

import android.media.AudioManager
import android.os.Build
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.Format
import androidx.media3.session.MediaBrowser
import androidx.media3.session.MediaController
import com.google.android.horologist.media.repository.PlayerRepository
import com.google.android.horologist.media3.offload.AudioOffloadManager
import com.google.android.horologist.media3.offload.AudioOffloadStatus
import com.google.android.horologist.media3.util.toAudioFormat
import com.google.android.horologist.mediasample.ui.AppConfig
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject
import javax.inject.Provider

@HiltViewModel
class AudioDebugScreenViewModel @Inject constructor(
    private val audioOffloadManager: Provider<AudioOffloadManager>,
    private val appConfig: AppConfig,
    playerRepository: PlayerRepository,
    private val mediaController: Deferred<MediaBrowser>,
) : ViewModel() {
    val mediaControllerFlow = flow {
        emit(null)
        emit(mediaController.await())
    }

    fun audioOffloadFlow(): Flow<AudioOffloadStatus> = if (appConfig.offloadEnabled) {
        audioOffloadManager.get().offloadStatus
    } else {
        flowOf(AudioOffloadStatus.Disabled)
    }

    val uiState: StateFlow<UiState?> = combine(
        audioOffloadFlow(),
        playerRepository.currentMedia,
        mediaControllerFlow,
    ) { audioOffloadStatus, currentMedia, mediaController ->

        UiState(
            currentTrack = currentMedia?.title,
            audioOffloadStatus = audioOffloadStatus,
            formatSupported = isFormatSupported(mediaController, audioOffloadStatus.format),
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = null,
    )

    public fun isFormatSupported(mediaController: MediaController?, format: Format?): Boolean? {
        val audioFormat = format?.toAudioFormat() ?: return null

        if (mediaController == null) {
            return null
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val audioAttributes = mediaController.audioAttributes.audioAttributesV21.audioAttributes
            return AudioManager.isOffloadedPlaybackSupported(audioFormat, audioAttributes)
        }

        // Not supported before 30
        return false
    }

    data class UiState(
        val currentTrack: String?,
        val audioOffloadStatus: AudioOffloadStatus,
        val formatSupported: Boolean?,
    )
}
