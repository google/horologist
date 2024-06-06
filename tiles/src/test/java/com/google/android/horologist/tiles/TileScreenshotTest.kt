@file:Suppress("UnstableApiUsage", "DEPRECATION")

package com.google.android.horologist.tiles

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
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
import androidx.wear.protolayout.ResourceBuilders.IMAGE_FORMAT_ARGB_8888
import androidx.wear.protolayout.ResourceBuilders.ImageFormat
import androidx.wear.protolayout.material.Text
import androidx.wear.protolayout.material.Typography
import androidx.wear.protolayout.material.layouts.PrimaryLayout
import com.google.android.horologist.compose.tools.TileLayoutPreview
import com.google.android.horologist.screenshots.rng.WearDevice
import com.google.android.horologist.screenshots.rng.WearScreenshotTest
import com.google.android.horologist.tiles.images.toImageResource
import com.google.android.horologist.tiles.render.SingleTileLayoutRenderer
import org.junit.Test

class TileScreenshotTest : WearScreenshotTest() {

    override fun testName(suffix: String): String =
        "src/test/screenshots/" +
            "${javaClass.simpleName}_" +
            "${testInfo.methodName}_" +
            "${super.device?.id ?: WearDevice.GenericLargeRound.id}" +
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
                    // With transparency
                    format = ResourceBuilders.IMAGE_FORMAT_ARGB_8888
                )
            )
        }
    }

    @Test
    fun imageRgb565() {
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
                    // Opaque background
                    format = ResourceBuilders.IMAGE_FORMAT_RGB_565
                )
            )
        }
    }

    class TestImageTileRenderer(
        context: Context,
        val bitmap: Bitmap,
        @ImageFormat val format: Int
    ) : SingleTileLayoutRenderer<Unit, Unit>(context) {
        override fun renderTile(
            state: Unit,
            deviceParameters: DeviceParameters
        ): LayoutElementBuilders.LayoutElement {
            return Box.Builder()
                .setHeight(expand())
                .setWidth(expand())
                .setModifiers(
                    Modifiers.Builder()
                        .setBackground(
                            Background.Builder()
                                .setColor(argb(Color.DKGRAY))
                                .build()
                        )
                        .build()
                )
                .addContent(
                    PrimaryLayout.Builder(deviceParameters)
                        .setContent(
                            Image.Builder()
                                .setContentScaleMode(CONTENT_SCALE_MODE_FIT)
                                .setResourceId("dice")
                                .setWidth(expand())
                                .setHeight(dp(130f))
                                .build()
                        )
                        .setPrimaryLabelTextContent(
                            Text.Builder(
                                context,
                                if (format == IMAGE_FORMAT_ARGB_8888) "ARGB_8888" else "RGB_565"
                            )
                                .setColor(argb(Color.WHITE))
                                .setTypography(Typography.TYPOGRAPHY_BODY2)
                                .build()
                        ).build()
                )
                .build()
        }

        override fun ResourceBuilders.Resources.Builder.produceRequestedResources(
            resourceState: Unit,
            deviceParameters: DeviceParameters,
            resourceIds: List<String>
        ) {
            addIdToImageMapping("dice", bitmap.toImageResource(format))
        }
    }
}