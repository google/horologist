/*
 * Copyright 2025 The Android Open Source Project
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

package com.google.android.horologist.media.ui.material3.components.ambient

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.wear.compose.material3.ColorScheme
import androidx.wear.compose.material3.MaterialTheme
import com.google.android.horologist.media.ui.material3.components.display.TrackMediaDisplay
import com.google.android.horologist.media.ui.model.R
import com.google.android.horologist.media.ui.state.model.MediaUiModel

/**
 * Ambient [MediaDisplay] implementation for [PlayerScreen] including player status.
 *
 * @param media The current media being played.
 * @param loading Whether the player is currently loading.
 * @param modifier Modifier for the display.
 * @param colorScheme The color scheme to use for the display.
 */
@Composable
public fun AmbientMediaInfoDisplay(
    media: MediaUiModel?,
    loading: Boolean,
    modifier: Modifier = Modifier,
    colorScheme: ColorScheme = MaterialTheme.colorScheme,
) {
    if (media is MediaUiModel.Ready) {
        TrackMediaDisplay(
            media = media,
            modifier = modifier,
            colorScheme = colorScheme,
            titleOverflow = TextOverflow.Clip,
            subtitleOverflow = TextOverflow.Clip,
            titleSoftWrap = false,
            subtitleSoftWrap = false,
        )
    } else {
        AmbientMessageDisplay(
            message =
                if (loading) {
                    stringResource(R.string.horologist_loading_title)
                } else {
                    stringResource(R.string.horologist_nothing_playing)
                },
            modifier = modifier,
            colorScheme = colorScheme,
        )
    }
}
