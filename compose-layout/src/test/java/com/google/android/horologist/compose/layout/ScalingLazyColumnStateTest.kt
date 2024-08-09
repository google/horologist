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

import androidx.wear.compose.material.Text
import androidx.wear.compose.material.TimeText
import com.google.android.horologist.compose.layout.ScalingLazyColumnDefaults.ItemType
import com.google.android.horologist.screenshots.FixedTimeSource
import com.google.android.horologist.screenshots.rng.WearDevice
import com.google.android.horologist.screenshots.rng.WearScreenshotTest
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.ParameterizedRobolectricTestRunner
@RunWith(ParameterizedRobolectricTestRunner::class)
class ScalingLazyColumnStateTest(override val device: WearDevice) : WearScreenshotTest() {

    public override val tolerance: Float = 0.1f

    override fun testName(suffix: String): String =
        "src/test/snapshots/" +
            "${javaClass.simpleName}_" +
            "${testInfo.methodName}_" +
            "${super.device?.id ?: WearDevice.GenericLargeRound.id}" +
            "$suffix.png"

    @Test
    fun testRememberResponsiveColumnState() =
        runTest {
            AppScaffold(
                timeText = { TimeText(timeSource = FixedTimeSource) },
            ) {
                val columnState = rememberResponsiveColumnState(
                    contentPadding = ScalingLazyColumnDefaults.padding(
                        first = ItemType.Text,
                        last = ItemType.Text,
                    ),
                )
                ScreenScaffold(scrollState = columnState) {
                    ScalingLazyColumn(
                        columnState = columnState,
                    ) {
                        items(100) {
                            Text("Item $it")
                        }
                    }
                }
            }
        }

    @Test
    fun testSetInitialRememberResponsiveColumnState() =
        runTest {
            AppScaffold(
                timeText = { TimeText(timeSource = FixedTimeSource) },
            ) {
                val columnState = rememberResponsiveColumnState(
                    contentPadding = ScalingLazyColumnDefaults.padding(
                        first = ItemType.Text,
                        last = ItemType.Text,
                    ),
                    initialItemIndex = 4,
                )
                ScreenScaffold(scrollState = columnState) {
                    ScalingLazyColumn(
                        columnState = columnState,
                    ) {
                        items(100) {
                            Text("Item $it")
                        }
                    }
                }
            }
        }

    companion object {
        @JvmStatic
        @ParameterizedRobolectricTestRunner.Parameters
        fun devices() = WearDevice.entries
    }
}
