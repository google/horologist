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

@file:OptIn(
    ExperimentalHorologistMedia3BackendApi::class
)

package com.google.android.horologist.media3

import android.content.Context
import android.net.Uri
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.test.utils.TestExoPlayerBuilder
import androidx.test.core.app.ApplicationProvider
import com.google.android.horologist.audio.AudioOutput
import com.google.android.horologist.media3.rules.PlaybackRules
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Ignore
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@Ignore("Working on proper fix and also test for playback bug")
@RunWith(RobolectricTestRunner::class)
class WearConfiguredPlayerTest {
    private val context: Context = ApplicationProvider.getApplicationContext()
    private val player = TestExoPlayerBuilder(context).build()

    private val audioOutputRepository = FakeAudioOutputRepository()
    private val errorReporter = FakeErrorReporter()

    val mediaItem1 = exampleMediaItem("1")

    @After
    fun tearDown() {
        player.release()
    }

    @Test
    fun abortsPlaybackIfNotPlayable() = runTest {
        val playbackRules = object : PlaybackRules {
            override suspend fun canPlayItem(mediaItem: MediaItem): Boolean {
                return false
            }

            override fun canPlayWithOutput(audioOutput: AudioOutput): Boolean {
                return true
            }
        }

        val audioOutputSelector = FakeAudioOutputSelector(null, audioOutputRepository)

        val wearConfiguredPlayer = WearConfiguredPlayer(
            player,
            audioOutputRepository,
            audioOutputSelector,
            playbackRules,
            errorReporter,
            coroutineScope = this
        )

        wearConfiguredPlayer.setMediaItem(mediaItem1)
        wearConfiguredPlayer.prepare()
        wearConfiguredPlayer.play()

        advanceUntilIdle()

        assertThat(errorReporter.messages).contains(R.string.horologist_cant_play_item)
        assertThat(player.playWhenReady).isFalse()
    }

    @Test
    fun playsNormallyIfAllowed() = runTest {
        val playbackRules = object : PlaybackRules {
            override suspend fun canPlayItem(mediaItem: MediaItem): Boolean {
                return true
            }

            override fun canPlayWithOutput(audioOutput: AudioOutput): Boolean {
                return audioOutput is AudioOutput.BluetoothHeadset
            }
        }

        val audioOutputSelector = FakeAudioOutputSelector(null, audioOutputRepository)

        coroutineScope {
            try {
                val wearConfiguredPlayer = WearConfiguredPlayer(
                    player,
                    audioOutputRepository,
                    audioOutputSelector,
                    playbackRules,
                    errorReporter,
                    coroutineScope = this
                )

                val mediaItem = exampleMediaItem("1")
                wearConfiguredPlayer.setMediaItem(mediaItem)
                wearConfiguredPlayer.prepare()
                wearConfiguredPlayer.play()

                assertThat(errorReporter.messages).isEmpty()
                assertThat(player.playWhenReady).isTrue()
            } catch (e: Throwable) {
                e.printStackTrace()
            }
        }
    }

    fun exampleMediaItem(id: String) = MediaItem.Builder()
        .setMediaId(id)
        .setUri("asset://android_asset/media/mp4/testvid_1022ms.mp4")
        .setMediaMetadata(
            MediaMetadata.Builder()
                .setTitle("title-$id")
                .setArtist("artist")
                .setArtworkUri(Uri.parse("https://upload.wikimedia.org/wikipedia/en/9/93/ForSquirrelsExample.jpg"))
                .build()
        )
        .build()
}
