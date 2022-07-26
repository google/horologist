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
import com.google.android.horologist.mediasample.domain.model.PlaylistDownload

object DownloadMediaUiModelMapper {

    fun map(
        media: Media,
        status: PlaylistDownload.Status
    ): DownloadMediaUiModel = when (status) {
        PlaylistDownload.Status.Idle,
        PlaylistDownload.Status.InProgress -> {
            DownloadMediaUiModel.Unavailable(MediaUiModelMapper.map(media))
        }
        PlaylistDownload.Status.Completed -> {
            DownloadMediaUiModel.Available(MediaUiModelMapper.map(media))
        }
    }

    fun map(list: List<Pair<Media, PlaylistDownload.Status>>): List<DownloadMediaUiModel> =
        list.map { map(it.first, it.second) }
}
