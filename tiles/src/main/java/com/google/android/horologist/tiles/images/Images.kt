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
import androidx.wear.tiles.ResourceBuilders
import androidx.wear.tiles.ResourceBuilders.ImageResource
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
    configurer: ImageRequest.Builder.() -> Unit = {}
): Bitmap? {
    val request = ImageRequest.Builder(context)
        .data(data)
        .apply(configurer)
        .allowRgb565(true)
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
    configurer: ImageRequest.Builder.() -> Unit = {}
): ImageResource? = loadImage(context, data, configurer)?.toImageResource()

/**
 * Convert a bitmap to a ImageResource.
 *
 * Ensures it uses RGB_565 encoding, then generates an ImageResource
 * with the correct width and height.
 */
public fun Bitmap.toImageResource(): ImageResource {
    val rgb565Bitmap = if (config == Bitmap.Config.RGB_565) {
        this
    } else {
        copy(Bitmap.Config.RGB_565, false)
    }

    val byteBuffer = ByteBuffer.allocate(rgb565Bitmap.byteCount)
    rgb565Bitmap.copyPixelsToBuffer(byteBuffer)
    val bytes: ByteArray = byteBuffer.array()

    return ImageResource.Builder().setInlineResource(
        ResourceBuilders.InlineImageResource.Builder()
            .setData(bytes)
            .setWidthPx(rgb565Bitmap.width)
            .setHeightPx(rgb565Bitmap.height)
            .setFormat(ResourceBuilders.IMAGE_FORMAT_RGB_565)
            .build()
    )
        .build()
}
