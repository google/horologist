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

package com.google.android.horologist.media.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.google.android.horologist.compose.layout.ScalingLazyColumnDefaults
import com.google.android.horologist.compose.layout.ScalingLazyColumnState
import kotlinx.coroutines.launch

/**
 * A preview ScalingLazyColumnState forced into a set position from the top of screen.
 */
@Composable
fun positionedState(
    topIndex: Int = 0,
    topScrollOffset: Int? = null
): ScalingLazyColumnState {
    val configuration = LocalConfiguration.current
    val density = LocalDensity.current
    val initialOffset = with(density) {
        val screenHeight = configuration.screenHeightDp.dp.roundToPx()
        (screenHeight / 2) + (topScrollOffset ?: (-32).dp.roundToPx())
    }

    val coroutineScope = rememberCoroutineScope()

    return ScalingLazyColumnDefaults.belowTimeText().create().apply {
        coroutineScope.launch {
            state.scrollToItem(topIndex, initialOffset)
        }
    }
}
