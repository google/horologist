/*
 * Copyright 2024 The Android Open Source Project
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

import androidx.benchmark.macro.CompilationMode
import androidx.benchmark.macro.ExperimentalMetricApi
import androidx.benchmark.macro.FrameTimingMetric
import androidx.benchmark.macro.MacrobenchmarkScope
import androidx.benchmark.macro.Metric
import androidx.benchmark.macro.PowerMetric
import androidx.benchmark.macro.StartupMode
import androidx.benchmark.macro.StartupTimingMetric
import androidx.benchmark.macro.junit4.MacrobenchmarkRule
import androidx.media3.session.MediaBrowser
import androidx.test.filters.LargeTest
import com.google.android.horologist.media.benchmark.MediaApp
import com.google.android.horologist.media.benchmark.MediaControllerHelper
import com.google.common.util.concurrent.ListenableFuture
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import org.junit.Rule
import org.junit.Test

@LargeTest
class DownloadBenchmark {
    private val includePower: Boolean = false

    val mediaApp: MediaApp = TestMedia.MediaSampleApp

    @get:Rule
    public val benchmarkRule: MacrobenchmarkRule = MacrobenchmarkRule()

    public lateinit var mediaControllerFuture: ListenableFuture<MediaBrowser>

    @Test
    public fun download(): Unit = benchmarkRule.measureRepeated(
        packageName = mediaApp.packageName,
        metrics = metrics(),
        compilationMode = CompilationMode.Partial(),
        iterations = 3,
        startupMode = StartupMode.WARM,
        setupBlock = {
            mediaControllerFuture = MediaControllerHelper.lookupController(
                mediaApp.playerComponentName,
            )

            startActivityAndWait()

            sendTestBroadcast("delete", "Electronic")

            // Wait for service
            mediaControllerFuture.get()
        },
    ) {
        val mediaController = mediaControllerFuture.get()

        val item = TestMedia.Intro

        runBlocking {
            sendTestBroadcast(
                command = "download",
                argument = "${item.mediaId}:${item.requestMetadata.mediaUri}",
            )

            delay(5000)

            withContext<Unit>(Dispatchers.Main) {
                mediaController.setMediaItems(mediaApp.testMedia)

                delay(100)

                mediaController.prepare()
                mediaController.play()
            }
        }
    }

    private fun MacrobenchmarkScope.sendTestBroadcast(
        command: String,
        argument: String,
    ) {
        println("sending")
        val result = device.executeShellCommand(
            "am broadcast " +
                "-a " +
                "com.google.android.horologist.mediasample.testing.TEST " +
                "--es " +
                "$command \"$argument\" " +
                "-n " +
                "com.google.android.horologist.mediasample/" +
                "com.google.android.horologist.mediasample.testing.BenchmarkBroadcastReceiver",
        )
        println("sent $result")
    }

    public open fun metrics(): List<Metric> = listOfNotNull(
        StartupTimingMetric(),
        FrameTimingMetric(),
        if (includePower) PowerMetric(type = PowerMetric.Type.Battery()) else null,
    )
}
