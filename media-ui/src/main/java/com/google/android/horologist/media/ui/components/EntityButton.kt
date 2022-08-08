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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.ButtonDefaults
import androidx.wear.compose.material.Icon
import com.google.android.horologist.media.ui.ExperimentalHorologistMediaUiApi

/**
 * A standard visual design for a button on the Entity screen.
 */
@ExperimentalHorologistMediaUiApi
@Composable
public fun EntityButton(
    onClick: () -> Unit,
    icon: ImageVector,
    contentDescription: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    Button(
        modifier = modifier,
        colors = ButtonDefaults.secondaryButtonColors(),
        onClick = onClick,
        enabled = enabled
    ) {
        Icon(
            modifier = Modifier.size(ButtonDefaults.SmallButtonSize),
            imageVector = icon,
            contentDescription = contentDescription
        )
    }
}
