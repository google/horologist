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

import androidx.media3.session.MediaBrowser
import androidx.test.filters.LargeTest
import com.google.common.util.concurrent.ListenableFuture
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import org.junit.Test

@LargeTest
class QueryApp {

    private lateinit var mediaControllerFuture: ListenableFuture<MediaBrowser>

    @Test
    fun startup() {
        mediaControllerFuture = MediaControllerHelper.lookupController(
            TestMedia.MediaSampleApp.playerComponentName
        )

        // Wait for service
        val mediaController = mediaControllerFuture.get()

        runBlocking(Dispatchers.Main) {
            val mediaItem = mediaController.currentMediaItem

            if (mediaItem == null) {
                println("mediaItem: null")
            } else {
                println("uri: ${mediaItem.localConfiguration?.uri}")
                println("id: ${mediaItem.mediaId}")
                println("title: ${mediaItem.mediaMetadata.title}")
                println("title: ${mediaItem.mediaMetadata.artist}")
            }
        }
    }
}
