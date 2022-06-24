/*
 * Copyright 2022 Google Inc. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
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
import androidx.test.annotation.UiThreadTest
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.RequiresDevice
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.UiDevice
import com.google.android.horologist.audio.AudioOutput
import com.google.android.horologist.audio.SystemAudioRepository
import com.google.wear.media3.PlaybackRules
import com.google.wear.media3.db.AdditionalDataHandler
import com.google.wear.media3.db.AppEventDao
import com.google.wear.media3.db.MediaDao
import com.google.wear.media3.library.DownloadState
import com.google.wear.media3.logs.AppEventLevel
import com.google.wear.mediatoolkit.logging.AudioOffloadManager
import com.google.wear.mediatoolkit.storage.LibraryActions
import com.google.wear.mediatoolkit.uamp.di.UampPlaybackRules
import com.google.wear.mediatoolkit.uamp.junit.RequireNetwork
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.guava.await
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import org.assertj.core.api.Assertions.assertThat
import org.junit.Assume.assumeTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.time.Instant
import javax.inject.Inject
import kotlin.time.Duration.Companion.seconds

@RunWith(AndroidJUnit4::class)
@HiltAndroidTest
@RequiresDevice
@RequireNetwork
class AudioOffloadTest : BasePlaybackTest() {
    @Inject
    lateinit var appEventDao: AppEventDao

    @Inject
    lateinit var audioSink: AudioSink

    @Inject
    lateinit var audioOffloadManager: AudioOffloadManager

    @Inject
    lateinit var mediaDao: MediaDao

    @Inject
    lateinit var libraryActions: LibraryActions

    @Inject
    lateinit var playbackRules: PlaybackRules

    lateinit var audioOutputRepository: SystemAudioRepository

    private lateinit var device: UiDevice

    val downloadFirst = true

    @Before
    @UiThreadTest
    override fun init() {
        super.init()

        assumeTrue(playbackRules.attemptOffload)
        assumeTrue(playbackRules is UampPlaybackRules)

        device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())

        audioOutputRepository = SystemAudioRepository.fromContext(context.applicationContext)
    }

    @Test
    fun testAudioOffload() {
        val start = Instant.now()

        runTest(dispatchTimeoutMs = 600000) {
            val browser = browserFuture.await()

            val mediaItem = TestMedia.songMp3.toMediaItem(AdditionalDataHandler.None)

            assertThat(audioOffloadManager.foreground.value).isFalse()

            if (downloadFirst) {
                libraryActions.download(mediaItem)

                val finalDownloadState = mediaDao.getDownloadState(mediaItem.mediaId.toInt())
                    .onEach {
                        println("Download: " + it?.downloadState)
                    }
                    .filter { it?.downloadState == DownloadState.Downloaded || it?.downloadState == DownloadState.Failed }
                    .first()

                assertThat(finalDownloadState?.downloadState).isEqualTo(DownloadState.Downloaded)
            }

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

                    assertThat(audioOffloadManager.sleepingForOffload.value)
                        .withFailMessage("sleepingForOffload")
                        .isTrue()

                    assertThat(flags.contains("AUDIO_OUTPUT_FLAG_COMPRESS_OFFLOAD"))

                    assertThat(audioOffloadManager.times.value.enabled).isGreaterThan(0)
                } else {
                    assertThat(audioOffloadManager.sleepingForOffload.value)
                        .withFailMessage("sleepingForOffload")
                        .isFalse()

                    assertThat(!flags.contains("AUDIO_OUTPUT_FLAG_COMPRESS_OFFLOAD"))
                    assertThat(flags.contains("AUDIO_OUTPUT_FLAG_PRIMARY"))

                    assertThat(audioOffloadManager.times.value.enabled).isEqualTo(0L)
                }

                assertThat(audioOffloadManager.foreground.value).isFalse()

                val events = appEventDao.getRecentAppEvents(start).first()

                val errors = events.filter { it.level == AppEventLevel.Error }

                if (offloadExpected) {
                    assertThat(errors).isEmpty()

                    delay(3000)

                    val times = audioOffloadManager.times.value
                    assertThat(times.enabled).isGreaterThan(times.disabled)
                } else {
                    // Maybe
                    // AudioTrack init failed 0 Config(8000, 12, 2000000) (recoverable)
                    if (errors.isNotEmpty()) {
                        assertThat(errors.first().event).contains("AudioTrack init failed 0 Config")
                    }
                }

                assertThat(browser.isPlaying).isTrue()

                browser.stop()
            }
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
