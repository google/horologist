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

package com.google.android.horologist.sample.media

import androidx.annotation.OptIn
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.Player.Command
import androidx.media3.common.util.UnstableApi
import com.google.android.horologist.media.data.model.TrackPosition
import com.google.android.horologist.media.data.repository.PlayerRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * A fake implementation of [PlayerRepository].
 *
 * It does NOT output any audio, it just manipulates the state to pretend it's playing.
 */
class PlayerRepositoryImpl(
    private val mediaDataSource: MediaDataSource
) : PlayerRepository {

    private var _availableCommandsList = MutableStateFlow(Player.Commands.EMPTY)
    override val availableCommands: StateFlow<Player.Commands> = _availableCommandsList

    private var _playing = MutableStateFlow(false)
    override val isPlaying: StateFlow<Boolean> = _playing

    private var _currentMediaItem: MutableStateFlow<MediaItem?> = MutableStateFlow(null)
    override val currentMediaItem: StateFlow<MediaItem?> = _currentMediaItem

    private var _trackPosition: MutableStateFlow<TrackPosition?> = MutableStateFlow(null)
    override val trackPosition: StateFlow<TrackPosition?> = _trackPosition

    private var _shuffleModeEnabled = MutableStateFlow(false)
    override val shuffleModeEnabled: StateFlow<Boolean> = _shuffleModeEnabled

    private var mediaItems: List<MediaItem>? = null
    private var currentItemIndex = -1

    private lateinit var coroutineScope: CoroutineScope
    private var playingJob: Job? = null

    fun setCoroutineScope(coroutineScope: CoroutineScope) {
        this.coroutineScope = coroutineScope
    }

    fun fetchData() {
        mediaDataSource.fetchData().also {
            mediaItems = it
            currentItemIndex++
            _currentMediaItem.value = it[currentItemIndex]
            updateCommands()
        }
    }

    private fun updateCommands() {
        mediaItems?.let {
            addCommand(Player.COMMAND_PLAY_PAUSE)

            if (currentItemIndex < it.size - 1) {
                addCommand(Player.COMMAND_SEEK_TO_NEXT_MEDIA_ITEM)
            } else {
                removeCommand(Player.COMMAND_SEEK_TO_NEXT_MEDIA_ITEM)
            }
        } ?: run {
            removeCommand(Player.COMMAND_PLAY_PAUSE)
        }

        if (currentItemIndex > 0) {
            addCommand(Player.COMMAND_SEEK_TO_PREVIOUS_MEDIA_ITEM)
        } else {
            removeCommand(Player.COMMAND_SEEK_TO_PREVIOUS_MEDIA_ITEM)
        }
    }

    override fun prepareAndPlay(mediaItem: MediaItem, play: Boolean) {
        _currentMediaItem.value = mediaItem

        if (play) {
            pretendIsPlaying()
        }
    }

    override fun prepareAndPlay(mediaItems: List<MediaItem>?, startIndex: Int, play: Boolean) {
        mediaItems?.let {
            this.mediaItems = it
            currentItemIndex = startIndex
            _currentMediaItem.value = it[startIndex]
        }

        if (play) {
            pretendIsPlaying()
        }
    }

    private fun pretendIsPlaying() {
        _playing.value = true

        if (trackPosition.value?.current == DURATION) {
            _trackPosition.value = INITIAL_TRACK_POSITION
        }

        playingJob = coroutineScope.launch {
            repeat(DURATION.toInt()) {
                delay(1_000L)
                updatePosition()
            }
            mediaItems?.let {
                if (currentItemIndex < it.size - 1) {
                    delay(1_000L)
                    seekToNextMediaItem()
                } else {
                    pause()
                }
            } ?: run {
                pause()
            }
        }
    }

    override fun seekToPreviousMediaItem() {
        val wasPlaying = _playing.value
        if (wasPlaying) {
            playingJob?.cancel()
            _playing.value = false
        }

        currentItemIndex--
        _currentMediaItem.value = mediaItems!![currentItemIndex]
        _trackPosition.value = INITIAL_TRACK_POSITION
        updateCommands()

        if (wasPlaying) {
            prepareAndPlay()
        }
    }

    override fun seekToNextMediaItem() {
        val wasPlaying = _playing.value
        if (wasPlaying) {
            playingJob?.cancel()
            _playing.value = false
        }

        currentItemIndex++
        _currentMediaItem.value = mediaItems!![currentItemIndex]
        _trackPosition.value = INITIAL_TRACK_POSITION
        updateCommands()

        if (wasPlaying) {
            prepareAndPlay()
        }
    }

    override fun getSeekBackIncrement(): Long = 0

    override fun seekBack() {
        // do nothing
    }

    override fun getSeekForwardIncrement(): Long = 0

    override fun seekForward() {
        // do nothing
    }

    override fun pause() {
        _playing.value = false
        playingJob?.cancel()
    }

    override fun toggleShuffle() {
        // do nothing
    }

    override fun updatePosition() {
        _trackPosition.value = _trackPosition.value?.let {
            it.copy(current = it.current + 1)
        } ?: INITIAL_TRACK_POSITION.copy(current = INITIAL_TRACK_POSITION.current + 1)
    }

    private fun addCommand(@Command command: Int) {
        @OptIn(UnstableApi::class)
        _availableCommandsList.value = Player.Commands.Builder()
            .addAll(_availableCommandsList.value)
            .add(command)
            .build()
    }

    private fun removeCommand(@Command command: Int) {
        @OptIn(UnstableApi::class)
        _availableCommandsList.value = Player.Commands.Builder()
            .addAll(_availableCommandsList.value)
            .remove(command)
            .build()
    }

    companion object {
        private const val DURATION = 180L

        private val INITIAL_TRACK_POSITION = TrackPosition(0, DURATION)
    }

    class Factory(
        private val mediaDataSource: MediaDataSource
    ) {
        fun create(): PlayerRepositoryImpl = PlayerRepositoryImpl(mediaDataSource)
    }
}
