/*
 * Copyright 2025 The Android Open Source Project
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

package com.google.android.horologist.media.ui.material3.components.controls

import androidx.compose.animation.Animatable
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.wear.compose.material3.Icon
import androidx.wear.compose.material3.MaterialTheme
import com.google.android.horologist.images.base.paintable.Paintable
import com.google.android.horologist.images.base.paintable.PaintableIcon

/**
 * An icon to be shown in the media display before media title.
 *
 * @param paintableRes The paintable to be shown.
 * @param tint The tint to be applied to the icon.
 */
@Composable
internal fun MediaTitleIcon(
    paintableRes: Paintable,
    tint: Color = MaterialTheme.colorScheme.primary,
) {
    val animatedTint = remember { Animatable(tint) }
    val animationSpec = MaterialTheme.motionScheme.slowEffectsSpec<Color>()
    LaunchedEffect(tint) {
        animatedTint.animateTo(tint, animationSpec)
    }
    if (paintableRes is PaintableIcon) {
        Icon(
            modifier = Modifier.fillMaxSize(),
            painter = paintableRes.rememberPainter(),
            contentDescription = null,
            tint = animatedTint.value,
        )
    } else {
        Image(
            modifier = Modifier.fillMaxSize().clip(CircleShape),
            painter = paintableRes.rememberPainter(),
            contentDescription = null,
            contentScale = ContentScale.FillHeight,
        )
    }
}
