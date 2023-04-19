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

import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import com.google.android.horologist.annotations.ExperimentalHorologistApi
import com.google.android.horologist.media.model.Media

/**
 * Custom implementation to populate [MediaItem] with values from [Media.extras].
 */
@ExperimentalHorologistApi
public interface MediaItemExtrasMapper {

    public fun map(
        media: Media,
        mediaItemBuilder: MediaItem.Builder,
        mediaMetadataBuilder: MediaMetadata.Builder,
        requestMetadataBuilder: MediaItem.RequestMetadata.Builder
    )
}

@ExperimentalHorologistApi
public object MediaItemExtrasMapperNoopImpl : MediaItemExtrasMapper {
    override fun map(
        media: Media,
        mediaItemBuilder: MediaItem.Builder,
        mediaMetadataBuilder: MediaMetadata.Builder,
        requestMetadataBuilder: MediaItem.RequestMetadata.Builder
    ) {
        // do nothing
    }
}
