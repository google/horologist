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

package com.google.android.horologist.media.data.repository

import android.content.Context
import android.os.Looper.getMainLooper
import androidx.test.core.app.ApplicationProvider
import com.google.android.horologist.media.model.Media
import com.google.android.horologist.media.model.PlaybackStateEvent
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
    @Suppress("unused") // it's used by junit to display the test name
    private val description: String,
    private val whenBlock: (PlayerRepositoryImpl) -> Unit,
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
        assertThat(sut.currentMedia.value).isNull()
        assertThat(sut.shuffleModeEnabled.value).isFalse()
        assertThat(sut.player.value).isNull()
        assertThat(sut.latestPlaybackState.value).isEqualTo(PlaybackStateEvent.INITIAL)
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
            param("seek to default index") { sut: PlayerRepositoryImpl ->
                sut.seekToDefaultPosition(1)
            },
            param("pause") { sut: PlayerRepositoryImpl ->
                sut.pause()
            },
            param("hasPreviousMedia") { sut: PlayerRepositoryImpl ->
                sut.hasPreviousMedia()
            },
            param("skipToPreviousMedia") { sut: PlayerRepositoryImpl ->
                sut.skipToPreviousMedia()
            },
            param("hasNextMedia") { sut: PlayerRepositoryImpl ->
                sut.hasNextMedia()
            },
            param("skipToNextMedia") { sut: PlayerRepositoryImpl ->
                sut.skipToNextMedia()
            },
            param("seekBack") { sut: PlayerRepositoryImpl ->
                sut.seekBack()
            },
            param("seekForward") { sut: PlayerRepositoryImpl ->
                sut.seekForward()
            },
            param("setShuffleModeEnabled") { sut: PlayerRepositoryImpl ->
                sut.setShuffleModeEnabled(true)
            },
            param("setMedia") { sut: PlayerRepositoryImpl ->
                sut.setMedia(getDummyMedia())
            },
            param("setMediaList") { sut: PlayerRepositoryImpl ->
                sut.setMediaList(listOf(getDummyMedia()))
            },
            param("addMedia") { sut: PlayerRepositoryImpl ->
                sut.addMedia(getDummyMedia())
            },
            param("addMedia with index") { sut: PlayerRepositoryImpl ->
                sut.addMedia(1, getDummyMedia())
            },
            param("removeMedia") { sut: PlayerRepositoryImpl ->
                sut.removeMedia(1)
            },
            param("clearMediaList") { sut: PlayerRepositoryImpl ->
                sut.clearMediaList()
            },
            param("getMediaCount") { sut: PlayerRepositoryImpl ->
                sut.getMediaCount()
            },
            param("getMediaAt") { sut: PlayerRepositoryImpl ->
                sut.getMediaAt(1)
            },
            param("getCurrentMediaIndex") { sut: PlayerRepositoryImpl ->
                sut.getCurrentMediaIndex()
            },
            param("setPlaybackSpeed") { sut: PlayerRepositoryImpl ->
                sut.setPlaybackSpeed(2f)
            },
        )

        private fun param(
            description: String,
            whenBlock: (PlayerRepositoryImpl) -> Unit,
        ) = arrayOf(description, whenBlock)

        private fun getDummyMedia() = Media(
            id = "id",
            uri = "uri",
            title = "title",
            artist = "artist",
            artworkUri = "artworkUri",
        )
    }
}
