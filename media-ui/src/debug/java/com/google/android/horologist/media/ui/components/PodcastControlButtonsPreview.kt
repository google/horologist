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

package com.google.android.horologist.media.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.google.android.horologist.media.ui.components.controls.SeekButtonIncrement
import com.google.android.horologist.media.ui.state.model.TrackPositionUiModel
import kotlin.time.Duration.Companion.seconds

@Preview(
    "Enabled - Playing - With progress",
    backgroundColor = 0xff000000,
    showBackground = true
)
@Composable
fun PodcastControlButtonsPreview() {
    PodcastControlButtons(
        onPlayButtonClick = {},
        onPauseButtonClick = {},
        playPauseButtonEnabled = true,
        playing = true,
        trackPositionUiModel = TrackPositionUiModel.Actual(0.25f, 25.seconds, 100.seconds),
        onSeekBackButtonClick = {},
        seekBackButtonEnabled = true,
        onSeekForwardButtonClick = {},
        seekForwardButtonEnabled = true,
        seekBackButtonIncrement = SeekButtonIncrement.Ten,
        seekForwardButtonIncrement = SeekButtonIncrement.Thirty
    )
}

@Preview(
    "Enabled - Playing - With progress",
    backgroundColor = 0xff000000,
    showBackground = true
)
@Composable
fun PodcastControlButtonsPreviewUnknown() {
    PodcastControlButtons(
        onPlayButtonClick = {},
        onPauseButtonClick = {},
        playPauseButtonEnabled = true,
        playing = true,
        trackPositionUiModel = TrackPositionUiModel.Actual(0.25f, 25.seconds, 100.seconds),
        onSeekBackButtonClick = {},
        seekBackButtonEnabled = true,
        onSeekForwardButtonClick = {},
        seekForwardButtonEnabled = true,
        seekBackButtonIncrement = SeekButtonIncrement.Unknown,
        seekForwardButtonIncrement = SeekButtonIncrement.Unknown
    )
}

@Preview(
    "Disabled - Not playing - Without progress",
    backgroundColor = 0xff000000,
    showBackground = true
)
@Composable
fun PodcastControlButtonsPreviewNoProgress() {
    PodcastControlButtons(
        onPlayButtonClick = {},
        onPauseButtonClick = {},
        playPauseButtonEnabled = false,
        playing = false,
        onSeekBackButtonClick = {},
        seekBackButtonEnabled = false,
        onSeekForwardButtonClick = {},
        seekForwardButtonEnabled = false,
        seekBackButtonIncrement = SeekButtonIncrement.Unknown,
        seekForwardButtonIncrement = SeekButtonIncrement.Five
    )
}

@Preview(
    backgroundColor = 0xff888800,
    showBackground = true
)
@Composable
fun PodcastControlDisabledLightBackground() {
    PodcastControlButtons(
        onPlayButtonClick = {},
        onPauseButtonClick = {},
        playPauseButtonEnabled = false,
        playing = false,
        onSeekBackButtonClick = {},
        seekBackButtonEnabled = false,
        onSeekForwardButtonClick = {},
        seekForwardButtonEnabled = false,
        seekBackButtonIncrement = SeekButtonIncrement.Unknown,
        seekForwardButtonIncrement = SeekButtonIncrement.Five
    )
}
