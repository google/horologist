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

@file:OptIn(ExperimentalHorologistMediaDataApi::class)

package com.google.android.horologist.media.data.repository

import android.content.Context
import android.os.Looper.getMainLooper
import androidx.media3.common.Player
import androidx.media3.test.utils.TestExoPlayerBuilder
import androidx.media3.test.utils.robolectric.TestPlayerRunHelper.playUntilPosition
import androidx.media3.test.utils.robolectric.TestPlayerRunHelper.runUntilPendingCommandsAreFullyHandled
import androidx.media3.test.utils.robolectric.TestPlayerRunHelper.runUntilPlaybackState
import androidx.test.core.app.ApplicationProvider
import com.google.android.horologist.media.data.ExperimentalHorologistMediaDataApi
import com.google.android.horologist.media.data.mapper.MediaItemExtrasMapperNoopImpl
import com.google.android.horologist.media.data.mapper.MediaItemMapper
import com.google.android.horologist.media.model.Command
import com.google.android.horologist.media.model.Media
import com.google.android.horologist.media.model.MediaPosition
import com.google.android.horologist.media.model.PlayerState
import com.google.common.truth.Truth.assertThat
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Ignore
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows.shadowOf
import org.robolectric.annotation.Config
import org.robolectric.shadows.ShadowLooper
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE)
class PlayerRepositoryImplTest {

    private lateinit var sut: PlayerRepositoryImpl

    private lateinit var context: Context

    private lateinit var mediaItemMapper: MediaItemMapper

    @Before
    fun setUp() {
        // execute all tasks posted to main looper
        shadowOf(getMainLooper()).idle()

        mediaItemMapper = MediaItemMapper(MediaItemExtrasMapperNoopImpl)

        context = ApplicationProvider.getApplicationContext()
        sut = PlayerRepositoryImpl()
    }

    @Test
    fun `when connect then state is correct`() {
        // given
        val player = TestExoPlayerBuilder(context).build()

        // when
        sut.connect(player) {}

        // then
        assertThat(sut.currentState.value).isEqualTo(PlayerState.Idle)
        assertThat(sut.currentMedia.value).isNull()
        assertThat(sut.playbackSpeed.value).isEqualTo(1f)
        assertThat(sut.shuffleModeEnabled.value).isFalse()
        assertThat(sut.player.value).isSameInstanceAs(player)
        assertThat(sut.mediaPosition.value).isNull()
        assertThat(sut.availableCommands.value).containsExactlyElementsIn(
            listOf(Command.PlayPause, Command.SetShuffle)
        )
    }

    @Test
    fun `given is connected when connect then exception is thrown`() {
        // given
        val player = TestExoPlayerBuilder(context).build()
        sut.connect(player) {}

        // when
        val whenBlock = { sut.connect(player) {} }

        // then
        assertThrows("previously connected", IllegalStateException::class.java) { whenBlock() }
    }

    @Test
    fun `given connected when close then player is NOT cleared`() {
        // given
        val player = TestExoPlayerBuilder(context).build()
        sut.connect(player) {}

        // when
        sut.close()

        // then
        assertThat(sut.player.value).isSameInstanceAs(player)
    }

    @Test
    fun `given close callback when close then callback is called`() {
        // given
        val player = TestExoPlayerBuilder(context).build()
        var called = false
        val onCloseSpy = { called = true }
        sut.connect(player, onCloseSpy)

        // when
        sut.close()

        // then
        assertTrue(called)
    }

    @Test
    fun `given is connected and prepared when play until position then state is correct`() {
        // given
        val player = TestExoPlayerBuilder(context).build()
        sut.connect(player) {}

        val media = getStubMedia("id")

        sut.setMedia(media)
        sut.prepare()

        // when
        sut.play()
        // and
        playUntilPosition(player, 0, 5.seconds.inWholeMilliseconds)

        // then
        assertThat(sut.currentState.value).isEqualTo(PlayerState.Playing)
        assertThat(sut.currentMedia.value).isEqualTo(media)
        assertThat(sut.playbackSpeed.value).isEqualTo(1f)
        assertThat(sut.shuffleModeEnabled.value).isFalse()
        assertThat(sut.player.value).isSameInstanceAs(player)
        // position is unknown because updatePosition function was not called
        assertThat(sut.mediaPosition.value).isEqualTo(MediaPosition.UnknownDuration(0.seconds))
        assertThat(sut.availableCommands.value).containsExactlyElementsIn(
            listOf(Command.PlayPause, Command.SeekBack, Command.SeekForward, Command.SetShuffle)
        )
    }

