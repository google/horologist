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

package com.google.android.horologist.media.ui.material3.components.display

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material3.ColorScheme
import androidx.wear.compose.material3.MaterialTheme
import androidx.wear.compose.material3.Text
import com.google.android.horologist.images.base.paintable.Paintable
import com.google.android.horologist.media.ui.material3.components.controls.MediaTitleIcon
import com.google.android.horologist.media.ui.material3.util.MEDIA_TITLE_EDGE_GRADIENT_WIDTH
import com.google.android.horologist.media.ui.material3.util.MEDIA_TITLE_ICON_SIZE
import com.google.android.horologist.media.ui.material3.util.TRACK_SUBTITLE_HEIGHT
import com.google.android.horologist.media.ui.material3.util.TRACK_TITLE_HEIGHT

/** A simple text only display showing artist and title in two separated rows. */
@Composable
public fun TextMediaDisplay(
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier,
    titleIcon: Paintable? = null,
    colorScheme: ColorScheme = MaterialTheme.colorScheme,
    titleOverflow: TextOverflow = TextOverflow.Ellipsis,
    subtitleOverflow: TextOverflow = TextOverflow.Ellipsis,
    titleSoftWrap: Boolean = true,
    subtitleSoftWrap: Boolean = true,
) {
    val textMeasurer = rememberTextMeasurer()
    var measuredTextWidth by remember { mutableStateOf(0.dp) }
    with(LocalDensity.current) {
        measuredTextWidth = textMeasurer.measure(
            text = AnnotatedString(title),
            style = MaterialTheme.typography.titleMedium,
            constraints = Constraints(maxWidth = Int.MAX_VALUE),
        ).size.width.toDp()
    }
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp
    val titleWidthRatio = if (titleIcon != null) 0.648f else 0.6672f
    val subtitleWidthRatio = if (titleIcon != null) 0.71f else 0.75f
    val isTextLongerThanDrawableArea =
        measuredTextWidth.value > titleWidthRatio * screenWidth.value
    val shouldShowTitleSpacer = titleIcon != null || isTextLongerThanDrawableArea

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(titleWidthRatio).height(TRACK_TITLE_HEIGHT),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            titleIcon?.let {
                Box(modifier = Modifier.size(MEDIA_TITLE_ICON_SIZE)) {
                    MediaTitleIcon(paintableRes = it, tint = colorScheme.primary)
                }
            }
            if (shouldShowTitleSpacer) {
                Spacer(modifier = Modifier.width(MEDIA_TITLE_EDGE_GRADIENT_WIDTH))
            }
            Text(
                text = title,
                modifier =
                    if (isTextLongerThanDrawableArea) {
                        Modifier.drawRightFadeGradient(MEDIA_TITLE_EDGE_GRADIENT_WIDTH)
                    } else {
                        Modifier
                    },
                color = colorScheme.onSurface,
                maxLines = 1,
                overflow = titleOverflow,
                softWrap = titleSoftWrap,
                style = MaterialTheme.typography.titleMedium,
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(subtitleWidthRatio).height(TRACK_SUBTITLE_HEIGHT),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = subtitle,
                color = colorScheme.onSurface,
                textAlign = TextAlign.Center,
                overflow = subtitleOverflow,
                softWrap = subtitleSoftWrap,
                maxLines = 1,
                style = MaterialTheme.typography.bodyMedium,
            )
        }
    }
}

private fun Modifier.drawRightFadeGradient(edgeGradientWidth: Dp) = this.drawWithCache {
    val width = edgeGradientWidth.toPx()
    val rightBrush =
        Brush.horizontalGradient(
            listOf(Color.Transparent, Color.Black),
            startX = size.width,
            endX = size.width - width,
        )
    onDrawWithContent {
        drawContent()
        drawRect(
            size = Size(width, size.height),
            topLeft = Offset(size.width - width, 0f),
            brush = rightBrush,
            blendMode = BlendMode.DstIn,
        )
    }
}
