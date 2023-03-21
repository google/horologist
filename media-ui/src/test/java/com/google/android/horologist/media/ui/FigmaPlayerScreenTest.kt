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

@file:OptIn(ExperimentalHorologistApi::class)

package com.google.android.horologist.media.ui

import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import app.cash.paparazzi.DeviceConfig
import com.google.android.horologist.annotations.ExperimentalHorologistApi
import com.google.android.horologist.audio.VolumeState
import com.google.android.horologist.audio.ui.components.SettingsButtonsDefaults
import com.google.android.horologist.audio.ui.components.actions.SetVolumeButton
import com.google.android.horologist.audio.ui.components.actions.SettingsButton
import com.google.android.horologist.media.ui.state.PlayerUiState
import com.google.android.horologist.media.ui.state.model.MediaUiModel
import com.google.android.horologist.media.ui.state.model.TrackPositionUiModel
import com.google.android.horologist.media.ui.uamp.UampColors
import com.google.android.horologist.paparazzi.WearPaparazzi
import org.junit.Ignore
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import kotlin.time.Duration.Companion.seconds

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
                subtitle = "Meat Loaf"
            ),
            trackPositionUiModel = TrackPositionUiModel.Actual(percent = 0.75f, position = 75.seconds, duration = 100.seconds),
            connected = true
        )

        val name = when (deviceConfig) {
            DeviceConfig.WEAR_OS_SQUARE -> "square"
            DeviceConfig.WEAR_OS_SMALL_ROUND -> "small_round"
            DeviceConfig.GALAXY_WATCH4_CLASSIC_LARGE -> "large_round"
            else -> "unknown"
        }

        paparazzi.snapshot(name = name) {
            MediaPlayerTestCase(
                playerUiState = playerUiState,
                colors = UampColors,
                time = "09:30",
                round = deviceConfig != DeviceConfig.WEAR_OS_SQUARE,
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
        fun devices() = listOf(
            DeviceConfig.WEAR_OS_SQUARE,
            DeviceConfig.WEAR_OS_SMALL_ROUND,
            DeviceConfig.GALAXY_WATCH4_CLASSIC_LARGE
        )
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
