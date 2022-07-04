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

@file:OptIn(ComplicationExperimental::class)

package com.google.android.horologist.tiles.complication

import android.app.PendingIntent
import android.content.Context
import android.graphics.drawable.Icon
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.wear.watchface.complications.data.ComplicationData
import androidx.wear.watchface.complications.data.ComplicationExperimental
import androidx.wear.watchface.complications.data.ComplicationType
import androidx.wear.watchface.complications.data.EmptyComplicationData
import androidx.wear.watchface.complications.data.ListComplicationData
import androidx.wear.watchface.complications.data.LongTextComplicationData
import androidx.wear.watchface.complications.data.MonochromaticImage
import androidx.wear.watchface.complications.data.MonochromaticImageComplicationData
import androidx.wear.watchface.complications.data.PhotoImageComplicationData
import androidx.wear.watchface.complications.data.PlainComplicationText
import androidx.wear.watchface.complications.data.ProtoLayoutComplicationData
import androidx.wear.watchface.complications.data.RangedValueComplicationData
import androidx.wear.watchface.complications.data.ShortTextComplicationData
import androidx.wear.watchface.complications.data.SmallImage
import androidx.wear.watchface.complications.data.SmallImageComplicationData
import androidx.wear.watchface.complications.data.SmallImageType

/**
 * A complication provider that support small images. Upon tapping on the app icon,
 * the complication will launch the controls screen.
 */
public abstract class TypedComplicationRenderer<T>(override val context: Context) :
    ComplicationRenderer<T> {
    final override fun render(type: ComplicationType, data: T): ComplicationData {
        return when (type) {
            ComplicationType.LONG_TEXT -> renderLongText(data) ?: EmptyComplicationData()
            ComplicationType.MONOCHROMATIC_IMAGE -> renderMonochromaticImage(data)
                ?: EmptyComplicationData()
            ComplicationType.PHOTO_IMAGE -> renderPhotoImage(data) ?: EmptyComplicationData()
            ComplicationType.RANGED_VALUE -> renderRangedValue(data) ?: EmptyComplicationData()
            ComplicationType.SHORT_TEXT -> renderShortText(data) ?: EmptyComplicationData()
            ComplicationType.SMALL_IMAGE -> renderSmallImage(data) ?: EmptyComplicationData()
            ComplicationType.PROTO_LAYOUT -> renderProtoLayout(data) ?: EmptyComplicationData()
            ComplicationType.LIST -> renderList(data) ?: EmptyComplicationData()
            else -> EmptyComplicationData()
        }
    }

    override fun supportedTypes(): List<ComplicationType> = listOf(
        ComplicationType.LONG_TEXT, ComplicationType.MONOCHROMATIC_IMAGE,
        ComplicationType.PHOTO_IMAGE, ComplicationType.RANGED_VALUE,
        ComplicationType.SHORT_TEXT, ComplicationType.SMALL_IMAGE
    )

    public fun icon(@DrawableRes id: Int): Icon = Icon.createWithResource(context, id)

    public fun text(@StringRes id: Int): String = context.getText(id).toString()

    public abstract fun renderShortText(data: T): ShortTextComplicationData?

    public fun shortText(
        name: String,
        title: String,
        @DrawableRes icon: Int?,
        launchIntent: PendingIntent?
    ) = ShortTextComplicationData.Builder(
        PlainComplicationText.Builder(name)
            .build(),
        PlainComplicationText.Builder(
            text = name
        ).build()
    )
        .apply {
            if (icon != null) {
                setMonochromaticImage(
                    MonochromaticImage.Builder(icon(icon))
                        .build()
                )
            }
        }
        .setTitle(
            PlainComplicationText.Builder(
                text = title,
            )
                .build()
        )
        .setTapAction(launchIntent)
        .build()

    public abstract fun renderSmallImage(data: T): SmallImageComplicationData?

    public fun smallImage(
        @DrawableRes icon: Int,
        type: SmallImageType = SmallImageType.PHOTO,
        name: String,
        launchIntent: PendingIntent?
    ) = SmallImageComplicationData.Builder(
        smallImage = SmallImage.Builder(
            image = icon(icon),
            type = type
        )
            .build(),
        contentDescription = PlainComplicationText.Builder(
            text = name
        )
            .build()
    )
        .setTapAction(launchIntent)
        .build()

    open fun renderList(data: T): ListComplicationData? = null

    open fun renderLongText(data: T): LongTextComplicationData? = null

    public fun longText(
        @DrawableRes icon: Int,
        type: SmallImageType = SmallImageType.PHOTO,
        name: String,
        title: String,
        launchIntent: PendingIntent?
    ) = LongTextComplicationData.Builder(
        text = PlainComplicationText.Builder(
            text = name,
        )
            .build(),
        contentDescription = PlainComplicationText.Builder(
            text = name
        )
            .build()
    )
        .setSmallImage(
            SmallImage.Builder(
                image = icon(icon),
                type = type
            ).build()
        )
        .setTitle(
            PlainComplicationText.Builder(
                text = title,
            )
                .build()
        )
        .setTapAction(launchIntent)
        .build()

    open fun renderMonochromaticImage(data: T): MonochromaticImageComplicationData? = null

    open fun renderPhotoImage(data: T): PhotoImageComplicationData? = null

    open fun renderRangedValue(data: T): RangedValueComplicationData? = null

    open fun renderProtoLayout(data: T): ProtoLayoutComplicationData? = null
}
