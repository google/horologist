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

@file:OptIn(ExperimentalMediaUiApi::class)

package com.google.android.horologist.mediaui.components.controls

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material.icons.filled.ShuffleOn
import androidx.compose.ui.test.junit4.createComposeRule
import com.google.android.horologist.mediaui.ExperimentalMediaUiApi
import org.junit.Rule
import org.junit.Test
import toolbox.hasIconImageVector

class ShuffleButtonTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun givenShuffleIsOn_thenIconIsShuffleOn() {
        // given
        composeTestRule.setContent {
            ShuffleButton(
                onClick = {},
                enabled = true,
                shuffleOn = true,
            )
        }

        // then
        composeTestRule.onNode(hasIconImageVector(Icons.Default.ShuffleOn)).assertExists()
    }

    @Test
    fun givenShuffleIsOff_thenIconIsShuffle() {
        // given
        composeTestRule.setContent {
            ShuffleButton(
                onClick = {},
                enabled = true,
                shuffleOn = false,
            )
        }

        // then
        composeTestRule.onNode(hasIconImageVector(Icons.Default.Shuffle)).assertExists()
    }
}
