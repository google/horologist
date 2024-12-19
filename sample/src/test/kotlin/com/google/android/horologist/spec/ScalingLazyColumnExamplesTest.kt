/*
 * Copyright 2024 The Android Open Source Project
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

package com.google.android.horologist.spec

import androidx.compose.runtime.Composable
import androidx.compose.ui.test.hasScrollToNodeAction
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.swipeUp
import com.google.android.horologist.compose.tools.Device
import com.google.android.horologist.screensizes.WearLegacyScreenSizeTest
import org.junit.Assume.assumeFalse
import org.junit.Test

class ScalingLazyColumnExamplesTest(device: Device) : WearLegacyScreenSizeTest(
    device = device,
    showTimeText = false,
) {

    @Composable
    override fun Content() {
        assumeFalse(true)
    }

    @Test
    fun title1Line() {
        runTest {
            Sample1Line()
        }
    }

    @Test
    fun title2Lines() {
        runTest {
            Sample2Lines()
        }
    }

    @Test
    fun title3Lines() {
        runTest {
            Sample3Lines()
        }
    }

    @Test
    fun title1Button() {
        runTest {
            Sample1Button()
        }
    }

    @Test
    fun title2Button() {
        runTest {
            Sample2Buttons()
        }
    }

    @Test
    fun title1CompactChip() {
        runTest {
            SampleCompactChip()
        }
    }

    @Test
    fun titleOtherChips() {
        runTest {
            SampleOtherChips()
        }
    }

    @Test
    fun titleUnspecified() {
        runTest {
            SampleUnspecified()
        }
    }

    @Test
    fun titleOtherCards() {
        runTest {
            SampleOtherCards()
        }
    }

    @Test
    fun titleOtherText() {
        runTest {
            SampleOtherText()
        }
    }

    @Test
    fun bottom1Button() {
        runTest(capture = false) {
            Bottom1Button()
        }
        scrollToBottom()
        captureScreenshot()
    }

    @Test
    fun bottom2Buttons() {
        runTest(capture = false) {
            Bottom2Buttons()
        }
        scrollToBottom()
        captureScreenshot()
    }

    @Test
    fun bottom3Buttons() {
        runTest(capture = false) {
            Bottom3Buttons()
        }
        scrollToBottom()
        captureScreenshot()
    }

    @Test
    fun bottomOtherChips() {
        runTest(capture = false) {
            BottomOtherChips()
        }
        scrollToBottom()
        captureScreenshot()
    }

    @Test
    fun bottomOtherCards() {
        runTest(capture = false) {
            BottomOtherCards()
        }
        scrollToBottom()
        captureScreenshot()
    }

    @Test
    fun bottomUnspecified() {
        runTest(capture = false) {
            BottomUnspecified()
        }
        scrollToBottom()
        captureScreenshot()
    }

    @Test
    fun bottomOtherText() {
        runTest(capture = false) {
            BottomOtherText()
        }
        scrollToBottom()
        captureScreenshot()
    }

    private fun scrollToBottom() {
        composeRule.onNode(hasScrollToNodeAction())
            .performTouchInput { repeat(10) { swipeUp() } }
    }

    @Test
    fun sideMixed() {
        runTest {
            SideMixed()
        }
    }

    @Test
    fun sideChips() {
        runTest {
            SideChips()
        }
    }

    @Test
    fun sideCards() {
        runTest {
            SideCards()
        }
    }

    @Test
    fun sideText() {
        runTest {
            SideText()
        }
    }
}
