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

package com.google.android.horologist.media.ui.components.base

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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.ChipDefaults
import androidx.wear.compose.material.CircularProgressIndicator
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material.LocalContentAlpha
import androidx.wear.compose.material.MaterialTheme
import coil.compose.rememberAsyncImagePainter

private val indicatorPadding = 8.dp
private val progressBarStrokeWidth = 2.dp

/**
 * A default icon implementation to be used with a [StandardChip] that accepts an icon as slot.
 * This implementation displays an icon with a circular progress indicator around it.
 * The progress indicator is in an indeterminate state and spins indefinitely.
 *
 * @param modifier [Modifier] to apply to this layout node.
 * @param icon image or icon to be displayed in the center of this view.
 * @param largeIcon true if it should display the icon with in a large size.
 * @param progressIndicatorColor the color of the progress indicator that is around the icon.
 * @param progressTrackColor the color of the background for the progress indicator.
 * @param placeholder A [Painter] that is displayed while the image is loading.
 */
@Composable
internal fun StandardChipIconWithProgress(
    modifier: Modifier = Modifier,
    largeIcon: Boolean = false,
    icon: Any? = null,
    placeholder: Painter? = null,
    progressIndicatorColor: Color = MaterialTheme.colors.primary,
    progressTrackColor: Color = MaterialTheme.colors.onSurface.copy(alpha = 0.10f)
) {
    val iconSize = if (largeIcon) {
        ChipDefaults.LargeIconSize
    } else {
        ChipDefaults.IconSize
    }

    Box(
        modifier = modifier
            .size(iconSize)
            .clip(CircleShape)
    ) {
        CircularProgressBar(
            modifier = Modifier.align(Alignment.Center),
            progress = null,
            size = iconSize - progressBarStrokeWidth,
            indicatorPadding = indicatorPadding,
            iconProgressIndicatorColor = progressIndicatorColor,
            iconProgressTrackColor = progressTrackColor
        )
        when (icon) {
            is ImageVector -> {
                Icon(
                    imageVector = icon,
                    contentDescription = null, // hidden from talkback
                    modifier = Modifier
                        .align(Alignment.Center)
                        .size(iconSize - indicatorPadding)
                        .clip(CircleShape)
                )
            }
            else -> {
                Image(
                    painter = rememberAsyncImagePainter(
                        model = icon,
                        placeholder = placeholder
                    ),
                    contentDescription = null, // hidden from talkback
                    modifier = Modifier
                        .align(Alignment.Center)
                        .size(iconSize - indicatorPadding)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop,
                    alpha = LocalContentAlpha.current
                )
            }
        }
    }
}

/**
 * A default icon implementation to be used with a [StandardChip] that accepts an icon as slot.
 * This implementation displays an icon with a circular progress indicator around it.
 * The progress indicator express the proportion of completion of an ongoing task.
 *
 * @param modifier [Modifier] to apply to this layout node.
 * @param icon image or icon to be displayed in the center of this view.
 * @param largeIcon true if it should display the icon with in a large size.
 * @param progressIndicatorColor the color of the progress indicator that is around the icon.
 * @param progressTrackColor the color of the background for the progress indicator.
 * @param placeholder A [Painter] that is displayed while the image is loading.
 * @param progress The progress of this progress indicator where 0.0 represents no progress and 1.0
 * represents completion. Values outside of this range are coerced into the range 0..1.
 */
@Composable
internal fun StandardChipIconWithProgress(
    progress: Float,
    modifier: Modifier = Modifier,
    largeIcon: Boolean = false,
    icon: Any? = null,
    placeholder: Painter? = null,
    progressIndicatorColor: Color = MaterialTheme.colors.primary,
    progressTrackColor: Color = MaterialTheme.colors.onSurface.copy(alpha = 0.10f)
) {
    val iconSize = if (largeIcon) {
        ChipDefaults.LargeIconSize
    } else {
        ChipDefaults.IconSize
    }

    Box(
        modifier = modifier
            .size(iconSize)
            .clip(CircleShape)
    ) {
        CircularProgressBar(
            modifier = Modifier.align(Alignment.Center),
            progress = progress,
            size = iconSize - progressBarStrokeWidth,
            indicatorPadding = indicatorPadding,
            iconProgressIndicatorColor = progressIndicatorColor,
            iconProgressTrackColor = progressTrackColor
        )

        when (icon) {
            is ImageVector -> {
                Icon(
                    imageVector = icon,
                    contentDescription = null, // hidden from talkback
                    modifier = Modifier
                        .align(Alignment.Center)
                        .size(iconSize - indicatorPadding)
                        .clip(CircleShape)
                )
            }
            else -> {
                Image(
                    painter = rememberAsyncImagePainter(
                        model = icon,
                        placeholder = placeholder
                    ),
                    contentDescription = null, // hidden from talkback
                    modifier = Modifier
                        .align(Alignment.Center)
                        .size(iconSize - indicatorPadding)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop,
                    alpha = LocalContentAlpha.current
                )
            }
        }
    }
}

@Composable
private fun CircularProgressBar(
    modifier: Modifier = Modifier,
    progress: Float?,
    size: Dp,
    indicatorPadding: Dp,
    iconProgressIndicatorColor: Color,
    iconProgressTrackColor: Color
) {
    if (progress != null) {
        CircularProgressIndicator(
            modifier = modifier
                .size(size + indicatorPadding),
            indicatorColor = iconProgressIndicatorColor,
            trackColor = iconProgressTrackColor,
            progress = progress / 100,
            strokeWidth = 2.dp
        )
    } else {
        CircularProgressIndicator(
            modifier = modifier
                .size(size + indicatorPadding),
            indicatorColor = iconProgressIndicatorColor,
            trackColor = iconProgressTrackColor,
            strokeWidth = 2.dp
        )
    }
}
