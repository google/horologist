/*
 * Copyright 2024 The Android Open Source Project
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

@file:Suppress("UnstableApiUsage", "DEPRECATION")
@file:OptIn(ExperimentalTestApi::class)

package com.google.android.horologist.tiles

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material3.FilledIconButton
import androidx.wear.compose.material3.Text
import androidx.wear.protolayout.ColorBuilders.argb
import androidx.wear.protolayout.DeviceParametersBuilders.DeviceParameters
import androidx.wear.protolayout.DimensionBuilders.dp
import androidx.wear.protolayout.DimensionBuilders.expand
import androidx.wear.protolayout.LayoutElementBuilders
import androidx.wear.protolayout.LayoutElementBuilders.Box
import androidx.wear.protolayout.LayoutElementBuilders.CONTENT_SCALE_MODE_FIT
import androidx.wear.protolayout.LayoutElementBuilders.Image
import androidx.wear.protolayout.ModifiersBuilders.Background
import androidx.wear.protolayout.ModifiersBuilders.Modifiers
import androidx.wear.protolayout.ResourceBuilders
import androidx.wear.protolayout.material.Text
import androidx.wear.protolayout.material.Typography
import androidx.wear.protolayout.material.layouts.PrimaryLayout
import com.google.android.horologist.screenshots.rng.WearDevice
import com.google.android.horologist.screenshots.rng.WearScreenshotTest
import com.google.android.horologist.screenshots.tiles.TileLayoutPreview
import com.google.android.horologist.tiles.images.toImageResource
import com.google.android.horologist.tiles.render.SingleTileLayoutRenderer
import kotlinx.coroutines.runBlocking
import org.junit.Test

class TileScreenshotTest : WearScreenshotTest() {
    override fun testName(suffix: String): String =
        "src/test/screenshots/" +
            "${javaClass.simpleName}_" +
            "${testInfo.methodName}_" +
            (super.device?.id ?: WearDevice.GenericLargeRound.id) +
            "$suffix.png"

    @Composable
    override fun TestScaffold(content: @Composable () -> Unit) {
        content()
    }

    @Test
    fun imageArgb8888() {
        // https://en.wikipedia.org/wiki/File:PNG_transparency_demonstration_1.png
        val bitmap =
            BitmapFactory.decodeFile("src/test/resources/PNG_transparency_demonstration_1.png")

        runTest {
            val context = LocalContext.current

            TileLayoutPreview(
                state = Unit,
                resourceState = Unit,
                renderer = TestImageTileRenderer(
                    context = context,
                    bitmap = bitmap,
                ),
            )
        }
    }

    @Test
    fun imageRgb565() {
        // https://en.wikipedia.org/wiki/File:PNG_transparency_demonstration_1.png
        val bitmap =
            BitmapFactory.decodeFile("src/test/resources/PNG_transparency_demonstration_1.png")
                .copy(Bitmap.Config.RGB_565, false)

        runTest {
            val context = LocalContext.current

            TileLayoutPreview(
                state = Unit,
                resourceState = Unit,
                renderer = TestImageTileRenderer(
                    context = context,
                    bitmap = bitmap,
                ),
            )
        }
    }

    @Test
    fun composable() {
        val capture = RobolectricComposableBitmapRenderer()
        val bitmap = runBlocking {
            capture.renderComposableToBitmap(DpSize(400.dp, 300.dp)) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        drawCircle(androidx.compose.ui.graphics.Color.DarkGray)
                    }
                    FilledIconButton(onClick = {}) {
                        Text("\uD83D\uDC6A")
                    }
                }
            }
        }

        runTest {
            val context = LocalContext.current

            TileLayoutPreview(
                state = Unit,
                resourceState = Unit,
                renderer = TestImageTileRenderer(
                    context = context,
                    bitmap = bitmap.asAndroidBitmap(),
                ),
            )
        }
    }

    class TestImageTileRenderer(
        context: Context,
        val bitmap: Bitmap,
    ) : SingleTileLayoutRenderer<Unit, Unit>(context) {
        override fun renderTile(
            state: Unit,
            deviceParameters: DeviceParameters,
        ): LayoutElementBuilders.LayoutElement {
            return Box.Builder()
                .setHeight(expand())
                .setWidth(expand())
                .setModifiers(
                    Modifiers.Builder()
                        .setBackground(
                            Background.Builder()
                                .setColor(argb(Color.DKGRAY))
                                .build(),
                        )
                        .build(),
                )
                .addContent(
                    PrimaryLayout.Builder(deviceParameters)
                        .setContent(
                            Image.Builder()
                                .setContentScaleMode(CONTENT_SCALE_MODE_FIT)
                                .setResourceId("dice")
                                .setWidth(expand())
                                .setHeight(dp(130f))
                                .build(),
                        )
                        .setPrimaryLabelTextContent(
                            Text.Builder(
                                context,
                                when (bitmap.config) {
                                    Bitmap.Config.ARGB_8888 -> "ARGB_8888"
                                    Bitmap.Config.RGB_565 -> "RGB_565"
                                    else -> "UNDEFINED"
                                },
                            )
                                .setColor(argb(Color.WHITE))
                                .setTypography(Typography.TYPOGRAPHY_BODY2)
                                .build(),
                        ).build(),
                )
                .build()
        }

        override fun ResourceBuilders.Resources.Builder.produceRequestedResources(
            resourceState: Unit,
            deviceParameters: DeviceParameters,
            resourceIds: List<String>,
        ) {
            addIdToImageMapping("dice", bitmap.toImageResource())
        }
    }
}
