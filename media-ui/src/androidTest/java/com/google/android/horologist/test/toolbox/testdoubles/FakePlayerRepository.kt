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

@file:OptIn(ExperimentalHorologistMediaApi::class)

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

class FakePlayerRepository : PlayerRepository {

    private val _connected = MutableStateFlow(true)
    override val connected: StateFlow<Boolean> = _connected

    private var _availableCommandsList = MutableStateFlow(emptySet<Command>())
    override val availableCommands: StateFlow<Set<Command>> = _availableCommandsList

    private var _currentState = MutableStateFlow(PlayerState.Idle)
    override val currentState: StateFlow<PlayerState> = _currentState

    private var _currentMediaItem: MutableStateFlow<MediaItem?> = MutableStateFlow(null)
    override val currentMediaItem: StateFlow<MediaItem?> = _currentMediaItem

    private var _mediaItemPosition: MutableStateFlow<MediaItemPosition?> = MutableStateFlow(null)
    override val mediaItemPosition: StateFlow<MediaItemPosition?> = _mediaItemPosition

    private var _shuffleModeEnabled = MutableStateFlow(false)
    override val shuffleModeEnabled: StateFlow<Boolean> = _shuffleModeEnabled

    private var _mediaItems: List<MediaItem>? = null
    private var currentItemIndex = -1

    override fun prepare() {
        // do nothing
    }

    override fun play() {
        _currentState.value = PlayerState.Playing
    }

    override fun play(mediaItemIndex: Int) {
        currentItemIndex = mediaItemIndex
        _currentState.value = PlayerState.Playing
    }

    override fun pause() {
        _currentState.value = PlayerState.Ready
    }

    override fun hasPreviousMediaItem(): Boolean = currentItemIndex > 0

    override fun skipToPreviousMediaItem() {
        currentItemIndex--
        _currentMediaItem.value = _mediaItems!![currentItemIndex]
    }

    override fun hasNextMediaItem(): Boolean =
        _mediaItems?.let {
            currentItemIndex < it.size - 2
        } ?: false

    override fun skipToNextMediaItem() {
        currentItemIndex++
        _currentMediaItem.value = _mediaItems!![currentItemIndex]
    }

    override fun getSeekBackIncrement(): Duration = 0.seconds // not implemented

    override fun seekBack() {
        // do nothing
    }

    override fun getSeekForwardIncrement(): Duration = 0.seconds // not implemented

    override fun seekForward() {
        // do nothing
    }

    override fun setShuffleModeEnabled(shuffleModeEnabled: Boolean) {
        // do nothing
    }

    override fun setMediaItem(mediaItem: MediaItem) {
        _currentMediaItem.value = mediaItem
        currentItemIndex = 0
    }

    override fun setMediaItems(mediaItems: List<MediaItem>) {
        _mediaItems = mediaItems
        currentItemIndex = 0
        _currentMediaItem.value = mediaItems[currentItemIndex]
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

    override fun getMediaItemCount(): Int = _mediaItems?.size ?: 0

    override fun getMediaItemAt(index: Int): MediaItem? = null // not implemented

    override fun getCurrentMediaItemIndex(): Int = 0 // not implemented

    override fun release() {
        _connected.value = false
    }

    fun updatePosition() {
        _mediaItemPosition.value = _mediaItemPosition.value?.let {
            val newCurrent = it.current + 1.seconds
            if (it is MediaItemPosition.KnownDuration) {
                MediaItemPosition.create(newCurrent, it.duration)
            } else {
                MediaItemPosition.UnknownDuration(newCurrent)
            }
        } ?: MediaItemPosition.create(1.seconds, 10.seconds)
    }

    fun addCommand(command: Command) {
        _availableCommandsList.value += command
    }
}
