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

package com.google.android.horologist.media.data

import com.google.android.horologist.media.model.MediaItem
import androidx.media3.common.MediaItem as Media3MediaItem

/**
 * Maps a [Media3.MediaItem][Media3MediaItem] into a [MediaItem].
 */
public object MediaItemMapper {

    /**
     * @param media3MediaItem [Media3MediaItem] to be mapped.
     * @param defaultArtist value for [MediaItem.artist].
     */
    public fun map(
        media3MediaItem: Media3MediaItem,
        defaultArtist: String = ""
    ): MediaItem = MediaItem(
        id = media3MediaItem.mediaId,
        uri = media3MediaItem.requestMetadata.mediaUri?.toString() ?: "",
        title = media3MediaItem.mediaMetadata.displayTitle?.toString(),
        artist = media3MediaItem.mediaMetadata.artist?.toString() ?: defaultArtist,
        artworkUri = media3MediaItem.mediaMetadata.artworkUri?.toString()
    )
}
