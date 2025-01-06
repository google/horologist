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

package com.google.android.horologist.compose.material

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.takeOrElse
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextAlign
import androidx.wear.compose.material.LocalContentAlpha
import androidx.wear.compose.material.LocalContentColor
import androidx.wear.compose.material.LocalTextStyle
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.TimeText
import com.google.android.horologist.compose.layout.AppScaffold
import com.google.android.horologist.compose.layout.ScalingLazyColumn
import com.google.android.horologist.compose.layout.ScalingLazyColumnDefaults.ItemType
import com.google.android.horologist.compose.layout.ScalingLazyColumnDefaults.padding
import com.google.android.horologist.compose.layout.ScreenScaffold
import com.google.android.horologist.compose.layout.rememberResponsiveColumnState
import com.google.android.horologist.compose.material.ListHeaderDefaults.firstItemPadding
import com.google.android.horologist.screenshots.FixedTimeSource
import com.google.android.horologist.screenshots.rng.WearDevice
import com.google.android.horologist.screenshots.rng.WearDeviceScreenshotTest
import org.junit.Test

class SLCTitleTest(device: WearDevice) : WearDeviceScreenshotTest(device) {

    override fun testName(suffix: String): String =
        "src/test/screenshots/" +
            "${javaClass.simpleName}_" +
            "${testInfo.methodName}_" +
            "${super.device?.id ?: WearDevice.GenericLargeRound.id}" +
            "$suffix.png"

    @Test
    fun padding_16() {
        listWithPadding(ItemType.Text)
    }

    @Test
    fun padding_21() {
        listWithPadding(ItemType.BodyText)
    }

    fun listWithPadding(topPadding: ItemType) {
        runTest {
            Box {
                AppScaffold(timeText = { TimeText(timeSource = FixedTimeSource) }) {
                    val columnState = rememberResponsiveColumnState(
                        contentPadding = padding(
                            first = topPadding,
                            last = ItemType.Text,
                        ),
                    )

                    val alpha = remember {
                        derivedStateOf {
                            columnState.state.layoutInfo.visibleItemsInfo.getOrNull(0)?.alpha?.let {
                                String.format("%.2f", it * 100f)
                            }
                        }
                    }

                    ScreenScaffold(scrollState = columnState) {
                        ScalingLazyColumn(columnState = columnState) {
                            item {
                                ResponsiveListHeader(
                                    contentColor = Color.White,
                                    contentPadding = firstItemPadding(),
                                    modifier = Modifier.testTag("Header-1"),
                                ) {
                                    Text(
                                        modifier = Modifier.fillMaxWidth(),
                                        textAlign = TextAlign.Center,
                                        text = "Alpha ${alpha.value}",
                                    )
                                }
                            }
                            item {
                                ResponsiveListHeader(
                                    contentColor = Color.White,
                                    contentPadding = firstItemPadding(),
                                    modifier = Modifier.testTag("Header-2"),
                                ) {
                                    val color = Color.Unspecified
                                    val style = LocalTextStyle.current
                                    val localContentColor =
                                        LocalContentColor.current.copy(alpha = LocalContentAlpha.current)

                                    val textColor = color.takeOrElse {
                                        style.color.takeOrElse {
                                            localContentColor
                                        }
                                    }

                                    Text(
                                        modifier = Modifier.fillMaxWidth(),
                                        textAlign = TextAlign.Center,
                                        text = "Header ${textColor.hex}",
                                    )
                                }
                            }
                            item {
                                ResponsiveListHeader(
                                    contentColor = Color.White,
                                    contentPadding = firstItemPadding(),
                                ) {
                                    val style = LocalTextStyle.current
                                    BasicText(
                                        modifier = Modifier.fillMaxWidth(),
                                        text = "style ${style.color.hex}",
                                        color = { style.color },
                                    )
                                }
                            }
                            item {
                                ResponsiveListHeader(
                                    contentColor = Color.White,
                                    contentPadding = firstItemPadding(),
                                ) {
                                    val localContentColor =
                                        LocalContentColor.current.copy(alpha = LocalContentAlpha.current)
                                    BasicText(
                                        modifier = Modifier.fillMaxWidth(),
                                        text = "content color ${localContentColor.hex}",
                                        color = { localContentColor },
                                    )
                                }
                            }
                            item {
                                ResponsiveListHeader(
                                    contentColor = Color.White,
                                    contentPadding = firstItemPadding(),
                                ) {
                                    BasicText(
                                        modifier = Modifier.fillMaxWidth(),
                                        text = "White ${Color.White.hex}",
                                        color = { Color.White },
                                    )
                                }
                            }
                        }
                    }
                }

                Canvas(modifier = Modifier.fillMaxSize()) {
                    val paddingLine = topPadding.topPaddingPct * size.height
                    drawLine(Color.White, start = Offset(0f, paddingLine), end = Offset(size.width, paddingLine))
                }
            }
        }
    }
}

val Color.hex
    get() = String.format("#%06X", 0xFFFFFF and toArgb())
