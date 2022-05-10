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

package com.google.android.horologist.media.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.ButtonDefaults
import androidx.wear.compose.material.CircularProgressIndicator
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Scaffold
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.TimeText
import com.google.android.horologist.media.ui.ExperimentalHorologistMediaUiApi
import com.google.android.horologist.media.ui.components.MediaControlButtons
import com.google.android.horologist.media.ui.components.TextMediaDisplay

@Preview(
    group = "Large Round",
    device = Devices.WEAR_OS_LARGE_ROUND,
    showSystemUi = true,
    backgroundColor = BACKGROUND_COLOR,
    showBackground = true
)
@Preview(
    group = "Small Round",
    device = Devices.WEAR_OS_SMALL_ROUND,
    showSystemUi = true,
    backgroundColor = BACKGROUND_COLOR,
    showBackground = true
)
@Preview(
    group = "Square",
    device = Devices.WEAR_OS_SQUARE,
    showSystemUi = true,
    backgroundColor = BACKGROUND_COLOR,
    showBackground = true
)
@Composable
fun PlayerScreenPreview() {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        timeText = { TimeText() }
    ) {
        PlayerScreen(
            mediaDisplay = {
                TextMediaDisplay(
                    artist = "Journey",
                    title = "Don't Stop Believin'"
                )
            },
            controlButtons = {
                MediaControlButtons(
                    onPlayClick = {},
                    onPauseClick = {},
                    playPauseEnabled = true,
                    playing = true,
                    percent = 0.25F,
                    onSeekToNextButtonClick = {},
                    seekToNextButtonEnabled = true,
                    onSeekToPreviousButtonClick = {},
                    seekToPreviousButtonEnabled = true,
                )
            },
            buttons = {},
        )
    }
}

@Preview(
    name = "With custom media display",
    group = "Large Round",
    device = Devices.WEAR_OS_LARGE_ROUND,
    showSystemUi = true,
    backgroundColor = BACKGROUND_COLOR,
    showBackground = true
)
@Preview(
    name = "With custom media display",
    group = "Small Round",
    device = Devices.WEAR_OS_SMALL_ROUND,
    showSystemUi = true,
    backgroundColor = BACKGROUND_COLOR,
    showBackground = true
)
@Preview(
    name = "With custom media display",
    group = "Square",
    device = Devices.WEAR_OS_SQUARE,
    showSystemUi = true,
    backgroundColor = BACKGROUND_COLOR,
    showBackground = true
)
@Composable
fun PlayerScreenPreviewCustomMediaDisplay() {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        timeText = { TimeText() }
    ) {
        PlayerScreen(
            mediaDisplay = {
                Text(
                    "RTÉ Lyric FM\nRTÉ",
                    style = MaterialTheme.typography.title2.copy(color = Color.Red),
                    textAlign = TextAlign.Center
                )
            },
            controlButtons = {
                MediaControlButtons(
                    onPlayClick = {},
                    onPauseClick = {},
                    playPauseEnabled = true,
                    playing = true,
                    percent = 0.75F,
                    onSeekToNextButtonClick = {},
                    seekToNextButtonEnabled = true,
                    onSeekToPreviousButtonClick = {},
                    seekToPreviousButtonEnabled = true,
                )
            },
            buttons = {
                Button(
                    onClick = { },
                    colors = ButtonDefaults.iconButtonColors(),
                    modifier = Modifier.size(ButtonDefaults.SmallButtonSize),
                ) {
                    Icon(imageVector = Icons.Default.VolumeUp, contentDescription = "Set Volume")
                }
            },
        )
    }
}

@Preview(
    name = "With custom background",
    group = "Large Round",
    device = Devices.WEAR_OS_LARGE_ROUND,
    showSystemUi = true,
    backgroundColor = BACKGROUND_COLOR,
    showBackground = true
)
@Preview(
    name = "With custom background",
    group = "Small Round",
    device = Devices.WEAR_OS_SMALL_ROUND,
    showSystemUi = true,
    backgroundColor = BACKGROUND_COLOR,
    showBackground = true
)
@Preview(
    name = "With custom background",
    group = "Square",
    device = Devices.WEAR_OS_SQUARE,
    showSystemUi = true,
    backgroundColor = BACKGROUND_COLOR,
    showBackground = true
)
@Composable
fun PlayerScreenPreviewCustomBackground() {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        timeText = { TimeText() }
    ) {
        PlayerScreen(
            mediaDisplay = {
                TextMediaDisplay(
                    artist = "Casaca",
                    title = "Da Da Da"
                )
            },
            controlButtons = {
                MediaControlButtons(
                    onPlayClick = {},
                    onPauseClick = {},
                    playPauseEnabled = true,
                    playing = true,
                    onSeekToNextButtonClick = {},
                    seekToNextButtonEnabled = true,
                    onSeekToPreviousButtonClick = {},
                    seekToPreviousButtonEnabled = true,
                )
            },
            buttons = {},
            background = {
                Box(modifier = Modifier.fillMaxSize()) {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .size(124.dp),
                        progress = 0.75f,
                        indicatorColor = Color.Magenta,
                    )
                    CircularProgressIndicator(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .size(132.dp),
                        progress = 0.75f,
                        indicatorColor = Color.White,
                    )
                    CircularProgressIndicator(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .size(140.dp),
                        progress = 0.75f,
                        indicatorColor = Color.Blue,
                    )
                }
            }
        )
    }
}

private const val BACKGROUND_COLOR = 0xFF313234
