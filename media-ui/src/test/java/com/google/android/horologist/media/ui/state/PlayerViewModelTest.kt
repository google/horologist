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

package com.google.android.horologist.media.ui.state

import com.google.android.horologist.media.ui.ExperimentalHorologistMediaUiApi
import com.google.android.horologist.media.ui.state.model.MediaUiModel
import com.google.android.horologist.media.ui.state.model.TrackPositionUiModel
import com.google.android.horologist.test.toolbox.testdoubles.MockPlayerRepository
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class PlayerViewModelTest {

    private lateinit var sut: PlayerViewModel

    @Before
    fun setUp() {
        sut = PlayerViewModel(MockPlayerRepository())
    }

    @Test
    fun givenANonConnectedPlayerRepository_thenPlayerUiStateHasPlayAndPauseDisabled() = runTest {
        // when
        val result = sut.playerUiState.first()

        // then
        assertThat(result).isEqualTo(
            PlayerUiState(
                playEnabled = false,
                pauseEnabled = false,
                seekBackEnabled = false,
                seekForwardEnabled = false,
                seekToPreviousEnabled = false,
                seekToNextEnabled = false,
                shuffleEnabled = false,
                shuffleOn = false,
                playPauseEnabled = false,
                playing = false,
                media = MediaUiModel(id = "", title = null, artist = null),
                trackPosition = TrackPositionUiModel(current = 0, duration = 0, percent = 0f),
                connected = false
            )
        )
    }
}
