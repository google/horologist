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

package com.google.android.horologist.compose.pager

import androidx.compose.animation.core.SpringSpec
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.gestures.snapping.SnapFlingBehavior
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerDefaults
import androidx.compose.foundation.pager.PagerSnapDistance
import androidx.compose.foundation.pager.PagerState
import androidx.compose.runtime.Composable

/**
 * Contains the default fling values used by [HorizontalPager].
 *
 * This exists only until available in androidx.wear.compose.foundation.pager.
 */
public object HorizontalPagerDefaults {
    /**
     * Creates [flingParams] that represents fling properties for [HorizontalPager].
     * See [HorizontalPagerSample] for usage.
     *
     * @param pagerState the state to control this pager
     */
    @Composable
    @OptIn(ExperimentalFoundationApi::class)
    public fun flingParams(
        pagerState: PagerState
    ): SnapFlingBehavior {
        return PagerDefaults.flingBehavior(
            state = pagerState,
            pagerSnapDistance = PagerSnapDistance.atMost(0),
            snapAnimationSpec = SpringSpec(dampingRatio = 1f, stiffness = 200f)
        )
    }
}
