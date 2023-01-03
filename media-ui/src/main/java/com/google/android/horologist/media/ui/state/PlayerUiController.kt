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

package com.google.android.horologist.media.ui.state

import androidx.compose.runtime.Stable
import com.google.android.horologist.media.repository.PlayerRepository

/**
 * Represents a mapping between media UI controls and a [PlayerRepository].
 *
 * This class should generally always be hosted inside a ViewModel to ensure it's tied to a
 * lifecycle that survives configuration changes.
 */
@Stable
public data class PlayerUiController(private val playerRepository: PlayerRepository) {
    public fun play() {
        // Prepare is needed to ensure playback
        playerRepository.prepare()
        playerRepository.play()
    }

    public fun pause() {
        playerRepository.pause()
    }

    public fun skipToPreviousMedia() {
        playerRepository.skipToPreviousMedia()
    }

    public fun skipToNextMedia() {
        playerRepository.skipToNextMedia()
    }

    public fun seekBack() {
        playerRepository.seekBack()
    }

    public fun seekForward() {
        playerRepository.seekForward()
    }

    public fun setPlaybackSpeed(speed: Float) {
        playerRepository.setPlaybackSpeed(speed)
    }
}
