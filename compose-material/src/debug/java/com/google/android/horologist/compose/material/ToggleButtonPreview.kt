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
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.VolumeOff
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview

@Preview(
    backgroundColor = 0xff000000, showBackground = true
)
@Composable
fun ToggleButtonDefaultPreviewIcon() {
    ToggleButton(icon = Icons.Filled.AirplanemodeActive, onCheckedChange = {})
}

@Preview(
    backgroundColor = 0xff000000, showBackground = true
)
@Composable
fun ToggleButtonDefaultPreviewIcon2() {
    ToggleButton(icon = Icons.Filled.AirplanemodeInactive, checked = false, onCheckedChange = {})
}

@Preview(
    backgroundColor = 0xff000000, showBackground = true
)
@Composable
fun ToggleButtonDefaultPreviewIcon3() {
    ToggleButton(icon = Icons.Filled.AirplanemodeActive, checked = false, onCheckedChange = {})
}

@Preview(
    backgroundColor = 0xff000000, showBackground = true
)
@Composable
fun ToggleButtonDefaultPreviewText1() {
    ToggleButton(
        checked = false, onCheckedChange = {}, text = "Monday"
    )
}

@Preview(
    backgroundColor = 0xff000000, showBackground = true
)
@Composable
fun ToggleButtonDefaultPreviewText2() {
    ToggleButton(
        checked = true, onCheckedChange = {}, text = "Mon"
    )
}

@Preview(
    backgroundColor = 0xff000000, showBackground = true
)
@Composable
fun ToggleButtonSmallPreviewIcon1() {
    ToggleButton(
        icon = Icons.Filled.VolumeUp, onCheckedChange = {}, smallSize = true
    )
}

@Preview(
    backgroundColor = 0xff000000, showBackground = true
)
@Composable
fun ToggleButtonSmallPreviewIcon2() {
    ToggleButton(
        icon = Icons.Filled.VolumeOff,
        checked = false,
        onCheckedChange = {},
        iconRtlMode = IconRtlMode.Mirrored,
        smallSize = true
    )
}

@Preview(
    backgroundColor = 0xff000000, showBackground = true
)
@Composable
fun ToggleButtonSmallPreviewIcon3() {
    ToggleButton(
        icon = Icons.Filled.VolumeUp,
        checked = false,
        onCheckedChange = {},
        smallSize = true
    )
}

@Preview(
    backgroundColor = 0xff000000, showBackground = true
)
@Composable
fun ToggleButtonIconOnlyPreviewOutlinedChecked() {
    ToggleButton(
        icon = Icons.Outlined.FavoriteBorder,
        checked = true,
        onCheckedChange = {},
        iconOnly = true
    )
}

@Preview(
    backgroundColor = 0xff000000, showBackground = true
)
@Composable
fun ToggleButtonIconOnlyPreviewOutlinedUnchecked() {
    ToggleButton(
        icon = Icons.Outlined.FavoriteBorder,
        checked = false,
        onCheckedChange = {},
        iconOnly = true
    )
}

@Preview(
    backgroundColor = 0xff000000, showBackground = true
)
@Composable
fun ToggleButtonIconOnlyPreviewFilledChecked() {
    ToggleButton(
        icon = Icons.Filled.Favorite,
        checked = true,
        onCheckedChange = {},
        iconOnly = true
    )
}

@Preview(
    backgroundColor = 0xff000000, showBackground = true
)
@Composable
fun ToggleButtonIconOnlyPreviewFilledUnchecked() {
    ToggleButton(
        icon = Icons.Filled.Favorite,
        checked = false,
        onCheckedChange = {},
        iconOnly = true
    )
}
