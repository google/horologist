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

import com.google.android.horologist.media.ui.state.model.PlaylistDownloadUiModel
import com.google.android.horologist.mediasample.domain.model.Playlist

/**
 * Maps a [Playlist] into a [PlaylistDownloadUiModel].
 */
object PlaylistDownloadUiModelMapper {

    fun map(playlist: Playlist): PlaylistDownloadUiModel =
        PlaylistDownloadUiModel.Completed(PlaylistUiModelMapper.map(playlist))
}
