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

@file:OptIn(
    ExperimentalHorologistMediaUiApi::class,
    ExperimentalFoundationApi::class
)

package com.google.android.horologist.media.ui.screens.player

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.CircularProgressIndicator
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Scaffold
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.TimeText
import com.google.android.horologist.audio.VolumeState
import com.google.android.horologist.audio.ui.components.SettingsButtons
import com.google.android.horologist.audio.ui.components.SettingsButtonsDefaults
import com.google.android.horologist.compose.pager.PagerScreen
import com.google.android.horologist.compose.tools.ThemeValues
import com.google.android.horologist.compose.tools.WearLargeRoundDevicePreview
import com.google.android.horologist.compose.tools.WearPreviewDevices
import com.google.android.horologist.compose.tools.WearPreviewFontSizes
import com.google.android.horologist.compose.tools.WearPreviewThemes
import com.google.android.horologist.media.ui.ExperimentalHorologistMediaUiApi
import com.google.android.horologist.media.ui.R
import com.google.android.horologist.media.ui.components.MediaControlButtons
import com.google.android.horologist.media.ui.components.background.RadialBackground
import com.google.android.horologist.media.ui.components.display.TextMediaDisplay
import com.google.android.horologist.media.ui.state.model.TrackPositionUiModel
import com.google.android.horologist.media.ui.uamp.UampTheme
import kotlin.time.Duration.Companion.seconds

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
        PagerScreen(count = 2) {
            PlayerScreen(
                mediaDisplay = {
                    TextMediaDisplay(
                        subtitle = "Journey",
                        title = "Don't Stop Believin'"
                    )
                },
                controlButtons = {
                    MediaControlButtons(
                        onPlayButtonClick = {},
                        onPauseButtonClick = {},
                        playPauseButtonEnabled = true,
                        playing = true,
                        trackPositionUiModel = TrackPositionUiModel.Actual(0.25f, 25.seconds, 100.seconds),
                        onSeekToNextButtonClick = {},
                        seekToNextButtonEnabled = true,
                        onSeekToPreviousButtonClick = {},
                        seekToPreviousButtonEnabled = true
                    )
                },
                buttons = {
                    SettingsButtons(
                        volumeState = VolumeState(5, 10),
                        onVolumeClick = { },
                        onOutputClick = { },
                        brandIcon = {
                            SettingsButtonsDefaults.BrandIcon(
                                R.drawable.ic_uamp,
                                enabled = true
                            )
                        }
                    )
                },
                onVolumeChangeByScroll = {}
            )
        }
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
        PagerScreen(count = 2) {
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
                        onPlayButtonClick = {},
                        onPauseButtonClick = {},
                        playPauseButtonEnabled = true,
                        playing = true,
                        trackPositionUiModel = TrackPositionUiModel.Actual(0.75f, 75.seconds, 100.seconds),
                        onSeekToNextButtonClick = {},
                        seekToNextButtonEnabled = true,
                        onSeekToPreviousButtonClick = {},
                        seekToPreviousButtonEnabled = true
                    )
                },
                buttons = {
                    SettingsButtons(
                        volumeState = VolumeState(5, 10),
                        onVolumeClick = { },
                        onOutputClick = { },
                        brandIcon = {
                            SettingsButtonsDefaults.BrandIcon(
                                R.drawable.ic_uamp,
                                enabled = true
                            )
                        }
                    )
                },
                onVolumeChangeByScroll = {}
            )
        }
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
        PagerScreen(count = 2) {
            PlayerScreen(
                mediaDisplay = {
                    TextMediaDisplay(
                        subtitle = "Casaca",
                        title = "Da Da Da"
                    )
                },
                controlButtons = {
                    MediaControlButtons(
                        onPlayButtonClick = {},
                        onPauseButtonClick = {},
                        playPauseButtonEnabled = true,
                        playing = true,
                        onSeekToNextButtonClick = {},
                        seekToNextButtonEnabled = true,
                        onSeekToPreviousButtonClick = {},
                        seekToPreviousButtonEnabled = true
                    )
                },
                buttons = {
                    SettingsButtons(
                        volumeState = VolumeState(5, 10),
                        onVolumeClick = { },
                        onOutputClick = { },
                        brandIcon = {
                            SettingsButtonsDefaults.BrandIcon(
                                R.drawable.ic_uamp,
                                enabled = true
                            )
                        }
                    )
                },
                background = {
                    Box(modifier = Modifier.fillMaxSize()) {
                        CircularProgressIndicator(
                            modifier = Modifier
                                .align(Alignment.Center)
                                .size(124.dp),
                            progress = 0.75f,
                            indicatorColor = Color.Magenta
                        )
                        CircularProgressIndicator(
                            modifier = Modifier
                                .align(Alignment.Center)
                                .size(132.dp),
                            progress = 0.75f,
                            indicatorColor = Color.White
                        )
                        CircularProgressIndicator(
                            modifier = Modifier
                                .align(Alignment.Center)
                                .size(140.dp),
                            progress = 0.75f,
                            indicatorColor = Color.Blue
                        )
                    }
                },
                onVolumeChangeByScroll = {}
            )
        }
    }
}

