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

package com.google.android.horologist.composables

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.paint
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.Chip
import androidx.wear.compose.material.ChipColors
import androidx.wear.compose.material.ChipDefaults
import androidx.wear.compose.material.ExperimentalWearMaterialApi
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.PlaceholderDefaults
import androidx.wear.compose.material.placeholder
import androidx.wear.compose.material.placeholderShimmer
import androidx.wear.compose.material.rememberPlaceholderState
import com.google.android.horologist.annotations.ExperimentalHorologistApi

/**
 * A placeholder chip to be displayed while the contents of the [Chip] is being loaded.
 */
@OptIn(ExperimentalWearMaterialApi::class)
@ExperimentalHorologistApi
@Composable
public fun PlaceholderChip(
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
    colors: ChipColors = ChipDefaults.primaryChipColors(),
    enabled: Boolean = false,
    contentDescription: String = stringResource(id = R.string.horologist_placeholderchip_content_description)
) {
    val chipPlaceholderState = rememberPlaceholderState { false }

    Chip(
        modifier = modifier
            .height(52.dp) // ChipDefaults.Height
            .fillMaxWidth()
            .clip(shape = MaterialTheme.shapes.small)
            .paint(
                painter = colors.background(enabled = enabled).value,
                contentScale = ContentScale.Crop
            )
            .placeholderShimmer(chipPlaceholderState)
            .semantics {
                this.contentDescription = contentDescription
            },
        onClick = onClick,
        enabled = enabled,
        label = {
            Column {
                Box(
                    modifier = Modifier
                        .padding(end = 10.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .fillMaxWidth()
                        .height(12.dp)
                        .placeholder(chipPlaceholderState)
                )
                Spacer(Modifier.size(8.dp))
            }
        },
        secondaryLabel = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(end = 30.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .height(12.dp)
                    .placeholder(chipPlaceholderState)
            )
        },
        icon = {
            Box(
                modifier = Modifier
                    .clip(CircleShape)
                    .size(ChipDefaults.LargeIconSize)
                    .placeholder(chipPlaceholderState)
            )
        },
        colors = PlaceholderDefaults.placeholderChipColors(
            originalChipColors = colors,
            placeholderState = chipPlaceholderState
        )
    )

    if (!chipPlaceholderState.isShowContent) {
        LaunchedEffect(chipPlaceholderState) {
            chipPlaceholderState.startPlaceholderAnimation()
        }
    }
}
