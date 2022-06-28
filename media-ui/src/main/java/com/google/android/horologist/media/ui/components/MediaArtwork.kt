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

package com.google.android.horologist.media.ui.components

import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import coil.compose.rememberAsyncImagePainter
import com.google.android.horologist.media.ui.ExperimentalHorologistMediaUiApi
import com.google.android.horologist.media.ui.state.model.MediaItemUiModel

@ExperimentalHorologistMediaUiApi
@Composable
public fun MediaArtwork(
    mediaItem: MediaItemUiModel,
    modifier: Modifier = Modifier,
    placeholder: Painter? = null,
) {
    MediaArtwork(
        artworkUri = mediaItem.artworkUri,
        contentDescription = mediaItem.title,
        modifier = modifier,
        placeholder = placeholder
    )
}

@ExperimentalHorologistMediaUiApi
@Composable
public fun MediaArtwork(
    artworkUri: String?,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    placeholder: Painter? = null,
) {
    Image(
        modifier = modifier,
        painter = rememberAsyncImagePainter(model = artworkUri, placeholder = placeholder),
        contentDescription = contentDescription,
        contentScale = ContentScale.Fit
    )
}
