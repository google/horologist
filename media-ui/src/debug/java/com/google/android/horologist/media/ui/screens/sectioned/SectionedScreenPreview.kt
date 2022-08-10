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

@file:OptIn(ExperimentalHorologistMediaUiApi::class)

package com.google.android.horologist.media.ui.screens.sectioned

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Downloading
import androidx.compose.material.icons.filled.FeaturedPlayList
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.rememberScalingLazyListState
import com.google.android.horologist.compose.tools.WearPreviewDevices
import com.google.android.horologist.media.ui.ExperimentalHorologistMediaUiApi
import com.google.android.horologist.media.ui.components.base.SecondaryPlaceholderChip
import com.google.android.horologist.media.ui.components.base.StandardChip
import com.google.android.horologist.media.ui.components.base.StandardChipType
import com.google.android.horologist.media.ui.components.base.Title
import com.google.android.horologist.media.ui.utils.rememberVectorPainter

@WearPreviewDevices
@Composable
fun SectionedScreenPreviewLoadingSection() {
    SectionedScreen(
        sections = listOf(
            downloadsLoadingSection,
            favouritesEmptySection
        ),
        focusRequester = FocusRequester(),
        scalingLazyListState = rememberScalingLazyListState()
    )
}

@WearPreviewDevices
@Composable
fun SectionedScreenPreviewLoadedSection() {
    SectionedScreen(
        sections = listOf(
            downloadsLoadedSection,
            favouritesFailedSection
        ),
        focusRequester = FocusRequester(),
        scalingLazyListState = rememberScalingLazyListState()
    )
}

@WearPreviewDevices
@Composable
fun SectionedScreenPreviewFailedSection() {
    SectionedScreen(
        sections = listOf(
            downloadsFailedSection,
            favouritesLoadedSection
        ),
        focusRequester = FocusRequester(),
        scalingLazyListState = rememberScalingLazyListState()
    )
}

@WearPreviewDevices
@Composable
fun SectionedScreenPreviewEmptySection() {
    SectionedScreen(
        sections = listOf(
            downloadsEmptySection,
            favouritesLoadingSection
        ),
        focusRequester = FocusRequester(),
        scalingLazyListState = rememberScalingLazyListState()
    )
}

private val downloadsLoadingSection = Section(
    state = Section.State.Loading {
        SecondaryPlaceholderChip()
    },
    headerContent = { DownloadsTitle() }
)

private val downloadsLoadedSection = Section(
    state = Section.State.Loaded(
        listOf(
            {
                StandardChip(
                    label = "Nu Metal Essentials",
                    onClick = { },
                    icon = "icon",
                    largeIcon = true,
                    placeholder = rememberVectorPainter(
                        image = Icons.Default.FeaturedPlayList,
                        tintColor = Color.Green
                    ),
                    chipType = StandardChipType.Secondary
                )
            },
            {
                StandardChip(
                    label = "00s Rock",
                    onClick = { },
                    secondaryLabel = "15%",
                    icon = Icons.Default.Downloading,
                    chipType = StandardChipType.Secondary
                )
            }
        )
    ),
    headerContent = { DownloadsTitle() }
)

private val downloadsFailedSection = Section(
    state = Section.State.Empty {
        Text(
            text = "Failed to load downloads. Please try again later.",
            modifier = Modifier.padding(top = 8.dp, bottom = 8.dp),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.body2
        )
    },
    headerContent = { DownloadsTitle() }
)

private val downloadsEmptySection = Section(
    state = Section.State.Empty {
        Text(
            text = "Download music to start listening",
            modifier = Modifier.padding(top = 8.dp, bottom = 8.dp),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.body2
        )
    },
    headerContent = { DownloadsTitle() }
)

@Composable
private fun DownloadsTitle() {
    Title(
        text = "Downloads",
        modifier = Modifier.padding(bottom = 12.dp)
    )
}

private val favouritesLoadingSection = Section(
    state = Section.State.Empty {
        SecondaryPlaceholderChip()
    },
    headerContent = { FavouritesTitle() }
)

private val favouritesLoadedSection = Section(
    state = Section.State.Loaded(
        listOf(
            {
                StandardChip(
                    label = "Dance Anthems",
                    onClick = { },
                    icon = "icon",
                    largeIcon = true,
                    placeholder = rememberVectorPainter(
                        image = Icons.Default.FeaturedPlayList,
                        tintColor = Color.Green
                    ),
                    chipType = StandardChipType.Secondary
                )
            },
            {
                StandardChip(
                    label = "Indie Jukebox",
                    onClick = { },
                    icon = "icon",
                    largeIcon = true,
                    placeholder = rememberVectorPainter(
                        image = Icons.Default.FeaturedPlayList,
                        tintColor = Color.Green
                    ),
                    chipType = StandardChipType.Secondary
                )
            }
        )
    ),
    headerContent = { FavouritesTitle() }
)

private val favouritesFailedSection = Section(
    state = Section.State.Empty {
        Text(
            text = "Failed to load favourites. Please try again later.",
            modifier = Modifier.padding(top = 8.dp, bottom = 8.dp),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.body2
        )
    },
    headerContent = { FavouritesTitle() }
)

private val favouritesEmptySection = Section(
    state = Section.State.Empty {
        Text(
            text = "Mark songs or albums as favourites to see them here",
            modifier = Modifier.padding(top = 8.dp, bottom = 8.dp),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.body2
        )
    },
    headerContent = { FavouritesTitle() }
)

@Composable
private fun FavouritesTitle() {
    Title(
        text = "Favourites",
        modifier = Modifier.padding(bottom = 12.dp)
    )
}
