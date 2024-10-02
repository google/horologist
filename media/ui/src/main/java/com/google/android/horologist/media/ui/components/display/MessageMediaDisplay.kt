/*
 * Copyright 2023 The Android Open Source Project
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

package com.google.android.horologist.media.ui.components.display

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text
import com.google.android.horologist.annotations.ExperimentalHorologistApi
import com.google.android.horologist.images.base.paintable.Paintable
import com.google.android.horologist.media.ui.components.controls.MediaTitleIcon

/**
 * A simple text-only display showing status information or a message.
 */
@ExperimentalHorologistApi
@Composable
public fun MessageMediaDisplay(
    modifier: Modifier = Modifier,
    message: String,
    appIcon: Paintable? = null,
) {
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        val style = MaterialTheme.typography.body2
        Row(
            modifier = Modifier
                // 84.49% of parent equals 6.3% of screen width applied on each side when
                // applied on top of the 9.38% in the ConstraintLayout.
                .fillMaxWidth(0.8449f),
            horizontalArrangement = Arrangement.Center,
        ) {
            appIcon?.let {
                Box(
                    modifier = Modifier.size(16.dp),
                ) {
                    MediaTitleIcon(it)
                }
            }
            Text(
                text = message.orEmpty(),
                modifier = Modifier
                    .padding(
                        start = if (appIcon != null) 8.dp else 12.dp,
                    )
                    .semantics { heading() },
                color = MaterialTheme.colors.onBackground,
                textAlign = TextAlign.Left,
                overflow = TextOverflow.Ellipsis,
                maxLines = 2,
                style = style,
            )
        }
        Text("", style = style, minLines = 2)
    }
}
