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

package com.google.android.horologist.compose.tools

import androidx.compose.runtime.Composable
import androidx.compose.ui.text.PlatformTextStyle
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Typography

@Suppress("DEPRECATION")
@Composable
public fun newTypography(): Typography {
    val defaultTypography = MaterialTheme.typography
    return defaultTypography.copy(
        display1 = defaultTypography.display1.copy(platformStyle = PlatformTextStyle(false)),
        display2 = defaultTypography.display2.copy(platformStyle = PlatformTextStyle(false)),
        display3 = defaultTypography.display3.copy(platformStyle = PlatformTextStyle(false)),
        title1 = defaultTypography.title1.copy(platformStyle = PlatformTextStyle(false)),
        title2 = defaultTypography.title2.copy(platformStyle = PlatformTextStyle(false)),
        title3 = defaultTypography.title3.copy(platformStyle = PlatformTextStyle(false)),
        body1 = defaultTypography.body1.copy(platformStyle = PlatformTextStyle(false)),
        body2 = defaultTypography.body2.copy(platformStyle = PlatformTextStyle(false)),
        button = defaultTypography.button.copy(platformStyle = PlatformTextStyle(false)),
        caption1 = defaultTypography.caption1.copy(platformStyle = PlatformTextStyle(false)),
        caption2 = defaultTypography.caption2.copy(platformStyle = PlatformTextStyle(false)),
        caption3 = defaultTypography.caption3.copy(platformStyle = PlatformTextStyle(false)),
    )
}