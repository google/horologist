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

package com.google.android.horologist.tile

import android.content.Context
import android.graphics.Color
import android.graphics.Color.RED
import android.graphics.Color.WHITE
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.wear.compose.ui.tooling.preview.WearPreviewLargeRound
import androidx.wear.protolayout.ColorBuilders
import androidx.wear.protolayout.DeviceParametersBuilders.DeviceParameters
import androidx.wear.protolayout.DimensionBuilders.*
import androidx.wear.protolayout.LayoutElementBuilders
import androidx.wear.protolayout.LayoutElementBuilders.ArcLine
import androidx.wear.protolayout.ModifiersBuilders
import androidx.wear.protolayout.ResourceBuilders
import androidx.wear.protolayout.ResourceBuilders.Resources
import androidx.wear.protolayout.TypeBuilders
import androidx.wear.protolayout.TypeBuilders.StringLayoutConstraint
import androidx.wear.protolayout.expression.AnimationParameterBuilders.*
import androidx.wear.protolayout.expression.DynamicBuilders
import androidx.wear.protolayout.expression.DynamicBuilders.DynamicFloat
import com.google.android.horologist.compose.tools.TileLayoutPreview
import com.google.android.horologist.sample.R
import com.google.android.horologist.tiles.render.SingleTileLayoutRenderer
import kotlin.time.Duration.Companion.seconds


