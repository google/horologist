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

import android.content.Intent
import androidx.benchmark.macro.CompilationMode
import androidx.benchmark.macro.ExperimentalMetricApi
import androidx.benchmark.macro.FrameTimingMetric
import androidx.benchmark.macro.Metric
import androidx.benchmark.macro.PowerMetric
import androidx.benchmark.macro.StartupMode
import androidx.benchmark.macro.StartupTimingMetric
import androidx.benchmark.macro.junit4.MacrobenchmarkRule
import androidx.test.filters.LargeTest
import com.google.android.horologist.media.benchmark.metrics.CompositionMetric
import org.junit.Rule
import org.junit.Test

@LargeTest
class MarqueeSampleBenchmark {
    private val includePower: Boolean = false

    @get:Rule
    public val benchmarkRule: MacrobenchmarkRule = MacrobenchmarkRule()

    @Test
    public fun marqueeFull() {
        runBenchmark("Full")
    }

    @Test
    public fun marqueeNavOnly() {
        runBenchmark("NavOnly")
    }

    @Test
    public fun marqueePagerOnly() {
        runBenchmark("PagerOnly")
    }

    @Test
    public fun marqueeScreenOnly() {
        runBenchmark("ScreenOnly")
    }

    private fun runBenchmark(mode: String) {
        benchmarkRule.measureRepeated(
            packageName = "com.google.android.horologist.mediasample",
            metrics = metrics(),
            compilationMode = CompilationMode.Partial(),
            iterations = 1,
            startupMode = StartupMode.WARM,
        ) {
            startActivityAndWait(
                Intent("com.google.android.horologist.mediasample.BENCHMARK").apply {
                    putExtra("Mode", mode)
                }
            )

            Thread.sleep(30000)
        }
    }

    public open fun metrics(): List<Metric> = listOfNotNull(
        StartupTimingMetric(),
        FrameTimingMetric(),
        if (includePower) PowerMetric(type = PowerMetric.Type.Battery()) else null
    )
}
