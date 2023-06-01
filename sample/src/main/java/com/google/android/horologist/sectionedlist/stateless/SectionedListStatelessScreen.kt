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

package com.google.android.horologist.sectionedlist.stateless

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DirectionsRun
import androidx.compose.material.icons.filled.DownloadDone
import androidx.compose.material.icons.filled.LibraryMusic
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.SelfImprovement
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.wear.compose.ui.tooling.preview.WearPreviewDevices
import com.google.android.horologist.base.ui.components.StandardChip
import com.google.android.horologist.base.ui.components.StandardChipType
import com.google.android.horologist.composables.SectionedList
import com.google.android.horologist.composables.SectionedListScope
import com.google.android.horologist.compose.layout.ScalingLazyColumnState
import com.google.android.horologist.compose.layout.belowTimeTextPreview
import com.google.android.horologist.compose.material.Title
import com.google.android.horologist.sample.R

@Composable
fun SectionedListStatelessScreen(
    modifier: Modifier = Modifier,
    columnState: ScalingLazyColumnState
) {
    SectionedList(
        columnState = columnState,
        modifier = modifier
    ) {
        topMenuSection()

        recommendationsSection()

        trendingSection()

        bottomMenuSection()
    }
}

private fun SectionedListScope.topMenuSection() {
    // Section without header and without footer
    section(
        listOf(
            Pair(R.string.sectionedlist_downloads_button, Icons.Default.DownloadDone),
            Pair(R.string.sectionedlist_your_library_button, Icons.Default.LibraryMusic)
        )
    ) {
        loaded { (label, icon) ->
            StandardChip(
                label = stringResource(label),
                onClick = { },
                icon = icon,
                chipType = StandardChipType.Secondary
            )
        }
    }
}

private fun SectionedListScope.recommendationsSection() {
    // Section with header and footer
    section(
        listOf(
            Pair("Running playlist", Icons.Default.DirectionsRun),
            Pair("Focus", Icons.Default.SelfImprovement),
            Pair("Summer hits", Icons.Default.LightMode)
        )
    ) {
        header {
            Title(
                stringResource(id = R.string.sectionedlist_recommendations_title),
                Modifier.padding(vertical = 8.dp)
            )
        }

        loaded { (label, icon) ->
            StandardChip(
                label = label,
                onClick = { },
                icon = icon,
                chipType = StandardChipType.Secondary
            )
        }

        footer {
            StandardChip(
                label = stringResource(id = R.string.sectionedlist_see_more_button),
                onClick = { },
                chipType = StandardChipType.Secondary
            )
        }
    }
}

private fun SectionedListScope.trendingSection() {
    // Section with header and footer
    section(
        listOf(
            Pair("Bad Habits", "Ed Sheeran"),
            Pair("There'd Better Be A Mirrorball", "Arctic Monkeys"),
            Pair("180 Hours", "Dudu Kanegae")
        )
    ) {
        header {
            Title(
                text = stringResource(id = R.string.sectionedlist_trending_title),
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }

        loaded { (title, artist) ->
            StandardChip(
                label = title,
                onClick = { },
                secondaryLabel = artist,
                icon = Icons.Default.MusicNote,
                chipType = StandardChipType.Secondary
            )
        }

        footer {
            StandardChip(
                label = stringResource(id = R.string.sectionedlist_see_more_button),
                onClick = { },
                chipType = StandardChipType.Secondary
            )
        }
    }
}

private fun SectionedListScope.bottomMenuSection() {
    // Section with single item
    section {
        loaded {
            StandardChip(
                label = stringResource(R.string.sectionedlist_settings_button),
                onClick = { },
                icon = Icons.Default.Settings,
                chipType = StandardChipType.Secondary
            )
        }
    }
}

@WearPreviewDevices
@Composable
fun SectionedListStatelessScreenPreview() {
    SectionedListStatelessScreen(columnState = belowTimeTextPreview())
}
