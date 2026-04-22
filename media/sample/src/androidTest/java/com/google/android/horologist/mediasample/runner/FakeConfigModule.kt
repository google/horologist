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

package com.google.android.horologist.mediasample.runner

import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.os.StrictMode
import com.google.android.horologist.audio.SystemAudioRepository
import com.google.android.horologist.media3.audio.AudioOutputSelector
import com.google.android.horologist.media3.audio.BluetoothSettingsOutputSelector
import com.google.android.horologist.mediasample.di.CacheDir
import com.google.android.horologist.mediasample.di.ConfigModule
import com.google.android.horologist.mediasample.di.IsEmulator
import com.google.android.horologist.mediasample.di.SuppressSpeakerPlayback
import com.google.android.horologist.mediasample.ui.AppConfig
import com.google.android.horologist.mediasample.ui.util.resetAfter
import dagger.Module
import dagger.Provides
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import java.io.File
import javax.inject.Singleton

@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [ConfigModule::class],
)
object FakeConfigModule {
    var appConfigFn: () -> AppConfig = { AppConfig() }

    @Singleton
    @Provides
    @IsEmulator
    fun isEmulator() = Build.PRODUCT.startsWith("sdk_gwear")

    @Singleton
    @Provides
    @SuppressSpeakerPlayback
    fun suppressSpeakerPlayback() = false

    @Singleton
    @Provides
    fun appConfig(): AppConfig = appConfigFn()

    @Singleton
    @Provides
    @CacheDir
    fun cacheDir(
        @ApplicationContext application: Context,
        appConfig: AppConfig,
    ): File =
        StrictMode.allowThreadDiskWrites().resetAfter {
            appConfig.cacheDir ?: application.cacheDir
        }

    @Singleton
    @Provides
    fun audioOutputSelector(
        systemAudioRepository: SystemAudioRepository,
    ): AudioOutputSelector =
        BluetoothSettingsOutputSelector(systemAudioRepository)

    @Singleton
    @Provides
    fun systemAudioRepository(
        @ApplicationContext application: Context,
    ): SystemAudioRepository =
        SystemAudioRepository.fromContext(application)

    @Singleton
    @Provides
    fun notificationManager(
        @ApplicationContext application: Context,
    ): NotificationManager =
        application.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
}
