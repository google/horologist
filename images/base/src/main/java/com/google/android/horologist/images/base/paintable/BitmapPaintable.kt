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

package com.google.android.horologist.images.base.paintable

import android.graphics.Bitmap
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.graphics.painter.Painter

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