    @Test
    fun `given is NOT connected and played until position when connect then state is correct`() {
        // given
        val player = TestExoPlayerBuilder(context).build()

        val media = getStubMedia("id")

        player.setMediaItem(mediaItemMapper.map(media))
        player.prepare()

        // when
        sut.connect(player) {}
        // and
        player.play()
        // and
        playUntilPosition(player, 0, 5.seconds.inWholeMilliseconds)

        // then
        assertThat(sut.currentState.value).isEqualTo(PlayerState.Playing)
        assertThat(sut.currentMedia.value).isEqualTo(media)
        assertThat(sut.playbackSpeed.value).isEqualTo(1f)
        assertThat(sut.shuffleModeEnabled.value).isFalse()
        assertThat(sut.player.value).isSameInstanceAs(player)
        assertThat(sut.availableCommands.value).containsExactlyElementsIn(
            listOf(Command.PlayPause, Command.SeekBack, Command.SeekForward, Command.SetShuffle)
        )
    }

    @Test
    fun `given is connected and prepared when play until end then state is correct`() {
        // given
        val player = TestExoPlayerBuilder(context).build()
        sut.connect(player) {}

        val media = getStubMedia("id")

        sut.setMedia(media)
        sut.prepare()

        // when
        sut.play()
        // and
        runUntilPlaybackState(player, Player.STATE_ENDED)
        runUntilPendingCommandsAreFullyHandled(player)

        // then
        assertThat(sut.currentState.value).isEqualTo(PlayerState.Ended)
        assertThat(sut.currentMedia.value).isEqualTo(media)
        assertThat(sut.playbackSpeed.value).isEqualTo(1f)
        assertThat(sut.shuffleModeEnabled.value).isFalse()
        assertThat(sut.player.value).isSameInstanceAs(player)
        assertThat(sut.mediaPosition.value).isEqualTo(MediaPosition.UnknownDuration(0.seconds))
        assertThat(sut.availableCommands.value).containsExactlyElementsIn(
            listOf(Command.PlayPause, Command.SeekBack, Command.SeekForward, Command.SetShuffle)
        )
    }

    @Test
    fun `given is connected and prepared when play until end then state progression is correct`() {
        // given initial state

        // then
        assertThat(sut.currentState.value).isEqualTo(PlayerState.Idle)

        // given
        val player = TestExoPlayerBuilder(context).build()

        // when
        sut.connect(player) {}

        // then
        assertThat(sut.currentState.value).isEqualTo(PlayerState.Idle)

        // given
        val media = getStubMedia("id")

        // when
        sut.setMedia(media)
        runUntilPendingCommandsAreFullyHandled(player)

        // then
        assertThat(sut.currentState.value).isEqualTo(PlayerState.Idle)

        // when
        sut.prepare()
        runUntilPendingCommandsAreFullyHandled(player)

        // then
        assertThat(sut.currentState.value).isEqualTo(PlayerState.Loading)

        // when
        runUntilPlaybackState(player, Player.STATE_READY)
        runUntilPendingCommandsAreFullyHandled(player)

        // then
        assertThat(sut.currentState.value).isEqualTo(PlayerState.Ready)

        // when
        sut.play()
        runUntilPendingCommandsAreFullyHandled(player)

        // then
        assertThat(sut.currentState.value).isEqualTo(PlayerState.Playing)

        // when
        sut.pause()
        runUntilPendingCommandsAreFullyHandled(player)

        // then
        assertThat(sut.currentState.value).isEqualTo(PlayerState.Ready)

        // when
        sut.play()
        runUntilPendingCommandsAreFullyHandled(player)

        // then
        assertThat(sut.currentState.value).isEqualTo(PlayerState.Playing)

        // when
        runUntilPlaybackState(player, Player.STATE_ENDED)
        runUntilPendingCommandsAreFullyHandled(player)

        // then
        assertThat(sut.currentState.value).isEqualTo(PlayerState.Ended)
    }

    @Test
    fun `given is connected and prepared when play with index until position then state is correct`() {
        // given
        val player = TestExoPlayerBuilder(context).build()
        sut.connect(player) {}

        val media1 = getStubMedia("id1")
        val media2 = getStubMedia("id2")

        sut.setMediaList(listOf(media1, media2))
        sut.seekToDefaultPosition(mediaIndex = 1)
        sut.prepare()

        // when
        sut.play()

        // then
        runUntilPendingCommandsAreFullyHandled(player)
        assertThat(sut.currentMedia.value).isEqualTo(media2)

        // and when
        playUntilPosition(player, 1, 5.seconds.inWholeMilliseconds)

        // then
        assertThat(sut.getMediaCount()).isEqualTo(2)
        assertThat(sut.getMediaAt(1)).isEqualTo(media2)
        assertThat(sut.getCurrentMediaIndex()).isEqualTo(1)
        assertThat(sut.currentState.value).isEqualTo(PlayerState.Playing)
        assertThat(sut.currentMedia.value).isEqualTo(media2)
        assertThat(sut.playbackSpeed.value).isEqualTo(1f)
        assertThat(sut.shuffleModeEnabled.value).isFalse()
        assertThat(sut.player.value).isSameInstanceAs(player)
        // position is unknown because updatePosition function was not called
        assertThat(sut.mediaPosition.value).isEqualTo(MediaPosition.UnknownDuration(0.seconds))
        assertThat(sut.availableCommands.value).containsExactlyElementsIn(
            listOf(
                Command.PlayPause,
                Command.SkipToPreviousMedia,
                Command.SeekBack,
                Command.SeekForward,
                Command.SetShuffle
            )
        )
    }

