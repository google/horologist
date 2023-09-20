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

package com.google.android.horologist.screensizes

import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontWeight
import androidx.wear.compose.material.MaterialTheme
import com.google.android.horologist.compose.tools.Device
import com.google.android.horologist.compose.tools.GooglePixelWatch
import com.google.android.horologist.compose.tools.LargeRound
import com.google.android.horologist.compose.tools.MobvoiTicWatchPro5
import com.google.android.horologist.compose.tools.SamsungGalaxyWatch5
import com.google.android.horologist.compose.tools.SamsungGalaxyWatch6Large
import com.google.android.horologist.compose.tools.SmallRound
import com.google.android.horologist.compose.tools.copy
import com.google.android.horologist.screenshots.ScreenshotBaseTest
import com.google.android.horologist.screenshots.ScreenshotTestRule.Companion.defaultScreenTimeText
import com.google.android.horologist.screenshots.ScreenshotTestRule.Companion.screenshotTestRuleParams
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.ParameterizedRobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.Shadows
import org.robolectric.shadows.ShadowDisplay

@RunWith(ParameterizedRobolectricTestRunner::class)
abstract class ScreenSizeTest(
    val device: Device,
    val showTimeText: Boolean
) : ScreenshotBaseTest(
    screenshotTestRuleParams {
        screenTimeText = if (showTimeText) {
            defaultScreenTimeText()
        } else {
            { }
        }
        testLabel = device.name.lowercase().replace("\\W+".toRegex(), "")
    },
) {
    @Composable
    abstract fun Content()

    @Test
    fun screenshot() {
        val shadowDisplay = Shadows.shadowOf(ShadowDisplay.getDefaultDisplay())
        shadowDisplay.setDensity(device.density)
        shadowDisplay.setHeight(device.screenSizePx)
        shadowDisplay.setWidth(device.screenSizePx)

        RuntimeEnvironment.setFontScale(device.fontScale)
        RuntimeEnvironment.setQualifiers("+w${device.screenSizeDp}dp-h${device.screenSizeDp}dp")

        screenshotTestRule.setContent(takeScreenshot = true) {
            MaterialTheme(typography = MaterialTheme.typography.copy { this.copy(fontWeight = if (device.boldText) FontWeight.Bold else FontWeight.Medium) }) {
                Content()
            }
        }
    }


    companion object {
        @JvmStatic
        @ParameterizedRobolectricTestRunner.Parameters
        fun devices() = listOf(
            MobvoiTicWatchPro5,
            SamsungGalaxyWatch5,
            SamsungGalaxyWatch6Large,
            GooglePixelWatch,
            SmallRound,
            LargeRound,
            GooglePixelWatch.copy("Small Device, Big Fonts", fontScale = 1.24f, boldText = true),
            MobvoiTicWatchPro5.copy("Large Device, Small Fonts", fontScale = 0.94f)
        )
    }
}