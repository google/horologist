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

package com.google.android.horologist.media.ui.components

import androidx.core.content.ContextCompat
import coil.annotation.ExperimentalCoilApi
import coil.decode.DataSource
import coil.request.SuccessResult
import coil.test.FakeImageLoaderEngine
import com.google.android.horologist.images.coil.CoilPaintable
import com.google.android.horologist.images.coil.FakeImageLoader
import com.google.android.horologist.media.ui.state.model.MediaUiModel
import com.google.android.horologist.screenshots.rng.WearLegacyA11yTest
import org.junit.Test

class MediaChipA11yTest : WearLegacyA11yTest() {

    override val imageLoader = FakeImageLoaderEngine.Builder()
        .intercept(
            predicate = {
                it == FakeImageLoader.TestIconResourceUri
            },
            interceptor = {
                SuccessResult(
                    drawable = ContextCompat.getDrawable(
                        it.request.context,
                        FakeImageLoader.TestIconResource,
                    )!!,
                    request = it.request,
                    dataSource = DataSource.DISK,
                )
            },
        )
        .build()

    @Test
    fun a11y() {
        runComponentTest {
            MediaChip(
                media = MediaUiModel(
                    id = "id",
                    title = "Red Hot Chilli Peppers",
                    artwork = CoilPaintable(FakeImageLoader.TestIconResourceUri),
                ),
                onClick = {},
            )
        }
    }
}
