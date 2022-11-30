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

@file:OptIn(ExperimentalHorologistMediaApi::class)

package com.google.android.horologist.media.ui.state.mapper

import com.google.android.horologist.media.ExperimentalHorologistMediaApi
import com.google.android.horologist.media.model.MediaPosition
import com.google.android.horologist.media.ui.ExperimentalHorologistMediaUiApi
import com.google.android.horologist.media.ui.state.model.TrackPositionUiModel

/**
 * Map a [MediaPosition] into a [TrackPositionUiModel]
 */
@ExperimentalHorologistMediaUiApi
public object TrackPositionUiModelMapper {

    internal data class TrackPositionValues(val current: Long, val duration: Long, val percent: Float, val showProgressBar: Boolean)

    public fun map(mediaPosition: MediaPosition): TrackPositionUiModel {
        val (current, duration, percent, showProgress) = if (mediaPosition is MediaPosition.KnownDuration) {
            TrackPositionValues(mediaPosition.current.inWholeMilliseconds, mediaPosition.duration.inWholeMilliseconds, mediaPosition.percent, true)
        } else {
            TrackPositionValues(0L, 0L, 0F, false)
        }

        return TrackPositionUiModel(
            current = current,
            duration = duration,
            percent = percent,
            showProgress = showProgress
        )
    }
}
