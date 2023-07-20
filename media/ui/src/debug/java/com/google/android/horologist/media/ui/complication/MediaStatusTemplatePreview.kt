/*
 * Copyright 2023 The Android Open Source Project
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

import android.graphics.Color
import android.graphics.drawable.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.wear.watchface.complications.data.SmallImageType
import com.google.android.horologist.compose.tools.WearPreview
import com.google.android.horologist.media.ui.R

@Composable
@WearPreview
fun MediaStatusTemplatePreview() {
    val context = LocalContext.current
    val renderer = MediaStatusTemplate(context)

    ComplicationRendererPreview(
        complicationRenderer = renderer,
        data = MediaStatusTemplate.Data(
            R.drawable.ic_baseline_queue_music_24,
            icon = Icon.createWithResource(
                context,
                R.drawable.ic_baseline_queue_music_24
            ).apply {
                setTint(Color.RED)
            },
            type = SmallImageType.ICON,
            launchIntent = null,
            title = "Now, that's what I call Hits 24",
            text = "Now 24",
            contentDescription = null
        )
    )
}
