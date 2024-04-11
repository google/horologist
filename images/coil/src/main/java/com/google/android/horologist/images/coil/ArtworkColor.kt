/*
 * Copyright 2023 The Android Open Source Project
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

package com.google.android.horologist.images.coil

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.core.graphics.drawable.toBitmap
import androidx.palette.graphics.Palette
import androidx.wear.compose.material.MaterialTheme
import coil.imageLoader
import coil.request.ImageRequest
import com.google.android.horologist.annotations.ExperimentalHorologistApi

@Composable
@ExperimentalHorologistApi
public fun rememberArtworkColor(
    model: Any?,
    defaultColor: Color = MaterialTheme.colors.primary,
): State<Color> {
    val context = LocalContext.current
    val imageLoader = context.imageLoader

    val artworkColor = remember { mutableStateOf(defaultColor) }

    LaunchedEffect(model) {
        artworkColor.value = if (model != null) {
            val request =
                ImageRequest.Builder(context)
                    .data(model)
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

private fun centerColor(palette: Palette?) =
    palette?.lightVibrantSwatch?.rgb?.color
        ?: palette?.lightMutedSwatch?.rgb?.color ?: Color.Black

private val Int.color: Color
    get() = Color(this)
