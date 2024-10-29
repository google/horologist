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

@Composable
fun rememberSeekToNextButtonState(player: Player): SeekToNextButtonState {
    val seekToNextButtonState = remember(player) { SeekToNextButtonState(player) }
    LaunchedEffect(player) { seekToNextButtonState.observe() }
    return seekToNextButtonState
}

class SeekToNextButtonState(private val player: Player) {
    private var _isEnabled by mutableStateOf(shouldEnableSeekToNextButton(player))
    val isEnabled: Boolean
        get() = _isEnabled

    fun onClick() {
        player.seekToNext()
    }

    suspend fun observe(): Nothing =
        player.listen { events ->
            if (
                // TODO: Update trigger logic
                events.containsAny(
                    Player.EVENT_PLAYBACK_STATE_CHANGED,
                    Player.EVENT_PLAY_WHEN_READY_CHANGED,
                    Player.EVENT_AVAILABLE_COMMANDS_CHANGED,
                )
            ) {
                _isEnabled = shouldEnableSeekToNextButton(this)
            }
        }

    private fun shouldEnableSeekToNextButton(player: Player): Boolean {
        // TODO: Actual logic for enable/disable
        return player.isCommandAvailable(Player.COMMAND_SEEK_TO_NEXT_MEDIA_ITEM)
    }
}
