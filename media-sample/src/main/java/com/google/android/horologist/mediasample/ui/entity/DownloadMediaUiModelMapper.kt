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

package com.google.android.horologist.mediasample.ui.entity

import com.google.android.horologist.media.model.Media
import com.google.android.horologist.media.ui.state.mapper.MediaUiModelMapper
import com.google.android.horologist.media.ui.state.model.DownloadMediaUiModel
import com.google.android.horologist.mediasample.domain.model.MediaDownload

object DownloadMediaUiModelMapper {

    fun map(
        mediaDownload: MediaDownload,
    ): DownloadMediaUiModel = when (mediaDownload.status) {
        MediaDownload.Status.Idle,
        MediaDownload.Status.InProgress -> {
            DownloadMediaUiModel.Unavailable(MediaUiModelMapper.map(mediaDownload.media))
        }
        MediaDownload.Status.Completed -> {
            DownloadMediaUiModel.Available(MediaUiModelMapper.map(mediaDownload.media))
        }
    }

    fun mapList(mediaDownloadList: List<MediaDownload>): List<DownloadMediaUiModel> =
        mediaDownloadList.map(::map)

    fun map(media: Media): DownloadMediaUiModel =
        DownloadMediaUiModel.Unavailable(MediaUiModelMapper.map(media))

    fun map(mediaList: List<Media>): List<DownloadMediaUiModel> = mediaList.map(::map)
}
