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

import android.app.Application
import android.app.NotificationManager
import androidx.annotation.CallSuper
import androidx.media3.datasource.cache.Cache
import androidx.test.annotation.UiThreadTest
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.UiDevice
import com.google.android.horologist.audio.SystemAudioRepository
import com.google.android.horologist.media3.offload.AudioOffloadManager
import com.google.android.horologist.media3.rules.PlaybackRules
import com.google.android.horologist.mediasample.runner.FakeConfigModule
import com.google.android.horologist.mediasample.ui.AppConfig
import com.google.android.horologist.networks.rules.NetworkingRules
import dagger.hilt.android.testing.HiltAndroidRule
import org.junit.After
import org.junit.Before
import org.junit.Rule
import java.io.File
import java.util.UUID
import javax.inject.Inject

abstract class BaseContainerTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @Inject
    protected lateinit var audioOffloadManager: AudioOffloadManager

    @Inject
    protected lateinit var audioOutputRepository: SystemAudioRepository

    @Inject
    protected lateinit var notificationManager: NotificationManager

    @Inject
    protected lateinit var downloadCache: Cache

    @Inject
    protected lateinit var appConfig: AppConfig

    protected lateinit var device: UiDevice

    private lateinit var cacheDir: File

    internal lateinit var application: Application

    @Before
    @UiThreadTest
    @CallSuper
    open fun init() {
        application =
            InstrumentationRegistry.getInstrumentation().targetContext.applicationContext as Application

        cacheDir = File(application.cacheDir, UUID.randomUUID().toString()).also {
            it.mkdirs()
        }

        FakeConfigModule.appConfigFn = { appConfig() }

        hiltRule.inject()
        device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
    }

    open fun appConfig(): AppConfig {
        return AppConfig(
            strictMode = false,
            cacheDir = cacheDir,
            offloadEnabled = false,
            strictNetworking = NetworkingRules.Lenient,
            cacheItems = false,
            playbackRules = PlaybackRules.SpeakerAllowed,
        )
    }

    @After
    @UiThreadTest
    @CallSuper
    open fun cleanup() {
        cacheDir.deleteRecursively()
    }
}
