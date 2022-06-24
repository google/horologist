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

@file:OptIn(ExperimentalCoroutinesApi::class)

package com.google.android.horologist.mediasample

import androidx.media3.exoplayer.audio.DefaultAudioSink
import com.google.android.horologist.media.data.Media3MediaItemMapper
import com.google.android.horologist.media3.flows.currentMediaItemFlow
import com.google.android.horologist.media3.flows.waitForNotPlaying
import com.google.android.horologist.media3.flows.waitForPlaying
import com.google.android.horologist.media3.rules.PlaybackRules
import com.google.android.horologist.mediasample.samples.GaplessSamples
import com.google.common.truth.Truth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.withContext
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized

@RunWith(Parameterized::class)
class GaplessPlaybackTest(override val appConfig: AppConfig) : BasePlaybackTest() {
    @Test
    fun testPlayback() {
        runTest {
            val browser = browser()

            val items = mutableSetOf<String?>()

            val job = launch {
                browser.currentMediaItemFlow().collect {
                    items.add(it?.requestMetadata!!.mediaUri.toString())
                }
            }

            val mediaItems = GaplessSamples

            withContext(Dispatchers.Main) {
                browser.setMediaItems(Media3MediaItemMapper.map(mediaItems))
                browser.prepare()
                browser.play()
            }

            browser.waitForPlaying()

            if (appConfig.offloadEnabled) {
                val format = appContainer.audioOffloadManager.format.value
                println(format?.sampleMimeType)
            }

            browser.waitForNotPlaying()

            Truth.assertThat(items).containsExactlyElementsIn(mediaItems.map { it.uri })

            if (appConfig.offloadEnabled) {
                val offloadTimes = appContainer.audioOffloadManager.snapOffloadTimes()
                println(offloadTimes)
                Truth.assertThat(offloadTimes.enabled).isGreaterThan(offloadTimes.disabled)
            }

            job.cancel()
        }
    }

    companion object {
        val OffloadDisabled = AppConfig(
            offloadEnabled = false,
            offloadMode = DefaultAudioSink.OFFLOAD_MODE_DISABLED
        )

        val OffloadNotRequired = AppConfig(
            offloadEnabled = true,
            offloadMode = DefaultAudioSink.OFFLOAD_MODE_ENABLED_GAPLESS_NOT_REQUIRED
        )

        val OffloadRequired = AppConfig(
            offloadEnabled = true,
            offloadMode = DefaultAudioSink.OFFLOAD_MODE_ENABLED_GAPLESS_REQUIRED
        )

        val SpeakerAllowed = AppConfig(
            offloadEnabled = true,
            offloadMode = DefaultAudioSink.OFFLOAD_MODE_ENABLED_GAPLESS_NOT_REQUIRED,
            playbackRules = PlaybackRules.SpeakerAllowed
        )

        @JvmStatic
        @Parameterized.Parameters
        fun data() = listOf(OffloadNotRequired)
    }
}
