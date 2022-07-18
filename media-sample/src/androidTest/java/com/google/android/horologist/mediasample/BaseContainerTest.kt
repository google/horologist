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
import androidx.media3.exoplayer.audio.AudioSink
import androidx.test.annotation.UiThreadTest
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.UiDevice
import com.google.android.horologist.audio.SystemAudioRepository
import com.google.android.horologist.media3.offload.AudioOffloadManager
import com.google.android.horologist.media3.rules.PlaybackRules
import com.google.android.horologist.networks.rules.NetworkingRules
import dagger.hilt.android.testing.HiltAndroidRule
import org.junit.After
import org.junit.Before
import org.junit.Rule
import javax.inject.Inject

abstract class BaseContainerTest {
    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    protected lateinit var device: UiDevice

    // Default to most permissable settings for tests
    protected open val appConfig = AppConfig(
        offloadEnabled = false,
        strictNetworking = NetworkingRules.Lenient,
        cacheItems = false,
        playbackRules = PlaybackRules.SpeakerAllowed
    )

    internal lateinit var application: Application

    @Inject
    protected lateinit var audioOffloadManager: AudioOffloadManager

    @Inject
    protected lateinit var audioSink: AudioSink

    @Inject
    protected lateinit var audioOutputRepository: SystemAudioRepository

    @Inject
    protected lateinit var notificationManager: NotificationManager

    @Inject
    protected lateinit var downloadCache: Cache

    @Before
    @UiThreadTest
    @CallSuper
    open fun init() {
        hiltRule.inject()

        application =
            InstrumentationRegistry.getInstrumentation().targetContext.applicationContext as Application
        device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())

        clearCache()
    }

    fun clearCache() {
        downloadCache.keys.forEach {
            downloadCache.removeResource(it)
        }
    }

    @After
    @UiThreadTest
    @CallSuper
    open fun cleanup() {
    }
}
