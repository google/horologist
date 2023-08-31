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

package com.google.android.horologist.media.ui.components

import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.Chip
import androidx.wear.compose.material.ChipDefaults
import androidx.wear.compose.material.Text
import com.google.android.horologist.annotations.ExperimentalHorologistApi
import com.google.android.horologist.media.ui.state.model.MediaUiModel

/**
 * A rounded chip to show a single [MediaUiModel].
 *
 * @param media The [MediaUiModel] that the [title][MediaUiModel.title] and
 * [artwork][MediaUiModel.artworkUri] will be used to display on the chip.
 * @param onClick Will be called when the user clicks the chip.
 * @param modifier The Modifier to be applied to the chip.
 * @param defaultTitle A text to be used when [MediaUiModel.title] is null.
 * @param placeholder A placeholder image to be displayed while
 * [artwork][MediaUiModel.artworkUri] is being loaded.
 */
@ExperimentalHorologistApi
@Composable
public fun MediaChip(
    media: MediaUiModel,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    defaultTitle: String = "",
    placeholder: Painter? = null,
) {
    val artworkUri = media.artworkUri
    val title = media.title

    MediaChip(
        title = title.takeIf { it.isNotEmpty() } ?: defaultTitle,
        artworkUri = artworkUri,
        onClick = onClick,
        modifier = modifier,
        placeholder = placeholder,
    )
}

/**
 * A rounded chip to show a single media title and its artwork.
 */
@ExperimentalHorologistApi
@Composable
public fun MediaChip(
    title: String,
    artworkUri: Any?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    placeholder: Painter? = null,
) {
    val appIcon: (@Composable BoxScope.() -> Unit)? = artworkUri?.let {
        {
            MediaArtwork(
                modifier = Modifier.size(ChipDefaults.LargeIconSize),
                contentDescription = title,
                artworkUri = artworkUri,
                placeholder = placeholder,
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
            bottom = 6.dp,
        ),
        icon = appIcon,
        label = {
            Text(
                text = title,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
        },
    )
}
