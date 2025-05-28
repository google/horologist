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

package com.google.android.horologist.media.ui.material3.components.ambient

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.style.TextAlign
import androidx.wear.compose.material3.ColorScheme
import androidx.wear.compose.material3.MaterialTheme
import androidx.wear.compose.material3.Text
import com.google.android.horologist.media.ui.material3.util.TRACK_SUBTITLE_HEIGHT
import com.google.android.horologist.media.ui.material3.util.TRACK_TITLE_HEIGHT
import com.google.android.horologist.media.ui.material3.util.isLargeScreen

/** Display message for the ambient mode. */
@Composable
public fun AmbientMessageDisplay(
    message: String,
    modifier: Modifier = Modifier,
    colorScheme: ColorScheme = MaterialTheme.colorScheme,
) {
    val isLargeScreen = LocalConfiguration.current.isLargeScreen
    val height = TRACK_TITLE_HEIGHT + TRACK_SUBTITLE_HEIGHT
    Box(modifier = modifier.height(height), contentAlignment = Alignment.Center) {
        Text(
            text = message,
            modifier = Modifier.fillMaxWidth(if (isLargeScreen) 0.71f else 0.75f),
            textAlign = TextAlign.Center,
            color = colorScheme.onSurface,
            style = MaterialTheme.typography.titleMedium,
            maxLines = 1,
        )
    }
}
