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
import com.google.android.horologist.annotations.ExperimentalHorologistApi
import com.google.android.horologist.images.base.paintable.Paintable
import com.google.android.horologist.media.ui.state.model.MediaUiModel

@ExperimentalHorologistApi
@Composable
public fun MediaArtwork(
    media: MediaUiModel.Ready,
    modifier: Modifier = Modifier,
    placeholder: Painter? = null,
) {
    val painter = media.artwork?.rememberPainter() ?: placeholder
    if (painter != null) {
        MediaArtwork(
            painter = painter,
            contentDescription = media.title,
            modifier = modifier,
        )
    }
}

@ExperimentalHorologistApi
@Composable
public fun MediaArtwork(
    artworkPaintable: Paintable,
    contentDescription: String?,
    modifier: Modifier = Modifier,
) {
    Image(
        modifier = modifier,
        painter = artworkPaintable.rememberPainter(),
        contentDescription = contentDescription,
        contentScale = ContentScale.Fit,
    )
}

@ExperimentalHorologistApi
@Composable
public fun MediaArtwork(
    painter: Painter,
    contentDescription: String?,
    modifier: Modifier = Modifier,
) {
    Image(
        modifier = modifier,
        painter = painter,
        contentDescription = contentDescription,
        contentScale = ContentScale.Fit,
    )
}
