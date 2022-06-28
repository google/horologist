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

package com.google.android.horologist.mediasample

import android.media.AudioFormat
import android.media.AudioManager
import android.os.Build
import androidx.media3.common.Format
import androidx.media3.common.MimeTypes
import androidx.media3.common.util.Assertions
import androidx.media3.common.util.Util
import androidx.media3.exoplayer.audio.AudioSink
import com.google.android.horologist.audio.AudioOutput
import com.google.android.horologist.media.data.Media3MediaItemMapper
import com.google.android.horologist.media.model.MediaItem
import com.google.android.horologist.media3.flows.waitForPlaying
import com.google.android.horologist.mediasample.playback.BasePlaybackTest
import com.google.common.truth.Truth.assertThat
import com.google.common.truth.Truth.assertWithMessage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import org.junit.Assume.assumeFalse
import org.junit.Test
import kotlin.time.Duration.Companion.seconds

class AudioOffloadTest : BasePlaybackTest() {
    override fun checkSupportedConfig() {
        super.checkSupportedConfig()

        assumeFalse("Offload not supported on emulator", appContainer.isEmulator)
    }

    @Test
    fun testAudioOffload() = runTest {
        val browser = browser()

        val mediaItem = Media3MediaItemMapper.map(
            MediaItem(
                id = "1",
                title = "Intro - The Way Of Waking Up (feat. Alan Watts)",
                uri = "https://storage.googleapis.com/uamp/The_Kyoto_Connection_-_Wake_Up/01_-_Intro_-_The_Way_Of_Waking_Up_feat_Alan_Watts.mp3",
                artist = "The Kyoto Connection",
                artworkUri = "https://storage.googleapis.com/uamp/The_Kyoto_Connection_-_Wake_Up/art.jpg",
            )
        )

        assertThat(audioOffloadManager.foreground.value).isFalse()

        val output = audioOutputRepository.audioOutput.value

        withContext(Dispatchers.Main) {
            browser.setMediaItem(mediaItem)
            browser.prepare()
            browser.play()

            assertThat(audioOffloadManager.offloadSchedulingEnabled.value).isTrue()

            withTimeout(10.seconds) {
                browser.waitForPlaying()
            }

            val format: Format = audioOffloadManager.format.value!!

            val offloadExpected = offloadExpected(output, Build.MODEL, format)

            val flags = checkAdbDumpsys()

            if (offloadExpected) {
                // This should be always true because ExoPlayer does this
                val audioFormat = format.audioFormat()
                val offloadedPlaybackSupported =
                    AudioManager.isOffloadedPlaybackSupported(
                        audioFormat,
                        browser.audioAttributes.audioAttributesV21.audioAttributes
                    )
                assertThat(offloadedPlaybackSupported).isTrue()

                val formatSupport = audioSink.getFormatSupport(format)
                assertThat(formatSupport).isEqualTo(AudioSink.SINK_FORMAT_SUPPORTED_DIRECTLY)

                assertWithMessage("sleepingForOffload")
                    .that(audioOffloadManager.sleepingForOffload.value)
                    .isTrue()

                assertThat(flags.contains("AUDIO_OUTPUT_FLAG_COMPRESS_OFFLOAD"))

                assertThat(audioOffloadManager.times.value.enabled).isGreaterThan(0)
            } else {
                assertWithMessage("sleepingForOffload")
                    .that(audioOffloadManager.sleepingForOffload.value)
                    .isFalse()

                assertThat(!flags.contains("AUDIO_OUTPUT_FLAG_COMPRESS_OFFLOAD"))
                assertThat(flags.contains("AUDIO_OUTPUT_FLAG_PRIMARY"))

                assertThat(audioOffloadManager.times.value.enabled).isEqualTo(0L)
            }

            assertThat(audioOffloadManager.foreground.value).isFalse()

            if (offloadExpected) {
                delay(3000)

                val times = audioOffloadManager.times.value
                assertThat(times.enabled).isGreaterThan(times.disabled)
            } else {
                // In logs
                // AudioTrack init failed 0 Config(8000, 12, 2000000) (recoverable)
            }

            assertThat(browser.isPlaying).isTrue()

            browser.stop()
        }
    }

    private fun Format.audioFormat(): AudioFormat {
        val encoding = MimeTypes.getEncoding(
            Assertions.checkNotNull(sampleMimeType),
            codecs
        )
        val channelConfig = Util.getAudioTrackChannelConfig(channelCount)
        return AudioFormat.Builder()
            .setSampleRate(sampleRate)
            .setChannelMask(channelConfig)
            .setEncoding(encoding)
            .build()
    }

    private suspend fun checkAdbDumpsys(): List<String> {
        val adbOutput = dumpSysAudioFlinger().lines()

        val offloadRegex = "offload".toRegex(RegexOption.IGNORE_CASE)
        val offloadLines = adbOutput.filter { it.contains(offloadRegex) }

        offloadLines.forEach { println(it) }

        val audioStreamOut =
            adbOutput.firstOrNull { it.startsWith("  AudioStreamOut") } ?: return emptyList()

        val flags = audioStreamOut.substringAfter('(').substringBefore(')').split('|')

        return flags
    }

    private fun offloadExpected(
        audioOutput: AudioOutput,
        modelName: String,
        format: Format
    ): Boolean {
        if (Build.VERSION.SDK_INT < 30)
            return false

        // https://en.wikipedia.org/wiki/Samsung_Galaxy_Watch_4
        if (modelName.startsWith("SM-R8")) {
            return audioOutput.name.contains("Galaxy Buds")
        }

        return true
    }

    @Suppress("BlockingMethodInNonBlockingContext")
    private suspend fun dumpSysAudioFlinger() = withContext(Dispatchers.IO) {
        device.executeShellCommand("dumpsys media.audio_flinger")
    }
}
