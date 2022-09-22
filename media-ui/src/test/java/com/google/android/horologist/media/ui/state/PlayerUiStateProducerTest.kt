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

import com.google.android.horologist.media.model.Command
import com.google.android.horologist.media.model.Media
import com.google.android.horologist.media.model.MediaPosition
import com.google.android.horologist.media.model.PlayerState
import com.google.android.horologist.media.ui.ExperimentalHorologistMediaUiApi
import com.google.android.horologist.media.ui.components.controls.SeekButtonIncrement
import com.google.android.horologist.media.ui.state.model.MediaUiModel
import com.google.android.horologist.media.ui.state.model.TrackPositionUiModel
import com.google.android.horologist.test.toolbox.testdoubles.MockPlayerRepository
import com.google.common.truth.Truth
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Test
import kotlin.time.DurationUnit
import kotlin.time.toDuration

class PlayerUiStateProducerTest {
    @Test
    fun `given a PlayerRepository produces correct PlayerUiState`() = runTest {
        // when
        val sut = PlayerUiStateProducer(
            MockPlayerRepository(
                connectedValue = true,
                availableCommandsValue = setOf(Command.PlayPause, Command.SeekBack),
                currentStateValue = PlayerState.Playing,
                currentMediaValue = Media(
                    id = "id",
                    uri = "http://uri",
                    title = "title",
                    artist = "artist"
                ),
                mediaPositionValue = MediaPosition.create(
                    current = 2.toDuration(DurationUnit.SECONDS),
                    duration = 20.toDuration(DurationUnit.SECONDS)
                )
            )
        )

        // then
        Truth.assertThat(sut.playerUiStateFlow.first()).isEqualTo(
            PlayerUiState(
                playEnabled = true,
                pauseEnabled = true,
                seekBackEnabled = true,
                seekForwardEnabled = false,
                seekToPreviousEnabled = false,
                seekToNextEnabled = false,
                shuffleEnabled = false,
                shuffleOn = false,
                playPauseEnabled = true,
                playing = true,
                media = MediaUiModel(id = "id", title = "title", artist = "artist"),
                trackPosition = TrackPositionUiModel(current = 2000, duration = 20000, percent = 0.1f),
                seekBackButtonIncrement = SeekButtonIncrement.Other(0),
                seekForwardButtonIncrement = SeekButtonIncrement.Other(0),
                connected = true
            )
        )
    }
}
