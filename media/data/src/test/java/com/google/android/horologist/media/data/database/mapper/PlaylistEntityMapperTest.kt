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

import com.google.android.horologist.media.data.database.model.PlaylistEntity
import com.google.android.horologist.media.model.Playlist
import com.google.common.truth.Truth.assertThat
import org.junit.Test

class PlaylistEntityMapperTest {

    @Test
    fun mapsCorrectly() {
        // given
        val id = "id"
        val name = "name"
        val artworkUri = "artworkUri"
        val playlist = Playlist(
            id = id,
            name = name,
            artworkUri = artworkUri,
            mediaList = listOf(),
        )

        // when
        val result = PlaylistEntityMapper.map(playlist)

        // then
        assertThat(result).isEqualTo(
            PlaylistEntity(
                playlistId = id,
                name = name,
                artworkUri = artworkUri,
            ),
        )
    }
}
