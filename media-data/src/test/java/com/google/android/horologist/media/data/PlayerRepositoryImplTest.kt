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

package com.google.android.horologist.media.data

import android.content.Context
import android.os.Looper.getMainLooper
import androidx.media3.common.Player
import androidx.media3.test.utils.TestExoPlayerBuilder
import androidx.media3.test.utils.robolectric.TestPlayerRunHelper.playUntilPosition
import androidx.media3.test.utils.robolectric.TestPlayerRunHelper.runUntilPendingCommandsAreFullyHandled
import androidx.media3.test.utils.robolectric.TestPlayerRunHelper.runUntilPlaybackState
import androidx.test.core.app.ApplicationProvider
import com.google.android.horologist.media.model.Command
import com.google.android.horologist.media.model.MediaItem
import com.google.android.horologist.media.model.MediaItemPosition
import com.google.android.horologist.media.model.PlayerState
import com.google.common.truth.Truth.assertThat
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows.shadowOf
import org.robolectric.annotation.Config
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE)
class PlayerRepositoryImplTest {

    private lateinit var sut: PlayerRepositoryImpl

    private lateinit var context: Context

    @Before
    fun setUp() {
        // execute all tasks posted to main looper
        shadowOf(getMainLooper()).idle()

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
        assertThat(sut.currentMediaItem.value).isNull()
        assertThat(sut.playbackSpeed.value).isEqualTo(1f)
        assertThat(sut.shuffleModeEnabled.value).isFalse()
        assertThat(sut.player.value).isSameInstanceAs(player)
        assertThat(sut.mediaItemPosition.value).isNull()
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

        val mediaItem = getStubMediaItem("id")

        sut.setMediaItem(mediaItem)
        sut.prepare()

        // when
        sut.play()
        // and
        playUntilPosition(player, 0, 5.seconds.inWholeMilliseconds)

        // then
        assertThat(sut.currentState.value).isEqualTo(PlayerState.Playing)
        assertThat(sut.currentMediaItem.value).isEqualTo(mediaItem)
        assertThat(sut.playbackSpeed.value).isEqualTo(1f)
        assertThat(sut.shuffleModeEnabled.value).isFalse()
        assertThat(sut.player.value).isSameInstanceAs(player)
        // position is unknown because updatePosition function was not called
        assertThat(sut.mediaItemPosition.value).isEqualTo(MediaItemPosition.UnknownDuration(0.seconds))
        assertThat(sut.availableCommands.value).containsExactlyElementsIn(
            listOf(Command.PlayPause, Command.SeekBack, Command.SeekForward, Command.SetShuffle)
        )
    }

