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

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.paint
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.ChipDefaults
import androidx.wear.compose.material.MaterialTheme

/**
 * Secondary placeholder chip.
 */
@Composable
internal fun SecondaryPlaceholderChip(
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
    enabled: Boolean = true,
) {
    val backgroundColor = MaterialTheme.colors.onSurfaceVariant.copy(alpha = 0.38f)

    Row(
        modifier = modifier
            .height(52.dp) // ChipDefaults.Height
            .fillMaxWidth()
            .clip(shape = MaterialTheme.shapes.small)
            .paint(
                painter = ChipDefaults
                    .secondaryChipColors()
                    .background(enabled = enabled).value,
                contentScale = ContentScale.Crop
            )
            .clickable(
                enabled = enabled,
                onClick = onClick,
                role = Role.Button,
                indication = rememberRipple(),
                interactionSource = remember { MutableInteractionSource() },
            )
            .padding(ChipDefaults.ContentPadding),
    ) {
        Box(
            modifier = Modifier
                .align(Alignment.CenterVertically)
                .clip(CircleShape)
                .background(backgroundColor)
                .size(ChipDefaults.LargeIconSize)
        )

        Spacer(modifier = Modifier.width(6.dp))

        Column(
            modifier = Modifier
                .fillMaxHeight()
                .weight(1.0f),
            verticalArrangement = Arrangement.Center,
        ) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(backgroundColor)
                    .fillMaxWidth()
                    .height(12.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(modifier = Modifier.fillMaxWidth()) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(backgroundColor)
                        .weight(1.0f)
                        .height(12.dp)
                )

                Spacer(modifier = Modifier.width(20.dp))
            }
        }

        Spacer(modifier = Modifier.width(10.dp))
    }
}
