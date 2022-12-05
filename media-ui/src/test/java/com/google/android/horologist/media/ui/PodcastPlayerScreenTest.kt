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

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.google.android.horologist.media.ui.components.PodcastControlButtons
import com.google.android.horologist.media.ui.components.controls.SeekButtonIncrement
import com.google.android.horologist.media.ui.state.PlayerUiState
import com.google.android.horologist.media.ui.state.model.MediaUiModel
import com.google.android.horologist.media.ui.state.model.TrackPositionUiModel
import com.google.android.horologist.paparazzi.ExperimentalHorologistPaparazziApi
import com.google.android.horologist.paparazzi.WearPaparazzi
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized

@RunWith(Parameterized::class)
class PodcastPlayerScreenTest(
    private val options: PodcastOptions
) {
    @get:Rule
    val paparazzi = WearPaparazzi()

    @Test
    fun mediaPlayerScreen() {
        val playerUiState = PlayerUiState(
            playEnabled = true,
            pauseEnabled = true,
            seekBackEnabled = true,
            seekForwardEnabled = true,
            seekToPreviousEnabled = false,
            seekToNextEnabled = true,
            shuffleEnabled = false,
            shuffleOn = false,
            playPauseEnabled = true,
            playing = true,
            media = MediaUiModel(
                id = "",
                title = "The power of types",
                subtitle = "Kotlinconf"
            ),
            trackPosition = TrackPositionUiModel(current = 30, duration = 225, percent = 0.133f, showProgress = true),
            connected = true
        )

        paparazzi.snapshot(options.toString()) {
            Box(modifier = Modifier.background(Color.Black)) {
                MediaPlayerTestCase(playerUiState = playerUiState, controlButtons = {
                    PodcastControlButtons(
                        onPlayButtonClick = { },
                        onPauseButtonClick = { },
                        playPauseButtonEnabled = playerUiState.playPauseEnabled,
                        playing = playerUiState.playing,
                        percent = playerUiState.trackPosition?.percent ?: 0f,
                        showProgress = playerUiState.trackPosition?.showProgress ?: false,
                        onSeekBackButtonClick = { },
                        seekBackButtonEnabled = playerUiState.seekBackEnabled,
                        onSeekForwardButtonClick = { },
                        seekForwardButtonEnabled = playerUiState.seekForwardEnabled,
                        seekBackButtonIncrement = options.seekBackButtonIncrement,
                        seekForwardButtonIncrement = options.seekForwardButtonIncrement
                    )
                })
            }
        }
    }

    companion object {
        @JvmStatic
        @Parameterized.Parameters
        fun options(): List<PodcastOptions> = listOf(
            PodcastOptions(SeekButtonIncrement.Unknown, SeekButtonIncrement.Unknown),
            PodcastOptions(SeekButtonIncrement.Ten, SeekButtonIncrement.Ten),
            PodcastOptions(SeekButtonIncrement.Five, SeekButtonIncrement.Thirty)
        )
    }

    data class PodcastOptions(
        val seekBackButtonIncrement: SeekButtonIncrement,
        val seekForwardButtonIncrement: SeekButtonIncrement
    ) {
        override fun toString(): String {
            return "${seekBackButtonIncrement}_$seekForwardButtonIncrement"
        }
    }
}
