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

import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import com.google.android.horologist.media.data.ExperimentalHorologistMediaDataApi
import com.google.android.horologist.media.data.model.TrackPosition
import com.google.android.horologist.media.data.repository.PlayerRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

@OptIn(ExperimentalHorologistMediaDataApi::class)
class StubPlayerRepository : PlayerRepository {

    override val availableCommands: StateFlow<Player.Commands>
        get() = MutableStateFlow(Player.Commands.EMPTY)

    override val isPlaying: StateFlow<Boolean>
        get() = MutableStateFlow(false)

    override val currentMediaItem: StateFlow<MediaItem?>
        get() = MutableStateFlow(null)

    override val trackPosition: StateFlow<TrackPosition?>
        get() = MutableStateFlow(null)

    override val shuffleModeEnabled: StateFlow<Boolean>
        get() = MutableStateFlow(false)

    override fun prepareAndPlay(mediaItem: MediaItem, play: Boolean) {
        // do nothing
    }

    override fun prepareAndPlay(
        mediaItems: List<MediaItem>?,
        startIndex: Int,
        play: Boolean
    ) {
        // do nothing
    }

    override fun seekToPreviousMediaItem() {
        // do nothing
    }

    override fun seekToNextMediaItem() {
        // do nothing
    }

    override fun getSeekBackIncrement(): Long = SEEK_INCREMENT

    override fun seekBack() {
        // do nothing
    }

    override fun getSeekForwardIncrement(): Long = SEEK_INCREMENT

    override fun seekForward() {
        // do nothing
    }

    override fun pause() {
        // do nothing
    }

    override fun toggleShuffle() {
        // do nothing
    }

    override fun updatePosition() {
        // do nothing
    }

    companion object {
        const val SEEK_INCREMENT = 15000L
    }
}
