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
import com.google.android.horologist.media.repository.PlayerRepository
import com.google.android.horologist.media.ui.snackbar.SnackbarManager
import com.google.android.horologist.media.ui.snackbar.UiMessage
import com.google.android.horologist.mediasample.AppConfig
import com.google.android.horologist.mediasample.R
import com.google.android.horologist.mediasample.data.api.UampService
import com.google.android.horologist.mediasample.domain.SettingsRepository
import com.google.android.horologist.mediasample.domain.model.Settings
import com.google.android.horologist.mediasample.util.ResourceProvider
import com.google.android.horologist.networks.data.DataRequestRepository
import com.google.android.horologist.networks.data.DataUsageReport
import com.google.android.horologist.networks.data.Networks
import com.google.android.horologist.networks.status.NetworkRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import java.io.IOException
import javax.inject.Inject
import kotlin.time.Duration.Companion.seconds

@HiltViewModel
class MediaPlayerAppViewModel @Inject constructor(
    networkRepository: NetworkRepository,
    dataRequestRepository: DataRequestRepository,
    appConfig: AppConfig,
    settingsRepository: SettingsRepository,
    private val settings: SettingsRepository,
    private val playerRepository: PlayerRepository,
    private val uampService: UampService,
    private val snackbarManager: SnackbarManager,
    private val resourceProvider: ResourceProvider
) : ViewModel() {
    val networkStatus: StateFlow<Networks> = networkRepository.networkStatus

    val networkUsage: StateFlow<DataUsageReport?> = dataRequestRepository.currentPeriodUsage()
        .stateIn(
            viewModelScope,
            started = SharingStarted.WhileSubscribed(5.seconds.inWholeMilliseconds),
            initialValue = null
        )

    val settingsState: StateFlow<Settings?> = settingsRepository.settingsFlow.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        null
    )

    val deepLinkPrefix: String = appConfig.deeplinkUriPrefix

    suspend fun loadItems() {
        if (playerRepository.currentMedia.value == null) {
            try {
                val mediaItems = uampService.catalog().music.map {
                    it.toMedia()
                }

                playerRepository.setMediaList(mediaItems)
                playerRepository.prepare()
            } catch (ioe: IOException) {
                snackbarManager.showMessage(
                    UiMessage(
                        message = resourceProvider.getString(R.string.horologist_sample_network_error),
                        error = true
                    )
                )
            }
        }
    }

    suspend fun startupSetup(navigateToLibrary: () -> Unit) {
        waitForConnection()

        val currentMediaItem = playerRepository.currentMedia.value

        if (currentMediaItem == null) {
            val loadAtStartup =
                settings.settingsFlow.first().loadItemsAtStartup

            if (loadAtStartup) {
                loadItems()
            } else {
                navigateToLibrary()
            }
        }
    }

    suspend fun playItems(mediaId: String?, collectionId: String) {
        try {
            val mediaItems = uampService.catalog().music.map { it.toMedia() }.filter {
                it.artist == collectionId
            }

            val index = mediaItems.indexOfFirst { it.id == mediaId }.coerceAtLeast(0)

            waitForConnection()

            playerRepository.setMediaList(mediaItems)
            playerRepository.prepare()
            playerRepository.play(mediaIndex = index)
        } catch (ioe: IOException) {
            snackbarManager.showMessage(
                UiMessage(
                    message = resourceProvider.getString(R.string.horologist_sample_network_error),
                    error = true
                )
            )
        }
    }

    private suspend fun waitForConnection() {
        // setMediaItems is a noop before this point
        playerRepository.connected.filter { it }.first()
    }
}
