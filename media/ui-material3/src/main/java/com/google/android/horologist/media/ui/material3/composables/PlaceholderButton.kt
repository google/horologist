/*
 * Copyright 2025 The Android Open Source Project
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

package com.google.android.horologist.media.ui.material3.composables

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material3.Button
import androidx.wear.compose.material3.ButtonColors
import androidx.wear.compose.material3.ButtonDefaults
import androidx.wear.compose.material3.PlaceholderState
import androidx.wear.compose.material3.placeholder
import androidx.wear.compose.material3.placeholderShimmer
import androidx.wear.compose.material3.rememberPlaceholderState
import com.google.android.horologist.annotations.ExperimentalHorologistApi
import com.google.android.horologist.composables.R

/**
 * A placeholder button to be displayed while the contents of the [Button] is being loaded.
 */
@ExperimentalHorologistApi
@Composable
public fun PlaceholderButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
    placeholderState: PlaceholderState = rememberPlaceholderState(false),
    secondaryLabel: Boolean = true,
    icon: Boolean = true,
    colors: ButtonColors = ButtonDefaults.buttonColors(),
    enabled: Boolean = false,
    contentDescription: String = stringResource(id = R.string.horologist_placeholderchip_content_description),
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier
            .semantics {
                this.contentDescription = contentDescription
            }
            .placeholderShimmer(placeholderState),
        label = {
            Box(
                Modifier
                    .height(12.dp)
                    .fillMaxWidth()
                    .placeholder(placeholderState),
            )
        },
        icon = if (icon) {
            {
                Box(
                    Modifier
                        .size(ButtonDefaults.IconSize)
                        .clip(CircleShape)
                        .placeholder(placeholderState),
                )
            }
        } else {
            null
        },
        secondaryLabel = if (secondaryLabel) {
            {
                Box(
                    Modifier
                        .height(12.dp)
                        .fillMaxWidth()
                        .placeholder(placeholderState),
                )
            }
        } else {
            null
        },
        colors = colors,
    )
}
