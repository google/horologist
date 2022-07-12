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
import com.google.android.horologist.mediasample.data.api.model.MusicApiModel
import com.google.common.truth.Truth.assertThat
import org.junit.Test

class PlaylistMapperTest {

    @Test
    fun map() {
        // given
        val catalog = getCatalog()

        // when
        val result = PlaylistMapper.map(catalog)

        // then
        assertThat(result).hasSize(3)
        assertThat(result[0].mediaItems).hasSize(1)
        assertThat(result[0].name).isEqualTo("genre1")
        assertThat(result[0].artworkUri).isEqualTo("image1_1")
        assertThat(result[0].mediaItems[0].id).isEqualTo("id1_1")

        assertThat(result[1].mediaItems).hasSize(2)
        assertThat(result[1].name).isEqualTo("genre2")
        assertThat(result[1].artworkUri).isEqualTo("image2_1")
        assertThat(result[1].mediaItems[0].id).isEqualTo("id2_1")
        assertThat(result[1].mediaItems[1].id).isEqualTo("id2_2")

        assertThat(result[2].mediaItems).hasSize(3)
        assertThat(result[2].name).isEqualTo("genre3")
        assertThat(result[2].artworkUri).isEqualTo("image3_1")
        assertThat(result[2].mediaItems[0].id).isEqualTo("id3_1")
        assertThat(result[2].mediaItems[1].id).isEqualTo("id3_2")
        assertThat(result[2].mediaItems[2].id).isEqualTo("id3_3")
    }

    private fun getCatalog(): CatalogApiModel {
        val list = mutableListOf<MusicApiModel>()

        for (genreIdx in 1..3) {
            for (musicIdx in 1..genreIdx) {
                val suffix = "${genreIdx}_$musicIdx"
                list.add(
                    MusicApiModel(
                        album = "album$suffix",
                        artist = "artist$suffix",
                        duration = musicIdx,
                        genre = "genre$genreIdx",
                        id = "id$suffix",
                        image = "image$suffix",
                        site = "site$suffix",
                        source = "source$suffix",
                        title = "title$suffix",
                        totalTrackCount = genreIdx,
                        trackNumber = musicIdx,
                    )
                )
            }
        }

        return CatalogApiModel(list)
    }
}
