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

@file:OptIn(ExperimentalHorologistMediaUiApi::class)

package com.google.android.horologist.media.ui.components.controls

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material.icons.filled.Replay10
import androidx.compose.material.icons.filled.Replay30
import androidx.compose.material.icons.filled.Replay5
import androidx.compose.ui.test.assertContentDescriptionEquals
import androidx.compose.ui.test.junit4.createComposeRule
import com.google.android.horologist.media.ui.ExperimentalHorologistMediaUiApi
import com.google.test.toolbox.hasIconImageVector
import org.junit.Rule
import org.junit.Test

class SeekBackButtonTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun givenIncrementIsFive_thenIconAndDescriptionAreFive() {
        // given
        composeTestRule.setContent {
            SeekBackButton(
                onClick = {},
                seekButtonIncrement = SeekButtonIncrement.Five,
            )
        }

        // then
        composeTestRule
            .onNode(hasIconImageVector(Icons.Default.Replay5))
            .assertContentDescriptionEquals("Rewind 5 seconds")
    }

    @Test
    fun givenIncrementIsTen_thenIconAndDescriptionAreTen() {
        // given
        composeTestRule.setContent {
            SeekBackButton(
                onClick = {},
                seekButtonIncrement = SeekButtonIncrement.Ten,
            )
        }

        // then
        composeTestRule
            .onNode(hasIconImageVector(Icons.Default.Replay10))
            .assertContentDescriptionEquals("Rewind 10 seconds")
    }

    @Test
    fun givenIncrementIsThirty_thenIconAndDescriptionAreThirty() {
        // given
        composeTestRule.setContent {
            SeekBackButton(
                onClick = {},
                seekButtonIncrement = SeekButtonIncrement.Thirty,
            )
        }

        // then
        composeTestRule
            .onNode(hasIconImageVector(Icons.Default.Replay30))
            .assertContentDescriptionEquals("Rewind 30 seconds")
    }

    @Test
    fun givenIncrementIsOtherValue_thenIconIsDefaultAndDescriptionIsOtherValue() {
        // given
        composeTestRule.setContent {
            SeekBackButton(
                onClick = {},
                seekButtonIncrement = SeekButtonIncrement.Other(15),
            )
        }

        // then
        composeTestRule
            .onNode(hasIconImageVector(Icons.Default.Replay))
            .assertContentDescriptionEquals("Rewind 15 seconds")
    }

    @Test
    fun givenIncrementIsUnknown_thenIconAndDescriptionAreDefault() {
        // given
        composeTestRule.setContent {
            SeekBackButton(
                onClick = {},
                seekButtonIncrement = SeekButtonIncrement.Unknown,
            )
        }

        // then
        composeTestRule
            .onNode(hasIconImageVector(Icons.Default.Replay))
            .assertContentDescriptionEquals("Rewind")
    }
}
