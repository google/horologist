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
public sealed class DownloadMediaUiModel(
    public open val id: String,
    public open val title: String? = null,
//    public open val artist: String? = null,
    public open val artworkUri: String? = null
) {
    public data class Downloaded(
        override val id: String,
        override val title: String? = null,
        val artist: String? = null,
        override val artworkUri: String? = null
    ) : DownloadMediaUiModel(
        id = id,
        title = title,
        artworkUri = artworkUri
    )

    public data class Downloading(
        override val id: String,
        override val title: String? = null,
        val progress: Progress,
        val size: Size,
        override val artworkUri: String? = null
    ) : DownloadMediaUiModel(
        id = id,
        title = title,
        artworkUri = artworkUri
    )

    public data class NotDownloaded(
        override val id: String,
        override val title: String? = null,
        val artist: String? = null,
        override val artworkUri: String? = null
    ) : DownloadMediaUiModel(
        id = id,
        title = title,
        artworkUri = artworkUri
    )

    public sealed class Progress {
        public object Waiting : Progress()

        public data class InProgress(val progress: Float) : Progress()
    }

    public sealed class Size {
        public object Unknown : Size()

        public data class Known(val sizeInBytes: Long) : Size()
    }
}
