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

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Stable
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.debugInspectorInfo
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.sqrt

/**
 * A [Modifier] for adding padding for round devices for rectangular content.
 *
 * If the device is round, an equal amount of padding required to inset the content inside the
 * circle.
 *
 * This method assumes that the layout will fill the entire screen, and that there are no oval
 * devices.
 */
@Stable
public fun Modifier.fillMaxRectangle(): Modifier = composed(
    inspectorInfo = debugInspectorInfo {
        name = "fillMaxRectangle"
    },
) {
    val isRound = LocalConfiguration.current.isScreenRound
    var inset: Dp = 0.dp
    if (isRound) {
        val screenHeightDp = LocalConfiguration.current.screenHeightDp
        val screenWidthDp = LocalConfiguration.current.smallestScreenWidthDp
        val maxSquareEdge = (sqrt(((screenHeightDp * screenWidthDp) / 2).toDouble()))
        inset = Dp(((screenHeightDp - maxSquareEdge) / 2).toFloat())
    }
    fillMaxSize().padding(all = inset)
}
