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

@file:Suppress("DEPRECATION")

package com.google.android.horologist.base.ui.components

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.stringResource
import androidx.wear.compose.material.ChipDefaults
import com.google.android.horologist.annotations.ExperimentalHorologistApi
import com.google.android.horologist.compose.material.Chip

/**
 * This composable fulfils the redlines of the following components:
 * - Primary or Secondary chip - according to [chipType] value;
 * - Standard chip - when [largeIcon] value is `false`;
 * - Chip with small or large avatar - according to [largeIcon] value;
 */
@Deprecated(
    "Replaced by Chip in Horologist Material Compose library",
    replaceWith = ReplaceWith(
        "Chip(label, onClick, modifier, secondaryLabel, icon, largeIcon, placeholder, enabled)",
        "com.google.android.horologist.compose.material.Chip"
    )
)
@ExperimentalHorologistApi
@Composable
public fun StandardChip(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    secondaryLabel: String? = null,
    icon: Any? = null,
    largeIcon: Boolean = false,
    placeholder: Painter? = null,
    chipType: StandardChipType = StandardChipType.Primary,
    enabled: Boolean = true
) {
    Chip(
        label = label,
        onClick = onClick,
        modifier = modifier,
        secondaryLabel = secondaryLabel,
        icon = icon,
        largeIcon = largeIcon,
        placeholder = placeholder,
        colors = when (chipType) {
            StandardChipType.Primary -> ChipDefaults.primaryChipColors()
            StandardChipType.Secondary -> ChipDefaults.secondaryChipColors()
        },
        enabled = enabled
    )
}

/**
 * An implementation of [StandardChip] allowing full customization of the icon displayed.
 */
@Deprecated(
    "Replaced by Chip in Horologist Material Compose library",
    replaceWith = ReplaceWith(
        "Chip(labelId, onClick, modifier, secondaryLabel, icon, largeIcon, enabled)",
        "com.google.android.horologist.compose.material.Chip"
    )
)
@ExperimentalHorologistApi
@Composable
public fun StandardChip(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    secondaryLabel: String? = null,
    icon: (@Composable BoxScope.() -> Unit)? = null,
    largeIcon: Boolean = false,
    chipType: StandardChipType = StandardChipType.Primary,
    enabled: Boolean = true
) {
    Chip(
        label = label,
        onClick = onClick,
        modifier = modifier,
        secondaryLabel = secondaryLabel,
        icon = icon,
        largeIcon = largeIcon,
        colors = when (chipType) {
            StandardChipType.Primary -> ChipDefaults.primaryChipColors()
            StandardChipType.Secondary -> ChipDefaults.secondaryChipColors()
        },
        enabled = enabled
    )
}

/**
 * An alternative function to [StandardChip] that allows a string resource id to be passed as label.
 */
@Deprecated(
    "Replaced by Chip in Horologist Material Compose library",
    replaceWith = ReplaceWith(
        "Chip(labelId, onClick, modifier, secondaryLabel, icon, largeIcon, placeholder, chipType, enabled)",
        "com.google.android.horologist.compose.material.Chip"
    )
)
@ExperimentalHorologistApi
@Composable
public fun StandardChip(
    @StringRes labelId: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    secondaryLabel: String? = null,
    icon: Any? = null,
    largeIcon: Boolean = false,
    placeholder: Painter? = null,
    chipType: StandardChipType = StandardChipType.Primary,
    enabled: Boolean = true
) {
    Chip(
        label = stringResource(id = labelId),
        onClick = onClick,
        modifier = modifier,
        secondaryLabel = secondaryLabel,
        icon = icon,
        largeIcon = largeIcon,
        placeholder = placeholder,
        colors = when (chipType) {
            StandardChipType.Primary -> ChipDefaults.primaryChipColors()
            StandardChipType.Secondary -> ChipDefaults.secondaryChipColors()
        },
        enabled = enabled
    )
}

@Deprecated(
    "StandardChip is deprecated an replaced by Chip in Horologist Material Compose library"
)
@ExperimentalHorologistApi
public enum class StandardChipType {
    Primary, Secondary
}
