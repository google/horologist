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

package com.google.android.horologist.media.ui.state.mapper

import androidx.compose.ui.graphics.Color
import com.google.android.horologist.annotations.ExperimentalHorologistApi
import com.google.android.horologist.media.model.Media
import com.google.android.horologist.media.ui.state.model.MediaUiModel

/**
 * Map a [Media] into a [MediaUiModel]
 */
@ExperimentalHorologistApi
public object MediaUiModelMapper {

    public fun map(media: Media, defaultTitle: String = "", defaultArtist: String = ""): MediaUiModel {
        var title = media.title
        var artist = media.artist
        if (title.isEmpty() && artist.isEmpty()) {
            title = defaultTitle
            artist = defaultArtist
        }
        return MediaUiModel(
            id = media.id,
            title = title,
            subtitle = artist,
            artworkUri = media.artworkUri,
            artworkColor = media.artworkColor?.let { Color(it) }
        )
    }
}
