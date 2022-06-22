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
import androidx.media3.common.util.Clock
import androidx.media3.datasource.DataSource
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
import com.google.android.horologist.media3.navigation.NavDeepLinkIntentBuilder
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

    val appConfig by lazy { mediaApplicationContainer.appConfig }

    val mediaCodecSelector by lazy { wearMedia3Factory.mediaCodecSelector() }

    val audioSink by lazy { wearMedia3Factory.audioSink(attemptOffload = appConfig.offloadEnabled) }

    val audioOnlyRenderersFactory by lazy {
        wearMedia3Factory.audioOnlyRenderersFactory(
            audioSink,
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

    private val streamDataSourceFactory: DataSource.Factory by lazy {
        OkHttpDataSource.Factory(
            NetworkAwareCallFactory(
                mediaApplicationContainer.networkModule.networkAwareCallFactory,
                defaultRequestType = RequestType.UnknownRequest
            )
        )
            .setTransferListener(TransferListener(mediaApplicationContainer.logger))
    }

    val mediaSourceFactory: MediaSource.Factory by lazy {
        DefaultMediaSourceFactory(streamDataSourceFactory, extractorsFactory)
    }

    val exoPlayer = ExoPlayer.Builder(service, audioOnlyRenderersFactory)
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

            if (appConfig.offloadEnabled) {
                mediaApplicationContainer.audioOffloadManager.connect(this)
            }
        }

    val player = WearConfiguredPlayer(
        player = exoPlayer,
        audioOutputRepository = mediaApplicationContainer.audioContainer.audioOutputRepository,
        audioOutputSelector = mediaApplicationContainer.audioContainer.audioOutputSelector,
        playbackRules = mediaApplicationContainer.playbackRules,
        errorReporter = mediaApplicationContainer.logger
    )

    val librarySessionCallback by lazy {
        UampMediaLibrarySessionCallback(service.lifecycleScope, mediaApplicationContainer.logger)
    }

    val intentBuilder by lazy {
        NavDeepLinkIntentBuilder(
            mediaApplicationContainer.application,
            "${appConfig.deeplinkUriPrefix}/player?page=1",
            "${appConfig.deeplinkUriPrefix}/player?page=0"
        )
    }

    val mediaLibrarySession by lazy {
        MediaLibraryService.MediaLibrarySession.Builder(service, player, librarySessionCallback)
            .setSessionActivity(intentBuilder.buildPlayerIntent())
            .build()
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
                playbackService.container.serviceContainer(playbackService)

            serviceContainer.inject(playbackService)
        }
    }
}
