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

import android.net.Uri
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import com.google.android.horologist.media.data.ExperimentalHorologistMediaDataApi
import com.google.android.horologist.media.data.database.model.MediaEntity
import com.google.common.truth.Truth.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE)
class MediaMapperTest {

    private lateinit var sut: MediaMapper

    @Before
    fun setUp() {
        sut = MediaMapper(MediaExtrasMapperNoopImpl)
    }

    @Test
    fun `given MediaItem then maps correctly`() {
        // given
        val id = "id"
        val uri = "uri"
        val title = "title"
        val artist = "artist"
        val albumArtist = "albumArtist"
        val artworkUri = "artworkUri"

        val mediaItem = MediaItem.Builder()
            .setMediaId(id)
            .setUri(Uri.parse(uri))
            .setMediaMetadata(
                MediaMetadata.Builder()
                    .setTitle(title)
                    .setArtist(artist)
                    .setAlbumArtist(albumArtist)
                    .setArtworkUri(Uri.parse(artworkUri))
                    .build()
            )
            .build()

        // when
        val result = sut.map(mediaItem, mediaItem.mediaMetadata)

        // then
        assertThat(result.id).isEqualTo(id)
        assertThat(result.uri).isEqualTo(uri)
        assertThat(result.title).isEqualTo(title)
        assertThat(result.artist).isEqualTo(artist)
        assertThat(result.artworkUri).isEqualTo(artworkUri)
    }

    @Test
    fun `given MediaItem with null values then maps correctly`() {
        // given
        val id = "id"
        val uri = "uri"
        val artist = "artist"

        val mediaItem = MediaItem.Builder()
            .setMediaId(id)
            .setUri(Uri.parse(uri))
            .setMediaMetadata(
                MediaMetadata.Builder()
                    .setArtist(artist)
                    .build()
            )
            .build()

        // when
        val result = sut.map(mediaItem, mediaItem.mediaMetadata)

        // then
        assertThat(result.id).isEqualTo(id)
        assertThat(result.uri).isEqualTo(uri)
        assertThat(result.title).isEqualTo("")
        assertThat(result.artist).isEqualTo(artist)
        assertThat(result.artworkUri).isNull()
    }

    @Test
    fun `given MediaItem without displayTitle and artist falls back to albumArtist and title`() {
        // given
        val id = "id"
        val uri = "uri"
        val title = "title"
        val albumArtist = "albumArtist"

        val mediaItem = MediaItem.Builder()
            .setMediaId(id)
            .setUri(Uri.parse(uri))
            .setMediaMetadata(
                MediaMetadata.Builder()
                    .setTitle(title)
                    .setAlbumArtist(albumArtist)
                    .build()
            )
            .build()

        // when
        val result = sut.map(mediaItem, mediaItem.mediaMetadata)

        // then
        assertThat(result.id).isEqualTo(id)
        assertThat(result.uri).isEqualTo(uri)
        assertThat(result.title).isEqualTo(title)
        assertThat(result.artist).isEqualTo(albumArtist)
        assertThat(result.artworkUri).isNull()
    }

    @Test
    fun `given no title and artist, uses empty string`() {
        // given
        val mediaItem = MediaItem.Builder().build()

        // when
        val result = sut.map(mediaItem, mediaItem.mediaMetadata)

        // then
        assertThat(result.title).isEqualTo("")
        assertThat(result.artist).isEqualTo("")
    }

    @Test
    fun `given MediaItem and MediaMetadata metadata takes precedence`() {
        // given
        val id = "id"
        val title = "title"
        val artist = "artist"

        val mediaItem = MediaItem.Builder()
            .setMediaId(id)
            .setMediaMetadata(
                MediaMetadata.Builder()
                    .setTitle("ignore")
                    .setArtist("ignore")
                    .build()
            )
            .build()

        val mediaMetadata = MediaMetadata.Builder()
            .setTitle(title)
            .setArtist(artist)
            .build()

        // when
        val result = sut.map(mediaItem, mediaMetadata)

        // then
        assertThat(result.id).isEqualTo(id)
        assertThat(result.title).isEqualTo(title)
        assertThat(result.artist).isEqualTo(artist)
    }

    @Test
    fun `given custom extras mapper implementation then implementation is executed`() {
        // given
        val id = "id"
        val uri = "uri"
        val artist = "artist"

        val mediaItem = MediaItem.Builder()
            .setMediaId(id)
            .setUri(Uri.parse(uri))
            .setMediaMetadata(
                MediaMetadata.Builder()
                    .setArtist(artist)
                    .build()
            )
            .build()

        val sut = MediaMapper(object : MediaExtrasMapper {
            override fun map(
                mediaItem: MediaItem,
                mediaMetadata: MediaMetadata?
            ): Map<String, Any> = buildMap {
                put(id, mediaItem.mediaId)
                put(uri, mediaItem.localConfiguration!!.uri)
                put(artist, mediaItem.mediaMetadata.artist!!)
            }
        })

        // when
        val result = sut.map(mediaItem, mediaItem.mediaMetadata)

        // then
        assertThat(result.extras[id]).isEqualTo(id)
        assertThat(result.extras[uri]).isEqualTo(Uri.parse(uri))
        assertThat(result.extras[artist]).isEqualTo(artist)
    }

    @Test
    fun `given MediaEntity then maps correctly`() {
        // given
        val id = "id"
        val uri = "uri"
        val title = "title"
        val artist = "artist"
        val artworkUri = "artworkUri"

        val mediaEntity = MediaEntity(
            mediaId = id,
            mediaUrl = uri,
            artworkUrl = artworkUri,
            title = title,
            artist = artist
        )

        // when
        val result = sut.map(mediaEntity)

        // then
        assertThat(result.id).isEqualTo(id)
        assertThat(result.uri).isEqualTo(uri)
        assertThat(result.title).isEqualTo(title)
        assertThat(result.artist).isEqualTo(artist)
        assertThat(result.artworkUri).isEqualTo(artworkUri)
    }

    @Test
    fun `given MediaEntity with null values then maps correctly`() {
        // given
        val id = "id"
        val uri = "uri"
        val artworkUri = "artworkUri"

        val mediaEntity = MediaEntity(
            mediaId = id,
            mediaUrl = uri,
            artworkUrl = artworkUri,
            title = null,
            artist = null
        )

        // when
        val result = sut.map(mediaEntity)

        // then
        assertThat(result.id).isEqualTo(id)
        assertThat(result.uri).isEqualTo(uri)
        assertThat(result.title).isEqualTo("")
        assertThat(result.artist).isEqualTo("")
        assertThat(result.artworkUri).isEqualTo(artworkUri)
    }
}
