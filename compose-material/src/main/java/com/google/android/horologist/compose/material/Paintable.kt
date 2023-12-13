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

package com.google.android.horologist.compose.material

import android.graphics.Bitmap
import androidx.annotation.DrawableRes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.painterResource
import coil.compose.rememberAsyncImagePainter

/** Represents an image or graphic that can be displayed in a compose context via a [Painter]. */
@Stable
public interface Paintable {
    @Composable
    public fun rememberPainter(): Painter
}

/**
 * Represents an image or graphic that can be displayed in a compose context via a [Painter], but
 * that should be treated as an icon rather than an image (for example, tinting can be applied).
 **/
@Stable
public interface PaintableIcon : Paintable

/** An [ImageVector] that can be represented as a [Painter]. */
@JvmInline
public value class ImageVectorPaintable(private val imageVector: ImageVector) :
    PaintableIcon {

        @Composable
        override fun rememberPainter(): Painter = rememberVectorPainter(imageVector)

        public companion object {
            public fun ImageVector.asPaintable(): ImageVectorPaintable = ImageVectorPaintable(
                this,
            )
        }
    }

/** An drawable resource ID that can be represented as a [Painter]. */
@JvmInline
public value class DrawableResPaintable(
    @DrawableRes private val id: Int,
) : PaintableIcon {

    @Composable
    override fun rememberPainter(): Painter = painterResource(id = id)
}

/** A [Bitmap] that can be represented as a [Painter]. */
@JvmInline
public value class BitmapPaintable(private val bitmap: ImageBitmap) : Paintable {

    @Composable
    override fun rememberPainter(): Painter = remember { BitmapPainter(bitmap) }

    public companion object {
        public fun ImageBitmap.asPaintable(): BitmapPaintable = BitmapPaintable(this)
        public fun Bitmap.asPaintable(): BitmapPaintable = this.asImageBitmap().asPaintable()
    }
}

/** A wrapper around a coil-compatible model that can be represented as a [Painter]. */
@Stable
public class CoilPaintable(private val model: Any?, private val placeholder: Painter? = null) :
    Paintable {

        @Composable
        override fun rememberPainter(): Painter = rememberAsyncImagePainter(
            model = model,
            placeholder = placeholder,
        )
    }
