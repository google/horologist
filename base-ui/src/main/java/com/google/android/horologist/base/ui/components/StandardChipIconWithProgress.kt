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

package com.google.android.horologist.base.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.wear.compose.material.MaterialTheme
import com.google.android.horologist.annotations.ExperimentalHorologistApi
import com.google.android.horologist.compose.material.ChipIconWithProgress

/**
 * A default icon implementation to be used with a [StandardChip] that accepts an icon as slot.
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
@Deprecated(
    "Replaced by ChipIconWithProgress in Horologist Material Compose library",
    replaceWith = ReplaceWith(
        "ChipIconWithProgress(modifier, icon, largeIcon, placeholder, progressIndicatorColor, progressTrackColor)",
        "com.google.android.horologist.compose.material.ChipIconWithProgress"
    )
)
@ExperimentalHorologistApi
@Composable
public fun StandardChipIconWithProgress(
    modifier: Modifier = Modifier,
    icon: Any? = null,
    largeIcon: Boolean = false,
    placeholder: Painter? = null,
    progressIndicatorColor: Color = MaterialTheme.colors.primary,
    progressTrackColor: Color = MaterialTheme.colors.onSurface.copy(alpha = 0.10f)
) {
    ChipIconWithProgress(
        modifier = modifier,
        icon = icon,
        largeIcon = largeIcon,
        placeholder = placeholder,
        progressIndicatorColor = progressIndicatorColor,
        progressTrackColor = progressTrackColor
    )
}

/**
 * A default icon implementation to be used with a [StandardChip] that accepts an icon as slot.
 * This implementation displays an icon with a circular progress indicator around it.
 * The progress indicator express the proportion of completion of an ongoing task.
 *
 * @param progress The progress of this progress indicator where 0.0 represents no progress and 1.0
 * represents completion. Values outside of this range are coerced into the range 0..1.
 * @param modifier [Modifier] to apply to this layout node.
 * @param icon Image or icon to be displayed in the center of this view.
 * @param largeIcon True if it should display the icon with in a large size.
 * @param placeholder A [Painter] that is displayed while the icon image is loading.
 * @param progressIndicatorColor The color of the progress indicator that is around the icon.
 * @param progressTrackColor The color of the background for the progress indicator.
 */
@Deprecated(
    "Replaced by ChipIconWithProgress in Horologist Material Compose library",
    replaceWith = ReplaceWith(
        "ChipIconWithProgress(progress, modifier, icon, largeIcon, placeholder, progressIndicatorColor, progressTrackColor)",
        "com.google.android.horologist.compose.material.ChipIconWithProgress"
    )
)
@ExperimentalHorologistApi
@Composable
public fun StandardChipIconWithProgress(
    progress: Float,
    modifier: Modifier = Modifier,
    icon: Any? = null,
    largeIcon: Boolean = false,
    placeholder: Painter? = null,
    progressIndicatorColor: Color = MaterialTheme.colors.primary,
    progressTrackColor: Color = MaterialTheme.colors.onSurface.copy(alpha = 0.10f)
) {
    ChipIconWithProgress(
        progress = progress,
        modifier = modifier,
        icon = icon,
        largeIcon = largeIcon,
        placeholder = placeholder,
        progressIndicatorColor = progressIndicatorColor,
        progressTrackColor = progressTrackColor
    )
}
