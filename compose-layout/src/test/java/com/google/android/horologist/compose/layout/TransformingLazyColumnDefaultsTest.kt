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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowUpward
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.wear.compose.foundation.lazy.TransformingLazyColumn
import androidx.wear.compose.foundation.lazy.TransformingLazyColumnState
import androidx.wear.compose.foundation.lazy.rememberTransformingLazyColumnState
import androidx.wear.compose.material3.AppScaffold
import androidx.wear.compose.material3.EdgeButton
import androidx.wear.compose.material3.EdgeButtonSize
import androidx.wear.compose.material3.Icon
import androidx.wear.compose.material3.IconButton
import androidx.wear.compose.material3.ListHeader
import androidx.wear.compose.material3.MaterialTheme
import androidx.wear.compose.material3.OutlinedIconButton
import androidx.wear.compose.material3.ScreenScaffold
import androidx.wear.compose.material3.SurfaceTransformation
import androidx.wear.compose.material3.Text
import androidx.wear.compose.material3.TimeText
import androidx.wear.compose.material3.TitleCard
import androidx.wear.compose.material3.lazy.rememberTransformationSpec
import androidx.wear.compose.material3.lazy.transformedHeight
import androidx.wear.compose.material3.timeTextCurvedText
import com.google.android.horologist.compose.layout.ColumnItemType.Companion.EdgeButtonPadding
import com.google.android.horologist.screenshots.rng.WearDevice
import com.google.android.horologist.screenshots.rng.WearScreenshotTest
import kotlinx.coroutines.runBlocking
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.ParameterizedRobolectricTestRunner

@RunWith(ParameterizedRobolectricTestRunner::class)
class TransformingLazyColumnDefaultsTest(override val device: WearDevice) : WearScreenshotTest() {

    override fun testName(suffix: String): String =
        "src/test/screenshots/${this.javaClass.simpleName}_${testInfo.methodName}_" +
            "${device.id}$suffix.png"

    @Composable
    override fun TestScaffold(content: @Composable (() -> Unit)) {
        content()
    }

    @Test
    fun TitleAndCard() {
        lateinit var columnState: TransformingLazyColumnState
        runTest {
            AppScaffold(
                timeText = {
                    TimeText(timeSource = FixedTimeSource3)
                },
                // Why black needed here
                modifier = Modifier.background(MaterialTheme.colorScheme.background),
            ) {
                columnState = rememberTransformingLazyColumnState()
                ScreenScaffold(
                    scrollState = columnState,
                    contentPadding = rememberResponsiveColumnPadding(
                        first = ColumnItemType.ListHeader,
                        last = ColumnItemType.Card,
                    ),
                ) { contentPadding ->
                    val transformationSpec = rememberTransformationSpec()

                    TransformingLazyColumn(
                        state = columnState,
                        contentPadding = contentPadding,
                        modifier = Modifier
                            .fillMaxSize()
                            .testTag("TransformingLazyColumn"),
                    ) {
                        item {
                            ListHeader {
                                Text("Title")
                            }
                        }
                        items(3) {
                            TitleCard(
                                onClick = { /* Do something */ },
                                title = { Text("Title card") },
                                time = { Text("now") },
                                modifier = Modifier.transformedHeight(this, transformationSpec),
                                transformation = SurfaceTransformation(transformationSpec),
                            ) { Text("Card content") }
                        }
                    }
                }
            }
        }

        composeRule.waitForIdle()

        runBlocking {
            columnState.scroll {
                scrollBy(2000f)
            }
        }

        captureScreenshot("_end")
    }

    @Test
    fun ButtonAndEdgeButton() {
        lateinit var columnState: TransformingLazyColumnState
        runTest {
            AppScaffold(
                timeText = {
                    TimeText(timeSource = FixedTimeSource3)
                },
                // Why black needed here
                modifier = Modifier.background(MaterialTheme.colorScheme.background),
            ) {
                columnState = rememberTransformingLazyColumnState()
                ScreenScaffold(
                    scrollState = columnState,
                    contentPadding = rememberResponsiveColumnPadding(
                        first = ColumnItemType.IconButton,
                        last = EdgeButtonPadding,
                    ),
                    edgeButton = {
                        EdgeButton(onClick = { }, buttonSize = EdgeButtonSize.Large) {
                            Text("To top")
                        }
                    },
                ) { contentPadding ->
                    val transformationSpec = rememberTransformationSpec()

                    TransformingLazyColumn(
                        state = columnState,
                        contentPadding = contentPadding,
                        modifier = Modifier
                            .fillMaxSize()
                            .testTag("TransformingLazyColumn"),
                    ) {
                        item {
                            IconButton(onClick = {}) {
                                Icon(
                                    imageVector = Icons.Rounded.ArrowUpward,
                                    contentDescription = null,
                                )
                            }
                        }
                        items(3) {
                            TitleCard(
                                onClick = { /* Do something */ },
                                title = { Text("Title card") },
                                time = { Text("now") },
                                modifier = Modifier.transformedHeight(this, transformationSpec),
                                transformation = SurfaceTransformation(transformationSpec),
                            ) { Text("Card content") }
                        }
                    }
                }
            }
        }

        composeRule.waitForIdle()

        runBlocking {
            columnState.scroll {
                scrollBy(2000f)
            }
        }

        captureScreenshot("_end")
    }

    @Test
    fun IconButtonAndExtraSmallEdgeButton() {
        lateinit var columnState: TransformingLazyColumnState
        runTest {
            AppScaffold(
                timeText = {
                    TimeText {
                        timeTextCurvedText("10:10")
                    }
                },
                // Why black needed here
                modifier = Modifier.background(MaterialTheme.colorScheme.background),
            ) {
                columnState = rememberTransformingLazyColumnState()
                ScreenScaffold(
                    scrollState = columnState,
                    contentPadding = rememberResponsiveColumnPadding(
                        first = ColumnItemType.IconButton,
                        last = EdgeButtonPadding,
                    ),
                    edgeButton = {
                        EdgeButton(onClick = { }, buttonSize = EdgeButtonSize.ExtraSmall) {
                            Text("To top")
                        }
                    },
                ) { contentPadding ->
                    TransformingLazyColumn(
                        state = columnState,
                        contentPadding = contentPadding,
                        modifier = Modifier
                            .fillMaxSize()
                            .testTag("TransformingLazyColumn"),
                    ) {
                        item {
                            OutlinedIconButton(onClick = {}) {
                                Icon(
                                    imageVector = Icons.Rounded.ArrowUpward,
                                    contentDescription = null,
                                )
                            }
                        }
                        items(3) {
                            TitleCard(
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
            columnState.scroll {
                scrollBy(2000f)
            }
        }

        captureScreenshot("_end")
    }

    companion object {
        @JvmStatic
        @ParameterizedRobolectricTestRunner.Parameters
        fun devices() = WearDevice.entries
    }
}
