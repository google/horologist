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

import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.audio.DefaultAudioSink
import androidx.test.espresso.matcher.ViewMatchers
import com.google.android.horologist.media.data.Media3MediaItemMapper
import com.google.android.horologist.media3.flows.currentMediaItemFlow
import com.google.android.horologist.media3.flows.waitForNotPlaying
import com.google.android.horologist.media3.flows.waitForPlaying
import com.google.android.horologist.mediasample.samples.GaplessSamples
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.withContext
import org.junit.Test

class GaplessPlaybackTest : BasePlaybackTest() {
    override val appConfig: AppConfig
        get() = AppConfig(
            offloadEnabled = false,
            offloadMode = DefaultAudioSink.OFFLOAD_MODE_DISABLED
        )

    @Test
    fun testPlayback() = runTest {
        val browser = browser()

        val items = mutableListOf<String?>()

        launch {
            browser.currentMediaItemFlow().collect {
                items.add(it?.mediaId)
            }
        }

        withContext(Dispatchers.Main) {
            browser.setMediaItems(Media3MediaItemMapper.map(GaplessSamples))
            browser.prepare()
            browser.play()
        }

        browser.waitForPlaying()

        browser.waitForNotPlaying()

        Truth.assertThat()
    }
}
