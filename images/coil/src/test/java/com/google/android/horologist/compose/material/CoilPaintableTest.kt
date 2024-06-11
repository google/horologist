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

@file:OptIn(ExperimentalCoilApi::class)

package com.google.android.horologist.compose.material

import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.os.Looper
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.core.graphics.drawable.toDrawable
import androidx.wear.compose.material.ChipDefaults
import androidx.wear.compose.material.LocalContentAlpha
import coil.annotation.ExperimentalCoilApi
import coil.compose.AsyncImage
import coil.decode.DataSource
import coil.request.ImageRequest
import coil.request.SuccessResult
import coil.test.FakeImageLoaderEngine
import coil.transform.CircleCropTransformation
import com.google.android.horologist.compose.material.util.DECORATIVE_ELEMENT_CONTENT_DESCRIPTION
import com.google.android.horologist.images.coil.CoilPaintable
import com.google.android.horologist.images.coil.R.drawable.sample_image
import com.google.android.horologist.screenshots.rng.WearLegacyComponentTest
import org.junit.Test
import org.robolectric.Shadows.shadowOf

class CoilPaintableTest : WearLegacyComponentTest() {

    override val imageLoader = FakeImageLoaderEngine.Builder()
        .intercept(
            predicate = {
                it == sample_image
            },
            interceptor = {
                val rawBitmap =
                    BitmapFactory.decodeFile("src/main/res/drawable-nodpi/sample_image.png")

                val resultBitmap = it.request.transformations.fold(rawBitmap) { bitmap, transformation ->
                    transformation.transform(bitmap, it.size)
                }

                SuccessResult(
                    drawable = resultBitmap.toDrawable(it.request.context.resources),
                    request = it.request,
                    dataSource = DataSource.MEMORY,
                )
            },
        )
        .build()

    @Test
    fun imageFromChip() {
        runComponentTest {
            val paintable = CoilPaintable(
                sample_image,
            )

            ImageFromChip(paintable)
        }
    }

    @Test
    fun imageFromChipAlpha50() {
        runComponentTest {
            val paintable = CoilPaintable(
                sample_image,
            )

            CompositionLocalProvider(LocalContentAlpha provides 0.5f) {
                ImageFromChip(paintable)
            }
        }
    }

    @Test
    fun imageDirectAlpha50() {
        runComponentTest {
            val paintable = CoilPaintable(
                sample_image,
            )

            Image(
                painter = paintable.rememberPainter(),
                contentDescription = DECORATIVE_ELEMENT_CONTENT_DESCRIPTION,
                modifier = Modifier
                    .size(ChipDefaults.LargeIconSize)
                    .clip(CircleShape)
                    .alpha(0.5f),
            )
        }
    }

    @Test
    fun imageDirectOnlyDefaults() {
        runComponentTest {
            val paintable = CoilPaintable(
                sample_image,
            )

            Image(
                painter = paintable.rememberPainter(),
                contentDescription = DECORATIVE_ELEMENT_CONTENT_DESCRIPTION,
                modifier = Modifier
                    .size(ChipDefaults.LargeIconSize)
            )
        }
    }

    @Test
    fun imageRequest() {
        runComponentTest {
            val paintable = CoilPaintable(
                ImageRequest.Builder(LocalContext.current)
                    .data(sample_image)
                    .build()
            )

            Image(
                painter = paintable.rememberPainter(),
                contentDescription = DECORATIVE_ELEMENT_CONTENT_DESCRIPTION,
                modifier = Modifier
                    .size(ChipDefaults.LargeIconSize)
                    .clip(CircleShape),
            )
        }
    }

    @Test
    fun imageRequestWithTransformationAlpha50() {
        runComponentTest {
            val paintable = CoilPaintable(
                ImageRequest.Builder(LocalContext.current)
                    .data(sample_image)
                    .transformations(CircleCropTransformation())
                    .build()
            )

            Image(
                painter = paintable.rememberPainter(),
                contentDescription = DECORATIVE_ELEMENT_CONTENT_DESCRIPTION,
                alpha = 0.5f,
                modifier = Modifier.size(ChipDefaults.LargeIconSize)
            )
        }
    }

    // Not using CoilPaintable, just Coil directly
    @Test
    fun coilDirectlyAsyncImageAlpha50() {
        runComponentTest {
            AsyncImage(
                contentDescription = DECORATIVE_ELEMENT_CONTENT_DESCRIPTION,
                model = sample_image,
                modifier = Modifier
                    .size(ChipDefaults.LargeIconSize)
                    .clip(CircleShape)
                    .alpha(0.5f)
            )
        }
    }

    @Composable
    private fun ImageFromChip(paintable: CoilPaintable) {
        Image(
            painter = paintable.rememberPainter(),
            contentDescription = DECORATIVE_ELEMENT_CONTENT_DESCRIPTION,
            modifier = Modifier
                .size(ChipDefaults.LargeIconSize)
                .clip(CircleShape),
            contentScale = ContentScale.Crop,
            alpha = LocalContentAlpha.current,
        )
    }
}
