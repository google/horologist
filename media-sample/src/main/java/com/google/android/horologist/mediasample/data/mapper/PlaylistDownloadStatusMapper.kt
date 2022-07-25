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

package com.google.android.horologist.mediasample.data.mapper

import com.google.android.horologist.mediasample.data.database.model.PlaylistDownloadState
import com.google.android.horologist.mediasample.domain.model.PlaylistDownload

object PlaylistDownloadStatusMapper {

    fun map(playlistDownloadState: PlaylistDownloadState): PlaylistDownload.Status = when (playlistDownloadState) {
        PlaylistDownloadState.NotDownloaded -> PlaylistDownload.Status.Idle
        PlaylistDownloadState.Downloading -> PlaylistDownload.Status.InProgress
        PlaylistDownloadState.Downloaded -> PlaylistDownload.Status.Completed
        PlaylistDownloadState.Failed -> PlaylistDownload.Status.Idle
    }
}
