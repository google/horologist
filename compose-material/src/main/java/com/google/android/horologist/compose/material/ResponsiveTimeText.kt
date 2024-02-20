/*
 * Copyright 2024 The Android Open Source Project
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

package com.google.android.horologist.compose.material

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.wear.compose.foundation.CurvedScope
import androidx.wear.compose.foundation.CurvedTextStyle
import androidx.wear.compose.material.TimeSource
import androidx.wear.compose.material.TimeText
import androidx.wear.compose.material.TimeTextDefaults
import androidx.wear.compose.material.TimeTextDefaults.CurvedTextSeparator
import androidx.wear.compose.material.TimeTextDefaults.TextSeparator
import androidx.wear.compose.material.TimeTextDefaults.timeFormat

/**
 * Provides a [TimeText] component with a responsive padding of 2.1%.
 */
@Composable
public fun ResponsiveTimeText(
    modifier: Modifier = Modifier,
    timeSource: TimeSource = TimeTextDefaults.timeSource(timeFormat()),
    timeTextStyle: TextStyle = TimeTextDefaults.timeTextStyle(),
    contentPadding: PaddingValues = responsivePaddingDefaults(),
    startLinearContent: (@Composable () -> Unit)? = null,
    startCurvedContent: (CurvedScope.() -> Unit)? = null,
    endLinearContent: (@Composable () -> Unit)? = null,
    endCurvedContent: (CurvedScope.() -> Unit)? = null,
    textLinearSeparator: @Composable () -> Unit = { TextSeparator(textStyle = timeTextStyle) },
    textCurvedSeparator: CurvedScope.() -> Unit = {
        CurvedTextSeparator(curvedTextStyle = CurvedTextStyle(timeTextStyle))
    },
): Unit = TimeText(
    modifier = modifier,
    timeSource = timeSource,
    timeTextStyle = timeTextStyle,
    contentPadding = contentPadding,
    startLinearContent = startLinearContent,
    startCurvedContent = startCurvedContent,
    endLinearContent = endLinearContent,
    endCurvedContent = endCurvedContent,
    textLinearSeparator = textLinearSeparator,
    textCurvedSeparator = textCurvedSeparator,
)

@Composable
public fun responsivePaddingDefaults(): PaddingValues {
    val height = LocalConfiguration.current.screenHeightDp
    val padding = height * 0.021
    return PaddingValues(padding.dp)
}
