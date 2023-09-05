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

import androidx.compose.foundation.layout.Box
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTouchInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.wear.compose.material.Text
import com.google.common.truth.Truth.assertThat
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class RepeatableClickableButtonTest {
    @get:Rule
    val rule = createComposeRule()

    @Test
    fun findByTextAndClick() {
        var clickCounter = 0
        var repeatedClickCounter = 0
        val onClick: () -> Unit = { ++clickCounter }
        val onRepeatedClick: () -> Unit = { ++repeatedClickCounter }
        val text = "myButton"

        rule.setContent {
            Box {
                RepeatableClickableButton(onClick = onClick, onRepeatedClick = onRepeatedClick) {
                    Text(text)
                }
            }
        }

        rule.onNodeWithText(text).performClick()

        rule.runOnIdle {
            assertThat(clickCounter).isEqualTo(1)
            assertThat(repeatedClickCounter).isEqualTo(0)
        }
    }

    @Test
    fun findByTextAndHoldClick2Seconds() {
        var clickCounter = 0
        var repeatedClickCounter = 0
        val onClick: () -> Unit = { ++clickCounter }
        val onRepeatedClick: () -> Unit = { ++repeatedClickCounter }
        val text = "myButton"

        rule.setContent {
            Box {
                RepeatableClickableButton(onClick = onClick, onRepeatedClick = onRepeatedClick) {
                    Text(text)
                }
            }
        }

        rule.onNodeWithText(text).performTouchInput {
            down(center)
            advanceEventTime(2000L)
            up()
        }

        rule.runOnIdle {
            assertThat(clickCounter).isEqualTo(0)
            assertThat(repeatedClickCounter).isGreaterThan(0)
        }
    }
}
