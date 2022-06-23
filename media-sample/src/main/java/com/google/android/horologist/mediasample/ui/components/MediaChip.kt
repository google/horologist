/*
 * Copyright 2021 The Android Open Source Project
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

package com.google.android.horologist.mediasample.ui.components

import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.Chip
import androidx.wear.compose.material.ChipDefaults
import androidx.wear.compose.material.Text
import com.google.android.horologist.media.ui.state.model.MediaItemUiModel

/**
 * A rounded chip to show a single [MediaItemUiModel] with an
 * optional secondary action
 */
@Composable
fun MediaChip(
    modifier: Modifier = Modifier,
    mediaItem: MediaItemUiModel,
    onClick: () -> Unit,
) {
    val appIcon: (@Composable BoxScope.() -> Unit)? = mediaItem.artworkUri?.let {
        {
            MediaArtwork(
                modifier = Modifier.size(ChipDefaults.LargeIconSize),
                mediaItem = mediaItem
            )
        }
    }

    Chip(
        modifier = modifier.fillMaxWidth(),
        onClick = onClick,
        colors = ChipDefaults.secondaryChipColors(),
        contentPadding = PaddingValues(
            start = 12.dp,
            top = 6.dp,
            end = 0.dp,
            bottom = 6.dp
        ),
        icon = appIcon,
        label = {
            Text(
                text = mediaItem.title ?: "Unknown",
                maxLines = 2
            )
        }
    )
}
