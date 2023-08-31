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

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.horologist.auth.data.common.repository.AuthUserRepository
import com.google.android.horologist.media.repository.PlayerRepository
import com.google.android.horologist.media.repository.PlaylistRepository
import com.google.android.horologist.media.ui.snackbar.SnackbarManager
import com.google.android.horologist.media.ui.snackbar.UiMessage
import com.google.android.horologist.mediasample.R
import com.google.android.horologist.mediasample.domain.SettingsRepository
import com.google.android.horologist.mediasample.domain.proto.copy
import com.google.android.horologist.mediasample.ui.AppConfig
import com.google.android.horologist.mediasample.ui.util.ResourceProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.reduce
import kotlinx.coroutines.flow.stateIn
import java.io.IOException
import javax.inject.Inject
import kotlin.time.DurationUnit
import kotlin.time.toDuration

@HiltViewModel
class MediaPlayerAppViewModel
    @Inject
    constructor(
        appConfig: AppConfig,
        private val settingsRepository: SettingsRepository,
        private val playerRepository: PlayerRepository,
        private val playlistRepository: PlaylistRepository,
        private val snackbarManager: SnackbarManager,
        private val resourceProvider: ResourceProvider,
        private val authUserRepository: AuthUserRepository,
    ) : ViewModel() {

        val deepLinkPrefix: String = appConfig.deeplinkUriPrefix

        val appState = settingsRepository.settingsFlow.map {
            UampAppState(
                streamingMode = it.streamingMode,
                guestMode = it.guestMode,
            )
        }.stateIn(viewModelScope, started = SharingStarted.WhileSubscribed(5_000), initialValue = UampAppState())

        @OptIn(FlowPreview::class)
        private suspend fun loadItems() {
            playlistRepository.getAll()
                .catch { throwable ->
                    when (throwable) {
                        is IOException -> {
                            snackbarManager.showMessage(
                                UiMessage(
                                    message = resourceProvider.getString(R.string.sample_network_error),
                                    error = true,
                                ),
                            )
                        }
                        else -> throw throwable
                    }
                }
                .flatMapConcat { it.asFlow() }
                .map { it.mediaList }
                .reduce { accumulator, value -> accumulator + value }
                .also { list ->
                    playerRepository.setMediaList(list)
                }
        }

        suspend fun startupSetup(navigateToLibrary: () -> Unit) {
            waitForConnection()

            val currentMediaItem = playerRepository.currentMedia.value
            val settings = settingsRepository.settingsFlow.first()

            // If it's currently not playing and user opted in to load items at startup,
            // then we start playing using the last played media item.
            if (currentMediaItem == null && settings.loadItemsAtStartup) {
                playItems(settings.currentMediaItemId, settings.currentMediaListId, settings.currentPosition)
            } else if (currentMediaItem == null) {
                val loadAtStartup =
                    settingsRepository.settingsFlow.first().loadItemsAtStartup

                if (loadAtStartup) {
                    loadItems()
                } else {
                    navigateToLibrary()
                }
            }
        }

        suspend fun playItems(mediaId: String?, collectionId: String, position: Long) {
            try {
                playlistRepository.get(collectionId)?.let { playlist ->
                    val index = playlist.mediaList
                        .indexOfFirst { it.id == mediaId }
                        .coerceAtLeast(0)

                    waitForConnection()

                    settingsRepository.edit { it.copy { currentMediaListId = collectionId } }
                    mediaId?.let {
                            id ->
                        settingsRepository.edit { it.copy { currentMediaItemId = id } }
                    }
                    settingsRepository.edit { it.copy { currentPosition = position } }

                    playerRepository.setMediaList(
                        playlist.mediaList,
                        index,
                        position.toDuration(
                            DurationUnit.MILLISECONDS,
                        ),
                    )
                }
            } catch (e: IOException) {
                snackbarManager.showMessage(
                    UiMessage(
                        message = resourceProvider.getString(R.string.sample_network_error),
                        error = true,
                    ),
                )
            }
        }

        private suspend fun waitForConnection() {
            // setMediaItems is a noop before this point
            playerRepository.connected.filter { it }.first()
        }

        suspend fun shouldShowLoginPrompt(): Boolean {
            return !isGuestMode() && !isLoggedIn()
        }

        suspend fun isGuestMode(): Boolean {
            return appState.filter { it.guestMode != null }.first().guestMode == true
        }

        suspend fun isLoggedIn(): Boolean {
            return authUserRepository.getAuthenticated() != null
        }
    }

data class UampAppState(
    val streamingMode: Boolean? = null,
    val guestMode: Boolean? = null,
)
