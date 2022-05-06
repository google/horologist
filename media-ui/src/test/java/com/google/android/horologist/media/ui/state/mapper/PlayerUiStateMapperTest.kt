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

import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.android.horologist.media.data.model.TrackPosition
import com.google.android.horologist.media.ui.ExperimentalHorologistMediaUiApi
import com.google.android.horologist.media.ui.state.PlayerUiState
import com.google.common.truth.Truth.assertThat
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config

@RunWith(AndroidJUnit4::class)
@Config(manifest = Config.NONE)
class PlayerUiStateMapperTest {

    @Test
    fun givenNoCommandsAreAvailable_thenAllIsDisabled() {
        // given
        val commands = Player.Commands.EMPTY

        // when
        val result = PlayerUiStateMapper.map(
            commands,
            shuffleModeEnabled = false,
            isPlaying = false,
            mediaItem = null,
            trackPosition = null
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
        val commands = Player.Commands.Builder().add(Player.COMMAND_PLAY_PAUSE).build()

        // when
        val result = PlayerUiStateMapper.map(
            commands,
            shuffleModeEnabled = false,
            isPlaying = false,
            mediaItem = null,
            trackPosition = null
        )

        // then
        assertThat(result.playEnabled).isTrue()
    }

    @Test
    fun givenPlayPauseCommandIsAvailable_thenPauseIsEnabled() {
        // given
        val commands = Player.Commands.Builder().add(Player.COMMAND_PLAY_PAUSE).build()

        // when
        val result = PlayerUiStateMapper.map(
            commands,
            shuffleModeEnabled = false,
            isPlaying = false,
            mediaItem = null,
            trackPosition = null
        )

        // then
        assertThat(result.pauseEnabled).isTrue()
    }

    @Test
    fun givenSeekBackCommandIsAvailable_thenSeekBackIsEnabled() {
        // given
        val commands = Player.Commands.Builder().add(Player.COMMAND_SEEK_BACK).build()

        // when
        val result = PlayerUiStateMapper.map(
            commands,
            shuffleModeEnabled = false,
            isPlaying = false,
            mediaItem = null,
            trackPosition = null
        )

        // then
        assertThat(result.seekBackEnabled).isTrue()
    }

    @Test
    fun givenSeekForwardCommandIsAvailable_thenSeekForwardIsEnabled() {
        // given
        val commands = Player.Commands.Builder().add(Player.COMMAND_SEEK_FORWARD).build()

        // when
        val result = PlayerUiStateMapper.map(
            commands,
            shuffleModeEnabled = false,
            isPlaying = false,
            mediaItem = null,
            trackPosition = null
        )

        // then
        assertThat(result.seekForwardEnabled).isTrue()
    }

    @Test
    fun givenSeekToPreviousMediaItemCommandIsAvailable_thenSeekToPreviousIsEnabled() {
        // given
        val commands =
            Player.Commands.Builder().add(Player.COMMAND_SEEK_TO_PREVIOUS_MEDIA_ITEM).build()

        // when
        val result = PlayerUiStateMapper.map(
            commands,
            shuffleModeEnabled = false,
            isPlaying = false,
            mediaItem = null,
            trackPosition = null
        )

        // then
        assertThat(result.seekToPreviousEnabled).isTrue()
    }

    @Test
    fun givenSeekToNextMediaItemCommandIsAvailable_thenSeekToNextIsEnabled() {
        // given
        val commands =
            Player.Commands.Builder().add(Player.COMMAND_SEEK_TO_NEXT_MEDIA_ITEM).build()

        // when
        val result = PlayerUiStateMapper.map(
            commands,
            shuffleModeEnabled = false,
            isPlaying = false,
            mediaItem = null,
            trackPosition = null
        )

        // then
        assertThat(result.seekToNextEnabled).isTrue()
    }

    @Test
    fun givenSetShuffleModeCommandIsAvailable_thenShuffleIsEnabled() {
        // given
        val commands =
            Player.Commands.Builder().add(Player.COMMAND_SET_SHUFFLE_MODE).build()

        // when
        val result = PlayerUiStateMapper.map(
            commands,
            shuffleModeEnabled = false,
            isPlaying = false,
            mediaItem = null,
            trackPosition = null
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
            Player.Commands.EMPTY,
            shuffleModeEnabled = shuffleEnabled,
            isPlaying = false,
            mediaItem = null,
            trackPosition = null
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
            Player.Commands.EMPTY,
            shuffleModeEnabled = shuffleEnabled,
            isPlaying = false,
            mediaItem = null,
            trackPosition = null
        )

        // then
        assertThat(result.shuffleOn).isEqualTo(shuffleEnabled)
    }

    @Test
    fun givenPlayPauseCommandIsAvailable_thenPlayPauseIsEnabled() {
        // given
        val commands = Player.Commands.Builder().add(Player.COMMAND_PLAY_PAUSE).build()

        // when
        val result = PlayerUiStateMapper.map(
            commands,
            shuffleModeEnabled = false,
            isPlaying = false,
            mediaItem = null,
            trackPosition = null
        )

        // then
        assertThat(result.playPauseEnabled).isTrue()
    }

    @Test
    fun givenIsNOTPlaying_thenPlayingIsFalse() {
        // given
        val isPlaying = false

        // when
        val result = PlayerUiStateMapper.map(
            Player.Commands.EMPTY,
            shuffleModeEnabled = false,
            isPlaying = isPlaying,
            mediaItem = null,
            trackPosition = null
        )

        // then
        assertThat(result.playing).isEqualTo(isPlaying)
    }

    @Test
    fun givenIsPlaying_thenPlayingIsTrue() {
        // given
        val isPlaying = true

        // when
        val result = PlayerUiStateMapper.map(
            Player.Commands.EMPTY,
            shuffleModeEnabled = false,
            isPlaying = isPlaying,
            mediaItem = null,
            trackPosition = null
        )

        // then
        assertThat(result.playing).isEqualTo(isPlaying)
    }

    @Test
    fun givenMediaItem_thenMediaItemIsMappedCorrectly() {
        // given
        val title = "title"
        val artist = "artist"
        val metadataBuilder = MediaMetadata.Builder()
            .setDisplayTitle(title)
            .setArtist(artist)

        val mediaItem = MediaItem.Builder()
            .setMediaMetadata(metadataBuilder.build())
            .build()

        // when
        val result = PlayerUiStateMapper.map(
            Player.Commands.EMPTY,
            shuffleModeEnabled = false,
            isPlaying = false,
            mediaItem = mediaItem,
            trackPosition = null
        )

        // then
        Assert.assertNotNull(result.mediaItem)
        val expectedMediaItem = result.mediaItem!!
        assertThat(expectedMediaItem.title).isEqualTo(title)
        assertThat(expectedMediaItem.artist).isEqualTo(artist)
    }

    @Test
    fun givenTrackPosition_thenTrackPositionIsMappedCorrectly() {
        // given
        val current = 1L
        val duration = 2L
        val trackPosition = TrackPosition(current, duration)

        // when
        val result = PlayerUiStateMapper.map(
            Player.Commands.EMPTY,
            shuffleModeEnabled = false,
            isPlaying = false,
            mediaItem = null,
            trackPosition = trackPosition
        )

        // then
        Assert.assertNotNull(result.trackPosition)
        val expectedTrackPosition = result.trackPosition!!
        assertThat(expectedTrackPosition.current).isEqualTo(current)
        assertThat(expectedTrackPosition.percent).isEqualTo(0.5f)
    }
}
