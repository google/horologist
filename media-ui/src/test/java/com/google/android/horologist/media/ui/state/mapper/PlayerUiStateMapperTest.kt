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
import com.google.android.horologist.media.model.Media
import com.google.android.horologist.media.model.PlaybackState
import com.google.android.horologist.media.model.PlaybackStateEvent
import com.google.android.horologist.media.model.PlayerState
import com.google.android.horologist.media.ui.ExperimentalHorologistMediaUiApi
import com.google.android.horologist.media.ui.components.controls.SeekButtonIncrement
import com.google.android.horologist.media.ui.state.model.TrackPositionUiModel
import com.google.common.truth.Truth.assertThat
import org.junit.Assert.assertNotNull
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config
import kotlin.time.Duration.Companion.seconds
import kotlin.time.DurationUnit
import kotlin.time.toDuration

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
            media = null,
            playbackStateEvent = PlaybackStateEvent.INITIAL,
            shuffleModeEnabled = false,
            connected = false,
            seekBackIncrement = null,
            seekForwardIncrement = null
        )

        // then
        assertThat(result.playEnabled).isFalse()
        assertThat(result.pauseEnabled).isFalse()
        assertThat(result.seekBackEnabled).isFalse()
        assertThat(result.seekForwardEnabled).isFalse()
        assertThat(result.seekToPreviousEnabled).isFalse()
        assertThat(result.seekToNextEnabled).isFalse()
        assertThat(result.shuffleEnabled).isFalse()
        assertThat(result.playPauseEnabled).isFalse()
    }

    @Test
    fun givenPlayPauseCommandIsAvailable_thenPlayIsEnabled() {
        // given
        val commands = setOf(Command.PlayPause)

        // when
        val result = PlayerUiStateMapper.map(
            currentState = PlayerState.Ready,
            availableCommands = commands,
            media = null,
            playbackStateEvent = PlaybackStateEvent.INITIAL,
            shuffleModeEnabled = false,
            connected = true,
            seekBackIncrement = null,
            seekForwardIncrement = null
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
            media = null,
            playbackStateEvent = PlaybackStateEvent.INITIAL,
            shuffleModeEnabled = false,
            connected = true,
            seekBackIncrement = null,
            seekForwardIncrement = null
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
            media = null,
            playbackStateEvent = PlaybackStateEvent.INITIAL,
            shuffleModeEnabled = false,
            connected = true,
            seekBackIncrement = null,
            seekForwardIncrement = null
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
            media = null,
            playbackStateEvent = PlaybackStateEvent.INITIAL,
            shuffleModeEnabled = false,
            connected = true,
            seekBackIncrement = null,
            seekForwardIncrement = null
        )

        // then
        assertThat(result.seekForwardEnabled).isTrue()
    }

    @Test
    fun givenSkipToPreviousMediaCommandIsAvailable_thenSeekToPreviousIsEnabled() {
        // given
        val commands = setOf(Command.SkipToPreviousMedia)

        // when
        val result = PlayerUiStateMapper.map(
            currentState = PlayerState.Ready,
            availableCommands = commands,
            media = null,
            playbackStateEvent = PlaybackStateEvent.INITIAL,
            shuffleModeEnabled = false,
            connected = true,
            seekBackIncrement = null,
            seekForwardIncrement = null
        )

        // then
        assertThat(result.seekToPreviousEnabled).isTrue()
    }

    @Test
    fun givenSkipToNextMediaCommandIsAvailable_thenSeekToNextIsEnabled() {
        // given
        val commands = setOf(Command.SkipToNextMedia)

        // when
        val result = PlayerUiStateMapper.map(
            currentState = PlayerState.Ready,
            availableCommands = commands,
            media = null,
            playbackStateEvent = PlaybackStateEvent.INITIAL,
            shuffleModeEnabled = false,
            connected = true,
            seekBackIncrement = null,
            seekForwardIncrement = null
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
            media = null,
            playbackStateEvent = PlaybackStateEvent.INITIAL,
            shuffleModeEnabled = false,
            connected = true,
            seekBackIncrement = null,
            seekForwardIncrement = null
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
            media = null,
            playbackStateEvent = PlaybackStateEvent.INITIAL,
            shuffleModeEnabled = shuffleEnabled,
            connected = true,
            seekBackIncrement = null,
            seekForwardIncrement = null
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
            media = null,
            playbackStateEvent = PlaybackStateEvent.INITIAL,
            shuffleModeEnabled = shuffleEnabled,
            connected = true,
            seekBackIncrement = null,
            seekForwardIncrement = null
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
            media = null,
            playbackStateEvent = PlaybackStateEvent.INITIAL,
            shuffleModeEnabled = false,
            connected = true,
            seekBackIncrement = null,
            seekForwardIncrement = null
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
            media = null,
            playbackStateEvent = PlaybackStateEvent.INITIAL,
            shuffleModeEnabled = false,
            connected = true,
            seekBackIncrement = null,
            seekForwardIncrement = null
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
            media = null,
            playbackStateEvent = PlaybackStateEvent.INITIAL,
            shuffleModeEnabled = false,
            connected = true,
            seekBackIncrement = null,
            seekForwardIncrement = null
        )

        // then
        assertThat(result.playing).isTrue()
    }

    @Test
    fun givenMedia_thenMediaItemIsMappedCorrectly() {
        // given
        val id = "id"
        val title = "title"
        val artist = "artist"
        val media = Media(
            id = id,
            uri = "http://www.example.com",
            title = title,
            artist = artist
        )

        // when
        val result = PlayerUiStateMapper.map(
            currentState = PlayerState.Ready,
            availableCommands = emptySet(),
            media = media,
            playbackStateEvent = PlaybackStateEvent.INITIAL,
            shuffleModeEnabled = false,
            connected = true,
            seekBackIncrement = null,
            seekForwardIncrement = null
        )

        // then
        assertNotNull(result.media)
        val expectedMediaItem = result.media!!
        assertThat(expectedMediaItem.id).isEqualTo(id)
        assertThat(expectedMediaItem.title).isEqualTo(title)
        assertThat(expectedMediaItem.subtitle).isEqualTo(artist)
    }

    @Test
    fun givenMediaPosition_thenTrackPositionIsMappedCorrectly() {
        // given
        val current = 1.seconds
        val duration = 2.seconds
        val playbackState = PlaybackState(
            playerState = PlayerState.Playing,
            isLive = false,
            currentPosition = current,
            duration = duration,
            playbackSpeed = 1f
        )

        // when
        val result = PlayerUiStateMapper.map(
            currentState = PlayerState.Ready,
            availableCommands = emptySet(),
            media = null,
            playbackStateEvent = PlaybackStateEvent(
                playbackState,
                PlaybackStateEvent.Cause.PositionDiscontinuity,
                0.toDuration(DurationUnit.SECONDS)
            ),
            shuffleModeEnabled = false,
            connected = true,
            seekBackIncrement = null,
            seekForwardIncrement = null
        )

        // then
        assertNotNull(result.trackPositionUiModel)
        val expectedTrackPosition = result.trackPositionUiModel
        assertThat(expectedTrackPosition).isInstanceOf(TrackPositionUiModel.Predictive::class.java)
        expectedTrackPosition as TrackPositionUiModel.Predictive
        assertThat(expectedTrackPosition.predictor.predictPercent(0)).isEqualTo(0.5f)
        assertThat(expectedTrackPosition.predictor.predictPercent(duration.inWholeMilliseconds)).isEqualTo(1f)
    }

    @Test
    fun givenIncrements_thenIncrementsAreMappedCorrectly() {
        // given
        val forward = 12.seconds
        val back = 23.seconds

        // when
        val result = PlayerUiStateMapper.map(
            currentState = PlayerState.Ready,
            availableCommands = emptySet(),
            media = null,
            playbackStateEvent = PlaybackStateEvent.INITIAL,
            shuffleModeEnabled = false,
            connected = true,
            seekBackIncrement = back,
            seekForwardIncrement = forward
        )

        // then
        assertThat(result.seekBackButtonIncrement).isEqualTo(SeekButtonIncrement.Known(23))
        assertThat(result.seekForwardButtonIncrement).isEqualTo(SeekButtonIncrement.Known(12))
    }

    @Test
    fun givenNullIncrements_thenIncrementsAreUnknown() {
        // when
        val result = PlayerUiStateMapper.map(
            currentState = PlayerState.Ready,
            availableCommands = emptySet(),
            media = null,
            playbackStateEvent = PlaybackStateEvent.INITIAL,
            shuffleModeEnabled = false,
            connected = true,
            seekBackIncrement = null,
            seekForwardIncrement = null
        )

        // then
        assertThat(result.seekBackButtonIncrement).isEqualTo(SeekButtonIncrement.Unknown)
        assertThat(result.seekForwardButtonIncrement).isEqualTo(SeekButtonIncrement.Unknown)
    }
}
