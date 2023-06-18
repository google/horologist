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
import androidx.compose.material.icons.filled.AirplanemodeActive
import androidx.compose.material.icons.filled.AirplanemodeInactive
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material.icons.filled.Flight
import androidx.compose.material.icons.filled.NoFlash
import androidx.compose.material.icons.filled.VolumeOff
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview

@Preview(
    backgroundColor = 0xff000000,
    showBackground = true
)
@Composable
fun ToggleButtonDefaultPreviewIcon() {
    ToggleButton(
        onCheckedChange = {},
        icon = Icons.Filled.AirplanemodeActive
    )
}

@Preview(
    backgroundColor = 0xff000000,
    showBackground = true
)
@Composable
fun ToggleButtonDefaultPreviewIcon2() {
    ToggleButton(
        checked = false,
        onCheckedChange = {},
        icon = Icons.Filled.AirplanemodeInactive
    )
}

@Preview(
    backgroundColor = 0xff000000,
    showBackground = true
)
@Composable
fun ToggleButtonDefaultPreviewIcon3() {
    ToggleButton(
        checked = false,
        onCheckedChange = {},
        icon = Icons.Filled.AirplanemodeActive
    )
}

@Preview(
    backgroundColor = 0xff000000,
    showBackground = true
)
@Composable
fun ToggleButtonDefaultPreviewText1() {
    ToggleButton(
        checked = false,
        onCheckedChange = {},
        text = "Monday"
    )
}

@Preview(
    backgroundColor = 0xff000000,
    showBackground = true
)
@Composable
fun ToggleButtonDefaultPreviewText2() {
    ToggleButton(
        checked = true,
        onCheckedChange = {},
        text = "Mon"
    )
}

@Preview(
    backgroundColor = 0xff000000,
    showBackground = true
)
@Composable
fun ToggleButtonSmallPreviewIcon1() {
    ToggleButton(onCheckedChange = {},
        icon = Icons.Filled.VolumeUp,
        variant = ToggleButtonVariants.Small
    )
}

@Preview(
    backgroundColor = 0xff000000,
    showBackground = true
)
@Composable
fun ToggleButtonSmallPreviewIcon2() {
    ToggleButton(onCheckedChange = {},
        checked = false,
        icon = Icons.Filled.VolumeOff,
        variant = ToggleButtonVariants.Small
    )
}

@Preview(
    backgroundColor = 0xff000000,
    showBackground = true
)
@Composable
fun ToggleButtonSmallPreviewIcon3() {
    ToggleButton(onCheckedChange = {},
        checked = false,
        icon = Icons.Filled.VolumeUp,
        variant = ToggleButtonVariants.Small
    )
}

@Preview(
    backgroundColor = 0xff000000,
    showBackground = true
)
@Composable
fun ToggleButtonIconOnlyPreviewOutlinedChecked() {
    ToggleButton(
        checked = true,
        onCheckedChange = {},
        icon = Icons.Outlined.FavoriteBorder,
        variant = ToggleButtonVariants.IconOnly
    )
}

@Preview(
    backgroundColor = 0xff000000,
    showBackground = true
)
@Composable
fun ToggleButtonIconOnlyPreviewFilledChecked() {
    ToggleButton(
        checked = true,
        onCheckedChange = {},
        icon = Icons.Filled.Favorite,
        variant = ToggleButtonVariants.IconOnly
    )
}