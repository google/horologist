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
import androidx.media3.test.utils.TestExoPlayerBuilder
import androidx.test.core.app.ApplicationProvider
import com.google.android.horologist.media.data.ExperimentalHorologistMediaDataApi
import com.google.android.horologist.media.data.mapper.MediaExtrasMapperNoopImpl
import com.google.android.horologist.media.data.mapper.MediaItemExtrasMapperNoopImpl
import com.google.android.horologist.media.data.mapper.MediaItemMapper
import com.google.android.horologist.media.data.mapper.MediaMapper
import com.google.android.horologist.media.model.Media
import org.junit.Assert.assertThrows
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.ParameterizedRobolectricTestRunner
import org.robolectric.Shadows.shadowOf
import org.robolectric.annotation.Config

@RunWith(ParameterizedRobolectricTestRunner::class)
@Config(manifest = Config.NONE)
class PlayerRepositoryImplCloseTest(
    @Suppress("unused") // it's used by junit to display the test name
    private val description: String,
    private val whenBlock: (PlayerRepositoryImpl, Context) -> Unit
) {

    private lateinit var sut: PlayerRepositoryImpl

    private lateinit var context: Context

    @Before
    fun setUp() {
        // execute all tasks posted to main looper
        shadowOf(getMainLooper()).idle()

        context = ApplicationProvider.getApplicationContext()
        sut = PlayerRepositoryImpl(
            mediaMapper = MediaMapper(MediaExtrasMapperNoopImpl),
            mediaItemMapper = MediaItemMapper(MediaItemExtrasMapperNoopImpl)
        )
    }

    @Test
    fun `given is closed when block is called then exception is thrown`() {
        // given
        sut.close()

        // then
        assertThrows(IllegalStateException::class.java) { whenBlock(sut, context) }
    }

    companion object {

        @JvmStatic
        @ParameterizedRobolectricTestRunner.Parameters(name = "{0}")
        fun params() = listOf(
            param("connect") { sut: PlayerRepositoryImpl, context: Context ->
                sut.connect(TestExoPlayerBuilder(context).build()) {}
            },
            param("prepare") { sut: PlayerRepositoryImpl, _: Context ->
                sut.prepare()
            },
            param("play") { sut: PlayerRepositoryImpl, _: Context ->
                sut.play()
            },
            param("seek to default position") { sut: PlayerRepositoryImpl, _: Context ->
                sut.seekToDefaultPosition(1)
            },
            param("pause") { sut: PlayerRepositoryImpl, _: Context ->
                sut.pause()
            },
            param("hasPreviousMedia") { sut: PlayerRepositoryImpl, _: Context ->
                sut.hasPreviousMedia()
            },
            param("skipToPreviousMedia") { sut: PlayerRepositoryImpl, _: Context ->
                sut.skipToPreviousMedia()
            },
            param("hasNextMedia") { sut: PlayerRepositoryImpl, _: Context ->
                sut.hasNextMedia()
            },
            param("skipToNextMedia") { sut: PlayerRepositoryImpl, _: Context ->
                sut.skipToNextMedia()
            },
            param("getSeekBackIncrement") { sut: PlayerRepositoryImpl, _: Context ->
                sut.getSeekBackIncrement()
            },
            param("seekBack") { sut: PlayerRepositoryImpl, _: Context ->
                sut.seekBack()
            },
            param("getSeekForwardIncrement") { sut: PlayerRepositoryImpl, _: Context ->
                sut.getSeekForwardIncrement()
            },
            param("seekForward") { sut: PlayerRepositoryImpl, _: Context ->
                sut.seekForward()
            },
            param("setShuffleModeEnabled") { sut: PlayerRepositoryImpl, _: Context ->
                sut.setShuffleModeEnabled(true)
            },
            param("setMedia") { sut: PlayerRepositoryImpl, _: Context ->
                sut.setMedia(getDummyMedia())
            },
            param("setMediaList") { sut: PlayerRepositoryImpl, _: Context ->
                sut.setMediaList(listOf(getDummyMedia()))
            },
            param("addMedia") { sut: PlayerRepositoryImpl, _: Context ->
                sut.addMedia(getDummyMedia())
            },
            param("addMedia with index") { sut: PlayerRepositoryImpl, _: Context ->
                sut.addMedia(1, getDummyMedia())
            },
            param("removeMedia") { sut: PlayerRepositoryImpl, _: Context ->
                sut.removeMedia(1)
            },
            param("clearMediaList") { sut: PlayerRepositoryImpl, _: Context ->
                sut.clearMediaList()
            },
            param("getMediaCount") { sut: PlayerRepositoryImpl, _: Context ->
                sut.getMediaCount()
            },
            param("getMediaAt") { sut: PlayerRepositoryImpl, _: Context ->
                sut.getMediaAt(1)
            },
            param("getCurrentMediaIndex") { sut: PlayerRepositoryImpl, _: Context ->
                sut.getCurrentMediaIndex()
            },
            param("release") { sut: PlayerRepositoryImpl, _: Context ->
                sut.release()
            },
        )

        private fun param(
            description: String,
            whenBlock: (PlayerRepositoryImpl, Context) -> Unit
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
