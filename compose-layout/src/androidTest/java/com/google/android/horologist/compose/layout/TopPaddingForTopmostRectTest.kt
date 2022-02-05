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
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.assertTopPositionInRootIsEqualTo
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.Text
import org.junit.Ignore
import org.junit.Rule
import org.junit.Test
import kotlin.math.pow
import kotlin.math.sqrt

class TopPaddingForTopmostRectTest {
    companion object {
        private const val tag = "tag"
        private const val roundTopPaddingPercent = 0.0938f
        private const val rectTopPaddingPercent = 0.052f
    }

    @get:Rule
    val composeTestRule = createComposeRule()

    @Ignore("Cannot achieve the same content width of Text composable with Placeable")
    @Test
    fun fitToTopMarginInRoundDisplay() {
        val roundTopPadding = 0.0938f
        var screenHeight = 0f
        var isRoundDisplay = false

        composeTestRule.setContent {
            isRoundDisplay = LocalConfiguration.current.isScreenRound
            screenHeight = LocalConfiguration.current.screenHeightDp.toFloat()
            SampleText(
                topPaddingPercent = roundTopPadding, paddingStrategy = TopPaddingStrategy.FitToTopPadding
            )
        }
        if (!isRoundDisplay) return

        val layoutInfo = composeTestRule.onRoot().fetchSemanticsNode().layoutInfo
        val contentInfo = composeTestRule.onNodeWithTag(tag).fetchSemanticsNode().layoutInfo

        val radius = screenHeight / 2f
        val contentWidth = contentInfo.coordinates.size.width / layoutInfo.density.density
        val expectedTopMargin = radius - sqrt(radius.pow(2) - (contentWidth / 2).pow(2))

        composeTestRule.onNodeWithTag(tag)
            .assertTopPositionInRootIsEqualTo(Dp(expectedTopMargin))
    }

    @Test
    fun fixedTopMarginInRoundDisplay() {
        var isRoundDisplay = false
        composeTestRule.setContent {
            isRoundDisplay = LocalConfiguration.current.isScreenRound
            SampleText(
                topPaddingPercent = roundTopPaddingPercent,
                paddingStrategy = TopPaddingStrategy.FixedPadding
            )
        }
        if (!isRoundDisplay) return
        val layoutInfo = composeTestRule.onRoot().fetchSemanticsNode().layoutInfo

        composeTestRule.onNodeWithTag(tag)
            .assertTopPositionInRootIsEqualTo(
                (layoutInfo.height * roundTopPaddingPercent / composeTestRule.density.density).dp
            )
    }

    @Test
    fun fixedTopMarginInRectDisplay() {
        var isRoundDisplay = true
        composeTestRule.setContent {
            isRoundDisplay = LocalConfiguration.current.isScreenRound
            SampleText(
                topPaddingPercent = rectTopPaddingPercent,
                paddingStrategy = TopPaddingStrategy.FixedPadding
            )
        }
        if (isRoundDisplay) return
        val layoutInfo = composeTestRule.onRoot().fetchSemanticsNode().layoutInfo

        composeTestRule.onNodeWithTag(tag)
            .assertTopPositionInRootIsEqualTo(
                (layoutInfo.height * rectTopPaddingPercent / composeTestRule.density.density).dp
            )
    }

    @Composable
    private fun SampleText(topPaddingPercent: Float, paddingStrategy: TopPaddingStrategy) {
        Box(modifier = Modifier.fillMaxSize()) {
            Text(
                text = "Hello, world!",
                modifier = Modifier
                    .topPaddingForTopmostRect(topPaddingPercent, paddingStrategy)
                    .testTag(tag)
                    .wrapContentHeight()
            )
        }
    }
}
