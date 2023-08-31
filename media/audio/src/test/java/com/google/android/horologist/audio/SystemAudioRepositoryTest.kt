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

package com.google.android.horologist.audio

import android.content.Context
import android.media.AudioManager
import androidx.mediarouter.media.MediaRouter
import androidx.test.filters.MediumTest
import androidx.test.platform.app.InstrumentationRegistry
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import org.junit.Before
import org.junit.Ignore
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows
import org.robolectric.annotation.Config
import kotlin.time.Duration.Companion.seconds
import android.media.MediaRouter as MediaRouterLegacy

@MediumTest
@RunWith(RobolectricTestRunner::class)
@Config(
    sdk = [30],
)
class SystemAudioRepositoryTest {
    private lateinit var mediaRouter: MediaRouter
    private lateinit var audioManager: AudioManager
    private lateinit var mediaRouterLegacy: MediaRouterLegacy
    private lateinit var context: Context

    @Before
    fun setUp() {
        context = InstrumentationRegistry.getInstrumentation().targetContext
        mediaRouterLegacy =
            context.getSystemService(Context.MEDIA_ROUTER_SERVICE) as MediaRouterLegacy

        audioManager =
            context.getSystemService(Context.AUDIO_SERVICE) as AudioManager

        mediaRouter = MediaRouter.getInstance(context)
    }

    @Test
    fun testAudioOutputRepository() {
        SystemAudioRepository.fromContext(context).use { repository ->
            assertThat(repository.audioOutput.value).isNotNull()
            assertThat(repository.available.value).isNotEmpty()

            repository.close()

            assertThat(repository.audioOutput.value).isEqualTo(AudioOutput.None)
            assertThat(repository.available.value).isEmpty()
        }
    }

    @Test
    @Ignore("File robolectric bug for MediaRouter")
    fun testVolumeRepository() {
        Shadows.shadowOf(mediaRouterLegacy).addBluetoothRoute()
        Shadows.shadowOf(audioManager).apply {
            setStreamMaxVolume(5)
            setStreamMaxVolume(10)
        }

        assertThat(mediaRouter.selectedRoute.deviceType)
            .isEqualTo(MediaRouter.RouteInfo.DEVICE_TYPE_BLUETOOTH)

        SystemAudioRepository.fromContext(context).use { repository ->
            val startingVolume = repository.volumeState.value.current

            repository.increaseVolume()

            val newVolume = runBlocking {
                withTimeout(2.seconds) {
                    repository.volumeState
                        .map { it.current }
                        .filter { it > startingVolume }
                        .first()
                }
            }

            assertThat(newVolume).isGreaterThan(startingVolume)
            repository.close()
        }
    }
}
