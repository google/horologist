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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.google.android.horologist.media.ui.ExperimentalHorologistMediaUiApi
import com.google.android.horologist.media.ui.state.model.TrackPositionUiModel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.isActive

@ExperimentalHorologistMediaUiApi
internal class MediaProgressState(initial: Float? = null) : State<Float> {
    private val predictedProgress = mutableStateOf(0f)
    private var actualProgress by mutableStateOf<Float?>(initial)

    override val value: Float get() = actualProgress ?: predictedProgress.value

    suspend fun predictProgress(predictor: (Long) -> Float): Unit = coroutineScope {
        val elapsedRealtime = SystemClock.elapsedRealtime()
        predictedProgress.value = predictor(elapsedRealtime)
        actualProgress = null
        val initialFrameTime = withInfiniteAnimationFrameMillis { it }
        while (isActive) {
            val frameTimeOffset = withInfiniteAnimationFrameMillis { it - initialFrameTime }
            predictedProgress.value = predictor(elapsedRealtime + frameTimeOffset)
        }
    }

    fun setProgress(progress: Float) {
        actualProgress = progress
    }

    companion object {
        @Composable
        fun fromMediaProgress(trackPositionUiModel: TrackPositionUiModel): State<Float> {
            val state = remember { MediaProgressState(trackPositionUiModel.initial) }
            LaunchedEffect(trackPositionUiModel) {
                if (trackPositionUiModel is TrackPositionUiModel.Actual) {
                    state.setProgress(trackPositionUiModel.percent)
                } else if (trackPositionUiModel is TrackPositionUiModel.Predictive) {
                    state.predictProgress(trackPositionUiModel.predictor::predictPercent)
                }
            }
            return state
        }

        private val TrackPositionUiModel.initial get() = (this as? TrackPositionUiModel.Actual)?.percent
    }
}
