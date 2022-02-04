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
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.assertTopPositionInRootIsEqualTo
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.Text
import org.junit.Rule
import org.junit.Test

class TopPaddingForTopmostRectTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun topPaddingForTopmostRect_fitToTopMarginInCircle() {
        // TODO ForceMode
        val roundTopPadding = 0.0938f
        composeTestRule.setContent {
            SampleText(
                topPadding = roundTopPadding, paddingStrategy = TopPaddingStrategy.FitToTopPadding
            )
        }
        val layoutInfo = composeTestRule.onRoot().fetchSemanticsNode().layoutInfo
        val topMargin = (layoutInfo.height * roundTopPadding / layoutInfo.density.density + 0.5f).dp

        composeTestRule.onNodeWithTag("text")
            .assertTopPositionInRootIsEqualTo(topMargin)
    }

    @Composable
    private fun SampleText(topPadding: Float, paddingStrategy: TopPaddingStrategy) {
        Box(modifier = Modifier.fillMaxSize()) {
            Text(
                text = "Test Text",
                modifier = Modifier
                    .topPaddingForTopmostRect(topPadding, paddingStrategy)
                    .height(48.dp)
                    .wrapContentHeight()
                    .testTag("text")
            )
        }
    }
}