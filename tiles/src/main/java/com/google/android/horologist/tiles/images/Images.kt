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

package com.google.android.horologist.tiles.images

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.wear.protolayout.ResourceBuilders
import androidx.wear.protolayout.ResourceBuilders.IMAGE_FORMAT_ARGB_8888
import androidx.wear.protolayout.ResourceBuilders.IMAGE_FORMAT_RGB_565
import androidx.wear.protolayout.ResourceBuilders.IMAGE_FORMAT_UNDEFINED
import androidx.wear.protolayout.ResourceBuilders.ImageResource
import coil.ImageLoader
import coil.request.ImageRequest
import java.nio.ByteBuffer

/**
 * Load a Bitmap from a CoilImage loader.
 *
 * @param context the context of the service or activity.
 * @param data the image to fetch in one of the support Coil formats such as String, HttpUrl.
 * @param configurer any additional configuration of the ImageRequest being built.
 */
public suspend fun ImageLoader.loadImage(
    context: Context,
    data: Any?,
    configurer: ImageRequest.Builder.() -> Unit = {},
): Bitmap? {
    val request = ImageRequest.Builder(context)
        .data(data)
        .apply(configurer)
        .allowRgb565(false)
        .allowHardware(false)
        .build()
    val response = execute(request)
    return (response.drawable as? BitmapDrawable)?.bitmap
}

/**
 * Load an ImageResource from a CoilImage loader.
 *
 * @param context the context of the service or activity.
 * @param data the image to fetch in one of the support Coil formats such as String, HttpUrl.
 * @param configurer any additional configuration of the ImageRequest being built.
 */
public suspend fun ImageLoader.loadImageResource(
    context: Context,
    data: Any?,
    configurer: ImageRequest.Builder.() -> Unit = {},
): ImageResource? = loadImage(context, data, configurer)?.toImageResource()

/**
 * Convert a bitmap to a ImageResource.
 *
 * Format will be one of IMAGE_FORMAT_ARGB_8888, IMAGE_FORMAT_RGB_565 or IMAGE_FORMAT_UNDEFINED,
 * based on the bitmap.
 */
public fun Bitmap.toImageResource(): ImageResource {
    val format = when (this.config) {
        Bitmap.Config.ARGB_8888 -> IMAGE_FORMAT_ARGB_8888
        Bitmap.Config.RGB_565 -> IMAGE_FORMAT_RGB_565
        else -> IMAGE_FORMAT_UNDEFINED
    }

    val byteBuffer = ByteBuffer.allocate(byteCount)
    copyPixelsToBuffer(byteBuffer)
    val bytes: ByteArray = byteBuffer.array()

    return ImageResource.Builder().setInlineResource(
        ResourceBuilders.InlineImageResource.Builder()
            .setData(bytes)
            .setWidthPx(width)
            .setHeightPx(height)
            .setFormat(format)
            .build(),
    )
        .build()
}

/**
 * Convert a [ImageBitmap] to a ImageResource.
 *
 * Format will be one of IMAGE_FORMAT_ARGB_8888, IMAGE_FORMAT_RGB_565 or IMAGE_FORMAT_UNDEFINED,
 * based on the bitmap.
 */
public fun ImageBitmap.toImageResource(): ImageResource = this.asAndroidBitmap().toImageResource()
