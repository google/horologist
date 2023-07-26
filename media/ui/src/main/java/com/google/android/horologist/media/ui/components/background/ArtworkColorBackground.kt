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

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.platform.LocalContext
import androidx.core.graphics.drawable.toBitmap
import androidx.palette.graphics.Palette
import androidx.wear.compose.material.MaterialTheme
import coil.imageLoader
import coil.request.ImageRequest
import com.google.android.horologist.annotations.ExperimentalHorologistApi

/**
 * Background using a radial gradient extracted from artwork.
 */
@Composable
@ExperimentalHorologistApi
@Deprecated("Prefer background modifier")
public fun ArtworkColorBackground(
    artworkUri: Any?,
    modifier: Modifier = Modifier,
    defaultColor: Color? = null,
    background: Color = MaterialTheme.colors.background
) {
    val artworkColor = rememberArtworkColor(
        artworkUri = artworkUri,
        defaultColor = defaultColor ?: Color.Black
    )

    val radialGradiant = rememberArtworkColorBrush(
        artworkColor = artworkColor.value,
        background = background
    )

    Canvas(modifier = modifier) {
        drawRect(radialGradiant.value)
    }
}

@Composable
@ExperimentalHorologistApi
public fun rememberArtworkColorBrush(
    artworkColor: Color,
    background: Color = Color.Black
): State<Brush> {
    val animatedBackgroundColor = animateColorAsState(
        targetValue = artworkColor,
        animationSpec = tween(450, 0, LinearEasing),
        label = "ColorBackground"
    )

    return remember {
        derivedStateOf {
            Brush.radialGradient(
                listOf(
                    animatedBackgroundColor.value.copy(alpha = 0.3f).compositeOver(background),
                    background
                )
            )
        }
    }
}

@Composable
@ExperimentalHorologistApi
public fun rememberArtworkColor(
    artworkUri: Any?,
    defaultColor: Color = MaterialTheme.colors.primary
): State<Color> {
    val context = LocalContext.current
    val imageLoader = context.imageLoader

    val artworkColor = remember { mutableStateOf(defaultColor) }

    LaunchedEffect(artworkUri) {
        artworkColor.value = if (artworkUri != null) {
            val request =
                ImageRequest.Builder(context)
                    .data(artworkUri)
                    .allowHardware(false)
                    .build()
            val result = imageLoader.execute(request)
            val palette = result.drawable?.let { Palette.Builder(it.toBitmap()).generate() }
            centerColor(palette)
        } else {
            defaultColor
        }
    }
    return artworkColor
}

@Composable
@Deprecated("Prefer background modifier")
@ExperimentalHorologistApi
public fun ColorBackground(
    color: Color?,
    modifier: Modifier = Modifier,
    background: Color = MaterialTheme.colors.background
) {
    val animatedBackgroundColor = animateColorAsState(
        targetValue = color ?: Color.Black,
        animationSpec = tween(450, 0, LinearEasing),
        label = "ColorBackground"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.radialGradient(
                    listOf(
                        animatedBackgroundColor.value
                            .copy(alpha = 0.3f)
                            .compositeOver(background),
                        background
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
