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

package com.google.android.horologist.test.toolbox.testdoubles

import com.google.android.horologist.media.ExperimentalHorologistMediaApi
import com.google.android.horologist.media.model.Command
import com.google.android.horologist.media.model.Media
import com.google.android.horologist.media.model.MediaPosition
import com.google.android.horologist.media.model.PlayerState
import com.google.android.horologist.media.repository.PlayerRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

@OptIn(ExperimentalHorologistMediaApi::class)
class StubPlayerRepository : PlayerRepository {
    override val connected: StateFlow<Boolean>
        get() = MutableStateFlow(false)

    override val availableCommands: StateFlow<Set<Command>>
        get() = MutableStateFlow(emptySet())

    override val currentState: StateFlow<PlayerState>
        get() = MutableStateFlow(PlayerState.Idle)

    override val currentMedia: StateFlow<Media?>
        get() = MutableStateFlow(null)

    override val mediaPosition: StateFlow<MediaPosition?>
        get() = MutableStateFlow(null)

    override val shuffleModeEnabled: StateFlow<Boolean>
        get() = MutableStateFlow(false)

    override fun prepare() {
        // do nothing
    }

    override fun play() {
        // do nothing
    }

    override fun seekToDefaultPosition(mediaIndex: Int) {
        // do nothing
    }

    override fun pause() {
        // do nothing
    }

    override fun hasPreviousMedia(): Boolean = false

    override fun skipToPreviousMedia() {
        // do nothing
    }

    override fun hasNextMedia(): Boolean = false

    override fun skipToNextMedia() {
        // do nothing
    }

    override fun getSeekBackIncrement(): Duration = 0.seconds

    override fun seekBack() {
        // do nothing
    }

    override fun getSeekForwardIncrement(): Duration = 0.seconds

    override fun seekForward() {
        // do nothing
    }

    override fun setShuffleModeEnabled(shuffleModeEnabled: Boolean) {
        // do nothing
    }

    override fun setMedia(media: Media) {
        // do nothing
    }

    override fun setMediaList(mediaList: List<Media>) {
        // do nothing
    }

    override fun addMedia(media: Media) {
        // do nothing
    }

    override fun addMedia(index: Int, media: Media) {
        // do nothing
    }

    override fun removeMedia(index: Int) {
        // do nothing
    }

    override fun clearMediaList() {
        // do nothing
    }

    override fun getMediaCount(): Int = 0

    override fun getMediaAt(index: Int): Media? = null

    override fun getCurrentMediaIndex(): Int = -1

    override fun release() {
        // do nothing
    }
}
