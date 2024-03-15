/*
 * Copyright 2024 The Android Open Source Project
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

package com.google.android.horologist.media.ui.components.controls

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.wear.compose.material.MaterialTheme
import com.google.android.horologist.compose.material.Icon
import com.google.android.horologist.images.base.paintable.Paintable
import com.google.android.horologist.images.base.paintable.PaintableIcon

/**
 * An icon to be shown in the media display before media title.
 */
@Composable
internal fun MediaTitleIcon(icon: Paintable) {
    if (icon is PaintableIcon) {
        Icon(
            modifier = Modifier.fillMaxSize(),
            paintable = icon,
            contentDescription = null,
            tint = MaterialTheme.colors.onBackground,
        )
    } else {
        Image(
            modifier = Modifier.fillMaxSize(),
            painter = icon.rememberPainter(),
            contentDescription = null,
            contentScale = ContentScale.FillHeight,
        )
    }
}
