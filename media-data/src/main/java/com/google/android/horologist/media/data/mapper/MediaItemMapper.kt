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

import android.net.Uri
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaItem.RequestMetadata
import androidx.media3.common.MediaMetadata
import com.google.android.horologist.media.data.ExperimentalHorologistMediaDataApi
import com.google.android.horologist.media.model.Media

/**
 * Maps a [Media] into a [MediaItem].
 */
@ExperimentalHorologistMediaDataApi
public object MediaItemMapper {

    public fun map(media: Media): MediaItem {
        val parsedUri = Uri.parse(media.uri)
        val artworkUri = media.artworkUri?.let(Uri::parse)

        return MediaItem.Builder()
            .setMediaId(media.id)
            .setUri(parsedUri)
            .setRequestMetadata(
                RequestMetadata.Builder()
                    .setMediaUri(parsedUri)
                    .build()
            )
            .setMediaMetadata(
                MediaMetadata.Builder()
                    .setDisplayTitle(media.title)
                    .setArtist(media.artist)
                    .setArtworkUri(artworkUri)
                    .build()
            )
            .build()
    }
}
