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

package com.google.android.horologist.mediasample.benchmark

import android.content.ComponentName
import androidx.benchmark.macro.CompilationMode
import androidx.benchmark.macro.ExperimentalMetricApi
import androidx.benchmark.macro.StartupMode
import androidx.benchmark.macro.TraceSectionMetric
import androidx.benchmark.macro.junit4.MacrobenchmarkRule
import androidx.media3.session.MediaBrowser
import androidx.test.filters.LargeTest
import com.google.android.horologist.mediasample.benchmark.MediaControllerHelper.startPlaying
import com.google.android.horologist.mediasample.benchmark.MediaControllerHelper.stopPlaying
import com.google.common.util.concurrent.ListenableFuture
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import org.junit.Rule
import org.junit.Test
import kotlin.time.Duration.Companion.seconds

@LargeTest
@OptIn(ExperimentalMetricApi::class)
class PlaybackBenchmark {

    @get:Rule
    val benchmarkRule = MacrobenchmarkRule()

    private lateinit var mediaControllerFuture: ListenableFuture<MediaBrowser>

    @Test
    fun startup() = benchmarkRule.measureRepeated(
        packageName = PACKAGE_NAME,
        metrics = listOf(
//            AudioUnderrunMetric(), // Failing with an exception
            TraceSectionMetric("Player.isPlaying"),
            TraceSectionMetric("Player.isLoading")
        ),
        compilationMode = CompilationMode.Partial(),
        iterations = 1,
        startupMode = StartupMode.COLD,
        setupBlock = {
            mediaControllerFuture = MediaControllerHelper.lookupController(
                ComponentName(
                    PACKAGE_NAME,
                    PlaybackService
                )
            )

            // Wait for service
            mediaControllerFuture.get()
        }
    ) {
        val mediaController = mediaControllerFuture.get()

        runBlocking {
            mediaController.startPlaying(TestMedia.Intro)

            delay(10.seconds)

            withContext(Dispatchers.Main) {
                if (!mediaController.isPlaying) {
                    throw IllegalStateException("Not playing after 10 seconds")
                }
            }

            delay(20.seconds)

            mediaController.stopPlaying()

            delay(1.seconds)
        }
    }

    companion object {
        const val PlaybackService =
            "com.google.android.horologist.mediasample.data.service.playback.PlaybackService"
        private const val PACKAGE_NAME = "com.google.android.horologist.mediasample"
    }
}
