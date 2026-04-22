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

package com.google.android.horologist.tiles.complication

import android.app.PendingIntent
import android.graphics.drawable.Icon
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.wear.watchface.complications.data.ComplicationText
import androidx.wear.watchface.complications.data.LongTextComplicationData
import androidx.wear.watchface.complications.data.MonochromaticImage
import androidx.wear.watchface.complications.data.PhotoImageComplicationData
import androidx.wear.watchface.complications.data.PlainComplicationText
import androidx.wear.watchface.complications.data.RangedValueComplicationData
import androidx.wear.watchface.complications.data.ShortTextComplicationData
import androidx.wear.watchface.complications.data.SmallImage
import androidx.wear.watchface.complications.data.SmallImageComplicationData
import androidx.wear.watchface.complications.data.SmallImageType
import com.google.android.horologist.annotations.ExperimentalHorologistApi

@ExperimentalHorologistApi
public object DataTemplates {
    public fun ComplicationTemplate<*>.longText(
        icon: Icon?,
        type: SmallImageType = SmallImageType.PHOTO,
        title: String?,
        text: String,
        launchIntent: PendingIntent?,
        contentDescription: ComplicationText? = null,
    ): LongTextComplicationData = LongTextComplicationData.Builder(
        text = PlainComplicationText.Builder(
            text = text,
        )
            .build(),
        contentDescription = contentDescription ?: PlainComplicationText.Builder(
            text = text,
        )
            .build(),
    )
        .apply {
            if (icon != null) {
                setSmallImage(
                    SmallImage.Builder(
                        image = icon,
                        type = type,
                    ).build(),
                )
            }
            if (title != null) {
                setTitle(
                    PlainComplicationText.Builder(
                        text = title,
                    )
                        .build(),
                )
            }
        }
        .setTapAction(launchIntent)
        .build()

    public fun ComplicationTemplate<*>.smallImage(
        icon: Icon,
        type: SmallImageType = SmallImageType.PHOTO,
        name: String,
        launchIntent: PendingIntent?,
        contentDescription: ComplicationText? = null,
    ): SmallImageComplicationData = SmallImageComplicationData.Builder(
        smallImage = SmallImage.Builder(
            image = icon,
            type = type,
        )
            .build(),
        contentDescription = contentDescription ?: PlainComplicationText.Builder(
            text = name,
        )
            .build(),
    )
        .setTapAction(launchIntent)
        .build()

    public fun ComplicationTemplate<*>.shortText(
        title: String?,
        text: String,
        @DrawableRes icon: Int?,
        launchIntent: PendingIntent?,
        contentDescription: ComplicationText? = null,
    ): ShortTextComplicationData = ShortTextComplicationData.Builder(
        PlainComplicationText.Builder(text)
            .build(),
        contentDescription = contentDescription ?: PlainComplicationText.Builder(text = text)
            .build(),
    )
        .apply {
            if (icon != null) {
                setMonochromaticImage(
                    MonochromaticImage.Builder(icon(icon))
                        .build(),
                )
            }
            if (title != null) {
                setTitle(
                    PlainComplicationText.Builder(
                        text = title,
                    )
                        .build(),
                )
            }
        }
        .setTapAction(launchIntent)
        .build()

    public fun ComplicationTemplate<*>.photoImage(
        photoImage: Icon,
        name: String,
        launchIntent: PendingIntent?,
        contentDescription: ComplicationText? = null,
    ): PhotoImageComplicationData = PhotoImageComplicationData.Builder(
        photoImage,
        contentDescription = contentDescription ?: PlainComplicationText.Builder(
            text = name,
        )
            .build(),
    )
        .setTapAction(launchIntent)
        .build()

    public fun ComplicationTemplate<*>.rangedValue(
        value: Float,
        min: Float,
        max: Float,
        title: String,
        text: String,
        image: MonochromaticImage?,
        launchIntent: PendingIntent?,
    ): RangedValueComplicationData = RangedValueComplicationData.Builder(
        value,
        min,
        max,
        PlainComplicationText.Builder(text = text).build(),
    )
        .setText(PlainComplicationText.Builder(text = text).build())
        .setTitle(PlainComplicationText.Builder(text = title).build())
        .setTapAction(launchIntent)
        .setMonochromaticImage(image)
        .build()

    public fun ComplicationTemplate<*>.icon(
        @DrawableRes id: Int,
    ): Icon = Icon.createWithResource(
        context,
        id,
    )

    public fun ComplicationTemplate<*>.text(
        @StringRes id: Int,
    ): String = context.getText(id)
        .toString()
}
