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

package com.google.android.horologist.composables

import androidx.compose.runtime.Composable
import androidx.compose.ui.focus.FocusRequester
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.rememberScalingLazyListState
import com.google.android.horologist.compose.tools.WearPreviewDevices
import com.google.android.horologist.compose.tools.WearPreviewFontSizes
import com.google.android.horologist.compose.tools.a11y.PreviewForCharLimit
import com.google.android.horologist.media.ui.components.base.StandardChip
import com.google.android.horologist.media.ui.components.base.StandardChipType
import com.google.android.horologist.media.ui.components.base.Title

@PreviewForCharLimit(
    stringResIds = [
        "horologist_browse_downloads_title",
        "horologist_browse_library_title",
        "horologist_browse_library_playlists",
        "horologist_browse_playlist_title"
    ]
)
@WearPreviewDevices
@WearPreviewFontSizes
@Composable
fun SectionedListSectionHeaderPreviewForCharLimit() {
    SectionedList(
        focusRequester = FocusRequester(),
        scalingLazyListState = rememberScalingLazyListState()
    ) {
        section {
            header { Title(text = "W".repeat(36)) }
        }
    }
}

@PreviewForCharLimit(
    stringResIds = [
        "horologist_browse_downloads_empty"
    ]
)
@WearPreviewDevices
@WearPreviewFontSizes
@Composable
fun SectionedListEmptyTextPreviewForCharLimit() {
    SectionedList(
        focusRequester = FocusRequester(),
        scalingLazyListState = rememberScalingLazyListState()
    ) {
        section<Unit>(Section.State.Empty()) {
            empty { Text(text = "W".repeat(200)) }
        }
    }
}

@PreviewForCharLimit(
    stringResIds = [
        "horologist_browse_library_playlists_button",
        "horologist_browse_library_settings_button",
        "horologist_playlist_download_button_download",
        "horologist_playlist_download_button_cancel"
    ]
)
@WearPreviewDevices
@WearPreviewFontSizes
@Composable
fun SectionedListStandardChipPreviewForCharLimit() {
    SectionedList(
        focusRequester = FocusRequester(),
        scalingLazyListState = rememberScalingLazyListState()
    ) {
        section {
            loaded {
                StandardChip(
                    label = "W".repeat(20),
                    onClick = { },
                    chipType = StandardChipType.Secondary
                )
            }
        }
    }
}

@PreviewForCharLimit(
    stringResIds = [
        "horologist_playlist_download_download_progress_known_size",
        "horologist_playlist_download_download_progress_waiting"
    ]
)
@WearPreviewDevices
@WearPreviewFontSizes
@Composable
fun SectionedListStandardChipSecondaryLabelPreviewForCharLimit() {
    SectionedList(
        focusRequester = FocusRequester(),
        scalingLazyListState = rememberScalingLazyListState()
    ) {
        section {
            loaded {
                StandardChip(
                    label = "W".repeat(10),
                    onClick = { },
                    secondaryLabel = "W".repeat(13),
                    chipType = StandardChipType.Secondary
                )
            }
        }
    }
}
