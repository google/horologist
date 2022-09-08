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

import android.content.Intent
import androidx.benchmark.macro.ExperimentalBaselineProfilesApi
import androidx.benchmark.macro.junit4.BaselineProfileRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.UiDevice
import org.junit.Before
import org.junit.Ignore
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

// This test generates a baseline profile rules file that can be added to the app to configure
// the classes and methods that are pre-compiled at installation time, rather than JIT'd at runtime.
// 1) Run this test on a device
// 2) Copy the generated file to your workspace - command is output as part of the test:
// `adb pull "/sdcard/Android/media/com.example.android.wearable.composeadvanced.benchmark/"
//           "additional_test_output/BaselineProfile_profile-baseline-prof-2022-03-25-16-58-49.txt"
//           .`
// 3) Add the rules as androidMain/baseline-prof.txt
// Note that Compose libraries have profile rules already so the main benefit is to add any
// rules that are specific to classes and methods in your own app and library code.
@ExperimentalBaselineProfilesApi
@RunWith(AndroidJUnit4::class)
@Ignore("Until we disable in CI runs")
class BaselineProfile {

    @get:Rule
    val baselineRule = BaselineProfileRule()

    private lateinit var device: UiDevice

    @Before
    fun setUp() {
        val instrumentation = InstrumentationRegistry.getInstrumentation()
        device = UiDevice.getInstance(instrumentation)
    }

    @Test
    fun profile() {
        baselineRule.collectBaselineProfile(
            packageName = PACKAGE_NAME,
            profileBlock = {
                val intent = Intent()
                intent.action = ACTION
                startActivityAndWait(intent)

            }
        )
    }

    companion object {
        private const val PACKAGE_NAME = "com.google.android.horologist.mediasample"
        private const val ACTION = "com.google.android.horologist.mediasample.MAIN"
    }
}
