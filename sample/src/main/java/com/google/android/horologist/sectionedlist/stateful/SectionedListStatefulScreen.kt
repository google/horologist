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

package com.google.android.horologist.sectionedlist.stateful

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.DownloadDone
import androidx.compose.material.icons.filled.LibraryMusic
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.wear.compose.material.ChipDefaults
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text
import androidx.wear.compose.ui.tooling.preview.WearPreviewDevices
import com.google.android.horologist.composables.PlaceholderChip
import com.google.android.horologist.composables.Section
import com.google.android.horologist.composables.SectionedList
import com.google.android.horologist.composables.SectionedListScope
import com.google.android.horologist.compose.layout.ScalingLazyColumnState
import com.google.android.horologist.compose.layout.belowTimeTextPreview
import com.google.android.horologist.compose.material.Chip
import com.google.android.horologist.compose.material.Title
import com.google.android.horologist.compose.material.util.DECORATIVE_ELEMENT_CONTENT_DESCRIPTION
import com.google.android.horologist.sample.R
import com.google.android.horologist.sectionedlist.stateful.SectionedListStatefulScreenViewModel.Recommendation
import com.google.android.horologist.sectionedlist.stateful.SectionedListStatefulScreenViewModel.RecommendationSectionState
import com.google.android.horologist.sectionedlist.stateful.SectionedListStatefulScreenViewModel.Trending
import com.google.android.horologist.sectionedlist.stateful.SectionedListStatefulScreenViewModel.TrendingSectionState

@Composable
fun SectionedListStatefulScreen(
    modifier: Modifier = Modifier,
    viewModel: SectionedListStatefulScreenViewModel = viewModel(),
    columnState: ScalingLazyColumnState
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    SectionedList(
        columnState = columnState,
        modifier = modifier
    ) {
        topMenuSection()

        recommendationsSection(state = state, viewModel = viewModel)

        trendingSection(state = state, viewModel = viewModel)

        bottomMenuSection()
    }
}

private fun SectionedListScope.topMenuSection() {
    section(
        listOf(
            Pair(R.string.sectionedlist_downloads_button, Icons.Default.DownloadDone),
            Pair(R.string.sectionedlist_your_library_button, Icons.Default.LibraryMusic)
        )
    ) {
        loaded { (stringResId, icon) ->
            Chip(
                label = stringResource(stringResId),
                onClick = { },
                icon = icon,
                colors = ChipDefaults.secondaryChipColors()
            )
        }
    }
}

private fun SectionedListScope.recommendationsSection(
    state: SectionedListStatefulScreenViewModel.UiState,
    viewModel: SectionedListStatefulScreenViewModel
) {
    val recommendationsState: Section.State<Recommendation> =
        when (val recommendationSectionState = state.recommendationSectionState) {
            RecommendationSectionState.Loading -> Section.State.Loading
            is RecommendationSectionState.Loaded -> Section.State.Loaded(
                recommendationSectionState.list
            )

            RecommendationSectionState.Failed -> Section.State.Failed
        }

    section(recommendationsState) {
        header {
            Title(
                stringResource(id = R.string.sectionedlist_recommendations_title),
                Modifier.padding(vertical = 8.dp)
            )
        }

        loaded { recommendation: Recommendation ->
            Chip(
                label = recommendation.playlistName,
                onClick = { },
                icon = recommendation.icon,
                colors = ChipDefaults.secondaryChipColors()
            )
        }

        loading(count = 2) {
            Column {
                PlaceholderChip(colors = ChipDefaults.secondaryChipColors())
            }
        }

        failed {
            FailedView(onClick = { viewModel.loadRecommendations() })
        }

        footer {
            Chip(
                label = stringResource(id = R.string.sectionedlist_see_more_button),
                onClick = { },
                colors = ChipDefaults.secondaryChipColors()
            )
        }
    }
}

private fun SectionedListScope.trendingSection(
    state: SectionedListStatefulScreenViewModel.UiState,
    viewModel: SectionedListStatefulScreenViewModel
) {
    val trendingState: Section.State<Trending> =
        when (val recommendationSectionState = state.trendingSectionState) {
            TrendingSectionState.Loading -> Section.State.Loading
            is TrendingSectionState.Loaded -> Section.State.Loaded(
                recommendationSectionState.list
            )

            TrendingSectionState.Failed -> Section.State.Failed
        }

    section(trendingState) {
        header {
            Title(
                text = stringResource(id = R.string.sectionedlist_trending_title),
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }

        loaded { trending: Trending ->
            Chip(
                label = trending.name,
                onClick = { },
                secondaryLabel = trending.artist,
                icon = Icons.Default.MusicNote,
                colors = ChipDefaults.secondaryChipColors()
            )
        }

        loading(count = 2) {
            Column {
                PlaceholderChip(colors = ChipDefaults.secondaryChipColors())
            }
        }

        failed {
            FailedView(onClick = { viewModel.loadTrending() })
        }

        footer {
            Chip(
                label = stringResource(
                    id = R.string.sectionedlist_see_more_button
                ),
                onClick = { },
                colors = ChipDefaults.secondaryChipColors()
            )
        }
    }
}

private fun SectionedListScope.bottomMenuSection() {
    section {
        loaded {
            Chip(
                label = stringResource(R.string.sectionedlist_settings_button),
                onClick = { },
                icon = Icons.Default.Settings,
                colors = ChipDefaults.secondaryChipColors()
            )
        }
    }
}

@Composable
private fun FailedView(onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .height(108.dp)
            .clickable { onClick() },
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Default.CloudOff,
            contentDescription = DECORATIVE_ELEMENT_CONTENT_DESCRIPTION,
            modifier = Modifier
                .size(ChipDefaults.LargeIconSize)
                .clip(CircleShape),
            tint = Color.Gray
        )

        Text(
            text = stringResource(R.string.sectionedlist_failed_to_load),
            modifier = Modifier.padding(top = 8.dp, bottom = 8.dp),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.body2
        )
    }
}

@WearPreviewDevices
@Composable
fun SectionedListStatefulScreenPreview() {
    SectionedListStatefulScreen(columnState = belowTimeTextPreview())
}
