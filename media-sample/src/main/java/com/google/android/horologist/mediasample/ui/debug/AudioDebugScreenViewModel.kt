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
    private val playerRepository: PlayerRepository,
) : ViewModel() {
    val ticker = flow {
        while (true) {
            delay(1.seconds)
            emit(Unit)
        }
    }

    private val offloadTimesFlow = ticker.map {
        audioOffloadManager.snapOffloadTimes()
    }

    val uiState: StateFlow<UiState> = combine(
        audioOffloadManager.foreground,
        audioOffloadManager.format,
        audioOffloadManager.sleepingForOffload,
        audioOffloadManager.offloadSchedulingEnabled,
        offloadTimesFlow
    ) { foreground, format, sleepingForOffload, offloadSchedulingEnabled, offloadTimes ->
        UiState(
            foreground = foreground,
            format = format,
            times = offloadTimes,
            sleepingForOffload = sleepingForOffload,
            offloadSchedulingEnabled = offloadSchedulingEnabled
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = UiState(
            foreground = audioOffloadManager.foreground.value,
            format = audioOffloadManager.format.value,
            times = audioOffloadManager.snapOffloadTimes(),
            sleepingForOffload = audioOffloadManager.sleepingForOffload.value,
            offloadSchedulingEnabled = audioOffloadManager.offloadSchedulingEnabled.value
        )
    )

    data class UiState(
        val foreground: Boolean,
        val format: Format?,
        val times: OffloadTimes,
        val sleepingForOffload: Boolean,
        val offloadSchedulingEnabled: Boolean
    )
}