    @Test
    fun `given is playing when pause then state is correct`() {
        // given
        val player = TestExoPlayerBuilder(context).build()
        sut.connect(player) {}

        val media = getStubMedia("id")
        sut.setMedia(media)
        sut.prepare()
        sut.play()
        playUntilPosition(player, 0, 510.milliseconds.inWholeMilliseconds)
        runUntilPendingCommandsAreFullyHandled(player)

        // when
        sut.pause()
        runUntilPendingCommandsAreFullyHandled(player)

        // then
        assertThat(sut.currentState.value).isEqualTo(PlayerState.Ready)
        assertThat(sut.currentMedia.value).isEqualTo(media)
        assertThat(sut.playbackSpeed.value).isEqualTo(1f)
        assertThat(sut.shuffleModeEnabled.value).isFalse()
        assertThat(sut.player.value).isSameInstanceAs(player)
        assertThat(sut.mediaPosition.value!!.current).isEqualTo(510.milliseconds)
        assertThat(sut.availableCommands.value).containsExactlyElementsIn(
            listOf(Command.PlayPause, Command.SeekBack, Command.SeekForward, Command.SetShuffle)
        )
    }

    @Test
    fun `given seek increment when getSeekBackIncrement then correct value is returned`() {
        // given
        val seekIncrement = 1234L
        val player = TestExoPlayerBuilder(context).setSeekBackIncrementMs(seekIncrement).build()
        sut.connect(player) {}

        // when
        val result = sut.seekBackIncrement.value

        // then
        assertThat(result).isEqualTo(seekIncrement.milliseconds)
    }

    @Test
    fun `given seek increment when getSeekForwardIncrement then correct value is returned`() {
        // given
        val seekIncrement = 1234L
        val player = TestExoPlayerBuilder(context).setSeekForwardIncrementMs(seekIncrement).build()
        sut.connect(player) {}

        // when
        val result = sut.seekForwardIncrement.value

        // then
        assertThat(result).isEqualTo(seekIncrement.milliseconds)
    }

    @Test
    fun `given connected when setShuffleModeEnabled then state is correct`() {
        // given
        val player = TestExoPlayerBuilder(context).build()
        sut.connect(player) {}

        // when
        sut.setShuffleModeEnabled(true)

        // Allow events to propogate
        ShadowLooper.idleMainLooper()

        // then
        assertThat(sut.currentState.value).isEqualTo(PlayerState.Idle)
        assertThat(sut.currentMedia.value).isNull()
        assertThat(sut.playbackSpeed.value).isEqualTo(1f)
        assertThat(sut.shuffleModeEnabled.value).isTrue()
        assertThat(sut.player.value).isSameInstanceAs(player)
        assertThat(sut.mediaPosition.value).isNull()
        assertThat(sut.availableCommands.value).containsExactlyElementsIn(
            listOf(Command.PlayPause, Command.SetShuffle)
        )
    }

    @Test
    fun `given NO previous Media is set when setMedia then state is correct`() {
        // given
        val player = TestExoPlayerBuilder(context).build()
        sut.connect(player) {}

        val media = getDummyMedia()

        // when
        sut.setMedia(media)
        runUntilPendingCommandsAreFullyHandled(player)

        // then
        assertThat(sut.getMediaCount()).isEqualTo(1)
        assertThat(sut.getMediaAt(0)).isEqualTo(media)
        assertThat(sut.currentState.value).isEqualTo(PlayerState.Idle)
        assertThat(sut.currentMedia.value).isEqualTo(media)
        assertThat(sut.playbackSpeed.value).isEqualTo(1f)
        assertThat(sut.shuffleModeEnabled.value).isFalse()
        assertThat(sut.player.value).isSameInstanceAs(player)
        assertThat(sut.mediaPosition.value).isEqualTo(MediaPosition.UnknownDuration(0.seconds))
        assertThat(sut.availableCommands.value).containsExactlyElementsIn(
            listOf(Command.PlayPause, Command.SetShuffle)
        )
    }

