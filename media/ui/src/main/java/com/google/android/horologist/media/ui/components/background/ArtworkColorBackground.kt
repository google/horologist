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
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.center
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.RadialGradientShader
import androidx.compose.ui.graphics.compositeOver
import androidx.wear.compose.material.MaterialTheme
import com.google.android.horologist.annotations.ExperimentalHorologistApi
import com.google.android.horologist.images.coil.CoilPaintable
import com.google.android.horologist.images.coil.rememberArtworkColor

/**
 * Background using a radial gradient extracted from artwork.
 */
@Composable
@ExperimentalHorologistApi
public fun ArtworkColorBackground(
    paintable: CoilPaintable?,
    modifier: Modifier = Modifier,
    defaultColor: Color? = null,
    background: Color = MaterialTheme.colors.background,
) {
    val artworkColor = rememberArtworkColor(
        model = paintable?.model,
        defaultColor = defaultColor ?: Color.Black,
    )

    ColorBackground(artworkColor.value, modifier = modifier, background = background)
}

@Composable
@ExperimentalHorologistApi
public fun rememberArtworkColorBrush(
    artworkColor: Color,
    background: Color = Color.Black,
): State<Brush> {
    val animatedBackgroundColor = animateColorAsState(
        targetValue = artworkColor,
        animationSpec = tween(450, 0, LinearEasing),
        label = "ColorBackground",
    )

    return remember {
        derivedStateOf {
            Brush.radialGradient(
                listOf(
                    animatedBackgroundColor.value.copy(alpha = 0.3f).compositeOver(background),
                    background,
                ),
            )
        }
    }
}

@Composable
@ExperimentalHorologistApi
public fun ColorBackground(
    color: Color?,
    modifier: Modifier = Modifier,
    background: Color = MaterialTheme.colors.background,
) {
    val animatedBackgroundColor = animateColorAsState(
        targetValue = color ?: Color.Black,
        animationSpec = tween(450, 0, LinearEasing),
        label = "ColorBackground",
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .drawWithCache {
                val bitmap = ImageBitmap(size.width.toInt(), size.height.toInt())
                val canvas = androidx.compose.ui.graphics.Canvas(bitmap)
                val paint = Paint().apply {
                    shader = RadialGradientShader(
                        center = Offset(x = size.center.x, y = size.center.y),
                        radius = size.minDimension / 2,
                        colors = listOf(
                            animatedBackgroundColor.value
                                .copy(alpha = 0.3f)
                                .compositeOver(background),
                            background,
                        ),
                    )
                }
                canvas.drawRect(0f, 0f, size.width, size.height, paint = paint)
                onDrawBehind {
                    drawImage(bitmap)
                }
            },
    )
}
