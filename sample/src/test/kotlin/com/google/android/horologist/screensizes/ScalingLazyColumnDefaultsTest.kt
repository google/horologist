/*
 * Copyright 2023 The Android Open Source Project
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

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.test.hasScrollToNodeAction
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.unit.dp
import androidx.test.espresso.action.ViewActions.swipeUp
import androidx.wear.compose.foundation.lazy.ScalingLazyListState
import androidx.wear.compose.material.AppCard
import androidx.wear.compose.material.Text
import com.google.android.horologist.composables.SectionedList
import com.google.android.horologist.compose.layout.ScalingLazyColumn
import com.google.android.horologist.compose.layout.ScalingLazyColumnState
import com.google.android.horologist.compose.layout.rememberResponsiveColumnState
import com.google.android.horologist.compose.material.Chip
import com.google.android.horologist.compose.material.Title
import com.google.android.horologist.compose.tools.Device
import com.google.android.horologist.sample.R
import com.google.android.horologist.sample.Screen
import org.junit.Test

class ScalingLazyColumnDefaultsTest(device: Device) :
    WearLegacyScreenSizeTest(
        device = device,
        showTimeText = false,
    ) {

        @Composable
        override fun Content() {
            Standard()
        }

        @Test
        fun standard_end() {
            runTest(capture = false) {
                val listState = ScalingLazyListState()

                androidx.wear.compose.foundation.lazy.ScalingLazyColumn(
                    state = listState,
                    modifier = Modifier.fillMaxSize(),
                ) {
                    items(20) {
                        SampleAppCard()
                    }
                }
            }

            composeRule.onNode(hasScrollToNodeAction())
                .performTouchInput { repeat(10) { swipeUp() } }

            captureScreenshot()
        }

        @Test
        fun standard_chips() {
            runTest {
                val listState = ScalingLazyListState()

                androidx.wear.compose.foundation.lazy.ScalingLazyColumn(
                    state = listState,
                    modifier = Modifier.fillMaxSize(),
                ) {
                    items(20) {
                        SampleChip(it)
                    }
                }
            }
        }

        @Test
        fun responsive_chips() {
            runTest {
                val columnState = rememberResponsiveColumnState()

                SampleChipMenu(columnState = columnState)
            }
        }

        @Test
        fun standard_chips_end() {
            runTest(capture = false) {
                val listState = ScalingLazyListState()

                androidx.wear.compose.foundation.lazy.ScalingLazyColumn(
                    state = listState,
                    modifier = Modifier.fillMaxSize(),
                ) {
                    items(20) {
                        SampleChip(it)
                    }
                }

                LaunchedEffect(Unit) {
                    listState.scrollToItem(100, 0)
                }
            }

            composeRule.onNode(hasScrollToNodeAction())
                .performTouchInput { repeat(10) { swipeUp() } }

            captureScreenshot()
        }

        @Test
        fun responsive_chips_end() {
            runTest(capture = false) {
                val columnState = rememberResponsiveColumnState()

                SampleChipMenu(columnState = columnState)
            }

            composeRule.onNode(hasScrollToNodeAction())
                .performTouchInput { repeat(10) { swipeUp() } }

            captureScreenshot()
        }

        @Composable
        fun SampleMenu(columnState: ScalingLazyColumnState, modifier: Modifier = Modifier) {
            SectionedList(
                columnState = columnState,
                modifier = modifier.fillMaxSize(),
            ) {
                section(
                    listOf(
                        Pair(
                            R.string.sectionedlist_stateless_sections_menu,
                            Screen.SectionedListStatelessScreen.route,
                        ),
                        Pair(
                            R.string.sectionedlist_stateful_sections_menu,
                            Screen.SectionedListStatefulScreen.route,
                        ),
                        Pair(
                            R.string.sectionedlist_expandable_sections_menu,
                            Screen.SectionedListExpandableScreen.route,
                        ),
                    ),
                ) {
                    header {
                        Title(
                            stringResource(R.string.sectionedlist_samples_title),
                            Modifier.padding(vertical = 8.dp),
                        )
                    }

                    loaded {
                        SampleAppCard()
                    }
                }
            }
        }

        @Composable
        private fun SampleAppCard() {
            AppCard(
                onClick = { },
                appName = {
                    Text("App Name")
                },
                time = {
                    Text("12:05")
                },
                title = {
                    Text("Title")
                },
            ) {
                Text("Content\nContent\nContent")
            }
        }

        @Composable
        fun SampleChipMenu(columnState: ScalingLazyColumnState, modifier: Modifier = Modifier) {
            ScalingLazyColumn(columnState = columnState, modifier = modifier) {
                items(20) {
                    SampleChip(it)
                }
            }
        }

        @Composable
        private fun SampleChip(it: Int) {
            Chip(label = "Chip $it", onClick = { /*TODO*/ })
        }
    }
