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

package com.google.android.horologist.mediasample.ui.browse

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlaylistPlay
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.wear.compose.material.ScalingLazyListState
import com.google.android.horologist.media.ui.screens.browse.BrowseScreen
import com.google.android.horologist.media.ui.screens.browse.BrowseScreenPlaylistsSectionButton
import com.google.android.horologist.mediasample.R

@Composable
fun UampStreamingBrowseScreen(
    onPlaylistsClick: () -> Unit,
    onSettingsClick: () -> Unit,
    focusRequester: FocusRequester,
    scalingLazyListState: ScalingLazyListState,
    modifier: Modifier = Modifier
) {
    BrowseScreen(
        focusRequester = focusRequester,
        scalingLazyListState = scalingLazyListState,
        modifier = modifier
    ) {
        this.playlistsSection(
            buttons = listOf(
                BrowseScreenPlaylistsSectionButton(
                    textId = R.string.horologist_browse_library_playlists_button,
                    icon = Icons.Default.PlaylistPlay,
                    onClick = onPlaylistsClick
                ),
                BrowseScreenPlaylistsSectionButton(
                    textId = R.string.horologist_browse_library_settings_button,
                    icon = Icons.Default.Settings,
                    onClick = onSettingsClick
                )
            )
        )
    }
}
