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

package com.google.android.horologist.media.benchmark

import androidx.benchmark.macro.CompilationMode
import androidx.benchmark.macro.Metric
import androidx.benchmark.macro.StartupMode
import androidx.benchmark.macro.StartupTimingMetric
import androidx.benchmark.macro.junit4.MacrobenchmarkRule
import androidx.test.filters.LargeTest
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized

@LargeTest
@RunWith(Parameterized::class)
public abstract class BaseStartupBenchmark {
    @get:Rule
    public val benchmarkRule: MacrobenchmarkRule = MacrobenchmarkRule()

    public open val compilationMode: CompilationMode = CompilationMode.Partial()

    public abstract val mediaApp: MediaApp

    @Test
    public fun startup(): Unit = benchmarkRule.measureRepeated(
        packageName = mediaApp.packageName,
        metrics = metrics(),
        compilationMode = compilationMode,
        iterations = 5,
        startupMode = StartupMode.COLD,
    ) {
        startActivityAndWait()
        // sleep to allow time for report fully drawn
        Thread.sleep(5000)
    }

    public open fun metrics(): List<Metric> = listOf(StartupTimingMetric())
}
