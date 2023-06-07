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

package com.google.android.horologist.compose.material

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text
import com.google.android.horologist.annotations.ExperimentalHorologistApi
import com.google.android.horologist.compose.material.util.DECORATIVE_ELEMENT_CONTENT_DESCRIPTION

/**
 * A title heading to group and identify items.
 */
@ExperimentalHorologistApi
@Composable
public fun Title(
    @StringRes textId: Int,
    modifier: Modifier = Modifier,
    textType: TextType = TextType.Primary,
    icon: ImageVector? = null,
    iconSize: Dp = 24.dp
) {
    Title(
        text = stringResource(id = textId),
        modifier = modifier,
        textType = textType,
        icon = icon,
        iconSize = iconSize
    )
}

/**
 * A title heading to group and identify items.
 */
@ExperimentalHorologistApi
@Composable
public fun Title(
    text: String,
    modifier: Modifier = Modifier,
    textType: TextType = TextType.Primary,
    icon: ImageVector? = null,
    iconTint: Color = MaterialTheme.colors.onBackground,
    iconSize: Dp = 24.dp
) {
    Row(
        modifier = Modifier
            .padding(horizontal = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        icon?.let {
            Icon(
                imageVector = icon,
                contentDescription = DECORATIVE_ELEMENT_CONTENT_DESCRIPTION,
                modifier = Modifier
                    .size(iconSize)
                    .clip(CircleShape),
                tint = iconTint
            )
            Spacer(modifier = Modifier.width(6.dp))
        }
        Text(
            text = text,
            modifier = modifier
                .semantics { heading() }
                .fillMaxWidth(),
            color = when (textType) {
                TextType.Primary -> MaterialTheme.colors.onSurfaceVariant
                TextType.Secondary -> MaterialTheme.colors.onBackground
            },
            textAlign = when (textType) {
                TextType.Primary -> TextAlign.Center
                TextType.Secondary -> TextAlign.Left
            },
            overflow = TextOverflow.Ellipsis,
            maxLines = 3,
            style = when (textType) {
                TextType.Primary -> MaterialTheme.typography.button
                TextType.Secondary -> MaterialTheme.typography.caption1
            }
        )
    }
}

@ExperimentalHorologistApi
public enum class TextType {
    Primary,
    Secondary,
}
