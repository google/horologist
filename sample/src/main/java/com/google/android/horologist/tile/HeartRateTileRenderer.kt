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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.wear.compose.ui.tooling.preview.WearPreviewLargeRound
import androidx.wear.protolayout.ColorBuilders
import androidx.wear.protolayout.DeviceParametersBuilders.DeviceParameters
import androidx.wear.protolayout.DimensionBuilders
import androidx.wear.protolayout.DimensionBuilders.AngularLayoutConstraint
import androidx.wear.protolayout.LayoutElementBuilders
import androidx.wear.protolayout.ModifiersBuilders
import androidx.wear.protolayout.ResourceBuilders
import androidx.wear.protolayout.ResourceBuilders.Resources
import androidx.wear.protolayout.StateBuilders
import androidx.wear.protolayout.TypeBuilders
import androidx.wear.protolayout.TypeBuilders.StringLayoutConstraint
import androidx.wear.protolayout.expression.AnimationParameterBuilders
import androidx.wear.protolayout.expression.DynamicBuilders
import androidx.wear.protolayout.expression.DynamicBuilders.DynamicFloat
import androidx.wear.protolayout.expression.StateEntryBuilders
import com.google.android.horologist.compose.tools.TileLayoutPreview
import com.google.android.horologist.sample.R
import com.google.android.horologist.tiles.render.SingleTileLayoutRenderer


