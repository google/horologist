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
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class RepeatableClickableButtonTest {
    @get:Rule
    public val rule = createComposeRule()
    private val text = "myRepeatableClickableButton"
    private val onClick: () -> Unit = { ++clickCounter }
    private val onLongRepeatableClick: () -> Unit = { ++longRepeatableClickCounter }
    private val onLongRepeatableClickEnd: () -> Unit = { longRepeatedClickEnd = true }

    private var clickCounter = 0
    private var longRepeatableClickCounter = 0
    private var longRepeatedClickEnd = false

    @Before
    fun setup() {
        rule.setContent {
            Box {
                RepeatableClickableButton(
                    onClick = onClick,
                    onLongRepeatableClick = onLongRepeatableClick,
                    onLongRepeatableClickEnd = onLongRepeatableClickEnd,
                ) {
                    Text(text)
                }
            }
        }

        // Reset counters and state
        clickCounter = 0
        longRepeatableClickCounter = 0
        longRepeatedClickEnd = false
    }

    @Test
    fun findByTextAndClick() {
        rule.onNodeWithText(text).performClick()

        rule.runOnIdle {
            assertThat(clickCounter).isEqualTo(1)
            assertThat(longRepeatableClickCounter).isEqualTo(0)
            assertThat(longRepeatedClickEnd).isFalse()
        }
    }

    @Test
    fun findByTextAndHoldClick2Seconds() {
        rule.onNodeWithText(text).performTouchInput {
            down(center)
            advanceEventTime(2000L)
            up()
        }

        rule.runOnIdle {
            assertThat(clickCounter).isEqualTo(0)
            assertThat(longRepeatableClickCounter).isGreaterThan(0)
            assertThat(longRepeatedClickEnd).isTrue()
        }
    }
}
