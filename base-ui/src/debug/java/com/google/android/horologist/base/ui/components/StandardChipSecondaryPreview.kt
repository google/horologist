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

package com.google.android.horologist.base.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.materialPath
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Preview(
    backgroundColor = 0xff000000,
    showBackground = true
)
@Composable
fun StandardChipSecondaryPreview() {
    StandardChip(
        label = "Primary label",
        onClick = { },
        chipType = StandardChipType.Secondary
    )
}

@Preview(
    name = "With secondary label",
    backgroundColor = 0xff000000,
    showBackground = true
)
@Composable
fun StandardChipSecondaryPreviewWithSecondaryLabel() {
    StandardChip(
        label = "Primary label",
        onClick = { },
        secondaryLabel = "Secondary label",
        chipType = StandardChipType.Secondary
    )
}

@Preview(
    name = "With icon",
    backgroundColor = 0xff000000,
    showBackground = true
)
@Composable
fun StandardChipSecondaryPreviewWithIcon() {
    StandardChip(
        label = "Primary label",
        onClick = { },
        icon = Icons.Default.Image,
        chipType = StandardChipType.Secondary
    )
}

@Preview(
    name = "With large icon",
    backgroundColor = 0xff000000,
    showBackground = true
)
@Composable
fun StandardChipSecondaryPreviewWithLargeIcon() {
    StandardChip(
        label = "Primary label",
        onClick = { },
        icon = Icon32dp,
        largeIcon = true,
        chipType = StandardChipType.Secondary
    )
}

@Preview(
    name = "With secondary label and icon",
    backgroundColor = 0xff000000,
    showBackground = true
)
@Composable
fun StandardChipSecondaryPreviewWithSecondaryLabelAndIcon() {
    StandardChip(
        label = "Primary label",
        onClick = { },
        secondaryLabel = "Secondary label",
        icon = Icons.Default.Image,
        chipType = StandardChipType.Secondary
    )
}

@Preview(
    name = "With secondary label and large icon",
    backgroundColor = 0xff000000,
    showBackground = true
)
@Composable
fun StandardChipSecondaryPreviewWithSecondaryLabelAndLargeIcon() {
    StandardChip(
        label = "Primary label",
        onClick = { },
        secondaryLabel = "Secondary label",
        icon = Icon32dp,
        largeIcon = true,
        chipType = StandardChipType.Secondary
    )
}

@Preview(
    name = "Disabled",
    backgroundColor = 0xff000000,
    showBackground = true
)
@Composable
fun StandardChipSecondaryPreviewDisabled() {
    StandardChip(
        label = "Primary label",
        onClick = { },
        secondaryLabel = "Secondary label",
        icon = Icons.Default.Image,
        chipType = StandardChipType.Secondary,
        enabled = false
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
