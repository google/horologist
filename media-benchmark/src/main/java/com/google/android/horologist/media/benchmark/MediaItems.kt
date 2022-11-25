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

package com.google.android.horologist.media.benchmark

import android.net.Uri
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata

public object MediaItems {
    public fun buildMediaItem(id: String, mediaUri: String, artworkUri: String?, title: String, artist: String): MediaItem {
        val parsedUri = Uri.parse(mediaUri)

        val mediaItemBuilder = MediaItem.Builder()
        val mediaMetadataBuilder = MediaMetadata.Builder()
        val requestMetadataBuilder = MediaItem.RequestMetadata.Builder()

        mediaItemBuilder
            .setMediaId(id)
            .setUri(parsedUri)

        mediaMetadataBuilder
            .setTitle(title)
            .setDisplayTitle(title)
            .setArtist(artist)
            .setArtworkUri(artworkUri?.let<String, Uri?>(Uri::parse))

        requestMetadataBuilder
            .setMediaUri(parsedUri)

        mediaItemBuilder
            .setMediaMetadata(mediaMetadataBuilder.build())
            .setRequestMetadata(requestMetadataBuilder.build())

        return mediaItemBuilder.build()
    }
}
