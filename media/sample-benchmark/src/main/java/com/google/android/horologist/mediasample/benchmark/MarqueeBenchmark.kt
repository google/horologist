/*
 * Copyright 2023 The Android Open Source Project
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

@file:OptIn(ExperimentalMetricApi::class)

package com.google.android.horologist.mediasample.benchmark

import android.util.Log
import androidx.benchmark.macro.CompilationMode
import androidx.benchmark.macro.ExperimentalMetricApi
import androidx.benchmark.macro.FrameTimingMetric
import androidx.benchmark.macro.Metric
import androidx.benchmark.macro.PowerMetric
import androidx.benchmark.macro.StartupMode
import androidx.benchmark.macro.StartupTimingMetric
import androidx.benchmark.macro.junit4.MacrobenchmarkRule
import androidx.media3.common.MediaItem
import androidx.media3.session.MediaBrowser
import androidx.test.filters.LargeTest
import androidx.test.platform.app.InstrumentationRegistry
import com.google.android.horologist.media.benchmark.MediaApp
import com.google.android.horologist.media.benchmark.MediaControllerHelper
import com.google.android.horologist.media.benchmark.MediaItems.buildMediaItem
import com.google.android.horologist.media.benchmark.metrics.CompositionMetric
import com.google.common.util.concurrent.ListenableFuture
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import org.junit.Ignore
import org.junit.Rule
import org.junit.Test
import kotlin.time.Duration.Companion.seconds

@LargeTest
class MarqueeBenchmark {
    private val includePower: Boolean = false

    val mediaApp: MediaApp = TestMedia.MediaSampleApp

    @get:Rule
    public val benchmarkRule: MacrobenchmarkRule = MacrobenchmarkRule()

    public lateinit var mediaControllerFuture: ListenableFuture<MediaBrowser>

    val arguments = InstrumentationRegistry.getArguments()

    @Test
    @Ignore
    public fun marquee() {
        measurePlayerScreen(List(10) { intro })
    }

    @Test
    @Ignore
    public fun noMarquee() {
        val introShort = intro.copy(title = "Intro")
        measurePlayerScreen(List(10) { introShort })
    }

    @Test
    @Ignore
    public fun marqueeNoPlayback() {
        measurePlayerScreen(List(10) { intro }, playback = false)
    }

    @Test
    @Ignore
    public fun rotateModes() {
        measurePlayerScreen(
            listOf(
                intro.copy(artist = "Marquee-Animated-Radial-Shown"), // Default
                intro.copy(artist = "Marquee-Animated-Linear-Shown"), // Linear
                intro.copy(artist = "Marquee-Animated-Flat-Shown"), // Linear
                intro.copy(artist = "NonOffscreen-Animated-Radial-Shown"), // Non offscreen
                intro.copy(artist = "Marquee-Hidden-Radial-Hidden"), // no other buttons
            ), playback = false
        )
    }

    @Test
    public fun compareDefault() {
        measurePlayerScreen(
            List(2) {
                intro.copy(artist = "Marquee-Animated-Radial-Shown")
            },
            playback = false
        )
    }

    @Test
    public fun compareLinear() {
        measurePlayerScreen(
            List(2) {
                intro.copy(artist = "Marquee-Animated-Linear-Shown")
            },
            playback = false
        )
    }

    @Test
    public fun compareFlat() {
        measurePlayerScreen(
            List(2) {
                intro.copy(artist = "Marquee-Animated-Flat-Shown")
            },
            playback = false
        )
    }

    @Test
    public fun compareNonOffscreen() {
        measurePlayerScreen(
            List(2) {
                intro.copy(artist = "NonOffscreen-Animated-Radial-Shown")
            },
            playback = false
        )
    }

    @Test
    public fun compareNoButtons() {
        measurePlayerScreen(
            List(2) {
                intro.copy(artist = "Marquee-Hidden-Radial-Hidden")
            },
            playback = false
        )
    }

    @Test
    public fun compareOnlyMarquee() {
        measurePlayerScreen(
            List(2) {
                intro.copy(artist = "Marquee-Hidden-Hidden-Hidden")
            },
            playback = false
        )
    }

    @Test
    public fun compareOnlyMarqueeNonOffscreen() {
        measurePlayerScreen(
            List(2) {
                intro.copy(artist = "NonOffscreen-Hidden-Hidden-Hidden")
            },
            playback = false
        )
    }

    @Test
    public fun compareNoBackground() {
        measurePlayerScreen(
            List(2) {
                intro.copy(artist = "Marquee-Animated-Hidden-Shown")
            },
            playback = false
        )
    }

    private fun MediaItem.copy(artist: String? = null, title: String? = null) =
        buildUpon().setMediaMetadata(
            mediaMetadata.buildUpon()
                .apply {
                    if (artist != null) {
                        setArtist(artist)
                    }
                    if (title != null) {
                        setTitle(title)
                    }
                }
                .build()
        )
            .build()

    private fun measurePlayerScreen(items: List<MediaItem>, playback: Boolean = true) {
        benchmarkRule.measureRepeated(
            packageName = mediaApp.packageName,
            metrics = metrics(),
            compilationMode = CompilationMode.Partial(),
            iterations = 1,
            startupMode = StartupMode.WARM,
            setupBlock = {
                logMessage("Setup")
                mediaControllerFuture = MediaControllerHelper.lookupController(
                    mediaApp.playerComponentName
                )

                logMessage("waiting for controller")

                // Wait for service
                mediaControllerFuture.get()
            }
        ) {
            startActivityAndWait()

            val mediaController = mediaControllerFuture.get()

            runBlocking {
                withContext(Dispatchers.Main) {
                    logMessage("Main")
                    mediaController.setMediaItems(items)
                    mediaController.volume = 0.1f

                    if (playback) {
                        logMessage("playing")
                        mediaController.prepare()
                        mediaController.play()
                    }

                    logMessage("delaying 5")
                    delay(5.seconds)

                    logMessage("Playing ${mediaController.currentMediaItem?.mediaMetadata}")

                    logMessage("delaying 10")
                    delay(10.seconds)

                    logMessage("isPlaying ${mediaController.isPlaying}")

                    while (mediaController.hasNextMediaItem()) {
                        mediaController.seekToNextMediaItem()

                        logMessage("delaying 5")
                        delay(5.seconds)

                        logMessage("Playing ${mediaController.currentMediaItem?.mediaMetadata}")

                        logMessage("delaying 10")
                        delay(10.seconds)
                    }
                }
            }
            logMessage("after runBlocking")
        }
    }

    fun logMessage(s: String) {
        Log.w("UampBenchmark", s)
    }

    public open fun metrics(): List<Metric> = listOfNotNull(
        StartupTimingMetric(),
        FrameTimingMetric(),
        if (includePower) PowerMetric(type = PowerMetric.Type.Battery()) else null,
        if (arguments.getBoolean("androidx.benchmark.fullTracing.enable")) {
            CompositionMetric("androidx.compose.foundation.Canvas")
        } else {
            null
        }
    )

    companion object {
        val intro = buildMediaItem(
            "1",
            "https://storage.googleapis.com/uamp/The_Kyoto_Connection_-_Wake_Up/01_-_Intro_-_The_Way_Of_Waking_Up_feat_Alan_Watts.mp3",
            "https://storage.googleapis.com/uamp/The_Kyoto_Connection_-_Wake_Up/art.jpg",
            "Intro - The Way Of Waking Up (feat. Alan Watts)",
            "The Kyoto Connection"
        )
    }
}
