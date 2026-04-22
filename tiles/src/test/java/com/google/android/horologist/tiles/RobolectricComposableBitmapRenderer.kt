/*
 * Copyright 2025 The Android Open Source Project
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

import android.os.Looper.getMainLooper
import android.view.View
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.ImageBitmapConfig
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.DpSize
import androidx.concurrent.futures.await
import androidx.test.core.app.ActivityScenario
import androidx.test.core.view.captureToBitmapAsync
import com.google.android.horologist.tiles.composable.ComposableBitmapRenderer
import com.google.android.horologist.tiles.composable.convert
import org.robolectric.Shadows.shadowOf
import org.robolectric.shadows.ShadowLooper

class RobolectricComposableBitmapRenderer() : ComposableBitmapRenderer {
    override suspend fun renderComposableToBitmap(
        canvasSize: DpSize,
        config: ImageBitmapConfig?,
        composableContent: @Composable () -> Unit,
    ): ImageBitmap {
        val scenario = ActivityScenario.launch(ComponentActivity::class.java)

        scenario.use {
            lateinit var content: View
            scenario.onActivity({ activity ->
                activity.setContent {
                    Box(modifier = Modifier.size(canvasSize)) {
                        composableContent()
                    }
                }
                content = activity.findViewById(android.R.id.content)
            })

            shadowOf(getMainLooper()).idle()
            ShadowLooper.idleMainLooper()

            val rawBitmap = content.captureToBitmapAsync().await().asImageBitmap()
            return if (config != null) {
                rawBitmap.convert(config)
            } else {
                rawBitmap
            }
        }
    }
}
