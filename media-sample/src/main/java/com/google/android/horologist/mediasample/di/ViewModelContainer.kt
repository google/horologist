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
import androidx.media3.session.MediaBrowser
import androidx.media3.session.SessionToken
import com.google.android.horologist.media3.flows.buildSuspend
import com.google.android.horologist.media3.player.PlayerRepositoryImpl
import com.google.android.horologist.mediasample.components.PlaybackService
import com.google.android.horologist.mediasample.ui.MediaPlayerScreenViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

/**
 * Simple DI implementation - to be replaced by hilt.
 */
class ViewModelContainer(
    private val mediaApplicationContainer: MediaApplicationContainer,
): AutoCloseable {
    internal val mediaController by lazy {
        mediaApplicationContainer.coroutineScope.async {
            val application = mediaApplicationContainer.application
            MediaBrowser.Builder(
                application,
                SessionToken(application, ComponentName(application, PlaybackService::class.java))
            ).buildSuspend()
        }
    }

    internal val playerRepository: PlayerRepositoryImpl by lazy {
        PlayerRepositoryImpl().also { playerRepository ->
            mediaApplicationContainer.coroutineScope.launch(Dispatchers.Main) {
                val player = mediaController.await()
                playerRepository.connect(
                    player = player,
                    onClose = player::release
                )
            }
        }
    }

    internal val mediaPlayerScreenViewModelFactory: MediaPlayerScreenViewModel.Factory by lazy {
        MediaPlayerScreenViewModel.Factory(playerRepository)
    }

    override fun close() {
        playerRepository.close()
    }
}
