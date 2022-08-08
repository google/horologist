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
import androidx.media3.common.MediaItem.RequestMetadata
import androidx.media3.common.MediaMetadata
import com.google.android.horologist.media.data.ExperimentalHorologistMediaDataApi
import com.google.android.horologist.media.model.Media
import com.google.common.truth.Truth.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE)
class MediaItemMapperTest {

    private lateinit var sut: MediaItemMapper

    @Before
    fun setUp() {
        sut = MediaItemMapper(MediaItemExtrasMapperNoopImpl)
    }

    @Test
    fun `given MediaItem then maps correctly`() {
        // given
        val id = "id"
        val uri = "uri"
        val title = "title"
        val artist = "artist"
        val artworkUri = "artworkUri"

        val mediaItem = Media(
            id = id,
            uri = uri,
            title = title,
            artist = artist,
            artworkUri = artworkUri
        )

        // when
        val result = sut.map(mediaItem)

        // then
        assertThat(result.mediaId).isEqualTo(id)
        assertThat(result.localConfiguration!!.uri).isEqualTo((Uri.parse(uri)))
        assertThat(result.requestMetadata.mediaUri).isEqualTo((Uri.parse(uri)))
        assertThat(result.mediaMetadata.displayTitle).isEqualTo(title)
        assertThat(result.mediaMetadata.artist).isEqualTo(artist)
        assertThat(result.mediaMetadata.artworkUri).isEqualTo(Uri.parse(artworkUri))
    }

    @Test
    fun `given MediaItem with null values then maps correctly`() {
        // given
        val id = "id"
        val uri = "uri"
        val artist = "artist"
        val title = "title"

        val mediaItem = Media(
            id = id,
            uri = uri,
            artist = artist,
            title = title
        )

        // when
        val result = sut.map(mediaItem)

        // then
        assertThat(result.mediaId).isEqualTo(id)
        assertThat(result.localConfiguration!!.uri).isEqualTo((Uri.parse(uri)))
        assertThat(result.requestMetadata.mediaUri).isEqualTo((Uri.parse(uri)))
        assertThat(result.mediaMetadata.displayTitle).isEqualTo(title)
        assertThat(result.mediaMetadata.artist).isEqualTo(artist)
        assertThat(result.mediaMetadata.artworkUri).isNull()
    }

    @Test
    fun `given custom extras mapper implementation then implementation is executed`() {
        // given
        val customId = "customId"
        val customIdValue = "customIdValue"
        val customArtist = "customArtist"
        val customArtistValue = "customArtistValue"
        val customUri = "customUri"
        val customUriValue = "customUriValue"

        val mediaItem = Media(
            id = "id",
            uri = "uri",
            artist = "artist",
            title = "title",
            extras = mapOf(
                customId to customIdValue,
                customArtist to customArtistValue,
                customUri to customUriValue
            )
        )

        val sut = MediaItemMapper(object : MediaItemExtrasMapper {
            override fun map(
                media: Media,
                mediaItemBuilder: MediaItem.Builder,
                mediaMetadataBuilder: MediaMetadata.Builder,
                requestMetadataBuilder: RequestMetadata.Builder
            ) {
                mediaItemBuilder.setMediaId(media.extras[customId].toString())
                mediaMetadataBuilder.setArtist(media.extras[customArtist].toString())
                requestMetadataBuilder.setMediaUri(Uri.parse(media.extras[customUri].toString()))
            }
        })

        // when
        val result = sut.map(mediaItem)

        // then
        assertThat(result.mediaId).isEqualTo(customIdValue)
        assertThat(result.mediaMetadata.artist).isEqualTo(customArtistValue)
        assertThat(result.requestMetadata.mediaUri).isEqualTo(Uri.parse(customUriValue))
    }
}
