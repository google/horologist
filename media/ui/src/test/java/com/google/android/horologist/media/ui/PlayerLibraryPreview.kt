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

@file:OptIn(ExperimentalFoundationApi::class)

package com.google.android.horologist.media.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.wear.compose.material.PositionIndicator
import androidx.wear.compose.material.Scaffold
import androidx.wear.compose.material.TimeText
import com.google.android.horologist.compose.layout.ScalingLazyColumnState
import com.google.android.horologist.compose.layout.scrollAway
import com.google.android.horologist.compose.pager.PagerScreen
import com.google.android.horologist.compose.tools.RoundPreview
import com.google.android.horologist.screenshots.FixedTimeSource

@Composable
fun PlayerLibraryPreview(
    columnState: ScalingLazyColumnState,
    round: Boolean = true,
    function: @Composable () -> Unit
) {
    RoundPreview(round = round) {
        PagerScreen(
            state = rememberPagerState {
                2
            }
        ) {
            if (it == 0) {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    timeText = {
                        TimeText(
                            modifier = Modifier.scrollAway(columnState),
                            timeSource = FixedTimeSource
                        )
                    },
                    positionIndicator = {
                        PositionIndicator(columnState.state)
                    }
                ) {
                    Box(modifier = Modifier.background(Color.Black)) {
                        function()
                    }
                }
            }
        }
    }
}
