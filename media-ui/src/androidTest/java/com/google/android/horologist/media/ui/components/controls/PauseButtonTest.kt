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
import androidx.media3.common.Player.COMMAND_PLAY_PAUSE
import com.google.android.horologist.media.ui.ExperimentalMediaUiApi
import com.google.android.horologist.media.ui.state.PlayerViewModel
import com.google.common.truth.Truth.assertThat
import com.google.test.toolbox.testdoubles.FakePlayerRepository
import org.junit.Rule
import org.junit.Test

class PauseButtonTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun whenPlayPauseCommandBecomesAvailable_thenPauseButtonGetsEnabled() {
        // given
        val playerRepository = FakePlayerRepository()

        val playerViewModel = PlayerViewModel(playerRepository)

        composeTestRule.setContent { PauseButton(playerViewModel = playerViewModel) }

        val pauseButton = composeTestRule.onNodeWithContentDescription("Pause")

        // then
        pauseButton.assertIsNotEnabled()

        // when
        playerRepository.addCommand(COMMAND_PLAY_PAUSE)

        // then
        pauseButton.assertIsEnabled()
    }

    @Test
    fun givenPlayerRepoIsPlaying_whenPauseIsClicked_thenPlayerRepoIsNOTPlaying() {
        // given
        val playerRepository = FakePlayerRepository()
        playerRepository.addCommand(COMMAND_PLAY_PAUSE)
        playerRepository.prepareAndPlay()
        assertThat(playerRepository.isPlaying.value).isTrue()

        val playerViewModel = PlayerViewModel(playerRepository)

        composeTestRule.setContent { PauseButton(playerViewModel = playerViewModel) }

        // when
        composeTestRule.onNodeWithContentDescription("Pause").performClick()

        // then
        composeTestRule.waitUntil(timeoutMillis = 1_000) { !playerRepository.isPlaying.value }
    }
}
