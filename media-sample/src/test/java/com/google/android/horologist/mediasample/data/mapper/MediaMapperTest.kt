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

    @Test
    fun givenListOfMusicApiModel_thenMapsCorrectly() {
        // given
        val musicApiModel1 = MusicApiModel(
            album = "album1",
            artist = "artist1",
            duration = 1,
            genre = "genre1",
            id = "id1",
            image = "artworkUri1",
            site = "site1",
            source = "source1",
            title = "title1",
            totalTrackCount = 1,
            trackNumber = 1,
        )

        val musicApiModel2 = MusicApiModel(
            album = "album2",
            artist = "artist2",
            duration = 2,
            genre = "genre2",
            id = "id2",
            image = "artworkUri2",
            site = "site2",
            source = "source2",
            title = "title2",
            totalTrackCount = 2,
            trackNumber = 2,
        )

        val list = listOf(musicApiModel1, musicApiModel2)

        // when
        val result = MediaMapper.map(list)

        // then
        assertThat(result).hasSize(2)
        assertThat(result[0].id).isEqualTo(musicApiModel1.id)
        assertThat(result[0].uri).isEqualTo(musicApiModel1.source)
        assertThat(result[0].title).isEqualTo(musicApiModel1.title)
        assertThat(result[0].artist).isEqualTo(musicApiModel1.artist)
        assertThat(result[0].artworkUri).isEqualTo(musicApiModel1.image)
        assertThat(result[0].extras).isEmpty()

        assertThat(result).hasSize(2)
        assertThat(result[1].id).isEqualTo(musicApiModel2.id)
        assertThat(result[1].uri).isEqualTo(musicApiModel2.source)
        assertThat(result[1].title).isEqualTo(musicApiModel2.title)
        assertThat(result[1].artist).isEqualTo(musicApiModel2.artist)
        assertThat(result[1].artworkUri).isEqualTo(musicApiModel2.image)
        assertThat(result[1].extras).isEmpty()
    }
}
