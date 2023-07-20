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

package com.google.android.horologist.composables

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.focusTarget
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performRotaryScrollInput
import androidx.wear.compose.material.Picker
import androidx.wear.compose.material.PickerScope
import androidx.wear.compose.material.PickerState
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.rememberPickerState
import com.google.common.truth.Truth.assertThat
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@RunWith(RobolectricTestRunner::class)
@Config(
    sdk = [30],
    qualifiers = "w227dp-h227dp-small-notlong-round-watch-xhdpi-keyshidden-nonav"
)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
class RotaryInteractionTest {
    @get:Rule
    val rule = createComposeRule()

    private val focusRequester = FocusRequester()

    @Test
    fun snapPickerByOneItemForward_whenRotaryRotated() {
        lateinit var pickerState: PickerState
        val selectedOption = 5
        val numberOfOptions = 15

        rule.setContent {
            pickerState = rememberPickerState(
                initialNumberOfOptions = numberOfOptions,
                initiallySelectedOption = selectedOption,
                repeatItems = false
            )
            defaultPickerWithRotaryAccumulator(pickerState)
        }
        rule.runOnIdle { focusRequester.requestFocus() }

        @OptIn(ExperimentalTestApi::class)
        rule.onNodeWithTag(TEST_TAG).performRotaryScrollInput {
            // Scroll by 1 item forward
            rotateToScrollVertically(50.0f)
        }

        rule.runOnIdle {
            assertThat(pickerState.selectedOption).isEqualTo(selectedOption + 1)
        }
    }

    @Test
    fun snapPickerByTwoItemsForward_whenRotaryRotated() {
        lateinit var pickerState: PickerState
        val selectedOption = 5
        val numberOfOptions = 15

        rule.setContent {
            pickerState = rememberPickerState(
                initialNumberOfOptions = numberOfOptions,
                initiallySelectedOption = selectedOption,
                repeatItems = false
            )
            defaultPickerWithRotaryAccumulator(pickerState)
        }
        rule.runOnIdle { focusRequester.requestFocus() }

        @OptIn(ExperimentalTestApi::class)
        rule.onNodeWithTag(TEST_TAG).performRotaryScrollInput {
            // Scroll by 2 items forward
            rotateToScrollVertically(50.0f)
            advanceEventTime(50)
            rotateToScrollVertically(50.0f)
        }

        rule.runOnIdle {
            assertThat(pickerState.selectedOption).isEqualTo(selectedOption + 2)
        }
    }

    @Test
    fun snapPickerByOneItemBackward_whenRotaryRotated() {
        lateinit var pickerState: PickerState
        val selectedOption = 5
        val numberOfOptions = 15

        rule.setContent {
            pickerState = rememberPickerState(
                initialNumberOfOptions = numberOfOptions,
                initiallySelectedOption = selectedOption,
                repeatItems = false
            )
            defaultPickerWithRotaryAccumulator(pickerState)
        }
        rule.runOnIdle { focusRequester.requestFocus() }

        @OptIn(ExperimentalTestApi::class)
        rule.onNodeWithTag(TEST_TAG).performRotaryScrollInput {
            // Scroll by 1 item backward
            rotateToScrollVertically(-50.0f)
        }

        rule.runOnIdle {
            assertThat(pickerState.selectedOption).isEqualTo(selectedOption - 1)
        }
    }

    @Test
    fun snapPickerByTwoItemsBackward_whenRotaryRotated() {
        lateinit var pickerState: PickerState
        val selectedOption = 5
        val numberOfOptions = 15

        rule.setContent {
            pickerState = rememberPickerState(
                initialNumberOfOptions = numberOfOptions,
                initiallySelectedOption = selectedOption,
                repeatItems = false
            )
            defaultPickerWithRotaryAccumulator(pickerState)
        }
        rule.runOnIdle { focusRequester.requestFocus() }

        @OptIn(ExperimentalTestApi::class)
        rule.onNodeWithTag(TEST_TAG).performRotaryScrollInput {
            // Scroll by 2 items backward
            rotateToScrollVertically(-50.0f)
            advanceEventTime(50)
            rotateToScrollVertically(-50.0f)
        }

        rule.runOnIdle {
            assertThat(pickerState.selectedOption).isEqualTo(selectedOption - 2)
        }
    }

    @Composable
    private fun pickerOption():
        (@Composable PickerScope.(optionIndex: Int, pickerSelected: Boolean) -> Unit) =
        { value: Int, pickerSelected: Boolean ->
            Text("$value, $pickerSelected")
        }

    @Composable
    private fun defaultPickerWithRotaryAccumulator(pickerState: PickerState) {
        val pickerGroupItem = pickerGroupItemWithRSB(
            pickerState = pickerState,
            modifier = Modifier,
            contentDescription = null,
            onSelected = {},
            readOnlyLabel = null,
            option = pickerOption()
        )

        Picker(
            state = pickerState,
            onSelected = pickerGroupItem.onSelected,
            contentDescription = pickerGroupItem.contentDescription,
            readOnlyLabel = pickerGroupItem.readOnlyLabel,
            modifier = pickerGroupItem.modifier
                .testTag(TEST_TAG)
                .focusRequester(focusRequester)
                .focusTarget()
        ) {
            pickerGroupItem.option(this, it, true)
        }
    }
}

const val TEST_TAG = "test-tag"
