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

@file:OptIn(ExperimentalMediaUiApi::class)

package com.google.android.horologist.mediaui.components.controls

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.google.android.horologist.mediaui.ExperimentalMediaUiApi
import com.google.android.horologist.mediaui.components.PlayPauseProgressButton

@Preview("Enabled - Playing - Progress 0%")
@Composable
fun PlayPauseProgressButtonPreview0() {
    PlayPauseProgressButton(
        onPlayClick = {},
        onPauseClick = {},
        enabled = true,
        playing = true,
        percent = 0f
    )
}

@Preview("Disabled - Not playing - Progress 25%")
@Composable
fun PlayPauseProgressButtonPreview25() {
    PlayPauseProgressButton(
        onPlayClick = {},
        onPauseClick = {},
        enabled = false,
        playing = false,
        percent = 0.25f
    )
}

@Preview("Disabled - Playing - Progress 75%")
@Composable
fun PlayPauseProgressButtonPreview75() {
    PlayPauseProgressButton(
        onPlayClick = {},
        onPauseClick = {},
        enabled = false,
        playing = true,
        percent = 0.75f
    )
}

@Preview("Enabled - Not playing - Progress 100%")
@Composable
fun PlayPauseProgressButtonPreview100() {
    PlayPauseProgressButton(
        onPlayClick = {},
        onPauseClick = {},
        enabled = true,
        playing = false,
        percent = 1f
    )
}
