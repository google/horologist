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

import androidx.annotation.DimenRes
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.layout.layout
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import kotlin.math.max
import kotlin.math.roundToInt

// TODO add comment
/**
 */
enum class TopPaddingStrategy {
    /**  */
    FitToTopPadding,
    /**  */
    FixedPadding,
}

/**
 * Apply a space along top edge of the rectangular content which is placed on the topmost of
 * a screen. When the part of the content is cropped by the display, add extra padding.
 * @param defaultTopMarginPercentId percentage of top padding defined in dimens.xml.
 * @param paddingStrategy
 */
fun Modifier.topPaddingForTopmostRect(
    @DimenRes defaultTopMarginPercentId: Int,
    paddingStrategy: TopPaddingStrategy = TopPaddingStrategy.FixedPadding,
): Modifier =
    composed {
        val context = LocalContext.current
        val defaultTopMarginPercent = remember {
            context.floatDimensionResource(defaultTopMarginPercentId)
        }
        topPaddingForTopmostRect(defaultTopMarginPercent, paddingStrategy)
    }

/**
 * Apply a space along top edge of the rectangular content which is placed on the topmost of
 * a screen. When the part of the content is cropped by the display, add extra padding.
 * @param defaultTopMarginPercent
 * @param paddingStrategy
 */
fun Modifier.topPaddingForTopmostRect(
    defaultTopMarginPercent: Float,
    paddingStrategy: TopPaddingStrategy = TopPaddingStrategy.FixedPadding,
): Modifier =
    composed {
        require(defaultTopMarginPercent in 0f..1f)
        val context = LocalContext.current
        val configuration = LocalConfiguration.current
        val displayMetrics = remember { context.resources.displayMetrics }
        val defaultTopMargin = defaultTopMarginPercent * displayMetrics.heightPixels

        layout { measurable, constraints ->
            val placeable = measurable.measure(constraints)
            val topMargin =
                if (configuration.isScreenRound) {
                    val fitToTopMargin = calculateVerticalOffsetForRect(
                        displayMetrics.widthPixels, placeable.width
                    )
                    when (paddingStrategy) {
                        TopPaddingStrategy.FixedPadding -> max(defaultTopMargin, fitToTopMargin)
                        TopPaddingStrategy.FitToTopPadding -> fitToTopMargin
                    }
                } else {
                    defaultTopMargin
                }
            layout(constraints.maxWidth, topMargin.roundToInt() + placeable.height) {
                placeable.placeRelative(
                    (constraints.maxWidth - placeable.width) / 2,
                    topMargin.roundToInt()
                )
            }
        }
    }
