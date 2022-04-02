/*
 * Copyright 2021 The Android Open Source Project
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

package com.google.android.horologist.compose.pager

import androidx.compose.foundation.MutatePriority
import androidx.compose.foundation.gestures.ScrollScope
import androidx.compose.foundation.gestures.ScrollableState
import com.google.accompanist.pager.PagerState
import com.google.android.horologist.compose.devices.Haptics
import com.google.android.horologist.compose.devices.RotaryInput
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.time.ExperimentalTime

/**
 * ScrollableState integration for Horizontal Pager.
 */
public class PagerScrollHandler(
    private val pagerState: PagerState,
    private val coroutineScope: CoroutineScope,
    private val rotaryInput: RotaryInput,
    private val haptics: Haptics,
) : ScrollableState {
    val ordered = Dispatchers.Main.limitedParallelism(1)

    override val isScrollInProgress: Boolean
        get() = totalDelta != 0f

    override fun dispatchRawDelta(delta: Float): Float = scrollableState.dispatchRawDelta(delta)

    private var totalDelta = 0f

    @OptIn(ExperimentalTime::class)
    private val scrollableState = ScrollableState { delta ->
        totalDelta += delta

        coroutineScope.launch(ordered) {
            val offset = totalDelta.toInt() / rotaryInput.rotaryPixelsForPageChange

            if (offset != 0) {
                val consumed = offset * rotaryInput.rotaryPixelsForPageChange
                totalDelta -= consumed
                val newTargetPage =
                    (pagerState.targetPage + offset).coerceIn(0, pagerState.pageCount - 1)
                if (newTargetPage != pagerState.targetPage) {
                    if (rotaryInput.needsHaptic) {
                        haptics.performScrollTick()
                    }
                    pagerState.scrollToPage(newTargetPage)
                }
            }
        }

        delta
    }

    override suspend fun scroll(
        scrollPriority: MutatePriority, block: suspend ScrollScope.() -> Unit
    ) {
        scrollableState.scroll(block = block)
    }
}
