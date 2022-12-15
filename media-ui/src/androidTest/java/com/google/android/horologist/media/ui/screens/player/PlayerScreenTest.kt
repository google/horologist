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

package com.google.android.horologist.media.ui.screens.player

import androidx.compose.ui.semantics.ProgressBarRangeInfo
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.hasProgressBarRangeInfo
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.performClick
import androidx.test.filters.FlakyTest
import androidx.test.filters.LargeTest
import androidx.wear.compose.material.Text
import com.google.android.horologist.media.model.Command
import com.google.android.horologist.media.model.Media
import com.google.android.horologist.media.model.PlaybackState
import com.google.android.horologist.media.model.PlayerState
import com.google.android.horologist.media.ui.ExperimentalHorologistMediaUiApi
import com.google.android.horologist.media.ui.state.PlayerViewModel
import com.google.android.horologist.test.toolbox.matchers.hasProgressBar
import com.google.android.horologist.test.toolbox.testdoubles.FakePlayerRepository
import com.google.common.truth.Truth.assertThat
import org.junit.Rule
import org.junit.Test

@FlakyTest(detail = "https://github.com/google/horologist/issues/407")
@LargeTest
class PlayerScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun givenShowProgressIsTrue_thenProgressBarIsDisplayed() {
        // given
        var playerRepository = FakePlayerRepository()
        val playerViewModel = PlayerViewModel(playerRepository)

        composeTestRule.setContent { PlayerScreen(playerViewModel = playerViewModel) }

