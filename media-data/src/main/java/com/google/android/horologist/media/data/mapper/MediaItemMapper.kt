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
public class MediaItemMapper(
    private val mediaItemExtrasMapper: MediaItemExtrasMapper
) {

    public fun map(mediaItem: Media): MediaItem {
        val parsedUri = Uri.parse(mediaItem.uri)
        val artworkUri = mediaItem.artworkUri?.let(Uri::parse)

        val mediaItemBuilder = MediaItem.Builder()
        val mediaMetadataBuilder = MediaMetadata.Builder()
        val requestMetadataBuilder = RequestMetadata.Builder()

        mediaItemBuilder
            .setMediaId(mediaItem.id)
            .setUri(parsedUri)

        mediaMetadataBuilder
            .setTitle(mediaItem.title)
            .setDisplayTitle(mediaItem.title)
            .setArtist(mediaItem.artist)
            .setArtworkUri(artworkUri)

        requestMetadataBuilder
            .setMediaUri(parsedUri)

        mediaItemExtrasMapper.map(
            mediaItem,
            mediaItemBuilder,
            mediaMetadataBuilder,
            requestMetadataBuilder
        )

        mediaItemBuilder
            .setMediaMetadata(mediaMetadataBuilder.build())
            .setRequestMetadata(requestMetadataBuilder.build())

        return mediaItemBuilder.build()
    }
}
