/*
 * Copyright 2024 The Android Open Source Project
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

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.CardDefaults
import androidx.wear.compose.material.LocalContentColor
import androidx.wear.compose.material.LocalTextStyle
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.PlaceholderState
import androidx.wear.compose.material.Text
import com.google.android.horologist.compose.material.util.placeholderIf

@Composable
fun TitleCard(
    onClick: () -> Unit,
    title: String,
    modifier: Modifier = Modifier,
    onLongClick: (() -> Unit)? = null,
    enabled: Boolean = true,
    time: String? = null,
    backgroundPainter: Painter = CardDefaults.cardBackgroundPainter(),
    contentColor: Color = MaterialTheme.colors.onSurfaceVariant,
    titleColor: Color = MaterialTheme.colors.onSurface,
    timeColor: Color = contentColor,
    placeholderState: PlaceholderState? = null,
    content: @Composable ColumnScope.() -> Unit,
) {
    Card(
        onClick = onClick,
        onLongClick = onLongClick,
        modifier = modifier,
        enabled = enabled,
        contentPadding = CardDefaults.ContentPadding,
        backgroundPainter = backgroundPainter,
        role = null,
        shape = MaterialTheme.shapes.large,
        placeholderState = placeholderState,
    ) {
        val showContent = placeholderState == null || placeholderState.isShowContent

        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                CompositionLocalProvider(
                    LocalContentColor provides titleColor,
                    LocalTextStyle provides MaterialTheme.typography.title3,
                ) {
                    Text(
                        text = title,
                        modifier = Modifier
                            .run { if (showContent) this else this.width(60.dp) }
                            .placeholderIf(placeholderState),
                    )
                }
                time?.let {
                    Spacer(modifier = Modifier.weight(1.0f))
                    CompositionLocalProvider(
                        LocalContentColor provides timeColor,
                        LocalTextStyle provides MaterialTheme.typography.caption1,
                    ) {
                        Text(
                            text = time,
                            modifier = Modifier
                                .run { if (showContent) this else this.width(30.dp) }
                                .placeholderIf(placeholderState),
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(2.dp))
            CompositionLocalProvider(
                LocalContentColor provides contentColor,
                LocalTextStyle provides MaterialTheme.typography.body1,
            ) {
                content()
            }
        }
    }
}
