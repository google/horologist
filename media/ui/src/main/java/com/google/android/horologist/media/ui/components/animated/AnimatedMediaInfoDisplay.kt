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
import com.google.android.horologist.annotations.ExperimentalHorologistApi
import com.google.android.horologist.media.ui.components.display.LoadingMediaDisplay
import com.google.android.horologist.media.ui.components.display.NothingPlayingDisplay
import com.google.android.horologist.media.ui.state.model.MediaUiModel

/**
 * Animated [MediaDisplay] implementation for [PlayerScreen] including player status.
 */
@ExperimentalHorologistApi
@Composable
public fun AnimatedMediaInfoDisplay(
    media: MediaUiModel?,
    loading: Boolean,
    modifier: Modifier = Modifier,
) {
    if (loading) {
        LoadingMediaDisplay(modifier)
    } else if (media is MediaUiModel.Ready) {
        MarqueeTextMediaDisplay(
            modifier = modifier,
            title = media.title,
            artist = media.subtitle,
            titleIcon = media.titleIcon,
        )
    } else {
        NothingPlayingDisplay(modifier)
    }
}
