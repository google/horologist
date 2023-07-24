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

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.ExperimentalWearMaterialApi
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.placeholder
import androidx.wear.compose.material.placeholderShimmer
import androidx.wear.compose.material.rememberPlaceholderState
import com.google.android.horologist.annotations.ExperimentalHorologistApi
import com.google.android.horologist.media.ui.components.animated.MarqueeTextMediaDisplay

/**
 * A loading state display. This style is matched to the Text of [TextMediaDisplay] as
 * [MarqueeTextMediaDisplay]
 */
@OptIn(ExperimentalWearMaterialApi::class)
@ExperimentalHorologistApi
@Composable
public fun LoadingMediaDisplay(
    modifier: Modifier = Modifier
) {
    // Always shimmer on the placeholder pills.
    val placeholderState = rememberPlaceholderState { /* isContentReady = */ false }

    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Spacer(modifier = Modifier.height(1.dp))
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(6.dp))
                .background(MaterialTheme.colors.surface)
                .placeholderShimmer(placeholderState)
                .placeholder(placeholderState)
                .width(120.dp)
                .height(12.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(6.dp))
                .background(MaterialTheme.colors.surface)
                .placeholderShimmer(placeholderState)
                .placeholder(placeholderState)
                .width(80.dp)
                .height(12.dp)
        )
    }

    if (!placeholderState.isShowContent) {
        LaunchedEffect(placeholderState) { placeholderState.startPlaceholderAnimation() }
    }
}
