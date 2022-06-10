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

package com.google.android.horologist.tiles

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import androidx.test.platform.app.InstrumentationRegistry
import androidx.wear.tiles.ResourceBuilders
import coil.ComponentRegistry
import coil.ImageLoader
import coil.decode.DataSource
import coil.disk.DiskCache
import coil.memory.MemoryCache
import coil.request.DefaultRequestOptions
import coil.request.Disposable
import coil.request.ImageRequest
import coil.request.ImageResult
import coil.request.SuccessResult
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class ImagesTest {
    private lateinit var bitmap: Bitmap
    private lateinit var context: Context
    private lateinit var imageLoader: ImageLoader

    @Before
    fun setup() {
        context = InstrumentationRegistry.getInstrumentation().context
        imageLoader = FakeImageLoader(context)

        // https://wordpress.org/openverse/image/34896de8-afb0-494c-af63-17b73fc14124/
        bitmap = BitmapFactory.decodeResource(context.resources, R.drawable.coil)
    }

    @Test
    public fun loadImageResource() {
        runTest {
            val imageResource = imageLoader.loadImageResource(context, bitmap)

            val inlineResource = imageResource!!.inlineResource!!
            assertThat(inlineResource).isNotNull()
            assertThat(inlineResource.format).isEqualTo(ResourceBuilders.IMAGE_FORMAT_RGB_565)
            assertThat(inlineResource.widthPx).isEqualTo(100)
            assertThat(inlineResource.heightPx).isEqualTo(100)
        }
    }
}

// https://coil-kt.github.io/coil/image_loaders/#testing
class FakeImageLoader(private val context: Context) : ImageLoader {
    override val defaults = DefaultRequestOptions()
    override val components = ComponentRegistry()
    override val memoryCache: MemoryCache? get() = null
    override val diskCache: DiskCache? get() = null

    override fun enqueue(request: ImageRequest): Disposable {
        // Always call onStart before onSuccess.
        request.target?.onStart(request.placeholder)
        val result = BitmapDrawable(context.resources, request.data as Bitmap)
        request.target?.onSuccess(result)
        return object : Disposable {
            override val job = CompletableDeferred(newResult(request, result))
            override val isDisposed get() = true
            override fun dispose() {}
        }
    }

    override suspend fun execute(request: ImageRequest): ImageResult {
        val result = BitmapDrawable(context.resources, request.data as Bitmap)
        return newResult(request, result)
    }

    private fun newResult(request: ImageRequest, drawable: BitmapDrawable): SuccessResult {
        return SuccessResult(
            drawable = drawable,
            request = request,
            dataSource = DataSource.NETWORK
        )
    }

    override fun newBuilder(): ImageLoader.Builder = throw UnsupportedOperationException()

    override fun shutdown() {}
}
