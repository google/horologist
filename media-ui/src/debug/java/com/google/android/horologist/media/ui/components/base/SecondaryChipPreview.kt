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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.materialPath
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.android.horologist.media.ui.utils.rememberVectorPainter

@Preview(
    backgroundColor = 0xff000000,
    showBackground = true,
)
@Composable
fun SecondaryChipPreview() {
    SecondaryChip(label = "Primary label", onClick = { })
}

@Preview(
    name = "With secondary label",
    backgroundColor = 0xff000000,
    showBackground = true,
)
@Composable
fun SecondaryChipPreviewWithSecondaryLabel() {
    SecondaryChip(
        label = "Primary label",
        onClick = { },
        secondaryLabel = "Secondary label",
    )
}

@Preview(
    name = "With icon",
    backgroundColor = 0xff000000,
    showBackground = true,
)
@Composable
fun SecondaryChipPreviewWithIcon() {
    SecondaryChip(
        label = "Primary label",
        onClick = { },
        icon = "iconUri",
        placeholder = rememberVectorPainter(
            image = Icons.Default.Add,
            tintColor = Color.White,
        )
    )
}

@Preview(
    name = "With ImageVector as icon",
    backgroundColor = 0xff000000,
    showBackground = true,
)
@Composable
fun SecondaryChipPreviewWithImageVectorAsIcon() {
    SecondaryChip(
        label = "Primary label",
        onClick = { },
        icon = Icons.Default.Add
    )
}

@Preview(
    name = "With large icon",
    backgroundColor = 0xff000000,
    showBackground = true,
)
@Composable
fun SecondaryChipPreviewWithLargeIcon() {
    SecondaryChip(
        label = "Primary label",
        onClick = { },
        icon = "iconUri",
        largeIcon = true,
        placeholder = rememberVectorPainter(
            image = Icon32dp,
            tintColor = Color.White,
        )
    )
}

@Preview(
    name = "With secondary label and icon",
    backgroundColor = 0xff000000,
    showBackground = true,
)
@Composable
fun SecondaryChipPreviewWithSecondaryLabelAndIcon() {
    SecondaryChip(
        label = "Primary label",
        onClick = { },
        secondaryLabel = "Secondary label",
        icon = "iconUri",
        placeholder = rememberVectorPainter(
            image = Icons.Default.Add,
            tintColor = Color.White,
        )
    )
}

@Preview(
    name = "With secondary label and icon",
    backgroundColor = 0xff000000,
    showBackground = true,
)
@Composable
fun SecondaryChipPreviewWithSecondaryLabelAndLargeIcon() {
    SecondaryChip(
        label = "Primary label",
        onClick = { },
        secondaryLabel = "Secondary label",
        icon = "iconUri",
        largeIcon = true,
        placeholder = rememberVectorPainter(
            image = Icon32dp,
            tintColor = Color.White,
        )
    )
}

@Preview(
    name = "Disabled",
    backgroundColor = 0xff000000,
    showBackground = true,
)
@Composable
fun SecondaryChipPreviewDisabled() {
    SecondaryChip(
        label = "Primary label",
        onClick = { },
        secondaryLabel = "Secondary label",
        icon = "iconUri",
        placeholder = rememberVectorPainter(
            image = Icons.Default.Add,
            tintColor = Color.White,
        ),
        enabled = false,
    )
}

@Preview(
    name = "With long text",
    backgroundColor = 0xff000000,
    showBackground = true,
)
@Composable
fun SecondaryChipPreviewWithLongText() {
    SecondaryChip(
        label = "Primary label very very very very very very very very very very very very very very very very very long text",
        onClick = { }
    )
}

@Preview(
    name = "With secondary label and long text",
    backgroundColor = 0xff000000,
    showBackground = true,
)
@Composable
fun SecondaryChipPreviewWithSecondaryLabelAndLongText() {
    SecondaryChip(
        label = "Primary label very very very very very very very very long text",
        onClick = { },
        secondaryLabel = "Secondary label very very very very very very very very very long text",
        icon = "iconUri",
        placeholder = rememberVectorPainter(
            image = Icons.Default.Add,
            tintColor = Color.White,
        ),
    )
}

@Preview(
    name = "Using icon smaller than 24dp",
    backgroundColor = 0xff000000,
    showBackground = true,
)
@Composable
fun SecondaryChipPreviewUsingSmallIcon() {
    SecondaryChip(
        label = "Primary label",
        onClick = { },
        icon = "iconUri",
        placeholder = rememberVectorPainter(
            image = Icon12dp,
            tintColor = Color.White,
        )
    )
}

@Preview(
    name = "With large icon, using icon smaller than 32dp",
    backgroundColor = 0xff000000,
    showBackground = true,
)
@Composable
fun SecondaryChipPreviewWithLargeIconUsingSmallIcon() {
    SecondaryChip(
        label = "Primary label",
        onClick = { },
        icon = "iconUri",
        largeIcon = true,
        placeholder = rememberVectorPainter(
            image = Icon12dp,
            tintColor = Color.White,
        )
    )
}

@Preview(
    name = "Using icon larger than 24dp",
    backgroundColor = 0xff000000,
    showBackground = true,
)
@Composable
fun SecondaryChipPreviewUsingExtraLargeIcon() {
    SecondaryChip(
        label = "Primary label",
        onClick = { },
        icon = "iconUri",
        placeholder = rememberVectorPainter(
            image = Icon48dp,
            tintColor = Color.White,
        )
    )
}

@Preview(
    name = "With large icon, using icon larger than 32dp",
    backgroundColor = 0xff000000,
    showBackground = true,
)
@Composable
fun SecondaryChipPreviewWithLargeIconUsingExtraLargeIcon() {
    SecondaryChip(
        label = "Primary label",
        onClick = { },
        icon = "iconUri",
        largeIcon = true,
        placeholder = rememberVectorPainter(
            image = Icon48dp,
            tintColor = Color.White,
        )
    )
}

private val Icon12dp: ImageVector
    get() = ImageVector.Builder(
        name = "Icon Small",
        defaultWidth = 12f.dp,
        defaultHeight = 12f.dp,
        viewportWidth = 12f,
        viewportHeight = 12f,
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
        viewportHeight = 32f,
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
        viewportHeight = 48f,
    )
        .materialPath {
            horizontalLineToRelative(48.0f)
            verticalLineToRelative(48.0f)
            horizontalLineTo(0.0f)
            close()
        }
        .build()
