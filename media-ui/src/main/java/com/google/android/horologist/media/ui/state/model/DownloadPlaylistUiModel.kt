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

package com.google.android.horologist.media.ui.state.model

import com.google.android.horologist.media.ui.ExperimentalHorologistMediaUiApi

@ExperimentalHorologistMediaUiApi
public sealed class DownloadPlaylistUiModel(
    public open val title: String,
    public open val artworkUri: String? = null,
) {
    public data class Completed(
        override val title: String,
        override val artworkUri: String?,
    ) : DownloadPlaylistUiModel(title = title, artworkUri = artworkUri)

    public data class InProgress(
        override val title: String,
        override val artworkUri: String?,
        val percentage: Int
    ) : DownloadPlaylistUiModel(title = title, artworkUri = artworkUri)
}
