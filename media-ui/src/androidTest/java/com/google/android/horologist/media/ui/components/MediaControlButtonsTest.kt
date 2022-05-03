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

package com.google.android.horologist.media.ui.components

import androidx.compose.ui.semantics.ProgressBarRangeInfo
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.hasProgressBarRangeInfo
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.performClick
import androidx.media3.common.MediaItem
import androidx.media3.common.Player.COMMAND_PLAY_PAUSE
import androidx.media3.common.Player.COMMAND_SEEK_TO_NEXT_MEDIA_ITEM
import androidx.media3.common.Player.COMMAND_SEEK_TO_PREVIOUS_MEDIA_ITEM
import com.google.android.horologist.media.ui.ExperimentalMediaUiApi
import com.google.android.horologist.media.ui.state.PlayerViewModel
import com.google.common.truth.Truth.assertThat
import com.google.test.toolbox.hasProgressBar
import com.google.test.toolbox.testdoubles.FakePlayerRepository
import org.junit.Rule
import org.junit.Test

class MediaControlButtonsTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun givenShowProgressIsTrue_thenProgressBarIsDisplayed() {
        // given
        val playerRepository = FakePlayerRepository()
        val playerViewModel = PlayerViewModel(playerRepository)

        composeTestRule.setContent { MediaControlButtons(playerViewModel = playerViewModel) }

        // then
        composeTestRule.onNode(hasProgressBar())
            .assertExists()
    }

    @Test
    fun givenShowProgressIsFalse_thenProgressBarIsNOTDisplayed() {
        // given
        val showProgress = false

        val playerRepository = FakePlayerRepository()
        val playerViewModel = PlayerViewModel(playerRepository)

        composeTestRule.setContent {
            MediaControlButtons(
                playerViewModel = playerViewModel,
                showProgress = showProgress
            )
        }

        // then
        composeTestRule.onNode(hasProgressBar())
            .assertDoesNotExist()
    }

    @Test
    fun givenPlayerRepoIsNOTPlaying_whenPlayIsClicked_thenPlayerRepoIsPlaying() {
        // given
        val playerRepository = FakePlayerRepository()
        playerRepository.addCommand(COMMAND_PLAY_PAUSE)

        val playerViewModel = PlayerViewModel(playerRepository)

        assertThat(playerRepository.isPlaying.value).isFalse()

        composeTestRule.setContent { MediaControlButtons(playerViewModel = playerViewModel) }

        // when
        composeTestRule.onNodeWithContentDescription("Play")
            .performClick()

        // then
        composeTestRule.waitUntil(timeoutMillis = 1_000) { playerRepository.isPlaying.value }
    }

    @Test
    fun givenPlayerRepoIsPlaying_whenPauseIsClicked_thenPlayerRepoIsNOTPlaying() {
        // given
        val playerRepository = FakePlayerRepository()
        playerRepository.addCommand(COMMAND_PLAY_PAUSE)
        playerRepository.prepareAndPlay()

        val playerViewModel = PlayerViewModel(playerRepository)

        assertThat(playerRepository.isPlaying.value).isTrue()

        composeTestRule.setContent { MediaControlButtons(playerViewModel = playerViewModel) }

        // when
        composeTestRule.onNodeWithContentDescription("Pause")
            .performClick()

        // then
        composeTestRule.waitUntil(timeoutMillis = 1_000) { !playerRepository.isPlaying.value }
    }

    @Test
    fun givenMediaItemList_whenSeekToPreviousIsClicked_thenPreviousItemIsPlaying() {
        // given
        val playerRepository = FakePlayerRepository()

        val mediaItem1 = MediaItem.Builder().build()
        val mediaItem2 = MediaItem.Builder().build()
        playerRepository.prepareAndPlay(listOf(mediaItem1, mediaItem2), 1)

        val playerViewModel = PlayerViewModel(playerRepository)

        composeTestRule.setContent { MediaControlButtons(playerViewModel = playerViewModel) }

        // when
        composeTestRule.onNodeWithContentDescription("Previous")
            .performClick()

        // then
        composeTestRule.waitUntil(timeoutMillis = 1_000) { playerRepository.currentMediaItem.value == mediaItem1 }
    }

    @Test
    fun givenMediaItemList_whenSeekToNextIsClicked_thenNextItemIsPlaying() {
        // given
        val playerRepository = FakePlayerRepository()

        val mediaItem1 = MediaItem.Builder().build()
        val mediaItem2 = MediaItem.Builder().build()
        playerRepository.prepareAndPlay(listOf(mediaItem1, mediaItem2), 0)

        val playerViewModel = PlayerViewModel(playerRepository)

        composeTestRule.setContent { MediaControlButtons(playerViewModel = playerViewModel) }

        // when
        composeTestRule.onNodeWithContentDescription("Next")
            .performClick()

        // then
        composeTestRule.waitUntil(timeoutMillis = 1_000) { playerRepository.currentMediaItem.value == mediaItem2 }
    }

    @Test
    fun whenUpdatePosition_thenProgressIsUpdated() {
        // given
        val playerRepository = FakePlayerRepository()
        val playerViewModel = PlayerViewModel(playerRepository)

        composeTestRule.setContent { MediaControlButtons(playerViewModel = playerViewModel) }

        // when
        playerRepository.updatePosition()

        // then
        composeTestRule.onNode(hasProgressBarRangeInfo(ProgressBarRangeInfo(0.1f, 0.0f..1.0f)))
            .assertIsDisplayed()

        // when
        playerRepository.updatePosition()

        // then
        composeTestRule.onNode(hasProgressBarRangeInfo(ProgressBarRangeInfo(0.2f, 0.0f..1.0f)))
            .assertIsDisplayed()
    }

    @Test
    fun whenPlayPauseCommandBecomesAvailable_thenPlayPauseButtonGetsEnabled() {
        // given
        val playerRepository = FakePlayerRepository()
        playerRepository.prepareAndPlay()

        val playerViewModel = PlayerViewModel(playerRepository)

        composeTestRule.setContent { MediaControlButtons(playerViewModel = playerViewModel) }

        val button = composeTestRule.onNodeWithContentDescription("Pause")

        // then
        button.assertIsNotEnabled()

        // when
        playerRepository.addCommand(COMMAND_PLAY_PAUSE)

        // then
        button.assertIsEnabled()
    }

    @Test
    fun whenSeekToPreviousMediaItemCommandBecomesAvailable_thenSeekToPreviousButtonGetsEnabled() {
        // given
        val playerRepository = FakePlayerRepository()
        val playerViewModel = PlayerViewModel(playerRepository)

        composeTestRule.setContent { MediaControlButtons(playerViewModel = playerViewModel) }

        val button = composeTestRule.onNodeWithContentDescription("Previous")

        // then
        button.assertIsNotEnabled()

        // when
        playerRepository.addCommand(COMMAND_SEEK_TO_PREVIOUS_MEDIA_ITEM)

        // then
        button.assertIsEnabled()
    }

    @Test
    fun whenSeekToNextMediaItemCommandBecomesAvailable_thenSeekToNextButtonGetsEnabled() {
        // given
        val playerRepository = FakePlayerRepository()
        val playerViewModel = PlayerViewModel(playerRepository)

        composeTestRule.setContent { MediaControlButtons(playerViewModel = playerViewModel) }

        val button = composeTestRule.onNodeWithContentDescription("Next")

        // then
        button.assertIsNotEnabled()

        // when
        playerRepository.addCommand(COMMAND_SEEK_TO_NEXT_MEDIA_ITEM)

        // then
        button.assertIsEnabled()
    }
}
