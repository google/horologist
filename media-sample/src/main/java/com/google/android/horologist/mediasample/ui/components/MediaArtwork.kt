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

package com.google.android.horologist.mediasample.ui.components

import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import coil.compose.rememberAsyncImagePainter
import com.google.android.horologist.media.ui.state.model.MediaItemUiModel
import com.google.android.horologist.mediasample.R

@Composable
fun MediaArtwork(
    mediaItem: MediaItemUiModel,
    modifier: Modifier = Modifier,
) {
    val title = mediaItem.title
    val artworkUri = mediaItem.artworkUri

    MediaArtwork(artworkUri = artworkUri, title = title, modifier = modifier)
}

@Composable
fun MediaArtwork(
    artworkUri: String?,
    title: String?,
    modifier: Modifier = Modifier,
) {
    Image(
        modifier = modifier,
        painter = rememberAsyncImagePainter(artworkUri),
        contentDescription = title ?: stringResource(id = R.string.horologist_no_title),
        contentScale = ContentScale.Fit
    )
}
