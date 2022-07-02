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

package com.google.android.horologist.media3.complication

import android.app.PendingIntent
import android.content.Context
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.wear.watchface.complications.data.ComplicationType
import androidx.wear.watchface.complications.data.LongTextComplicationData
import androidx.wear.watchface.complications.data.ShortTextComplicationData
import androidx.wear.watchface.complications.data.SmallImageComplicationData
import androidx.wear.watchface.complications.data.SmallImageType
import com.google.android.horologist.media3.R
import com.google.android.horologist.tiles.complication.TypedComplicationRenderer

class AppLaunchTemplate(context: Context) :
    TypedComplicationRenderer<AppLaunchTemplate.Data>(context) {
    data class Data(
        @StringRes val appName: Int,
        @DrawableRes val appIcon: Int,
        @DrawableRes val appImage: Int?,
        val launchIntent: PendingIntent?
    )

    override fun previewData(): Data = Data(
        appName = R.string.horologist_sample_name,
        appIcon = R.drawable.ic_baseline_queue_music_24,
        appImage = R.drawable.ic_uamp,
        launchIntent = null
    )

    override fun supportedTypes(): List<ComplicationType> =
        listOf(
            ComplicationType.SMALL_IMAGE,
            ComplicationType.SHORT_TEXT,
            ComplicationType.LONG_TEXT
        )

    override fun renderShortText(data: Data): ShortTextComplicationData =
        shortText(text(data.appName), data.appIcon, data.launchIntent)

    override fun renderSmallImage(data: Data): SmallImageComplicationData {
        return if (data.appImage != null) {
            smallImage(
                data.appImage,
                SmallImageType.PHOTO,
                text(data.appName),
                data.launchIntent
            )
        } else {
            smallImage(
                data.appIcon,
                SmallImageType.ICON,
                text(data.appName),
                data.launchIntent
            )
        }
    }

    override fun renderLongText(data: Data): LongTextComplicationData {
        return if (data.appImage != null) {
            longText(
                data.appImage,
                SmallImageType.PHOTO,
                text(data.appName),
                data.launchIntent
            )
        } else {
            longText(
                data.appIcon,
                SmallImageType.ICON,
                text(data.appName),
                data.launchIntent
            )
        }
    }
}
