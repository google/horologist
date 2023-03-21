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

@file:OptIn(ExperimentalHorologistApi::class)

package com.google.android.horologist.media.data.database.mapper

import com.google.android.horologist.annotations.ExperimentalHorologistApi
import com.google.android.horologist.media.data.database.model.MediaEntity
import com.google.android.horologist.media.model.Media
import com.google.common.truth.Truth.assertThat
import org.junit.Test

class MediaEntityMapperTest {

    @Test
    fun mapsCorrectly() {
        // given
        val id = "id"
        val uri = "uri"
        val title = "title"
        val artist = "artist"
        val artworkUri = "artworkUri"
        val media = Media(
            id = id,
            uri = uri,
            title = title,
            artist = artist,
            artworkUri = artworkUri
        )

        // when
        val result = MediaEntityMapper.map(media)

        // then
        assertThat(result).isEqualTo(
            MediaEntity(
                mediaId = id,
                mediaUrl = uri,
                artworkUrl = artworkUri,
                title = title,
                artist = artist
            )
        )
    }

    @Test
    fun givenNullValues_thenMapsCorrectly() {
        // given
        val id = "id"
        val uri = "uri"
        val title = "title"
        val artist = "artist"
        val artworkUri = null
        val media = Media(
            id = id,
            uri = uri,
            title = title,
            artist = artist,
            artworkUri = artworkUri
        )

        // when
        val result = MediaEntityMapper.map(media)

        // then
        assertThat(result).isEqualTo(
            MediaEntity(
                mediaId = id,
                mediaUrl = uri,
                artworkUrl = "",
                title = title,
                artist = artist
            )
        )
    }
}