    @Test
    fun `given previous Media is set when setMedia then state is correct`() {
        // given
        val player = TestExoPlayerBuilder(context).build()
        sut.connect(player) {}
        sut.setMedia(getStubMedia("id1"))

        val media = getStubMedia("id2")

        // when
        sut.setMedia(media)
        runUntilPendingCommandsAreFullyHandled(player)

        // then
        assertThat(sut.getMediaCount()).isEqualTo(1)
        assertThat(sut.getMediaAt(0)).isEqualTo(media)
        assertThat(sut.currentState.value).isEqualTo(PlayerState.Idle)
        assertThat(sut.currentMedia.value).isEqualTo(media)
        assertThat(sut.playbackSpeed.value).isEqualTo(1f)
        assertThat(sut.shuffleModeEnabled.value).isFalse()
        assertThat(sut.player.value).isSameInstanceAs(player)
        assertThat(sut.mediaPosition.value).isEqualTo(MediaPosition.UnknownDuration(0.seconds))
        assertThat(sut.availableCommands.value).containsExactlyElementsIn(
            listOf(Command.PlayPause, Command.SetShuffle)
        )
    }

    @Test
    fun `given is playing when setMedia then state is correct`() {
        // given
        val player = TestExoPlayerBuilder(context).build()
        sut.connect(player) {}
        val media1 = getStubMedia("id1")
        sut.setMedia(media1)
        sut.prepare()
        sut.play()
        playUntilPosition(player, 0, 510.milliseconds.inWholeMilliseconds)
        sut.updatePosition()

        // then
        assertThat(sut.getMediaAt(0)).isEqualTo(media1)
        assertThat(sut.currentState.value).isEqualTo(PlayerState.Playing)
        assertThat(sut.mediaPosition.value!!.current).isEqualTo(500.milliseconds)

        // given
        val media2 = getDummyMedia()

        // when
        sut.setMedia(media2)
        runUntilPendingCommandsAreFullyHandled(player)

        // then
        assertThat(sut.getMediaCount()).isEqualTo(1)
        assertThat(sut.getMediaAt(0)).isEqualTo(media2)
        assertThat(sut.currentState.value).isEqualTo(PlayerState.Loading)
        assertThat(sut.currentMedia.value).isEqualTo(media2)
        assertThat(sut.playbackSpeed.value).isEqualTo(1f)
        assertThat(sut.shuffleModeEnabled.value).isFalse()
        assertThat(sut.player.value).isSameInstanceAs(player)
        assertThat(sut.mediaPosition.value).isEqualTo(MediaPosition.UnknownDuration(0.seconds))
        assertThat(sut.availableCommands.value).containsExactlyElementsIn(
            listOf(Command.PlayPause, Command.SetShuffle)
        )
    }

    @Test
    fun `given NO previous MediaList is set when setMediaList then state is correct`() {
        // given
        val player = TestExoPlayerBuilder(context).build()
        sut.connect(player) {}

        val media1 = getStubMedia("id1")
        val media2 = getStubMedia("id2")
        val mediaList = listOf(media1, media2)

        // when
        sut.setMediaList(mediaList)
        runUntilPendingCommandsAreFullyHandled(player)

        // then
        assertThat(sut.getMediaCount()).isEqualTo(2)
        assertThat(sut.getMediaAt(0)).isEqualTo(media1)
        assertThat(sut.getMediaAt(1)).isEqualTo(media2)
        assertThat(sut.currentState.value).isEqualTo(PlayerState.Idle)
        assertThat(sut.currentMedia.value).isEqualTo(media1)
        assertThat(sut.playbackSpeed.value).isEqualTo(1f)
        assertThat(sut.shuffleModeEnabled.value).isFalse()
        assertThat(sut.player.value).isSameInstanceAs(player)
        assertThat(sut.mediaPosition.value).isEqualTo(MediaPosition.UnknownDuration(0.seconds))
        assertThat(sut.availableCommands.value).containsExactlyElementsIn(
            listOf(Command.PlayPause, Command.SkipToNextMedia, Command.SetShuffle)
        )
    }

    @Test
    fun `given previous MediaList is set when setMediaList then state is correct`() {
        // given
        val player = TestExoPlayerBuilder(context).build()
        sut.connect(player) {}
        sut.setMediaList(listOf(getStubMedia("id1"), getStubMedia("id2")))

        val media1 = getStubMedia("id3")
        val media2 = getStubMedia("id4")
        val mediaList = listOf(media1, media2)

        // when
        sut.setMediaList(mediaList)
        runUntilPendingCommandsAreFullyHandled(player)

        // then
        assertThat(sut.getMediaCount()).isEqualTo(2)
        assertThat(sut.getMediaAt(0)).isEqualTo(media1)
        assertThat(sut.getMediaAt(1)).isEqualTo(media2)
        assertThat(sut.currentState.value).isEqualTo(PlayerState.Idle)
        assertThat(sut.currentMedia.value).isEqualTo(media1)
        assertThat(sut.playbackSpeed.value).isEqualTo(1f)
        assertThat(sut.shuffleModeEnabled.value).isFalse()
        assertThat(sut.player.value).isSameInstanceAs(player)
        assertThat(sut.mediaPosition.value).isEqualTo(MediaPosition.UnknownDuration(0.seconds))
        assertThat(sut.availableCommands.value).containsExactlyElementsIn(
            listOf(Command.PlayPause, Command.SkipToNextMedia, Command.SetShuffle)
        )
    }

