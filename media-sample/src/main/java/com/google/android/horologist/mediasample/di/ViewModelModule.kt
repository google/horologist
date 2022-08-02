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
import android.content.Context
import androidx.media3.session.MediaBrowser
import androidx.media3.session.SessionToken
import com.google.android.horologist.media.data.mapper.MediaExtrasMapper
import com.google.android.horologist.media.data.mapper.MediaExtrasMapperNoopImpl
import com.google.android.horologist.media.data.mapper.MediaItemExtrasMapper
import com.google.android.horologist.media.data.mapper.MediaItemExtrasMapperNoopImpl
import com.google.android.horologist.media.data.mapper.MediaItemMapper
import com.google.android.horologist.media.data.mapper.MediaMapper
import com.google.android.horologist.media.data.repository.PlayerRepositoryImpl
import com.google.android.horologist.media.repository.PlayerRepository
import com.google.android.horologist.media3.flows.buildSuspend
import com.google.android.horologist.mediasample.components.PlaybackService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.ActivityRetainedLifecycle
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ActivityRetainedScoped
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

@Module
@InstallIn(ActivityRetainedComponent::class)
object ViewModelModule {

    @ActivityRetainedScoped
    @Provides
    fun providesCoroutineScope(
        activityRetainedLifecycle: ActivityRetainedLifecycle
    ): CoroutineScope {
        return CoroutineScope(SupervisorJob() + Dispatchers.Default).also {
            activityRetainedLifecycle.addOnClearedListener {
                it.cancel()
            }
        }
    }

    @Provides
    @ActivityRetainedScoped
    fun mediaController(
        @ApplicationContext application: Context,
        activityRetainedLifecycle: ActivityRetainedLifecycle,
        coroutineScope: CoroutineScope
    ): Deferred<MediaBrowser> =
        coroutineScope.async {
            MediaBrowser.Builder(
                application,
                SessionToken(application, ComponentName(application, PlaybackService::class.java))
            ).buildSuspend()
        }.also {
            activityRetainedLifecycle.addOnClearedListener {
                it.cancel()
                if (it.isCompleted && !it.isCancelled) {
                    it.getCompleted().release()
                }
            }
        }

    @Provides
    @ActivityRetainedScoped
    fun playerRepositoryImpl(
        mediaMapper: MediaMapper,
        mediaItemMapper: MediaItemMapper,
        activityRetainedLifecycle: ActivityRetainedLifecycle,
        coroutineScope: CoroutineScope,
        mediaController: Deferred<MediaBrowser>
    ): PlayerRepositoryImpl =
        PlayerRepositoryImpl(
            mediaMapper = mediaMapper,
            mediaItemMapper = mediaItemMapper,
        ).also { playerRepository ->
            activityRetainedLifecycle.addOnClearedListener {
                playerRepository.close()
            }

            coroutineScope.launch(Dispatchers.Main) {
                val player = mediaController.await()
                playerRepository.connect(
                    player = player,
                    onClose = player::release
                )
            }
        }

    @Provides
    @ActivityRetainedScoped
    fun playerRepository(
        playerRepositoryImpl: PlayerRepositoryImpl
    ): PlayerRepository = playerRepositoryImpl

    @Provides
    fun mediaExtrasMapper(): MediaExtrasMapper = MediaExtrasMapperNoopImpl

    @Provides
    fun mediaItemExtrasMapper(): MediaItemExtrasMapper = MediaItemExtrasMapperNoopImpl

    @Provides
    @ActivityRetainedScoped
    fun mediaMapper(mediaExtrasMapper: MediaExtrasMapper): MediaMapper =
        MediaMapper(mediaExtrasMapper)

    @Provides
    @ActivityRetainedScoped
    fun mediaItemMapper(mediaItemExtrasMapper: MediaItemExtrasMapper): MediaItemMapper =
        MediaItemMapper(mediaItemExtrasMapper)
}
