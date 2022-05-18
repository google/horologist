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

import com.google.android.horologist.media.model.MediaItem

/**
 * Simple data source for a list of fake [MediaItem]s
 */
class MediaDataSource {

    // the stage is set, the green flag, drops!
    private val songs = listOf(
        "Highway Star" to "Deep Purple",
        "Paranoid" to "Black Sabbath",
        "Peter Gunn" to "Henry Mancini",
        "Bad to the Bone" to "George Thorogood and the Destroyers",
        "Born to Be Wild" to "Steppenwolf",
    )

    fun fetchData(): List<MediaItem> {
        return mutableListOf<MediaItem>().also {
            for ((title, artist) in songs) {
                it.add(MediaItem(title = title, artist = artist))
            }
        }
    }
}
