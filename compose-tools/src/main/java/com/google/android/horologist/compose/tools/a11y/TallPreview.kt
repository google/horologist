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

package com.google.android.horologist.compose.tools.a11y

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.wear.compose.foundation.lazy.ScalingLazyColumnDefaults
import androidx.wear.compose.foundation.lazy.ScalingParams

@Composable
public fun TallPreview(
    width: Int,
    height: Int,
    function: @Composable (ScalingParams) -> Unit
) {
    with(LocalDensity.current) {
        Box(
            modifier = Modifier
                .width(width.toDp())
                .height(height.toDp())
        ) {
            val scalingParams = ScalingLazyColumnDefaults.scalingParams(
                edgeScale = 1f,
                edgeAlpha = 1f
            )
            function(scalingParams)
        }
    }
}
