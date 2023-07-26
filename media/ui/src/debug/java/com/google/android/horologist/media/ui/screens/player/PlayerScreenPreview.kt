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

@file:OptIn(ExperimentalFoundationApi::class)

package com.google.android.horologist.media.ui.screens.player

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.BlendMode
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
import androidx.wear.compose.ui.tooling.preview.WearPreviewDevices
import androidx.wear.compose.ui.tooling.preview.WearPreviewFontScales
import androidx.wear.compose.ui.tooling.preview.WearPreviewLargeRound
import com.google.android.horologist.audio.ui.VolumeUiState
import com.google.android.horologist.audio.ui.components.SettingsButtons
import com.google.android.horologist.audio.ui.components.SettingsButtonsDefaults
import com.google.android.horologist.compose.pager.PagerScreen
import com.google.android.horologist.compose.tools.ThemeValues
import com.google.android.horologist.compose.tools.WearPreviewThemes
import com.google.android.horologist.logo.R
import com.google.android.horologist.media.ui.components.MediaControlButtons
import com.google.android.horologist.media.ui.components.background.radialBackgroundBrush
import com.google.android.horologist.media.ui.components.display.NothingPlayingDisplay
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
        PagerScreen(
            state = rememberPagerState {
                2
            }
        ) {
            PlayerScreen(
                mediaDisplay = {
                    TextMediaDisplay(
                        title = "Don't Stop Believin'",
                        subtitle = "Journey"
                    )
                },
                controlButtons = {
                    MediaControlButtons(
                        onPlayButtonClick = {},
                        onPauseButtonClick = {},
                        playPauseButtonEnabled = true,
                        playing = true,
                        trackPositionUiModel = TrackPositionUiModel.Actual(
                            0.25f,
                            25.seconds,
                            100.seconds
                        ),
                        onSeekToNextButtonClick = {},
                        seekToNextButtonEnabled = true,
                        onSeekToPreviousButtonClick = {},
                        seekToPreviousButtonEnabled = true
                    )
                },
                buttons = {
                    SettingsButtons(
                        volumeUiState = VolumeUiState(5, 10),
                        onVolumeClick = { },
                        onOutputClick = { },
                        brandIcon = {
                            SettingsButtonsDefaults.BrandIcon(
                                R.drawable.ic_stat_horologist,
                                enabled = true
                            )
                        }
                    )
                }
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
        PagerScreen(
            state = rememberPagerState {
                2
            }
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
                        onPlayButtonClick = {},
                        onPauseButtonClick = {},
                        playPauseButtonEnabled = true,
                        playing = true,
                        trackPositionUiModel = TrackPositionUiModel.Actual(
                            0.75f,
                            75.seconds,
                            100.seconds
                        ),
                        onSeekToNextButtonClick = {},
                        seekToNextButtonEnabled = true,
                        onSeekToPreviousButtonClick = {},
                        seekToPreviousButtonEnabled = true
                    )
                },
                buttons = {
                    SettingsButtons(
                        volumeUiState = VolumeUiState(5, 10),
                        onVolumeClick = { },
                        onOutputClick = { },
                        brandIcon = {
                            SettingsButtonsDefaults.BrandIcon(
                                R.drawable.ic_stat_horologist,
                                enabled = true
                            )
                        }
                    )
                }
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
        PagerScreen(
            state = rememberPagerState {
                2
            }
        ) {
            PlayerScreen(
                mediaDisplay = {
                    TextMediaDisplay(
                        title = "Da Da Da",
                        subtitle = "Casaca"
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
                        volumeUiState = VolumeUiState(5, 10),
                        onVolumeClick = { },
                        onOutputClick = { },
                        brandIcon = {
                            SettingsButtonsDefaults.BrandIcon(
                                R.drawable.ic_stat_horologist,
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
                }
            )
        }
    }
}

@WearPreviewDevices
@WearPreviewFontScales
@Composable
fun PlayerScreenPreviewDevices() {
    UampTheme {
        DefaultMediaPreview()
    }
}

@WearPreviewLargeRound
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
        PagerScreen(
            state = rememberPagerState {
                2
            }
        ) {
            PlayerScreen(
                modifier = Modifier.drawWithCache {
                    val background = radialBackgroundBrush(Color.Yellow, Color.Black)
                    onDrawWithContent {
                        // Clear the circular region so we have transparent pixels to blend against
                        // This enables us to reuse the underlying buffer we are drawing into without
                        // having to consume additional overhead of an offscreen compositing layer
                        drawRect(color = Color.Black, blendMode = BlendMode.Clear)

                        drawContent()

                        // Components on media player may use transparency, so draw in the gaps
                        drawRect(background, blendMode = BlendMode.DstOver)
                    }
                },
                mediaDisplay = {
                    TextMediaDisplay(
                        title = "Don't Stop Believin'",
                        subtitle = "Journey"
                    )
                },
                controlButtons = {
                    MediaControlButtons(
                        onPlayButtonClick = {},
                        onPauseButtonClick = {},
                        playPauseButtonEnabled = true,
                        playing = true,
                        trackPositionUiModel = TrackPositionUiModel.Actual(
                            0.25f,
                            25.seconds,
                            100.seconds
                        ),
                        onSeekToNextButtonClick = {},
                        seekToNextButtonEnabled = true,
                        onSeekToPreviousButtonClick = {},
                        seekToPreviousButtonEnabled = true
                    )
                },
                buttons = {
                    SettingsButtons(
                        volumeUiState = VolumeUiState(5, 10),
                        onVolumeClick = { },
                        onOutputClick = { },
                        brandIcon = {
                            SettingsButtonsDefaults.BrandIcon(
                                R.drawable.ic_stat_horologist,
                                enabled = true
                            )
                        }
                    )
                }
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
        PagerScreen(
            state = rememberPagerState {
                2
            }
        ) {
            PlayerScreen(
                mediaDisplay = { NothingPlayingDisplay(Modifier) },
                controlButtons = {
                    MediaControlButtons(
                        onPlayButtonClick = {},
                        onPauseButtonClick = {},
                        playPauseButtonEnabled = false,
                        playing = false,
                        onSeekToPreviousButtonClick = {},
                        seekToPreviousButtonEnabled = false,
                        onSeekToNextButtonClick = {},
                        seekToNextButtonEnabled = false
                    )
                },
                buttons = {
                    SettingsButtons(
                        volumeUiState = VolumeUiState(5, 10),
                        onVolumeClick = { },
                        onOutputClick = { },
                        brandIcon = {
                            SettingsButtonsDefaults.BrandIcon(
                                R.drawable.ic_stat_horologist,
                                enabled = true
                            )
                        },
                        enabled = false
                    )
                }
            )
        }
    }
}

private const val BACKGROUND_COLOR = 0xFF313234
