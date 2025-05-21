/*
 * Copyright 2025 The Android Open Source Project
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

package com.google.android.horologist.media.ui.material3.components.background

import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.layout.ContentScale
import androidx.wear.compose.material3.ColorScheme
import androidx.wear.compose.material3.MaterialTheme
import com.google.android.horologist.images.base.paintable.Paintable

/** Background created using a radial gradient on top of artwork. */
@Composable
public fun ArtworkImageBackground(
    artwork: Paintable?,
    modifier: Modifier = Modifier,
    colorScheme: ColorScheme = MaterialTheme.colorScheme,
) {
    Crossfade(
        targetState = artwork,
        animationSpec = MaterialTheme.motionScheme.slowEffectsSpec(),
        label = "ArtworkCrossfade",
    ) { currentImage ->
        val currentImagePainter = currentImage?.rememberPainter()
        currentImagePainter?.let { painter ->
            Image(
                painter = painter,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                alpha = 0.6f,
                modifier =
                    modifier.fillMaxSize().drawWithCache {
                        val gradientBrush =
                            Brush.radialGradient(0.65f to Color.Transparent, 1f to colorScheme.background)
                        onDrawWithContent {
                            drawRect(colorScheme.background)
                            drawContent()
                            drawRect(color = colorScheme.primaryContainer, alpha = 0.3f)
                            drawRect(color = colorScheme.onPrimary, alpha = 0.6f)
                            drawRect(gradientBrush)
                        }
                    },
            )
        }
    }
}

@Composable
public fun ColorBackground(
    color: Color?,
    modifier: Modifier = Modifier,
    background: Color = MaterialTheme.colorScheme.background,
) {
    val animatedBackgroundColor =
        animateColorAsState(
            targetValue = color ?: Color.Black,
            animationSpec = tween(450, 0, LinearEasing),
            label = "ColorBackground",
        )

    Box(
        modifier =
            modifier.fillMaxSize().drawWithCache {
                val bitmap = ImageBitmap(size.width.toInt(), size.height.toInt())
                val canvas = androidx.compose.ui.graphics.Canvas(bitmap)
                val paint =
                    Paint().apply {
                        shader =
                            RadialGradientShader(
                                center = Offset(x = size.center.x, y = size.center.y),
                                radius = size.minDimension / 2,
                                colors =
                                    listOf(
                                        animatedBackgroundColor.value.copy(alpha = 0.3f).compositeOver(background),
                                        background,
                                    ),
                            )
                    }
                canvas.drawRect(0f, 0f, size.width, size.height, paint = paint)
                onDrawBehind { drawImage(bitmap) }
            },
    )
}
