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

package com.google.android.horologist.mediasample.ui.mapper

import com.google.android.horologist.media.ui.state.model.DownloadMediaUiModel
import com.google.android.horologist.mediasample.domain.model.MediaDownload

object DownloadMediaUiModelMapper {

    private const val PROGRESS_FORMAT = "%.0f"
    private const val PROGRESS_WAITING = 0f

    fun map(
        mediaDownload: MediaDownload
    ): DownloadMediaUiModel = when (mediaDownload.status) {
        MediaDownload.Status.Idle -> {
            DownloadMediaUiModel.NotDownloaded(
                id = mediaDownload.media.id,
                title = mediaDownload.media.title,
                artist = mediaDownload.media.artist,
                artworkUri = mediaDownload.media.artworkUri
            )
        }

        is MediaDownload.Status.InProgress -> {
            DownloadMediaUiModel.Downloading(
                id = mediaDownload.media.id,
                title = mediaDownload.media.title,
                progress = if (mediaDownload.status.progress == PROGRESS_WAITING) {
                    DownloadMediaUiModel.Progress.Waiting
                } else {
                    DownloadMediaUiModel.Progress.InProgress(PROGRESS_FORMAT.format(mediaDownload.status.progress))
                },
                size = when (mediaDownload.size) {
                    is MediaDownload.Size.Known -> DownloadMediaUiModel.Size.Known(mediaDownload.size.sizeInBytes)
                    is MediaDownload.Size.Unknown -> DownloadMediaUiModel.Size.Unknown
                },
                artworkUri = mediaDownload.media.artworkUri
            )
        }
        MediaDownload.Status.Completed -> {
            DownloadMediaUiModel.Downloaded(
                id = mediaDownload.media.id,
                title = mediaDownload.media.title,
                artist = mediaDownload.media.artist,
                artworkUri = mediaDownload.media.artworkUri
            )
        }
    }
}
