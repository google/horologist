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

package com.google.android.horologist.media3.service

import android.annotation.SuppressLint
import androidx.media3.exoplayer.offline.Download
import androidx.media3.exoplayer.offline.DownloadManager
import androidx.media3.exoplayer.scheduler.Requirements
import com.google.android.horologist.annotations.ExperimentalHorologistApi
import com.google.android.horologist.media3.logging.ErrorReporter
import com.google.android.horologist.media3.logging.ErrorReporter.Category.Downloads
import com.google.android.horologist.networks.data.RequestType.MediaRequest
import com.google.android.horologist.networks.data.RequestType.MediaRequest.MediaRequestType
import com.google.android.horologist.networks.highbandwidth.HighBandwidthConnectionLease
import com.google.android.horologist.networks.highbandwidth.HighBandwidthNetworkMediator
import com.google.android.horologist.networks.request.HighBandwidthRequest
import com.google.android.horologist.networks.rules.NetworkingRulesEngine

/**
 * Simple implementation of [DownloadManager.Listener] for downloading with
 * a required high bandwidth network. Also includes event logging.
 */
@SuppressLint("UnsafeOptInUsageError")
@ExperimentalHorologistApi
public class NetworkAwareDownloadListener(
    private val appEventLogger: ErrorReporter,
    private val highBandwidthNetworkMediator: HighBandwidthNetworkMediator,
    private val networkingRulesEngine: NetworkingRulesEngine,
) : DownloadManager.Listener {
    private var networkRequest: HighBandwidthConnectionLease? = null

    override fun onInitialized(downloadManager: DownloadManager) {
        appEventLogger.logMessage("init", category = Downloads)

        updateNetworkState(downloadManager)
    }

    override fun onDownloadsPausedChanged(
        downloadManager: DownloadManager,
        downloadsPaused: Boolean,
    ) {
        appEventLogger.logMessage("paused $downloadsPaused", category = Downloads)
    }

    override fun onDownloadChanged(
        downloadManager: DownloadManager,
        download: Download,
        finalException: Exception?,
    ) {
        val percent = (download.percentDownloaded).toInt().coerceAtLeast(0)
        appEventLogger.logMessage(
            "download ${download.request.uri.lastPathSegment} $percent% ${finalException?.message.orEmpty()}",
            category = Downloads,
        )

        updateNetworkState(downloadManager)
    }

    override fun onDownloadRemoved(downloadManager: DownloadManager, download: Download) {
        appEventLogger.logMessage("removed ${download.name}", category = Downloads)
    }

    override fun onIdle(downloadManager: DownloadManager) {
        updateNetworkState(downloadManager)

        appEventLogger.logMessage("idle", category = Downloads)
    }

    override fun onRequirementsStateChanged(
        downloadManager: DownloadManager,
        requirements: Requirements,
        notMetRequirements: Int,
    ) {
        appEventLogger.logMessage("missingReqs $notMetRequirements", category = Downloads)
    }

    override fun onWaitingForRequirementsChanged(
        downloadManager: DownloadManager,
        waitingForRequirements: Boolean,
    ) {
        updateNetworkState(downloadManager)

        appEventLogger.logMessage(
            "waitingForRequirements $waitingForRequirements",
            category = Downloads,
        )
    }

    private fun updateNetworkState(downloadManager: DownloadManager) {
        val hasReadyDownloads =
            downloadManager.currentDownloads.isNotEmpty() && !downloadManager.isWaitingForRequirements

        if (hasReadyDownloads) {
            if (networkRequest == null) {
                val types =
                    networkingRulesEngine.supportedTypes(MediaRequest(MediaRequestType.Download))
                val request = HighBandwidthRequest.from(types)

                appEventLogger.logMessage(
                    "Requesting network for downloads $networkRequest",
                    category = Downloads,
                )

                networkRequest = highBandwidthNetworkMediator.requestHighBandwidthNetwork(request)
            }
        } else {
            if (networkRequest != null) {
                appEventLogger.logMessage("Releasing network for downloads", category = Downloads)
                networkRequest?.close()
                networkRequest = null
            }
        }
    }

    private val Download.name: String
        get() = this.request.uri.lastPathSegment ?: "unknown"
}
