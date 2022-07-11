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

package com.google.android.horologist.media.ui.complication

import android.app.PendingIntent
import android.content.Context
import android.graphics.drawable.Icon
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.wear.watchface.complications.data.ComplicationType
import androidx.wear.watchface.complications.data.EmptyComplicationData
import androidx.wear.watchface.complications.data.LongTextComplicationData
import androidx.wear.watchface.complications.data.NoDataComplicationData
import androidx.wear.watchface.complications.data.ShortTextComplicationData
import androidx.wear.watchface.complications.data.SmallImageComplicationData
import androidx.wear.watchface.complications.data.SmallImageType
import com.google.android.horologist.media.ui.R
import com.google.android.horologist.tiles.complication.TypedComplicationRenderer

class MediaStatusTemplate(
    context: Context,
) :
    TypedComplicationRenderer<MediaStatusTemplate.Data>(context) {

    data class Data(
        @DrawableRes val appIconRes: Int? = null,
        val icon: Icon? = null,
        val type: SmallImageType,
        val name: String,
        val launchIntent: PendingIntent?
    )

    override fun previewData(): Data = Data(
        name = "All",
        appIconRes = R.drawable.ic_baseline_queue_music_24,
        type = SmallImageType.ICON,
        launchIntent = null,
    )

    override fun supportedTypes(): List<ComplicationType> =
        listOf(
            ComplicationType.SMALL_IMAGE,
            ComplicationType.SHORT_TEXT,
            ComplicationType.LONG_TEXT
        )

    override fun renderShortText(data: Data): ShortTextComplicationData =
        shortText(
            name = data.name,
            icon = data.appIconRes,
            launchIntent = data.launchIntent
        )

    override fun renderSmallImage(data: Data): SmallImageComplicationData? {
        if (data.icon == null) {
            return null
        }

        return smallImage(
                icon = data.icon,
                type = data.type,
                name = data.name,
                launchIntent = data.launchIntent
            )
    }

    override fun renderLongText(data: Data): LongTextComplicationData {
        return longText(
            icon = data.icon,
            type = data.type,
            name = data.name,
            launchIntent = data.launchIntent
        )
    }
}
