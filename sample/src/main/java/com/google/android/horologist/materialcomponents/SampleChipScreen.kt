/*
 * Copyright 2023 The Android Open Source Project
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

import android.R
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.materialPath
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.ChipDefaults
import com.google.android.horologist.compose.layout.ScalingLazyColumn
import com.google.android.horologist.compose.layout.ScalingLazyColumnState
import com.google.android.horologist.compose.material.Chip
import com.google.android.horologist.images.base.paintable.ImageVectorPaintable.Companion.asPaintable

@Composable
internal fun SampleChipScreen(
    modifier: Modifier = Modifier,
    columnState: ScalingLazyColumnState,
) {
    ScalingLazyColumn(
        columnState = columnState,
        modifier = modifier,
    ) {
        item {
            Chip(
                label = "Primary label",
                onClick = { },
            )
        }
        item {
            Chip(
                label = "Primary label",
                onClick = { },
                icon = Icons.Default.Image.asPaintable(),
            )
        }
        item {
            Chip(
                label = "Primary label",
                onClick = { },
                icon = Icon32dp.asPaintable(),
                largeIcon = true,
            )
        }
        item {
            Chip(
                label = "Primary label",
                onClick = { },
                secondaryLabel = "Secondary label",
                icon = Icons.Default.Image.asPaintable(),
            )
        }
        item {
            Chip(
                label = "Primary label",
                onClick = { },
                secondaryLabel = "Secondary label",
                icon = Icon32dp.asPaintable(),
                largeIcon = true,
            )
        }
        item {
            Chip(
                label = "Primary label",
                onClick = { },
                secondaryLabel = "Secondary label",
                icon = Icons.Default.Image.asPaintable(),
                enabled = false,
            )
        }
        item {
            Chip(
                label = "Primary label",
                onClick = { },
                secondaryLabel = "Secondary label",
                icon = Icons.Default.Image.asPaintable(),
                colors = ChipDefaults.secondaryChipColors(),
            )
        }
        item {
            Chip(
                label = "Primary label",
                onClick = { },
                secondaryLabel = "Secondary label",
                icon = Icons.Default.Image.asPaintable(),
                colors = ChipDefaults.gradientBackgroundChipColors(),
            )
        }
        item {
            Chip(
                label = "Primary label",
                onClick = { },
                secondaryLabel = "Secondary label",
                icon = Icons.Default.Image.asPaintable(),
                colors = ChipDefaults.imageBackgroundChipColors(
                    backgroundImagePainter = painterResource(id = R.drawable.ic_dialog_alert),
                ),
            )
        }
    }
}

private val Icon32dp: ImageVector
    get() = ImageVector.Builder(
        name = "Icon Large",
        defaultWidth = 32f.dp,
        defaultHeight = 32f.dp,
        viewportWidth = 32f,
        viewportHeight = 32f,
    )
        .materialPath {
            horizontalLineToRelative(32.0f)
            verticalLineToRelative(32.0f)
            horizontalLineTo(0.0f)
            close()
        }
        .build()
