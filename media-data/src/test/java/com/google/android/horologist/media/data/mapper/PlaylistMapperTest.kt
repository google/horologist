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

@file:OptIn(ExperimentalHorologistMediaDataApi::class)

package com.google.android.horologist.media.data.mapper

import com.google.android.horologist.media.data.ExperimentalHorologistMediaDataApi
import com.google.android.horologist.media.data.database.model.MediaEntity
import com.google.android.horologist.media.data.database.model.PlaylistEntity
import com.google.android.horologist.media.data.database.model.PopulatedPlaylist
import com.google.android.horologist.media.model.Media
import com.google.android.horologist.media.model.Playlist
import com.google.common.truth.Truth.assertThat
import org.junit.Before
import org.junit.Test

class PlaylistMapperTest {

    private lateinit var sut: PlaylistMapper

    @Before
    fun setUp() {
        sut = PlaylistMapper(MediaMapper(MediaExtrasMapperNoopImpl))
    }

    @Test
    fun mapsCorrectly() {
        // given
        val playlistId = "playlistId"
        val playlistName = "playlistName"
        val playlistArtworkUri = "playlistArtworkUri"
        val mediaId = "mediaId"
        val mediaUrl = "mediaUrl"
        val artworkUrl = "artworkUrl"
        val title = "title"
        val artist = "artist"

        val populatedPlaylist = PopulatedPlaylist(
            PlaylistEntity(
                playlistId = playlistId,
                name = playlistName,
                artworkUri = playlistArtworkUri
            ),
            listOf(
                MediaEntity(
                    mediaId = mediaId,
                    mediaUrl = mediaUrl,
                    artworkUrl = artworkUrl,
                    title = title,
                    artist = artist
                )
            )
        )

        // then
        val result = sut.map(populatedPlaylist)

        // then
        assertThat(result).isEqualTo(
            Playlist(
                id = playlistId,
                name = playlistName,
                artworkUri = playlistArtworkUri,
                listOf(
                    Media(
                        id = mediaId,
                        uri = mediaUrl,
                        title = title,
                        artist = artist,
                        artworkUri = artworkUrl
                    )
                )
            )
        )
    }
}
