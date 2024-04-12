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

@file:Suppress("INVISIBLE_MEMBER", "INVISIBLE_REFERENCE")
@file:OptIn(ExperimentalRoborazziApi::class)

package com.google.android.horologist.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.github.takahirom.roborazzi.ExperimentalRoborazziApi
import com.github.takahirom.roborazzi.RoborazziOptions
import com.github.takahirom.roborazzi.ThresholdValidator
import com.github.takahirom.roborazzi.captureRoboImage
import com.google.android.horologist.screenshots.rng.WearLegacyScreenTest
import org.junit.Test

class MarqueeTest : WearLegacyScreenTest() {

    @Test
    fun noMarquee() {
        runComponentTest {
            MarqueeSample("Sia")
        }
    }

    @Test
    fun marquee() {
        withDrawingEnabled {
            composeRule.mainClock.autoAdvance = false
            composeRule.setContent {
                withImageLoader(imageLoader) {
                    Box(
                        modifier = Modifier
                            .wrapContentSize()
                            .background(Color.Black),
                    ) {
                        MarqueeSample("Tikki Tikki Tembo-no Sa Rembo-chari Bari Ruchi-pip Peri Pembo")
                    }
                }
            }
            captureComponentImage()
            composeRule.mainClock.advanceTimeBy(4_500)
            captureComponentImage("_4500")
        }
    }

    public fun runComponentTest(
        content: @Composable () -> Unit,
    ) {
        withDrawingEnabled {
            composeRule.setContent {
                withImageLoader(imageLoader) {
                    Box(
                        modifier = Modifier
                            .wrapContentSize()
                            .background(Color.Black),
                    ) {
                        content()
                    }
                }
            }
            captureComponentImage()
        }
    }

    private fun captureComponentImage(suffix: String = "") {
        composeRule.onRoot().captureRoboImage(
            filePath = testName(suffix),
            roborazziOptions = RoborazziOptions(
                recordOptions = RoborazziOptions.RecordOptions(
                    applyDeviceCrop = false,
                ),
                compareOptions = RoborazziOptions.CompareOptions(
                    resultValidator = ThresholdValidator(tolerance),
                ),
            ),
        )
    }

    @Composable
    private fun MarqueeSample(text: String) {
        MarqueeText(
            text = text,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .width(192.dp),
        )
    }
}
