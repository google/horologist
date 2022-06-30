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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.text.style.TextOverflow
import androidx.wear.compose.material.Chip
import androidx.wear.compose.material.ChipDefaults
import androidx.wear.compose.material.Text
import com.google.android.horologist.media.ui.ExperimentalHorologistMediaUiApi
import com.google.android.horologist.media.ui.components.MediaArtwork

/**
 * [Chip] to show all items in the selected category.
 */
@ExperimentalHorologistMediaUiApi
@Composable
public fun ShowPlaylistChip(
    artworkUri: Any?,
    name: String?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    placeholder: Painter? = null,
) {
    val appIcon: (@Composable BoxScope.() -> Unit)? = artworkUri?.let {
        {
            MediaArtwork(
                modifier = Modifier.size(ChipDefaults.LargeIconSize),
                contentDescription = name,
                artworkUri = artworkUri,
                placeholder = placeholder,
            )
        }
    }

    Chip(
        modifier = modifier.fillMaxWidth(),
        colors = ChipDefaults.secondaryChipColors(),
        icon = appIcon,
        label = {
            Text(
                text = name.orEmpty(),
                overflow = TextOverflow.Ellipsis
            )
        },
        onClick = onClick
    )
}
