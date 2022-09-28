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

import com.google.android.horologist.media.ui.ExperimentalHorologistMediaUiApi
import com.google.android.horologist.media.ui.components.PlayPauseButton
import com.google.android.horologist.media.ui.components.controls.PauseButton
import com.google.android.horologist.media.ui.components.controls.PlayButton
import com.google.android.horologist.media.ui.components.controls.SeekBackButton
import com.google.android.horologist.media.ui.components.controls.SeekButtonIncrement
import com.google.android.horologist.media.ui.components.controls.SeekForwardButton
import com.google.android.horologist.media.ui.components.controls.SeekToNextButton
import com.google.android.horologist.media.ui.components.controls.SeekToPreviousButton
import com.google.android.horologist.media.ui.components.controls.ShuffleButton
import com.google.android.horologist.media.ui.state.model.MediaUiModel
import com.google.android.horologist.media.ui.state.model.TrackPositionUiModel

/**
 * Represent the state of the Media UI components.
 *
 * @param playEnabled whether [PlayButton] is enabled
 * @param pauseEnabled whether [PauseButton] is enabled
 * @param seekBackEnabled whether [SeekBackButton] button is enabled
 * @param seekForwardEnabled whether [SeekForwardButton] button is enabled
 * @param seekToPreviousEnabled whether [SeekToPreviousButton] is enabled
 * @param seekToNextEnabled whether [SeekToNextButton] is enabled
 * @param shuffleEnabled whether [ShuffleButton] is enabled
 * @param shuffleOn whether [ShuffleButton] should display a shuffle on icon
 * @param playPauseEnabled whether [PlayPauseButton] is enabled
 * @param playing whether [PlayPauseButton] should display the play or pause button
 * @param media current [MediaUiModel]
 * @param seekBackButtonIncrement increment when seeking back.
 * @param seekForwardButtonIncrement increment when seeking forward.
 * @param connected is the player screen connected.
 */
@ExperimentalHorologistMediaUiApi
public data class PlayerUiState(
    val playEnabled: Boolean,
    val pauseEnabled: Boolean,
    val seekBackEnabled: Boolean,
    val seekForwardEnabled: Boolean,
    val seekToPreviousEnabled: Boolean,
    val seekToNextEnabled: Boolean,
    val shuffleEnabled: Boolean,
    val shuffleOn: Boolean,
    val playPauseEnabled: Boolean,
    val playing: Boolean,
    val media: MediaUiModel?,
    val trackPosition: TrackPositionUiModel?,
    val seekBackButtonIncrement: SeekButtonIncrement = SeekButtonIncrement.Unknown,
    val seekForwardButtonIncrement: SeekButtonIncrement = SeekButtonIncrement.Unknown,
    val connected: Boolean
) {
    public companion object {
        /**
         * Value for UIs before a connected player is available.
         */
        public val NotConnected = PlayerUiState(
            playEnabled = false,
            pauseEnabled = false,
            seekBackEnabled = false,
            seekForwardEnabled = false,
            seekToPreviousEnabled = false,
            seekToNextEnabled = false,
            shuffleEnabled = false,
            shuffleOn = false,
            playPauseEnabled = false,
            playing = false,
            media = null,
            trackPosition = null,
            connected = false
        )
    }
}
