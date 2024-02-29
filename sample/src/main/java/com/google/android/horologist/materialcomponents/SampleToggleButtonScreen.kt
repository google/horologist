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

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AirplanemodeActive
import androidx.compose.material.icons.filled.AirplanemodeInactive
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.google.android.horologist.compose.layout.ScalingLazyColumn
import com.google.android.horologist.compose.layout.ScalingLazyColumnDefaults
import com.google.android.horologist.compose.layout.ScreenScaffold
import com.google.android.horologist.compose.layout.rememberResponsiveColumnState
import com.google.android.horologist.compose.material.ToggleButton
import com.google.android.horologist.compose.material.ToggleButtonDefaults
import com.google.android.horologist.images.base.paintable.ImageVectorPaintable.Companion.asPaintable

@Composable
internal fun SampleToggleButtonScreen(
    modifier: Modifier = Modifier,
) {
    val columnState = rememberResponsiveColumnState(
        contentPadding = ScalingLazyColumnDefaults.padding(),
    )

    ScreenScaffold(scrollState = columnState) {
        ScalingLazyColumn(
            columnState = columnState,
            modifier = modifier,
        ) {
            item {
                ToggleButton(
                    text = "Monday",
                    onCheckedChanged = {},
                )
            }
            item {
                ToggleButton(
                    text = "Monday",
                    onCheckedChanged = {},
                    checked = false,
                )
            }
            item {
                ToggleButton(
                    checkedIcon = Icons.Filled.AirplanemodeActive.asPaintable(),
                    notCheckedIcon = Icons.Filled.AirplanemodeInactive.asPaintable(),
                    contentDescription = "contentDescription",
                    onCheckedChanged = {},
                    smallSize = true,
                )
            }
            item {
                ToggleButton(
                    checkedIcon = Icons.Filled.AirplanemodeActive.asPaintable(),
                    notCheckedIcon = Icons.Filled.AirplanemodeInactive.asPaintable(),
                    contentDescription = "contentDescription",
                    onCheckedChanged = {},
                    checked = false,
                    smallSize = true,
                )
            }
            item {
                ToggleButton(
                    checkedIcon = Icons.Filled.AirplanemodeActive.asPaintable(),
                    notCheckedIcon = Icons.Filled.AirplanemodeInactive.asPaintable(),
                    contentDescription = "contentDescription",
                    onCheckedChanged = {},
                    colors = ToggleButtonDefaults.iconOnlyColors(),
                    smallSize = true,
                )
            }
            item {
                ToggleButton(
                    checkedIcon = Icons.Filled.AirplanemodeActive.asPaintable(),
                    notCheckedIcon = Icons.Filled.AirplanemodeInactive.asPaintable(),
                    contentDescription = "contentDescription",
                    onCheckedChanged = {},
                    checked = false,
                    colors = ToggleButtonDefaults.iconOnlyColors(),
                    smallSize = true,
                )
            }
        }
    }
}
