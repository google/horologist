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

package com.google.android.horologist.mediasample.playback

import androidx.annotation.CallSuper
import androidx.media3.session.MediaBrowser
import androidx.test.annotation.UiThreadTest
import com.google.android.horologist.mediasample.BaseContainerTest
import com.google.android.horologist.mediasample.di.ViewModelModule
import org.junit.After
import org.junit.Assume
import org.junit.Before

open class BasePlaybackTest : BaseContainerTest() {
    private lateinit var viewModelContainer: ViewModelModule

    @Before
    @UiThreadTest
    @CallSuper
    override fun init() {
        super.init()

        viewModelContainer = ViewModelModule(appContainer)

        checkSupportedConfig()
    }

    @After
    @UiThreadTest
    @CallSuper
    override fun cleanup() {
        super.cleanup()
        if (this::viewModelContainer.isInitialized) {
            viewModelContainer.close()
            viewModelContainer.mediaController.getCompleted().setMediaItems(listOf())
        }
    }

    protected fun checkSupportedConfig() {
        val appConfig = this.appConfig

        if (appContainer.isEmulator) {
            Assume.assumeFalse(appConfig.offloadEnabled)
        }
    }

    suspend fun browser(): MediaBrowser {
        return viewModelContainer.mediaController.await()
    }
}
