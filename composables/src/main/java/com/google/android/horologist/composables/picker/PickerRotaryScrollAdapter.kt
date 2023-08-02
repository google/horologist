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

package com.google.android.horologist.composables.picker

import com.google.android.horologist.compose.rotaryinput.RotaryScrollAdapter

/**
 * An extension function for creating [RotaryScrollAdapter] from [Picker]
 */
internal fun PickerState.toRotaryScrollAdapter(): PickerRotaryScrollAdapter =
    PickerRotaryScrollAdapter(this)

/**
 * An implementation of rotary scroll adapter for [Picker]
 */
internal class PickerRotaryScrollAdapter(
    override val scrollableState: PickerState
) : RotaryScrollAdapter {

    /**
     * Returns a height of a centered item, as all items in picker have the same height.
     */
    override fun averageItemSize(): Float =
        scrollableState.scalingLazyListState
            .layoutInfo.visibleItemsInfo[currentItemIndex()].size.toFloat()

    /**
     * Current (centred) item index
     */
    override fun currentItemIndex(): Int =
        scrollableState.scalingLazyListState.centerItemIndex

    /**
     * An offset from the item centre
     */
    override fun currentItemOffset(): Float =
        scrollableState.scalingLazyListState.centerItemScrollOffset.toFloat()
}
