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
import androidx.media3.test.utils.TestExoPlayerBuilder
import androidx.test.core.app.ApplicationProvider
import com.google.android.horologist.media.model.MediaItem
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
        sut = PlayerRepositoryImpl()
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
            param("play with index") { sut: PlayerRepositoryImpl, _: Context ->
                sut.play(1)
            },
            param("pause") { sut: PlayerRepositoryImpl, _: Context ->
                sut.pause()
            },
            param("hasPreviousMediaItem") { sut: PlayerRepositoryImpl, _: Context ->
                sut.hasPreviousMediaItem()
            },
            param("skipToPreviousMediaItem") { sut: PlayerRepositoryImpl, _: Context ->
                sut.skipToPreviousMediaItem()
            },
            param("hasNextMediaItem") { sut: PlayerRepositoryImpl, _: Context ->
                sut.hasNextMediaItem()
            },
            param("skipToNextMediaItem") { sut: PlayerRepositoryImpl, _: Context ->
                sut.skipToNextMediaItem()
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
            param("setMediaItem") { sut: PlayerRepositoryImpl, _: Context ->
                sut.setMediaItem(getDummyMediaItem())
            },
            param("setMediaItems") { sut: PlayerRepositoryImpl, _: Context ->
                sut.setMediaItems(listOf(getDummyMediaItem()))
            },
            param("addMediaItem") { sut: PlayerRepositoryImpl, _: Context ->
                sut.addMediaItem(getDummyMediaItem())
            },
            param("addMediaItem with index") { sut: PlayerRepositoryImpl, _: Context ->
                sut.addMediaItem(1, getDummyMediaItem())
            },
            param("removeMediaItem") { sut: PlayerRepositoryImpl, _: Context ->
                sut.removeMediaItem(1)
            },
            param("clearMediaItems") { sut: PlayerRepositoryImpl, _: Context ->
                sut.clearMediaItems()
            },
            param("getMediaItemCount") { sut: PlayerRepositoryImpl, _: Context ->
                sut.getMediaItemCount()
            },
            param("getMediaItemAt") { sut: PlayerRepositoryImpl, _: Context ->
                sut.getMediaItemAt(1)
            },
            param("getCurrentMediaItemIndex") { sut: PlayerRepositoryImpl, _: Context ->
                sut.getCurrentMediaItemIndex()
            },
            param("release") { sut: PlayerRepositoryImpl, _: Context ->
                sut.release()
            },
        )

        private fun param(
            description: String,
            whenBlock: (PlayerRepositoryImpl, Context) -> Unit
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
