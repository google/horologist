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

package com.google.android.horologist.compose.layout

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onRoot
import androidx.test.filters.MediumTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import kotlin.math.pow
import kotlin.math.sqrt

@MediumTest
@RunWith(RobolectricTestRunner::class)
class FillMaxRectangleTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    @Config(
        sdk = [30],
        qualifiers = "w227dp-h227dp-small-notlong-notround-watch-xhdpi-keyshidden-nonav",
    )
    fun testSquare() {
        composeTestRule.setContent {
            Boxes()
        }

        val width = composeTestRule.onRoot().fetchSemanticsNode().size.width
        val safeWidth = composeTestRule.onNodeWithTag("boxInset").fetchSemanticsNode().size.width

        assertEquals(width, safeWidth)
    }

    @Test
    @Config(
        sdk = [30],
        qualifiers = "w227dp-h227dp-small-notlong-round-watch-xhdpi-keyshidden-nonav",
    )
    fun testCircle() {
        composeTestRule.setContent {
            Boxes()
        }

        val width = composeTestRule.onRoot().fetchSemanticsNode().size.width
        val safeWidth = composeTestRule.onNodeWithTag("boxInset").fetchSemanticsNode().size.width

        val expectedWidth = sqrt(2.0 * (width.toDouble() / 2).pow(2.0))
        assertEquals(expectedWidth, safeWidth.toDouble(), 1.0)
    }

    @Composable
    private fun Boxes() {
        Box(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .fillMaxRectangle()
                    .testTag("boxInset"),
            ) {
            }
        }
    }
}
