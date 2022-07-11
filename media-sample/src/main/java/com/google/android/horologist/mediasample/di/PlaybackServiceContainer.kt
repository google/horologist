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

import android.app.Service
import androidx.lifecycle.lifecycleScope
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.Clock
import androidx.media3.datasource.DataSource
import androidx.media3.datasource.cache.CacheDataSource
import androidx.media3.datasource.okhttp.OkHttpDataSource
import androidx.media3.exoplayer.DefaultLoadControl
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.analytics.DefaultAnalyticsCollector
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import androidx.media3.exoplayer.source.MediaSource
import androidx.media3.extractor.DefaultExtractorsFactory
import androidx.media3.extractor.ExtractorsFactory
import androidx.media3.session.MediaLibraryService
import com.google.android.horologist.media3.WearConfiguredPlayer
import com.google.android.horologist.media3.config.WearMedia3Factory
import com.google.android.horologist.media3.logging.AnalyticsEventLogger
import com.google.android.horologist.media3.logging.TransferListener
import com.google.android.horologist.mediasample.AppConfig
import com.google.android.horologist.mediasample.components.MediaApplication
import com.google.android.horologist.mediasample.components.PlaybackService
import com.google.android.horologist.mediasample.media.UampMediaLibrarySessionCallback
import com.google.android.horologist.networks.data.RequestType
import com.google.android.horologist.networks.okhttp.NetworkAwareCallFactory

/**
 * Simple DI implementation - to be replaced by hilt.
 */
class PlaybackServiceContainer(
    private val mediaApplicationContainer: MediaApplicationContainer,
    private val service: PlaybackService,
    private val wearMedia3Factory: WearMedia3Factory
) : AutoCloseable {
    val loadControl = DefaultLoadControl.Builder()
        .setBackBuffer(
            /* backBufferDurationMs = */ 30_000,
            /* retainBackBufferFromKeyframe = */ false
        )
        .build()

    val appConfig: AppConfig
        get() = mediaApplicationContainer.appConfig

    val mediaCodecSelector by lazy { wearMedia3Factory.mediaCodecSelector() }

    val audioOnlyRenderersFactory by lazy {
        wearMedia3Factory.audioOnlyRenderersFactory(
            mediaApplicationContainer.audioSink,
            mediaCodecSelector
        )
    }

    val defaultAnalyticsCollector by lazy {
        DefaultAnalyticsCollector(Clock.DEFAULT).apply {
            addListener(AnalyticsEventLogger(mediaApplicationContainer.logger))
        }
    }

    val extractorsFactory: ExtractorsFactory by lazy {
        DefaultExtractorsFactory()
    }

    val transferListener by lazy { TransferListener(mediaApplicationContainer.logger) }

    private val streamDataSourceFactory: DataSource.Factory by lazy {
        OkHttpDataSource.Factory(
            NetworkAwareCallFactory(
                mediaApplicationContainer.networkModule.networkAwareCallFactory,
                defaultRequestType = RequestType.UnknownRequest
            )
        )
            .setTransferListener(transferListener)
    }

    private val cacheDataSourceFactory: CacheDataSource.Factory by lazy {
        CacheDataSource.Factory()
            .setCache(mediaApplicationContainer.downloadCache)
            .setUpstreamDataSourceFactory(streamDataSourceFactory)
            .setEventListener(transferListener)
            .apply {
                if (!appConfig.cacheWriteBack) {
                    setCacheWriteDataSinkFactory(null)
                }
            }
    }

    val mediaSourceFactory: MediaSource.Factory by lazy {
        val dataSourceFactory =
            if (appConfig.cacheItems) {
                cacheDataSourceFactory
            } else {
                streamDataSourceFactory
            }
        DefaultMediaSourceFactory(dataSourceFactory, extractorsFactory)
    }

    val exoPlayer by lazy {
        ExoPlayer.Builder(service, audioOnlyRenderersFactory)
            .setAnalyticsCollector(defaultAnalyticsCollector)
            .setMediaSourceFactory(mediaSourceFactory)
            .setAudioAttributes(AudioAttributes.DEFAULT, true)
            .setHandleAudioBecomingNoisy(true)
            .setWakeMode(C.WAKE_MODE_NETWORK)
            .setLoadControl(loadControl)
            .setSeekForwardIncrementMs(10_000)
            .setSeekBackIncrementMs(10_000)
            .build().apply {
                addListener(defaultAnalyticsCollector)

                addListener(dataUpdatesListener)

                if (appConfig.offloadEnabled) {
                    mediaApplicationContainer.audioOffloadManager.connect(this)
                }
            }
    }

    val player by lazy {
        WearConfiguredPlayer(
            player = exoPlayer,
            audioOutputRepository = mediaApplicationContainer.audioContainer.systemAudioRepository,
            audioOutputSelector = mediaApplicationContainer.audioContainer.audioOutputSelector,
            playbackRules = mediaApplicationContainer.playbackRules,
            errorReporter = mediaApplicationContainer.logger
        )
    }

    val librarySessionCallback by lazy {
        UampMediaLibrarySessionCallback(service.lifecycleScope, mediaApplicationContainer.logger)
    }

    val mediaLibrarySession by lazy {
        MediaLibraryService.MediaLibrarySession.Builder(service, player, librarySessionCallback)
            .setSessionActivity(mediaApplicationContainer.intentBuilder.buildPlayerIntent())
            .build()
    }

    val dataUpdatesListener by lazy {
        mediaApplicationContainer.dataUpdates.listener
    }

    override fun close() {
    }

    fun inject(service: PlaybackService) {
        service.mediaLibrarySession = mediaLibrarySession
    }

    companion object {
        internal val Service.container: MediaApplicationContainer
            get() = (application as MediaApplication).container

        fun inject(playbackService: PlaybackService) {
            val serviceContainer =
                playbackService.container.playbackServiceContainer(playbackService)

            serviceContainer.inject(playbackService)
        }
    }
}
