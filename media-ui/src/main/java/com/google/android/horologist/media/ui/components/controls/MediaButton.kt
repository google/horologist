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

import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.ButtonColors
import androidx.wear.compose.material.ButtonDefaults
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material.MaterialTheme
import com.google.android.horologist.media.ui.ExperimentalHorologistMediaUiApi
import com.google.android.horologist.media.ui.components.controls.MediaButtonDefaults.mediaButtonDefaultColors
import com.google.android.horologist.media.ui.semantics.CustomSemanticsProperties.iconImageVector

/**
 * A base button for media controls.
 */
@ExperimentalHorologistMediaUiApi
@Composable
public fun MediaButton(
    onClick: () -> Unit,
    icon: ImageVector,
    contentDescription: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    colors: ButtonColors = mediaButtonDefaultColors,
    iconSize: Dp = 30.dp,
    tapTargetSize: DpSize = DpSize(48.dp, 60.dp),
    iconAlign: Alignment.Horizontal = Alignment.CenterHorizontally
) {
    Button(
        onClick = onClick,
        modifier = modifier.size(tapTargetSize),
        enabled = enabled,
        colors = colors,
    ) {
        Icon(
            modifier = Modifier
                .size(iconSize)
                .run {
                    when (iconAlign) {
                        Alignment.Start -> {
                            offset(x = -7.5.dp)
                        }
                        Alignment.End -> {
                            offset(x = 7.5.dp)
                        }
                        else -> {
                            this
                        }
                    }
                }
                .align(Alignment.Center)
                .semantics { iconImageVector = icon },
            imageVector = icon,
            contentDescription = contentDescription
        )
    }
}

public object MediaButtonDefaults {
    public val mediaButtonDefaultColors: ButtonColors
        @Composable
        get() = ButtonDefaults.buttonColors(
            backgroundColor = Color.Transparent,
            contentColor = MaterialTheme.colors.onSurface,
            disabledBackgroundColor = Color.Transparent
        )
}
