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

package com.google.android.horologist.mediasample.components

import android.app.Application
import android.os.StrictMode
import coil.ImageLoader
import coil.ImageLoaderFactory
import com.google.android.horologist.mediasample.di.MediaApplicationContainer
import java.util.concurrent.Callable

class MediaApplication: Application(), ImageLoaderFactory {
    internal lateinit var container: MediaApplicationContainer

    lateinit var imageLoader: Callable<ImageLoader>

    override fun onCreate() {
        super.onCreate()

        setStrictMode()

        MediaApplicationContainer.install(this)
    }

    fun setStrictMode() {
        StrictMode.setThreadPolicy(
            StrictMode.ThreadPolicy.Builder()
                .detectDiskReads()
                .detectDiskWrites()
                .detectNetwork()
                .penaltyLog()
                .build()
        )
    }

    override fun newImageLoader(): ImageLoader {
        return imageLoader.call()
    }
}