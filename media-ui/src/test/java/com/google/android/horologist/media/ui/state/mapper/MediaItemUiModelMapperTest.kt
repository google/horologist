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

import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import com.google.common.truth.Truth.assertThat
import org.junit.Test

class MediaItemUiModelMapperTest {

    @Test
    fun givenMediaItem_thenMapsCorrectly() {
        // given
        val title = "title"
        val artist = "artist"
        val metadataBuilder = MediaMetadata.Builder()
            .setDisplayTitle(title)
            .setArtist(artist)

        val mediaItem = MediaItem.Builder()
            .setMediaMetadata(metadataBuilder.build())
            .build()

        // when
        val result = MediaItemUiModelMapper.map(mediaItem)

        // then
        assertThat(result.title).isEqualTo(title)
        assertThat(result.artist).isEqualTo(artist)
    }
}
