/*
 * Copyright 2023 The Android Open Source Project
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

package com.google.android.horologist.audio.ui.state

import com.google.android.horologist.audio.VolumeRepository
import com.google.android.horologist.audio.ui.state.mapper.VolumeUiStateMapper
import com.google.android.horologist.audio.ui.state.model.VolumeUiState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlin.math.absoluteValue
import kotlin.math.roundToInt

public class VolumeStateHolder(
    private val repository: VolumeRepository,
    private val volumeStepPercent: Float = DEFAULT_VOLUME_STEP_PERCENT
) {
    public val volumeUiStateFlow: Flow<VolumeUiState> = repository.volumeState.map(VolumeUiStateMapper::map)

    public fun increaseVolume() {
        if (repository.volumeState.value.relativelyAdjustable) {
            repository.increaseVolume()
        } else if (repository.volumeState.value.absolutelyAdjustable) {
            changeVolume(volumeStepPercent)
        }
    }

    public fun decreaseVolume() {
        if (repository.volumeState.value.relativelyAdjustable) {
            repository.decreaseVolume()
        } else if (repository.volumeState.value.absolutelyAdjustable) {
            changeVolume(-volumeStepPercent)
        }
    }

    public fun changeVolume(percentDelta: Float) {
        val min = repository.volumeState.value.min
        val max = repository.volumeState.value.max
        val current = repository.volumeState.value.current
        if (percentDelta == 0f || min == null || max == null || current == null) {
            return
        }
        if (repository.volumeState.value.absolutelyAdjustable) {
            val delta = (percentDelta * (max - min)).roundToInt().let {
                if (percentDelta > 0f) {
                    it.coerceAtLeast(1)
                } else {
                    it.coerceAtMost(-1)
                }
            }
            repository.setVolume((current + delta).coerceIn(min, max))
        } else if (repository.volumeState.value.relativelyAdjustable) {
            val steps = (percentDelta / volumeStepPercent).absoluteValue.roundToInt().coerceAtLeast(1)
            repeat(steps) {
                if (percentDelta > 0f) {
                    repository.increaseVolume()
                } else {
                    repository.decreaseVolume()
                }
            }
        }
    }

    private companion object {
        private const val DEFAULT_VOLUME_STEP_PERCENT: Float = 0.05f
    }
}
