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

import android.content.ComponentName
import android.content.Context
import android.os.Vibrator
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStoreFile
import androidx.media3.database.DatabaseProvider
import androidx.media3.database.StandaloneDatabaseProvider
import androidx.media3.datasource.cache.Cache
import androidx.media3.datasource.cache.NoOpCacheEvictor
import androidx.media3.datasource.cache.SimpleCache
import androidx.media3.exoplayer.ExoPlayer.AudioOffloadListener
import androidx.media3.exoplayer.audio.DefaultAudioSink
import androidx.wear.watchface.complications.datasource.ComplicationDataSourceUpdateRequester
import com.google.android.horologist.media.ui.snackbar.SnackbarManager
import com.google.android.horologist.media3.config.WearMedia3Factory
import com.google.android.horologist.media3.logging.ErrorReporter
import com.google.android.horologist.media3.navigation.IntentBuilder
import com.google.android.horologist.media3.navigation.NavDeepLinkIntentBuilder
import com.google.android.horologist.media3.offload.AudioOffloadManager
import com.google.android.horologist.media3.rules.PlaybackRules
import com.google.android.horologist.mediasample.AppConfig
import com.google.android.horologist.mediasample.complication.DataUpdates
import com.google.android.horologist.mediasample.complication.MediaStatusComplicationService
import com.google.android.horologist.mediasample.domain.SettingsRepository
import com.google.android.horologist.mediasample.media.AudioOffloadListenerList
import com.google.android.horologist.mediasample.system.Logging
import com.google.android.horologist.mediasample.util.ResourceProvider
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object MediaApplicationModule {

    @Singleton
    @Provides
    fun intentBuilder(
        @ApplicationContext application: Context,
        appConfig: AppConfig
    ): IntentBuilder =
        NavDeepLinkIntentBuilder(
            application,
            "${appConfig.deeplinkUriPrefix}/player?page=1",
            "${appConfig.deeplinkUriPrefix}/player?page=0"
        )

    @Singleton
    @Provides
    fun playbackRules(
        appConfig: AppConfig,
        @IsEmulator isEmulator: Boolean
    ): PlaybackRules =
        if (appConfig.playbackRules != null) {
            appConfig.playbackRules
        } else if (isEmulator) {
            PlaybackRules.SpeakerAllowed
        } else {
            PlaybackRules.Normal
        }

    @Singleton
    @Provides
    fun prefsDataStore(
        @ApplicationContext application: Context,
        @ForApplicationScope coroutineScope: CoroutineScope
    ): DataStore<Preferences> =
        PreferenceDataStoreFactory.create(
            corruptionHandler = null,
            migrations = listOf(),
            scope = coroutineScope
        ) {
            application.preferencesDataStoreFile("prefs")
        }

    @Singleton
    @Provides
    @ForApplicationScope
    fun coroutineScope(): CoroutineScope =
        CoroutineScope(SupervisorJob() + Dispatchers.Default)

    @Singleton
    @Provides
    fun audioSink(
        appConfig: AppConfig,
        wearMedia3Factory: WearMedia3Factory,
        audioOffloadListener: AudioOffloadListener
    ): DefaultAudioSink =
        wearMedia3Factory.audioSink(
            attemptOffload = appConfig.offloadEnabled,
            offloadMode = appConfig.offloadMode,
            audioOffloadListener = audioOffloadListener
        )

    @Singleton
    @Provides
    fun audioOffloadListener(
        listeners: AudioOffloadListenerList
    ): AudioOffloadListener = listeners

    @Singleton
    @Provides
    fun audioOffloadListenerList(): AudioOffloadListenerList = AudioOffloadListenerList()

    @Singleton
    @Provides
    fun wearMedia3Factory(
        @ApplicationContext application: Context,
    ): WearMedia3Factory =
        WearMedia3Factory(application)

    @Singleton
    @Provides
    fun audioOffloadManager(
        logger: ErrorReporter,
        settingsRepository: SettingsRepository,
        @ForApplicationScope coroutineScope: CoroutineScope,
        appConfig: AppConfig,
        audioOffloadListenerList: AudioOffloadListenerList
    ): AudioOffloadManager {
        val audioOffloadStrategyFlow =
            settingsRepository.settingsFlow.map { it.offloadMode.strategy }
        return AudioOffloadManager(
            logger,
            audioOffloadStrategyFlow
        ).also { audioOffloadManager ->
            audioOffloadListenerList.addListener(audioOffloadManager.audioOffloadListener)

            if (appConfig.offloadEnabled) {
                coroutineScope.launch {
                    settingsRepository.settingsFlow.map { it.debugOffload }
                        .collectLatest { debug ->
                            if (debug) {
                                audioOffloadManager.printDebugLogsLoop()
                            }
                        }
                }
            }
        }
    }

    @Singleton
    @Provides
    fun logger(
        @ApplicationContext application: Context,
    ): Logging = Logging(res = application.resources)

    @Singleton
    @Provides
    fun errorReporter(
        logging: Logging,
    ): ErrorReporter = logging

    @Singleton
    @Provides
    fun vibrator(
        @ApplicationContext application: Context,
    ): Vibrator =
        application.getSystemService(Vibrator::class.java)

    @Singleton
    @Provides
    fun cacheDatabaseProvider(
        @ApplicationContext application: Context,
    ): DatabaseProvider = StandaloneDatabaseProvider(application)

    @Singleton
    @Provides
    fun media3Cache(
        @CacheDir cacheDir: File,
        cacheDatabaseProvider: DatabaseProvider
    ): Cache =
        SimpleCache(
            cacheDir.resolve("media3cache"),
            NoOpCacheEvictor(),
            cacheDatabaseProvider
        )

    @Singleton
    @Provides
    fun snackbarManager() =
        SnackbarManager()

    @Singleton
    @Provides
    fun ResourceProvider(
        @ApplicationContext application: Context
    ): ResourceProvider = ResourceProvider(application.resources)

    @Singleton
    @Provides
    fun dataUpdates(
        @ApplicationContext application: Context,
    ): DataUpdates {
        val updater = ComplicationDataSourceUpdateRequester.create(
            application,
            ComponentName(
                application, MediaStatusComplicationService::class.java
            )
        )
        return DataUpdates(updater)
    }
}
