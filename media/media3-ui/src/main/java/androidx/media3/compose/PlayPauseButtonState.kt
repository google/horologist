/*
 * Copyright 2024 The Android Open Source Project
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

package androidx.media3.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.media3.common.Player
import androidx.media3.common.listen
import androidx.media3.common.util.Util.handlePlayPauseButtonAction
import androidx.media3.common.util.Util.shouldShowPlayButton

/**
 * This is useful syntactic sugar and should be public
 */
@Composable
fun rememberPlayPauseButtonState(player: Player): PlayPauseButtonState {
    val playPauseButtonState = remember(player) { PlayPauseButtonState(player) }
    LaunchedEffect(player) { playPauseButtonState.observe() }
    return playPauseButtonState
}

/**
 * State that holds all interactions to correctly deal with a [PlayPauseButton].
 *
 * @property[isEnabled] determined by `isCommandAvailable(Player.COMMAND_PLAY_PAUSE)` and having
 *   something in the [Timeline] to play
 */
class PlayPauseButtonState(private val player: Player) {
    private var _isEnabled by mutableStateOf(shouldEnablePlayPauseButton(player))
    val isEnabled: Boolean
        get() = _isEnabled

    var showPlay by mutableStateOf(shouldShowPlayButton(player))
        private set

    /**
     * Handles the interaction with the PlayPause button according to the current state of the
     * [Player].
     *
     * The [Player] update that follows can take a form of [Player.play], [Player.pause],
     * [Player.prepare] or [Player.seekToDefaultPosition].
     *
     * @see [handlePlayButtonAction]
     * @see [handlePauseButtonAction]
     * @see [shouldShowPlayButton]
     */
    fun onClick() {
        handlePlayPauseButtonAction(player)
    }

    /**
     * Subscribes to updates from [Player.Events] and listens to
     * * [Player.EVENT_PLAYBACK_STATE_CHANGED] and [Player.EVENT_PLAY_WHEN_READY_CHANGED] in order to
     *   determine whether the player is effectively in a playing state or not.
     * * [Player.EVENT_AVAILABLE_COMMANDS_CHANGED] in order to determine whether the button should be
     *   enabled, i.e. respond to user input.
     */
    suspend fun observe(): Nothing =
        player.listen { events ->
            if (
                events.containsAny(
                    Player.EVENT_PLAYBACK_STATE_CHANGED,
                    Player.EVENT_PLAY_WHEN_READY_CHANGED,
                    Player.EVENT_AVAILABLE_COMMANDS_CHANGED,
                )
            ) {
                showPlay = shouldShowPlayButton(this)
                _isEnabled = shouldEnablePlayPauseButton(this)
            }
        }

    private fun shouldEnablePlayPauseButton(player: Player): Boolean {
        // Allowed to play-pause and there is something to play further
        return player.isCommandAvailable(Player.COMMAND_PLAY_PAUSE) &&
            !(player.isCommandAvailable(Player.COMMAND_GET_TIMELINE) && player.currentTimeline.isEmpty)
    }
}
