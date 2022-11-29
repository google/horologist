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

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.ScalingLazyListState
import androidx.wear.compose.material.scrollAway as scrollAwayCompose

/**
 * Scroll an item vertically in/out of view based on a [ScrollState].
 * Typically used to scroll a [TimeText] item out of view as the user starts to scroll a
 * vertically scrollable [Column] of items upwards and bring additional items into view.
 *
 * @param scrollState The [ScrollState] to used as the basis for the scroll-away.
 * @param offset Adjustment to the starting point for scrolling away. Positive values result in
 * the scroll away starting later.
 */
@Deprecated(
    "Replaced by Wear Compose scrollAway",
    replaceWith = ReplaceWith(
        "this.scrollAway(scrollState, offset)",
        "androidx.wear.compose.material.scrollAway"
    )
)
public fun Modifier.scrollAway(
    scrollState: ScrollState,
    offset: Dp = 0.dp
): Modifier = scrollAwayCompose(scrollState, offset)

/**
 * Scroll an item vertically in/out of view based on a [LazyListState].
 * Typically used to scroll a [TimeText] item out of view as the user starts to scroll
 * a [LazyColumn] of items upwards and bring additional items into view.
 *
 * @param scrollState The [LazyListState] to used as the basis for the scroll-away.
 * @param itemIndex The item for which the scroll offset will trigger scrolling away.
 * @param offset Adjustment to the starting point for scrolling away. Positive values result in
 * the scroll away starting later.
 */
@Deprecated(
    "Replaced by Wear Compose scrollAway",
    replaceWith = ReplaceWith(
        "this.scrollAway(scrollState, itemIndex, offset)",
        "androidx.wear.compose.material.scrollAway"
    )
)
public fun Modifier.scrollAway(
    scrollState: LazyListState,
    itemIndex: Int = 0,
    offset: Dp = 0.dp
): Modifier = scrollAwayCompose(scrollState, itemIndex, offset)

/**
 * Scroll an item vertically in/out of view based on a [ScalingLazyListState].
 * Typically used to scroll a [TimeText] item out of view as the user starts to scroll
 * a [ScalingLazyColumn] of items upwards and bring additional items into view.
 *
 * @param scrollState The [ScalingLazyListState] to used as the basis for the scroll-away.
 * @param itemIndex The item for which the scroll offset will trigger scrolling away.
 * @param offset Adjustment to the starting point for scrolling away. Positive values result in
 * the scroll away starting later, negative values start scrolling away earlier.
 */
@Deprecated(
    "Replaced by Wear Compose scrollAway",
    replaceWith = ReplaceWith(
        "this.scrollAway(scrollState, itemIndex, offset)",
        "androidx.wear.compose.material.scrollAway"
    )
)
public fun Modifier.scrollAway(
    scrollState: ScalingLazyListState,
    itemIndex: Int = 1,
    offset: Dp = 0.dp
): Modifier = scrollAwayCompose(scrollState, itemIndex, offset)
