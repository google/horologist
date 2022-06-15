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

import android.util.Log
import androidx.media3.common.ForwardingPlayer
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import com.google.android.horologist.audio.AudioOutputRepository
import com.google.android.horologist.media3.audio.AudioOutputSelector
import com.google.android.horologist.media3.flows.isPlayingFlow
import com.google.android.horologist.media3.logging.ErrorReporter
import com.google.android.horologist.media3.rules.PlaybackRules
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

/**
 * Wrapper for a UI hosted Player instance to enforce Playback Rules.
 * @see [PlaybackRules]
 */
@ExperimentalHorologistMedia3BackendApi
public class WearConfiguredPlayer(
    player: Player,
    private val audioOutputRepository: AudioOutputRepository,
    private val audioOutputSelector: AudioOutputSelector,
    private val playbackRules: PlaybackRules,
    private val errorReporter: ErrorReporter,
) : ForwardingPlayer(player) {
    /**
     * Start proactive noise detection, unlike ExoPlayer setHandleAudioBecomingNoisy
     * this also handles when accidentally started in the wrong mode due to race conditions.
     */
    public fun CoroutineScope.startNoiseDetection() {
        launch(Dispatchers.Main) {
            combine(
                audioOutputRepository.audioOutput,
                wrappedPlayer.isPlayingFlow()
            ) { audioOutput, isPlaying ->
                Log.i("WearPlayer", "Playing status: $isPlaying $audioOutput")
                Pair(audioOutput, isPlaying)
            }.filter { (audioOutput, isPlaying) ->
                isPlaying && !playbackRules.canPlayWithOutput(audioOutput = audioOutput)
            }.collect {
                Log.i("WearPlayer", "Pausing")
                pause()
            }
        }
    }

    override fun play() {
        // TODO check multiple items
        val mediaItem = currentMediaItem ?: return

        val play = runBlocking {
            attemptPlay(mediaItem)
        }

        if (play) {
            wrappedPlayer.play()
        }
    }

    private suspend fun attemptPlay(
        mediaItem: MediaItem,
    ): Boolean = coroutineScope {
        val currentAudioOutput = audioOutputRepository.audioOutput.value

        if (!playbackRules.canPlayItem(mediaItem)) {
            errorReporter.showMessage(R.string.horologist_cant_play_item)
            return@coroutineScope false
        }

        val canPlayWithCurrentOutput = playbackRules.canPlayWithOutput(currentAudioOutput)

        if (!canPlayWithCurrentOutput) {
            launch {
                val newAudioOutput = audioOutputSelector.selectNewOutput(currentAudioOutput)

                val canPlayWithNewOutput =
                    newAudioOutput != null && playbackRules.canPlayWithOutput(newAudioOutput)

                if (canPlayWithNewOutput) {
                    withContext(Dispatchers.Main) {
                        // TODO check in an unchanged state still
                        play()
                    }
                }
            }

            // playback depends on new output
            return@coroutineScope false
        }

        return@coroutineScope true
    }

    override fun pause() {
        wrappedPlayer.pause()
    }

    override fun release() {
        super.release()
    }

    override fun setPlayWhenReady(playWhenReady: Boolean) {
        if (playWhenReady) {
            play()
        } else {
            pause()
        }
    }
}
