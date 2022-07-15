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

import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.ButtonDefaults
import androidx.wear.compose.material.Icon

/**
 * This composable fulfils the redlines of the following components:
 * - Primary, Secondary or Icon only button - according to [buttonType] value;
 * - Default, Large, Small and Extra Small button - according to [buttonSize] value;
 */
@Composable
internal fun StandardButton(
    imageVector: ImageVector,
    contentDescription: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    buttonType: StandardButtonType = StandardButtonType.Primary,
    buttonSize: StandardButtonSize = StandardButtonSize.Default,
    enabled: Boolean = true,
) {
    Button(
        onClick = onClick,
        modifier = modifier.size(buttonSize.tapTargetSize),
        enabled = enabled,
        colors = when (buttonType) {
            StandardButtonType.Primary -> ButtonDefaults.primaryButtonColors()
            StandardButtonType.Secondary -> ButtonDefaults.secondaryButtonColors()
            StandardButtonType.IconOnly -> ButtonDefaults.iconButtonColors()
        }
    ) {
        Icon(
            imageVector = imageVector,
            contentDescription = contentDescription,
            modifier = Modifier
                .size(buttonSize.iconSize)
                .align(Alignment.Center)
        )
    }
}

internal enum class StandardButtonType {
    Primary,
    Secondary,
    IconOnly,
}

internal enum class StandardButtonSize(
    val iconSize: Dp,
    val tapTargetSize: Dp,
) {
    Default(iconSize = 26.dp, tapTargetSize = 52.dp),
    Large(iconSize = 30.dp, tapTargetSize = 60.dp),
    Small(iconSize = 24.dp, tapTargetSize = 48.dp),
    ExtraSmall(iconSize = 24.dp, tapTargetSize = 48.dp),
}
