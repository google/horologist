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

import androidx.compose.animation.core.Animatable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.withFrameMillis
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.horologist.annotations.ExperimentalHorologistApi
import com.google.android.horologist.media.model.TimestampProvider
import com.google.android.horologist.media.ui.animation.PlaybackProgressAnimation.PLAYBACK_PROGRESS_ANIMATION_SPEC
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
@ExperimentalHorologistApi
internal class ProgressStateHolder(
    initial: Float,
    private val timestampProvider: TimestampProvider,
) {
    private val actual = mutableFloatStateOf(initial)
    private val animatable = Animatable(0f)
    val state = derivedStateOf { actual.floatValue + animatable.value - animatable.targetValue }

    suspend fun setProgress(percent: Float, canAnimate: Boolean) = coroutineScope {
        val offset = percent - actual.floatValue
        actual.floatValue = percent
        if (!canAnimate || animatable.isRunning || abs(offset) < ANIMATION_THRESHOLD) {
            return@coroutineScope
        }
        launch(NonCancellable) {
            animatable.animateTo(offset, PLAYBACK_PROGRESS_ANIMATION_SPEC)
            animatable.snapTo(0f)
        }
    }

    suspend fun predictProgress(predictor: (Long) -> Float) = coroutineScope {
        val initialFrameTime = withFrameMillis { timestampProvider.getTimestamp() - it }
        do {
            withFrameMillis {
                actual.floatValue = predictor(initialFrameTime + it)
            }
        } while (isActive)
    }

    companion object {
        // Never animate progress under this threshold
        private const val ANIMATION_THRESHOLD = 0.01f

        @Composable
        fun fromTrackPositionUiModel(trackPositionUiModel: TrackPositionUiModel): State<Float> {
            val lifecycleOwner = LocalLifecycleOwner.current
            val timestampProvider = LocalTimestampProvider.current
            val stateHolder = remember {
                val initial = trackPositionUiModel.getCurrentPercent(timestampProvider.getTimestamp())
                ProgressStateHolder(initial, timestampProvider)
            }
            LaunchedEffect(trackPositionUiModel, lifecycleOwner) {
                val percent = trackPositionUiModel.getCurrentPercent(timestampProvider.getTimestamp())
                stateHolder.setProgress(percent, trackPositionUiModel.shouldAnimate)
                if (trackPositionUiModel is TrackPositionUiModel.Predictive) {
                    // Prediction only happens when the UI is visible.
                    lifecycleOwner.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                        stateHolder.predictProgress(trackPositionUiModel.predictor::predictPercent)
                    }
                }
            }
            return stateHolder.state
        }

        private fun TrackPositionUiModel.getCurrentPercent(timestamp: Long) = when (this) {
            is TrackPositionUiModel.Actual -> percent
            is TrackPositionUiModel.SeekProjection -> percent
            is TrackPositionUiModel.Predictive -> predictor.predictPercent(timestamp)
            else -> 0f
        }
    }
}