    @Ignore("Fix test once this issue is fixed: https://github.com/androidx/media/issues/85")
    @Test
    fun `given NO previous MediaList is set when setMediaListAndPlay then state is correct`() {
        // given
        val player = TestExoPlayerBuilder(context).build()
        sut.connect(player) {}

        val media1 = getStubMedia("id1")
        val media2 = getStubMedia("id2")
        val mediaList = listOf(media1, media2)

        // when
        sut.setMediaListAndPlay(mediaList, 1)
        runUntilPendingCommandsAreFullyHandled(player)

        // then
        assertThat(sut.getMediaCount()).isEqualTo(2)
        assertThat(sut.getMediaAt(0)).isEqualTo(media1)
        assertThat(sut.getMediaAt(1)).isEqualTo(media2)
        assertThat(sut.currentState.value).isEqualTo(PlayerState.Playing)
        assertThat(sut.currentMedia.value).isEqualTo(media2)
        assertThat(sut.playbackSpeed.value).isEqualTo(1f)
        assertThat(sut.shuffleModeEnabled.value).isFalse()
        assertThat(sut.player.value).isSameInstanceAs(player)
        assertThat(sut.mediaPosition.value).isEqualTo(MediaPosition.UnknownDuration(0.seconds))
        assertThat(sut.availableCommands.value).containsExactlyElementsIn(
            listOf(Command.PlayPause, Command.SkipToPreviousMedia, Command.SetShuffle)
        )
    }

    @Test
    fun `given NO previous Media is set when addMedia then state is correct`() {
        // given
        val player = TestExoPlayerBuilder(context).build()
        sut.connect(player) {}

        val media = getStubMedia("id")

        // when
        sut.addMedia(media)
        runUntilPendingCommandsAreFullyHandled(player)

        // then
        assertThat(sut.getMediaCount()).isEqualTo(1)
        assertThat(sut.getMediaAt(0)).isEqualTo(media)
        assertThat(sut.currentState.value).isEqualTo(PlayerState.Idle)
        assertThat(sut.currentMedia.value).isEqualTo(media)
        assertThat(sut.playbackSpeed.value).isEqualTo(1f)
        assertThat(sut.shuffleModeEnabled.value).isFalse()
        assertThat(sut.player.value).isSameInstanceAs(player)
        assertThat(sut.mediaPosition.value).isEqualTo(MediaPosition.UnknownDuration(0.seconds))
        assertThat(sut.availableCommands.value).containsExactlyElementsIn(
            listOf(Command.PlayPause, Command.SetShuffle)
        )
    }

    @Test
    fun `given previous Media is set when addMedia then state is correct`() {
        // given
        val player = TestExoPlayerBuilder(context).build()
        sut.connect(player) {}
        val previousMedia = getStubMedia("id1")
        sut.setMedia(previousMedia)

        val media = getStubMedia("id2")

        // when
        sut.addMedia(media)
        runUntilPendingCommandsAreFullyHandled(player)

        // then
        assertThat(sut.getMediaCount()).isEqualTo(2)
        assertThat(sut.getMediaAt(0)).isEqualTo(previousMedia)
        assertThat(sut.getMediaAt(1)).isEqualTo(media)
        assertThat(sut.currentState.value).isEqualTo(PlayerState.Idle)
        assertThat(sut.currentMedia.value).isEqualTo(previousMedia)
        assertThat(sut.playbackSpeed.value).isEqualTo(1f)
        assertThat(sut.shuffleModeEnabled.value).isFalse()
        assertThat(sut.player.value).isSameInstanceAs(player)
        assertThat(sut.mediaPosition.value).isEqualTo(MediaPosition.UnknownDuration(0.seconds))
        assertThat(sut.availableCommands.value).containsExactlyElementsIn(
            listOf(Command.PlayPause, Command.SkipToNextMedia, Command.SetShuffle)
        )
    }

