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

package com.google.android.horologist.sectionedlist.component

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.wear.compose.material.Chip
import androidx.wear.compose.material.ChipDefaults
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material.Text

@Composable
fun TwoLinesChip(
    primaryLabel: String,
    secondaryLabel: String,
    imageVector: ImageVector
) {
    Chip(
        label = {
            Text(
                text = primaryLabel,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Left,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1
            )
        },
        onClick = { },
        modifier = Modifier.fillMaxWidth(),
        secondaryLabel = {
            Text(
                text = secondaryLabel,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1
            )
        },
        icon = {
            Icon(
                imageVector = imageVector,
                contentDescription = null, // hidden from talkback
                modifier = Modifier
                    .size(ChipDefaults.LargeIconSize)
                    .clip(CircleShape),
                tint = Color.Gray
            )
        },
        colors = ChipDefaults.secondaryChipColors()
    )
}
