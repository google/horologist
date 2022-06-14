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
import com.google.android.horologist.media.model.MediaItem
import com.google.android.horologist.media.model.MediaItemPosition
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

    override val currentMediaItem: StateFlow<MediaItem?>
        get() = MutableStateFlow(null)

    override val mediaItemPosition: StateFlow<MediaItemPosition?>
        get() = MutableStateFlow(null)

    override val shuffleModeEnabled: StateFlow<Boolean>
        get() = MutableStateFlow(false)

    override fun prepare() {
        // do nothing
    }

    override fun play() {
        // do nothing
    }

    override fun play(mediaItemIndex: Int) {
        // do nothing
    }

    override fun pause() {
        // do nothing
    }

    override fun hasPreviousMediaItem(): Boolean = false

    override fun skipToPreviousMediaItem() {
        // do nothing
    }

    override fun hasNextMediaItem(): Boolean = false

    override fun skipToNextMediaItem() {
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

    override fun setMediaItem(mediaItem: MediaItem) {
        // do nothing
    }

    override fun setMediaItems(mediaItems: List<MediaItem>) {
        // do nothing
    }

    override fun addMediaItem(mediaItem: MediaItem) {
        // do nothing
    }

    override fun addMediaItem(index: Int, mediaItem: MediaItem) {
        // do nothing
    }

    override fun removeMediaItem(index: Int) {
        // do nothing
    }

    override fun clearMediaItems() {
        // do nothing
    }

    override fun getMediaItemCount(): Int = 0

    override fun getMediaItemAt(index: Int): MediaItem? = null

    override fun getCurrentMediaItemIndex(): Int = -1

    override fun release() {
        // do nothing
    }
}
