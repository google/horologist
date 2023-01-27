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
import com.google.android.horologist.media.model.PlaybackStateEvent
import com.google.android.horologist.media.model.PlayerState
import com.google.android.horologist.media.repository.PlayerRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

class FakePlayerRepository() : PlayerRepository {

    private val _connected = MutableStateFlow(true)
    override val connected: StateFlow<Boolean> = _connected

    private var _availableCommandsList = MutableStateFlow(emptySet<Command>())
    override val availableCommands: StateFlow<Set<Command>> = _availableCommandsList

    private var _latestPlaybackState = MutableStateFlow(PlaybackStateEvent.INITIAL)
    override val latestPlaybackState: StateFlow<PlaybackStateEvent> = _latestPlaybackState

    private var _currentMedia: MutableStateFlow<Media?> = MutableStateFlow(null)
    override val currentMedia: StateFlow<Media?> = _currentMedia

    private var _shuffleModeEnabled = MutableStateFlow(false)
    override val shuffleModeEnabled: StateFlow<Boolean> = _shuffleModeEnabled

    private var _seekBackIncrement = MutableStateFlow<Duration?>(null)
    override val seekBackIncrement: StateFlow<Duration?> = _seekBackIncrement

    private var _seekForwardIncrement = MutableStateFlow<Duration?>(null)
    override val seekForwardIncrement: StateFlow<Duration?> = _seekForwardIncrement

    private var _mediaList: List<Media>? = null
    private var currentItemIndex = -1

    override fun play() {
        _latestPlaybackState.value = PlaybackStateEvent(_latestPlaybackState.value.playbackState.copy(playerState = PlayerState.Playing), PlaybackStateEvent.Cause.PlayerStateChanged)
    }

    override fun seekToDefaultPosition(mediaIndex: Int) {
        currentItemIndex = mediaIndex
    }

    override fun pause() {
        _latestPlaybackState.value = PlaybackStateEvent(_latestPlaybackState.value.playbackState.copy(playerState = PlayerState.Stopped), PlaybackStateEvent.Cause.PlayerStateChanged)
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

    override fun setMediaList(mediaList: List<Media>, index: Int, position: Duration?) {
        _mediaList = mediaList
        currentItemIndex = 0
        _currentMedia.value = mediaList[currentItemIndex]
        _latestPlaybackState.value = PlaybackStateEvent(
            _latestPlaybackState.value.playbackState.copy(
                duration = 10.seconds.takeIf { position != null },
                currentPosition = position
            ),
            PlaybackStateEvent.Cause.PositionDiscontinuity
        )
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

    override fun setPlaybackSpeed(speed: Float) {
        // do nothing
    }

    fun setPosition(position: Duration?, duration: Duration?) {
        _latestPlaybackState.value = PlaybackStateEvent(_latestPlaybackState.value.playbackState.copy(duration = duration, currentPosition = position), PlaybackStateEvent.Cause.PositionDiscontinuity)
    }

    fun addCommand(command: Command) {
        _availableCommandsList.value += command
    }
}
