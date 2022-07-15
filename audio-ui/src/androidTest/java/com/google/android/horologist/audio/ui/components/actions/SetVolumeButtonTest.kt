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

package com.google.android.horologist.audio.ui.components.actions

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.VolumeDown
import androidx.compose.material.icons.filled.VolumeMute
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.test.filters.MediumTest
import com.google.android.horologist.audio.VolumeState
import com.google.android.horologist.test.toolbox.matchers.hasIconImageVector
import org.junit.Rule
import org.junit.Test

@MediumTest
class SetVolumeButtonTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun givenCurrentVolumeIsNotMaxAndNotMin_thenIconIsVolumeDown() {
        // given
        val currentVolume = 5
        composeTestRule.setContent {
            SetVolumeButton(
                onVolumeClick = {},
                volumeState = VolumeState(current = currentVolume, max = 10)
            )
        }

        // then
        composeTestRule.onNodeWithContentDescription("Set Volume")
            .assert(hasIconImageVector(Icons.Default.VolumeDown))
    }

    @Test
    fun givenCurrentVolumeIsMinimum_thenIconIsVolumeMute() {
        // given
        val currentVolume = 0
        composeTestRule.setContent {
            SetVolumeButton(
                onVolumeClick = {},
                volumeState = VolumeState(current = currentVolume, max = 10)
            )
        }

        // then
        composeTestRule.onNodeWithContentDescription("Set Volume")
            .assert(hasIconImageVector(Icons.Default.VolumeMute))
    }

    @Test
    fun givenCurrentVolumeIsMaximum_thenIconIsVolumeUp() {
        // given
        val currentVolume = 10
        composeTestRule.setContent {
            SetVolumeButton(
                onVolumeClick = {},
                volumeState = VolumeState(current = currentVolume, max = currentVolume)
            )
        }

        // then
        composeTestRule.onNodeWithContentDescription("Set Volume")
            .assert(hasIconImageVector(Icons.Default.VolumeUp))
    }
}
