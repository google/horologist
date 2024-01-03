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

package com.google.android.horologist.compose.material

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.ChipDefaults
import androidx.wear.compose.material.CircularProgressIndicator
import androidx.wear.compose.material.LocalContentAlpha
import androidx.wear.compose.material.MaterialTheme
import com.google.android.horologist.annotations.ExperimentalHorologistApi
import com.google.android.horologist.compose.material.util.DECORATIVE_ELEMENT_CONTENT_DESCRIPTION
import com.google.android.horologist.images.base.paintable.Paintable
import com.google.android.horologist.images.base.paintable.PaintableIcon
private val indicatorPadding = 8.dp
private val progressBarStrokeWidth = 2.dp

/**
 * A default icon implementation to be used with a [Chip] that accepts an icon as slot.
 * This implementation displays an icon with a circular progress indicator around it.
 * The progress indicator is in an indeterminate state and spins indefinitely.
 *
 * @param modifier [Modifier] to apply to this layout node.
 * @param icon Image or icon to be displayed in the center of this view.
 * @param largeIcon True if it should display the icon with in a large size.
 * @param placeholder A [Painter] that is displayed while the icon image is loading.
 * @param progressIndicatorColor The color of the progress indicator that is around the icon.
 * @param progressTrackColor The color of the background for the progress indicator.
 */
@ExperimentalHorologistApi
@Composable
public fun ChipIconWithProgress(
    modifier: Modifier = Modifier,
    icon: Paintable? = null,
    largeIcon: Boolean = false,
    progressIndicatorColor: Color = MaterialTheme.colors.primary,
    progressTrackColor: Color = MaterialTheme.colors.onSurface.copy(alpha = 0.10f),
) {
    ChipIconWithProgressInternal(
        progress = null,
        icon = icon,
        largeIcon = largeIcon,
        progressIndicatorColor = progressIndicatorColor,
        progressTrackColor = progressTrackColor,
        modifier = modifier,
    )
}

/**
 * A default icon implementation to be used with a [Chip] that accepts an icon as slot.
 * This implementation displays an icon with a circular progress indicator around it.
 * The progress indicator express the proportion of completion of an ongoing task.
 *
 * @param progress The progress of this progress indicator as a percentage where 0.0 represents no progress and 100.0
 * represents completion. Values outside of this range are coerced into the range 0..100F.
 * @param modifier [Modifier] to apply to this layout node.
 * @param icon Image or icon to be displayed in the center of this view.
 * @param largeIcon True if it should display the icon with in a large size.
 * @param placeholder A [Painter] that is displayed while the icon image is loading.
 * @param progressIndicatorColor The color of the progress indicator that is around the icon.
 * @param progressTrackColor The color of the background for the progress indicator.
 */
@ExperimentalHorologistApi
@Composable
public fun ChipIconWithProgress(
    progress: Float,
    modifier: Modifier = Modifier,
    icon: Paintable? = null,
    largeIcon: Boolean = false,
    progressIndicatorColor: Color = MaterialTheme.colors.primary,
    progressTrackColor: Color = MaterialTheme.colors.onSurface.copy(alpha = 0.10f),
) {
    ChipIconWithProgressInternal(
        progress = progress,
        icon = icon,
        largeIcon = largeIcon,
        progressIndicatorColor = progressIndicatorColor,
        progressTrackColor = progressTrackColor,
        modifier = modifier,
    )
}

@Composable
private fun ChipIconWithProgressInternal(
    progress: Float?,
    icon: Paintable?,
    largeIcon: Boolean,
    progressIndicatorColor: Color,
    progressTrackColor: Color,
    modifier: Modifier = Modifier,
) {
    val iconSize = if (largeIcon) {
        ChipDefaults.LargeIconSize
    } else {
        ChipDefaults.IconSize
    }

    Box(
        modifier = modifier
            .size(iconSize)
            .clip(CircleShape),
    ) {
        if (progress != null) {
            CircularProgressIndicator(
                modifier = modifier
                    .size(iconSize - progressBarStrokeWidth + indicatorPadding),
                indicatorColor = progressIndicatorColor,
                trackColor = progressTrackColor,
                progress = progress / 100,
                strokeWidth = progressBarStrokeWidth,
            )
        } else {
            CircularProgressIndicator(
                modifier = modifier
                    .size(iconSize - progressBarStrokeWidth + indicatorPadding),
                indicatorColor = progressIndicatorColor,
                trackColor = progressTrackColor,
                strokeWidth = progressBarStrokeWidth,
            )
        }

        icon ?: return
        if (icon is PaintableIcon) {
            Icon(
                paintable = icon,
                contentDescription = DECORATIVE_ELEMENT_CONTENT_DESCRIPTION,
                modifier = Modifier
                    .align(Alignment.Center)
                    .size(iconSize - indicatorPadding)
                    .clip(CircleShape),
            )
        } else {
            Image(
                painter = icon.rememberPainter(),
                contentDescription = DECORATIVE_ELEMENT_CONTENT_DESCRIPTION,
                modifier = Modifier
                    .align(Alignment.Center)
                    .size(iconSize - indicatorPadding)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop,
                alpha = LocalContentAlpha.current,
            )
        }
    }
}
