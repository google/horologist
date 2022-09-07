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

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DirectionsRun
import androidx.compose.material.icons.filled.DownloadDone
import androidx.compose.material.icons.filled.LibraryMusic
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.SelfImprovement
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.res.stringResource
import androidx.wear.compose.material.ScalingLazyListState
import androidx.wear.compose.material.rememberScalingLazyListState
import com.google.android.horologist.composables.SectionedList
import com.google.android.horologist.compose.tools.WearPreviewDevices
import com.google.android.horologist.sample.R
import com.google.android.horologist.sectionedlist.component.SingleLineChip
import com.google.android.horologist.sectionedlist.component.SingleLineNoIconChip
import com.google.android.horologist.sectionedlist.component.Title
import com.google.android.horologist.sectionedlist.component.TwoLinesChip

@Composable
fun SectionedListStatelessScreen(
    modifier: Modifier = Modifier,
    scalingLazyListState: ScalingLazyListState = rememberScalingLazyListState(),
    focusRequester: FocusRequester = remember { FocusRequester() }
) {
    SectionedList(
        focusRequester = focusRequester,
        scalingLazyListState = scalingLazyListState,
        modifier = modifier
    ) {
        // Section without header and without footer
        section(
            listOf(
                Pair(R.string.sectionedlist_downloads_button, Icons.Default.DownloadDone),
                Pair(R.string.sectionedlist_your_library_button, Icons.Default.LibraryMusic)
            )
        ) {
            loaded { item ->
                SingleLineChip(text = stringResource(item.first), imageVector = item.second)
            }
        }

        // Section with header and footer
        section(
            listOf(
                Pair("Running playlist", Icons.Default.DirectionsRun),
                Pair("Focus", Icons.Default.SelfImprovement),
                Pair("Summer hits", Icons.Default.LightMode)
            )
        ) {
            header {
                Title(stringResource(id = R.string.sectionedlist_recommendations_title))
            }

            loaded { item ->
                SingleLineChip(item.first, item.second)
            }

            footer {
                SingleLineNoIconChip(stringResource(id = R.string.sectionedlist_see_more_button))
            }
        }

        // Section with header and footer
        section(
            listOf(
                Pair("Bad Habits", "Ed Sheeran"),
                Pair("There'd Better Be A Mirrorball", "Arctic Monkeys"),
                Pair("180 Hours", "Dudu Kanegae")
            )
        ) {
            header {
                Title(stringResource(id = R.string.sectionedlist_trending_title))
            }

            loaded { item ->
                TwoLinesChip(
                    primaryLabel = item.first,
                    secondaryLabel = item.second,
                    imageVector = Icons.Default.MusicNote
                )
            }

            footer {
                SingleLineNoIconChip(stringResource(id = R.string.sectionedlist_see_more_button))
            }
        }

        // Section with single item
        section {
            loaded {
                SingleLineChip(
                    text = stringResource(R.string.sectionedlist_settings_button),
                    imageVector = Icons.Default.Settings
                )
            }
        }
    }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }
}

@WearPreviewDevices
@Composable
fun SectionedListStatelessScreenPreview() {
    SectionedListStatelessScreen()
}
