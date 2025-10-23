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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.wear.compose.material3.ColorScheme
import androidx.wear.compose.material3.MaterialTheme
import androidx.wear.compose.material3.PlaceholderState
import androidx.wear.compose.material3.placeholder
import androidx.wear.compose.material3.placeholderShimmer
import androidx.wear.compose.material3.rememberPlaceholderState
import com.google.android.horologist.media.ui.material3.util.TRACK_SUBTITLE_HEIGHT
import com.google.android.horologist.media.ui.material3.util.TRACK_SUBTITLE_PLACEHOLDER_HEIGHT
import com.google.android.horologist.media.ui.material3.util.TRACK_SUBTITLE_PLACEHOLDER_WIDTH
import com.google.android.horologist.media.ui.material3.util.TRACK_TITLE_HEIGHT
import com.google.android.horologist.media.ui.material3.util.TRACK_TITLE_PLACEHOLDER_HEIGHT
import com.google.android.horologist.media.ui.material3.util.TRACK_TITLE_PLACEHOLDER_WIDTH

/**
 * A loading state display. This style is matched to the Text of [TextMediaDisplay] as
 * [MarqueeTextMediaDisplay]
 */
@Composable
public fun LoadingMediaDisplay(
    modifier: Modifier = Modifier,
    placeholderState: PlaceholderState = rememberPlaceholderState(true),
    colorScheme: ColorScheme = MaterialTheme.colorScheme,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Box(
            modifier = Modifier.fillMaxWidth().height(TRACK_TITLE_HEIGHT),
            contentAlignment = Alignment.Center,
        ) {
            Box(
                modifier =
                    Modifier.width(TRACK_TITLE_PLACEHOLDER_WIDTH)
                        .height(TRACK_TITLE_PLACEHOLDER_HEIGHT)
                        .placeholderShimmer(
                            placeholderState = placeholderState,
                            color = colorScheme.onBackground,
                        )
                        .placeholder(
                            placeholderState = placeholderState,
                            color = colorScheme.outlineVariant
                        ),
            )
        }
        Box(
            modifier = Modifier.fillMaxWidth().height(TRACK_SUBTITLE_HEIGHT),
            contentAlignment = Alignment.Center,
        ) {
            Box(
                modifier =
                    Modifier.width(TRACK_SUBTITLE_PLACEHOLDER_WIDTH)
                        .height(TRACK_SUBTITLE_PLACEHOLDER_HEIGHT)
                        .placeholderShimmer(
                            placeholderState = placeholderState,
                            color = colorScheme.onBackground,
                        )
                        .placeholder(
                            placeholderState = placeholderState,
                            color = colorScheme.outlineVariant
                        ),
            )
        }
    }
}
