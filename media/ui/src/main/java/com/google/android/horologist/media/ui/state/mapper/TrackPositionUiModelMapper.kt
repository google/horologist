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

import com.google.android.horologist.annotations.ExperimentalHorologistApi
import com.google.android.horologist.media.model.PlaybackStateEvent
import com.google.android.horologist.media.model.PlayerState
import com.google.android.horologist.media.ui.state.model.TrackPositionUiModel
import kotlin.time.Duration.Companion.milliseconds

/**
 * Functions to map a [TrackPositionUiModel] based on data from other layers.
 */
@ExperimentalHorologistApi
public object TrackPositionUiModelMapper {
    public fun map(event: PlaybackStateEvent, seekProjection: Long? = null): TrackPositionUiModel {
        val currentPosition = event.playbackState.currentPosition
        val duration = event.playbackState.duration
        val durationMs = duration?.inWholeMilliseconds
        val currentPositionMs = currentPosition?.inWholeMilliseconds
        if (event.playbackState.playerState == PlayerState.Loading) {
            return TrackPositionUiModel.Loading(showProgress = true)
        }
        if (currentPositionMs == null || durationMs == null || durationMs <= 0) {
            return TrackPositionUiModel.Hidden
        }
        seekProjection?.let { projection ->
            if (projection >= 0) {
                val percent = projection.toFloat() / durationMs.toFloat()
                return TrackPositionUiModel.SeekProjection(
                    percent = percent,
                    duration = duration,
                    position = projection.milliseconds,
                )
            }
        }
        val predictor = event.createPositionPredictor()
        if (event.playbackState.isPlaying && predictor != null) {
            return TrackPositionUiModel.Predictive(predictor)
        }
        val percent = currentPositionMs.toFloat() / durationMs.toFloat()
        return TrackPositionUiModel.Actual(percent, duration, currentPosition)
    }
}
