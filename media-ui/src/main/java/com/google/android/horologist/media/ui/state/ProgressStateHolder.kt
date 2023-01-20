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
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.withInfiniteAnimationFrameMillis
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.wear.compose.material.ProgressIndicatorDefaults
import com.google.android.horologist.media.ui.ExperimentalHorologistMediaUiApi
import com.google.android.horologist.media.ui.state.model.TrackPositionUiModel
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.math.abs

/**
 * State holder for the media progress indicator that supports both ongoing predictive progress and
 * animating progress.
 */
@ExperimentalHorologistMediaUiApi
internal class ProgressStateHolder(initial: Float) {
    private val actual = mutableStateOf(initial)
    private val animatable = Animatable(0f)
    val state = derivedStateOf {
        if (animatable.isRunning) {
            actual.value + animatable.value - animatable.targetValue
        } else {
            actual.value
        }
    }

    suspend fun setProgress(percent: Float, canAnimate: Boolean) = coroutineScope {
        val offset = percent - actual.value
        actual.value = percent
        if (!canAnimate || animatable.isRunning || abs(offset) < ANIMATION_THRESHOLD) {
            return@coroutineScope
        }
        launch(NonCancellable) {
            animatable.animateTo(offset, ProgressIndicatorDefaults.ProgressAnimationSpec)
            animatable.snapTo(0f)
        }
    }

    suspend fun predictProgress(predictor: (Long) -> Float) = coroutineScope {
        val elapsedRealtime = getElapsedRealtime()
        val initialFrameTime = withInfiniteAnimationFrameMillis { it }
        do {
            withInfiniteAnimationFrameMillis {
                val frameTimeOffset = it - initialFrameTime
                actual.value = predictor(elapsedRealtime + frameTimeOffset)
            }
        } while (isActive)
    }

    companion object {
        // Never animate progress under this threshold
        private const val ANIMATION_THRESHOLD = 0.01f

        @Composable
        fun fromTrackPositionUiModel(trackPositionUiModel: TrackPositionUiModel): State<Float> {
            val stateHolder = remember { ProgressStateHolder(trackPositionUiModel.percent) }
            LaunchedEffect(trackPositionUiModel) {
                stateHolder.setProgress(trackPositionUiModel.percent, trackPositionUiModel.shouldAnimate)
                if (trackPositionUiModel is TrackPositionUiModel.Predictive) {
                    stateHolder.predictProgress(trackPositionUiModel.predictor::predictPercent)
                }
            }
            return stateHolder.state
        }

        private val TrackPositionUiModel.percent: Float
            get() = when (this) {
                is TrackPositionUiModel.Actual -> percent
                is TrackPositionUiModel.Predictive -> predictor.predictPercent(getElapsedRealtime())
                else -> 0f
            }

        private fun getElapsedRealtime() = SystemClock.elapsedRealtime()
    }
}
