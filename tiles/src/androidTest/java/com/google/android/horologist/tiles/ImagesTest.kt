/*
 * Copyright 2026 The Android Open Source Project
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

package com.google.android.horologist.tiles

import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.ImageDecoder
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SdkSuppress
import androidx.wear.protolayout.ResourceBuilders
import com.google.android.horologist.tiles.images.toImageResource
import com.google.common.truth.Truth.assertThat
import java.io.ByteArrayOutputStream
import java.nio.ByteBuffer
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@SdkSuppress(minSdkVersion = 28)
class ImagesTest {
    @Test
    fun hardwareBitmapToImageResource() {
        val softwareBitmap = Bitmap.createBitmap(
            intArrayOf(Color.RED, Color.GREEN),
            2,
            1,
            Bitmap.Config.ARGB_8888,
        )
        val encodedBitmap = ByteArrayOutputStream().also {
            softwareBitmap.compress(Bitmap.CompressFormat.PNG, 100, it)
        }.toByteArray()
        val hardwareBitmap = ImageDecoder.decodeBitmap(
            ImageDecoder.createSource(ByteBuffer.wrap(encodedBitmap)),
        ) { decoder, _, _ ->
            decoder.allocator = ImageDecoder.ALLOCATOR_HARDWARE
        }
        assertThat(hardwareBitmap.config).isEqualTo(Bitmap.Config.HARDWARE)
        val expected = hardwareBitmap.copy(Bitmap.Config.ARGB_8888, false)
            .toImageResource()
            .inlineResource!!

        val actual = hardwareBitmap.toImageResource().inlineResource!!

        assertThat(actual.format).isEqualTo(ResourceBuilders.IMAGE_FORMAT_ARGB_8888)
        assertThat(actual.widthPx).isEqualTo(2)
        assertThat(actual.heightPx).isEqualTo(1)
        assertThat(actual.data).isEqualTo(expected.data)
    }
}
