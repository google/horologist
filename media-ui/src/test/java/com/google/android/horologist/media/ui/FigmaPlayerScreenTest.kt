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

@file:OptIn(ExperimentalHorologistMediaUiApi::class, ExperimentalHorologistPaparazziApi::class)

package com.google.android.horologist.media.ui

import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import app.cash.paparazzi.DeviceConfig
import com.google.android.horologist.audio.VolumeState
import com.google.android.horologist.audio.ui.components.SettingsButtonsDefaults
import com.google.android.horologist.audio.ui.components.actions.SetVolumeButton
import com.google.android.horologist.audio.ui.components.actions.SettingsButton
import com.google.android.horologist.media.ui.state.PlayerUiState
import com.google.android.horologist.media.ui.state.model.MediaUiModel
import com.google.android.horologist.media.ui.state.model.TrackPositionUiModel
import com.google.android.horologist.media.ui.uamp.UampColors
import com.google.android.horologist.paparazzi.ExperimentalHorologistPaparazziApi
import com.google.android.horologist.paparazzi.GALAXY_WATCH4_CLASSIC_LARGE
import com.google.android.horologist.paparazzi.WEAR_OS_SMALL_ROUND
import com.google.android.horologist.paparazzi.WEAR_OS_SQUARE
import com.google.android.horologist.paparazzi.WearPaparazzi
import org.junit.Ignore
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized

@Ignore("For interactive use only")
@RunWith(Parameterized::class)
class FigmaPlayerScreenTest(
    private val deviceConfig: DeviceConfig
) {
    @get:Rule
    val paparazzi = WearPaparazzi(
        deviceConfig = deviceConfig
    )

    @Test
    fun mediaPlayerScreen() {
        val playerUiState = PlayerUiState(
            playEnabled = true,
            pauseEnabled = true,
            seekBackEnabled = true,
            seekForwardEnabled = true,
            seekToPreviousEnabled = true,
            seekToNextEnabled = true,
            shuffleEnabled = false,
            shuffleOn = false,
            playPauseEnabled = true,
            playing = false,
            media = MediaUiModel(
                id = "",
                title = "Bat Out of Hell",
                artist = "Meat Loaf"
            ),
            trackPosition = TrackPositionUiModel(current = 75, duration = 100, percent = 0.75f),
            connected = true
        )

        val name = when (deviceConfig) {
            WEAR_OS_SQUARE -> "square"
            WEAR_OS_SMALL_ROUND -> "small_round"
            GALAXY_WATCH4_CLASSIC_LARGE -> "large_round"
            else -> "unknown"
        }

        paparazzi.snapshot(name = name) {
            MediaPlayerTestCase(
                playerUiState = playerUiState,
                colors = UampColors,
                time = "09:30",
                round = deviceConfig != WEAR_OS_SQUARE,
                buttons = {
                    UampSettingsButtons(
                        volumeState = VolumeState(10, 10),
                        onVolumeClick = { }
                    )
                }
            )
        }
    }

    companion object {
        @JvmStatic
        @Parameterized.Parameters
        fun devices() = listOf(WEAR_OS_SQUARE, WEAR_OS_SMALL_ROUND, GALAXY_WATCH4_CLASSIC_LARGE)
    }
}

@Composable
public fun UampSettingsButtons(
    volumeState: VolumeState,
    onVolumeClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        SettingsButton(
            onClick = { },
            imageVector = if (false) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
            contentDescription = ""
        )

        SettingsButtonsDefaults.BrandIcon(
            iconId = R.drawable.ic_uamp,
            enabled = enabled
        )

        SetVolumeButton(
            onVolumeClick = onVolumeClick,
            volumeState = volumeState
        )
    }
}
