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

package com.google.android.horologist.compose.layout

import android.content.Context
import android.content.res.Configuration
import androidx.annotation.DimenRes
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp

@ReadOnlyComposable
@Composable
public fun paddingValues(
    @DimenRes startPercentId: Int = 0,
    @DimenRes topPercentId: Int = 0,
    @DimenRes endPercentId: Int = 0,
    @DimenRes bottomPercentId: Int = 0,
): PaddingValues =
    paddingValues(
        LocalContext.current, LocalConfiguration.current,
        startPercentId = startPercentId,
        topPercentId = topPercentId,
        endPercentId = endPercentId,
        bottomPercentId = bottomPercentId
    )

public fun paddingValues(
    context: Context,
    configuration: Configuration,
    @DimenRes startPercentId: Int = 0,
    @DimenRes topPercentId: Int = 0,
    @DimenRes endPercentId: Int = 0,
    @DimenRes bottomPercentId: Int = 0,
): PaddingValues {
    val startPercent =
        if (startPercentId == 0) 0f else context.floatDimensionResource(startPercentId)
    val topPercent =
        if (topPercentId == 0) 0f else context.floatDimensionResource(topPercentId)
    val endPercent =
        if (endPercentId == 0) 0f else context.floatDimensionResource(endPercentId)
    val bottomPercent =
        if (bottomPercentId == 0) 0f else context.floatDimensionResource(bottomPercentId)
    return PaddingValues(
        start = Dp(configuration.screenWidthDp * startPercent),
        top = Dp(configuration.screenHeightDp * topPercent),
        end = Dp(configuration.screenWidthDp * endPercent),
        bottom = Dp(configuration.screenHeightDp * bottomPercent),
    )
}

public fun paddingValues(
    context: Context,
    configuration: Configuration,
    @DimenRes horizontalPercentId: Int = 0,
    @DimenRes verticalPercentId: Int = 0
): PaddingValues {
    val horizontalPercent =
        if (horizontalPercentId == 0) 0f else context.floatDimensionResource(horizontalPercentId)
    val verticalPercent =
        if (verticalPercentId == 0) 0f else context.floatDimensionResource(verticalPercentId)
    return PaddingValues(
        horizontal = Dp(configuration.screenWidthDp * horizontalPercent),
        vertical = Dp(configuration.screenHeightDp * verticalPercent)
    )
}