    @Test
    fun `given is connected after prepared when play until position then state is correct`() {
        // given
        val player = TestExoPlayerBuilder(context).build()

        val mediaItem = getStubMediaItem("id")

        player.setMediaItem(Media3MediaItemMapper.map(mediaItem))
        player.prepare()

        // when
        player.play()
        // and
        playUntilPosition(player, 0, 5.seconds.inWholeMilliseconds)

        sut.connect(player) {}

        // then
        assertThat(sut.currentState.value).isEqualTo(PlayerState.Playing)
        assertThat(sut.currentMediaItem.value).isEqualTo(mediaItem)
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

        val mediaItem = getStubMediaItem("id")

        sut.setMediaItem(mediaItem)
        sut.prepare()

        // when
        sut.play()
        // and
        runUntilPlaybackState(player, Player.STATE_ENDED)
        runUntilPendingCommandsAreFullyHandled(player)

        // then
        assertThat(sut.currentState.value).isEqualTo(PlayerState.Ended)
        assertThat(sut.currentMediaItem.value).isEqualTo(mediaItem)
        assertThat(sut.playbackSpeed.value).isEqualTo(1f)
        assertThat(sut.shuffleModeEnabled.value).isFalse()
        assertThat(sut.player.value).isSameInstanceAs(player)
        assertThat(sut.mediaItemPosition.value).isEqualTo(MediaItemPosition.UnknownDuration(0.seconds))
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
        val mediaItem = getStubMediaItem("id")

        // when
        sut.setMediaItem(mediaItem)
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

        val mediaItem1 = getStubMediaItem("id1")
        val mediaItem2 = getStubMediaItem("id2")

        sut.setMediaItems(listOf(mediaItem1, mediaItem2))
        sut.prepare()

        // when
        sut.play(mediaItemIndex = 1)

        // then
        runUntilPendingCommandsAreFullyHandled(player)
        assertThat(sut.currentMediaItem.value).isEqualTo(mediaItem2)

        // and when
        playUntilPosition(player, 1, 5.seconds.inWholeMilliseconds)

        // then
        assertThat(sut.getMediaItemCount()).isEqualTo(2)
        assertThat(sut.getMediaItemAt(1)).isEqualTo(mediaItem2)
        assertThat(sut.getCurrentMediaItemIndex()).isEqualTo(1)
        assertThat(sut.currentState.value).isEqualTo(PlayerState.Playing)
        assertThat(sut.currentMediaItem.value).isEqualTo(mediaItem2)
        assertThat(sut.playbackSpeed.value).isEqualTo(1f)
        assertThat(sut.shuffleModeEnabled.value).isFalse()
        assertThat(sut.player.value).isSameInstanceAs(player)
        // position is unknown because updatePosition function was not called
        assertThat(sut.mediaItemPosition.value).isEqualTo(MediaItemPosition.UnknownDuration(0.seconds))
        assertThat(sut.availableCommands.value).containsExactlyElementsIn(
            listOf(
                Command.PlayPause,
                Command.SkipToPreviousMediaItem,
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

        val mediaItem = getStubMediaItem("id")
        sut.setMediaItem(mediaItem)
        sut.prepare()
        sut.play()
        playUntilPosition(player, 0, 510.milliseconds.inWholeMilliseconds)
        runUntilPendingCommandsAreFullyHandled(player)

        // when
        sut.pause()
        runUntilPendingCommandsAreFullyHandled(player)

        // then
        assertThat(sut.currentState.value).isEqualTo(PlayerState.Ready)
        assertThat(sut.currentMediaItem.value).isEqualTo(mediaItem)
        assertThat(sut.playbackSpeed.value).isEqualTo(1f)
        assertThat(sut.shuffleModeEnabled.value).isFalse()
        assertThat(sut.player.value).isSameInstanceAs(player)
        assertThat(sut.mediaItemPosition.value!!.current).isEqualTo(510.milliseconds)
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
        val result = sut.getSeekBackIncrement()

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
        val result = sut.getSeekForwardIncrement()

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

        // then
        assertThat(sut.currentState.value).isEqualTo(PlayerState.Idle)
        assertThat(sut.currentMediaItem.value).isNull()
        assertThat(sut.playbackSpeed.value).isEqualTo(1f)
        assertThat(sut.shuffleModeEnabled.value).isTrue()
        assertThat(sut.player.value).isSameInstanceAs(player)
        assertThat(sut.mediaItemPosition.value).isNull()
        assertThat(sut.availableCommands.value).containsExactlyElementsIn(
            listOf(Command.PlayPause, Command.SetShuffle)
        )
    }

    @Test
    fun `given NO previous MediaItems is set when setMediaItem then state is correct`() {
        // given
        val player = TestExoPlayerBuilder(context).build()
        sut.connect(player) {}

        val mediaItem = getDummyMediaItem()

        // when
        sut.setMediaItem(mediaItem)
        runUntilPendingCommandsAreFullyHandled(player)

        // then
        assertThat(sut.getMediaItemCount()).isEqualTo(1)
        assertThat(sut.getMediaItemAt(0)).isEqualTo(mediaItem)
        assertThat(sut.currentState.value).isEqualTo(PlayerState.Idle)
        assertThat(sut.currentMediaItem.value).isEqualTo(mediaItem)
        assertThat(sut.playbackSpeed.value).isEqualTo(1f)
        assertThat(sut.shuffleModeEnabled.value).isFalse()
        assertThat(sut.player.value).isSameInstanceAs(player)
        assertThat(sut.mediaItemPosition.value).isEqualTo(MediaItemPosition.UnknownDuration(0.seconds))
        assertThat(sut.availableCommands.value).containsExactlyElementsIn(
            listOf(Command.PlayPause, Command.SetShuffle)
        )
    }

    @Test
    fun `given previous MediaItem is set when setMediaItem then state is correct`() {
        // given
        val player = TestExoPlayerBuilder(context).build()
        sut.connect(player) {}
        sut.setMediaItem(getStubMediaItem("id1"))

        val mediaItem = getStubMediaItem("id2")

        // when
        sut.setMediaItem(mediaItem)
        runUntilPendingCommandsAreFullyHandled(player)

        // then
        assertThat(sut.getMediaItemCount()).isEqualTo(1)
        assertThat(sut.getMediaItemAt(0)).isEqualTo(mediaItem)
        assertThat(sut.currentState.value).isEqualTo(PlayerState.Idle)
        assertThat(sut.currentMediaItem.value).isEqualTo(mediaItem)
        assertThat(sut.playbackSpeed.value).isEqualTo(1f)
        assertThat(sut.shuffleModeEnabled.value).isFalse()
        assertThat(sut.player.value).isSameInstanceAs(player)
        assertThat(sut.mediaItemPosition.value).isEqualTo(MediaItemPosition.UnknownDuration(0.seconds))
        assertThat(sut.availableCommands.value).containsExactlyElementsIn(
            listOf(Command.PlayPause, Command.SetShuffle)
        )
    }

    @Test
    fun `given is playing when setMediaItem then state is correct`() {
        // given
        val player = TestExoPlayerBuilder(context).build()
        sut.connect(player) {}
        val mediaItem1 = getStubMediaItem("id1")
        sut.setMediaItem(mediaItem1)
        sut.prepare()
        sut.play()
        playUntilPosition(player, 0, 510.milliseconds.inWholeMilliseconds)
        sut.updatePosition()

        // then
        assertThat(sut.getMediaItemAt(0)).isEqualTo(mediaItem1)
        assertThat(sut.currentState.value).isEqualTo(PlayerState.Playing)
        assertThat(sut.mediaItemPosition.value!!.current).isEqualTo(500.milliseconds)

        // given
        val mediaItem2 = getDummyMediaItem()

        // when
        sut.setMediaItem(mediaItem2)
        runUntilPendingCommandsAreFullyHandled(player)

        // then
        assertThat(sut.getMediaItemCount()).isEqualTo(1)
        assertThat(sut.getMediaItemAt(0)).isEqualTo(mediaItem2)
        assertThat(sut.currentState.value).isEqualTo(PlayerState.Loading)
        assertThat(sut.currentMediaItem.value).isEqualTo(mediaItem2)
        assertThat(sut.playbackSpeed.value).isEqualTo(1f)
        assertThat(sut.shuffleModeEnabled.value).isFalse()
        assertThat(sut.player.value).isSameInstanceAs(player)
        assertThat(sut.mediaItemPosition.value).isEqualTo(MediaItemPosition.UnknownDuration(0.seconds))
        assertThat(sut.availableCommands.value).containsExactlyElementsIn(
            listOf(Command.PlayPause, Command.SetShuffle)
        )
    }

    @Test
    fun `given NO previous MediaItems is set when setMediaItems then state is correct`() {
        // given
        val player = TestExoPlayerBuilder(context).build()
        sut.connect(player) {}

        val mediaItem1 = getStubMediaItem("id1")
        val mediaItem2 = getStubMediaItem("id2")
        val mediaItems = listOf(mediaItem1, mediaItem2)

        // when
        sut.setMediaItems(mediaItems)
        runUntilPendingCommandsAreFullyHandled(player)

        // then
        assertThat(sut.getMediaItemCount()).isEqualTo(2)
        assertThat(sut.getMediaItemAt(0)).isEqualTo(mediaItem1)
        assertThat(sut.getMediaItemAt(1)).isEqualTo(mediaItem2)
        assertThat(sut.currentState.value).isEqualTo(PlayerState.Idle)
        assertThat(sut.currentMediaItem.value).isEqualTo(mediaItem1)
        assertThat(sut.playbackSpeed.value).isEqualTo(1f)
        assertThat(sut.shuffleModeEnabled.value).isFalse()
        assertThat(sut.player.value).isSameInstanceAs(player)
        assertThat(sut.mediaItemPosition.value).isEqualTo(MediaItemPosition.UnknownDuration(0.seconds))
        assertThat(sut.availableCommands.value).containsExactlyElementsIn(
            listOf(Command.PlayPause, Command.SkipToNextMediaItem, Command.SetShuffle)
        )
    }

    @Test
    fun `given previous MediaItems is set when setMediaItems then state is correct`() {
        // given
        val player = TestExoPlayerBuilder(context).build()
        sut.connect(player) {}
        sut.setMediaItems(listOf(getStubMediaItem("id1"), getStubMediaItem("id2")))

        val mediaItem1 = getStubMediaItem("id3")
        val mediaItem2 = getStubMediaItem("id4")
        val mediaItems = listOf(mediaItem1, mediaItem2)

        // when
        sut.setMediaItems(mediaItems)
        runUntilPendingCommandsAreFullyHandled(player)

        // then
        assertThat(sut.getMediaItemCount()).isEqualTo(2)
        assertThat(sut.getMediaItemAt(0)).isEqualTo(mediaItem1)
        assertThat(sut.getMediaItemAt(1)).isEqualTo(mediaItem2)
        assertThat(sut.currentState.value).isEqualTo(PlayerState.Idle)
        assertThat(sut.currentMediaItem.value).isEqualTo(mediaItem1)
        assertThat(sut.playbackSpeed.value).isEqualTo(1f)
        assertThat(sut.shuffleModeEnabled.value).isFalse()
        assertThat(sut.player.value).isSameInstanceAs(player)
        assertThat(sut.mediaItemPosition.value).isEqualTo(MediaItemPosition.UnknownDuration(0.seconds))
        assertThat(sut.availableCommands.value).containsExactlyElementsIn(
            listOf(Command.PlayPause, Command.SkipToNextMediaItem, Command.SetShuffle)
        )
    }

    @Test
    fun `given NO previous MediaItems is set when addMediaItem then state is correct`() {
        // given
        val player = TestExoPlayerBuilder(context).build()
        sut.connect(player) {}

        val mediaItem = getStubMediaItem("id")

        // when
        sut.addMediaItem(mediaItem)
        runUntilPendingCommandsAreFullyHandled(player)

        // then
        assertThat(sut.getMediaItemCount()).isEqualTo(1)
        assertThat(sut.getMediaItemAt(0)).isEqualTo(mediaItem)
        assertThat(sut.currentState.value).isEqualTo(PlayerState.Idle)
        assertThat(sut.currentMediaItem.value).isEqualTo(mediaItem)
        assertThat(sut.playbackSpeed.value).isEqualTo(1f)
        assertThat(sut.shuffleModeEnabled.value).isFalse()
        assertThat(sut.player.value).isSameInstanceAs(player)
        assertThat(sut.mediaItemPosition.value).isEqualTo(MediaItemPosition.UnknownDuration(0.seconds))
        assertThat(sut.availableCommands.value).containsExactlyElementsIn(
            listOf(Command.PlayPause, Command.SetShuffle)
        )
    }

    @Test
    fun `given previous MediaItem is set when addMediaItem then state is correct`() {
        // given
        val player = TestExoPlayerBuilder(context).build()
        sut.connect(player) {}
        val previousMediaItem = getStubMediaItem("id1")
        sut.setMediaItem(previousMediaItem)

        val mediaItem = getStubMediaItem("id2")

        // when
        sut.addMediaItem(mediaItem)
        runUntilPendingCommandsAreFullyHandled(player)

        // then
        assertThat(sut.getMediaItemCount()).isEqualTo(2)
        assertThat(sut.getMediaItemAt(0)).isEqualTo(previousMediaItem)
        assertThat(sut.getMediaItemAt(1)).isEqualTo(mediaItem)
        assertThat(sut.currentState.value).isEqualTo(PlayerState.Idle)
        assertThat(sut.currentMediaItem.value).isEqualTo(previousMediaItem)
        assertThat(sut.playbackSpeed.value).isEqualTo(1f)
        assertThat(sut.shuffleModeEnabled.value).isFalse()
        assertThat(sut.player.value).isSameInstanceAs(player)
        assertThat(sut.mediaItemPosition.value).isEqualTo(MediaItemPosition.UnknownDuration(0.seconds))
        assertThat(sut.availableCommands.value).containsExactlyElementsIn(
            listOf(Command.PlayPause, Command.SkipToNextMediaItem, Command.SetShuffle)
        )
    }

    @Test
    fun `given NO previous MediaItems is set when addMediaItem with valid index then state is correct`() {
        // given
        val player = TestExoPlayerBuilder(context).build()
        sut.connect(player) {}

        val mediaItem = getStubMediaItem("id")

        // when
        sut.addMediaItem(0, mediaItem)
        runUntilPendingCommandsAreFullyHandled(player)

        // then
        assertThat(sut.getMediaItemCount()).isEqualTo(1)
        assertThat(sut.getMediaItemAt(0)).isEqualTo(mediaItem)
        assertThat(sut.currentState.value).isEqualTo(PlayerState.Idle)
        assertThat(sut.currentMediaItem.value).isEqualTo(mediaItem)
        assertThat(sut.playbackSpeed.value).isEqualTo(1f)
        assertThat(sut.shuffleModeEnabled.value).isFalse()
        assertThat(sut.player.value).isSameInstanceAs(player)
        assertThat(sut.mediaItemPosition.value).isEqualTo(MediaItemPosition.UnknownDuration(0.seconds))
        assertThat(sut.availableCommands.value).containsExactlyElementsIn(
            listOf(Command.PlayPause, Command.SetShuffle)
        )
    }

    @Test
    fun `given previous MediaItem is set when addMediaItem with valid index then state is correct`() {
        // given
        val player = TestExoPlayerBuilder(context).build()
        sut.connect(player) {}
        val previousMediaItem = getStubMediaItem("id1")
        sut.setMediaItem(previousMediaItem)

        val mediaItem = getStubMediaItem("id2")

        // when
        sut.addMediaItem(0, mediaItem)
        runUntilPendingCommandsAreFullyHandled(player)

        // then
        assertThat(sut.getMediaItemCount()).isEqualTo(2)
        assertThat(sut.getMediaItemAt(0)).isEqualTo(mediaItem)
        assertThat(sut.getMediaItemAt(1)).isEqualTo(previousMediaItem)
        assertThat(sut.currentState.value).isEqualTo(PlayerState.Idle)
        assertThat(sut.currentMediaItem.value).isEqualTo(previousMediaItem)
        assertThat(sut.playbackSpeed.value).isEqualTo(1f)
        assertThat(sut.shuffleModeEnabled.value).isFalse()
        assertThat(sut.player.value).isSameInstanceAs(player)
        assertThat(sut.mediaItemPosition.value).isEqualTo(MediaItemPosition.UnknownDuration(0.seconds))
        assertThat(sut.availableCommands.value).containsExactlyElementsIn(
            listOf(Command.PlayPause, Command.SkipToPreviousMediaItem, Command.SetShuffle)
        )
    }

    @Test
    fun `given previous MediaItem is set when addMediaItem with index greater than size of playlist then state is correct`() {
        // given
        val player = TestExoPlayerBuilder(context).build()
        sut.connect(player) {}
        val previousMediaItem = getStubMediaItem("id1")
        sut.setMediaItem(previousMediaItem)

        val mediaItem = getStubMediaItem("id2")

        // when
        sut.addMediaItem(5, mediaItem)
        runUntilPendingCommandsAreFullyHandled(player)

        // then
        assertThat(sut.getMediaItemCount()).isEqualTo(2)
        assertThat(sut.getMediaItemAt(0)).isEqualTo(previousMediaItem)
        assertThat(sut.getMediaItemAt(1)).isEqualTo(mediaItem)
        assertThat(sut.currentState.value).isEqualTo(PlayerState.Idle)
        assertThat(sut.currentMediaItem.value).isEqualTo(previousMediaItem)
        assertThat(sut.playbackSpeed.value).isEqualTo(1f)
        assertThat(sut.shuffleModeEnabled.value).isFalse()
        assertThat(sut.player.value).isSameInstanceAs(player)
        assertThat(sut.mediaItemPosition.value).isEqualTo(MediaItemPosition.UnknownDuration(0.seconds))
        assertThat(sut.availableCommands.value).containsExactlyElementsIn(
            listOf(Command.PlayPause, Command.SkipToNextMediaItem, Command.SetShuffle)
        )
    }

    @Test
    fun `given connected when addMediaItem with invalid index then state is correct`() {
        // given
        val player = TestExoPlayerBuilder(context).build()
        sut.connect(player) {}
        val mediaItem = getStubMediaItem("id1")

        // when
        val whenBlock = { sut.addMediaItem(-1, mediaItem) }

        // then
        assertThrows(IllegalArgumentException::class.java) { whenBlock() }
    }

    @Test
    fun `given connected AND NO previous MediaItem is set when removeMediaItem with zero index then state is correct`() {
        // given
        val player = TestExoPlayerBuilder(context).build()
        sut.connect(player) {}

        // when
        sut.removeMediaItem(0)

        // then
        // I'd expect it to throw IllegalArgumentException as per test below, maybe a bug in Media3
        assertThat(sut.getMediaItemCount()).isEqualTo(0)
        assertThat(sut.currentState.value).isEqualTo(PlayerState.Idle)
        assertThat(sut.currentMediaItem.value).isNull()
        assertThat(sut.playbackSpeed.value).isEqualTo(1f)
        assertThat(sut.shuffleModeEnabled.value).isFalse()
        assertThat(sut.player.value).isSameInstanceAs(player)
        assertThat(sut.mediaItemPosition.value).isNull()
        assertThat(sut.availableCommands.value).containsExactlyElementsIn(
            listOf(Command.PlayPause, Command.SetShuffle)
        )
    }

    @Test
    fun `given connected AND NO previous MediaItem is set when removeMediaItem with index greater than zero then state is correct`() {
        // given
        val player = TestExoPlayerBuilder(context).build()
        sut.connect(player) {}

        // when
        val whenBlock = { sut.removeMediaItem(1) }

        // then
        assertThrows(IllegalArgumentException::class.java) { whenBlock() }
    }

    @Test
    fun `given previous MediaItem is set when removeMediaItem with invalid index then state is correct`() {
        // given
        val player = TestExoPlayerBuilder(context).build()
        sut.connect(player) {}
        val previousMediaItem = getStubMediaItem("id1")
        sut.setMediaItem(previousMediaItem)

        // when
        val whenBlock = { sut.removeMediaItem(-1) }

        // then
        assertThrows(IllegalArgumentException::class.java) { whenBlock() }
    }

    @Test
    fun `given connected AND previous MediaItem is set when removeMediaItem with current item index then state is correct`() {
        // given
        val player = TestExoPlayerBuilder(context).build()
        sut.connect(player) {}

        val mediaItem1 = getStubMediaItem("id1")
        val mediaItem2 = getStubMediaItem("id2")
        val mediaItems = listOf(mediaItem1, mediaItem2)
        sut.setMediaItems(mediaItems)

        // when
        sut.removeMediaItem(0)
        runUntilPendingCommandsAreFullyHandled(player)

        // then
        assertThat(sut.getMediaItemCount()).isEqualTo(1)
        assertThat(sut.getMediaItemAt(0)).isEqualTo(mediaItem2)
        assertThat(sut.currentState.value).isEqualTo(PlayerState.Idle)
        assertThat(sut.currentMediaItem.value).isEqualTo(mediaItem2)
        assertThat(sut.playbackSpeed.value).isEqualTo(1f)
        assertThat(sut.shuffleModeEnabled.value).isFalse()
        assertThat(sut.player.value).isSameInstanceAs(player)
        assertThat(sut.mediaItemPosition.value).isEqualTo(MediaItemPosition.UnknownDuration(0.seconds))
        assertThat(sut.availableCommands.value).containsExactlyElementsIn(
            listOf(Command.PlayPause, Command.SetShuffle)
        )
    }

    @Test
    fun `given connected AND previous MediaItem is set when removeMediaItem with queued item index then state is correct`() {
        // given
        val player = TestExoPlayerBuilder(context).build()
        sut.connect(player) {}

        val mediaItem1 = getStubMediaItem("id1")
        val mediaItem2 = getStubMediaItem("id2")
        val mediaItems = listOf(mediaItem1, mediaItem2)
        sut.setMediaItems(mediaItems)

        // when
        sut.removeMediaItem(1)
        runUntilPendingCommandsAreFullyHandled(player)

        // then
        assertThat(sut.getMediaItemCount()).isEqualTo(1)
        assertThat(sut.getMediaItemAt(0)).isEqualTo(mediaItem1)
        assertThat(sut.currentState.value).isEqualTo(PlayerState.Idle)
        assertThat(sut.currentMediaItem.value).isEqualTo(mediaItem1)
        assertThat(sut.playbackSpeed.value).isEqualTo(1f)
        assertThat(sut.shuffleModeEnabled.value).isFalse()
        assertThat(sut.player.value).isSameInstanceAs(player)
        assertThat(sut.mediaItemPosition.value).isEqualTo(MediaItemPosition.UnknownDuration(0.seconds))
        assertThat(sut.availableCommands.value).containsExactlyElementsIn(
            listOf(Command.PlayPause, Command.SetShuffle)
        )
    }

    @Test
    fun `given connected AND NO previous MediaItem is set when clearMediaItems then state is correct`() {
        // given
        val player = TestExoPlayerBuilder(context).build()
        sut.connect(player) {}

        // when
        sut.clearMediaItems()

        // then
        assertThat(sut.getMediaItemCount()).isEqualTo(0)
        assertThat(sut.currentState.value).isEqualTo(PlayerState.Idle)
        assertThat(sut.currentMediaItem.value).isNull()
        assertThat(sut.playbackSpeed.value).isEqualTo(1f)
        assertThat(sut.shuffleModeEnabled.value).isFalse()
        assertThat(sut.player.value).isSameInstanceAs(player)
        assertThat(sut.mediaItemPosition.value).isNull()
        assertThat(sut.availableCommands.value).containsExactlyElementsIn(
            listOf(Command.PlayPause, Command.SetShuffle)
        )
    }

    @Test
    fun `given connected AND previous MediaItem is set when clearMediaItems then state is correct`() {
        // given
        val player = TestExoPlayerBuilder(context).build()
        sut.connect(player) {}

        sut.setMediaItems(listOf(getStubMediaItem("id1"), getStubMediaItem("id2")))

        // when
        sut.clearMediaItems()

        // then
        assertThat(sut.getMediaItemCount()).isEqualTo(0)
        assertThat(sut.currentState.value).isEqualTo(PlayerState.Idle)
        assertThat(sut.currentMediaItem.value).isNull()
        assertThat(sut.playbackSpeed.value).isEqualTo(1f)
        assertThat(sut.shuffleModeEnabled.value).isFalse()
        assertThat(sut.player.value).isSameInstanceAs(player)
        assertThat(sut.mediaItemPosition.value).isEqualTo(MediaItemPosition.UnknownDuration(0.seconds))
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

        val mediaItem = getStubMediaItem("id")

        sut.setMediaItem(mediaItem)
        sut.prepare()
        sut.play()
        playUntilPosition(player, 0, 5.seconds.inWholeMilliseconds)

        // when
        sut.updatePosition()

        // then
        assertThat(sut.currentState.value).isEqualTo(PlayerState.Playing)
        assertThat(sut.currentMediaItem.value).isEqualTo(mediaItem)
        assertThat(sut.playbackSpeed.value).isEqualTo(1f)
        assertThat(sut.shuffleModeEnabled.value).isFalse()
        assertThat(sut.player.value).isSameInstanceAs(player)
        assertThat(sut.availableCommands.value).containsExactlyElementsIn(
            listOf(Command.PlayPause, Command.SeekBack, Command.SeekForward, Command.SetShuffle)
        )

        assertThat(sut.mediaItemPosition.value).isInstanceOf(MediaItemPosition.KnownDuration::class.java)
        val expectedMediaItemPosition =
            MediaItemPosition.create(current = 1020.milliseconds, duration = 1022.milliseconds)
        val actualMediaItemPosition = sut.mediaItemPosition.value as MediaItemPosition.KnownDuration
        // TODO these checks can be simplified to `assertThat().isEqualTo()` once horologist implements equals for MediaItemPosition.KnownDuration
        assertThat(actualMediaItemPosition.current).isEqualTo(expectedMediaItemPosition.current)
        assertThat(actualMediaItemPosition.duration).isEqualTo(expectedMediaItemPosition.duration)
        assertThat(actualMediaItemPosition.percent).isEqualTo(expectedMediaItemPosition.percent)
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
        assertThat(sut.currentMediaItem.value).isNull()
        assertThat(sut.playbackSpeed.value).isEqualTo(1f)
        assertThat(sut.shuffleModeEnabled.value).isFalse()
        assertThat(sut.player.value).isSameInstanceAs(player)
        assertThat(sut.mediaItemPosition.value).isNull()
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

        // then
        assertThat(sut.currentState.value).isEqualTo(PlayerState.Idle)
        assertThat(sut.currentMediaItem.value).isNull()
        assertThat(sut.playbackSpeed.value).isEqualTo(speed)
        assertThat(sut.shuffleModeEnabled.value).isFalse()
        assertThat(sut.player.value).isSameInstanceAs(player)
        assertThat(sut.mediaItemPosition.value).isNull()
        assertThat(sut.availableCommands.value).containsExactlyElementsIn(
            listOf(Command.PlayPause, Command.SetShuffle)
        )
    }

    private fun getDummyMediaItem() = MediaItem(
        id = "id",
        uri = "uri",
        title = "title",
        artist = "artist",
        artworkUri = "artworkUri",
    )

    private fun getStubMediaItem(id: String) = MediaItem(
        id = id,
        uri = "asset://android_asset/media/mp4/testvid_1022ms.mp4",
        title = "title",
        artist = "artist",
        artworkUri = "artworkUri",
    )

    private fun assertInitialState() {
        assertThat(sut.currentState.value).isEqualTo(PlayerState.Idle)
        assertThat(sut.currentMediaItem.value).isNull()
        assertThat(sut.playbackSpeed.value).isEqualTo(1f)
        assertThat(sut.shuffleModeEnabled.value).isFalse()
        assertThat(sut.player.value).isNull()
        assertThat(sut.mediaItemPosition.value).isNull()
        assertThat(sut.availableCommands.value).isEmpty()
    }
}