class HeartRateTileRenderer(context: Context) :
    SingleTileLayoutRenderer<Unit, Unit>(
        context
    ) {
    override fun renderTile(
        state: Unit, deviceParameters: DeviceParameters
    ): LayoutElementBuilders.LayoutElement {
        return margin(
            stack(
                staticArc(0f, 360f, 8f, Color.GRAY),
                animatedArc,
                animatedDot,
                body
            ),
            3
        )
    }

    private val animationSpec = AnimationParameterBuilders.AnimationSpec.Builder()
        .setAnimationParameters(
            AnimationParameterBuilders.AnimationParameters.Builder()
                .setDurationMillis(ANIMATION_DURATION_MILLIS)
                .build()
        )
        .build()

    private val body: LayoutElementBuilders.LayoutElement = column(
        staticText("Anchor angle"),
        getDynamicText(STATE_KEY_ANCHOR),
        staticText("Arc Length"),
        getDynamicText(STATE_KEY_LENGTH),
        staticText("Indication position"),
        getDynamicText(STATE_KEY_DOT_POSITION)
    )

    private fun getDynamicText(key: String): LayoutElementBuilders.Text {
        return LayoutElementBuilders.Text.Builder()
            .setText(
                dynamicString(
                    DynamicFloat.animate(key, animationSpec).asInt().format(),
                    "Empty",
                    LayoutElementBuilders.TEXT_ALIGN_CENTER
                )
            )
            .setLayoutConstraintsForDynamicText(StringLayoutConstraint.Builder("XXXXXX").build())
            .build()
    }

    private val animatedArc: LayoutElementBuilders.Arc = createArc(
        STATE_KEY_ANCHOR,
        STATE_KEY_LENGTH, Color.RED
    )
    private val animatedDot: LayoutElementBuilders.Arc = createArc(
        STATE_KEY_DOT_POSITION,
        STATE_KEY_DOT_LENGTH, Color.WHITE
    )

    private fun createArc(
        anchorAngleKey: String,
        lengthKey: String,
        color: Int
    ): LayoutElementBuilders.Arc {
        return LayoutElementBuilders.Arc.Builder()
            .setAnchorType(LayoutElementBuilders.ARC_ANCHOR_START)
            .setAnchorAngle(dynamicDegrees(DynamicFloat.animate(anchorAngleKey, animationSpec)))
            .addContent(
                LayoutElementBuilders.ArcLine.Builder()
                    .setColor(ColorBuilders.argb(color))
                    .setLength(
                        dynamicDegrees(
                            DynamicFloat.animate(lengthKey, animationSpec),
                            360f,
                            LayoutElementBuilders.ANGULAR_ALIGNMENT_START
                        )
                    )
                    .setThickness(DimensionBuilders.dp(8f))
                    .setLayoutConstraintsForDynamicLength(
                        AngularLayoutConstraint.Builder(360f).build()
                    )
                    .build()
            )
            .setLayoutConstraintsForDynamicAnchorAngle(
                AngularLayoutConstraint.Builder(360f).build()
            )
            .build()
    }

    override fun createState(): StateBuilders.State {

        // Animate between -60 and 60
        val arcAnchor = Math.random().toFloat() * 160f - 100
        // Animate between 40 and 140
        val arcLength = Math.random().toFloat() * 100f + 40f
        // Position within the arc
        val indicatorAnchor = Math.random().toFloat() * arcLength + arcAnchor
        val indicatorLength = (0.1 + Math.random() * 0.01).toFloat()
        return StateBuilders.State.Builder()
            .addIdToValueMapping(
                STATE_KEY_ANCHOR,
                StateEntryBuilders.StateEntryValue.fromFloat(arcAnchor)
            )
            .addIdToValueMapping(
                STATE_KEY_LENGTH,
                StateEntryBuilders.StateEntryValue.fromFloat(arcLength)
            )
            .addIdToValueMapping(
                STATE_KEY_DOT_POSITION,
                StateEntryBuilders.StateEntryValue.fromFloat(indicatorAnchor)
            )
            .addIdToValueMapping(
                STATE_KEY_DOT_LENGTH,
                StateEntryBuilders.StateEntryValue.fromFloat(indicatorLength)
            )
            .build()
    }

    override fun Resources.Builder.produceRequestedResources(
        resourceState: Unit,
        deviceParameters: DeviceParameters,
        resourceIds: MutableList<String>
    ) {
        addIdToImageMapping(
            "main_icon",
            ResourceBuilders.ImageResource.Builder()
                .setAndroidResourceByResId(
                    ResourceBuilders.AndroidImageResourceByResId.Builder()
                        .setResourceId(R.drawable.ic_android)
                        .build()
                )
                .build()
        )
    }


    fun margin(
        inner: LayoutElementBuilders.LayoutElement?,
        all: Int
    ): LayoutElementBuilders.Box {
        return margin(inner, all, all, all, all)
    }

    fun margin(
        inner: LayoutElementBuilders.LayoutElement?,
        start: Int,
        end: Int,
        top: Int,
        bottom: Int
    ): LayoutElementBuilders.Box {
        return LayoutElementBuilders.Box.Builder().addContent(inner!!).setHeight(
            DimensionBuilders.expand()
        ).setWidth(DimensionBuilders.expand()).setModifiers(
            ModifiersBuilders.Modifiers.Builder().setPadding(
                ModifiersBuilders.Padding.Builder().setStart(
                    DimensionBuilders.dp(start.toFloat())
                ).setEnd(DimensionBuilders.dp(end.toFloat())).setTop(
                    DimensionBuilders.dp(top.toFloat())
                ).setBottom(DimensionBuilders.dp(bottom.toFloat())).build()
            ).build()
        ).build()
    }

    fun stack(vararg elements: LayoutElementBuilders.LayoutElement?): LayoutElementBuilders.LayoutElement {
        val builder = LayoutElementBuilders.Box.Builder().setHorizontalAlignment(
            LayoutElementBuilders.HORIZONTAL_ALIGN_CENTER
        ).setVerticalAlignment(
            LayoutElementBuilders.VERTICAL_ALIGN_CENTER
        ).setHeight(DimensionBuilders.expand()).setWidth(
            DimensionBuilders.expand()
        )
        for (element in elements) {
            builder.addContent(element!!)
        }
        return builder.build()
    }

    fun staticArc(
        start: Float,
        length: Float,
        thickness: Float,
        color: Int
    ): LayoutElementBuilders.Arc {
        return LayoutElementBuilders.Arc.Builder()
            .setAnchorAngle(DimensionBuilders.degrees(start)).setAnchorType(
                LayoutElementBuilders.ARC_ANCHOR_START
            ).addContent(
                LayoutElementBuilders.ArcLine.Builder().setColor(
                    ColorBuilders.argb(color)
                ).setLength(DimensionBuilders.degrees(length)).setThickness(
                    DimensionBuilders.dp(thickness)
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

    fun row(vararg elements: LayoutElementBuilders.LayoutElement?): LayoutElementBuilders.LayoutElement {
        val builder = LayoutElementBuilders.Row.Builder()
        builder.setHeight(DimensionBuilders.expand())
        for (element in elements) {
            builder.addContent(element!!)
        }
        return builder.build()
    }

    fun spacer(width: Float, height: Float): LayoutElementBuilders.Spacer {
        return LayoutElementBuilders.Spacer.Builder().setHeight(DimensionBuilders.dp(height))
            .setWidth(
                DimensionBuilders.dp(width)
            ).build()
    }

    fun staticText(text: String?): LayoutElementBuilders.LayoutElement {
        return LayoutElementBuilders.Text.Builder().setText(staticString(text)!!).build()
    }

    fun staticText(
        text: String?,
        fontSize: Float,
        color: Int
    ): LayoutElementBuilders.LayoutElement {
        return LayoutElementBuilders.Text.Builder().setText(staticString(text)!!).setFontStyle(
            LayoutElementBuilders.FontStyle.Builder().setSize(DimensionBuilders.sp(fontSize))
                .setColor(
                    ColorBuilders.argb(color)
                ).build()
        ).build()
    }

    fun box(
        width: Int,
        height: Int,
        element: LayoutElementBuilders.LayoutElement?
    ): LayoutElementBuilders.Box {
        return LayoutElementBuilders.Box.Builder()
            .setHeight(DimensionBuilders.dp(height.toFloat())).setWidth(
                DimensionBuilders.dp(width.toFloat())
            ).addContent(
                element!!
            ).build()
    }

    fun staticString(value: String?): TypeBuilders.StringProp {
        return TypeBuilders.StringProp.Builder(value!!).build()
    }

    fun dynamicString(
        text: DynamicBuilders.DynamicString?,
        valueForLayout: String?,
        align: Int
    ): TypeBuilders.StringProp {
        return TypeBuilders.StringProp.Builder("No Data").setDynamicValue(
            text!!
        ) //        .setValueForLayout(valueForLayout)
            //        .setTextAlignmentForLayout(align)
            .build()
    }

    fun dynamicColor(value: DynamicBuilders.DynamicColor?): ColorBuilders.ColorProp {
        return ColorBuilders.ColorProp.Builder(Color.WHITE).setDynamicValue(
            value!!
        ).build()
    }

    fun dynamicDegrees(
        value: DynamicFloat?,
        valueForLayout: Float = 360f,
        align: Int = LayoutElementBuilders.ANGULAR_ALIGNMENT_START
    ): DimensionBuilders.DegreesProp {
        return DimensionBuilders.DegreesProp.Builder(0f).setValue(0f).setDynamicValue(
            value!!
        ) //        .setValueForLayout(valueForLayout)
            //        .setAngularAlignmentForLayout(align)
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
        HeartRateTileRenderer(context)
    }

    TileLayoutPreview(
        Unit,
        Unit,
        renderer
    )
}