    @Test
    fun `given NO previous Media is set when addMedia with valid index then state is correct`() {
        // given
        val player = TestExoPlayerBuilder(context).build()
        sut.connect(player) {}

        val media = getStubMedia("id")

        // when
        sut.addMedia(0, media)
        runUntilPendingCommandsAreFullyHandled(player)

        // then
        assertThat(sut.getMediaCount()).isEqualTo(1)
        assertThat(sut.getMediaAt(0)).isEqualTo(media)
        assertThat(sut.currentState.value).isEqualTo(PlayerState.Idle)
        assertThat(sut.currentMedia.value).isEqualTo(media)
        assertThat(sut.playbackSpeed.value).isEqualTo(1f)
        assertThat(sut.shuffleModeEnabled.value).isFalse()
        assertThat(sut.player.value).isSameInstanceAs(player)
        assertThat(sut.mediaPosition.value).isEqualTo(MediaPosition.UnknownDuration(0.seconds))
        assertThat(sut.availableCommands.value).containsExactlyElementsIn(
            listOf(Command.PlayPause, Command.SetShuffle)
        )
    }

    @Test
    fun `given previous Media is set when addMedia with valid index then state is correct`() {
        // given
        val player = TestExoPlayerBuilder(context).build()
        sut.connect(player) {}
        val previousMedia = getStubMedia("id1")
        sut.setMedia(previousMedia)

        val media = getStubMedia("id2")

        // when
        sut.addMedia(0, media)
        runUntilPendingCommandsAreFullyHandled(player)

        // then
        assertThat(sut.getMediaCount()).isEqualTo(2)
        assertThat(sut.getMediaAt(0)).isEqualTo(media)
        assertThat(sut.getMediaAt(1)).isEqualTo(previousMedia)
        assertThat(sut.currentState.value).isEqualTo(PlayerState.Idle)
        assertThat(sut.currentMedia.value).isEqualTo(previousMedia)
        assertThat(sut.playbackSpeed.value).isEqualTo(1f)
        assertThat(sut.shuffleModeEnabled.value).isFalse()
        assertThat(sut.player.value).isSameInstanceAs(player)
        assertThat(sut.mediaPosition.value).isEqualTo(MediaPosition.UnknownDuration(0.seconds))
        assertThat(sut.availableCommands.value).containsExactlyElementsIn(
            listOf(Command.PlayPause, Command.SkipToPreviousMedia, Command.SetShuffle)
        )
    }

    @Test
    fun `given previous Media is set when addMedia with index greater than size of playlist then state is correct`() {
        // given
        val player = TestExoPlayerBuilder(context).build()
        sut.connect(player) {}
        val previousMedia = getStubMedia("id1")
        sut.setMedia(previousMedia)

        val media = getStubMedia("id2")

        // when
        sut.addMedia(5, media)
        runUntilPendingCommandsAreFullyHandled(player)

        // then
        assertThat(sut.getMediaCount()).isEqualTo(2)
        assertThat(sut.getMediaAt(0)).isEqualTo(previousMedia)
        assertThat(sut.getMediaAt(1)).isEqualTo(media)
        assertThat(sut.currentState.value).isEqualTo(PlayerState.Idle)
        assertThat(sut.currentMedia.value).isEqualTo(previousMedia)
        assertThat(sut.playbackSpeed.value).isEqualTo(1f)
        assertThat(sut.shuffleModeEnabled.value).isFalse()
        assertThat(sut.player.value).isSameInstanceAs(player)
        assertThat(sut.mediaPosition.value).isEqualTo(MediaPosition.UnknownDuration(0.seconds))
        assertThat(sut.availableCommands.value).containsExactlyElementsIn(
            listOf(Command.PlayPause, Command.SkipToNextMedia, Command.SetShuffle)
        )
    }

    @Test
    fun `given connected when addMedia with invalid index then state is correct`() {
        // given
        val player = TestExoPlayerBuilder(context).build()
        sut.connect(player) {}
        val media = getStubMedia("id1")

        // when
        val whenBlock = { sut.addMedia(-1, media) }

        // then
        assertThrows(IllegalArgumentException::class.java) { whenBlock() }
    }

    @Test
    fun `given connected AND NO previous Media is set when removeMedia with zero index then state is correct`() {
        // given
        val player = TestExoPlayerBuilder(context).build()
        sut.connect(player) {}

        // when
        sut.removeMedia(0)

        // then
        // I'd expect it to throw IllegalArgumentException as per test below, maybe a bug in Media3
        assertThat(sut.getMediaCount()).isEqualTo(0)
        assertThat(sut.currentState.value).isEqualTo(PlayerState.Idle)
        assertThat(sut.currentMedia.value).isNull()
        assertThat(sut.playbackSpeed.value).isEqualTo(1f)
        assertThat(sut.shuffleModeEnabled.value).isFalse()
        assertThat(sut.player.value).isSameInstanceAs(player)
        assertThat(sut.mediaPosition.value).isNull()
        assertThat(sut.availableCommands.value).containsExactlyElementsIn(
            listOf(Command.PlayPause, Command.SetShuffle)
        )
    }

