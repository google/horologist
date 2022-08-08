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

@file:OptIn(ExperimentalHorologistMediaUiApi::class)

package com.google.android.horologist.media.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.google.android.horologist.media.ui.ExperimentalHorologistMediaUiApi

@Preview(
    "Enabled - Playing - With progress",
    backgroundColor = 0xff000000,
    showBackground = true
)
@Composable
fun MediaControlButtonsPreview() {
    MediaControlButtons(
        onPlayButtonClick = {},
        onPauseButtonClick = {},
        playPauseButtonEnabled = true,
        playing = true,
        percent = 0.25F,
        onSeekToNextButtonClick = {},
        seekToNextButtonEnabled = true,
        onSeekToPreviousButtonClick = {},
        seekToPreviousButtonEnabled = true
    )
}

@Preview(
    "Disabled - Not playing - Without progress",
    backgroundColor = 0xff000000,
    showBackground = true
)
@Composable
fun MediaControlButtonsPreviewNoProgress() {
    MediaControlButtons(
        onPlayButtonClick = {},
        onPauseButtonClick = {},
        playPauseButtonEnabled = false,
        playing = false,
        onSeekToNextButtonClick = {},
        seekToNextButtonEnabled = false,
        onSeekToPreviousButtonClick = {},
        seekToPreviousButtonEnabled = false
    )
}
