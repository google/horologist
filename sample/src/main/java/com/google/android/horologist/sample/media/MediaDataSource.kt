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

package com.google.android.horologist.sample.media

import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata

/**
 * Simple data source for a list of fake [MediaItem]s
 */
class MediaDataSource {

    private val songs = listOf(
        Pair("Highway Star", "Deep Purple"),
        Pair("Paranoid", "Black Sabbath"),
        Pair("Peter Gunn", "Henry Mancini"),
        Pair("Bad to the Bone", "George Thorogood and the Destroyers"),
        Pair("Born to Be Wild", "Steppenwolf"),
    )

    fun fetchData(): List<MediaItem> {
        return mutableListOf<MediaItem>().also {
            for (song in songs) {
                it.add(
                    MediaItem.Builder()
                        .setMediaMetadata(
                            MediaMetadata.Builder()
                                .setDisplayTitle(song.first)
                                .setArtist(song.second)
                                .build()
                        )
                        .build()
                )
            }
        }
    }
}