    @Test
    fun `given connected AND NO previous Media is set when removeMedia with index greater than zero then state is correct`() {
        // given
        val player = TestExoPlayerBuilder(context).build()
        sut.connect(player) {}

        // when
        val whenBlock = { sut.removeMedia(1) }

        // then
        assertThrows(IllegalArgumentException::class.java) { whenBlock() }
    }

    @Test
    fun `given previous Media is set when removeMedia with invalid index then state is correct`() {
        // given
        val player = TestExoPlayerBuilder(context).build()
        sut.connect(player) {}
        val previousMedia = getStubMedia("id1")
        sut.setMedia(previousMedia)

        // when
        val whenBlock = { sut.removeMedia(-1) }

        // then
        assertThrows(IllegalArgumentException::class.java) { whenBlock() }
    }

    @Test
    fun `given connected AND previous Media is set when removeMedia with current Media index then state is correct`() {
        // given
        val player = TestExoPlayerBuilder(context).build()
        sut.connect(player) {}

        val media1 = getStubMedia("id1")
        val media2 = getStubMedia("id2")
        val mediaList = listOf(media1, media2)
        sut.setMediaList(mediaList)

        // when
        sut.removeMedia(0)
        runUntilPendingCommandsAreFullyHandled(player)

        // then
        assertThat(sut.getMediaCount()).isEqualTo(1)
        assertThat(sut.getMediaAt(0)).isEqualTo(media2)
        assertThat(sut.currentState.value).isEqualTo(PlayerState.Idle)
        assertThat(sut.currentMedia.value).isEqualTo(media2)
        assertThat(sut.playbackSpeed.value).isEqualTo(1f)
        assertThat(sut.shuffleModeEnabled.value).isFalse()
        assertThat(sut.player.value).isSameInstanceAs(player)
        assertThat(sut.mediaPosition.value).isEqualTo(MediaPosition.UnknownDuration(0.seconds))
        assertThat(sut.availableCommands.value).containsExactlyElementsIn(
            listOf(Command.PlayPause, Command.SetShuffle)
        )
    }

    @Test
    fun `given connected AND previous Media is set when removeMedia with queued Media index then state is correct`() {
        // given
        val player = TestExoPlayerBuilder(context).build()
        sut.connect(player) {}

        val media1 = getStubMedia("id1")
        val media2 = getStubMedia("id2")
        val mediaList = listOf(media1, media2)
        sut.setMediaList(mediaList)

        // when
        sut.removeMedia(1)
        runUntilPendingCommandsAreFullyHandled(player)

        // then
        assertThat(sut.getMediaCount()).isEqualTo(1)
        assertThat(sut.getMediaAt(0)).isEqualTo(media1)
        assertThat(sut.currentState.value).isEqualTo(PlayerState.Idle)
        assertThat(sut.currentMedia.value).isEqualTo(media1)
        assertThat(sut.playbackSpeed.value).isEqualTo(1f)
        assertThat(sut.shuffleModeEnabled.value).isFalse()
        assertThat(sut.player.value).isSameInstanceAs(player)
        assertThat(sut.mediaPosition.value).isEqualTo(MediaPosition.UnknownDuration(0.seconds))
        assertThat(sut.availableCommands.value).containsExactlyElementsIn(
            listOf(Command.PlayPause, Command.SetShuffle)
        )
    }

    @Test
    fun `given connected AND NO previous Media is set when clearMediaList then state is correct`() {
        // given
        val player = TestExoPlayerBuilder(context).build()
        sut.connect(player) {}

        // when
        sut.clearMediaList()

        // then
        assertThat(sut.getMediaCount()).isEqualTo(0)
        assertThat(sut.currentState.value).isEqualTo(PlayerState.Idle)
        assertThat(sut.currentMedia.value).isNull()
        assertThat(sut.playbackSpeed.value).isEqualTo(1f)
        assertThat(sut.shuffleModeEnabled.value).isFalse()
        assertThat(sut.player.value).isSameInstanceAs(player)
        assertThat(sut.mediaPosition.value).isNull()
        assertThat(sut.availableCommands.value).containsExactlyElementsIn(
            listOf(Command.PlayPause, Command.SetShuffle)
        )
    }

    @Test
    fun `given connected AND previous Media is set when clearMediaList then state is correct`() {
        // given
        val player = TestExoPlayerBuilder(context).build()
        sut.connect(player) {}

        sut.setMediaList(listOf(getStubMedia("id1"), getStubMedia("id2")))

        // when
        sut.clearMediaList()

        // then
        assertThat(sut.getMediaCount()).isEqualTo(0)
        assertThat(sut.currentState.value).isEqualTo(PlayerState.Idle)
        assertThat(sut.currentMedia.value).isNull()
        assertThat(sut.playbackSpeed.value).isEqualTo(1f)
        assertThat(sut.shuffleModeEnabled.value).isFalse()
        assertThat(sut.player.value).isSameInstanceAs(player)
        assertThat(sut.mediaPosition.value).isEqualTo(MediaPosition.UnknownDuration(0.seconds))
        assertThat(sut.availableCommands.value).containsExactlyElementsIn(
            listOf(Command.PlayPause, Command.SetShuffle)
        )
    }

