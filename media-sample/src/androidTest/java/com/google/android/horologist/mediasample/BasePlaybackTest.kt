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

import android.content.ComponentName
import android.content.Context
import androidx.annotation.CallSuper
import androidx.media3.session.MediaBrowser
import androidx.media3.session.SessionToken
import androidx.test.annotation.UiThreadTest
import androidx.test.platform.app.InstrumentationRegistry
import com.google.android.horologist.mediasample.components.PlaybackService
import com.google.common.util.concurrent.ListenableFuture
import org.junit.After
import org.junit.Before

open class BasePlaybackTest {
    private lateinit var context: Context
    lateinit var browserFuture: ListenableFuture<MediaBrowser>

    @Before
    @UiThreadTest
    @CallSuper
    fun init() {
        this.context = InstrumentationRegistry.getInstrumentation().targetContext

//        cache.keys.forEach {
//            cache.removeResource(it)
//        }

        browserFuture = MediaBrowser.Builder(
            context,
            SessionToken(
                context,
                ComponentName(context, PlaybackService::class.java)
            )
        ).buildAsync()
    }

    @After
    @UiThreadTest
    @CallSuper
    fun cleanup() {
        if (this::browserFuture.isInitialized) {
            MediaBrowser.releaseFuture(browserFuture)
        }
    }
}
