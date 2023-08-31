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

import com.google.android.horologist.mediasample.data.api.model.MusicApiModel
import com.google.common.truth.Truth.assertThat
import org.junit.Test

class MediaMapperTest {

    @Test
    fun givenMusicApiModel_thenMapsCorrectly() {
        // given
        val id = "id"
        val title = "title"
        val uri = "uri"
        val artist = "artist"
        val artworkUri = "artworkUri"

        val musicApiModel = MusicApiModel(
            album = "album",
            artist = artist,
            duration = 1,
            genre = "genre",
            id = id,
            image = artworkUri,
            site = "site",
            source = uri,
            title = title,
            totalTrackCount = 1,
            trackNumber = 1,
        )

        // when
        val result = MediaMapper.map(musicApiModel)

        // then
        assertThat(result.id).isEqualTo(id)
        assertThat(result.uri).isEqualTo(uri)
        assertThat(result.title).isEqualTo(title)
        assertThat(result.artist).isEqualTo(artist)
        assertThat(result.artworkUri).isEqualTo(artworkUri)
        assertThat(result.extras).isEmpty()
    }
}
