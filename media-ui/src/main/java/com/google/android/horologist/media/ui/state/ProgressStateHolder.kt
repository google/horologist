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

package com.google.android.horologist.media.ui.state

import android.os.SystemClock
import androidx.compose.animation.core.withInfiniteAnimationFrameMillis
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import com.google.android.horologist.media.ui.ExperimentalHorologistMediaUiApi
import com.google.android.horologist.media.ui.state.model.TrackPositionUiModel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.isActive

@ExperimentalHorologistMediaUiApi
internal class ProgressStateHolder(initial: Float) {
    val state = mutableStateOf(initial)

    suspend fun predictProgress(predictor: (Long) -> Float): Unit = coroutineScope {
        val elapsedRealtime = SystemClock.elapsedRealtime()
        val initialFrameTime = withInfiniteAnimationFrameMillis { it }
        do {
            withInfiniteAnimationFrameMillis {
                val frameTimeOffset = it - initialFrameTime
                state.value = predictor(elapsedRealtime + frameTimeOffset)
            }
        } while (isActive)
    }

    companion object {
        @Composable
        fun fromTrackPositionUiModel(trackPositionUiModel: TrackPositionUiModel): State<Float> {
            val stateHolder = remember { ProgressStateHolder(trackPositionUiModel.initial) }
            LaunchedEffect(trackPositionUiModel) {
                if (trackPositionUiModel is TrackPositionUiModel.Actual) {
                    stateHolder.state.value = trackPositionUiModel.percent
                } else if (trackPositionUiModel is TrackPositionUiModel.Predictive) {
                    stateHolder.predictProgress(trackPositionUiModel.predictor::predictPercent)
                }
            }
            return stateHolder.state
        }

        private val TrackPositionUiModel.initial get() = (this as? TrackPositionUiModel.Actual)?.percent ?: 0f
    }
}
