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

import android.content.ComponentName
import android.os.Looper
import androidx.media3.common.MediaItem
import androidx.media3.session.MediaBrowser
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import androidx.test.platform.app.InstrumentationRegistry
import com.google.common.util.concurrent.ListenableFuture
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

public object MediaControllerHelper {
    public fun lookupController(component: ComponentName): ListenableFuture<MediaBrowser> {
        val context = InstrumentationRegistry.getInstrumentation().context
        return MediaBrowser.Builder(
            context,
            SessionToken(context, component)
        )
            .setApplicationLooper(Looper.getMainLooper())
            .buildAsync()
    }

    public suspend fun MediaController.startPlaying(mediaItem: MediaItem) {
        withContext(Dispatchers.Main) {
            setMediaItem(mediaItem)

            delay(100)

            prepare()
            play()
        }
    }

    public suspend fun MediaController.stopPlaying() {
        withContext(Dispatchers.Main) {
            stop()
        }
    }
}
