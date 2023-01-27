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
@file:OptIn(
    ExperimentalHorologistPaparazziApi::class,
    ExperimentalHorologistComposablesApi::class
)

package com.google.android.horologist.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.wear.compose.foundation.lazy.ScalingLazyListState
import androidx.wear.compose.material.Chip
import androidx.wear.compose.material.ChipDefaults
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.PositionIndicator
import androidx.wear.compose.material.Scaffold
import androidx.wear.compose.material.Text
import com.google.android.horologist.compose.layout.ScalingLazyColumnDefaults
import com.google.android.horologist.compose.layout.ScalingLazyColumnState
import com.google.android.horologist.compose.tools.RoundPreview
import com.google.android.horologist.compose.tools.a11y.forceState
import com.google.android.horologist.paparazzi.ExperimentalHorologistPaparazziApi
import com.google.android.horologist.paparazzi.WearPaparazzi
import org.junit.Rule
import org.junit.Test

class SectionedListTest {
    @get:Rule
    val paparazzi = WearPaparazzi()

    @Test
    fun loadingSection() {
        paparazzi.snapshot {
            val columnState = positionedState(0, 0)

            SectionedListPreview(columnState.state) {
                SectionedList(columnState = columnState) {
                    downloadsSection(state = Section.State.Loading())

                    favouritesSection(state = Section.State.Empty())
                }
            }
        }
    }

    @Test
    fun loadedSection() {
        paparazzi.snapshot {
            val columnState = positionedState(0, 0)

            SectionedListPreview(columnState.state) {
                SectionedList(columnState = columnState) {
                    downloadsSection(state = Section.State.Loaded(downloads))

                    favouritesSection(state = Section.State.Failed())
                }
            }
        }
    }

    @Test
    fun loadedSection_secondPage() {
        paparazzi.snapshot {
            val columnState = positionedState(4, 0)

            SectionedListPreview(columnState.state) {
                SectionedList(columnState = columnState) {
                    downloadsSection(state = Section.State.Loaded(downloads))

                    favouritesSection(state = Section.State.Failed())
                }
            }
        }
    }

    @Test
    fun failedSection() {
        paparazzi.snapshot {
            val columnState = positionedState(0, 0)

            SectionedListPreview(columnState.state) {
                SectionedList(columnState = columnState) {
                    downloadsSection(state = Section.State.Failed())

                    favouritesSection(state = Section.State.Loaded(favourites))
                }
            }
        }
    }

    @Test
    fun failedSection_secondPage() {
        paparazzi.snapshot {
            val columnState = positionedState(4, 0)

            SectionedListPreview(columnState.state) {
                SectionedList(columnState = columnState) {
                    downloadsSection(state = Section.State.Failed())

                    favouritesSection(state = Section.State.Loaded(favourites))
                }
            }
        }
    }

    @Test
    fun emptySection() {
        paparazzi.snapshot {
            val columnState = positionedState(0, 0)

            SectionedListPreview(columnState.state) {
                SectionedList(columnState = columnState) {
                    downloadsSection(state = Section.State.Empty())

                    favouritesSection(state = Section.State.Loading())
                }
            }
        }
    }

    @Composable
    private fun SectionedListPreview(
        scrollState: ScalingLazyListState,
        content: @Composable () -> Unit
    ) {
        RoundPreview(round = true) {
            Scaffold(
                modifier = Modifier.fillMaxSize(),
                positionIndicator = {
                    PositionIndicator(scrollState)
                }
            ) {
                Box(modifier = Modifier.background(Color.Black)) {
                    content()
                }
            }
        }
    }

    private val downloads = listOf("Nu Metal Essentials", "00s Rock")

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
    private fun DownloadsHeader() {
        Text(
            text = "Downloads",
            modifier = Modifier.padding(bottom = 12.dp),
            overflow = TextOverflow.Ellipsis,
            maxLines = 3,
            style = MaterialTheme.typography.title3
        )
    }

    @Composable
    private fun DownloadsLoading() {
        PlaceholderChip(colors = ChipDefaults.secondaryChipColors())
    }

    @Composable
    private fun DownloadsLoaded(text: String) {
        Chip(
            label = {
                Text(
                    text = text,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Left,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 2
                )
            },
            onClick = { },
            modifier = Modifier.fillMaxWidth(),
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
            colors = ChipDefaults.secondaryChipColors()
        )
    }

    @Composable
    private fun DownloadsFailed() {
        Text(
            text = "Failed to load downloads. Please try again later.",
            modifier = Modifier.padding(top = 8.dp, bottom = 8.dp),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.body2
        )
    }

    @Composable
    private fun DownloadsEmpty() {
        Text(
            text = "Download music to start listening.",
            modifier = Modifier.padding(top = 8.dp, bottom = 8.dp),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.body2
        )
    }

    @Composable
    private fun DownloadsFooter() {
        Chip(
            label = {
                Text(
                    text = "More downloads..",
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 2
                )
            },
            onClick = { },
            modifier = Modifier.fillMaxWidth(),
            colors = ChipDefaults.secondaryChipColors()
        )
    }

    private val favourites = listOf("Dance Anthems", "Indie Jukebox")

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
            label = {
                Text(
                    text = text,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Left,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 2
                )
            },
            onClick = { },
            modifier = Modifier.fillMaxWidth(),
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
            label = {
                Text(
                    text = "More favourites..",
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 2
                )
            },
            onClick = { },
            modifier = Modifier.fillMaxWidth(),
            colors = ChipDefaults.secondaryChipColors()
        )
    }
}

@Composable
public fun positionedState(
    topIndex: Int,
    topScrollOffset: Int
): ScalingLazyColumnState {
    return ScalingLazyColumnDefaults.belowTimeText().create().apply {
        state.forceState(topIndex, topScrollOffset)
    }
}
