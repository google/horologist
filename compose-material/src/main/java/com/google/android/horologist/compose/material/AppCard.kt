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
import androidx.compose.foundation.layout.padding
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
import com.google.android.horologist.compose.material.util.ChipIcon
import com.google.android.horologist.compose.material.util.placeholderIf
import com.google.android.horologist.images.base.paintable.Paintable

@Composable
fun AppCard(
    onClick: () -> Unit,
    appName: String,
    title: String,
    modifier: Modifier = Modifier,
    time: String? = null,
    onLongClick: (() -> Unit)? = null,
    enabled: Boolean = true,
    appImage: Paintable? = null,
    backgroundPainter: Painter = CardDefaults.cardBackgroundPainter(),
    contentColor: Color = MaterialTheme.colors.onSurfaceVariant,
    appColor: Color = contentColor,
    timeColor: Color = contentColor,
    titleColor: Color = MaterialTheme.colors.onSurface,
    placeholderState: PlaceholderState? = null,
    content: @Composable ColumnScope.() -> Unit,
) {
    val showContent = placeholderState == null || placeholderState.isShowContent

    Card(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        onLongClick = onLongClick,
        contentPadding = CardDefaults.ContentPadding,
        backgroundPainter = backgroundPainter,
        shape = MaterialTheme.shapes.large,
        placeholderState = placeholderState,
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                appImage?.let {
                    ChipIcon(icon = it, largeIcon = false, placeholderState = placeholderState)
                    Spacer(Modifier.width(6.dp))
                }
                CompositionLocalProvider(
                    LocalContentColor provides appColor,
                    LocalTextStyle provides MaterialTheme.typography.caption1
                ) {
                    Text(
                        text = appName,
                        modifier = Modifier
                            .run { if (showContent) this else this.width(60.dp) }
                            .placeholderIf(placeholderState)
                    )
                }
                Spacer(modifier = Modifier.weight(1.0f))

                time?.let {
                    CompositionLocalProvider(
                        LocalContentColor provides timeColor,
                        LocalTextStyle provides MaterialTheme.typography.caption1,
                    ) {
                        Text(
                            text = time,
                            modifier = Modifier
                                .run { if (showContent) this else this.width(30.dp) }
                                .placeholderIf(placeholderState)
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                CompositionLocalProvider(
                    LocalContentColor provides titleColor,
                    LocalTextStyle provides MaterialTheme.typography.title3
                ) {
                    Text(title, modifier = Modifier
                        .run { if (showContent) this else this.width(60.dp) }
                        .placeholderIf(placeholderState))
                }
            }
            CompositionLocalProvider(
                LocalContentColor provides contentColor,
                LocalTextStyle provides MaterialTheme.typography.body1,
            ) {
                content()
            }
        }
    }
}