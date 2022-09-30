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
import com.google.android.horologist.media.model.Media
import com.google.android.horologist.media.model.MediaPosition
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

    private var _currentMedia: MutableStateFlow<Media?> = MutableStateFlow(null)
    override val currentMedia: StateFlow<Media?> = _currentMedia

    private var _mediaPosition: MutableStateFlow<MediaPosition?> = MutableStateFlow(null)
    override val mediaPosition: StateFlow<MediaPosition?> = _mediaPosition

    private var _shuffleModeEnabled = MutableStateFlow(false)
    override val shuffleModeEnabled: StateFlow<Boolean> = _shuffleModeEnabled

    private var _seekBackIncrement = MutableStateFlow<Duration?>(null)
    override val seekBackIncrement: StateFlow<Duration?> = _seekBackIncrement

    private var _seekForwardIncrement = MutableStateFlow<Duration?>(null)
    override val seekForwardIncrement: StateFlow<Duration?> = _seekForwardIncrement

    private var _mediaList: List<Media>? = null
    private var currentItemIndex = -1

    override fun prepare() {
        // do nothing
    }

    override fun play() {
        _currentState.value = PlayerState.Playing
    }

    override fun seekToDefaultPosition(mediaIndex: Int) {
        currentItemIndex = mediaIndex
    }

    override fun pause() {
        _currentState.value = PlayerState.Ready
    }

    override fun hasPreviousMedia(): Boolean = currentItemIndex > 0

    override fun skipToPreviousMedia() {
        currentItemIndex--
        _currentMedia.value = _mediaList!![currentItemIndex]
    }

    override fun hasNextMedia(): Boolean =
        _mediaList?.let {
            currentItemIndex < it.size - 2
        } ?: false

    override fun skipToNextMedia() {
        currentItemIndex++
        _currentMedia.value = _mediaList!![currentItemIndex]
    }

    override fun seekBack() {
        // do nothing
    }

    override fun seekForward() {
        // do nothing
    }

    override fun setShuffleModeEnabled(shuffleModeEnabled: Boolean) {
        // do nothing
    }

    override fun setMedia(media: Media) {
        _currentMedia.value = media
        currentItemIndex = 0
    }

    override fun setMediaList(mediaList: List<Media>) {
        _mediaList = mediaList
        currentItemIndex = 0
        _currentMedia.value = mediaList[currentItemIndex]
    }

    override fun setMediaListAndPlay(mediaList: List<Media>, index: Int) {
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

    override fun getMediaCount(): Int = _mediaList?.size ?: 0

    override fun getMediaAt(index: Int): Media? = null // not implemented

    override fun getCurrentMediaIndex(): Int = 0 // not implemented

    override fun release() {
        _connected.value = false
    }

    fun updatePosition() {
        _mediaPosition.value = _mediaPosition.value?.let {
            val newCurrent = it.current + 1.seconds
            if (it is MediaPosition.KnownDuration) {
                MediaPosition.create(newCurrent, it.duration)
            } else {
                MediaPosition.UnknownDuration(newCurrent)
            }
        } ?: MediaPosition.create(1.seconds, 10.seconds)
    }

    fun addCommand(command: Command) {
        _availableCommandsList.value += command
    }
}
