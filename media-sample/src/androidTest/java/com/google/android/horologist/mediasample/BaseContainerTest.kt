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

import androidx.annotation.CallSuper
import androidx.media3.exoplayer.audio.AudioSink
import androidx.test.annotation.UiThreadTest
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.UiDevice
import com.google.android.horologist.audio.SystemAudioRepository
import com.google.android.horologist.media3.offload.AudioOffloadManager
import com.google.android.horologist.mediasample.components.MediaApplication
import com.google.android.horologist.mediasample.di.MediaApplicationContainer
import org.junit.After
import org.junit.Before

open class BaseContainerTest {
    protected lateinit var device: UiDevice
    protected lateinit var appContainer: MediaApplicationContainer
    protected lateinit var application: MediaApplication

    protected open val appConfig = AppConfig()

    protected val audioOffloadManager: AudioOffloadManager
        get() = appContainer.audioOffloadManager

    protected val audioSink: AudioSink
        get() = appContainer.audioSink

    protected val audioOutputRepository: SystemAudioRepository
        get() = appContainer.audioContainer.systemAudioRepository

    @Before
    @UiThreadTest
    @CallSuper
    open fun init() {
        application =
            InstrumentationRegistry.getInstrumentation().targetContext.applicationContext as MediaApplication

        application.appConfig = appConfig

        appContainer = application.container

        device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())

        clearCache()
    }

    fun clearCache() {
        val cache = appContainer.downloadCache
        cache.keys.forEach {
            cache.removeResource(it)
        }
    }

    @After
    @UiThreadTest
    @CallSuper
    open fun cleanup() {
        appContainer.close()
    }
}
