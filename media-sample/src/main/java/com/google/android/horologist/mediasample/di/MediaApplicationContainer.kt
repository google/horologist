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
import android.os.StrictMode
import android.os.Vibrator
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.media3.database.StandaloneDatabaseProvider
import androidx.media3.datasource.cache.NoOpCacheEvictor
import androidx.media3.datasource.cache.SimpleCache
import coil.Coil
import com.google.android.horologist.audio.SystemAudioRepository
import com.google.android.horologist.media.data.PlayerRepositoryImpl
import com.google.android.horologist.media3.audio.AudioOutputSelector
import com.google.android.horologist.media3.config.WearMedia3Factory
import com.google.android.horologist.media3.offload.AudioOffloadManager
import com.google.android.horologist.media3.rules.PlaybackRules
import com.google.android.horologist.mediasample.AppConfig
import com.google.android.horologist.mediasample.catalog.UampService
import com.google.android.horologist.mediasample.components.MediaActivity
import com.google.android.horologist.mediasample.components.MediaApplication
import com.google.android.horologist.mediasample.components.PlaybackService
import com.google.android.horologist.mediasample.system.Logging
import com.google.android.horologist.networks.data.DataRequestRepository
import com.google.android.horologist.networks.status.NetworkRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import java.io.Closeable

/**
 * Simple DI implementation - to be replaced by hilt.
 */
class MediaApplicationContainer(
    internal val application: MediaApplication,
    internal val appConfig: AppConfig = AppConfig()
) : Closeable {
    val isEmulator = Build.PRODUCT.startsWith("sdk_gwear")

    val playbackRules: PlaybackRules by lazy {
        if (appConfig.playbackRules != null) {
            appConfig.playbackRules
        } else if (isEmulator) {
            PlaybackRules.SpeakerAllowed
        } else {
            PlaybackRules.Normal
        }
    }

    val coroutineScope: CoroutineScope by lazy {
        CoroutineScope(SupervisorJob() + Dispatchers.Default)
    }

    val viewModelModule: ViewModelModule by lazy {
        ViewModelModule(this)
    }

    // Must happen on Main thread
    val audioContainer = AudioContainer(this)

    val audioSink by lazy {
        wearMedia3Factory.audioSink(
            attemptOffload = appConfig.offloadEnabled,
            offloadMode = appConfig.offloadMode
        )
    }

    val networkModule by lazy { NetworkModule(this) }

    internal val wearMedia3Factory: WearMedia3Factory by lazy {
        WearMedia3Factory(application)
    }

    val audioOffloadManager by lazy { AudioOffloadManager(logger) }

    internal val logger: Logging by lazy { Logging(application.resources) }

    val vibrator: Vibrator by lazy {
        application.getSystemService(Vibrator::class.java)
    }

    private val cacheDatabaseProvider by lazy {
        StandaloneDatabaseProvider(application)
    }

    val cacheDir by lazy {
        StrictMode.allowThreadDiskWrites().resetAfter {
            application.cacheDir
        }
    }

    val downloadCache by lazy {
        val media3CacheDir = cacheDir.resolve("media3cache")
        SimpleCache(
            media3CacheDir,
            NoOpCacheEvictor(),
            cacheDatabaseProvider
        )
    }

    internal fun serviceContainer(service: PlaybackService): PlaybackServiceContainer {
        return PlaybackServiceContainer(this, service, wearMedia3Factory).also {
            service.lifecycle.addObserver(object : DefaultLifecycleObserver {
                override fun onStop(owner: LifecycleOwner) {
                    it.close()
                }
            })
        }
    }

    internal fun activityContainer(activity: MediaActivity): MediaActivityContainer {
        return MediaActivityContainer(this).also {
            activity.lifecycle.addObserver(object : DefaultLifecycleObserver {
                override fun onStop(owner: LifecycleOwner) {
                    it.close()
                }
            })
        }
    }

    // Confusingly the result of allowThreadDiskWrites is the old policy,
    // while allow* methods immediately apply the change.
    // So `this` is the policy before we overrode it.
    fun <R> StrictMode.ThreadPolicy.resetAfter(block: () -> R) = try {
        block()
    } finally {
        StrictMode.setThreadPolicy(this)
    }

    override fun close() {
        audioContainer.close()
        StrictMode.allowThreadDiskWrites().resetAfter {
            downloadCache.release()
        }
        application._container = null
    }

    fun install() {
        Coil.setImageLoader {
            networkModule.imageLoader
        }
    }

    companion object {
        val PlayerRepositoryImplKey = object : CreationExtras.Key<PlayerRepositoryImpl> {}
        val NetworkRepositoryKey = object : CreationExtras.Key<NetworkRepository> {}
        val DataRequestRepositoryKey = object : CreationExtras.Key<DataRequestRepository> {}
        val AppConfigKey = object : CreationExtras.Key<AppConfig> {}
        val AudioOffloadManagerKey = object : CreationExtras.Key<AudioOffloadManager> {}
        val AudioOutputSelectorKey = object : CreationExtras.Key<AudioOutputSelector> {}
        val UampServiceKey = object : CreationExtras.Key<UampService> {}
        val SystemAudioRepositoryKey = object : CreationExtras.Key<SystemAudioRepository> {}
        val VibratorKey = object : CreationExtras.Key<Vibrator> {}
    }
}
