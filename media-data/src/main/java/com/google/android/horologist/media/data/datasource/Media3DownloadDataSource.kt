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

package com.google.android.horologist.media.data.datasource

import android.content.Context
import android.net.Uri
import androidx.media3.exoplayer.offline.DownloadRequest
import androidx.media3.exoplayer.offline.DownloadService
import com.google.android.horologist.media.data.ExperimentalHorologistMediaDataApi
import com.google.android.horologist.media.model.MediaDownload

/**
 * Media3 data source of [MediaDownload].
 */
@ExperimentalHorologistMediaDataApi
public class Media3DownloadDataSource(
    private val context: Context,
    private val downloadService: Class<out DownloadService>
) {

    public fun download(id: String, uri: Uri) {
        val downloadRequest = DownloadRequest.Builder(id, uri).build()

        DownloadService.sendAddDownload(
            context,
            downloadService,
            downloadRequest,
            true
        )
    }

    public fun removeDownload(id: String) {
        DownloadService.sendRemoveDownload(
            context,
            downloadService,
            id,
            false
        )
    }
}
