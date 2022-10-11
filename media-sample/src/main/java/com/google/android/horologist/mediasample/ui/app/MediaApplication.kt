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

package com.google.android.horologist.mediasample.ui.app

import android.app.Application
import android.os.StrictMode
import coil.ImageLoader
import coil.ImageLoaderFactory
import com.google.android.horologist.media.sync.initializers.Sync
import com.google.android.horologist.mediasample.R
import com.google.android.horologist.mediasample.ui.AppConfig
import com.google.firebase.FirebaseApp
import com.google.firebase.ktx.Firebase
import com.google.firebase.perf.ktx.performance
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class MediaApplication : Application(), ImageLoaderFactory {
    @Inject
    lateinit var imageLoader: ImageLoader

    @Inject
    lateinit var appConfig: AppConfig

    override fun onCreate() {
        super.onCreate()

        setStrictMode()

        // Initialize Sync; the system responsible for keeping data in the app up to date.
        Sync.initialize(context = this)

        FirebaseApp.initializeApp(this)

        val remoteConfig: FirebaseRemoteConfig = Firebase.remoteConfig.apply {
            setDefaultsAsync(R.xml.remote_config_defaults)
            setConfigSettingsAsync(remoteConfigSettings {
                minimumFetchIntervalInSeconds = 3600
            })
        }

        Firebase.performance.isPerformanceCollectionEnabled = true
//            remoteConfig.getBoolean("perf_enabled")
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

    override fun newImageLoader(): ImageLoader = imageLoader
}
