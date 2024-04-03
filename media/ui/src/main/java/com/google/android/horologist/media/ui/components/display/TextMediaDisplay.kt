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

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.foundation.text.appendInlineContent
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.Placeholder
import androidx.compose.ui.text.PlaceholderVerticalAlign
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text
import com.google.android.horologist.annotations.ExperimentalHorologistApi
import com.google.android.horologist.images.base.paintable.Paintable
import com.google.android.horologist.media.ui.components.controls.MediaTitleIcon
import com.google.android.horologist.media.ui.util.isLargeScreen

/**
 * A simple text only display showing artist and title in two separated rows.
 */
@ExperimentalHorologistApi
@Composable
public fun TextMediaDisplay(
    title: String,
    subtitle: String,
    titleIcon: Paintable? = null,
    modifier: Modifier = Modifier,
) {
    val isLargeScreen = LocalConfiguration.current.isLargeScreen

    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        val textStyle = MaterialTheme.typography.button
        val text = buildAnnotatedString {
            if (titleIcon != null) {
                appendInlineContent(id = "iconSlot")
                append(" ")
            }
            append(title)
        }
        val inlineContent = if (titleIcon != null) {
            mapOf(
                "iconSlot" to InlineTextContent(
                    Placeholder(textStyle.fontSize, textStyle.fontSize, PlaceholderVerticalAlign.TextCenter),
                ) {
                    MediaTitleIcon(titleIcon)
                },
            )
        } else {
            emptyMap()
        }
        Text(
            text = text,
            inlineContent = inlineContent,
            modifier = Modifier
                // 89.76% of parent equals 4.16% of screen width applied on each side when
                // applied on top of the 9.38% in the ConstraintLayout.
                .fillMaxWidth(0.8976f)
                .padding(
                    top = if (isLargeScreen) 0.dp else 2.dp,
                    bottom = if (isLargeScreen) 3.dp else 1.dp,
                ),
            color = MaterialTheme.colors.onBackground,
            textAlign = TextAlign.Center,
            maxLines = 1,
            style = textStyle,
        )
        Text(
            text = subtitle,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 1.dp, bottom = .6.dp),
            color = MaterialTheme.colors.onBackground,
            textAlign = TextAlign.Center,
            overflow = TextOverflow.Ellipsis,
            maxLines = 1,
            style = MaterialTheme.typography.body2,
        )
    }
}
