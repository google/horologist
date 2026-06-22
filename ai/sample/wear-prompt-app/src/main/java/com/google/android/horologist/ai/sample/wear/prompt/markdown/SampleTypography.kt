/*
 * Copyright 2025-2026 The Android Open Source Project
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

package com.google.android.horologist.ai.sample.wear.prompt.markdown

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.wear.compose.material3.LocalContentColor
import androidx.wear.compose.material3.MaterialTheme
import com.mikepenz.markdown.model.DefaultMarkdownColors
import com.mikepenz.markdown.model.DefaultMarkdownTypography

@Composable
fun sampleTypography(): DefaultMarkdownTypography {
    val link = MaterialTheme.typography.bodyMedium.copy(
        fontWeight = FontWeight.Bold,
        textDecoration = TextDecoration.Underline,
        color = Color.Blue,
    )
    val text = MaterialTheme.typography.bodyMedium
    return DefaultMarkdownTypography(
        h1 = MaterialTheme.typography.titleLarge,
        h2 = MaterialTheme.typography.titleMedium,
        h3 = MaterialTheme.typography.titleSmall,
        h4 = MaterialTheme.typography.displayLarge,
        h5 = MaterialTheme.typography.displayMedium,
        h6 = MaterialTheme.typography.displaySmall,
        text = text,
        code = MaterialTheme.typography.bodyMedium.copy(
            fontFamily = FontFamily.Monospace,
            color = LocalContentColor.current,
        ),
        quote = MaterialTheme.typography.bodyMedium.plus(SpanStyle(fontStyle = FontStyle.Italic)),
        paragraph = MaterialTheme.typography.bodyLarge,
        ordered = MaterialTheme.typography.bodyLarge,
        bullet = MaterialTheme.typography.bodyLarge,
        list = MaterialTheme.typography.bodyLarge,
        inlineCode = MaterialTheme.typography.bodyLarge.copy(
            fontFamily = FontFamily.Monospace,
            color = LocalContentColor.current,
        ),
        textLink = TextLinkStyles(style = link.toSpanStyle()),
        table = text.copy(color = Color.Unspecified),
    )
}

@Composable
fun sampleColors() = DefaultMarkdownColors(
    text = Color.White,
    codeBackground = MaterialTheme.colorScheme.background,
    inlineCodeBackground = MaterialTheme.colorScheme.background,
    dividerColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f),
    tableBackground = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.02f),
)
