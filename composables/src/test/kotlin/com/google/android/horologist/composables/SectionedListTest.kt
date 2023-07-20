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

@file:Suppress("TestFunctionName" /* incorrectly flagging composable functions */)

package com.google.android.horologist.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FeaturedPlayList
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.ChipDefaults
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.PositionIndicator
import androidx.wear.compose.material.Scaffold
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.TimeText
import com.google.android.horologist.compose.layout.ScalingLazyColumnState
import com.google.android.horologist.compose.layout.scrollAway
import com.google.android.horologist.compose.material.Chip
import com.google.android.horologist.screenshots.FixedTimeSource
import com.google.android.horologist.screenshots.ScreenshotBaseTest
import com.google.android.horologist.screenshots.ScreenshotTestRule.Companion.screenshotTestRuleParams
import org.junit.Test

class SectionedListTest : ScreenshotBaseTest(
    screenshotTestRuleParams {
        screenTimeText = {}
    }
) {

    @Test
    fun loadingSection() {
        screenshotTestRule.setContent(takeScreenshot = true) {
            val columnState = positionedState()

            SectionedListPreview(columnState) {
                SectionedList(columnState = columnState) {
                    downloadsSection(state = Section.State.Loading)

                    favouritesSection(state = Section.State.Empty)
                }
            }
        }
    }

    @Test
    fun loadedSection() {
        screenshotTestRule.setContent(takeScreenshot = true) {
            val columnState = positionedState()

            SectionedListPreview(columnState) {
                SectionedList(columnState = columnState) {
                    downloadsSection(state = Section.State.Loaded(downloads))

                    favouritesSection(state = Section.State.Failed)
                }
            }
        }
    }

    @Test
    fun loadedSection_secondPage() {
        screenshotTestRule.setContent(takeScreenshot = true) {
            val columnState = positionedState(4)

            SectionedListPreview(columnState) {
                SectionedList(columnState = columnState) {
                    downloadsSection(state = Section.State.Loaded(downloads))

                    favouritesSection(state = Section.State.Failed)
                }
            }
        }
    }

    @Test
    fun failedSection() {
        screenshotTestRule.setContent(takeScreenshot = true) {
            val columnState = positionedState()

            SectionedListPreview(columnState) {
                SectionedList(columnState = columnState) {
                    downloadsSection(state = Section.State.Failed)

                    favouritesSection(state = Section.State.Loaded(favourites))
                }
            }
        }
    }

    @Test
    fun failedSection_secondPage() {
        screenshotTestRule.setContent(takeScreenshot = true) {
            val columnState = positionedState(4)

            SectionedListPreview(columnState) {
                SectionedList(columnState = columnState) {
                    downloadsSection(state = Section.State.Failed)

                    favouritesSection(state = Section.State.Loaded(favourites))
                }
            }
        }
    }

    @Test
    fun emptySection() {
        screenshotTestRule.setContent(takeScreenshot = true) {
            val columnState = positionedState()

            SectionedListPreview(columnState) {
                SectionedList(columnState = columnState) {
                    downloadsSection(state = Section.State.Empty)

                    favouritesSection(state = Section.State.Loading)
                }
            }
        }
    }

    @Test
    fun emptyContentForStates() {
        screenshotTestRule.setContent(takeScreenshot = true) {
            val columnState = positionedState()

            SectionedListPreview(columnState) {
                SectionedList(columnState = columnState) {
                    section {
                        header { Text("Section 1") }
                        loaded { Text("Item 1") }
                    }

                    section {
                        header { Text("Section 2") }
                        loaded { Text("Item 1") }
                    }

                    section {
                        header { Text("Section 3") }
                        loaded { Text("Item 1") }
                    }

                    section {
                        header { Text("Section 4") }
                        loaded { Text("Item 1") }
                    }
                }
            }
        }
    }

    internal companion object {

        @Composable
        fun SectionedListPreview(
            columnState: ScalingLazyColumnState,
            content: @Composable () -> Unit
        ) {
            Scaffold(
                modifier = Modifier.fillMaxSize(),
                positionIndicator = {
                    PositionIndicator(columnState.state)
                },
                timeText = {
                    TimeText(
                        modifier = Modifier.scrollAway(columnState),
                        timeSource = FixedTimeSource
                    )
                }
            ) {
                Box(modifier = Modifier.background(Color.Black)) {
                    content()
                }
            }
        }

        val downloads = listOf("Nu Metal Essentials", "00s Rock")

        private fun SectionedListScope.downloadsSection(state: Section.State<String>) {
            section(state = state) {
                header { DownloadsHeader() }

                loading { DownloadsLoading() }

                loaded { DownloadsLoaded(it) }

                failed { DownloadsFailed() }

                empty { DownloadsEmpty() }

                footer { DownloadsFooter() }
            }
        }

        @Composable
        fun DownloadsHeader() {
            Text(
                text = "Downloads",
                modifier = Modifier.padding(bottom = 12.dp),
                overflow = TextOverflow.Ellipsis,
                maxLines = 3,
                style = MaterialTheme.typography.title3
            )
        }

        @Composable
        fun DownloadsLoading() {
            PlaceholderChip(colors = ChipDefaults.secondaryChipColors())
        }

        @Composable
        fun DownloadsLoaded(text: String) {
            Chip(
                label = text,
                onClick = { },
                icon = {
                    Icon(
                        imageVector = Icons.Default.FeaturedPlayList,
                        contentDescription = null,
                        modifier = Modifier
                            .size(ChipDefaults.LargeIconSize)
                            .clip(CircleShape),
                        tint = Color.Green
                    )
                },
                largeIcon = true,
                colors = ChipDefaults.secondaryChipColors()
            )
        }

        @Composable
        fun DownloadsFailed() {
            Text(
                text = "Failed to load downloads. Please try again later.",
                modifier = Modifier.padding(top = 8.dp, bottom = 8.dp),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.body2
            )
        }

        @Composable
        fun DownloadsEmpty() {
            Text(
                text = "Download music to start listening.",
                modifier = Modifier.padding(top = 8.dp, bottom = 8.dp),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.body2
            )
        }

        @Composable
        fun DownloadsFooter() {
            Chip(
                label = "More downloads..",
                onClick = { },
                colors = ChipDefaults.secondaryChipColors()
            )
        }

        val favourites = listOf("Dance Anthems", "Indie Jukebox")

        private fun SectionedListScope.favouritesSection(state: Section.State<String>) {
            section(state = state) {
                header { FavouritesHeader() }

                loading { FavouritesLoading() }

                loaded { FavouritesLoaded(it) }

                failed { FavouritesFailed() }

                empty { FavouritesEmpty() }

                footer { FavouritesFooter() }
            }
        }

        @Composable
        private fun FavouritesHeader() {
            Text(
                text = "Favourites",
                modifier = Modifier.padding(top = 12.dp, bottom = 12.dp),
                overflow = TextOverflow.Ellipsis,
                maxLines = 3,
                style = MaterialTheme.typography.title3
            )
        }

        @Composable
        private fun FavouritesLoading() {
            PlaceholderChip(colors = ChipDefaults.secondaryChipColors())
        }

        @Composable
        private fun FavouritesLoaded(text: String) {
            Chip(
                label = text,
                onClick = { },
                icon = {
                    Icon(
                        imageVector = Icons.Default.FeaturedPlayList,
                        contentDescription = null,
                        modifier = Modifier
                            .size(ChipDefaults.LargeIconSize)
                            .clip(CircleShape),
                        tint = Color.Green
                    )
                },
                largeIcon = true,
                colors = ChipDefaults.secondaryChipColors()
            )
        }

        @Composable
        private fun FavouritesFailed() {
            Text(
                text = "Failed to load favourites. Please try again later.",
                modifier = Modifier.padding(top = 8.dp, bottom = 8.dp),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.body2
            )
        }

        @Composable
        private fun FavouritesEmpty() {
            Text(
                text = "Mark songs or albums as favourites to see them here.",
                modifier = Modifier.padding(top = 8.dp, bottom = 8.dp),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.body2
            )
        }

        @Composable
        fun FavouritesFooter() {
            Chip(
                label = "More favourites..",
                onClick = { },
                colors = ChipDefaults.secondaryChipColors()
            )
        }
    }
}
