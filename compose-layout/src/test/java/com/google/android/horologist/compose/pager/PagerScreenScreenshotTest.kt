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

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.wear.compose.foundation.pager.rememberPagerState
import androidx.wear.compose.material.Text
import com.google.android.horologist.screenshots.rng.WearLegacyScreenTest
import org.junit.Test

class PagerScreenScreenshotTest : WearLegacyScreenTest() {

    @Test
    fun screens() {
        runTest {
            PagerScreen(
                state = rememberPagerState {
                    10
                },
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize(),
                ) {
                    Text(text = "Item $it", modifier = Modifier.align(Alignment.Center))
                }
            }
        }
    }
}
