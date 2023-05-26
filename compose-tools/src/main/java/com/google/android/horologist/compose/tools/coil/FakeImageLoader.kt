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

package com.google.android.horologist.compose.tools.coil

import android.content.Context
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import coil.Coil
import coil.ComponentRegistry
import coil.ImageLoader
import coil.decode.DataSource
import coil.disk.DiskCache
import coil.memory.MemoryCache
import coil.request.DefaultRequestOptions
import coil.request.Disposable
import coil.request.ErrorResult
import coil.request.ImageRequest
import coil.request.ImageResult
import coil.request.SuccessResult
import com.google.android.horologist.compose.tools.R
import kotlinx.coroutines.awaitCancellation
import java.io.IOException

// https://coil-kt.github.io/coil/image_loaders/#testing
public class FakeImageLoader(private val imageFn: suspend (ImageRequest) -> ImageResult) :
    ImageLoader {
    override val defaults: DefaultRequestOptions = DefaultRequestOptions()
    override val components: ComponentRegistry = ComponentRegistry()
    override val memoryCache: MemoryCache? get() = null
    override val diskCache: DiskCache? get() = null

    override fun enqueue(request: ImageRequest): Disposable = TODO()

    override suspend fun execute(request: ImageRequest): ImageResult {
        return imageFn(request)
    }

    override fun newBuilder(): ImageLoader.Builder = throw UnsupportedOperationException()

    override fun shutdown() {}

    // To be replaced by https://github.com/coil-kt/coil/pull/1451
    public inline fun override(function: () -> Unit) {
        Coil.setImageLoader(this)

        try {
            function()
        } finally {
            Coil::class.java.getDeclaredField("imageLoader").apply {
                isAccessible = true
            }.set(null, null)
        }
    }

    public companion object {
        public val NotFound: FakeImageLoader =
            FakeImageLoader { ErrorResult(null, it, IOException()) }

        public val Never: FakeImageLoader =
            FakeImageLoader { awaitCancellation() }

        public val Resources: FakeImageLoader =
            FakeImageLoader { request ->
                val context = request.context
                val data = dataAsResourceId(request.data)

                if (data != null) {
                    loadSuccessBitmap(context, request, data)
                } else {
                    loadErrorBitmap(request)
                }
            }

        public fun Fixed(@DrawableRes resId: Int): FakeImageLoader =
            FakeImageLoader { request ->
                val drawable = ContextCompat.getDrawable(request.context, resId)!!
                SuccessResult(drawable, request, DataSource.DISK)
            }

        public fun loadSuccessBitmap(
            context: Context,
            request: ImageRequest,
            @DrawableRes id: Int
        ): ImageResult {
            val drawable = ContextCompat.getDrawable(context, id)!!
            return SuccessResult(
                drawable = drawable,
                request = request,
                dataSource = DataSource.DISK
            )
        }

        public fun loadErrorBitmap(
            request: ImageRequest
        ): ImageResult {
            return ErrorResult(
                drawable = null,
                request = request,
                throwable = IOException("request for fake image failed")
            )
        }

        public fun dataAsResourceId(data: Any?): Int? {
            return if (data is Int) {
                data
            } else if (data is String && data.startsWith(TestUriPrefix)) {
                data.substring(TestUriPrefix.length).toInt()
            } else {
                null
            }
        }

        @DrawableRes public val TestIconResource: Int = R.drawable.sample_image
        public const val TestUriPrefix: String = "android.resource://com.google.android.horologist.compose.tools/"
        public val TestIconResourceUri: String = TestUriPrefix + TestIconResource
    }
}
