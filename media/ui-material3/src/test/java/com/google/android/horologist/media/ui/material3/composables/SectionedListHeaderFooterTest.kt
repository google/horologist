/*
 * Copyright 2025 The Android Open Source Project
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

package com.google.android.horologist.media.ui.material3.composables

import androidx.wear.compose.foundation.lazy.rememberScalingLazyListState
import androidx.wear.compose.material3.ScreenScaffold
import com.google.android.horologist.screenshots.rng.WearLegacyScreenTest
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.ParameterizedRobolectricTestRunner

@RunWith(ParameterizedRobolectricTestRunner::class)
class SectionedListHeaderFooterTest(
    private val headerVisibleStatesParam: Section.VisibleStates,
    private val footerVisibleStatesParam: Section.VisibleStates,
    private val sectionStateParam: Section.State<String>,
) : WearLegacyScreenTest() {

    @Test
    fun test() {
        runTest {
            val scrollState = rememberScalingLazyListState()

            ScreenScaffold(scrollState = scrollState) {
                SectionedList(scrollState = scrollState) {
                    section(state = sectionStateParam) {
                        header(visibleStates = headerVisibleStatesParam) { SectionedListTest.DownloadsHeader() }

                        loading { SectionedListTest.DownloadsLoading() }

                        loaded { SectionedListTest.DownloadsLoaded(it) }

                        failed { SectionedListTest.DownloadsFailed() }

                        empty { SectionedListTest.DownloadsEmpty() }

                        footer(visibleStates = footerVisibleStatesParam) { SectionedListTest.DownloadsFooter() }
                    }
                }
            }
        }
    }

    companion object {
        @JvmStatic
        @ParameterizedRobolectricTestRunner.Parameters
        @Suppress("unused") // it's used by the test as params
        fun params() = listOf(
            arrayOf(
                Section.ALL_STATES,
                Section.NO_STATES,
                Section.State.Loading,
            ),
            arrayOf(
                Section.ALL_STATES,
                Section.NO_STATES,
                Section.State.Loaded(SectionedListTest.downloads),
            ),
            arrayOf(
                Section.ALL_STATES,
                Section.NO_STATES,
                Section.State.Failed,
            ),
            arrayOf(
                Section.ALL_STATES,
                Section.NO_STATES,
                Section.State.Empty,
            ),
            arrayOf(
                Section.NO_STATES,
                Section.ALL_STATES,
                Section.State.Loading,
            ),
            arrayOf(
                Section.NO_STATES,
                Section.ALL_STATES,
                Section.State.Loaded(SectionedListTest.downloads),
            ),
            arrayOf(
                Section.NO_STATES,
                Section.ALL_STATES,
                Section.State.Failed,
            ),
            arrayOf(
                Section.NO_STATES,
                Section.ALL_STATES,
                Section.State.Empty,
            ),
        )
    }
}
