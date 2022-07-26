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

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.Format
import com.google.android.horologist.media.repository.PlayerRepository
import com.google.android.horologist.media3.offload.AudioError
import com.google.android.horologist.media3.offload.AudioOffloadManager
import com.google.android.horologist.media3.offload.OffloadTimes
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject
import kotlin.time.Duration.Companion.seconds

@HiltViewModel
class AudioDebugScreenViewModel @Inject constructor(
    private val audioOffloadManager: AudioOffloadManager,
    playerRepository: PlayerRepository,
) : ViewModel() {
    private val offloadTimesFlow = flow {
        while (true) {
            emit(Unit)
            delay(3.seconds)
        }
    }.map {
        audioOffloadManager.snapOffloadTimes()
    }

    private val formatDetails = combine(
        audioOffloadManager.format,
        playerRepository.currentMediaItem
    ) { format, currentMediaItem ->
        FormatDetails(
            format = format,
            formatSupported = audioOffloadManager.isFormatSupported(),
            currentTrack = currentMediaItem?.title
        )
    }

    private val offloadState = combine(
        audioOffloadManager.foreground,
        audioOffloadManager.sleepingForOffload,
        audioOffloadManager.offloadSchedulingEnabled,
    ) { foreground, sleepingForOffload, offloadSchedulingEnabled ->
        OffloadState(
            foreground = foreground,
            sleepingForOffload = sleepingForOffload,
            offloadSchedulingEnabled = offloadSchedulingEnabled,
        )
    }

    val uiState: StateFlow<UiState?> = combine(
        offloadState,
        formatDetails,
        offloadTimesFlow,
        audioOffloadManager.errors
    ) { offloadState, formatDetails, offloadTimes, errors ->
        UiState(
            offloadState = offloadState,
            formatDetails = formatDetails,
            times = offloadTimes,
            errors = errors
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = null
    )

    data class FormatDetails(
        val format: Format?,
        val formatSupported: Boolean?,
        val currentTrack: String?
    )

    data class OffloadState(
        val foreground: Boolean,
        val sleepingForOffload: Boolean,
        val offloadSchedulingEnabled: Boolean,
    )

    data class UiState(
        val times: OffloadTimes,
        val formatDetails: FormatDetails,
        val offloadState: OffloadState,
        val errors: List<AudioError>,
    )
}