class SampleAnimatedTileRenderer(context: Context) : SingleTileLayoutRenderer<Unit, Unit>(
    context
) {
    override val freshnessIntervalMillis = FRESHNESS_MILLIS.toLong()

    override fun renderTile(
        state: Unit, deviceParameters: DeviceParameters
    ): LayoutElementBuilders.LayoutElement {
        return margin(
            stack(
                staticArc(0f, 360f, 8f, Color.GRAY), createArc(), body
            ), 3
        )
    }

    private val animationSpec =
        AnimationSpec.Builder().setAnimationParameters(
            AnimationParameters.Builder()
                .setDurationMillis(ANIMATION_DURATION_MILLIS).build()
        ).build()

    private val body: LayoutElementBuilders.LayoutElement = column(
        staticText("Anchor angle"),
        getDynamicText(STATE_KEY_ANCHOR),
        staticText("Arc Length"),
        getDynamicText(STATE_KEY_LENGTH),
        staticText("Indication position"),
        getDynamicText(STATE_KEY_DOT_POSITION)
    )

    private fun getDynamicText(key: String): LayoutElementBuilders.Text {
        return LayoutElementBuilders.Text.Builder().setText(
            dynamicString(
                DynamicFloat.animate(key, animationSpec).asInt().format(),
                "Empty",
                LayoutElementBuilders.TEXT_ALIGN_CENTER
            )
        ).setLayoutConstraintsForDynamicText(StringLayoutConstraint.Builder("XXXXXX").build())
            .build()
    }

    private fun createArc(
    ): LayoutElementBuilders.Arc {
        return LayoutElementBuilders.Arc.Builder().addContent(
            ArcLine.Builder().setColor(
                ColorBuilders.ColorProp.Builder(WHITE).setDynamicValue(
                    DynamicBuilders.DynamicColor.animate(
                        WHITE, RED, AnimationSpec.Builder().setInfiniteRepeatable(
                        REPEAT_MODE_REVERSE
                    ).build()
                    )
                ).build()
            ).setLength(
                DegreesProp.Builder(100f).setDynamicValue(
                    DynamicFloat.animate(
                        0f, 360f, AnimationSpec.Builder().setAnimationParameters(
                        AnimationParameters.Builder()
                            .setDurationMillis(20.seconds.inWholeMilliseconds.toInt())
                            .build()
                    ).build()
                    )
                ).build()
            ).setThickness(dp(10f)).setLayoutConstraintsForDynamicLength(
                AngularLayoutConstraint.Builder(360f).build()
            ).build()
        ).build()
    }

    override fun Resources.Builder.produceRequestedResources(
        resourceState: Unit, deviceParameters: DeviceParameters, resourceIds: MutableList<String>
    ) {
        addIdToImageMapping(
            "main_icon", ResourceBuilders.ImageResource.Builder().setAndroidResourceByResId(
            ResourceBuilders.AndroidImageResourceByResId.Builder()
                .setResourceId(R.drawable.ic_android).build()
        ).build()
        )
    }


    fun margin(
        inner: LayoutElementBuilders.LayoutElement?, all: Int
    ): LayoutElementBuilders.Box {
        return margin(inner, all, all, all, all)
    }

    fun margin(
        inner: LayoutElementBuilders.LayoutElement?, start: Int, end: Int, top: Int, bottom: Int
    ): LayoutElementBuilders.Box {
        return LayoutElementBuilders.Box.Builder().addContent(inner!!).setHeight(
            expand()
        ).setWidth(expand()).setModifiers(
            ModifiersBuilders.Modifiers.Builder().setPadding(
                ModifiersBuilders.Padding.Builder().setStart(
                    dp(start.toFloat())
                ).setEnd(dp(end.toFloat())).setTop(
                    dp(top.toFloat())
                ).setBottom(dp(bottom.toFloat())).build()
            ).build()
        ).build()
    }

    fun stack(vararg elements: LayoutElementBuilders.LayoutElement?): LayoutElementBuilders.LayoutElement {
        val builder = LayoutElementBuilders.Box.Builder().setHorizontalAlignment(
            LayoutElementBuilders.HORIZONTAL_ALIGN_CENTER
        ).setVerticalAlignment(
            LayoutElementBuilders.VERTICAL_ALIGN_CENTER
        ).setHeight(expand()).setWidth(
            expand()
        )
        for (element in elements) {
            builder.addContent(element!!)
        }
        return builder.build()
    }

    fun staticArc(
        start: Float, length: Float, thickness: Float, color: Int
    ): LayoutElementBuilders.Arc {
        return LayoutElementBuilders.Arc.Builder().setAnchorAngle(degrees(start))
            .setAnchorType(
                LayoutElementBuilders.ARC_ANCHOR_START
            ).addContent(
                ArcLine.Builder().setColor(
                    ColorBuilders.argb(color)
                ).setLength(degrees(length)).setThickness(
                    dp(thickness)
                ).build()
            ).build()
    }

    fun column(vararg elements: LayoutElementBuilders.LayoutElement?): LayoutElementBuilders.LayoutElement {
        val builder = LayoutElementBuilders.Column.Builder()
        for (element in elements) {
            builder.addContent(element!!)
        }
        return builder.build()
    }

    fun staticText(text: String?): LayoutElementBuilders.LayoutElement {
        return LayoutElementBuilders.Text.Builder().setText(staticString(text)!!).build()
    }

    fun box(
        width: Int, height: Int, element: LayoutElementBuilders.LayoutElement?
    ): LayoutElementBuilders.Box {
        return LayoutElementBuilders.Box.Builder().setHeight(dp(height.toFloat())).setWidth(
            dp(width.toFloat())
        ).addContent(
            element!!
        ).build()
    }

    fun staticString(value: String?): TypeBuilders.StringProp {
        return TypeBuilders.StringProp.Builder(value!!).build()
    }

    fun dynamicString(
        text: DynamicBuilders.DynamicString?, valueForLayout: String?, align: Int
    ): TypeBuilders.StringProp {
        return TypeBuilders.StringProp.Builder("No Data").setDynamicValue(
            text!!
        ) //        .setValueForLayout(valueForLayout)
            //        .setTextAlignmentForLayout(align)
            .build()
    }

    companion object {
        private const val RESOURCES_VERSION = "1"
        private const val STATE_KEY_ANCHOR = "anchor"
        private const val STATE_KEY_LENGTH = "length"
        private const val STATE_KEY_DOT_POSITION = "dot"
        private const val STATE_KEY_DOT_LENGTH = "dotlength"
        private const val FRESHNESS_MILLIS = 2000
        const val ANIMATION_DURATION_MILLIS = 1000
    }
}


@WearPreviewLargeRound
@Composable
fun HeartrateTilePreview() {
    val context = LocalContext.current

    val renderer = remember {
        SampleAnimatedTileRenderer(context)
    }

    TileLayoutPreview(
        Unit, Unit, renderer
    )
}