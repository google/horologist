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

package com.google.android.horologist.mediasample.complication

import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.wear.watchface.complications.datasource.ComplicationDataSourceUpdateRequester
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class DataUpdates(
    private val updater: ComplicationDataSourceUpdateRequester
) {
    data class State(
        val mediaItem: MediaItem?
    )

    val listener: Player.Listener = object : Player.Listener {
        override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
            _stateFlow.update {
                it.copy(mediaItem = mediaItem)
            }
            updater.requestUpdateAll()
        }
    }

    private val _stateFlow = MutableStateFlow(State(null))
    val stateFlow: StateFlow<State> = _stateFlow.asStateFlow()
}
