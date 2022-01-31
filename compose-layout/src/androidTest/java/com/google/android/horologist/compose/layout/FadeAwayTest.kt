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

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performScrollToIndex
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.PositionIndicator
import androidx.wear.compose.material.Scaffold
import androidx.wear.compose.material.TimeText
import org.junit.Assert.assertEquals
import org.junit.Ignore
import org.junit.Rule
import org.junit.Test
import kotlin.math.roundToInt

class FadeAwayTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    @Ignore
    fun testScroll() {
        val scrollState = ScrollState(initial = 0)

        composeTestRule.setContent {
            Scaffold(
                timeText = {
                    TimeText(modifier = Modifier.fadeAway(scrollState))
                },
                positionIndicator = {
                    PositionIndicator(scrollState = scrollState)
                }
            ) {
                Box(Modifier.requiredSize(100.dp)) {
                    LazyColumn(
                        modifier = Modifier
                            .testTag("column")
                            .verticalScroll(scrollState)
                    ) {
                        repeat(3) { i ->
                            item {
                                ExampleCard(Modifier.requiredSize(50.dp), i)
                            }
                        }
                    }
                }
            }
        }

        val column = composeTestRule.onNodeWithTag("column")
        val card = composeTestRule.onNodeWithTag("card.0")
        val cardSemanticsNode = card.fetchSemanticsNode()

        assertEquals(0.0f, cardSemanticsNode.positionInRoot.y)

        column.performScrollToIndex(1)

        assertEquals(0.0f, cardSemanticsNode.positionInRoot.y)
    }

    @Test
    fun testColumn() {
        val scrollState = ScrollState(initial = 0)

        val modifier = Modifier.fadeAway(scrollState)

        var positionY = 0

        composeTestRule.setContent {
            Box(modifier = modifier
                .onGloballyPositioned { coordinates: LayoutCoordinates ->
                    positionY = coordinates.positionInRoot().y.roundToInt()
                }
            )
        }

        composeTestRule.runOnIdle {
            assertEquals(0, positionY)
        }

        scrollState.dispatchRawDelta(10f)

        composeTestRule.runOnIdle {
            assertEquals(-10, positionY)
        }

        scrollState.dispatchRawDelta(25f)

        composeTestRule.runOnIdle {
            assertEquals(-35, positionY)
        }
    }
}