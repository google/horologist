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

import android.os.Build
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.viewmodel.CreationExtras
import coil.Coil
import com.google.android.horologist.media.data.PlayerRepositoryImpl
import com.google.android.horologist.media3.config.WearMedia3Factory
import com.google.android.horologist.media3.offload.AudioOffloadManager
import com.google.android.horologist.media3.rules.PlaybackRules
import com.google.android.horologist.mediasample.AppConfig
import com.google.android.horologist.mediasample.components.MediaActivity
import com.google.android.horologist.mediasample.components.MediaApplication
import com.google.android.horologist.mediasample.components.PlaybackService
import com.google.android.horologist.mediasample.system.Logging
import com.google.android.horologist.networks.data.DataRequestRepository
import com.google.android.horologist.networks.status.NetworkRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

/**
 * Simple DI implementation - to be replaced by hilt.
 */
class MediaApplicationContainer(internal val application: MediaApplication) {
    val isEmulator = Build.PRODUCT.startsWith("sdk_gwear")

    val playbackRules: PlaybackRules by lazy {
        if (isEmulator) {
            PlaybackRules.Emulator
        } else {
            PlaybackRules.Normal
        }
    }

    val appConfig by lazy {
        AppConfig()
    }

    val coroutineScope: CoroutineScope by lazy {
        CoroutineScope(SupervisorJob() + Dispatchers.Default)
    }

    val viewModelModule: ViewModelModule by lazy {
        ViewModelModule(this)
    }

    val audioContainer by lazy { AudioContainer(this) }

    val networkModule by lazy { NetworkModule(this) }

    internal val wearMedia3Factory: WearMedia3Factory by lazy {
        WearMedia3Factory(application)
    }

    val audioOffloadManager by lazy { AudioOffloadManager(logger) }

    internal val logger: Logging by lazy { Logging(application.resources) }

    internal fun serviceContainer(service: PlaybackService): PlaybackServiceContainer =
        PlaybackServiceContainer(this, service, wearMedia3Factory).also {
            service.lifecycle.addObserver(object : DefaultLifecycleObserver {
                override fun onStop(owner: LifecycleOwner) {
                    it.close()
                }
            })
        }

    internal fun activityContainer(activity: MediaActivity): MediaActivityContainer =
        MediaActivityContainer(this).also {
            activity.lifecycle.addObserver(object : DefaultLifecycleObserver {
                override fun onStop(owner: LifecycleOwner) {
                    it.close()
                }
            })
        }

    companion object {
        fun install(mediaApplication: MediaApplication) {
            mediaApplication.container = MediaApplicationContainer(mediaApplication)

            Coil.setImageLoader {
                mediaApplication.container.networkModule.imageLoader
            }
        }

        val PlayerRepositoryImplKey = object : CreationExtras.Key<PlayerRepositoryImpl> {}
        val NetworkRepositoryKey = object : CreationExtras.Key<NetworkRepository> {}
        val DataRequestRepositoryKey = object : CreationExtras.Key<DataRequestRepository> {}
        val AppConfigKey = object : CreationExtras.Key<AppConfig> {}
        val AudioOffloadManagerKey = object : CreationExtras.Key<AudioOffloadManager> {}
    }
}
