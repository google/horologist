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

package com.google.android.horologist.media.data.mapper

import com.google.android.horologist.media.data.ExperimentalHorologistMediaDataApi
import com.google.android.horologist.media.data.database.model.MediaDownloadEntity
import com.google.android.horologist.media.data.database.model.MediaDownloadEntityStatus
import com.google.android.horologist.media.model.MediaDownload

/**
 * Functions to map models from other layers and / or packages into a [MediaDownload.Status].
 */
@ExperimentalHorologistMediaDataApi
public object MediaDownloadStatusMapper {

    /**
     * Maps from a [MediaDownloadEntity].
     */
    public fun map(mediaDownloadEntity: MediaDownloadEntity): MediaDownload.Status = when (mediaDownloadEntity.status) {
        MediaDownloadEntityStatus.NotDownloaded -> MediaDownload.Status.Idle
        MediaDownloadEntityStatus.Downloading -> MediaDownload.Status.InProgress(mediaDownloadEntity.progress)
        MediaDownloadEntityStatus.Downloaded -> MediaDownload.Status.Completed
        MediaDownloadEntityStatus.Failed -> MediaDownload.Status.Idle
    }
}
