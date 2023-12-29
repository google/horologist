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

package com.google.android.horologist.media.ui.components.actions

import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.wear.compose.material.ChipDefaults
import com.google.android.horologist.annotations.ExperimentalHorologistApi
import com.google.android.horologist.compose.material.Chip
import com.google.android.horologist.images.base.paintable.Paintable
import com.google.android.horologist.media.ui.components.MediaArtwork

/**
 * [Chip] to show all items in the selected category.
 */
@ExperimentalHorologistApi
@Composable
public fun ShowPlaylistChip(
    artworkPaintable: Paintable?,
    name: String?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val appIcon: (@Composable BoxScope.() -> Unit)? = artworkPaintable?.let {
        {
            MediaArtwork(
                modifier = Modifier.size(ChipDefaults.LargeIconSize),
                contentDescription = name,
                artworkPaintable = it,
            )
        }
    }

    Chip(
        label = name.orEmpty(),
        onClick = onClick,
        modifier = modifier,
        icon = appIcon,
        colors = ChipDefaults.secondaryChipColors(),
    )
}
