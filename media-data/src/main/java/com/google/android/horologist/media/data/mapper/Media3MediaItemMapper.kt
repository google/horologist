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
import androidx.media3.common.MediaItem.RequestMetadata
import androidx.media3.common.MediaMetadata
import com.google.android.horologist.media.model.MediaItem
import androidx.media3.common.MediaItem as Media3MediaItem

/**
 * Maps a [MediaItem] into a [Media3.MediaItem][Media3MediaItem].
 */
public object Media3MediaItemMapper {

    public fun map(mediaItem: MediaItem): Media3MediaItem {
        val parsedUri = Uri.parse("https://dl.espressif.com/dl/audio/ff-16b-2c-16000hz.mp3")
        val artworkUri = mediaItem.artworkUri?.let(Uri::parse)

        return Media3MediaItem.Builder()
            .setMediaId(mediaItem.id)
            .setUri(parsedUri)
            .setRequestMetadata(
                RequestMetadata.Builder()
                    .setMediaUri(parsedUri)
                    .build()
            )
            .setMediaMetadata(
                MediaMetadata.Builder()
                    .setDisplayTitle(mediaItem.title)
                    .setArtist(mediaItem.artist)
                    .setArtworkUri(artworkUri)
                    .build()
            )
            .build()
    }

    public fun map(mediaItems: List<MediaItem>): List<Media3MediaItem> = mediaItems.map(
        Media3MediaItemMapper::map
    )
}
