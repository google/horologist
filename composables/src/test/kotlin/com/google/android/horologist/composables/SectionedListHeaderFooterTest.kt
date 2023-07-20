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

package com.google.android.horologist.composables

import com.google.android.horologist.composables.Section.Companion.ALL_STATES
import com.google.android.horologist.composables.Section.Companion.NO_STATES
import com.google.android.horologist.composables.SectionedListTest.Companion.DownloadsEmpty
import com.google.android.horologist.composables.SectionedListTest.Companion.DownloadsFailed
import com.google.android.horologist.composables.SectionedListTest.Companion.DownloadsFooter
import com.google.android.horologist.composables.SectionedListTest.Companion.DownloadsHeader
import com.google.android.horologist.composables.SectionedListTest.Companion.DownloadsLoaded
import com.google.android.horologist.composables.SectionedListTest.Companion.DownloadsLoading
import com.google.android.horologist.composables.SectionedListTest.Companion.SectionedListPreview
import com.google.android.horologist.composables.SectionedListTest.Companion.downloads
import com.google.android.horologist.screenshots.ScreenshotBaseTest
import com.google.android.horologist.screenshots.ScreenshotTestRule.Companion.screenshotTestRuleParams
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.ParameterizedRobolectricTestRunner

@RunWith(ParameterizedRobolectricTestRunner::class)
class SectionedListHeaderFooterTest(
    private val headerVisibleStatesParam: Section.VisibleStates,
    private val footerVisibleStatesParam: Section.VisibleStates,
    private val sectionStateParam: Section.State<String>
) : ScreenshotBaseTest(
    screenshotTestRuleParams {
        screenTimeText = {}
    }
) {

    @Test
    fun test() {
        screenshotTestRule.setContent(takeScreenshot = true) {
            val columnState = positionedState()

            SectionedListPreview(columnState) {
                SectionedList(columnState = columnState) {
                    section(state = sectionStateParam) {
                        header(visibleStates = headerVisibleStatesParam) { DownloadsHeader() }

                        loading { DownloadsLoading() }

                        loaded { DownloadsLoaded(it) }

                        failed { DownloadsFailed() }

                        empty { DownloadsEmpty() }

                        footer(visibleStates = footerVisibleStatesParam) { DownloadsFooter() }
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
                ALL_STATES,
                NO_STATES,
                Section.State.Loading
            ),
            arrayOf(
                ALL_STATES,
                NO_STATES,
                Section.State.Loaded(downloads)
            ),
            arrayOf(
                ALL_STATES,
                NO_STATES,
                Section.State.Failed
            ),
            arrayOf(
                ALL_STATES,
                NO_STATES,
                Section.State.Empty
            ),
            arrayOf(
                NO_STATES,
                ALL_STATES,
                Section.State.Loading
            ),
            arrayOf(
                NO_STATES,
                ALL_STATES,
                Section.State.Loaded(downloads)
            ),
            arrayOf(
                NO_STATES,
                ALL_STATES,
                Section.State.Failed
            ),
            arrayOf(
                NO_STATES,
                ALL_STATES,
                Section.State.Empty
            )
        )
    }
}