        // then
        composeTestRule.onNode(hasProgressBar())
            .assertExists()
    }

    @Test
    fun givenShowProgressIsFalse_thenProgressBarIsNOTDisplayed() {
        // given
        val playerRepository = FakePlayerRepository()
        playerRepository.setPosition(PlaybackState.Unknown)
        val playerViewModel = PlayerViewModel(playerRepository)

        composeTestRule.setContent {
            PlayerScreen(
                playerViewModel = playerViewModel,
                controlButtons = { playerUiController, playerUiState ->
                    DefaultPlayerScreenControlButtons(
                        playerController = playerUiController,
                        playerUiState = playerUiState
                    )
                }
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
        playerRepository.addCommand(Command.PlayPause)

        val playerViewModel = PlayerViewModel(playerRepository)

        assertThat(playerRepository.currentState.value).isNotEqualTo(PlayerState.Playing)

        composeTestRule.setContent { PlayerScreen(playerViewModel = playerViewModel) }

        // when
        composeTestRule.onNodeWithContentDescription("Play")
            .performClick()

        // then
        composeTestRule.waitUntil(timeoutMillis = 1_000) {
            playerRepository.currentState.value == PlayerState.Playing
        }
    }

    @Test
    fun givenPlayerRepoIsPlaying_whenPauseIsClicked_thenPlayerRepoIsNOTPlaying() {
        // given
        val playerRepository = FakePlayerRepository()
        playerRepository.addCommand(Command.PlayPause)
        playerRepository.play()

        val playerViewModel = PlayerViewModel(playerRepository)

        assertThat(playerRepository.currentState.value).isEqualTo(PlayerState.Playing)

        composeTestRule.setContent { PlayerScreen(playerViewModel = playerViewModel) }

        // when
        composeTestRule.onNodeWithContentDescription("Pause")
            .performClick()

        // then
        composeTestRule.waitUntil(timeoutMillis = 1_000) {
            playerRepository.currentState.value != PlayerState.Playing
        }
    }

    @Test
    fun givenMediaList_whenSeekToPreviousIsClicked_thenPreviousItemIsPlaying() {
        // given
        val playerRepository = FakePlayerRepository()

        val media1 = Media(id = "", uri = "", title = "", artist = "")
        val media2 = Media(id = "", uri = "", title = "", artist = "")
        playerRepository.setMediaList(listOf(media1, media2))
        playerRepository.seekToDefaultPosition(1)
        playerRepository.play()

        val playerViewModel = PlayerViewModel(playerRepository)

        assertThat(playerRepository.currentMedia.value).isEqualTo(media2)

        composeTestRule.setContent { PlayerScreen(playerViewModel = playerViewModel) }

        // when
        composeTestRule.onNodeWithContentDescription("Previous")
            .performClick()

        // then
        composeTestRule.waitUntil(timeoutMillis = 1_000) {
            playerRepository.currentMedia.value == media1
        }
    }

    @Test
    fun givenMediaList_whenSeekToNextIsClicked_thenNextItemIsPlaying() {
        // given
        val playerRepository = FakePlayerRepository()

        val media1 = Media(id = "", uri = "", title = "", artist = "")
        val media2 = Media(id = "", uri = "", title = "", artist = "")
        playerRepository.setMediaList(listOf(media1, media2))
        playerRepository.seekToDefaultPosition(0)
        playerRepository.play()

        val playerViewModel = PlayerViewModel(playerRepository)

        assertThat(playerRepository.currentMedia.value).isEqualTo(media1)

        composeTestRule.setContent { PlayerScreen(playerViewModel = playerViewModel) }

        // when
        composeTestRule.onNodeWithContentDescription("Next")
            .performClick()

        // then
        composeTestRule.waitUntil(timeoutMillis = 1_000) {
            playerRepository.currentMedia.value == media2
        }
    }

    @Test
    fun whenUpdatePosition_thenProgressIsUpdated() {
        // given
        val playerRepository = FakePlayerRepository()
        val playerViewModel = PlayerViewModel(playerRepository)

        composeTestRule.setContent { PlayerScreen(playerViewModel = playerViewModel) }

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
        playerRepository.play()

        val playerViewModel = PlayerViewModel(playerRepository)

        composeTestRule.setContent { PlayerScreen(playerViewModel = playerViewModel) }

        val button = composeTestRule.onNodeWithContentDescription("Pause")

        // then
        button.assertIsNotEnabled()

        // when
        playerRepository.addCommand(Command.PlayPause)

        // then
        button.assertIsEnabled()
    }

    @Test
    fun whenSeekToPreviousMediaCommandBecomesAvailable_thenSeekToPreviousButtonGetsEnabled() {
        // given
        val playerRepository = FakePlayerRepository()
        val playerViewModel = PlayerViewModel(playerRepository)

        composeTestRule.setContent { PlayerScreen(playerViewModel = playerViewModel) }

        val button = composeTestRule.onNodeWithContentDescription("Previous")

        // then
        button.assertIsNotEnabled()

        // when
        playerRepository.addCommand(Command.SkipToPreviousMedia)

        // then
        button.assertIsEnabled()
    }

    @Test
    fun whenSeekToNextMediaCommandBecomesAvailable_thenSeekToNextButtonGetsEnabled() {
        // given
        val playerRepository = FakePlayerRepository()
        val playerViewModel = PlayerViewModel(playerRepository)

        composeTestRule.setContent { PlayerScreen(playerViewModel = playerViewModel) }

        val button = composeTestRule.onNodeWithContentDescription("Next")

        // then
        button.assertIsNotEnabled()

        // when
        playerRepository.addCommand(Command.SkipToNextMedia)

        // then
        button.assertIsEnabled()
    }

    @Test
    fun givenMedia_thenCorrectTitleAndArtistAndIsDisplayed() {
        // given
        val playerRepository = FakePlayerRepository()
        val artist = "artist"
        val title = "title"
        val media = Media(id = "", uri = "", title = title, artist = artist)
        playerRepository.setMedia(media)
        playerRepository.play()

        val playerViewModel = PlayerViewModel(playerRepository)

        composeTestRule.setContent { PlayerScreen(playerViewModel = playerViewModel) }

        // then
        composeTestRule.onNode(hasText(artist)).assertExists()
        composeTestRule.onNode(hasText(title)).assertExists()
    }

    @Test
    fun givenCustomMediaDisplay_thenCustomIsDisplayed() {
        // given
        val playerRepository = FakePlayerRepository()
        val artist = "artist"
        val title = "title"
        val media = Media(id = "", uri = "", title = title, artist = artist)
        playerRepository.setMedia(media)
        playerRepository.play()

        val playerViewModel = PlayerViewModel(playerRepository)

        composeTestRule.setContent {
            PlayerScreen(
                playerViewModel = playerViewModel,
                mediaDisplay = { Text("Custom") }
            )
        }

        // then
        composeTestRule.onNode(hasText("Custom")).assertExists()

        composeTestRule.onNode(hasText(artist)).assertDoesNotExist()
        composeTestRule.onNode(hasText(title)).assertDoesNotExist()
    }

    @Test
    fun givenCustomControlButtons_thenCustomIsDisplayed() {
        // given
        composeTestRule.setContent {
            PlayerScreen(
                playerViewModel = PlayerViewModel(FakePlayerRepository()),
                controlButtons = { _, _ -> Text("Custom") }
            )
        }

        // then
        composeTestRule.onNode(hasText("Custom")).assertExists()

        composeTestRule.onNodeWithContentDescription("Previous").assertDoesNotExist()
        composeTestRule.onNodeWithContentDescription("Next").assertDoesNotExist()
        composeTestRule.onNodeWithContentDescription("Play").assertDoesNotExist()
        composeTestRule.onNodeWithContentDescription("Pause").assertDoesNotExist()
        composeTestRule.onNode(hasProgressBar()).assertDoesNotExist()
    }

    @Test
    fun givenCustomButtons_thenCustomIsDisplayed() {
        // given
        composeTestRule.setContent {
            PlayerScreen(
                playerViewModel = PlayerViewModel(FakePlayerRepository()),
                buttons = { Text("Custom") }
            )
        }

        // then
        composeTestRule.onNode(hasText("Custom")).assertExists()
    }

    @Test
    fun givenCustomBackground_thenCustomIsDisplayed() {
        // given
        composeTestRule.setContent {
            PlayerScreen(
                playerViewModel = PlayerViewModel(FakePlayerRepository()),
                background = { Text("Custom") }
            )
        }

        // then
        composeTestRule.onNode(hasText("Custom")).assertExists()
    }
}
