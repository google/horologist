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

package com.google.android.horologist.media.ui.screens.browse

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Login
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlaylistPlay
import androidx.compose.material.icons.filled.Podcasts
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.wear.compose.material.ChipDefaults
import androidx.wear.compose.ui.tooling.preview.WearPreviewDevices
import com.google.android.horologist.composables.PlaceholderChip
import com.google.android.horologist.composables.Section
import com.google.android.horologist.compose.layout.belowTimeTextPreview
import com.google.android.horologist.compose.material.Chip
import com.google.android.horologist.media.ui.R

@WearPreviewDevices
@Composable
fun BrowseScreenPreview() {
    BrowseScreenPreviewSample(
        trendingSectionState = Section.State.Loaded(
            list = listOf("Mozart", "Beethoven")
        ),
        downloadsSectionState = Section.State.Loaded(
            list = listOf(
                "Puccini" to "O mio babbino caro",
                "J.S. Bach" to "Toccata and Fugue in D minor"
            )
        )
    )
}

@WearPreviewDevices
@Composable
fun BrowseScreenPreviewLoading() {
    BrowseScreenPreviewSample(
        trendingSectionState = Section.State.Loading,
        downloadsSectionState = Section.State.Loading
    )
}

@WearPreviewDevices
@Composable
fun BrowseScreenPreviewFailed() {
    BrowseScreenPreviewSample(
        trendingSectionState = Section.State.Failed,
        downloadsSectionState = Section.State.Failed
    )
}

@Composable
private fun BrowseScreenPreviewSample(
    trendingSectionState: Section.State<String>,
    downloadsSectionState: Section.State<Pair<String, String>>
) {
    BrowseScreen(columnState = belowTimeTextPreview()) {
        button(
            BrowseScreenPlaylistsSectionButton(
                textId = R.string.horologist_browse_screen_preview_sign_in,
                icon = Icons.Default.Login,
                onClick = { }
            )
        )

        section(
            state = trendingSectionState,
            titleId = R.string.horologist_browse_screen_preview_trending_title,
            emptyMessageId = R.string.horologist_browse_screen_preview_trending_empty,
            failedMessageId = R.string.horologist_browse_screen_preview_trending_failed
        ) {
            loaded { item: String ->
                Chip(
                    label = item,
                    onClick = { },
                    icon = Icons.Default.Person,
                    colors = ChipDefaults.secondaryChipColors()
                )
            }

            loading {
                PlaceholderChip(colors = ChipDefaults.secondaryChipColors())
            }
        }

        downloadsSection<Pair<String, String>>(
            state = downloadsSectionState
        ) {
            loaded { item ->
                Chip(
                    label = item.first,
                    onClick = { },
                    secondaryLabel = item.second,
                    icon = Icons.Default.MusicNote,
                    colors = ChipDefaults.secondaryChipColors()
                )
            }

            loading {
                PlaceholderChip(colors = ChipDefaults.secondaryChipColors())
            }

            footer {
                Chip(
                    label = stringResource(id = R.string.horologist_browse_screen_preview_see_more_button),
                    onClick = { },
                    colors = ChipDefaults.secondaryChipColors()
                )
            }
        }

        playlistsSection(
            buttons = listOf(
                BrowseScreenPlaylistsSectionButton(
                    textId = R.string.horologist_browse_screen_preview_playlists_button,
                    icon = Icons.Default.PlaylistPlay,
                    onClick = { }
                ),

                BrowseScreenPlaylistsSectionButton(
                    textId = R.string.horologist_browse_screen_preview_podcasts_button,
                    icon = Icons.Default.Podcasts,
                    onClick = { }
                )
            )
        )
    }
}
