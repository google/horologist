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

package com.google.android.horologist.media.ui.components.list.sectioned

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FeaturedPlayList
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.ChipDefaults
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.rememberScalingLazyListState
import com.google.android.horologist.composables.PlaceholderChip
import com.google.android.horologist.compose.tools.WearPreviewDevices
import com.google.android.horologist.media.ui.ExperimentalHorologistMediaUiApi
import com.google.android.horologist.media.ui.components.base.StandardChip
import com.google.android.horologist.media.ui.components.base.StandardChipType
import com.google.android.horologist.media.ui.components.base.Title
import com.google.android.horologist.media.ui.utils.rememberVectorPainter

@WearPreviewDevices
@Composable
fun SectionedListPreviewLoadingSection() {
    SectionedList(
        focusRequester = FocusRequester(),
        scalingLazyListState = rememberScalingLazyListState()
    ) {
        downloadsSection(scope = this, state = Section.State.Loading)

        favouritesSection(scope = this, state = Section.State.Empty)
    }
}

@WearPreviewDevices
@Composable
fun SectionedListPreviewLoadedSection() {
    SectionedList(
        focusRequester = FocusRequester(),
        scalingLazyListState = rememberScalingLazyListState()
    ) {
        downloadsSection(scope = this, state = Section.State.Loaded(downloads))

        favouritesSection(scope = this, state = Section.State.Failed)
    }
}

@WearPreviewDevices
@Composable
fun SectionedListPreviewFailedSection() {
    SectionedList(
        focusRequester = FocusRequester(),
        scalingLazyListState = rememberScalingLazyListState()
    ) {
        downloadsSection(scope = this, state = Section.State.Failed)

        favouritesSection(scope = this, state = Section.State.Loaded(favourites))
    }
}

@WearPreviewDevices
@Composable
fun SectionedListPreviewEmptySection() {
    SectionedList(
        focusRequester = FocusRequester(),
        scalingLazyListState = rememberScalingLazyListState()
    ) {
        downloadsSection(scope = this, state = Section.State.Empty)

        favouritesSection(scope = this, state = Section.State.Loading)
    }
}

private val downloads = listOf("Nu Metal Essentials", "00s Rock")

private fun downloadsSection(scope: SectionedListScope, state: Section.State) {
    scope.section(state = state) {
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
    Title(
        text = "Downloads",
        modifier = Modifier.padding(bottom = 12.dp)
    )
}

@Composable
private fun DownloadsLoading() {
    PlaceholderChip(colors = ChipDefaults.secondaryChipColors())
}

@Composable
private fun DownloadsLoaded(label: String) {
    StandardChip(
        label = label,
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
    StandardChip(
        label = "More downloads..",
        onClick = { },
        chipType = StandardChipType.Secondary
    )
}

private val favourites = listOf("Dance Anthems", "Indie Jukebox")

private fun favouritesSection(scope: SectionedListScope, state: Section.State) {
    scope.section(state = state) {
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
    Title(
        text = "Favourites",
        modifier = Modifier.padding(top = 12.dp, bottom = 12.dp)
    )
}

@Composable
private fun FavouritesLoading() {
    PlaceholderChip(colors = ChipDefaults.secondaryChipColors())
}

@Composable
private fun FavouritesLoaded(label: String) {
    StandardChip(
        label = label,
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
    StandardChip(
        label = "More favourites..",
        onClick = { },
        chipType = StandardChipType.Secondary
    )
}
