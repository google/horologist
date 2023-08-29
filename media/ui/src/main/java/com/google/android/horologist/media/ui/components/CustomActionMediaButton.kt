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

package com.google.android.horologist.media.ui.components

import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.ButtonColors
import androidx.wear.compose.material.ButtonDefaults
import coil.compose.AsyncImage
import com.google.android.horologist.annotations.ExperimentalHorologistApi

/**
 * A base button for custom action media controls.
 */
@ExperimentalHorologistApi
@Composable
public fun CustomActionMediaButton(
    onClick: () -> Unit,
    contentDescription: String,
    iconUri: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    colors: ButtonColors = ButtonDefaults.iconButtonColors(),
    tapTargetSize: DpSize = DpSize(48.dp, 60.dp),
) {
    Button(
        onClick = onClick,
        modifier = modifier.size(tapTargetSize),
        enabled = enabled,
        colors = colors,
    ) {
        AsyncImage(
            model = iconUri,
            contentDescription = contentDescription,
            colorFilter = ColorFilter.tint(colors.contentColor(enabled = enabled).value),
        )
    }
}
