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

package com.google.android.horologist.media.ui.components.controls

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material.icons.filled.ShuffleOn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.ToggleButton
import androidx.wear.compose.material.ToggleButtonColors
import androidx.wear.compose.material.ToggleButtonDefaults.toggleButtonColors
import com.google.android.horologist.annotations.ExperimentalHorologistApi
import com.google.android.horologist.base.ui.util.DECORATIVE_ELEMENT_CONTENT_DESCRIPTION
import com.google.android.horologist.media.ui.R

@ExperimentalHorologistApi
@Composable
public fun ShuffleToggleButton(
    shuffleOn: Boolean,
    onToggle: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    colors: ToggleButtonColors = toggleButtonColors(
        checkedBackgroundColor = Color.Transparent,
        checkedContentColor = MaterialTheme.colors.onSurface,
        uncheckedBackgroundColor = Color.Transparent,
        uncheckedContentColor = MaterialTheme.colors.onSurface
    )
) {
    val onLabel =
        stringResource(id = R.string.horologist_shuffle_button_on_content_description)
    val offLabel =
        stringResource(id = R.string.horologist_shuffle_button_off_content_description)
    ToggleButton(
        modifier = modifier.semantics {
            stateDescription = if (shuffleOn) onLabel else offLabel
        },
        onCheckedChange = onToggle,
        enabled = enabled,
        colors = colors,
        checked = shuffleOn
    ) {
        val icon = if (shuffleOn) Icons.Default.ShuffleOn else Icons.Default.Shuffle
        Icon(
            imageVector = icon,
            contentDescription = DECORATIVE_ELEMENT_CONTENT_DESCRIPTION
        )
    }
}
