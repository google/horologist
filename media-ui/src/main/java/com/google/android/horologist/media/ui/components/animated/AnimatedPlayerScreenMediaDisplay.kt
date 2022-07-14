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

package com.google.android.horologist.media.ui.components.animated

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.google.android.horologist.media.ui.ExperimentalHorologistMediaUiApi
import com.google.android.horologist.media.ui.R
import com.google.android.horologist.media.ui.components.InfoMediaDisplay
import com.google.android.horologist.media.ui.components.LoadingMediaDisplay
import com.google.android.horologist.media.ui.components.TextMediaDisplay
import com.google.android.horologist.media.ui.state.PlayerUiState

/**
 * Default [MediaDisplay] implementation for [PlayerScreen] including player status.
 */
@ExperimentalHorologistMediaUiApi
@Composable
public fun AnimatedPlayerScreenMediaDisplay(
    playerUiState: PlayerUiState,
    modifier: Modifier = Modifier,
) {
    val mediaItem = playerUiState.mediaItem
    if (!playerUiState.connected) {
        LoadingMediaDisplay(modifier)
    } else if (mediaItem != null) {
        TextMediaDisplay(
            modifier = modifier,
            title = mediaItem.title,
            artist = mediaItem.artist
        )
    } else {
        InfoMediaDisplay(
            message = stringResource(R.string.horologist_nothing_playing),
            modifier = modifier
        )
    }
}
