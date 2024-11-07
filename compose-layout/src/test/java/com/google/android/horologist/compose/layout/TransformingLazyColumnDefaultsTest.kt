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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.wear.compose.foundation.lazy.TransformingLazyColumn
import androidx.wear.compose.foundation.lazy.TransformingLazyColumnState
import androidx.wear.compose.foundation.lazy.rememberTransformingLazyColumnState
import androidx.wear.compose.material3.AppScaffold
import androidx.wear.compose.material3.ListHeader
import androidx.wear.compose.material3.MaterialTheme
import androidx.wear.compose.material3.ScreenScaffold
import androidx.wear.compose.material3.Text
import androidx.wear.compose.material3.TimeText
import androidx.wear.compose.material3.TitleCard
import androidx.wear.compose.material3.lazy.scrollTransform
import com.google.android.horologist.screenshots.rng.WearDevice
import com.google.android.horologist.screenshots.rng.WearScreenshotTest
import kotlinx.coroutines.runBlocking
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.ParameterizedRobolectricTestRunner

@RunWith(ParameterizedRobolectricTestRunner::class)
class TransformingLazyColumnDefaultsTest(override val device: WearDevice) : WearScreenshotTest() {
    @Composable
    override fun TestScaffold(content: @Composable (() -> Unit)) {
        content()
    }

    @Test
    fun TitleAndCard() {
        lateinit var columnState: TransformingLazyColumnState
        runTest {
            AppScaffold(
                timeText = { TimeText {
                    text("10:10")
                } },
                // Why black needed here
                modifier = Modifier.background(MaterialTheme.colorScheme.background)
            ) {
                columnState = rememberTransformingLazyColumnState()
                ScreenScaffold(scrollState = columnState) {
                    TransformingLazyColumn(
                        state = columnState,
                        contentPadding = rememberResponsiveColumnPadding(
                            first = ColumnItemType.ListHeader,
                            last = ColumnItemType.Card
                        ),
                        modifier = Modifier.fillMaxSize().testTag("TransformingLazyColumn")
                    ) {
                        item {
                            ListHeader(modifier = Modifier.scrollTransform(this)) {
                                Text("Title")
                            }
                        }
                        items(3) {
                            TitleCard(
                                modifier = Modifier.scrollTransform(this),
                                onClick = { /* Do something */ },
                                title = { Text("Title card") },
                                time = { Text("now") },
                            ) { Text("Card content") }
                        }
                    }
                }
            }
        }

        composeRule.waitForIdle()

        runBlocking {
            columnState.scrollToItem(5)
        }

        captureScreenshot("_end")
    }

    companion object {
        @JvmStatic
        @ParameterizedRobolectricTestRunner.Parameters
        fun devices() = WearDevice.entries
    }
}
