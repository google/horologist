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

package com.google.android.horologist.media.ui.state.mapper

import com.google.android.horologist.media.model.MediaPosition
import com.google.android.horologist.media.ui.ExperimentalHorologistMediaUiApi
import com.google.android.horologist.media.ui.state.model.TrackPositionUiModel

/**
 * Map a [MediaPosition] into a [TrackPositionUiModel]
 */
@ExperimentalHorologistMediaUiApi
public object TrackPositionUiModelMapper {

    public fun map(mediaPosition: MediaPosition): TrackPositionUiModel {
        val (duration, percent) = if (mediaPosition is MediaPosition.KnownDuration) {
            mediaPosition.duration.inWholeMilliseconds to mediaPosition.percent
        } else {
            0L to 0F
        }

        return TrackPositionUiModel(
            current = mediaPosition.current.inWholeMilliseconds,
            duration = duration,
            percent = percent
        )
    }
}
