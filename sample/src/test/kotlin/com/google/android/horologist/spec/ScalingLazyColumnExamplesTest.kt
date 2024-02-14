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
import com.google.android.horologist.compose.tools.Device
import com.google.android.horologist.screenshots.ScreenshotTestRule
import com.google.android.horologist.screensizes.ScreenSizeTest
import org.junit.Assume
import org.junit.Test

class ScalingLazyColumnExamplesTest(device: Device) : ScreenSizeTest(
    device = device, showTimeText = false, recordMode = ScreenshotTestRule.RecordMode.Record
) {

    @Composable
    override fun Content() {
        Assume.assumeFalse(true)
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
        runTest {
            Bottom1Button()
        }
    }

    @Test
    fun bottom2Buttons() {
        runTest {
            Bottom2Buttons()
        }
    }

    @Test
    fun bottom3Buttons() {
        runTest {
            Bottom3Buttons()
        }
    }

    @Test
    fun bottomOtherChips() {
        runTest {
            BottomOtherChips()
        }
    }

    @Test
    fun bottomOtherCards() {
        runTest {
            BottomOtherCards()
        }
    }

    @Test
    fun bottomOtherText() {
        runTest {
            BottomOtherText()
        }
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
