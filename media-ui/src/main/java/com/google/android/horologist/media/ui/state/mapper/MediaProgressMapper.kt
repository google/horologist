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
import com.google.android.horologist.media.model.PlaybackState
import com.google.android.horologist.media.ui.ExperimentalHorologistMediaUiApi
import com.google.android.horologist.media.ui.state.model.MediaProgress

/**
 * Map a [PlaybackState] into a [MediaProgress]
 */
@ExperimentalHorologistMediaUiApi
public object MediaProgressMapper {
    public fun map(playbackState: PlaybackState): MediaProgress {
        val currentPosition = playbackState.currentPosition
        val duration = playbackState.duration
        val elapsedRealtimeWhenCreated = playbackState.elapsedRealtimeWhenCreated
        return when {
            currentPosition == null || duration == null ->
                MediaProgress.Hidden
            playbackState.isPlaying && elapsedRealtimeWhenCreated != null ->
                MediaProgress.Predictive(
                    currentPositionMs = currentPosition.inWholeMilliseconds,
                    durationMs = duration.inWholeMilliseconds,
                    playbackSpeed = playbackState.playbackSpeed,
                    isLive = playbackState.isLive,
                    elapsedRealtimeMs = elapsedRealtimeWhenCreated.inWholeMilliseconds
                )
            else ->
                MediaProgress.Actual(
                    currentPositionMs = currentPosition.inWholeMilliseconds,
                    durationMs = duration.inWholeMilliseconds
                )
        }
    }
}
