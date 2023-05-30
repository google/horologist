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

package com.google.android.horologist.mediasample.playback

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.google.android.horologist.media.data.mapper.MediaItemExtrasMapperNoopImpl
import com.google.android.horologist.media.data.mapper.MediaItemMapper
import com.google.android.horologist.media.model.Media
import com.google.common.truth.Truth.assertThat
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.withContext
import org.junit.Ignore
import org.junit.Test
import org.junit.runner.RunWith

@Ignore("https://github.com/google/horologist/issues/1191")
@RunWith(AndroidJUnit4::class)
@LargeTest
@HiltAndroidTest
class PlaybackErrorTest : BasePlaybackTest() {

    private val mediaItemMapper: MediaItemMapper = MediaItemMapper(MediaItemExtrasMapperNoopImpl)

    @Test
    fun testFailingItem() = runTest {
        withContext(Dispatchers.Main) {
            val browser = browser()

            val badContent = Media(
                "1",
                "milkjawn",
                "milkjawn",
                "Milk Jawn",
                "https://cdn.player.fm/images/14416069/series/stoRUvKcFOzInZ1X/512.jpg"
            )

            browser.setMediaItem(
                mediaItemMapper.map(badContent)
            )
            browser.prepare()
            browser.play()

            // allow for async operations
            delay(5000)

            assertThat(browser.isPlaying).isFalse()
        }
    }
}
