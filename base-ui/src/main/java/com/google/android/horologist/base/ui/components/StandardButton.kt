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

package com.google.android.horologist.base.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.ButtonDefaults
import com.google.android.horologist.annotations.ExperimentalHorologistApi
import com.google.android.horologist.compose.material.Button
import com.google.android.horologist.compose.material.ButtonSize

/**
 * This composable fulfils the redlines of the following components:
 * - Primary, Secondary or Icon only button - according to [buttonType] value;
 * - Default, Large, Small and Extra Small button - according to [buttonSize] value;
 */
@Suppress("DEPRECATION")
@Deprecated(
    "Replaced by Button in Horologist Material Compose library",
    replaceWith = ReplaceWith(
        "Button(imageVector, contentDescription, onClick, modifier, buttonType, buttonSize, enabled)",
        "com.google.android.horologist.compose.material.Button"
    )
)
@ExperimentalHorologistApi
@Composable
public fun StandardButton(
    imageVector: ImageVector,
    contentDescription: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    buttonType: StandardButtonType = StandardButtonType.Primary,
    buttonSize: StandardButtonSize = StandardButtonSize.Default,
    enabled: Boolean = true
) {
    Button(
        imageVector = imageVector,
        contentDescription = contentDescription,
        onClick = onClick,
        modifier = modifier,
        colors = when (buttonType) {
            StandardButtonType.Primary -> ButtonDefaults.primaryButtonColors()
            StandardButtonType.Secondary -> ButtonDefaults.secondaryButtonColors()
            StandardButtonType.IconOnly -> ButtonDefaults.iconButtonColors()
        },
        buttonSize = when (buttonSize) {
            StandardButtonSize.Default -> ButtonSize.Default
            StandardButtonSize.Large -> ButtonSize.Large
            StandardButtonSize.Small -> ButtonSize.Small
            StandardButtonSize.ExtraSmall -> ButtonSize.ExtraSmall
        },
        enabled = enabled
    )
}

@Deprecated(
    "StandardButton is deprecated an replaced by Button in Horologist Material Compose library"
)
@ExperimentalHorologistApi
public enum class StandardButtonType {
    Primary,
    Secondary,
    IconOnly,
}

@Deprecated(
    "Replaced by ButtonSize in Horologist Material Compose library",
    replaceWith = ReplaceWith(
        "ButtonSize",
        "com.google.android.horologist.compose.material.ButtonSize"
    )
)
@ExperimentalHorologistApi
public enum class StandardButtonSize(
    public val iconSize: Dp,
    public val tapTargetSize: Dp
) {
    Default(iconSize = 26.dp, tapTargetSize = 52.dp),
    Large(iconSize = 30.dp, tapTargetSize = 60.dp),
    Small(iconSize = 24.dp, tapTargetSize = 48.dp),
    ExtraSmall(iconSize = 24.dp, tapTargetSize = 48.dp),
}
