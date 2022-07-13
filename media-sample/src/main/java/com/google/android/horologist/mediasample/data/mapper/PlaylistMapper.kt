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

package com.google.android.horologist.mediasample.data.mapper

import com.google.android.horologist.mediasample.data.api.model.CatalogApiModel
import com.google.android.horologist.mediasample.domain.model.Playlist

/**
 * Maps a [CatalogApiModel] into a [List] of [Playlist].
 */
object PlaylistMapper {

    fun map(catalog: CatalogApiModel): List<Playlist> =
        catalog.music
            .groupBy { it.genre }
            .map { entry ->
                Playlist(
                    id = sanitize(sanitize(entry.key)),
                    name = entry.key,
                    mediaItems = MediaItemMapper.map(entry.value),
                    artworkUri = entry.value.firstOrNull()?.image
                )
            }

    private fun sanitize(it: String): String {
        return it.replace("[^A-Za-z]".toRegex(), "")
    }
}
