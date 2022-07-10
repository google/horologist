/*
 * Copyright 2022 Google Inc. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.google.android.horologist.media.data

import android.content.Context
import android.os.Looper.getMainLooper
import androidx.test.core.app.ApplicationProvider
import com.google.android.horologist.media.model.MediaItem
import com.google.android.horologist.media.model.PlayerState
import com.google.common.truth.Truth.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.ParameterizedRobolectricTestRunner
import org.robolectric.Shadows.shadowOf
import org.robolectric.annotation.Config

@RunWith(ParameterizedRobolectricTestRunner::class)
@Config(manifest = Config.NONE)
class PlayerRepositoryImplNotConnectedTest(
    private val description: String,
    private val whenBlock: (PlayerRepositoryImpl) -> Unit
) {

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
    fun `given not connected when block is called then state is correct`() {
        // when
        whenBlock(sut)

        // then
        assertThat(sut.currentState.value).isEqualTo(PlayerState.Idle)
        assertThat(sut.currentMediaItem.value).isNull()
        assertThat(sut.playbackSpeed.value).isEqualTo(1f)
        assertThat(sut.shuffleModeEnabled.value).isFalse()
        assertThat(sut.player.value).isNull()
        assertThat(sut.mediaItemPosition.value).isNull()
        assertThat(sut.availableCommands.value).isEmpty()
    }

    companion object {

        @JvmStatic
        @ParameterizedRobolectricTestRunner.Parameters(name = "{0}")
        fun params() = listOf(
            param("initial state") { },
            param("prepare") { sut: PlayerRepositoryImpl ->
                sut.play()
            },
            param("play") { sut: PlayerRepositoryImpl ->
                sut.play()
            },
            param("play with index") { sut: PlayerRepositoryImpl ->
                sut.play(1)
            },
            param("pause") { sut: PlayerRepositoryImpl ->
                sut.pause()
            },
            param("hasPreviousMediaItem") { sut: PlayerRepositoryImpl ->
                sut.hasPreviousMediaItem()
            },
            param("skipToPreviousMediaItem") { sut: PlayerRepositoryImpl ->
                sut.skipToPreviousMediaItem()
            },
            param("hasNextMediaItem") { sut: PlayerRepositoryImpl ->
                sut.hasNextMediaItem()
            },
            param("skipToNextMediaItem") { sut: PlayerRepositoryImpl ->
                sut.skipToNextMediaItem()
            },
            param("getSeekBackIncrement") { sut: PlayerRepositoryImpl ->
                sut.getSeekBackIncrement()
            },
            param("seekBack") { sut: PlayerRepositoryImpl ->
                sut.seekBack()
            },
            param("getSeekForwardIncrement") { sut: PlayerRepositoryImpl ->
                sut.getSeekForwardIncrement()
            },
            param("seekForward") { sut: PlayerRepositoryImpl ->
                sut.seekForward()
            },
            param("setShuffleModeEnabled") { sut: PlayerRepositoryImpl ->
                sut.setShuffleModeEnabled(true)
            },
            param("setMediaItem") { sut: PlayerRepositoryImpl ->
                sut.setMediaItem(getDummyMediaItem())
            },
            param("setMediaItems") { sut: PlayerRepositoryImpl ->
                sut.setMediaItems(listOf(getDummyMediaItem()))
            },
            param("addMediaItem") { sut: PlayerRepositoryImpl ->
                sut.addMediaItem(getDummyMediaItem())
            },
            param("addMediaItem with index") { sut: PlayerRepositoryImpl ->
                sut.addMediaItem(1, getDummyMediaItem())
            },
            param("removeMediaItem") { sut: PlayerRepositoryImpl ->
                sut.removeMediaItem(1)
            },
            param("clearMediaItems") { sut: PlayerRepositoryImpl ->
                sut.clearMediaItems()
            },
            param("getMediaItemCount") { sut: PlayerRepositoryImpl ->
                sut.getMediaItemCount()
            },
            param("getMediaItemAt") { sut: PlayerRepositoryImpl ->
                sut.getMediaItemAt(1)
            },
            param("getCurrentMediaItemIndex") { sut: PlayerRepositoryImpl ->
                sut.getCurrentMediaItemIndex()
            },
            param("updatePosition") { sut: PlayerRepositoryImpl ->
                sut.updatePosition()
            },
            param("setPlaybackSpeed") { sut: PlayerRepositoryImpl ->
                sut.setPlaybackSpeed(2f)
            },
        )

        private fun param(
            description: String,
            whenBlock: (PlayerRepositoryImpl) -> Unit
        ) = arrayOf(description, whenBlock)

        private fun getDummyMediaItem() = MediaItem(
            id = "id",
            uri = "uri",
            title = "title",
            artist = "artist",
            artworkUri = "artworkUri",
        )
    }
}
