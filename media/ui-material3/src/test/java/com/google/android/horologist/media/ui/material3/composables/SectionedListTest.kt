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

@file:Suppress(
    "TestFunctionName", // incorrectly flagging composable functions
)

package com.google.android.horologist.media.ui.material3.composables

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.FeaturedPlayList
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.wear.compose.foundation.lazy.rememberScalingLazyListState
import androidx.wear.compose.material3.ButtonDefaults
import androidx.wear.compose.material3.FilledTonalButton
import androidx.wear.compose.material3.Icon
import androidx.wear.compose.material3.MaterialTheme
import androidx.wear.compose.material3.ScreenScaffold
import androidx.wear.compose.material3.Text
import com.google.android.horologist.screenshots.rng.WearLegacyScreenTest
import org.junit.Test

class SectionedListTest : WearLegacyScreenTest() {

    @Test
    fun loadingSection() {
        runTest {
            val scrollState = rememberScalingLazyListState()

            ScreenScaffold(scrollState = scrollState) {
                SectionedList(scrollState = scrollState) {
                    downloadsSection(state = Section.State.Loading)
                    favouritesSection(state = Section.State.Empty)
                }
            }
        }
    }

    @Test
    fun loadedSection() {
        runTest {
            val scrollState = rememberScalingLazyListState()

            ScreenScaffold(scrollState = scrollState) {
                SectionedList(scrollState = scrollState) {
                    downloadsSection(state = Section.State.Loaded(downloads))
                    favouritesSection(state = Section.State.Failed)
                }
            }
        }
    }

    @Test
    fun loadedSection_secondPage() {
        runTest(captureScreenshot = false) {
            val scrollState = rememberScalingLazyListState()

            ScreenScaffold(scrollState = scrollState) {
                SectionedList(scrollState = scrollState) {
                    downloadsSection(state = Section.State.Loaded(downloads))
                    favouritesSection(state = Section.State.Failed)
                }
            }
        }

        // TODO https://github.com/google/horologist/issues/2237
//        composeRule.onNode(hasScrollToNodeAction())
//            .performTouchInput { repeat(10) { swipeUp() } }
//
//        captureScreenshot()
    }

    @Test
    fun failedSection() {
        runTest {
            val scrollState = rememberScalingLazyListState()

            ScreenScaffold(scrollState = scrollState) {
                SectionedList(scrollState = scrollState) {
                    downloadsSection(state = Section.State.Failed)
                    favouritesSection(state = Section.State.Loaded(favourites))
                }
            }
        }
    }

    @Test
    fun failedSection_secondPage() {
        runTest(captureScreenshot = false) {
            val scrollState = rememberScalingLazyListState()

            ScreenScaffold(scrollState = scrollState) {
                SectionedList(scrollState = scrollState) {
                    downloadsSection(state = Section.State.Failed)
                    favouritesSection(state = Section.State.Loaded(favourites))
                }
            }
        }

        // TODO https://github.com/google/horologist/issues/2237
//        composeRule.onNode(hasScrollToNodeAction())
//            .performTouchInput { repeat(10) { swipeUp() } }
//
//        captureScreenshot()
    }

    @Test
    fun emptySection() {
        runTest {
            val scrollState = rememberScalingLazyListState()

            ScreenScaffold(scrollState = scrollState) {
                SectionedList(scrollState = scrollState) {
                    downloadsSection(state = Section.State.Empty)

                    favouritesSection(state = Section.State.Loading)
                }
            }
        }
    }

    @Test
    fun emptyContentForStates() {
        runTest {
            val scrollState = rememberScalingLazyListState()

            ScreenScaffold(scrollState = scrollState) {
                SectionedList(scrollState = scrollState) {
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
                style = MaterialTheme.typography.titleLarge,
            )
        }

        @Composable
        fun DownloadsLoading() {
            PlaceholderButton(
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.filledTonalButtonColors()
            )
        }

        @Composable
        fun DownloadsLoaded(text: String) {
            FilledTonalButton(
                label = { Text(text) },
                onClick = { },
                icon = {
                    Icon(
                        imageVector = Icons.AutoMirrored.Default.FeaturedPlayList,
                        contentDescription = null,
                        modifier = Modifier
                            .size(ButtonDefaults.LargeIconSize)
                            .clip(CircleShape),
                        tint = Color.Green,
                    )
                },
            )
        }

        @Composable
        fun DownloadsFailed() {
            Text(
                text = "Failed to load downloads. Please try again later.",
                modifier = Modifier.padding(top = 8.dp, bottom = 8.dp),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodyMedium,
            )
        }

        @Composable
        fun DownloadsEmpty() {
            Text(
                text = "Download music to start listening.",
                modifier = Modifier.padding(top = 8.dp, bottom = 8.dp),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodyMedium,
            )
        }

        @Composable
        fun DownloadsFooter() {
            FilledTonalButton(
                label = { Text("More downloads..") },
                onClick = { },
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
                style = MaterialTheme.typography.titleLarge,
            )
        }

        @Composable
        private fun FavouritesLoading() {
            PlaceholderButton(colors = ButtonDefaults.filledTonalButtonColors())
        }

        @Composable
        private fun FavouritesLoaded(text: String) {
            FilledTonalButton(
                label = { Text(text) },
                onClick = { },
                icon = {
                    Icon(
                        imageVector = Icons.AutoMirrored.Default.FeaturedPlayList,
                        contentDescription = null,
                        modifier = Modifier
                            .size(ButtonDefaults.LargeIconSize)
                            .clip(CircleShape),
                        tint = Color.Green,
                    )
                },
            )
        }

        @Composable
        private fun FavouritesFailed() {
            Text(
                text = "Failed to load favourites. Please try again later.",
                modifier = Modifier.padding(top = 8.dp, bottom = 8.dp),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodyMedium,
            )
        }

        @Composable
        private fun FavouritesEmpty() {
            Text(
                text = "Mark songs or albums as favourites to see them here.",
                modifier = Modifier.padding(top = 8.dp, bottom = 8.dp),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodyMedium,
            )
        }

        @Composable
        fun FavouritesFooter() {
            FilledTonalButton(
                label = { Text("More favourites..") },
                onClick = { },
            )
        }
    }
}
