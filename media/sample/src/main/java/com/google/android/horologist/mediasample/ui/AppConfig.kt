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

package com.google.android.horologist.mediasample.ui

import android.annotation.SuppressLint
import android.os.Build
import androidx.media3.exoplayer.audio.DefaultAudioSink
import com.google.android.horologist.mediasample.BuildConfig
import com.google.android.horologist.mediasample.di.config.UampNetworkingRules
import com.google.android.horologist.networks.rules.NetworkingRules
import java.io.File

@SuppressLint("UnsafeOptInUsageError")
data class AppConfig(
    val offloadEnabled: Boolean = Build.VERSION.SDK_INT >= 30,
    val strictNetworking: NetworkingRules? = UampNetworkingRules,
    val deeplinkUriPrefix: String = "uamp${if (BuildConfig.DEBUG) "-debug" else ""}://uamp",
    val cacheItems: Boolean = true,
    val cacheWriteBack: Boolean = true,
    val offloadMode: Int = DefaultAudioSink.OFFLOAD_MODE_ENABLED_GAPLESS_NOT_REQUIRED,
    val strictMode: Boolean = false,
    val cacheDir: File? = null,
)
