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

@file:OptIn(ExperimentalMediaDataApi::class)

package com.google.test.toolbox.testdoubles

import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.Player.Command
import com.google.android.horologist.media.data.ExperimentalMediaDataApi
import com.google.android.horologist.media.data.model.TrackPosition
import com.google.android.horologist.media.data.repository.PlayerRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class FakePlayerRepository : PlayerRepository {

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

    override fun prepareAndPlay(mediaItem: MediaItem, play: Boolean) {
        _currentMediaItem.value = mediaItem
        if (play) {
            _playing.value = true
        }
    }

    override fun prepareAndPlay(mediaItems: List<MediaItem>?, startIndex: Int, play: Boolean) {
        mediaItems?.let { _currentMediaItem.value = it[startIndex] }

        if (play) {
            _playing.value = true
        }
    }

    override fun seekToPreviousMediaItem() {
        TODO("Not yet implemented")
    }

    override fun seekToNextMediaItem() {
        TODO("Not yet implemented")
    }

    override fun getSeekBackIncrement(): Long? {
        TODO("Not yet implemented")
    }

    override fun seekBack() {
        TODO("Not yet implemented")
    }

    override fun getSeekForwardIncrement(): Long? {
        TODO("Not yet implemented")
    }

    override fun seekForward() {
        TODO("Not yet implemented")
    }

    override fun pause() {
        _playing.value = false
    }

    override fun toggleShuffle() {
        TODO("Not yet implemented")
    }

    override fun updatePosition() {
        TODO("Not yet implemented")
    }

    fun addCommand(@Command command: Int) {
        _availableCommandsList.value = Player.Commands.Builder()
            .addAll(_availableCommandsList.value)
            .add(command)
            .build()
    }
}
