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

package com.google.android.horologist.audio.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.getBoundsInRoot
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.test.filters.MediumTest
import com.google.android.horologist.audio.AudioOutput
import com.google.android.horologist.audio.VolumeState
import com.google.android.horologist.test.toolbox.matchers.assertHasClickLabel
import com.google.android.horologist.test.toolbox.matchers.assertHasStateDescription
import org.junit.Rule
import org.junit.Test

@MediumTest
class VolumeScreenA11yTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun testLabelOrdering() {
        val volumeState by mutableStateOf(
            VolumeState(
                current = 5,
                max = 10
            )
        )
        val audioOutput = AudioOutput.BluetoothHeadset("id", "Pixelbuds")

        composeTestRule.setContent {
            VolumeScreenTestCase(
                volumeState = volumeState,
                audioOutput = audioOutput
            )
        }

        composeTestRule.onNodeWithContentDescription("Increase Volume")
            .assertIsDisplayed()
            .assertHasClickAction()

        val onNodeWithContentDescription =
            composeTestRule.onNodeWithContentDescription("Increase Volume")
        val message = onNodeWithContentDescription.getBoundsInRoot()
        println(message)
        println(composeTestRule.onNodeWithContentDescription("Pixelbuds").getBoundsInRoot())
        println(composeTestRule.onNodeWithContentDescription("Decrease Volume").getBoundsInRoot())

        composeTestRule.onNodeWithContentDescription("Pixelbuds")
            .assertIsDisplayed()
            .assertHasClickAction()
            .assertHasStateDescription("Connected, Volume 5")
            .assertHasClickLabel("Change Audio Output")

        composeTestRule.onNodeWithContentDescription("Decrease Volume")
            .assertIsDisplayed()
            .assertHasClickAction()
    }
}
