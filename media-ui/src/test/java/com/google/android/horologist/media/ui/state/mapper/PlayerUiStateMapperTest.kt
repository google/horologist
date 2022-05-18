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

package com.google.android.horologist.media.ui.state.mapper

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.android.horologist.media.model.Command
import com.google.android.horologist.media.model.MediaItem
import com.google.android.horologist.media.model.MediaItemPosition
import com.google.android.horologist.media.model.PlayerState
import com.google.android.horologist.media.ui.ExperimentalHorologistMediaUiApi
import com.google.android.horologist.media.ui.state.PlayerUiState
import com.google.common.truth.Truth.assertThat
import org.junit.Assert.assertNotNull
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config
import kotlin.time.Duration.Companion.seconds

@RunWith(AndroidJUnit4::class)
@Config(manifest = Config.NONE)
class PlayerUiStateMapperTest {

    @Test
    fun givenNoCommandsAreAvailable_thenAllIsDisabled() {
        // given
        val commands = setOf<Command>()

        // when
        val result = PlayerUiStateMapper.map(
            currentState = PlayerState.Ready,
            availableCommands = commands,
            mediaItem = null,
            mediaItemPosition = null,
            shuffleModeEnabled = false,
        )

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
                mediaItem = null,
                trackPosition = null
            )
        )
    }

    @Test
    fun givenPlayPauseCommandIsAvailable_thenPlayIsEnabled() {
        // given
        val commands = setOf(Command.PlayPause)

        // when
        val result = PlayerUiStateMapper.map(
            currentState = PlayerState.Ready,
            availableCommands = commands,
            mediaItem = null,
            mediaItemPosition = null,
            shuffleModeEnabled = false,
        )

        // then
        assertThat(result.playEnabled).isTrue()
    }

    @Test
    fun givenPlayPauseCommandIsAvailable_thenPauseIsEnabled() {
        // given
        val commands = setOf(Command.PlayPause)

        // when
        val result = PlayerUiStateMapper.map(
            currentState = PlayerState.Ready,
            availableCommands = commands,
            mediaItem = null,
            mediaItemPosition = null,
            shuffleModeEnabled = false,
        )

        // then
        assertThat(result.pauseEnabled).isTrue()
    }

    @Test
    fun givenSeekBackCommandIsAvailable_thenSeekBackIsEnabled() {
        // given
        val commands = setOf(Command.SeekBack)

        // when
        val result = PlayerUiStateMapper.map(
            currentState = PlayerState.Ready,
            availableCommands = commands,
            mediaItem = null,
            mediaItemPosition = null,
            shuffleModeEnabled = false,
        )

        // then
        assertThat(result.seekBackEnabled).isTrue()
    }

    @Test
    fun givenSeekForwardCommandIsAvailable_thenSeekForwardIsEnabled() {
        // given
        val commands = setOf(Command.SeekForward)

        // when
        val result = PlayerUiStateMapper.map(
            currentState = PlayerState.Ready,
            availableCommands = commands,
            mediaItem = null,
            mediaItemPosition = null,
            shuffleModeEnabled = false,
        )

        // then
        assertThat(result.seekForwardEnabled).isTrue()
    }

    @Test
    fun givenSkipToPreviousMediaItemCommandIsAvailable_thenSeekToPreviousIsEnabled() {
        // given
        val commands = setOf(Command.SkipToPreviousMediaItem)

        // when
        val result = PlayerUiStateMapper.map(
            currentState = PlayerState.Ready,
            availableCommands = commands,
            mediaItem = null,
            mediaItemPosition = null,
            shuffleModeEnabled = false,
        )

        // then
        assertThat(result.seekToPreviousEnabled).isTrue()
    }

    @Test
    fun givenSkipToNextMediaItemCommandIsAvailable_thenSeekToNextIsEnabled() {
        // given
        val commands = setOf(Command.SkipToNextMediaItem)

        // when
        val result = PlayerUiStateMapper.map(
            currentState = PlayerState.Ready,
            availableCommands = commands,
            mediaItem = null,
            mediaItemPosition = null,
            shuffleModeEnabled = false,
        )

        // then
        assertThat(result.seekToNextEnabled).isTrue()
    }

    @Test
    fun givenSetShuffleModeCommandIsAvailable_thenShuffleIsEnabled() {
        // given
        val commands = setOf(Command.SetShuffle)

        // when
        val result = PlayerUiStateMapper.map(
            currentState = PlayerState.Ready,
            availableCommands = commands,
            mediaItem = null,
            mediaItemPosition = null,
            shuffleModeEnabled = false,
        )

        // then
        assertThat(result.shuffleEnabled).isTrue()
    }

    @Test
    fun givenShuffleDisabled_thenShuffleOnIsFalse() {
        // given
        val shuffleEnabled = false

        // when
        val result = PlayerUiStateMapper.map(
            currentState = PlayerState.Ready,
            availableCommands = emptySet(),
            mediaItem = null,
            mediaItemPosition = null,
            shuffleModeEnabled = shuffleEnabled,
        )

        // then
        assertThat(result.shuffleOn).isEqualTo(shuffleEnabled)
    }

    @Test
    fun givenShuffleEnabled_thenShuffleOnIsTrue() {
        // given
        val shuffleEnabled = true

        // when
        val result = PlayerUiStateMapper.map(
            currentState = PlayerState.Ready,
            availableCommands = emptySet(),
            mediaItem = null,
            mediaItemPosition = null,
            shuffleModeEnabled = shuffleEnabled,
        )

        // then
        assertThat(result.shuffleOn).isEqualTo(shuffleEnabled)
    }

    @Test
    fun givenPlayPauseCommandIsAvailable_thenPlayPauseIsEnabled() {
        // given
        val commands = setOf(Command.PlayPause)

        // when
        val result = PlayerUiStateMapper.map(
            currentState = PlayerState.Ready,
            availableCommands = commands,
            mediaItem = null,
            mediaItemPosition = null,
            shuffleModeEnabled = false,
        )

        // then
        assertThat(result.playPauseEnabled).isTrue()
    }

    @Test
    fun givenIsNOTPlaying_thenPlayingIsFalse() {
        // given
        val state = PlayerState.Ready

        // when
        val result = PlayerUiStateMapper.map(
            currentState = state,
            availableCommands = emptySet(),
            mediaItem = null,
            mediaItemPosition = null,
            shuffleModeEnabled = false,
        )

        // then
        assertThat(result.playing).isFalse()
    }

    @Test
    fun givenIsPlaying_thenPlayingIsTrue() {
        // given
        val state = PlayerState.Playing

        // when
        val result = PlayerUiStateMapper.map(
            currentState = state,
            availableCommands = emptySet(),
            mediaItem = null,
            mediaItemPosition = null,
            shuffleModeEnabled = false,
        )

        // then
        assertThat(result.playing).isTrue()
    }

    @Test
    fun givenMediaItem_thenMediaItemIsMappedCorrectly() {
        // given
        val title = "title"
        val artist = "artist"
        val mediaItem = MediaItem(title = title, artist = artist)

        // when
        val result = PlayerUiStateMapper.map(
            currentState = PlayerState.Ready,
            availableCommands = emptySet(),
            mediaItem = mediaItem,
            mediaItemPosition = null,
            shuffleModeEnabled = false,
        )

        // then
        assertNotNull(result.mediaItem)
        val expectedMediaItem = result.mediaItem!!
        assertThat(expectedMediaItem.title).isEqualTo(title)
        assertThat(expectedMediaItem.artist).isEqualTo(artist)
    }

    @Test
    fun givenMediaItemPosition_thenTrackPositionIsMappedCorrectly() {
        // given
        val current = 1.seconds
        val duration = 2.seconds
        val mediaItemPosition = MediaItemPosition.create(current, duration)

        // when
        val result = PlayerUiStateMapper.map(
            currentState = PlayerState.Ready,
            availableCommands = emptySet(),
            mediaItem = null,
            mediaItemPosition = mediaItemPosition,
            shuffleModeEnabled = false,
        )

        // then
        assertNotNull(result.trackPosition)
        val expectedTrackPosition = result.trackPosition!!
        assertThat(expectedTrackPosition.current).isEqualTo(current.inWholeMilliseconds)
        assertThat(expectedTrackPosition.duration).isEqualTo(duration.inWholeMilliseconds)
        assertThat(expectedTrackPosition.percent).isEqualTo(0.5f)
    }
}
