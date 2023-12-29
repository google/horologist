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

import androidx.compose.material.icons.materialPath
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.android.horologist.images.base.paintable.ImageVectorPaintable

@Preview(
    name = "Standard",
    backgroundColor = 0xff000000,
    showBackground = true,
)
@Composable
fun ChipIconWithProgressPreview() {
    ChipIconWithProgress()
}

@Preview(
    name = "With 75 percent download complete",
    backgroundColor = 0xff000000,
    showBackground = true,
)
@Composable
fun ChipIconWithProgressInProgressPreview() {
    ChipIconWithProgress(progress = 75f)
}

@Preview(
    name = "With 75 percent download complete with large icon",
    backgroundColor = 0xff000000,
    showBackground = true,
)
@Composable
fun ChipIconWithProgressInProgressLargeIconPreview() {
    ChipIconWithProgress(
        progress = 75f,
        icon = ImageVectorPaintable(Icon48dp),
        largeIcon = true,
    )
}

@Preview(
    name = "With 75 percent download complete with medium icon",
    backgroundColor = 0xff000000,
    showBackground = true,
)
@Composable
fun ChipIconWithProgressInProgressMediumIconPreview() {
    ChipIconWithProgress(progress = 75f, icon = ImageVectorPaintable(Icon32dp))
}

@Preview(
    name = "With 75 percent download complete with small icon",
    backgroundColor = 0xff000000,
    showBackground = true,
)
@Composable
fun ChipIconWithProgressInProgressSmallIconPreview() {
    ChipIconWithProgress(progress = 75f, icon = ImageVectorPaintable(Icon12dp))
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
