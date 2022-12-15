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
import androidx.compose.animation.core.AnimationResult
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.SnapSpec
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.withFrameMillis
import androidx.wear.compose.material.ProgressIndicatorDefaults
import com.google.android.horologist.media.ui.ExperimentalHorologistMediaUiApi
import com.google.android.horologist.media.ui.state.model.MediaProgress
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.isActive

@ExperimentalHorologistMediaUiApi
internal class MediaProgressState : State<Float> {
    private val animatable = Animatable(0f)
    private val state = mutableStateOf(0f)

    override val value: Float
        get() = if (animatable.isRunning) {
            animatable.value
        } else {
            state.value
        }

    suspend fun animateValue(targetValue: Float, animationSpec: AnimationSpec<Float>): AnimationResult<Float, AnimationVector1D> {
        animatable.snapTo(state.value)
        val result = animatable.animateTo(targetValue, animationSpec)
        state.value = targetValue
        return result
    }

    suspend fun animatePredicted(predictor: (Long) -> Float): Unit = coroutineScope {
        val elapsedRealtime = SystemClock.elapsedRealtime()
        val initialFrameTime = withFrameMillis { it }
        while (isActive) {
            val frameTimeOffset = withFrameMillis { it - initialFrameTime }
            state.value = predictor(elapsedRealtime + frameTimeOffset)
        }
    }

    companion object {
        @Composable
        fun fromMediaProgress(mediaProgress: MediaProgress): State<Float> {
            val state = remember { MediaProgressState() }
            LaunchedEffect(mediaProgress) {
                if (mediaProgress is MediaProgress.Actual) {
                    val spec = if (mediaProgress.animated) {
                        ProgressIndicatorDefaults.ProgressAnimationSpec
                    } else {
                        SnapSpec()
                    }
                    state.animateValue(mediaProgress.percent, spec)
                } else if (mediaProgress is MediaProgress.Predictive) {
                    state.animatePredicted(mediaProgress::predictPercent)
                }
            }
            return state
        }
    }
}
