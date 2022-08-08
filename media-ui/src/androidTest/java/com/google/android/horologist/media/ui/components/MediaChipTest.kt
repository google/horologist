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

package com.google.android.horologist.media.ui.components

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import com.google.android.horologist.media.ui.ExperimentalHorologistMediaUiApi
import com.google.android.horologist.media.ui.state.model.MediaUiModel
import org.junit.Rule
import org.junit.Test

class MediaChipTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun givenTitle_thenDisplaysTitle() {
        // given
        val title = "title"

        composeTestRule.setContent {
            MediaChip(
                media = MediaUiModel(id = "id", title = title),
                onClick = { }
            )
        }

        // then
        composeTestRule.onNodeWithText(title).assertExists()
    }

    @Test
    fun givenNoTitle_thenDisplaysTitle() {
        // given
        val defaultTitle = "defaultTitle"

        composeTestRule.setContent {
            MediaChip(
                media = MediaUiModel(id = "id"),
                onClick = { },
                defaultTitle = defaultTitle
            )
        }

        // then
        composeTestRule.onNodeWithText(defaultTitle).assertExists()
    }
}
