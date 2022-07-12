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
import androidx.wear.watchface.complications.data.ListComplicationData
import androidx.wear.watchface.complications.data.LongTextComplicationData
import androidx.wear.watchface.complications.data.MonochromaticImage
import androidx.wear.watchface.complications.data.MonochromaticImageComplicationData
import androidx.wear.watchface.complications.data.NoDataComplicationData
import androidx.wear.watchface.complications.data.PhotoImageComplicationData
import androidx.wear.watchface.complications.data.PlainComplicationText
import androidx.wear.watchface.complications.data.RangedValueComplicationData
import androidx.wear.watchface.complications.data.ShortTextComplicationData
import androidx.wear.watchface.complications.data.SmallImage
import androidx.wear.watchface.complications.data.SmallImageComplicationData
import androidx.wear.watchface.complications.data.SmallImageType

/**
 * A complication provider that support distinct render methods.
 */
public abstract class TypedComplicationTemplate<T>(override val context: Context) :
    ComplicationTemplate<T> {
    final override fun render(type: ComplicationType, data: T): ComplicationData {
        return when (type) {
            ComplicationType.LONG_TEXT -> renderLongText(data) ?: NoDataComplicationData()
            ComplicationType.MONOCHROMATIC_IMAGE -> renderMonochromaticImage(data)
                ?: NoDataComplicationData()
            ComplicationType.PHOTO_IMAGE -> renderPhotoImage(data) ?: NoDataComplicationData()
            ComplicationType.RANGED_VALUE -> renderRangedValue(data) ?: NoDataComplicationData()
            ComplicationType.SHORT_TEXT -> renderShortText(data) ?: NoDataComplicationData()
            ComplicationType.SMALL_IMAGE -> renderSmallImage(data) ?: NoDataComplicationData()
            ComplicationType.LIST -> renderList(data) ?: NoDataComplicationData()
            else -> NoDataComplicationData()
        }
    }

    override fun supportedTypes(): List<ComplicationType> = listOf(ComplicationType.SHORT_TEXT)

    public fun icon(@DrawableRes id: Int): Icon = Icon.createWithResource(context, id)

    public fun text(@StringRes id: Int): String = context.getText(id).toString()

    public abstract fun renderShortText(data: T): ShortTextComplicationData?

    public fun shortText(
        title: String,
        text: String,
        @DrawableRes icon: Int?,
        launchIntent: PendingIntent?
    ): ShortTextComplicationData = ShortTextComplicationData.Builder(
        PlainComplicationText.Builder(text)
            .build(),
        PlainComplicationText.Builder(
            text = text
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

    public open fun renderSmallImage(data: T): SmallImageComplicationData? = null

    public fun smallImage(
        icon: Icon,
        type: SmallImageType = SmallImageType.PHOTO,
        name: String,
        launchIntent: PendingIntent?
    ): SmallImageComplicationData = SmallImageComplicationData.Builder(
        smallImage = SmallImage.Builder(
            image = icon,
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

    public open fun renderList(data: T): ListComplicationData? = null

    public open fun renderLongText(data: T): LongTextComplicationData? = null

    public fun longText(
        icon: Icon?,
        type: SmallImageType = SmallImageType.PHOTO,
        title: String,
        text: String,
        launchIntent: PendingIntent?
    ): LongTextComplicationData = LongTextComplicationData.Builder(
        text = PlainComplicationText.Builder(
            text = text,
        )
            .build(),
        contentDescription = PlainComplicationText.Builder(
            text = text
        )
            .build()
    )
        .apply {
            if (icon != null) {
                setSmallImage(
                    SmallImage.Builder(
                        image = icon,
                        type = type
                    ).build()
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

    public open fun renderMonochromaticImage(data: T): MonochromaticImageComplicationData? = null

    public open fun renderPhotoImage(data: T): PhotoImageComplicationData? = null

    public open fun renderRangedValue(data: T): RangedValueComplicationData? = null
}
