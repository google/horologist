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

package com.google.android.horologist.media.ui.state.mapper

import com.google.android.horologist.media.model.Media
import com.google.common.truth.Truth.assertThat
import org.junit.Test

class MediaUiModelMapperTest {

    @Test
    fun givenMedia_thenMapsCorrectly() {
        // given
        val id = "id"
        val title = "title"
        val artist = "artist"
        val artworkUri = "artworkUri"
        val media = Media(
            id = id,
            uri = "http://www.example.com",
            title = title,
            artist = artist,
            artworkUri = artworkUri,
        )

        // when
        val result = MediaUiModelMapper.map(media)

        // then
        assertThat(result.id).isEqualTo(id)
        assertThat(result.title).isEqualTo(title)
        assertThat(result.subtitle).isEqualTo(artist)
        assertThat(result.artworkUri).isEqualTo(artworkUri)
    }
}
