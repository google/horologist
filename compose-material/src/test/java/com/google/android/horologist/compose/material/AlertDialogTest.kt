/*
 * Copyright 2026 The Android Open Source Project
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

@file:OptIn(ExperimentalHorologistApi::class)

package com.google.android.horologist.compose.material

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasScrollToNodeAction
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performScrollToNode
import androidx.test.filters.MediumTest
import androidx.wear.compose.material.Text
import com.google.android.horologist.annotations.ExperimentalHorologistApi
import com.google.android.horologist.compose.layout.ScalingLazyColumnState
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@MediumTest
@RunWith(RobolectricTestRunner::class)
class AlertDialogTest {

  @get:Rule val composeTestRule = createComposeRule()

  private var showDialog by mutableStateOf(true)

  @Test
  fun defaultState_whenShownAgain_resetsScrollPosition() {
    composeTestRule.setContent {
      AlertDialog(showDialog = showDialog, onDismiss = {}, title = title) {
        repeat(itemCount) { index -> item { Text("Item $index") } }
      }
    }

    composeTestRule.onNodeWithText(title).assertIsDisplayed()
    composeTestRule.onNode(hasScrollToNodeAction()).performScrollToNode(hasText(lastItem))
    composeTestRule.onNodeWithText(lastItem).assertIsDisplayed()

    composeTestRule.runOnIdle { showDialog = false }
    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithText(lastItem).assertDoesNotExist()

    composeTestRule.runOnIdle { showDialog = true }
    composeTestRule.waitForIdle()

    composeTestRule.onNodeWithText(title).assertIsDisplayed()
  }

  @Test
  fun suppliedState_whenShownAgain_preservesScrollPosition() {
    val state =
      ScalingLazyColumnState(
        initialScrollPosition = ScalingLazyColumnState.ScrollPosition(index = 3, offsetPx = 0)
      )
    composeTestRule.setContent {
      AlertDialog(showDialog = showDialog, onCancel = {}, onOk = {}, title = title, state = state) {
        repeat(itemCount) { index -> item { Text("Item $index") } }
      }
    }

    composeTestRule.onNode(hasScrollToNodeAction()).performScrollToNode(hasText(lastItem))
    composeTestRule.onNodeWithText(lastItem).assertIsDisplayed()
    val scrolledItemIndex = composeTestRule.runOnIdle { state.state.centerItemIndex }
    assertNotEquals(state.initialScrollPosition.index, scrolledItemIndex)

    composeTestRule.runOnIdle { showDialog = false }
    composeTestRule.waitForIdle()
    composeTestRule.runOnIdle { showDialog = true }
    composeTestRule.waitForIdle()

    assertEquals(scrolledItemIndex, composeTestRule.runOnIdle { state.state.centerItemIndex })
    composeTestRule.onNodeWithText(lastItem).assertIsDisplayed()
  }

  private companion object {
    const val itemCount = 25
    const val title = "Title"
    const val lastItem = "Item ${itemCount - 1}"
  }
}
