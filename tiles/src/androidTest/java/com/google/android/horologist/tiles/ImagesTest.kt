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

@file:OptIn(ExperimentalCoroutinesApi::class, ExperimentalCoroutinesApi::class)

package com.google.android.horologist.tiles

import android.content.Context
import androidx.test.filters.SmallTest
import androidx.test.platform.app.InstrumentationRegistry
import androidx.wear.tiles.ResourceBuilders
import com.google.android.horologist.tiles.images.loadImageResource
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

@SmallTest
class ImagesTest {
    private lateinit var context: Context

    @Before
    fun setup() {
        context = InstrumentationRegistry.getInstrumentation().context
    }

    @Test
    public fun loadImageResource() {
        runTest {
            val imageLoader = FakeImageLoader {
                // https://wordpress.org/openverse/image/34896de8-afb0-494c-af63-17b73fc14124/
                FakeImageLoader.loadSuccessBitmap(context, it, R.drawable.coil)
            }

            val imageResource = imageLoader.loadImageResource(context, R.drawable.coil)

            val inlineResource = imageResource!!.inlineResource!!
            assertThat(inlineResource.format).isEqualTo(ResourceBuilders.IMAGE_FORMAT_RGB_565)
            assertThat(inlineResource.widthPx).isEqualTo(200)
            assertThat(inlineResource.heightPx).isEqualTo(134)
        }
    }

    @Test
    public fun handlesFailures() {
        runTest {
            val imageLoader = FakeImageLoader {
                // https://wordpress.org/openverse/image/34896de8-afb0-494c-af63-17b73fc14124/
                FakeImageLoader.loadErrorBitmap(context, it, R.drawable.coil)
            }

            val imageResource = imageLoader.loadImageResource(context, R.drawable.coil) {
                error(R.drawable.coil)
            }

            val inlineResource = imageResource!!.inlineResource!!
            assertThat(inlineResource.format).isEqualTo(ResourceBuilders.IMAGE_FORMAT_RGB_565)
        }
    }
}
