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

package com.google.android.horologist.compose.material

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.materialPath
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.ChipDefaults

@Preview(
    backgroundColor = 0xff000000,
    showBackground = true
)
@Composable
fun ChipPreview() {
    Chip(
        label = "Primary label",
        onClick = { }
    )
}

@Preview(
    name = "With secondary label",
    backgroundColor = 0xff000000,
    showBackground = true
)
@Composable
fun ChipPreviewWithSecondaryLabel() {
    Chip(
        label = "Primary label",
        onClick = { },
        secondaryLabel = "Secondary label"
    )
}

@Preview(
    name = "With icon",
    backgroundColor = 0xff000000,
    showBackground = true
)
@Composable
fun ChipPreviewWithIcon() {
    Chip(
        label = "Primary label",
        onClick = { },
        icon = Icons.Default.Image
    )
}

@Preview(
    name = "With large icon",
    backgroundColor = 0xff000000,
    showBackground = true
)
@Composable
fun ChipPreviewWithLargeIcon() {
    Chip(
        label = "Primary label",
        onClick = { },
        icon = Icon32dp,
        largeIcon = true
    )
}

@Preview(
    name = "With secondary label and icon",
    backgroundColor = 0xff000000,
    showBackground = true
)
@Composable
fun ChipPreviewWithSecondaryLabelAndIcon() {
    Chip(
        label = "Primary label",
        onClick = { },
        secondaryLabel = "Secondary label",
        icon = Icons.Default.Image
    )
}

@Preview(
    name = "With secondary label and large icon",
    backgroundColor = 0xff000000,
    showBackground = true
)
@Composable
fun ChipPreviewWithSecondaryLabelAndLargeIcon() {
    Chip(
        label = "Primary label",
        onClick = { },
        secondaryLabel = "Secondary label",
        icon = Icon32dp,
        largeIcon = true
    )
}

@Preview(
    name = "Disabled",
    backgroundColor = 0xff000000,
    showBackground = true
)
@Composable
fun ChipPreviewDisabled() {
    Chip(
        label = "Primary label",
        onClick = { },
        secondaryLabel = "Secondary label",
        icon = Icons.Default.Image,
        enabled = false
    )
}

@Preview(
    name = "With secondary chip colors",
    backgroundColor = 0xff000000,
    showBackground = true
)
@Composable
fun ChipPreviewWithSecondaryChipColors() {
    Chip(
        label = "Primary label",
        onClick = { },
        secondaryLabel = "Secondary label",
        icon = Icons.Default.Image,
        colors = ChipDefaults.secondaryChipColors()
    )
}

@Preview(
    name = "With gradient background chip colors",
    backgroundColor = 0xff000000,
    showBackground = true
)
@Composable
fun ChipPreviewWithGradientBackgroundChipColors() {
    Chip(
        label = "Primary label",
        onClick = { },
        secondaryLabel = "Secondary label",
        icon = Icons.Default.Image,
        colors = ChipDefaults.gradientBackgroundChipColors()
    )
}

@Preview(
    name = "With outlined chip colors",
    backgroundColor = 0xff000000,
    showBackground = true
)
@Composable
fun ChipPreviewWithOutlinedChipColors() {
    Chip(
        label = "Primary label",
        onClick = { },
        secondaryLabel = "Secondary label",
        icon = Icons.Default.Image,
        colors = ChipDefaults.outlinedChipColors()
    )
}

@Preview(
    name = "With image background chip colors",
    backgroundColor = 0xff000000,
    showBackground = true
)
@Composable
fun ChipPreviewWithImageBackgroundChipColors() {
    Chip(
        label = "Primary label",
        onClick = { },
        secondaryLabel = "Secondary label",
        icon = Icons.Default.Image,
        colors = ChipDefaults.imageBackgroundChipColors(
            backgroundImagePainter = painterResource(id = android.R.drawable.ic_dialog_alert)
        )
    )
}

private val Icon32dp: ImageVector
    get() = ImageVector.Builder(
        name = "Icon Large",
        defaultWidth = 32f.dp,
        defaultHeight = 32f.dp,
        viewportWidth = 32f,
        viewportHeight = 32f
    )
        .materialPath {
            horizontalLineToRelative(32.0f)
            verticalLineToRelative(32.0f)
            horizontalLineTo(0.0f)
            close()
        }
        .build()
