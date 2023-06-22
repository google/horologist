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

import android.Manifest
import androidx.benchmark.macro.MacrobenchmarkScope
import androidx.benchmark.macro.junit4.BaselineProfileRule
import androidx.media3.session.MediaBrowser
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.GrantPermissionRule
import androidx.test.uiautomator.UiDevice
import com.google.android.horologist.media.benchmark.MediaControllerHelper.startPlaying
import com.google.android.horologist.media.benchmark.MediaControllerHelper.stopPlaying
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.time.Duration.Companion.seconds

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
@RunWith(AndroidJUnit4::class)
public abstract class BaseMediaBaselineProfile {
    @get:Rule
    public val grantPermissionRule: GrantPermissionRule = GrantPermissionRule.grant(Manifest.permission.POST_NOTIFICATIONS)

    @get:Rule
    public val baselineRule: BaselineProfileRule = BaselineProfileRule()

    private lateinit var device: UiDevice

    public abstract val mediaApp: MediaApp

    public open fun MacrobenchmarkScope.onStartup() {
        startActivityAndWait()
    }

    public open suspend fun checkPlayingState(mediaController: MediaBrowser) {
        withContext(Dispatchers.Main) {
            if (!mediaController.isPlaying) {
                throw IllegalStateException("Not playing after 10 seconds")
            }
        }
    }

    @Before
    public fun setUp() {
        val instrumentation = InstrumentationRegistry.getInstrumentation()
        device = UiDevice.getInstance(instrumentation)
    }

    @Test
    public fun profile() {
        baselineRule.collect(
            packageName = mediaApp.packageName,
            profileBlock = {
                onStartup()

                val mediaController = MediaControllerHelper.lookupController(
                    mediaApp.playerComponentName
                ).get()

                runBlocking(Dispatchers.Main) {
                    mediaController.startPlaying(mediaApp.testMedia)

                    delay(15.seconds)

                    checkPlayingState(mediaController)

                    mediaController.seekToNextMediaItem()
                    delay(2.seconds)

                    mediaController.seekToPreviousMediaItem()
                    delay(2.seconds)

                    mediaController.stopPlaying()
                }
            }
        )
    }
}
