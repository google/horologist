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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Forward10
import androidx.compose.material.icons.filled.Forward30
import androidx.compose.material.icons.filled.Forward5
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material.icons.filled.Replay10
import androidx.compose.material.icons.filled.Replay30
import androidx.compose.material.icons.filled.Replay5
import androidx.compose.material.icons.materialIcon
import androidx.compose.material.icons.materialPath
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.ButtonColors
import androidx.wear.compose.material.ButtonDefaults
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material.MaterialTheme
import com.google.android.horologist.annotations.ExperimentalHorologistApi
import com.google.android.horologist.media.ui.components.controls.MediaButtonDefaults.mediaButtonDefaultColors

/**
 * A base button for media controls.
 */
@ExperimentalHorologistApi
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
        colors = colors
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
                .align(Alignment.Center),
            imageVector = icon,
            contentDescription = contentDescription
        )
    }
}

@ExperimentalHorologistApi
public object MediaButtonDefaults {
    public val mediaButtonDefaultColors: ButtonColors
        @Composable
        get() = ButtonDefaults.buttonColors(
            backgroundColor = Color.Transparent,
            contentColor = MaterialTheme.colors.onSurface,
            disabledBackgroundColor = Color.Transparent
        )

    public fun seekBackIcon(seekButtonIncrement: SeekButtonIncrement): ImageVector =
        when (seekButtonIncrement) {
            SeekButtonIncrement.Five -> Icons.Default.Replay5
            SeekButtonIncrement.Ten -> Icons.Default.Replay10
            SeekButtonIncrement.Thirty -> Icons.Default.Replay30
            else -> Icons.Default.Replay
        }

    public fun seekForwardIcon(seekButtonIncrement: SeekButtonIncrement): ImageVector =
        when (seekButtonIncrement) {
            SeekButtonIncrement.Five -> Icons.Default.Forward5
            SeekButtonIncrement.Ten -> Icons.Default.Forward10
            SeekButtonIncrement.Thirty -> Icons.Default.Forward30
            else -> ForwardEmpty
        }

    // Icons.Default.Forward is not the same group as 5, 10 and 30 variant
    private val ForwardEmpty = materialIcon(name = "Filled.ForwardEmpty") {
        materialPath {
            moveTo(18.0f, 13.0f)
            curveToRelative(0.0f, 3.31f, -2.69f, 6.0f, -6.0f, 6.0f)
            reflectiveCurveToRelative(-6.0f, -2.69f, -6.0f, -6.0f)
            reflectiveCurveToRelative(2.69f, -6.0f, 6.0f, -6.0f)
            verticalLineToRelative(4.0f)
            lineToRelative(5.0f, -5.0f)
            lineToRelative(-5.0f, -5.0f)
            verticalLineToRelative(4.0f)
            curveToRelative(-4.42f, 0.0f, -8.0f, 3.58f, -8.0f, 8.0f)
            curveToRelative(0.0f, 4.42f, 3.58f, 8.0f, 8.0f, 8.0f)
            reflectiveCurveToRelative(8.0f, -3.58f, 8.0f, -8.0f)
            horizontalLineTo(18.0f)
            close()
        }
    }
}