@WearPreviewDevices
@WearPreviewFontSizes
@Composable
fun PlayerScreenPreviewDevices() {
    UampTheme {
        DefaultMediaPreview()
    }
}

@WearLargeRoundDevicePreview
@Composable
fun VolumeScreenTheme(
    @PreviewParameter(WearPreviewThemes::class) themeValues: ThemeValues
) {
    MaterialTheme(themeValues.colors) {
        DefaultMediaPreview()
    }
}

@Composable
fun DefaultMediaPreview() {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        timeText = { TimeText() }
    ) {
        PagerScreen(count = 2) {
            PlayerScreen(
                mediaDisplay = {
                    TextMediaDisplay(
                        subtitle = "Journey",
                        title = "Don't Stop Believin'"
                    )
                },
                controlButtons = {
                    MediaControlButtons(
                        onPlayButtonClick = {},
                        onPauseButtonClick = {},
                        playPauseButtonEnabled = true,
                        playing = true,
                        trackPositionUiModel = TrackPositionUiModel.Actual(0.25f, 25.seconds, 100.seconds),
                        onSeekToNextButtonClick = {},
                        seekToNextButtonEnabled = true,
                        onSeekToPreviousButtonClick = {},
                        seekToPreviousButtonEnabled = true
                    )
                },
                buttons = {
                    SettingsButtons(
                        volumeState = VolumeState(5, 10),
                        onVolumeClick = { },
                        onOutputClick = { },
                        brandIcon = {
                            SettingsButtonsDefaults.BrandIcon(
                                R.drawable.ic_uamp,
                                enabled = true
                            )
                        }
                    )
                },
                background = {
                    RadialBackground(color = Color.Yellow)
                },
                onVolumeChangeByScroll = {}
            )
        }
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
fun PlayerScreenPreviewNotingPlayingDisplay() {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        timeText = { TimeText() }
    ) {
        PagerScreen(count = 2) {
            PlayerScreen(
                mediaDisplay = {DefaultPlayerScreenMediaDisplay(media = null, loading = false)},
                controlButtons = {
                    MediaControlButtons(
                        onPlayButtonClick = {},
                        onPauseButtonClick = {},
                        playPauseButtonEnabled = false,
                        playing = false,
                        onSeekToPreviousButtonClick = {},
                        seekToPreviousButtonEnabled = false,
                        onSeekToNextButtonClick = {},
                        seekToNextButtonEnabled = false,
                    )
                },
                buttons = {
                    SettingsButtons(
                        volumeState = VolumeState(5, 10),
                        onVolumeClick = { },
                        onOutputClick = { },
                        brandIcon = {
                            SettingsButtonsDefaults.BrandIcon(
                                R.drawable.ic_uamp,
                                enabled = true
                            )
                        },
                        enabled = false
                    )
                },
                onVolumeChangeByScroll = {},
            )
        }
    }
}

private const val BACKGROUND_COLOR = 0xFF313234
