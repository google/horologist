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

package com.google.android.horologist.mediasample.offload

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.google.android.horologist.media.data.Media3MediaItemMapper
import com.google.android.horologist.media.model.MediaItem
import com.google.android.horologist.mediasample.playback.BasePlaybackTest
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.withContext
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@LargeTest
class GaplessOffloadPlaybackTest : BasePlaybackTest() {
    @Test
    fun testMediaBrowser() = runTest {
        val browser = browser()

        withContext(Dispatchers.Main) {
            assertThat(browser.isConnected).isTrue()
        }

        val GaplessSamples = listOf(
            "https://dl.espressif.com/dl/audio/ff-16b-1c-32000hz.mp3",
            "https://www2.iis.fraunhofer.de/AAC/gapless-sweep_part2_iis.m4a"
        ).mapIndexed { i, it ->
            MediaItem(
                id = i.toString(),
                uri = it,
                title = "Track $i",
                artist = "fraunhofer",
                artworkUri = "https://www2.iis.fraunhofer.de/AAC/logo-fraunhofer.gif",
            )
        }

        val items = Media3MediaItemMapper.map(GaplessSamples)

        withContext(Dispatchers.Main) {
            browser.setMediaItems(items)
            browser.prepare()
            browser.play()
        }

        delay(20000)
    }
}
