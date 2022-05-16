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

import com.google.android.horologist.media.model.Command
import com.google.android.horologist.media.model.MediaItem
import com.google.android.horologist.media.model.MediaItemPosition
import com.google.android.horologist.media.model.PlayerState
import com.google.android.horologist.media.repository.PlayerRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

/**
 * A fake implementation of [PlayerRepository].
 *
 * It does NOT output any audio, it just manipulates the state to pretend it's playing.
 */
class PlayerRepositoryImpl(
    private val mediaDataSource: MediaDataSource
) : PlayerRepository {

    private val _availableCommands = MutableStateFlow<Set<Command>>(emptySet())
    override val availableCommands: StateFlow<Set<Command>> = _availableCommands

    private val _currentState: MutableStateFlow<PlayerState> = MutableStateFlow(PlayerState.Idle)
    override val currentState: StateFlow<PlayerState> = _currentState

    private val _currentMediaItem: MutableStateFlow<MediaItem?> = MutableStateFlow(null)
    override val currentMediaItem: StateFlow<MediaItem?> = _currentMediaItem

    private val _mediaItemPosition: MutableStateFlow<MediaItemPosition?> = MutableStateFlow(null)
    override val mediaItemPosition: StateFlow<MediaItemPosition?> = _mediaItemPosition

    private var _shuffleModeEnabled = MutableStateFlow(false)
    override val shuffleModeEnabled: StateFlow<Boolean> = _shuffleModeEnabled

    private var _mediaItems: List<MediaItem>? = null
    private var currentItemIndex = -1

    private lateinit var coroutineScope: CoroutineScope
    private var playingJob: Job? = null

    fun setCoroutineScope(coroutineScope: CoroutineScope) {
        this.coroutineScope = coroutineScope
    }

    fun fetchData() {
        mediaDataSource.fetchData().also {
            _mediaItems = it
            currentItemIndex++
            _currentMediaItem.value = it[currentItemIndex]
            updateCommands()
        }
    }

    private fun updateCommands() {
        val commands = mutableSetOf<Command>()
        commands.addAll(_availableCommands.value)

        _mediaItems?.let {
            commands += Command.PlayPause

            if (currentItemIndex < it.size - 1) {
                commands += Command.SkipToNextMediaItem
            } else {
                commands -= Command.SkipToNextMediaItem
            }
        } ?: run {
            commands -= Command.PlayPause
        }

        if (currentItemIndex > 0) {
            commands += Command.SkipToPreviousMediaItem
        } else {
            commands -= Command.SkipToPreviousMediaItem
        }

        _availableCommands.value = commands
    }

    override fun prepare() {
        // do nothing
    }

    override fun play() {
        _currentState.value = PlayerState.Playing

        if (_mediaItemPosition.value?.current == DURATION) {
            _mediaItemPosition.value = INITIAL_MEDIA_ITEM_POSITION
        }

        playingJob = coroutineScope.launch {
            repeat(DURATION.inWholeSeconds.toInt()) {
                delay(1.seconds.inWholeMilliseconds)
                updatePosition()
            }
            _mediaItems?.let {
                if (currentItemIndex < it.size - 1) {
                    delay(1.seconds.inWholeMilliseconds)
                    skipToNextMediaItem()
                } else {
                    pause()
                }
            } ?: run {
                pause()
            }
        }
    }

    override fun play(mediaItemIndex: Int) {
        // do nothing
    }

    private fun updatePosition() {
        _mediaItemPosition.value = _mediaItemPosition.value?.let {
            MediaItemPosition.create(
                current = it.current + 1.seconds,
                duration = DURATION
            )
        } ?: MediaItemPosition.create(
            current = INITIAL_MEDIA_ITEM_POSITION.current + 1.seconds,
            duration = DURATION
        )
    }

    override fun pause() {
        _currentState.value = PlayerState.Ready
        playingJob?.cancel()
    }

    override fun hasPreviousMediaItem(): Boolean = currentItemIndex > 0

    override fun skipToPreviousMediaItem() {
        val wasPlaying = _currentState.value == PlayerState.Playing
        if (wasPlaying) {
            playingJob?.cancel()
            _currentState.value = PlayerState.Ready
        }

        currentItemIndex--
        _currentMediaItem.value = _mediaItems!![currentItemIndex]
        _mediaItemPosition.value = INITIAL_MEDIA_ITEM_POSITION
        updateCommands()

        if (wasPlaying) {
            play()
        }
    }

    override fun hasNextMediaItem(): Boolean =
        _mediaItems?.let {
            currentItemIndex < it.size - 2
        } ?: false

    override fun skipToNextMediaItem() {
        val wasPlaying = _currentState.value == PlayerState.Playing
        if (wasPlaying) {
            playingJob?.cancel()
            _currentState.value = PlayerState.Ready
        }

        currentItemIndex++
        _currentMediaItem.value = _mediaItems!![currentItemIndex]
        _mediaItemPosition.value = INITIAL_MEDIA_ITEM_POSITION
        updateCommands()

        if (wasPlaying) {
            play()
        }
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

    override fun getMediaItemCount(): Int = _mediaItems?.size ?: 0

    override fun getMediaItemAt(index: Int): MediaItem? = null // not implemented

    override fun getCurrentMediaItemIndex(): Int = 0 // not implemented

    override fun release() {
        // do nothing
    }

    companion object {
        private val DURATION = 180.seconds

        private val INITIAL_MEDIA_ITEM_POSITION = MediaItemPosition.create(0.seconds, DURATION)
    }

    class Factory(
        private val mediaDataSource: MediaDataSource
    ) {

        fun create(): PlayerRepositoryImpl = PlayerRepositoryImpl(mediaDataSource)
    }
}