    @Test
    fun `given is closed when updatePosition then state is correct`() {
        // given
        sut.close()

        // when
        sut.updatePosition()

        // then
        assertInitialState()
    }

    @Test
    fun `given play until position when updatePosition then state is correct`() {
        // given
        val player = TestExoPlayerBuilder(context).build()
        sut.connect(player) {}

        val media = getStubMedia("id")

        sut.setMedia(media)
        sut.prepare()
        sut.play()
        playUntilPosition(player, 0, 5.seconds.inWholeMilliseconds)

        // when
        sut.updatePosition()

        // then
        assertThat(sut.currentState.value).isEqualTo(PlayerState.Playing)
        assertThat(sut.currentMedia.value).isEqualTo(media)
        assertThat(sut.playbackSpeed.value).isEqualTo(1f)
        assertThat(sut.shuffleModeEnabled.value).isFalse()
        assertThat(sut.player.value).isSameInstanceAs(player)
        assertThat(sut.availableCommands.value).containsExactlyElementsIn(
            listOf(Command.PlayPause, Command.SeekBack, Command.SeekForward, Command.SetShuffle)
        )

        assertThat(sut.mediaPosition.value).isInstanceOf(MediaPosition.KnownDuration::class.java)
        val expectedMediaPosition =
            MediaPosition.create(current = 1020.milliseconds, duration = 1022.milliseconds)
        val actualMediaPosition = sut.mediaPosition.value as MediaPosition.KnownDuration
        // TODO these checks can be simplified to `assertThat().isEqualTo()` once horologist implements equals for MediaPosition.KnownDuration
        assertThat(actualMediaPosition.current).isEqualTo(expectedMediaPosition.current)
        assertThat(actualMediaPosition.duration).isEqualTo(expectedMediaPosition.duration)
        assertThat(actualMediaPosition.percent).isEqualTo(expectedMediaPosition.percent)
    }

    @Test
    fun `given is closed when setPlaybackSpeed then state is NOT changed`() {
        // given
        val player = TestExoPlayerBuilder(context).build()
        sut.connect(player) {}
        sut.close()
        val speed = 2f

        // when
        sut.setPlaybackSpeed(speed)

        // then
        assertThat(sut.currentState.value).isEqualTo(PlayerState.Idle)
        assertThat(sut.currentMedia.value).isNull()
        assertThat(sut.playbackSpeed.value).isEqualTo(1f)
        assertThat(sut.shuffleModeEnabled.value).isFalse()
        assertThat(sut.player.value).isSameInstanceAs(player)
        assertThat(sut.mediaPosition.value).isNull()
        assertThat(sut.availableCommands.value).containsExactlyElementsIn(
            listOf(Command.PlayPause, Command.SetShuffle)
        )
    }

    @Test
    fun `given connected when setPlaybackSpeed then state is correct`() {
        // given
        val speed = 2f

        val player = TestExoPlayerBuilder(context).build()
        sut.connect(player) {}

        // when
        sut.setPlaybackSpeed(speed)

        // Allow events to propogate
        ShadowLooper.idleMainLooper()

        // then
        assertThat(sut.currentState.value).isEqualTo(PlayerState.Idle)
        assertThat(sut.currentMedia.value).isNull()
        assertThat(sut.playbackSpeed.value).isEqualTo(speed)
        assertThat(sut.shuffleModeEnabled.value).isFalse()
        assertThat(sut.player.value).isSameInstanceAs(player)
        assertThat(sut.mediaPosition.value).isNull()
        assertThat(sut.availableCommands.value).containsExactlyElementsIn(
            listOf(Command.PlayPause, Command.SetShuffle)
        )
    }

    private fun getDummyMedia() = Media(
        id = "id",
        uri = "uri",
        title = "title",
        artist = "artist",
        artworkUri = "artworkUri"
    )

    private fun getStubMedia(id: String) = Media(
        id = id,
        uri = "asset://android_asset/media/mp4/testvid_1022ms.mp4",
        title = "title",
        artist = "artist",
        artworkUri = "artworkUri"
    )

    private fun assertInitialState() {
        assertThat(sut.currentState.value).isEqualTo(PlayerState.Idle)
        assertThat(sut.currentMedia.value).isNull()
        assertThat(sut.playbackSpeed.value).isEqualTo(1f)
        assertThat(sut.shuffleModeEnabled.value).isFalse()
        assertThat(sut.player.value).isNull()
        assertThat(sut.mediaPosition.value).isNull()
        assertThat(sut.availableCommands.value).isEmpty()
    }
}
