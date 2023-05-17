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

package com.google.android.horologist.base.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Image
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.google.android.horologist.base.ui.utils.FontScaleUtils

@Preview(
    name = "With long text",
    backgroundColor = 0xff000000,
    showBackground = true
)
@Composable
fun StandardChipPreviewWithLongText() {
    StandardChip(
        label = "Primary label very very very very very very very very very very very very very very very very very long text",
        onClick = { }
    )
}

@Preview(
    name = "With long text",
    backgroundColor = 0xff000000,
    showBackground = true,
    fontScale = FontScaleUtils.largestFontScale,
    group = "Largest font scale"
)
@Composable
fun StandardChipPreviewWithLongTextAndLargestFontScale() {
    StandardChip(
        label = "Primary label very very very very very very very very very very very very very very very very very long text",
        onClick = { }
    )
}

@Preview(
    name = "With secondary label and long text",
    backgroundColor = 0xff000000,
    showBackground = true
)
@Composable
fun StandardChipPrimaryPreviewWithSecondaryLabelAndLongText() {
    StandardChip(
        label = "Primary label very very very very very very very very long text",
        onClick = { },
        secondaryLabel = "Secondary label very very very very very very very very very long text",
        icon = Icons.Default.Image
    )
}

@Preview(
    name = "With secondary label and long text",
    backgroundColor = 0xff000000,
    showBackground = true,
    fontScale = FontScaleUtils.largestFontScale,
    group = "Largest font scale"
)
@Composable
fun StandardChipPrimaryPreviewWithSecondaryLabelAndLongTextAndLargestFontScale() {
    StandardChip(
        label = "Primary label very very very very very very very very long text",
        onClick = { },
        secondaryLabel = "Secondary label very very very very very very very very very long text",
        icon = Icons.Default.Image
    )
}

@Preview(
    name = "With long text",
    backgroundColor = 0xff000000,
    showBackground = true
)
@Composable
fun StandardChipSecondaryPreviewPreviewWithLongText() {
    StandardChip(
        label = "Primary label very very very very very very very very very very very very very very very very very long text",
        onClick = { },
        chipType = StandardChipType.Secondary
    )
}

@Preview(
    name = "With long text",
    backgroundColor = 0xff000000,
    showBackground = true,
    fontScale = FontScaleUtils.largestFontScale,
    group = "Largest font scale"
)
@Composable
fun StandardChipSecondaryPreviewPreviewWithLongTextAndLargestFontScale() {
    StandardChip(
        label = "Primary label very very very very very very very very very very very very very very very very very long text",
        onClick = { },
        chipType = StandardChipType.Secondary
    )
}

@Preview(
    name = "With secondary label and long text",
    backgroundColor = 0xff000000,
    showBackground = true
)
@Composable
fun StandardChipSecondaryPreviewPreviewWithSecondaryLabelAndLongText() {
    StandardChip(
        label = "Primary label very very very very very very very very long text",
        onClick = { },
        secondaryLabel = "Secondary label very very very very very very very very very long text",
        icon = Icons.Default.Image,
        chipType = StandardChipType.Secondary
    )
}

@Preview(
    name = "With secondary label and long text",
    backgroundColor = 0xff000000,
    showBackground = true,
    fontScale = FontScaleUtils.largestFontScale,
    group = "Largest font scale"
)
@Composable
fun StandardChipSecondaryPreviewPreviewWithSecondaryLabelAndLongTextAndLargestFontScale() {
    StandardChip(
        label = "Primary label very very very very very very very very long text",
        onClick = { },
        secondaryLabel = "Secondary label very very very very very very very very very long text",
        icon = Icons.Default.Image,
        chipType = StandardChipType.Secondary
    )
}
