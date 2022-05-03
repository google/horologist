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

package com.google.android.horologist.media.ui.components.controls

import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.performClick
import androidx.media3.common.Player
import com.google.android.horologist.media.ui.ExperimentalMediaUiApi
import com.google.android.horologist.media.ui.state.PlayerViewModel
import com.google.common.truth.Truth.assertThat
import com.google.test.toolbox.testdoubles.FakePlayerRepository
import org.junit.Rule
import org.junit.Test

class PlayButtonTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun whenPlayPauseCommandBecomesAvailable_thenPlayButtonGetsEnabled() {
        // given
        val playerRepository = FakePlayerRepository()

        val playerViewModel = PlayerViewModel(playerRepository)

        composeTestRule.setContent { PlayButton(playerViewModel = playerViewModel) }

        val playButton = composeTestRule.onNodeWithContentDescription("Play")

        // then
        playButton.assertIsNotEnabled()

        // when
        playerRepository.addCommand(Player.COMMAND_PLAY_PAUSE)

        // then
        playButton.assertIsEnabled()
    }

    @Test
    fun givenPlayerRepoIsNOTPlaying_whenPlayIsClicked_thenPlayerRepoIsPlaying() {
        // given
        val playerRepository = FakePlayerRepository()
        playerRepository.addCommand(Player.COMMAND_PLAY_PAUSE)
        assertThat(playerRepository.isPlaying.value).isFalse()

        val playerViewModel = PlayerViewModel(playerRepository)

        composeTestRule.setContent { PlayButton(playerViewModel = playerViewModel) }

        // when
        composeTestRule.onNodeWithContentDescription("Play").performClick()

        // then
        composeTestRule.waitUntil(timeoutMillis = 1_000) { playerRepository.isPlaying.value }
    }
}
