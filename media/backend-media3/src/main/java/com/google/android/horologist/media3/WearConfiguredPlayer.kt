/*
 * Copyright 2021 The Android Open Source Project
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

package com.google.android.horologist.media3

import android.annotation.SuppressLint
import androidx.media3.common.ForwardingPlayer
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import com.google.android.horologist.annotations.ExperimentalHorologistApi
import com.google.android.horologist.media3.logging.ErrorReporter
import com.google.android.horologist.media3.rules.PlaybackRules
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

/**
 * Wrapper for a UI hosted Player instance to enforce Playback Rules.
 * @see [PlaybackRules]
 */
@SuppressLint("UnsafeOptInUsageError")
@ExperimentalHorologistApi
public class WearConfiguredPlayer(
    player: Player,
    private val playbackRules: PlaybackRules,
    private val errorReporter: ErrorReporter,
    private val coroutineScope: CoroutineScope
) : ForwardingPlayer(player) {
    private var playAttempt: Job? = null

    override fun play() {
        val mediaItem = currentMediaItem ?: return

        playAttempt?.cancel()

        playAttempt = coroutineScope.launch {
            attemptPlay(mediaItem)
        }
    }

    private suspend fun attemptPlay(mediaItem: MediaItem) = coroutineScope {

        if (!playbackRules.canPlayItem(mediaItem)) {
            errorReporter.showMessage(R.string.horologist_cant_play_item)
            return@coroutineScope
        }

        wrappedPlayer.play()
    }

    override fun pause() {
        playAttempt?.cancel()

        wrappedPlayer.pause()
    }

    override fun setPlayWhenReady(playWhenReady: Boolean) {
        if (playWhenReady) {
            play()
        } else {
            pause()
        }
    }

    // Stored to allow some synthetic events
    private val listeners = mutableListOf<Player.Listener>()

    override fun addListener(listener: Player.Listener) {
        listeners.add(listener)

        super.addListener(listener)
    }

    override fun removeListener(listener: Player.Listener) {
        listeners.remove(listener)
        super.removeListener(listener)
    }
}
