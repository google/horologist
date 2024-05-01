/*
 * Copyright 2024 The Android Open Source Project
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

package com.google.android.horologist.materialcomponents

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Image
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.TitleCard
import com.google.android.horologist.compose.layout.ScalingLazyColumn
import com.google.android.horologist.compose.layout.ScalingLazyColumnState
import com.google.android.horologist.compose.material.AnimatedLabel
import com.google.android.horologist.compose.material.Chip
import com.google.android.horologist.compose.material.Icon
import com.google.android.horologist.images.base.paintable.ImageVectorPaintable.Companion.asPaintable

@Composable
internal fun SampleAnimatedComponents(
    modifier: Modifier = Modifier,
    columnState: ScalingLazyColumnState,
) {
    val secondaryLabels = arrayOf(
        "Click to change this text",
        "Click again to hide",
        null,
    )

    ScalingLazyColumn(
        columnState = columnState,
        modifier = modifier,
    ) {
        item {
            var selectedText by remember { mutableStateOf(0) }
            Chip(
                label = { Text("Chip") },
                modifier = Modifier.fillMaxWidth(),
                onClick = { selectedText++ },
                secondaryLabel = {
                    AnimatedDefaultText(secondaryLabels[selectedText % secondaryLabels.size])
                },
                icon = {
                    Icon(
                        paintable = Icons.Default.Image.asPaintable(),
                        contentDescription = "",
                    )
                },
            )
        }
        item {
            var selectedText by remember { mutableStateOf(0) }
            androidx.wear.compose.material.OutlinedChip(
                label = { Text("Outlined Chip") },
                modifier = Modifier.fillMaxWidth(),
                onClick = { selectedText++ },
                secondaryLabel = {
                    AnimatedDefaultText(secondaryLabels[selectedText % secondaryLabels.size])
                },
                icon = {
                    Icon(
                        paintable = Icons.Default.Image.asPaintable(),
                        contentDescription = "",
                    )
                },
            )
        }
        item {
            val cardContents = arrayOf(
                "Click to change this text",
                "Click again to change",
            )
            var selectedText by remember { mutableStateOf(0) }
            TitleCard(
                title = { Text("Card") },
                onClick = { selectedText++ },
            ) {
                AnimatedDefaultText(cardContents[selectedText % cardContents.size])
            }
        }
    }
}

@Composable
private fun AnimatedDefaultText(
    text: String?,
) {
    AnimatedLabel(label = text) { targetLabel ->
        Text(
            modifier = Modifier.fillMaxWidth(),
            text = targetLabel,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
        )
    }
}
