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
import androidx.wear.watchface.complications.data.ComplicationText
import androidx.wear.watchface.complications.data.ComplicationType
import androidx.wear.watchface.complications.data.LongTextComplicationData
import androidx.wear.watchface.complications.data.PhotoImageComplicationData
import androidx.wear.watchface.complications.data.ShortTextComplicationData
import androidx.wear.watchface.complications.data.SmallImageComplicationData
import androidx.wear.watchface.complications.data.SmallImageType
import com.google.android.horologist.media.ui.R
import com.google.android.horologist.tiles.complication.DataTemplates.longText
import com.google.android.horologist.tiles.complication.DataTemplates.photoImage
import com.google.android.horologist.tiles.complication.DataTemplates.shortText
import com.google.android.horologist.tiles.complication.DataTemplates.smallImage
import com.google.android.horologist.tiles.complication.TypedComplicationTemplate

public class MediaStatusTemplate(
    context: Context
) :
    TypedComplicationTemplate<MediaStatusTemplate.Data>(context) {

    public data class Data(
        @DrawableRes public val appIconRes: Int? = null,
        public val icon: Icon? = null,
        public val type: SmallImageType,
        public val title: String?,
        public val text: String,
        public val launchIntent: PendingIntent?,
        public val contentDescription: ComplicationText? = null
    )

    override fun previewData(): Data = Data(
        title = context.getString(R.string.horologist_preview_app_name),
        text = context.getString(R.string.horologist_preview_favorites),
        appIconRes = R.drawable.ic_baseline_queue_music_24,
        type = SmallImageType.ICON,
        launchIntent = null
    )

    override fun supportedTypes(): List<ComplicationType> =
        listOf(
            ComplicationType.SMALL_IMAGE,
            ComplicationType.SHORT_TEXT,
            ComplicationType.LONG_TEXT,
            ComplicationType.PHOTO_IMAGE
        )

    override fun renderShortText(data: Data): ShortTextComplicationData =
        shortText(
            title = data.title,
            text = data.text,
            icon = data.appIconRes,
            launchIntent = data.launchIntent,
            contentDescription = data.contentDescription
        )

    override fun renderSmallImage(data: Data): SmallImageComplicationData? {
        if (data.icon == null) {
            return null
        }

        return smallImage(
            icon = data.icon,
            type = data.type,
            name = data.text,
            launchIntent = data.launchIntent,
            contentDescription = data.contentDescription
        )
    }

    override fun renderLongText(data: Data): LongTextComplicationData {
        return longText(
            icon = data.icon,
            type = data.type,
            title = data.title,
            text = data.text,
            launchIntent = data.launchIntent,
            contentDescription = data.contentDescription
        )
    }

    override fun renderPhotoImage(data: Data): PhotoImageComplicationData? {
        if (data.icon == null) {
            return null
        }

        return photoImage(
            photoImage = data.icon,
            name = data.text,
            launchIntent = data.launchIntent,
            contentDescription = data.contentDescription
        )
    }
}
