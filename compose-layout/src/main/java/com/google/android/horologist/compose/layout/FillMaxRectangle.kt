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
import androidx.compose.runtime.Stable
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.layout.layout
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.debugInspectorInfo
import androidx.compose.ui.unit.Constraints
import kotlin.math.min
import kotlin.math.sqrt

/**
 * A [Modifier] for sizing rectangular content within round devices.
 *
 * If the device is round, the content is measured as the largest square whose corners touch the
 * circular boundary of the available layout constraints.
 *
 * This method assumes that the available layout is square and that there are no oval devices.
 */
@Stable
public fun Modifier.fillMaxRectangle(): Modifier = composed(
    inspectorInfo = debugInspectorInfo {
        name = "fillMaxRectangle"
    },
) {
    val isRound = LocalConfiguration.current.isScreenRound
    if (!isRound) {
        return@composed fillMaxSize()
    }

    layout { measurable, constraints ->
        if (!constraints.hasBoundedWidth || !constraints.hasBoundedHeight) {
            val placeable = measurable.measure(constraints)
            return@layout layout(placeable.width, placeable.height) {
                placeable.placeRelative(0, 0)
            }
        }

        val width = constraints.maxWidth
        val height = constraints.maxHeight
        val diameter = min(width, height)
        val maxSquareEdge = (diameter / sqrt(2.0)).toInt()
        val placeable = measurable.measure(Constraints.fixed(maxSquareEdge, maxSquareEdge))

        layout(width, height) {
            placeable.placeRelative(
                x = (width - maxSquareEdge) / 2,
                y = (height - maxSquareEdge) / 2,
            )
        }
    }
}
