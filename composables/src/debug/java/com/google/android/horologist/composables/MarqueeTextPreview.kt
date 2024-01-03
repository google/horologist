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

package com.google.android.horologist.composables

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.foundation.text.appendInlineContent
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FireTruck
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.Placeholder
import androidx.compose.ui.text.PlaceholderVerticalAlign
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.android.horologist.compose.tools.WearPreview
import kotlin.time.Duration.Companion.seconds

@WearPreview
@Composable
fun MarqueeTextTypicalPreview() {
    MarqueeText(
        text = "A very long text strings",
        modifier = Modifier
            .background(Color.DarkGray)
            .width(100.dp),
        textAlign = TextAlign.Center,
    )
}

@WearPreview
@Composable
fun MarqueeTextStyledPreview() {
    MarqueeText(
        text = buildAnnotatedString {
            append("A very long ")
            withStyle(style = SpanStyle(fontStyle = FontStyle.Italic)) {
                append("italic")
            }
            append(" text")
        },
        modifier = Modifier
            .background(Color.DarkGray)
            .width(100.dp),
        textAlign = TextAlign.Center,
    )
}

@WearPreview
@Composable
fun MarqueeTextShortTextCenterPreview() {
    MarqueeText(
        text = "A",
        modifier = Modifier
            .background(Color.DarkGray)
            .width(100.dp),
        textAlign = TextAlign.Center,
    )
}

@WearPreview
@Composable
fun MarqueeTextShortTextRightPreview() {
    MarqueeText(
        text = "A",
        modifier = Modifier
            .background(Color.DarkGray)
            .width(100.dp),
        textAlign = TextAlign.Right,
    )
}

@WearPreview
@Composable
fun MarqueeTextShortTextRtlPreview() {
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        MarqueeText(
            text = "A",
            modifier = Modifier
                .background(Color.DarkGray)
                .width(100.dp),
        )
    }
}

@WearPreview
@Composable
fun MarqueeTextConstantScrollingPreview() {
    MarqueeText(
        text = "A very long text strings",
        modifier = Modifier
            .background(Color.DarkGray)
            .width(100.dp),
        textAlign = TextAlign.Center,
        pauseTime = 0.seconds,
    )
}

@WearPreview
@Composable
fun MarqueeTextConstantWithIconScrollingPreview() {
    MarqueeText(
        text = buildAnnotatedString {
            appendInlineContent("firetruck")
            append("A long string next to an icon")
        },
        inlineContent = mapOf(
            "firetruck" to InlineTextContent(Placeholder(20.sp, 20.sp, PlaceholderVerticalAlign.Center)) {
                Image(
                    imageVector = Icons.Default.FireTruck,
                    contentDescription = null,
                    colorFilter = ColorFilter.tint(Color.White),
                    contentScale = ContentScale.FillHeight,
                )
            },
        ),
        modifier = Modifier
            .background(Color.DarkGray)
            .width(100.dp),
        textAlign = TextAlign.Center,
        pauseTime = 0.seconds,
    )
}
