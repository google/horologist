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

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import androidx.wear.compose.material.Text
import com.github.takahirom.roborazzi.RobolectricDeviceQualifiers
import com.github.takahirom.roborazzi.captureRoboImage
import com.google.android.horologist.screenshots.rng.WearScreenshotTest
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.ParameterizedRobolectricTestRunner
import org.robolectric.annotation.Config

@Config(
    sdk = [35],
    qualifiers = RobolectricDeviceQualifiers.LargeDesktop,
)
@RunWith(ParameterizedRobolectricTestRunner::class)
class FontScaleIndependentTest(val fontSize: Size) : WearScreenshotTest() {

    public override fun testName(suffix: String): String =
        "src/test/snapshots/images/" +
            "${this.javaClass.simpleName}_${fontSize.size}.png".also {
                println(it)
            }

    @Test
    fun testSizes() {
        val sizes = listOf(0.94f, 1f, 1.06f, 1.12f, 1.18f, 1.24f)
        captureRoboImage(testName("")) {
            val density = LocalDensity.current
            Column(modifier = Modifier.background(Color.Black)) {
                sizes.forEach { fontScale ->
                    CompositionLocalProvider(
                        LocalDensity provides Density(
                            density.density,
                            fontScale,
                        ),
                    ) {
                        Row {
                            Box(
                                modifier = Modifier.wrapContentSize(),
                                contentAlignment = Alignment.BottomStart,
                            ) {
                                FontScaleIndependent {
                                    Text(
                                        text = "Independent 1.0 |",
                                        fontSize = fontSize.size,
                                    )
                                }
                            }
                            Box(
                                modifier = Modifier.wrapContentSize(),
                                contentAlignment = Alignment.BottomStart,
                            ) {
                                val tm = rememberTextMeasurer()
                                val height = tm.measure("|", style = TextStyle.Default.copy(fontSize = fontSize.size)).size.height
                                Text(
                                    text = "| $fontScale Scale (${height}px)",
                                    fontSize = fontSize.size,
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    public companion object {
        @JvmStatic
        @ParameterizedRobolectricTestRunner.Parameters
        public fun fontSizes(): List<Size> = listOf(10.sp, 12.sp, 14.sp, 15.sp, 16.sp, 20.sp, 24.sp, 30.sp, 34.sp, 40.sp).map {
            Size(it)
        }
    }
}

data class Size(val size: TextUnit)
