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

package com.google.android.horologist.media.data.database.mapper

import com.google.android.horologist.annotations.ExperimentalHorologistApi
import com.google.android.horologist.media.data.database.model.MediaEntity
import com.google.android.horologist.media.model.Media

/**
 * Functions to map models from other layers and / or packages into a [MediaEntity].
 */
@ExperimentalHorologistApi
public object MediaEntityMapper {

    /**
     * Maps from a [Media].
     */
    public fun map(media: Media): MediaEntity = MediaEntity(
        mediaId = media.id,
        mediaUrl = media.uri,
        artworkUrl = media.artworkUri ?: "",
        title = media.title,
        artist = media.artist,
    )
}
