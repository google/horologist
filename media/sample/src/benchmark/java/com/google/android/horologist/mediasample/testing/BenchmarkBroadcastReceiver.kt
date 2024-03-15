/*
 * Copyright 2024 The Android Open Source Project
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

@file:OptIn(DelicateCoroutinesApi::class)

package com.google.android.horologist.mediasample.testing

import android.content.Context
import android.net.Uri
import android.os.Bundle
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.offline.DownloadRequest
import androidx.media3.exoplayer.offline.DownloadService
import com.google.android.horologist.mediasample.data.service.download.MediaDownloadServiceImpl
import kotlinx.coroutines.DelicateCoroutinesApi

@androidx.annotation.OptIn(UnstableApi::class)
class BenchmarkBroadcastReceiver : SuspendingBroadcastReceiver() {
    override val action: String = ACTION

    override suspend fun execute(context: Context, intentExtras: Bundle): BroadcastResult {
        if (intentExtras.containsKey("delete")) {
            return deleteAll(context, intentExtras)
        }

        if (intentExtras.containsKey("download")) {
            return downloadItem(context, intentExtras)
        }

        throw IllegalArgumentException("Unknown command")
    }

    suspend fun deleteAll(context: Context, intentExtras: Bundle): BroadcastResult {
        val delete = intentExtras.getString("delete")
        println("deletePlaylist($delete)")

        DownloadService.sendRemoveAllDownloads(context, MediaDownloadServiceImpl::class.java, false)

        return BroadcastResult(data = "deleting $delete")
    }

    suspend fun downloadItem(context: Context, intentExtras: Bundle): BroadcastResult {
        val download = intentExtras.getString("download")!!
        println("downloadPlaylist($download)")

        val (id, url) = download.split(":")

        val request = DownloadRequest.Builder(id, Uri.parse(url))
            .build()
        DownloadService.sendAddDownload(context, MediaDownloadServiceImpl::class.java, request, false)

        return BroadcastResult(data = "downloading $download")
    }

    companion object {
        val ACTION = "com.google.android.horologist.mediasample.testing.TEST"
    }
}
