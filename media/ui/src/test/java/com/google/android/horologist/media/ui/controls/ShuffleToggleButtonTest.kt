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

package com.google.android.horologist.media.ui.controls

import com.google.android.horologist.media.ui.components.controls.ShuffleToggleButton
import com.google.android.horologist.screenshots.rng.WearLegacyComponentTest
import org.junit.Test

class ShuffleToggleButtonTest : WearLegacyComponentTest() {

    @Test
    fun givenShuffleIsOn_thenIconIsShuffleOn() {
        runComponentTest {
            ShuffleToggleButton(
                onToggle = {},
                shuffleOn = true,
            )
        }
    }

    @Test
    fun givenShuffleIsOff_thenIconIsShuffle() {
        runComponentTest {
            ShuffleToggleButton(
                onToggle = {},
                shuffleOn = false,
            )
        }
    }
}
