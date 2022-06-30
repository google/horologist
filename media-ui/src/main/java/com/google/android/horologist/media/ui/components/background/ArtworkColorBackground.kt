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

package com.google.android.horologist.media.ui.components.background

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.core.graphics.drawable.toBitmap
import androidx.palette.graphics.Palette
import coil.imageLoader
import coil.request.ImageRequest

/**
 * Background using a radial gradient extracted from artwork.
 */
@Composable
public fun ArtworkColorBackground(
    artworkUri: Any?,
    modifier: Modifier = Modifier,
    defaultColor: Color? = null,
) {
    val context = LocalContext.current
    val imageLoader = context.imageLoader

    var artworkColor by remember { mutableStateOf<Color?>(null) }

    LaunchedEffect(artworkUri) {
        artworkColor = if (artworkUri != null) {
            val request =
                ImageRequest.Builder(context)
                    .data(artworkUri)
                    .allowHardware(false)
                    .build()
            val result = imageLoader.execute(request)
            val palette = result.drawable?.let { Palette.Builder(it.toBitmap()).generate() }
            centerColor(palette)
        } else {
            null
        }
    }

    ColorBackground(color = artworkColor ?: defaultColor, modifier = modifier)
}

@Composable
public fun ColorBackground(
    color: Color?,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.radialGradient(
                    listOf(
                        (color ?: Color.Black).copy(alpha = 0.3f),
                        Color.Transparent
                    )
                )
            )
    )
}

private fun centerColor(palette: Palette?) =
    palette?.lightVibrantSwatch?.rgb?.color
        ?: palette?.lightMutedSwatch?.rgb?.color ?: Color.Black

private val Int.color: Color
    get() = Color(this)
