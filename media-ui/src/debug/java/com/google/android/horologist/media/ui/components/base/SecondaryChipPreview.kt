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

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.materialPath
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.android.horologist.media.ui.utils.rememberVectorPainter

@Preview(
    group = "Variants",
    backgroundColor = 0xff000000,
    showBackground = true
)
@Composable
fun SecondaryChipPreview() {
    StandardChip(
        label = "Primary label",
        onClick = { },
        chipType = StandardChipType.Secondary
    )
}

@Preview(
    name = "With secondary label",
    group = "Variants",
    backgroundColor = 0xff000000,
    showBackground = true
)
@Composable
fun SecondaryChipPreviewWithSecondaryLabel() {
    StandardChip(
        label = "Primary label",
        onClick = { },
        secondaryLabel = "Secondary label",
        chipType = StandardChipType.Secondary
    )
}

@Preview(
    name = "With icon",
    group = "Variants",
    backgroundColor = 0xff000000,
    showBackground = true
)
@Composable
fun SecondaryChipPreviewWithIcon() {
    StandardChip(
        label = "Primary label",
        onClick = { },
        icon = Icons.Default.Image,
        chipType = StandardChipType.Secondary
    )
}

@Preview(
    name = "With large icon",
    group = "Variants",
    backgroundColor = 0xff000000,
    showBackground = true
)
@Composable
fun SecondaryChipPreviewWithLargeIcon() {
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
    group = "Variants",
    backgroundColor = 0xff000000,
    showBackground = true
)
@Composable
fun SecondaryChipPreviewWithSecondaryLabelAndIcon() {
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
    group = "Variants",
    backgroundColor = 0xff000000,
    showBackground = true
)
@Composable
fun SecondaryChipPreviewWithSecondaryLabelAndLargeIcon() {
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
    group = "Variants",
    backgroundColor = 0xff000000,
    showBackground = true
)
@Composable
fun SecondaryChipPreviewDisabled() {
    StandardChip(
        label = "Primary label",
        onClick = { },
        secondaryLabel = "Secondary label",
        icon = Icons.Default.Image,
        chipType = StandardChipType.Secondary,
        enabled = false
    )
}

@Preview(
    name = "With long text",
    group = "Long text",
    backgroundColor = 0xff000000,
    showBackground = true
)
@Composable
fun SecondaryChipPreviewWithLongText() {
    StandardChip(
        label = "Primary label very very very very very very very very very very very very very very very very very long text",
        onClick = { },
        chipType = StandardChipType.Secondary
    )
}

@Preview(
    name = "With secondary label and long text",
    group = "Long text",
    backgroundColor = 0xff000000,
    showBackground = true
)
@Composable
fun SecondaryChipPreviewWithSecondaryLabelAndLongText() {
    StandardChip(
        label = "Primary label very very very very very very very very long text",
        onClick = { },
        secondaryLabel = "Secondary label very very very very very very very very very long text",
        icon = Icons.Default.Image,
        chipType = StandardChipType.Secondary
    )
}

@Preview(
    name = "Using icon smaller than 24dp",
    group = "Icon check",
    backgroundColor = 0xff000000,
    showBackground = true
)
@Composable
fun SecondaryChipPreviewUsingSmallIcon() {
    StandardChip(
        label = "Primary label",
        onClick = { },
        icon = Icon12dp,
        chipType = StandardChipType.Secondary
    )
}

@Preview(
    name = "With large icon, using icon smaller than 32dp",
    group = "Icon check",
    backgroundColor = 0xff000000,
    showBackground = true
)
@Composable
fun SecondaryChipPreviewWithLargeIconUsingSmallIcon() {
    StandardChip(
        label = "Primary label",
        onClick = { },
        icon = Icon12dp,
        largeIcon = true,
        chipType = StandardChipType.Secondary
    )
}

@Preview(
    name = "Using icon larger than 24dp",
    group = "Icon check",
    backgroundColor = 0xff000000,
    showBackground = true
)
@Composable
fun SecondaryChipPreviewUsingExtraLargeIcon() {
    StandardChip(
        label = "Primary label",
        onClick = { },
        icon = Icon48dp,
        chipType = StandardChipType.Secondary
    )
}

@Preview(
    name = "With large icon, using icon larger than 32dp",
    group = "Icon check",
    backgroundColor = 0xff000000,
    showBackground = true
)
@Composable
fun SecondaryChipPreviewWithLargeIconUsingExtraLargeIcon() {
    StandardChip(
        label = "Primary label",
        onClick = { },
        icon = Icon48dp,
        largeIcon = true,
        chipType = StandardChipType.Secondary
    )
}

@Preview(
    name = "With icon placeholder ",
    group = "Icon check",
    backgroundColor = 0xff000000,
    showBackground = true
)
@Composable
fun SecondaryChipPreviewWithIconPlaceholder() {
    StandardChip(
        label = "Primary label",
        onClick = { },
        icon = Icons.Default.Image,
        chipType = StandardChipType.Secondary
    )
}

@Preview(
    name = "Disabled with icon placeholder",
    group = "Icon check",
    backgroundColor = 0xff000000,
    showBackground = true
)
@Composable
fun SecondaryChipPreviewDisabledWithIconPlaceholder() {
    StandardChip(
        label = "Primary label",
        onClick = { },
        secondaryLabel = "Secondary label",
        icon = "iconUri",
        placeholder = rememberVectorPainter(
            image = Icons.Default.Image,
            tintColor = Color.White
        ),
        chipType = StandardChipType.Secondary,
        enabled = false
    )
}

private val Icon12dp: ImageVector
    get() = ImageVector.Builder(
        name = "Icon Small",
        defaultWidth = 12f.dp,
        defaultHeight = 12f.dp,
        viewportWidth = 12f,
        viewportHeight = 12f
    )
        .materialPath {
            horizontalLineToRelative(12.0f)
            verticalLineToRelative(12.0f)
            horizontalLineTo(0.0f)
            close()
        }
        .build()

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

private val Icon48dp: ImageVector
    get() = ImageVector.Builder(
        name = "Icon Extra Large",
        defaultWidth = 48f.dp,
        defaultHeight = 48f.dp,
        viewportWidth = 48f,
        viewportHeight = 48f
    )
        .materialPath {
            horizontalLineToRelative(48.0f)
            verticalLineToRelative(48.0f)
            horizontalLineTo(0.0f)
            close()
        }
        .build()
