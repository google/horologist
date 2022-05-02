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

import com.google.android.horologist.media.ui.ExperimentalMediaUiApi
import com.google.android.horologist.media.ui.components.PlayPauseButton
import com.google.android.horologist.media.ui.components.controls.PauseButton
import com.google.android.horologist.media.ui.components.controls.PlayButton
import com.google.android.horologist.media.ui.components.controls.SeekBackButton
import com.google.android.horologist.media.ui.components.controls.SeekForwardButton
import com.google.android.horologist.media.ui.components.controls.SeekToNextButton
import com.google.android.horologist.media.ui.components.controls.SeekToPreviousButton
import com.google.android.horologist.media.ui.components.controls.ShuffleButton
import com.google.android.horologist.media.ui.state.model.MediaItemUiModel
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
 * @param mediaItem current [MediaItemUiModel]
 * @param trackPosition current [TrackPositionUiModel]
 */
@ExperimentalMediaUiApi
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
    val mediaItem: MediaItemUiModel?,
    val trackPosition: TrackPositionUiModel?,
)
