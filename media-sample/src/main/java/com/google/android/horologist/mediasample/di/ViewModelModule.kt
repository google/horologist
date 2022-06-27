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

@file:OptIn(ExperimentalCoroutinesApi::class)

package com.google.android.horologist.mediasample.di

import android.content.ComponentName
import androidx.lifecycle.viewmodel.MutableCreationExtras
import androidx.media3.session.MediaBrowser
import androidx.media3.session.SessionToken
import com.google.android.horologist.media.data.PlayerRepositoryImpl
import com.google.android.horologist.media3.flows.buildSuspend
import com.google.android.horologist.mediasample.components.PlaybackService
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

/**
 * Simple DI implementation - to be replaced by hilt.
 */
class ViewModelModule(
    internal val mediaApplicationContainer: MediaApplicationContainer,
) : AutoCloseable {
    internal val mediaController: Deferred<MediaBrowser> by lazy {
        mediaApplicationContainer.coroutineScope.async {
            val application = mediaApplicationContainer.application
            MediaBrowser.Builder(
                application,
                SessionToken(application, ComponentName(application, PlaybackService::class.java))
            ).buildSuspend()
        }
    }

    internal var _playerRepository: PlayerRepositoryImpl? = null

    internal val playerRepository: PlayerRepositoryImpl
        @Synchronized get() {
            return if (_playerRepository != null) {
                _playerRepository!!
            } else {
                PlayerRepositoryImpl().also { playerRepository ->
                    mediaApplicationContainer.coroutineScope.launch(Dispatchers.Main) {
                        val player = mediaController.await()
                        playerRepository.connect(
                            player = player,
                            onClose = player::release
                        )
                    }
                }.also {
                    _playerRepository = it
                }
            }
        }

    fun addCreationExtras(creationExtras: MutableCreationExtras) {
        creationExtras[MediaApplicationContainer.PlayerRepositoryImplKey] =
            playerRepository
        creationExtras[MediaApplicationContainer.UampServiceKey] =
            mediaApplicationContainer.networkModule.uampService
        creationExtras[MediaApplicationContainer.NetworkRepositoryKey] =
            mediaApplicationContainer.networkModule.networkRepository
        creationExtras[MediaApplicationContainer.AppConfigKey] =
            mediaApplicationContainer.appConfig
        creationExtras[MediaApplicationContainer.DataRequestRepositoryKey] =
            mediaApplicationContainer.networkModule.dataRequestRepository
        creationExtras[MediaApplicationContainer.AudioOffloadManagerKey] =
            mediaApplicationContainer.audioOffloadManager
        creationExtras[MediaApplicationContainer.AudioOutputSelectorKey] =
            mediaApplicationContainer.audioContainer.audioOutputSelector
        creationExtras[MediaApplicationContainer.SystemAudioRepositoryKey] =
            mediaApplicationContainer.audioContainer.systemAudioRepository
        creationExtras[MediaApplicationContainer.VibratorKey] =
            mediaApplicationContainer.vibrator
    }

    override fun close() {
        _playerRepository?.close()

        if (mediaController.isActive) {
            mediaController.cancel()
        }
        try {
            mediaController.getCompleted().release()
        } catch (ise: IllegalStateException) {
            // nothing
        }
    }
}
