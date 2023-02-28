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

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import com.google.android.horologist.media.ui.ExperimentalHorologistMediaUiApi
import com.google.android.horologist.media.ui.state.model.TrackPositionUiModel
import kotlin.time.Duration.Companion.seconds

@Preview(
    "Enabled - Playing - Progress 0%",
    backgroundColor = 0xff000000,
    showBackground = true
)
@Composable
fun PlayPauseProgressButtonPreview0() {
    PlayPauseProgressButton(
        onPlayClick = {},
        onPauseClick = {},
        enabled = true,
        playing = true,
        trackPositionUiModel = TrackPositionUiModel.Actual(0f, 0.seconds, 100.seconds)
    )
}

@Preview(
    "Disabled - Not playing - Progress 25%",
    backgroundColor = 0xff000000,
    showBackground = true
)
@Composable
fun PlayPauseProgressButtonPreview25() {
    PlayPauseProgressButton(
        onPlayClick = {},
        onPauseClick = {},
        enabled = false,
        playing = false,
        trackPositionUiModel = TrackPositionUiModel.Actual(0.25f, 25.seconds, 100.seconds)
    )
}

@Preview(
    "Disabled - Playing - Progress 75%",
    backgroundColor = 0xff000000,
    showBackground = true
)
@Composable
fun PlayPauseProgressButtonPreview75() {
    PlayPauseProgressButton(
        onPlayClick = {},
        onPauseClick = {},
        enabled = false,
        playing = true,
        trackPositionUiModel = TrackPositionUiModel.Actual(0.75f, 75.seconds, 100.seconds)
    )
}

@Preview(
    "Enabled - Not playing - Progress 100%",
    backgroundColor = 0xff000000,
    showBackground = true
)
@Composable
fun PlayPauseProgressButtonPreview100() {
    PlayPauseProgressButton(
        onPlayClick = {},
        onPauseClick = {},
        enabled = true,
        playing = false,
        trackPositionUiModel = TrackPositionUiModel.Actual(0.5f, 50.seconds, 100.seconds)
    )
}

@Preview(
    "Loading",
    backgroundColor = 0xff000000,
    showBackground = true
)
@Composable
fun PlayPauseProgressButtonLoadingPreview() {
    PlayPauseProgressButton(
        onPlayClick = {},
        onPauseClick = {},
        enabled = true,
        playing = true,
        trackPositionUiModel = TrackPositionUiModel.Loading(showProgress = true)
    )
}

@Preview(
    "On Background - Progress 50%",
    backgroundColor = 0xff000000,
    showBackground = true
)
@Composable
fun PlayPauseProgressButtonPreviewOnWhite() {
    Box(modifier = Modifier.background(Color.DarkGray)) {
        PlayPauseProgressButton(
            onPlayClick = {},
            onPauseClick = {},
            enabled = true,
            playing = false,
            trackPositionUiModel = TrackPositionUiModel.Actual(0.5f, 50.seconds, 100.seconds)
        )
    }
}
