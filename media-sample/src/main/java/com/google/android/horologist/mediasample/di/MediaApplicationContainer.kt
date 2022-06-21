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

package com.google.android.horologist.mediasample.di

import android.app.Activity
import android.app.Service
import android.os.Build
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.google.android.horologist.media3.config.WearMedia3Factory
import com.google.android.horologist.media3.rules.PlaybackRules
import com.google.android.horologist.mediasample.components.MediaActivity
import com.google.android.horologist.mediasample.components.MediaApplication
import com.google.android.horologist.mediasample.components.PlaybackService
import com.google.android.horologist.networks.data.DataRequestRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

/**
 * Simple DI implementation - to be replaced by hilt.
 */
class MediaApplicationContainer(internal val application: MediaApplication) {
    val playbackRules: PlaybackRules by lazy {
        val isEmulator = Build.PRODUCT.startsWith("sdk_gwear")

        if (isEmulator) {
            PlaybackRules.Emulator
        } else {
            PlaybackRules.Normal
        }
    }

    val appConfig by lazy { AppConfig(offloadEnabled = true) }

    val coroutineScope: CoroutineScope by lazy {
        CoroutineScope(SupervisorJob() + Dispatchers.Default)
    }

    val viewModelContainer: ViewModelContainer by lazy {
        ViewModelContainer(this)
    }

    val audioContainer by lazy { AudioContainer(this) }

    val networkContainer by lazy { NetworkContainer(this) }

    internal val wearMedia3Factory: WearMedia3Factory by lazy {
        WearMedia3Factory(application)
    }

    internal val logger: Logging by lazy { Logging(application.resources) }

    private fun serviceContainer(service: PlaybackService): PlaybackServiceContainer =
        PlaybackServiceContainer(this, service, wearMedia3Factory).also {
            service.lifecycle.addObserver(object : DefaultLifecycleObserver {
                override fun onStop(owner: LifecycleOwner) {
                    it.close()
                }
            })
        }

    private fun activityContainer(activity: MediaActivity): MediaActivityContainer =
        MediaActivityContainer(this, activity).also {
            activity.lifecycle.addObserver(object : DefaultLifecycleObserver {
                override fun onStop(owner: LifecycleOwner) {
                    it.close()
                }
            })
        }

    companion object {
        fun inject(playbackService: PlaybackService) {
            val serviceContainer =
                playbackService.container.serviceContainer(playbackService)

            serviceContainer.inject(playbackService)
        }

        fun inject(mediaActivity: MediaActivity) {
            val activityContainer =
                mediaActivity.container.activityContainer(mediaActivity)

            activityContainer.inject(mediaActivity)
        }

        private val Activity.container: MediaApplicationContainer
            get() = (application as MediaApplication).container

        private val Service.container: MediaApplicationContainer
            get() = (application as MediaApplication).container

        fun install(mediaApplication: MediaApplication) {
            mediaApplication.container = MediaApplicationContainer(mediaApplication)
        }
    }
}
