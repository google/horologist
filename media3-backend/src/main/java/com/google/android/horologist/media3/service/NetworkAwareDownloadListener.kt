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

import androidx.media3.exoplayer.offline.Download
import androidx.media3.exoplayer.offline.DownloadManager
import androidx.media3.exoplayer.scheduler.Requirements
import com.google.android.horologist.media3.ExperimentalHorologistMedia3BackendApi
import com.google.android.horologist.media3.logging.ErrorReporter
import com.google.android.horologist.media3.logging.ErrorReporter.Category.Downloads
import com.google.android.horologist.networks.ExperimentalHorologistNetworksApi
import com.google.android.horologist.networks.data.RequestType.MediaRequest
import com.google.android.horologist.networks.data.RequestType.MediaRequest.MediaRequestType
import com.google.android.horologist.networks.rules.NetworkingRulesEngine
import com.google.android.horologist.networks.status.HighBandwidthRequester
import com.google.android.horologist.networks.status.HighBandwidthRequesterImpl
import java.io.Closeable

/**
 * Simple implementation of DownloadListener for downloading with
 * the required network.  Also includes event logging.
 */
@ExperimentalHorologistMedia3BackendApi
@ExperimentalHorologistNetworksApi
public class NetworkAwareDownloadListener(
    private val appEventLogger: ErrorReporter,
    private val highBandwidthRequester: HighBandwidthRequester,
    private val networkingRulesEngine: NetworkingRulesEngine,
) : DownloadManager.Listener {
    private var networkRequest: Closeable? = null

    override fun onInitialized(downloadManager: DownloadManager) {
        appEventLogger.logMessage("init", category = Downloads)

        requestNetwork(downloadManager)
    }

    override fun onDownloadsPausedChanged(
        downloadManager: DownloadManager,
        downloadsPaused: Boolean
    ) {
        appEventLogger.logMessage("paused $downloadsPaused", category = Downloads)
    }

    override fun onDownloadChanged(
        downloadManager: DownloadManager,
        download: Download,
        finalException: Exception?
    ) {
        val percent = (download.percentDownloaded).toInt().coerceAtLeast(0)
        appEventLogger.logMessage(
            "download ${download.request.uri.lastPathSegment} $percent% ${finalException?.message.orEmpty()}",
            category = Downloads
        )

        requestNetwork(downloadManager)
    }

    override fun onDownloadRemoved(downloadManager: DownloadManager, download: Download) {
        appEventLogger.logMessage("removed ${download.name}", category = Downloads)
    }

    override fun onIdle(downloadManager: DownloadManager) {
        freeNetwork()

        appEventLogger.logMessage("idle", category = Downloads)
    }

    override fun onRequirementsStateChanged(
        downloadManager: DownloadManager,
        requirements: Requirements,
        notMetRequirements: Int
    ) {
        appEventLogger.logMessage("missingReqs $notMetRequirements", category = Downloads)
    }

    override fun onWaitingForRequirementsChanged(
        downloadManager: DownloadManager,
        waitingForRequirements: Boolean
    ) {
        if (waitingForRequirements) {
            freeNetwork()
        } else {
            requestNetwork(downloadManager)
        }

        appEventLogger.logMessage(
            "waitingForRequirements $waitingForRequirements",
            category = Downloads
        )
    }

    private fun requestNetwork(downloadManager: DownloadManager) {
        if (networkRequest != null) {
            if (downloadManager.currentDownloads.isNotEmpty() && !downloadManager.isWaitingForRequirements) {
                val types = networkingRulesEngine.supportedTypes(MediaRequest(MediaRequestType.Download))

                networkRequest = highBandwidthRequester.requestHighBandwidth(types)
            }
        }
    }

    private val Download.name: String
        get() = this.request.uri.lastPathSegment ?: "unknown"

    private fun freeNetwork() {
        networkRequest?.close()
        networkRequest = null
    }
}
